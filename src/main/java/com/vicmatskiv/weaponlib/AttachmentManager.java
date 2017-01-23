package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vicmatskiv.weaponlib.Weapon.State;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class AttachmentManager {
	
	private static final String ACTIVE_ATTACHMENT_TAG = "ActiveAttachments";
	private static final String SELECTED_ATTACHMENT_INDEXES_TAG = "SelectedAttachments";
	private static final String PREVIOUSLY_SELECTED_ATTACHMENT_TAG = "PreviouslySelectedAttachments";
	
	private ModContext modContext;
	
	AttachmentManager(ModContext modContext) {
		this.modContext = modContext;
	}

	@SubscribeEvent
	public void onItemToss(ItemTossEvent itemTossEvent) {
		ItemStack itemStack = itemTossEvent.entityItem.getEntityItem();
		Item item = itemStack.getItem();
		if(!(item instanceof Weapon)) {
			return; 
		}
		
		if(Weapon.isModifying(itemStack)) {
			exitAttachmentSelectionMode(itemStack, itemTossEvent.player);
		}
	}
	
	void toggleClientAttachmentSelectionMode(ItemStack itemStack, EntityPlayer player) {
		Item item = itemStack.getItem();
		if(!(item instanceof Weapon)) {
			return; 
		}
		Weapon weapon = (Weapon) item;
		WeaponClientStorage storage = weapon.getWeaponClientStorage(player);
		if(storage == null) return;
		if(storage.getState() == State.EJECT_SPENT_ROUND) {
			return;
		}
		if(storage.getState() != State.MODIFYING) {
			storage.setState(State.MODIFYING);
		} else {
			storage.setState(State.READY);
		}
    	modContext.getChannel().sendToServer(new AttachmentModeMessage());
	}
	
	void toggleServerAttachmentSelectionMode(ItemStack itemStack, EntityPlayer player) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		
		if(!Weapon.isModifying(itemStack)) {
			enterAttachmentSelectionMode(itemStack);
		} else {
			exitAttachmentSelectionMode(itemStack, player);
		}
	}
	
	void enterAttachmentSelectionMode(ItemStack itemStack) {
		ensureItemStack(itemStack);
		int activeAttachmentsIds[] = ensureActiveAttachments(itemStack);
		
		int selectedAttachmentIndexes[] = new int[AttachmentCategory.values.length];
		
		itemStack.getTagCompound().setIntArray(SELECTED_ATTACHMENT_INDEXES_TAG, selectedAttachmentIndexes);
		
		itemStack.getTagCompound().setIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG, 
				Arrays.copyOf(activeAttachmentsIds, activeAttachmentsIds.length));
		
		Weapon.setModifying(itemStack, true);
	}
	
	void exitAttachmentSelectionMode(ItemStack itemStack, EntityPlayer player) {
		ensureItemStack(itemStack);
		
		int activeAttachmentsIds[] = itemStack.getTagCompound().getIntArray(ACTIVE_ATTACHMENT_TAG);
		int previouslySelectedAttachmentIds[] = itemStack.getTagCompound().getIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG);
		for(int i = 0; i < activeAttachmentsIds.length; i++) {
			if(activeAttachmentsIds[i] != previouslySelectedAttachmentIds[i]) {
				Item newItem = Item.getItemById(activeAttachmentsIds[i]);
				Item oldItem = Item.getItemById(previouslySelectedAttachmentIds[i]);
				player.inventory.consumeInventoryItem(newItem);
				if(!player.inventory.addItemStackToInventory(new ItemStack(oldItem))) {
					System.err.println("Cannot add item back to the inventory: " + oldItem);
				}
			}
		}
		
		Weapon.setModifying(itemStack, false);
	}

	List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(ItemStack itemStack) {
		ensureItemStack(itemStack);
		
		List<CompatibleAttachment<? extends AttachmentContainer>> activeAttachments = new ArrayList<>();
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		
		Weapon weapon = (Weapon) itemStack.getItem();
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<? extends AttachmentContainer> compatibleAttachment = (CompatibleAttachment<? extends AttachmentContainer>) weapon.getCompatibleAttachments().get(item);
				if(compatibleAttachment != null) {
					activeAttachments.add(compatibleAttachment);
				}
			}
			
		}
		return activeAttachments;
	}

	private int[] ensureActiveAttachments(ItemStack itemStack) {
		int activeAttachmentsIds[] = itemStack.getTagCompound().getIntArray(ACTIVE_ATTACHMENT_TAG);
		
		Weapon weapon = (Weapon) itemStack.getItem();
		if(activeAttachmentsIds == null || activeAttachmentsIds.length != AttachmentCategory.values.length) {
			activeAttachmentsIds = new int[AttachmentCategory.values.length];
			itemStack.getTagCompound().setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
			for(CompatibleAttachment<Weapon> attachment: weapon.getCompatibleAttachments().values()) {
				if(attachment.isDefault()) {
					activeAttachmentsIds[attachment.getAttachment().getCategory().ordinal()] = Item.getIdFromItem(attachment.getAttachment());
				}
			}
		}
		return activeAttachmentsIds;
	}
	
	@SuppressWarnings("unchecked")
	void changeAttachment(AttachmentCategory attachmentCategory, ItemStack itemStack, EntityPlayer player) {
		if(!(itemStack.getItem() instanceof Weapon) || 
				!Weapon.isModifying(itemStack) /*((Weapon) itemStack.getItem()).getState(itemStack) != Weapon.STATE_MODIFYING*/) {
			return;
		}
		
		ensureItemStack(itemStack);
		Weapon weapon = (Weapon) itemStack.getItem();
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[attachmentCategory.ordinal()];
		ItemAttachment<Weapon> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<Weapon>) Item.getItemById(activeAttachmentIdForThisCategory);
		}
		
		ItemAttachment<Weapon> nextAttachment = nextCompatibleAttachment(attachmentCategory, currentAttachment, player, itemStack);

		if(currentAttachment != null && currentAttachment.getRemove() != null) {
			currentAttachment.getRemove().apply(currentAttachment, weapon, player);
		}
		
		if(nextAttachment != null && nextAttachment.getApply() != null) {
			nextAttachment.getApply().apply(nextAttachment, weapon, player);
		}
		
		activeAttachmentsIds[attachmentCategory.ordinal()] = Item.getIdFromItem(nextAttachment);;
		
		itemStack.getTagCompound().setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
	}
		
	@SuppressWarnings("unchecked")
	private ItemAttachment<Weapon> nextCompatibleAttachment(AttachmentCategory category, Item currentAttachment, EntityPlayer player, ItemStack itemStack) {
		Weapon weapon = (Weapon) itemStack.getItem();
		
		int[] selectedAttachmentIndexes = itemStack.getTagCompound().getIntArray(SELECTED_ATTACHMENT_INDEXES_TAG);
		if(selectedAttachmentIndexes == null || selectedAttachmentIndexes.length != AttachmentCategory.values.length) {
			return null;
		}

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
				int previouslySelectedAttachmentIds[] = itemStack.getTagCompound().getIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG);
				nextCompatibleAttachment = (ItemAttachment<Weapon>) Item.getItemById(previouslySelectedAttachmentIds[category.ordinal()]);
				if(nextCompatibleAttachment != null) {
					// if original attachment existed, exit, iterate till found one
					// reason: never stop on original attachment (index 0) if it's null
					break;
				} else {
					continue;
				}
			}
			ItemStack slotItemStack = player.inventory.getStackInSlot(currentIndex - 1);
			if(slotItemStack != null && slotItemStack.getItem() instanceof ItemAttachment) {
				ItemAttachment<Weapon> attachmentItemFromInventory = (ItemAttachment<Weapon>) slotItemStack.getItem();
				if(attachmentItemFromInventory.getCategory() == category && weapon.getCompatibleAttachments().containsKey(attachmentItemFromInventory)
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
		itemStack.getTagCompound().setIntArray(SELECTED_ATTACHMENT_INDEXES_TAG, selectedAttachmentIndexes);
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
	void addAttachment(ItemAttachment<Weapon> attachment, ItemStack weaponStack, EntityPlayer player) {
		if(!(weaponStack.getItem() instanceof Weapon)) {
			throw new IllegalStateException();
		}
		
		ensureItemStack(weaponStack);
		Weapon weapon = (Weapon) weaponStack.getItem();
		
		int[] activeAttachmentsIds = ensureActiveAttachments(weaponStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[attachment.getCategory().ordinal()];
		ItemAttachment<Weapon> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<Weapon>) Item.getItemById(activeAttachmentIdForThisCategory);
		}
		
		if(currentAttachment == null) {
			
			if(attachment != null && attachment.getApply() != null) {
				attachment.getApply().apply(attachment, weapon, player);
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
	ItemAttachment<Weapon> removeAttachment(AttachmentCategory attachmentCategory, ItemStack weaponStack, EntityPlayer player) {
		if(!(weaponStack.getItem() instanceof Weapon)) {
			throw new IllegalStateException();
		}
		
		ensureItemStack(weaponStack);
		Weapon weapon = (Weapon) weaponStack.getItem();
		
		int[] activeAttachmentsIds = ensureActiveAttachments(weaponStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[attachmentCategory.ordinal()];
		ItemAttachment<Weapon> currentAttachment = null;
		if(activeAttachmentIdForThisCategory > 0) {
			currentAttachment = (ItemAttachment<Weapon>) Item.getItemById(activeAttachmentIdForThisCategory);
		}
		
		if(currentAttachment != null && currentAttachment.getRemove() != null) {
			currentAttachment.getRemove().apply(currentAttachment, weapon, player);
		}
		
		if(currentAttachment != null) {
			activeAttachmentsIds[attachmentCategory.ordinal()] = -1;
			weaponStack.getTagCompound().setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
		}
		
		return currentAttachment;
	}
	
	ItemAttachment<Weapon> getActiveAttachment(ItemStack itemStack, AttachmentCategory category) {
		ensureItemStack(itemStack);
		
		Weapon weapon = (Weapon) itemStack.getItem();
		
		ItemAttachment<Weapon> itemAttachment = null;
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<Weapon> compatibleAttachment = weapon.getCompatibleAttachments().get(item);
				if(compatibleAttachment != null && category == compatibleAttachment.getAttachment().getCategory()) {
					itemAttachment = compatibleAttachment.getAttachment();
					break;
				}
			}
			
		}
		return itemAttachment;
	}
	
	boolean isActiveAttachment(ItemStack itemStack, ItemAttachment<Weapon> attachment) {
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		return Arrays.stream(activeAttachmentsIds).anyMatch((attachmentId) -> attachment == Item.getItemById(attachmentId));
	}
	
	boolean isSilencerOn(ItemStack itemStack) {
		if(itemStack.getTagCompound() == null) return false;
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[AttachmentCategory.SILENCER.ordinal()];
		return activeAttachmentIdForThisCategory > 0;
	}
	
	void changeTexture(ItemStack itemStack, EntityPlayer player) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		
		if(!Weapon.isModifying(itemStack)) {
			return;
		}
		
		Weapon weapon = (Weapon) itemStack.getItem();
		ensureItemStack(itemStack);
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
	
	private void ensureItemStack(ItemStack itemStack) {
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
	}
}
