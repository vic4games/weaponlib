package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.Permit.Status;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ReloadAspect implements Aspect<WeaponState, PlayerWeaponState> {
	
	static {
		TypeRegistry.getInstance().register(UnloadPermit.class);
		TypeRegistry.getInstance().register(LoadPermit.class);
		//TypeRegistry.getInstance().register(ReloadContext.class);
	}
	
	public static class UnloadPermit extends Permit<WeaponState> {
		
		public UnloadPermit() {}
		
		public UnloadPermit(WeaponState state) {
			super(state);
		}
	}
	
	public static class LoadPermit extends Permit<WeaponState> {
		
		public LoadPermit() {}
		
		public LoadPermit(WeaponState state) {
			super(state);
		}
	}
	
	private static Predicate<PlayerWeaponState> supportsDirectBulletLoad = 
			(c) -> ((Weapon)c.getItem()).getAmmoCapacity() > 0;
			
	private static Predicate<PlayerWeaponState> magazineAttached = 
			(c) -> AttachmentManager.getActiveAttachment(c.getItemStack(), AttachmentCategory.MAGAZINE) != null;
		
	private static Predicate<PlayerWeaponState> reloadAnimationCompleted = (c) -> true;
	
	private static Predicate<PlayerWeaponState> unloadAnimationCompleted = (c) -> 
		{ System.out.println("Checking if unload animation completed"); return true; };
		
	private static Predicate<PlayerItemState<WeaponState>> quietReload = (c) -> false;
	
	private ModContext modContext;

	private PermitManager permitManager;

	private StateManager<WeaponState, ? super PlayerWeaponState> stateManager;
	
	public ReloadAspect(ModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponState> stateManager) {

		if(permitManager == null) {
			throw new IllegalStateException("Permit manager not initialized");
		}
		
		this.stateManager = stateManager
		
		.in(this)
			.change(WeaponState.READY).to(WeaponState.LOAD)
			.when(supportsDirectBulletLoad.or(magazineAttached.negate()))
			.withPermit((s, c) -> new LoadPermit(s),
					(updatedState) -> modContext.getPlayerItemRegistry().update(updatedState),
					permitManager)
			.withAction((c, f, t, p) -> doPermittedLoad(c, p))
			.allowed()

		.in(this)
			.change(WeaponState.LOAD).to(WeaponState.READY)
			.when(reloadAnimationCompleted)
			.allowed()
			
		.in(this)
			.change(WeaponState.READY).to(WeaponState.UNLOAD)
			.when(magazineAttached)
			.withPermit((s, c) -> new UnloadPermit(s),
					modContext.getPlayerItemRegistry()::update,
					permitManager)
			.withAction((c, f, t, p) -> doPermittedUnload(c, p))
			.allowed()
		
		.in(this)
			.change(WeaponState.UNLOAD).to(WeaponState.READY)
			.when(unloadAnimationCompleted.or(quietReload))
			.allowed();
	}
	
	@Override
	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		permitManager.registerEvaluator(LoadPermit.class, PlayerWeaponState.class, (p, c) -> { evaluateLoad(p, c); });
		permitManager.registerEvaluator(UnloadPermit.class, PlayerWeaponState.class, (p, c) -> { evaluateUnload(p, c); });
	}
	

	void reloadMainHeldItem(EntityPlayer player) {
		PlayerWeaponState state = (PlayerWeaponState) contextForPlayer(player);
		stateManager.changeState(this, state, WeaponState.LOAD, WeaponState.UNLOAD);
	}

	void updateMainHeldItem(EntityPlayer player) {
		PlayerWeaponState state = (PlayerWeaponState) contextForPlayer(player);
		//stateManager.changeState(reloadContext, WeaponState.READY);
		stateManager.changeStateFromAnyOf(this, state, WeaponState.READY, WeaponState.UNLOAD_REQUESTED, WeaponState.UNLOAD);
	}
	
	private void evaluateLoad(LoadPermit p, PlayerItemState<WeaponState> playerItemState) {
		


		ItemStack weaponItemStack = playerItemState.getItemStack();
		
		if(weaponItemStack == null) {
			// Since reload request was sent for an item, the item was removed from the original slot
			return;
		}
		
		Weapon weapon = (Weapon) playerItemState.getItem();
		EntityPlayer player = playerItemState.getPlayer();
		if (compatibility.getTagCompound(weaponItemStack) != null && !player.isSprinting()) {
			List<ItemMagazine> compatibleMagazines = weapon.getCompatibleMagazines();
			List<ItemAttachment<Weapon>> compatibleBullets = weapon.getCompatibleAttachments(ItemBullet.class);
			ItemStack consumedStack;
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
				// Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.LOAD, newMagazine, ammo), (EntityPlayerMP) player);
				
			} else if(!compatibleBullets.isEmpty() && (consumedStack = WorldHelper.tryConsumingCompatibleItem(compatibleBullets,
					Math.min(weapon.getMaxBulletsPerReload(), weapon.getAmmoCapacity() - Tags.getAmmo(weaponItemStack)), player)) != null) {
				int ammo = Tags.getAmmo(weaponItemStack) + compatibility.getStackSize(consumedStack);
				Tags.setAmmo(weaponItemStack, ammo);
				// Update permit instead modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ammo), (EntityPlayerMP) player);
				compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
			} else if (WorldHelper.consumeInventoryItem(player.inventory, weapon.builder.ammo)) {
				Tags.setAmmo(weaponItemStack, weapon.builder.ammoCapacity);
				// Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, weapon.builder.ammoCapacity), (EntityPlayerMP) player);
				compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
			} else {
				Tags.setAmmo(weaponItemStack, 0);
				// Update permit isntead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, 0), (EntityPlayerMP) player);
			}
		} 
	
	}
	
	private void evaluateUnload(UnloadPermit p, PlayerWeaponState c) {
		System.out.println("Evaluating unload");
		try {
			Thread.sleep(4500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.setStatus(Status.GRANTED);
	}
	
	private void doPermittedLoad(PlayerWeaponState c, Permit<WeaponState> permit) {
//		storage.getCurrentAmmo().set(ammo);
//		if ((itemMagazine != null || ammo > 0) && !forceQuietReload) {
//			storage.setState(State.RELOAD_CONFIRMED);
//			long reloadingStopsAt = compatibility.world(player).getTotalWorldTime() + weapon.builder.reloadingTimeout;
//			storage.getReloadingStopsAt().set(reloadingStopsAt);
//			compatibility.playSound(player, weapon.getReloadSound(), 1.0F, 1.0F);
//		} else {
//			storage.setState(State.READY);
//		}
	}
	
	private void doPermittedUnload(PlayerItemState<WeaponState> c, Permit<WeaponState> p) {
		
//		ClientModContext.context = c; // TODO: generalize  context synchronization
//		ClientModContext.context.setPlayer(compatibility.getClientPlayer());
		System.out.println("Doing permitted unload");
		c.setPlayer(compatibility.getClientPlayer());
	}

	private PlayerItemState<WeaponState> contextForPlayer(EntityPlayer player) {
		
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
//			/*
//			WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, 
//					(Weapon) itemStack.getItem());
//			ReloadContext context = new ReloadContext(storage, player);
//			*/
//			if(ClientModContext.context == null) {
//				ClientModContext.context = new ReloadContext();
//				ClientModContext.context.setState(WeaponState.READY);
//			}
//			ClientModContext.context.itemStack = itemStack;
//			return ClientModContext.context;
			return (PlayerItemState<WeaponState>) ((ClientModContext)modContext).getPlayerItemRegistry().getMainHandItemContext(player);
		}
		return null;
	}



	

}
