package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.Weapon.State;
import com.vicmatskiv.weaponlib.state.NetworkPermitManager;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.RegisteredUuid;
import com.vicmatskiv.weaponlib.state.StateManager;
import com.vicmatskiv.weaponlib.state.UniversalObject;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class ReloadManager {
	
	private ModContext modContext;
	
	
	
	
	class ReloadContext extends CommonWeaponAspectContext {
		
	}
	
	StateManager stateManager; // TODO: weapon and magazine each needs to have its own state manager!
	
	private Predicate<ReloadContext> magazineAttached;
	private Predicate<ReloadContext> supportsDirectBulletLoad;
	private Predicate<ReloadContext> receivedAmmo;
	private Predicate<ReloadContext> quietReload;
	private Predicate<ReloadContext> reloadRequestTimeoutExpired;
	private Predicate<CommonWeaponAspectContext> reloadAnimationCompleted;

	private Predicate<ReloadContext> unloadingAnimationCompleted;

	private Predicate<CommonWeaponAspectContext> unloadAnimationCompleted;

	private BiConsumer<Permit, CommonWeaponAspectContext> serverAction;
	
	static class States { 
		static final com.vicmatskiv.weaponlib.state.ManagedState READY = new com.vicmatskiv.weaponlib.state.ManagedState();
		static final com.vicmatskiv.weaponlib.state.ManagedState RELOAD = new com.vicmatskiv.weaponlib.state.ManagedState();
		static final com.vicmatskiv.weaponlib.state.ManagedState UNLOAD = new com.vicmatskiv.weaponlib.state.ManagedState();
	};
	
	ReloadManager(ModContext modContext) {
		this.modContext = modContext;
		

		PermitManager<CommonWeaponAspectContext> weaponPermitManager = new NetworkPermitManager<CommonWeaponAspectContext>(modContext, serverAction);
		
		stateManager
		
		.in(ReloadContext.class).change(States.READY).to(States.RELOAD)
			.when(supportsDirectBulletLoad.or(magazineAttached.negate()))
			.withPermit(ReloadContext::getPermit, weaponPermitManager)
			.allowed()

		.in(CommonWeaponAspectContext.class).change(States.RELOAD).to(States.READY) // note the use of base context
			.when(reloadAnimationCompleted)
			.allowed()
			
		.in(ReloadContext.class).change(States.READY).to(States.UNLOAD)
			.when(magazineAttached)
			.withPermit(ReloadContext::getPermit, weaponPermitManager)
			.allowed()
		
		.in(CommonWeaponAspectContext.class).change(States.UNLOAD).to(States.READY) // note the use of base context
			.when(unloadAnimationCompleted)
			.allowed();

				
	}
//	
//	private void startUnload(MyStateContext context) {
//
//	}
//
//	private void requestLoadFromServer(MyStateContext context) {
//
//	}
//	
//	private void requestUnloadFromServer(MyStateContext context) {
//		// TODO Auto-generated method stub
//
//	}

//	void toggleReload(ReloadContext context) {
//		stateManager.changeState(context, States.RELOAD, States.UNLOAD);
//	}
//	
//	void completeLoad(MyStateContext context) {
//		stateManager.changeState(context, Weapon.State.RELOAD_CONFIRMED, Weapon.State.READY);
//	}
//	
//	void completeUnload(MyStateContext context) {
//		stateManager.changeState(context, Weapon.State.READY);
//	}
//	
//	void update(MyStateContext context) {
//		stateManager.changeState(context, Weapon.State.UNLOAD_REQUESTED_FROM_SERVER, Weapon.State.READY);
//	}
	
	//@SideOnly(Side.CLIENT)
	void toggleReload(ItemStack itemStack, EntityPlayer player) {
		/*
		 * Required rule for weapon:
		 * 
		 * 
		 * from: "ready" to "load" if ammoCapacity > 0 (weapon with non-magazine load)
		 * 
		 * from: "ready" to "load if ammoCapacity == 0 and no magazine attached
		 * 
		 * from: "ready" to "unload" if magazine attached
		 * 
		 * stateManager.addRule("read", "load", (context) -> ammoCapacity > 0);
		 * 
		 * 
		 * stateManager.addRule(
		 * 		"UNLOAD_STARTED", 
		 * 		"UNLOAD_REQUEST_FROM_SERVER", 
		 * 		(context) -> afterTimeout(10, TimeUnit.MILLISECONDS),
		 *      (context, fromState, toState) -> doSomething()

		 * 
		 * 
		 * switch(stateManager.findNextState(context, "UNLOAD_STARTED", "RELOAD_REQUESTED")) {
		 *   case "UNLOAD_STARTED": break;
		 *   case "RELOAD_REQUESTED": break;
		 * }
		 */
	
		
		if(itemStack.getItem() instanceof Weapon) {
			if(Weapon.isModifying(itemStack) || Weapon.isEjectedSpentRound(player, itemStack)) {
				return;
			}
			
			if(((Weapon)itemStack.getItem()).getAmmoCapacity() > 0) {
				initiateLoad(itemStack, player);
			} else {
				ItemAttachment<Weapon> existingMagazine = modContext.getAttachmentManager().getActiveAttachment(itemStack, AttachmentCategory.MAGAZINE);
				if(existingMagazine != null) {
					initiateUnload(itemStack, player);
				} else {
					initiateLoad(itemStack, player);
				}
			}
		} else if(itemStack.getItem() instanceof ItemMagazine) {
			initiateMagazineLoad(itemStack, player);
		}
		

	}
	
	//@SideOnly(Side.CLIENT)
	void initiateMagazineLoad(ItemStack itemStack, EntityPlayer player) {
		ItemMagazine magazine = (ItemMagazine) itemStack.getItem();
		
		if (Tags.getAmmo(itemStack) < magazine.getAmmo()) {
			modContext.getChannel().getChannel().sendToServer(new ReloadMessage(null, ReloadMessage.Type.LOAD, magazine, 0));
		}
	}

	//@SideOnly(Side.CLIENT)
	void initiateLoad(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		if(Weapon.isModifying(itemStack)) {
			return;
		}
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
		
		if (storage.getState() != State.RELOAD_REQUESTED && storage.getState() != State.RELOAD_CONFIRMED
				&& (weapon.getAmmoCapacity() == 0 || storage.getCurrentAmmo().get() < weapon.getAmmoCapacity())) {
			storage.getReloadingStopsAt().set(compatibility.world(player).getTotalWorldTime() + Weapon.MAX_RELOAD_TIMEOUT_TICKS);
			storage.setState(State.RELOAD_REQUESTED);
			modContext.getChannel().getChannel().sendToServer(new ReloadMessage(weapon));
		}
	}
	
	//@SideOnly(Side.CLIENT)
	void completeReload(ItemStack itemStack, EntityPlayer player, ItemMagazine itemMagazine, int ammo, boolean forceQuietReload) {
		
		if(itemStack.getItem() instanceof Weapon) {
			Weapon weapon = (Weapon) itemStack.getItem();
			WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
			if (storage == null) {
				return;
			}
				
			if (storage.getState() == State.RELOAD_REQUESTED) {
				storage.getCurrentAmmo().set(ammo);
				if ((itemMagazine != null || ammo > 0) && !forceQuietReload) {
					storage.setState(State.RELOAD_CONFIRMED);
					long reloadingStopsAt = compatibility.world(player).getTotalWorldTime() + weapon.builder.reloadingTimeout;
					storage.getReloadingStopsAt().set(reloadingStopsAt);
					compatibility.playSound(player, weapon.getReloadSound(), 1.0F, 1.0F);
				} else {
					storage.setState(State.READY);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	void reload(ItemStack itemStack, EntityPlayer player) {
		
		ItemStack consumedStack = null;
		if(itemStack.getItem() instanceof ItemMagazine) {
			ItemStack magazineItemStack = itemStack;
			ItemMagazine magazine = (ItemMagazine) magazineItemStack.getItem();
			List<ItemBullet> compatibleBullets = magazine.getCompatibleBullets();
			int currentAmmo = Tags.getAmmo(magazineItemStack);
			if(Tags.getState(magazineItemStack) != Weapon.State.RELOAD_CONFIRMED && currentAmmo < magazine.getAmmo() && (consumedStack = WorldHelper.tryConsumingCompatibleItem(compatibleBullets, magazine.getAmmo() - currentAmmo, player)) != null) {
				Tags.setState(magazineItemStack, Weapon.State.RELOAD_CONFIRMED);
				Tags.setDefaultTimer(magazineItemStack, compatibility.world(player).getTotalWorldTime() + magazine.getReloadTimeout());
				Tags.setAmmo(magazineItemStack, currentAmmo + compatibility.getStackSize(consumedStack));
				compatibility.playSound(player, magazine.getReloadSound(), 1.0F, 1.0F);
			}
			modContext.getChannel().getChannel().sendTo(new ReloadMessage(null, ReloadMessage.Type.LOAD, magazine, currentAmmo), (EntityPlayerMP) player);
			
		} else if(itemStack.getItem() instanceof Weapon) {
			ItemStack weaponItemStack = itemStack;
			Weapon weapon = (Weapon) itemStack.getItem();
			if (compatibility.getTagCompound(weaponItemStack) != null && !player.isSprinting()) {
				List<ItemMagazine> compatibleMagazines = weapon.getCompatibleMagazines();
				List<ItemAttachment<Weapon>> compatibleBullets = weapon.getCompatibleAttachments(ItemBullet.class);
				if(!compatibleMagazines.isEmpty()) {
					ItemAttachment<Weapon> existingMagazine = modContext.getAttachmentManager().getActiveAttachment(weaponItemStack, AttachmentCategory.MAGAZINE);
					int ammo = Tags.getAmmo(weaponItemStack);
					ItemMagazine newMagazine = null;
					if(existingMagazine == null) {
						ammo = 0;
						ItemStack magazineItemStack = WorldHelper.tryConsumingCompatibleItem(compatibleMagazines,
								1, player, magazineStack -> Tags.getAmmo(magazineStack) > 0, magazineStack -> true);
						if(magazineItemStack != null) {
							newMagazine = (ItemMagazine) magazineItemStack.getItem();
							ammo = Tags.getAmmo(magazineItemStack);
							Tags.setAmmo(weaponItemStack, ammo);
							modContext.getAttachmentManager().addAttachment((ItemAttachment<Weapon>) magazineItemStack.getItem(), weaponItemStack, player);
							compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
						}
					}
					modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.LOAD, newMagazine, ammo), (EntityPlayerMP) player);
					
				} else if(!compatibleBullets.isEmpty() && (consumedStack = WorldHelper.tryConsumingCompatibleItem(compatibleBullets,
						Math.min(weapon.getMaxBulletsPerReload(), weapon.getAmmoCapacity() - Tags.getAmmo(weaponItemStack)), player)) != null) {
					int ammo = Tags.getAmmo(weaponItemStack) + compatibility.getStackSize(consumedStack);
					Tags.setAmmo(weaponItemStack, ammo);
					modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ammo), (EntityPlayerMP) player);
					compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
				} else if (WorldHelper.consumeInventoryItem(player.inventory, weapon.builder.ammo)) {
					Tags.setAmmo(weaponItemStack, weapon.builder.ammoCapacity);
					modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, weapon.builder.ammoCapacity), (EntityPlayerMP) player);
					compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
				} else {
					Tags.setAmmo(weaponItemStack, 0);
					modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, 0), (EntityPlayerMP) player);
				}
			} 
		}
	}
	
	
	//@SideOnly(Side.CLIENT)
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
			storage.getReloadingStopsAt().set(compatibility.world(player).getTotalWorldTime() + weapon.getUnloadTimeoutTicks());
			storage.setState(State.UNLOAD_STARTED);
			compatibility.playSound(player, weapon.getUnloadSound(), 1.0F, 1.0F);
		}
	}
	
	//@SideOnly(Side.CLIENT)
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
			storage.getReloadingStopsAt().set(compatibility.world(player).getTotalWorldTime() + Weapon.MAX_RELOAD_TIMEOUT_TICKS);
			storage.setState(State.UNLOAD_REQUESTED_FROM_SERVER);
			modContext.getChannel().getChannel().sendToServer(new ReloadMessage(weapon, ReloadMessage.Type.UNLOAD, null, 
					storage.getCurrentAmmo().get()));
		} else {
			throw new IllegalStateException();
		}
	}

	//@SideOnly(Side.CLIENT)
	void completeUnload(ItemStack itemStack, EntityPlayer player) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
		if (storage == null) {
			return;
		}
			
		/*
		 * What if we have different scenarios driving state change conditions?
		 * For example, here we allow to transition state immediately, while in update method only after timeout
		 * 
		 * Option 1:
		 * 	create a condition to account for all scenarios, e.g.
		 * 
		 *  stateManager.allow(state1)
		 *  	.changeTo(state2)
		 *  	.when(context -> afterTimeout(context))
		 *  	.or(context -> context.tmpOverride)
		 *  	.execute((context, from, to) -> {})
		 *  	.ready();
		 */
		if (storage.getState() == State.UNLOAD_REQUESTED_FROM_SERVER) {
			storage.getCurrentAmmo().set(0);
			storage.setState(State.READY);
		}
	}

	//@SideOnly(Side.SERVER)
	void unload(ItemStack weaponItemStack, int ammo, EntityPlayer player) {
		Weapon weapon = (Weapon) weaponItemStack.getItem();
		if (compatibility.getTagCompound(weaponItemStack) != null && !player.isSprinting()) {
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
			modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.UNLOAD, null, 0), (EntityPlayerMP) player);
			compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
		}
	}
	
	void update(ItemStack itemStack, EntityPlayer player) {
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
			Weapon weapon = (Weapon) itemStack.getItem();
			WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, weapon);
			if(storage == null) {
				return;
			}
			
			State state = storage.getState();
			
			/*
			 * Required rule:
			 * 
			 * stateManager.addRule("RELOAD_REQUESTED", "READY", (context) -> after(reloadRequestExpirationTimeout), noAction)
			 * 
			 * stateManager.addRule("RELOAD_CONFIRMED", "READY", (context) -> after(reloadAnimationTimeout), noAction)
			 * 
			 * stateManager.addRule("UNLOAD_STARTED", "UNLOAD_REQUESTED_FROM_SERVER", (context) -> after(unloadAnimationDuration),
			 * 		(context, fromState, toState) -> requestUnloadFromServer() // note that request unload should not attempt to change state
			 * 
			 * stateManager.addRule("UNLOAD_REQUESTED_FROM_SERVER", "READY", (context) -> after(unloadRequestTimeout), noAction)
			 * 
			 * Note: UNLOAD_CONFIRMED state needs to be removed
			 * 
			 * then update method becomes as simple as:
			 * 
			 * stateManager.changeState(context, State.READY, State.UNLOAD_REQUESTED_FROM_SERVER);
			 */
			if(state == State.RELOAD_REQUESTED || state == State.RELOAD_CONFIRMED) {
				long currentTime = compatibility.world(player).getTotalWorldTime();
				if(storage.getReloadingStopsAt().get() <= currentTime) {
					storage.setState(State.READY);
				}
			} else if(state == State.UNLOAD_STARTED) {
				long currentTime = compatibility.world(player).getTotalWorldTime();
				if(storage.getReloadingStopsAt().get() <= currentTime) {
					requestUnloadFromServer(itemStack, player);
				}
			} else if(state == State.UNLOAD_REQUESTED_FROM_SERVER || state == State.UNLOAD_CONFIRMED) {
				long currentTime = compatibility.world(player).getTotalWorldTime();
				if(storage.getReloadingStopsAt().get() <= currentTime) {
					storage.setState(State.READY);
				}
			}
		} 
	}
}
