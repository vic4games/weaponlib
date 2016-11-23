package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vicmatskiv.weaponlib.Weapon.WeaponInstanceState;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.item.ItemTossEvent;

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
		
		Weapon weapon = (Weapon) item;
		if(weapon.getState(itemStack) == Weapon.STATE_MODIFYING) {
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
		if(storage.getState() != WeaponInstanceState.MODIFYING) {
			storage.setState(WeaponInstanceState.MODIFYING);
		} else {
			storage.setState(WeaponInstanceState.READY);
		}
    	modContext.getChannel().sendToServer(new AttachmentModeMessage());
	}
	
	void toggleServerAttachmentSelectionMode(ItemStack itemStack, EntityPlayer player) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		
		if(((Weapon) itemStack.getItem()).getState(itemStack) != Weapon.STATE_MODIFYING) {
			enterAttachmentSelectionMode(itemStack);
		} else {
			exitAttachmentSelectionMode(itemStack, player);
		}
	}
	
	void enterAttachmentSelectionMode(ItemStack itemStack) {
		ensureItemStack(itemStack);
		int activeAttachmentsIds[] = ensureActiveAttachments(itemStack);
		
		int selectedAttachmentIndexes[] = new int[AttachmentCategory.values.length];
		itemStack.stackTagCompound.setIntArray(SELECTED_ATTACHMENT_INDEXES_TAG, selectedAttachmentIndexes);
		
		itemStack.stackTagCompound.setIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG, 
				Arrays.copyOf(activeAttachmentsIds, activeAttachmentsIds.length));
		
		((Weapon) itemStack.getItem()).setState(itemStack, Weapon.STATE_MODIFYING);
		itemStack.stackTagCompound.setInteger(Weapon.PERSISTENT_STATE_TAG, WeaponInstanceState.MODIFYING.ordinal());
	}
	
	void exitAttachmentSelectionMode(ItemStack itemStack, EntityPlayer player) {
		ensureItemStack(itemStack);
		
		int activeAttachmentsIds[] = itemStack.stackTagCompound.getIntArray(ACTIVE_ATTACHMENT_TAG);
		int previouslySelectedAttachmentIds[] = itemStack.stackTagCompound.getIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG);
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
		
		((Weapon) itemStack.getItem()).setState(itemStack, Weapon.STATE_READY);
		itemStack.stackTagCompound.setInteger(Weapon.PERSISTENT_STATE_TAG, WeaponInstanceState.READY.ordinal());
	}

	List<CompatibleAttachment<Weapon>> getActiveAttachments(ItemStack itemStack) {
		ensureItemStack(itemStack);
		
		List<CompatibleAttachment<Weapon>> activeAttachments = new ArrayList<>();
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		
		Weapon weapon = (Weapon) itemStack.getItem();
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<Weapon> compatibleAttachment = weapon.getCompatibleAttachments().get(item);
				if(compatibleAttachment != null) {
					activeAttachments.add(compatibleAttachment);
				}
				
			}
			
		}
		return activeAttachments;
	}

	private int[] ensureActiveAttachments(ItemStack itemStack) {
		int activeAttachmentsIds[] = itemStack.stackTagCompound.getIntArray(ACTIVE_ATTACHMENT_TAG);
		
		Weapon weapon = (Weapon) itemStack.getItem();
		if(activeAttachmentsIds == null || activeAttachmentsIds.length != AttachmentCategory.values.length) {
			activeAttachmentsIds = new int[AttachmentCategory.values.length];
			itemStack.stackTagCompound.setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
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
		if(!(itemStack.getItem() instanceof Weapon) || ((Weapon) itemStack.getItem()).getState(itemStack) != Weapon.STATE_MODIFYING) {
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
		
		itemStack.stackTagCompound.setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
	}
		
	@SuppressWarnings("unchecked")
	private ItemAttachment<Weapon> nextCompatibleAttachment(AttachmentCategory category, Item currentAttachment, EntityPlayer player, ItemStack itemStack) {
		Weapon weapon = (Weapon) itemStack.getItem();
		
		int[] selectedAttachmentIndexes = itemStack.stackTagCompound.getIntArray(SELECTED_ATTACHMENT_INDEXES_TAG);
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
				int previouslySelectedAttachmentIds[] = itemStack.stackTagCompound.getIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG);
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
		itemStack.stackTagCompound.setIntArray(SELECTED_ATTACHMENT_INDEXES_TAG, selectedAttachmentIndexes);
		return nextCompatibleAttachment;
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
		if(itemStack.stackTagCompound == null) return false;
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[AttachmentCategory.SILENCER.ordinal()];
		return activeAttachmentIdForThisCategory > 0;
	}
	
	void changeTexture(ItemStack itemStack, EntityPlayer player) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		
		if(((Weapon) itemStack.getItem()).getState(itemStack) != Weapon.STATE_MODIFYING) {
			return;
		}
		
		Weapon weapon = (Weapon) itemStack.getItem();
		ensureItemStack(itemStack);
		int currentIndex = Weapon.getActiveTexture(itemStack);
		if(weapon.builder.textureNames.isEmpty()) {
			return;
		}
		if(currentIndex >= weapon.builder.textureNames.size() - 1) {
			currentIndex = 0;
		} else {
			currentIndex++;
		}
		Weapon.setActiveTexture(itemStack, currentIndex);
	}
	
	private void ensureItemStack(ItemStack itemStack) {
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
//			itemStack.stackTagCompound.setInteger(AMMO_TAG, 0);
//			itemStack.stackTagCompound.setInteger(SHOT_COUNTER_TAG, 0);
//			itemStack.stackTagCompound.setFloat(ZOOM_TAG, 1.0f);
//			itemStack.stackTagCompound.setFloat(RECOIL_TAG, builder.recoil);
//			itemStack.stackTagCompound.setLong(STOP_TIMER_TAG, 0);
//			itemStack.stackTagCompound.setLong(RESUME_TIMER_TAG, 0);
//			setState(itemStack, STATE_READY);
		}
	}
}
