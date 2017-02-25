package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.Permit.Status;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class WeaponAttachmentAspect implements Aspect<WeaponState, PlayerWeaponInstance> {
	
	static {
		TypeRegistry.getInstance().register(EnterAttachmentModePermit.class);
		TypeRegistry.getInstance().register(ExitAttachmentModePermit.class);		
	}
	
	private static final String ACTIVE_ATTACHMENT_TAG = "ActiveAttachments";
//	private static final String SELECTED_ATTACHMENT_INDEXES_TAG = "SelectedAttachments";
//	private static final String PREVIOUSLY_SELECTED_ATTACHMENT_TAG = "PreviouslySelectedAttachments";
	
	public static class EnterAttachmentModePermit extends Permit<WeaponState> {
		
		public EnterAttachmentModePermit() {}
		
		public EnterAttachmentModePermit(WeaponState state) {
			super(state);
		}
	}
	
	public static class ExitAttachmentModePermit extends Permit<WeaponState> {
		
		public ExitAttachmentModePermit() {}
		
		public ExitAttachmentModePermit(WeaponState state) {
			super(state);
		}
	}
	
	private ModContext modContext;
	private PermitManager permitManager;
	private StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager;
	
	private long clickSpammingTimeout = 100;
	
	private Predicate<PlayerWeaponInstance> clickSpammingPreventer = es ->
		System.currentTimeMillis() >= es.getStateUpdateTimestamp() + clickSpammingTimeout;
		
	private Collection<WeaponState> allowedUpdateFromStates = Arrays.asList(WeaponState.MODIFYING_REQUESTED);

	WeaponAttachmentAspect(ModContext modContext) {
		this.modContext = modContext;
	}
	
	@Override
	public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager) {

		if(permitManager == null) {
			throw new IllegalStateException("Permit manager not initialized");
		}
		
		this.stateManager = stateManager
		
			.in(this)
			.change(WeaponState.READY).to(WeaponState.MODIFYING)
			.when(clickSpammingPreventer)
			.withPermit((s, es) -> new EnterAttachmentModePermit(s),
					modContext.getPlayerItemInstanceRegistry()::update,
					permitManager)
			.manual()
			
		.in(this)
			.change(WeaponState.MODIFYING).to(WeaponState.READY)
			.when(clickSpammingPreventer)
			.withAction((instance) -> {permitManager.request(new ExitAttachmentModePermit(WeaponState.READY), 
					instance, (p, e) -> { /* do nothing on callback */});})
			.manual()
		;
	}
	
	@Override
	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		permitManager.registerEvaluator(EnterAttachmentModePermit.class, PlayerWeaponInstance.class, 
				this::enterAttachmentSelectionMode);
		permitManager.registerEvaluator(ExitAttachmentModePermit.class, PlayerWeaponInstance.class, 
				this::exitAttachmentSelectionMode);
		
	}
	
	public void toggleClientAttachmentSelectionMode(EntityPlayer player) {
		
		PlayerWeaponInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
		if(weaponInstance != null) {
			stateManager.changeState(this, weaponInstance, WeaponState.MODIFYING, WeaponState.READY);
		}
	}
	
	void updateMainHeldItem(EntityPlayer player) {
		PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
		if(instance != null) {
			stateManager.changeStateFromAnyOf(this, instance, allowedUpdateFromStates); // no target state specified, will trigger auto-transitions
		}
	}
	
	
	private void enterAttachmentSelectionMode(EnterAttachmentModePermit permit, PlayerWeaponInstance weaponInstance) {
		System.out.println("Entering attachment mode on server");
		ItemStack itemStack = weaponInstance.getItemStack();
		compatibility.ensureTagCompound(itemStack);
		int activeAttachmentsIds[] = weaponInstance.getActiveAttachmentIds();
		
		int selectedAttachmentIndexes[] = new int[AttachmentCategory.values.length];
//		compatibility.getTagCompound(itemStack).setIntArray(SELECTED_ATTACHMENT_INDEXES_TAG, selectedAttachmentIndexes);
//
//		compatibility.getTagCompound(itemStack).setIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG, 
//				Arrays.copyOf(activeAttachmentsIds, activeAttachmentsIds.length));
		
		
		weaponInstance.setSelectedAttachmentIndexes(selectedAttachmentIndexes);
		weaponInstance.setPreviouslyAttachmentIds(Arrays.copyOf(activeAttachmentsIds, activeAttachmentsIds.length));
		
		permit.setStatus(Status.GRANTED);
	}
	
	private void exitAttachmentSelectionMode(ExitAttachmentModePermit permit, PlayerWeaponInstance weaponInstance) {
		System.out.println("Exiting attachment mode on server");
		ItemStack itemStack = weaponInstance.getItemStack();
		compatibility.ensureTagCompound(itemStack);
		EntityPlayer player = weaponInstance.getPlayer();
		
		int activeAttachmentsIds[] = weaponInstance.getActiveAttachmentIds(); //compatibility.getTagCompound(itemStack).getIntArray(ACTIVE_ATTACHMENT_TAG);
		int previouslySelectedAttachmentIds[] = weaponInstance.getPreviouslyAttachmentIds(); // compatibility.getTagCompound(itemStack).getIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG);
		boolean hasValidPreviousAttachmentIds = previouslySelectedAttachmentIds != null
				&& previouslySelectedAttachmentIds.length == activeAttachmentsIds.length;
		for(int i = 0; i < activeAttachmentsIds.length; i++) {
			if(!hasValidPreviousAttachmentIds || activeAttachmentsIds[i] != previouslySelectedAttachmentIds[i]) {
				Item newItem = Item.getItemById(activeAttachmentsIds[i]);
				compatibility.consumeInventoryItem(player, newItem);
			}
			if(hasValidPreviousAttachmentIds && activeAttachmentsIds[i] != previouslySelectedAttachmentIds[i]) {
				Item oldItem = Item.getItemById(previouslySelectedAttachmentIds[i]);
				if(!player.inventory.addItemStackToInventory(new ItemStack(oldItem))) {
					System.err.println("Cannot add item back to the inventory: " + oldItem);
				}
			}
		}
		if(permit != null) {
			permit.setStatus(Status.GRANTED);
		}
	}

	List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(ItemStack itemStack) {
		if(true) {
			return Collections.emptyList();
			
		}
		compatibility.ensureTagCompound(itemStack);
		
		List<CompatibleAttachment<? extends AttachmentContainer>> activeAttachments = new ArrayList<>();
		
		PlayerItemInstance<?> itemInstance = modContext.getPlayerItemInstanceRegistry().getItemInstance(compatibility.clientPlayer(), itemStack);
		PlayerWeaponInstance weaponInstance;
		if(itemInstance instanceof PlayerWeaponInstance) {
			weaponInstance = (PlayerWeaponInstance) itemInstance;
		} else {
			weaponInstance = Tags.getInstance(itemStack, PlayerWeaponInstance.class);
		}
		
		
		if(weaponInstance == null) {
			return Collections.emptyList();
		}
		
		int[] activeAttachmentsIds = weaponInstance.getActiveAttachmentIds();
		
		Weapon weapon = (Weapon) itemStack.getItem();
		
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

//	private static int[] ensureActiveAttachments(ItemStack itemStack) {
//		int activeAttachmentsIds[] = compatibility.getTagCompound(itemStack).getIntArray(ACTIVE_ATTACHMENT_TAG);
//		
//		Weapon weapon = (Weapon) itemStack.getItem();
//		if(activeAttachmentsIds == null || activeAttachmentsIds.length != AttachmentCategory.values.length) {
//			activeAttachmentsIds = new int[AttachmentCategory.values.length];
//			compatibility.getTagCompound(itemStack).setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
//			for(CompatibleAttachment<Weapon> attachment: weapon.getCompatibleAttachments().values()) {
//				if(attachment.isDefault()) {
//					activeAttachmentsIds[attachment.getAttachment().getCategory().ordinal()] = Item.getIdFromItem(attachment.getAttachment());
//				}
//			}
//		}
//		return activeAttachmentsIds;
//	}
	
	@SuppressWarnings("unchecked")
	void changeAttachment(AttachmentCategory attachmentCategory, PlayerWeaponInstance weaponInstance) {
		
		int[] originalActiveAttachmentIds = weaponInstance.getActiveAttachmentIds();
		int[] activeAttachmentIds = Arrays.copyOf(originalActiveAttachmentIds, originalActiveAttachmentIds.length);
		int activeAttachmentIdForThisCategory = activeAttachmentIds[attachmentCategory.ordinal()];
		ItemAttachment<Weapon> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<Weapon>) Item.getItemById(activeAttachmentIdForThisCategory);
		}
		
		ItemAttachment<Weapon> nextAttachment = nextCompatibleAttachment(attachmentCategory, currentAttachment, 
				weaponInstance);
		
		if(nextAttachment != null) {
			System.out.println("Found next attachment " + nextAttachment);
		}

		if(currentAttachment != null && currentAttachment.getRemove() != null) {
			currentAttachment.getRemove().apply(currentAttachment, weaponInstance.getWeapon(), weaponInstance.getPlayer());
		}
		
		if(nextAttachment != null && nextAttachment.getApply() != null) {
			nextAttachment.getApply().apply(nextAttachment, weaponInstance.getWeapon(), weaponInstance.getPlayer());
		}
		
		activeAttachmentIds[attachmentCategory.ordinal()] = Item.getIdFromItem(nextAttachment);;
		
		weaponInstance.setActiveAttachmentIds(activeAttachmentIds);
	}
		
	@SuppressWarnings("unchecked")
	private ItemAttachment<Weapon> nextCompatibleAttachment(AttachmentCategory category, Item currentAttachment, PlayerWeaponInstance weaponInstance) {
		
		int[] originallySelectedAttachmentIndexes = weaponInstance.getSelectedAttachmentIds();
		if(originallySelectedAttachmentIndexes == null || originallySelectedAttachmentIndexes.length != AttachmentCategory.values.length) {
			return null;
		}

		int[] selectedAttachmentIndexes = Arrays.copyOf(originallySelectedAttachmentIndexes, originallySelectedAttachmentIndexes.length);
		int activeIndex = selectedAttachmentIndexes[category.ordinal()];
		
		/*
		 * 0 - original attachment
		 * 1 - 36 - attachments from inventory
		 * -1 - no attachment
		 */
		

		/*
		 * No original attachment (0), no compatible attachments, starting from 0
		 *    currentIndex: 0 -> -1
		 *    
		 * No original attachment (0), no compatible attachments, starting from -1
		 *    currentIndex: -1 -> 0
		 *    
		 *    
		 * No original attachment (0), compatible attachment found
		 *    currentIndex: 0 -> [1 - 36]
		 * 
		 * No original attachment (0), compatible attachment found, starting from [1 - 35]
		 *    currentIndex: [1 - 35] -> [1 - 35] + 1
		 *    
		 * No original attachment (0), compatible attachment found, starting from [36]
		 *    currentIndex: 36 -> 37 -> -1
		 *    
		 * No original attachment (0), compatible attachment found, starting from [-1]
		 *    currentIndex: -1 -> 0 (visual effect: no change, switching from no attachment to no attachment)
		 * 
		 */
		
		int currentIndex = activeIndex + 1;
		
		ItemAttachment<Weapon> nextCompatibleAttachment = null;
		for(; currentIndex <= 36; currentIndex++) {
			
			if(currentIndex == 0) {
				// Select original attachment that was there prior to entering attachment mode
				int previouslySelectedAttachmentIds[] = weaponInstance.getPreviouslyAttachmentIds();
				nextCompatibleAttachment = (ItemAttachment<Weapon>) Item.getItemById(previouslySelectedAttachmentIds[category.ordinal()]);
				if(nextCompatibleAttachment != null) {
					// if original attachment existed, exit, iterate till found one
					// reason: never stop on original attachment (index 0) if it's null
					break;
				} else {
					continue;
				}
			}
			ItemStack slotItemStack = weaponInstance.getPlayer().inventory.getStackInSlot(currentIndex - 1);
			if(slotItemStack != null && slotItemStack.getItem() instanceof ItemAttachment) {
				ItemAttachment<Weapon> attachmentItemFromInventory = (ItemAttachment<Weapon>) slotItemStack.getItem();
				if(attachmentItemFromInventory.getCategory() == category && weaponInstance.getWeapon().getCompatibleAttachments().containsKey(attachmentItemFromInventory)
						&& attachmentItemFromInventory != currentAttachment) {
					nextCompatibleAttachment = attachmentItemFromInventory;
					break;
				}
			}
		}
		if(nextCompatibleAttachment == null) {
			currentIndex = -1;
		}
		
		selectedAttachmentIndexes[category.ordinal()] = currentIndex;
		//compatibility.getTagCompound(itemStack).setIntArray(SELECTED_ATTACHMENT_INDEXES_TAG, selectedAttachmentIndexes);
		weaponInstance.setSelectedAttachmentIndexes(selectedAttachmentIndexes);
		return nextCompatibleAttachment;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Adds the attachment to the weapon identified by the itemStack without removing the attachment from the inventory.
	 * 
	 * @param nextAttachment
	 * @param itemStack
	 * @param player
	 */
	void addAttachment(ItemAttachment<Weapon> attachment, PlayerWeaponInstance weaponInstance) {
		
		int[] activeAttachmentsIds = weaponInstance.getActiveAttachmentIds();
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[attachment.getCategory().ordinal()];
		ItemAttachment<Weapon> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<Weapon>) Item.getItemById(activeAttachmentIdForThisCategory);
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
	ItemAttachment<Weapon> removeAttachment(AttachmentCategory attachmentCategory, PlayerWeaponInstance weaponInstance) {
		
		int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
		int activeAttachmentIdForThisCategory = activeAttachmentIds[attachmentCategory.ordinal()];
		ItemAttachment<Weapon> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<Weapon>) Item.getItemById(activeAttachmentIdForThisCategory);
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
	
	static ItemAttachment<Weapon> getActiveAttachment(AttachmentCategory category, PlayerWeaponInstance weaponInstance) {

		
		ItemAttachment<Weapon> itemAttachment = null;
		
		int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
		
		for(int activeIndex: activeAttachmentIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<Weapon> compatibleAttachment = weaponInstance.getWeapon().getCompatibleAttachments().get(item);
				if(compatibleAttachment != null && category == compatibleAttachment.getAttachment().getCategory()) {
					itemAttachment = compatibleAttachment.getAttachment();
					break;
				}
			}
			
		}
		return itemAttachment;
	}
	
	static boolean isActiveAttachment(ItemAttachment<Weapon> attachment, PlayerWeaponInstance weaponInstance) {
		int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
		return Arrays.stream(activeAttachmentIds).anyMatch((attachmentId) -> attachment == Item.getItemById(attachmentId));
	}
	
	boolean isSilencerOn(PlayerWeaponInstance weaponInstance) {
		int[] activeAttachmentsIds = weaponInstance.getActiveAttachmentIds();
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[AttachmentCategory.SILENCER.ordinal()];
		return activeAttachmentIdForThisCategory > 0;
	}
	
	void changeTexture(ItemStack itemStack, EntityPlayer player) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		
		Weapon weapon = (Weapon) itemStack.getItem();
		compatibility.ensureTagCompound(itemStack);
		int currentIndex = Tags.getActiveTexture(itemStack);
		if(weapon.builder.textureNames.isEmpty()) {
			return;
		}
		if(currentIndex >= weapon.builder.textureNames.size() - 1) {
			currentIndex = 0;
		} else {
			currentIndex++;
		}
		Tags.setActiveTexture(itemStack, currentIndex);
	}

}
