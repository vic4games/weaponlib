package com.vicmatskiv.weaponlib;

import java.util.List;

import com.vicmatskiv.weaponlib.Weapon.State;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReloadManager {
	
	private ModContext modContext;
	
	ReloadManager(ModContext modContext) {
		this.modContext = modContext;
	}
	
	@SideOnly(Side.CLIENT)
	void toggleReload(ItemStack itemStack, EntityPlayer player) {
		if(!(itemStack.getItem() instanceof Weapon)) {
			return;
		}
		
		if(Weapon.isModifying(itemStack)) {
			return;
		}
		
		ItemAttachment<Weapon> existingMagazine = modContext.getAttachmentManager().getActiveAttachment(itemStack, AttachmentCategory.MAGAZINE);
		if(existingMagazine != null) {
			initiateUnload(itemStack, player);
		} else {
			initiateReload(itemStack, player);
		}
	}

	@SideOnly(Side.CLIENT)
	void initiateReload(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		if(Weapon.isModifying(itemStack)) {
			return;
		}
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
		
		if (storage.getState() != State.RELOAD_REQUESTED && storage.getState() != State.RELOAD_CONFIRMED
				&& storage.getCurrentAmmo().get() < weapon.builder.ammoCapacity) {
			storage.getReloadingStopsAt().set(player.worldObj.getTotalWorldTime() + Weapon.MAX_RELOAD_TIMEOUT_TICKS);
			storage.setState(State.RELOAD_REQUESTED);
			modContext.getChannel().sendToServer(new ReloadMessage(weapon));
		}
	}

	@SideOnly(Side.CLIENT)
	void completeReload(ItemStack itemStack, EntityPlayer player, ItemMagazine itemMagazine, int ammo, boolean forceQuietReload) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
			
		if (storage.getState() == State.RELOAD_REQUESTED) {
			storage.getCurrentAmmo().set(ammo);
			if ((itemMagazine != null || ammo > 0) && !forceQuietReload) {
				storage.setState(State.RELOAD_CONFIRMED);
				long reloadingStopsAt = player.worldObj.getTotalWorldTime() + weapon.builder.reloadingTimeout;
				storage.getReloadingStopsAt().set(reloadingStopsAt);
				//TODO: Fix me: 
				player.playSound(weapon.getReloadSound(), 1.0F, 1.0F);
				//throw new UnsupportedOperationException("Fix me");
			} else {
				storage.setState(State.READY);
			}
		}
	}

	@SuppressWarnings("unchecked")
	void reload(ItemStack weaponItemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) weaponItemStack.getItem();
		if (weaponItemStack.getTagCompound() != null && !player.isSprinting()) {
			List<ItemMagazine> compatibleMagazines = weapon.getCompatibleMagazines();
			if(!compatibleMagazines.isEmpty()) {
				ItemAttachment<Weapon> existingMagazine = modContext.getAttachmentManager().getActiveAttachment(weaponItemStack, AttachmentCategory.MAGAZINE);
				int ammo = Tags.getAmmo(weaponItemStack);
				ItemMagazine newMagazine = null;
				if(existingMagazine == null) {
					ammo = 0;
					ItemStack magazineItemStack = tryConsumingPart(weapon, compatibleMagazines, player);
					if(magazineItemStack != null) {
						newMagazine = (ItemMagazine) magazineItemStack.getItem();
						ammo = Tags.getAmmo(magazineItemStack);
						Tags.setAmmo(weaponItemStack, ammo);
						modContext.getAttachmentManager().addAttachment((ItemAttachment<Weapon>) magazineItemStack.getItem(), weaponItemStack, player);
						player.playSound(weapon.getReloadSound(), 1.0F, 1.0F);
					}
				}
				modContext.getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.LOAD, newMagazine, ammo), (EntityPlayerMP) player);
				
			} else if (WorldHelper.consumeInventoryItem(player.inventory, weapon.builder.ammo)) {
				Tags.setAmmo(weaponItemStack, weapon.builder.ammoCapacity);
				modContext.getChannel().sendTo(new ReloadMessage(weapon, weapon.builder.ammoCapacity), (EntityPlayerMP) player);
				player.playSound(weapon.getReloadSound(), 1.0F, 1.0F);
			} else {
				Tags.setAmmo(weaponItemStack, 0);
				modContext.getChannel().sendTo(new ReloadMessage(weapon, 0), (EntityPlayerMP) player);
			}
		}
	}
	
	private ItemStack tryConsumingPart(Weapon weapon, List<? extends Item> compatibleParts, EntityPlayer player) {
		ItemStack magazineItemStack = null;
		for(Item magazine: compatibleParts) {
			if((magazineItemStack = consumeInventoryItem(magazine, player)) != null) {
				break;
			}
		}
		return magazineItemStack;
	}
	
	private ItemStack consumeInventoryItem(Item p_146026_1_, EntityPlayer player)
    {
		ItemStack result = null;
        int i = itemSlotIndex(p_146026_1_, player);

		if (i < 0) {
			return null;
		} else {

			result = player.inventory.mainInventory[i];
			if (--player.inventory.mainInventory[i].stackSize <= 0) {
				player.inventory.mainInventory[i] = null;
			}

			return result;
		}
    }
	
	private int itemSlotIndex(Item p_146029_1_, EntityPlayer player)
    {
        for (int i = 0; i < player.inventory.mainInventory.length; ++i)
        {
            if (player.inventory.mainInventory[i] != null && player.inventory.mainInventory[i].getItem() == p_146029_1_)
            {
                return i;
            }
        }

        return -1;
    }
	
	
	@SideOnly(Side.CLIENT)
	void initiateUnload(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		
		if(Weapon.isModifying(itemStack)) {
			return;
		}
		
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
		
		/*
		 * TODO: set state that initiates unloading animation from right here.
		 *       then change state again to send a message to the server.
		 *       The server will remove a magazine attachment immediately
		 */
		if (storage.getState() != State.UNLOAD_STARTED && storage.getState() != State.UNLOAD_REQUESTED_FROM_SERVER && storage.getState() != State.UNLOAD_CONFIRMED) {
			storage.getReloadingStopsAt().set(player.worldObj.getTotalWorldTime() + weapon.getUnloadTimeoutTicks());
			storage.setState(State.UNLOAD_STARTED);
			player.playSound(weapon.getUnloadSound(), 1.0F, 1.0F);
		}
	}
	
	@SideOnly(Side.CLIENT)
	void requestUnloadFromServer(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		
		if(Weapon.isModifying(itemStack)) {
			return;
		}
		
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
		
		/*
		 * TODO: set state that initiates unloading animation from right here.
		 *       then change state again to send a message to the server.
		 *       The server will remove a magazine attachment immediately
		 */
		if (storage.getState() == State.UNLOAD_STARTED) {
			storage.getReloadingStopsAt().set(player.worldObj.getTotalWorldTime() + Weapon.MAX_RELOAD_TIMEOUT_TICKS);
			storage.setState(State.UNLOAD_REQUESTED_FROM_SERVER);
			modContext.getChannel().sendToServer(new ReloadMessage(weapon, ReloadMessage.Type.UNLOAD, null, 
					storage.getCurrentAmmo().get()));
		} else {
			throw new IllegalStateException();
		}
	}

	@SideOnly(Side.CLIENT)
	void completeUnload(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
			
		if (storage.getState() == State.UNLOAD_REQUESTED_FROM_SERVER) {
			storage.getCurrentAmmo().set(0);
			storage.setState(State.READY);
		}
	}

	//@SideOnly(Side.SERVER)
	void unload(ItemStack weaponItemStack, int ammo, EntityPlayer player) {
		Weapon weapon = (Weapon) weaponItemStack.getItem();
		if (weaponItemStack.getTagCompound() != null && !player.isSprinting()) {
			// TODO: add item back to the inventory
			ItemAttachment<Weapon> attachment = modContext.getAttachmentManager().removeAttachment(AttachmentCategory.MAGAZINE, weaponItemStack, player);
			if(attachment instanceof ItemMagazine) {
				ItemStack attachmentItemStack = ((ItemMagazine) attachment).createItemStack();
				Tags.setAmmo(attachmentItemStack, ammo);

				if(!player.inventory.addItemStackToInventory(attachmentItemStack)) {
					System.err.println("Cannot add item back to the inventory: " + attachment);
				}
			} else {
				//throw new IllegalStateException();
			}

			Tags.setAmmo(weaponItemStack, 0);
			modContext.getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.UNLOAD, null, 0), (EntityPlayerMP) player);
			player.playSound(weapon.getReloadSound(), 1.0F, 1.0F);
		}
	}
	
	void update(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if(storage == null) {
			return;
		}
		
		State state = storage.getState();
		
		if(state == State.RELOAD_REQUESTED || state == State.RELOAD_CONFIRMED) {
			long currentTime = player.worldObj.getTotalWorldTime();
			if(storage.getReloadingStopsAt().get() <= currentTime) {
				storage.setState(State.READY);
			}
		} else if(state == State.UNLOAD_STARTED) {
			long currentTime = player.worldObj.getTotalWorldTime();
			if(storage.getReloadingStopsAt().get() <= currentTime) {
				requestUnloadFromServer(itemStack, player);
			}
		} else if(state == State.UNLOAD_REQUESTED_FROM_SERVER || state == State.UNLOAD_CONFIRMED) {
			long currentTime = player.worldObj.getTotalWorldTime();
			if(storage.getReloadingStopsAt().get() <= currentTime) {
				storage.setState(State.READY);
			}
		}
	}
}
