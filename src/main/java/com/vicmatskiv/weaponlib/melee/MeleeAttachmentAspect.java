package com.vicmatskiv.weaponlib.melee;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.AttachmentContainer;
import com.vicmatskiv.weaponlib.CompatibleAttachment;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.Permit.Status;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class MeleeAttachmentAspect implements Aspect<MeleeState, PlayerMeleeInstance> {
	
	private static final Logger logger = LogManager.getLogger(MeleeAttachmentAspect.class);

	
	static {
		TypeRegistry.getInstance().register(EnterAttachmentModePermit.class);
		TypeRegistry.getInstance().register(ExitAttachmentModePermit.class);
		TypeRegistry.getInstance().register(ChangeAttachmentPermit.class);
	}
	
	private static class AttachmentLookupResult {
		CompatibleAttachment<ItemMelee> compatibleAttachment;
		int index = -1;
	}
	
	public static class EnterAttachmentModePermit extends Permit<MeleeState> {
		
		public EnterAttachmentModePermit() {}
		
		public EnterAttachmentModePermit(MeleeState state) {
			super(state);
		}
	}
	
	public static class ExitAttachmentModePermit extends Permit<MeleeState> {
		
		public ExitAttachmentModePermit() {}
		
		public ExitAttachmentModePermit(MeleeState state) {
			super(state);
		}
	}
	
	public static class ChangeAttachmentPermit extends Permit<MeleeState> {
		
		AttachmentCategory attachmentCategory;

		public ChangeAttachmentPermit() {}
		
		public ChangeAttachmentPermit(AttachmentCategory attachmentCategory) {
			super(MeleeState.NEXT_ATTACHMENT);
			this.attachmentCategory = attachmentCategory;
		}
		
		@Override
		public void init(ByteBuf buf) {
			super.init(buf);
			attachmentCategory = AttachmentCategory.values()[buf.readInt()];
		}
		
		@Override
		public void serialize(ByteBuf buf) {
			super.serialize(buf);
			buf.writeInt(attachmentCategory.ordinal());
		}
	}
	
	private ModContext modContext;
	private PermitManager permitManager;
	private StateManager<MeleeState, ? super PlayerMeleeInstance> stateManager;
	
	private long clickSpammingTimeout = 100;
	
	private Predicate<PlayerMeleeInstance> clickSpammingPreventer = es ->
		System.currentTimeMillis() >= es.getStateUpdateTimestamp() + clickSpammingTimeout;
		
	private Collection<MeleeState> allowedUpdateFromStates = Arrays.asList(MeleeState.MODIFYING_REQUESTED);

	public MeleeAttachmentAspect(ModContext modContext) {
		this.modContext = modContext;
	}
	
	@Override
	public void setStateManager(StateManager<MeleeState, ? super PlayerMeleeInstance> stateManager) {

		if(permitManager == null) {
			throw new IllegalStateException("Permit manager not initialized");
		}
		
		this.stateManager = stateManager
		
		.in(this)
			.change(MeleeState.READY).to(MeleeState.MODIFYING)
			.when(clickSpammingPreventer)
			.withPermit((s, es) -> new EnterAttachmentModePermit(s),
					modContext.getPlayerItemInstanceRegistry()::update,
					permitManager)
			.manual()
			
		.in(this)
			.change(MeleeState.MODIFYING).to(MeleeState.READY)
			.when(clickSpammingPreventer)
			.withAction((instance) -> {permitManager.request(new ExitAttachmentModePermit(MeleeState.READY), 
					instance, (p, e) -> { /* do nothing on callback */});})
			.manual()
			
		.in(this)
			.change(MeleeState.MODIFYING).to(MeleeState.NEXT_ATTACHMENT)
			.when(clickSpammingPreventer)
			.withPermit(null,
					modContext.getPlayerItemInstanceRegistry()::update,
					permitManager)
			.manual()
			
		.in(this)
			.change(MeleeState.NEXT_ATTACHMENT).to(MeleeState.MODIFYING)
			.automatic()
		;
	}
	
	@Override
	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		permitManager.registerEvaluator(EnterAttachmentModePermit.class, PlayerMeleeInstance.class, 
				this::enterAttachmentSelectionMode);
		permitManager.registerEvaluator(ExitAttachmentModePermit.class, PlayerMeleeInstance.class, 
				this::exitAttachmentSelectionMode);
		permitManager.registerEvaluator(ChangeAttachmentPermit.class, PlayerMeleeInstance.class, 
				this::changeAttachment);
		
	}
	
	public void toggleClientAttachmentSelectionMode(EntityPlayer player) {
		
		PlayerMeleeInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMeleeInstance.class);
		if(weaponInstance != null) {
			stateManager.changeState(this, weaponInstance, MeleeState.MODIFYING, MeleeState.READY);
		}
	}
	
	public void onUpdate(EntityPlayer player) {
	    PlayerMeleeInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMeleeInstance.class);
	    if(instance != null) {
	        stateManager.changeStateFromAnyOf(this, instance, allowedUpdateFromStates); // no target state specified, will trigger auto-transitions
	    }  
	}
	
	
	private void enterAttachmentSelectionMode(EnterAttachmentModePermit permit, PlayerMeleeInstance weaponInstance) {
		logger.debug("Entering attachment mode");
		byte selectedAttachmentIndexes[] = new byte[AttachmentCategory.values.length];
		Arrays.fill(selectedAttachmentIndexes, (byte)-1);
		weaponInstance.setSelectedAttachmentIndexes(selectedAttachmentIndexes);
		
		permit.setStatus(Status.GRANTED);
	}
	
	private void exitAttachmentSelectionMode(ExitAttachmentModePermit permit, PlayerMeleeInstance weaponInstance) {
		logger.debug("Exiting attachment mode");
		weaponInstance.setSelectedAttachmentIndexes(new byte[0]);
		
		permit.setStatus(Status.GRANTED);
	}

	List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(EntityPlayer player, ItemStack itemStack) {

		compatibility.ensureTagCompound(itemStack);
		
		List<CompatibleAttachment<? extends AttachmentContainer>> activeAttachments = new ArrayList<>();
		
		PlayerItemInstance<?> itemInstance = modContext.getPlayerItemInstanceRegistry()
				.getItemInstance(player, itemStack);
		

		int[] activeAttachmentsIds;
		if(!(itemInstance instanceof PlayerMeleeInstance)) {
			activeAttachmentsIds = new int[AttachmentCategory.values.length];
			for(CompatibleAttachment<ItemMelee> attachment: ((ItemMelee) itemStack.getItem()).getCompatibleAttachments().values()) {
				if(attachment.isDefault()) {
					activeAttachmentsIds[attachment.getAttachment().getCategory().ordinal()] = Item.getIdFromItem(attachment.getAttachment());
				}
			}
		} else {
			activeAttachmentsIds = ((PlayerMeleeInstance) itemInstance).getActiveAttachmentIds();
		}
		
		ItemMelee weapon = (ItemMelee) itemStack.getItem();
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<? extends AttachmentContainer> compatibleAttachment = weapon.getCompatibleAttachments().get(item);
				if(compatibleAttachment != null) {
					activeAttachments.add(compatibleAttachment);
				}
			}
			
		}
		return activeAttachments;
	}
	
	public void changeAttachment(AttachmentCategory attachmentCategory, PlayerMeleeInstance weaponInstance) {
		if(weaponInstance != null) {
			stateManager.changeState(this, weaponInstance, new ChangeAttachmentPermit(attachmentCategory), 
					MeleeState.NEXT_ATTACHMENT);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void changeAttachment(ChangeAttachmentPermit permit, PlayerMeleeInstance weaponInstance) {
		AttachmentCategory attachmentCategory = permit.attachmentCategory;
		int[] originalActiveAttachmentIds = weaponInstance.getActiveAttachmentIds();
		int[] activeAttachmentIds = Arrays.copyOf(originalActiveAttachmentIds, originalActiveAttachmentIds.length);
		int activeAttachmentIdForThisCategory = activeAttachmentIds[attachmentCategory.ordinal()];
		ItemAttachment<ItemMelee> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<ItemMelee>) Item.getItemById(activeAttachmentIdForThisCategory);
		}
		
		AttachmentLookupResult lookupResult = next(attachmentCategory, currentAttachment, weaponInstance);
		
		if(currentAttachment != null) {
			// Need to apply removal functions first before applying addition functions
			if(currentAttachment.getRemove() != null) {
				currentAttachment.getRemove().apply(currentAttachment, weaponInstance.getWeapon(), weaponInstance.getPlayer());
			}
			if(currentAttachment.getRemove3() != null) {
				currentAttachment.getRemove3().apply(currentAttachment, weaponInstance);
			}
		}
		
		if(lookupResult.index >= 0) {
			ItemStack slotItemStack = weaponInstance.getPlayer().inventory.getStackInSlot(lookupResult.index);
			ItemAttachment<ItemMelee> nextAttachment = (ItemAttachment<ItemMelee>) slotItemStack.getItem();
			
			if(nextAttachment.getApply() != null) {
				nextAttachment.getApply().apply(nextAttachment, weaponInstance.getWeapon(), weaponInstance.getPlayer());
			} else if(nextAttachment.getApply3() != null) {
				nextAttachment.getApply3().apply(nextAttachment, weaponInstance);
			} else if(lookupResult.compatibleAttachment.getMeleeApplyHandler() != null) {
				lookupResult.compatibleAttachment.getMeleeApplyHandler().apply(nextAttachment, weaponInstance);
			} 
//			else {
//				ApplyHandler2<ItemMelee> handler = weaponInstance.getWeapon().getEquivalentHandler(attachmentCategory);
//				if(handler != null) {
//					handler.apply(null, weaponInstance);
//				}
//			}
			compatibility.consumeInventoryItemFromSlot(weaponInstance.getPlayer(), lookupResult.index);
			
			activeAttachmentIds[attachmentCategory.ordinal()] = Item.getIdFromItem(nextAttachment);
		} else {
			activeAttachmentIds[attachmentCategory.ordinal()] = -1;
//			ApplyHandler2<ItemMelee> handler = weaponInstance.getWeapon().getEquivalentHandler(attachmentCategory);
//			if(handler != null) {
//				handler.apply(null, weaponInstance);
//			}
		}

		if(currentAttachment != null) {
			// Item must be added to the same spot the next attachment comes from or to any spot if there is no next attachment
			compatibility.addItemToPlayerInventory(weaponInstance.getPlayer(), currentAttachment, lookupResult.index);
		}
		
		weaponInstance.setActiveAttachmentIds(activeAttachmentIds);
	}
	
	private AttachmentLookupResult next(AttachmentCategory category, Item currentAttachment, PlayerMeleeInstance weaponInstance) {
		/*
		 * Start with selected index -1 (current attachment).
		 * Current index = selected index
		 * Iterate through the inventory until found a compatible attachment
		 * If hit the end, reset the counter to 0 and continue
		 * 
		 * If had current attachment and no other attachment found, nothing changes
		 * e.g.
		 * 	   selectedIndex = -1, currentIndex starts with selectedIndex + 1 = 0
		 * 	   currentIndex: from 0 -> 36
		 * 
		 * If had current attachment and other attachment found at index 10:
		 * e.g.
		 * 	   selectedIndex = -1
		 *     currentIndex: from 0 -> 10
		 *     		addItemToPlayerInventory
		 *     selectedIndex = 10
		 *     
		 * when entering attachment mode, all selected indexes are set to -1
		 * selected indexes are really startSearchFromIndexes
		 */
		
		AttachmentLookupResult result = new AttachmentLookupResult();
		
		byte[] originallySelectedAttachmentIndexes = weaponInstance.getSelectedAttachmentIds();
		if(originallySelectedAttachmentIndexes == null || originallySelectedAttachmentIndexes.length != AttachmentCategory.values.length) {
			return result;
		}
		
		byte[] selectedAttachmentIndexes = Arrays.copyOf(originallySelectedAttachmentIndexes, originallySelectedAttachmentIndexes.length);
		int activeIndex = selectedAttachmentIndexes[category.ordinal()];
		
		
		result.index = -1;
		int offset = activeIndex + 1;
		for(int i = 0; i < 37; i++) {
			// i = 36 corresponds to "no attachment"
			int currentIndex = i + offset;
			
			if(currentIndex >= 36) {
				currentIndex -= 37;
			}
			
			logger.debug("Searching for an attachment in slot {}", currentIndex);
			
			if(currentIndex == -1) {
				result.index = -1;
				break;
			}
			
			ItemStack slotItemStack = weaponInstance.getPlayer().inventory.getStackInSlot(currentIndex);
			if(slotItemStack != null && slotItemStack.getItem() instanceof ItemAttachment) {
				@SuppressWarnings("unchecked")
				ItemAttachment<ItemMelee> attachmentItemFromInventory = (ItemAttachment<ItemMelee>) slotItemStack.getItem();
				CompatibleAttachment<ItemMelee> compatibleAttachment;
				if(attachmentItemFromInventory.getCategory() == category 
						&& (compatibleAttachment = weaponInstance.getWeapon().getCompatibleAttachments().get(attachmentItemFromInventory)) != null
						&& attachmentItemFromInventory != currentAttachment) {
					
					result.index = currentIndex;
					result.compatibleAttachment = compatibleAttachment;
					break;
				}
			}
		}
		
		selectedAttachmentIndexes[category.ordinal()] = (byte)result.index;
		weaponInstance.setSelectedAttachmentIndexes(selectedAttachmentIndexes);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Adds the attachment to the weapon identified by the itemStack without removing the attachment from the inventory.
	 * 
	 * @param nextAttachment
	 * @param itemStack
	 * @param player
	 */
	void addAttachment(ItemAttachment<ItemMelee> attachment, PlayerMeleeInstance weaponInstance) {
		
		int[] activeAttachmentsIds = weaponInstance.getActiveAttachmentIds();
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[attachment.getCategory().ordinal()];
		ItemAttachment<ItemMelee> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<ItemMelee>) Item.getItemById(activeAttachmentIdForThisCategory);
		}
		
		if(currentAttachment == null) {
			if(attachment != null && attachment.getApply() != null) {
				attachment.getApply().apply(attachment, weaponInstance.getWeapon(), weaponInstance.getPlayer());
			}
			activeAttachmentsIds[attachment.getCategory().ordinal()] = Item.getIdFromItem(attachment);;
		} else {
			System.err.println("Attachment of category " + attachment.getCategory() + " installed, remove it first");
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Removes the attachment from the weapon identified by the itemStack without adding the attachment to the inventory.
	 * 
	 * @param attachmentCategory
	 * @param itemStack
	 * @param player
	 * @return
	 */
	ItemAttachment<ItemMelee> removeAttachment(AttachmentCategory attachmentCategory, PlayerMeleeInstance weaponInstance) {
		
		int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
		int activeAttachmentIdForThisCategory = activeAttachmentIds[attachmentCategory.ordinal()];
		ItemAttachment<ItemMelee> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<ItemMelee>) Item.getItemById(activeAttachmentIdForThisCategory);
		}
		
		if(currentAttachment != null && currentAttachment.getRemove() != null) {
			currentAttachment.getRemove().apply(currentAttachment, weaponInstance.getWeapon(), weaponInstance.getPlayer());
		}
		
		if(currentAttachment != null) {
			activeAttachmentIds[attachmentCategory.ordinal()] = -1;
			weaponInstance.setActiveAttachmentIds(activeAttachmentIds);
		}
		
		return currentAttachment;
	}
	
	static ItemAttachment<ItemMelee> getActiveAttachment(AttachmentCategory category, PlayerMeleeInstance weaponInstance) {

		
		ItemAttachment<ItemMelee> itemAttachment = null;
		
		int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
		
		for(int activeIndex: activeAttachmentIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<ItemMelee> compatibleAttachment = weaponInstance.getWeapon().getCompatibleAttachments().get(item);
				if(compatibleAttachment != null && category == compatibleAttachment.getAttachment().getCategory()) {
					itemAttachment = compatibleAttachment.getAttachment();
					break;
				}
			}
			
		}
		return itemAttachment;
	}
	
	static boolean isActiveAttachment(ItemAttachment<ItemMelee> attachment, PlayerMeleeInstance weaponInstance) {
		int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
		return Arrays.stream(activeAttachmentIds).anyMatch((attachmentId) -> attachment == Item.getItemById(attachmentId));
	}
	
	
	ItemAttachment<ItemMelee> getActiveAttachment(PlayerMeleeInstance weaponInstance, AttachmentCategory category) {
		return weaponInstance.getAttachmentItemWithCategory(category);
	}

   
}