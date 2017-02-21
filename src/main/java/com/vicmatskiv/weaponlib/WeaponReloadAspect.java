package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.Permit.Status;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class WeaponReloadAspect implements Aspect<WeaponState, PlayerWeaponState> {
	
	static {
		TypeRegistry.getInstance().register(UnloadPermit.class);
		TypeRegistry.getInstance().register(LoadPermit.class);		
		TypeRegistry.getInstance().register(PlayerWeaponState.class); // TODO: move it out
	}
	
	private static final Set<WeaponState> allowedUpdateFromStates = new HashSet<>(
			Arrays.asList(
					WeaponState.LOAD_REQUESTED,  
					WeaponState.LOAD, 
					WeaponState.UNLOAD_PREPARING, 
					WeaponState.UNLOAD_REQUESTED, 
					WeaponState.UNLOAD));
	
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

	private static long reloadAnimationDuration = 1000;
	
	private static long unloadAnimationDuration = 1000;
		
	private static Predicate<PlayerWeaponState> reloadAnimationCompleted = es -> 
		System.currentTimeMillis() >= es.getStateUpdateTimestamp() + reloadAnimationDuration; // TODO: read reload animation duration from the state itself
	
	private static Predicate<PlayerWeaponState> unloadAnimationCompleted = es -> 
		System.currentTimeMillis() >= es.getStateUpdateTimestamp() + reloadAnimationDuration;
		
	private static Predicate<PlayerItemState<WeaponState>> quietReload = c -> false;
	
	private Predicate<PlayerItemState<WeaponState>> inventoryHasFreeSlots = c -> true; // TODO implement free slot check
	
	private ModContext modContext;

	private PermitManager permitManager;

	private StateManager<WeaponState, ? super PlayerWeaponState> stateManager;


	
	public WeaponReloadAspect(ModContext modContext) {
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
			.withPermit((s, es) -> new LoadPermit(s),
					modContext.getPlayerItemRegistry()::update,
					permitManager)
			.withAction((c, f, t, p) -> doPermittedLoad(c, (LoadPermit)p))
			.manual()

		.in(this)
			.change(WeaponState.LOAD).to(WeaponState.READY)
			.when(reloadAnimationCompleted)
			.automatic()
			
		.in(this)
			.prepare((c, f, t) -> { prepareUnload(c); }, unloadAnimationDuration)
			.change(WeaponState.READY).to(WeaponState.UNLOAD)
			.when(magazineAttached.and(inventoryHasFreeSlots))
			.withPermit((s, c) -> new UnloadPermit(s),
					modContext.getPlayerItemRegistry()::update,
					permitManager)
			.withAction((c, f, t, p) -> doPermittedUnload(c, (UnloadPermit)p))
			.manual()
		
		.in(this)
			.change(WeaponState.UNLOAD).to(WeaponState.READY)
			//.when(unloadAnimationCompleted.or(quietReload))
			.automatic()
		;
	}
	
	@Override
	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		permitManager.registerEvaluator(LoadPermit.class, PlayerWeaponState.class, (p, c) -> { evaluateLoad(p, c); });
		permitManager.registerEvaluator(UnloadPermit.class, PlayerWeaponState.class, (p, c) -> { evaluateUnload(p, c); });
	}
	
	public void reloadMainHeldItem(EntityPlayer player) {
		PlayerWeaponState state = (PlayerWeaponState) contextForPlayer(player);
		stateManager.changeState(this, state, WeaponState.LOAD, WeaponState.UNLOAD);
	}

	void updateMainHeldItem(EntityPlayer player) {
		PlayerWeaponState state = (PlayerWeaponState) contextForPlayer(player);
		if(state != null) {
			//allowedUpdateFromStates
			//stateManager.changeState(this, state);
			stateManager.changeStateFromAnyOf(this, state, allowedUpdateFromStates); // no target state specified, will trigger auto-transitions
		}
		
	}
	
	private void evaluateLoad(LoadPermit p, PlayerWeaponState playerWeaponState) {
		
		ItemStack weaponItemStack = playerWeaponState.getItemStack();
		
		if(weaponItemStack == null) {
			// Since reload request was sent for an item, the item was removed from the original slot
			return;
		}
		Status status = Status.GRANTED;
		Weapon weapon = (Weapon) playerWeaponState.getItem();
		EntityPlayer player = playerWeaponState.getPlayer();
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
						System.out.println("Setting server side ammo for the weapon to " + ammo);
						modContext.getAttachmentManager().addAttachment((ItemAttachment<Weapon>) magazineItemStack.getItem(), weaponItemStack, player);
						compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
					}
				}
				// Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.LOAD, newMagazine, ammo), (EntityPlayerMP) player);
				playerWeaponState.setAmmo(ammo);
			} else if(!compatibleBullets.isEmpty() && (consumedStack = WorldHelper.tryConsumingCompatibleItem(compatibleBullets,
					Math.min(weapon.getMaxBulletsPerReload(), weapon.getAmmoCapacity() - Tags.getAmmo(weaponItemStack)), player)) != null) {
				int ammo = Tags.getAmmo(weaponItemStack) + compatibility.getStackSize(consumedStack);
				Tags.setAmmo(weaponItemStack, ammo);
				// Update permit instead modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ammo), (EntityPlayerMP) player);
				playerWeaponState.setAmmo(ammo);
				compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
			} else if (WorldHelper.consumeInventoryItem(player.inventory, weapon.builder.ammo)) {
				Tags.setAmmo(weaponItemStack, weapon.builder.ammoCapacity);
				// Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, weapon.builder.ammoCapacity), (EntityPlayerMP) player);
				playerWeaponState.setAmmo(weapon.builder.ammoCapacity);
				compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
			} else {
				System.out.println("No ammo found, denying...");
				Tags.setAmmo(weaponItemStack, 0);
				playerWeaponState.setAmmo(0);
				status = Status.DENIED;
				// Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, 0), (EntityPlayerMP) player);
			}
		} 
		
		p.setStatus(status);
	}
	
	private void prepareUnload(PlayerWeaponState playerWeaponState) {
		compatibility.playSound(playerWeaponState.getPlayer(), playerWeaponState.getWeapon().getUnloadSound(), 1.0F, 1.0F);
	}
	
	private void evaluateUnload(UnloadPermit p, PlayerWeaponState playerWeaponState) {
		System.out.println("Evaluating unload");
		ItemStack weaponItemStack = playerWeaponState.getItemStack();
		EntityPlayer player = playerWeaponState.getPlayer();
		
		Weapon weapon = (Weapon) weaponItemStack.getItem();
		if (compatibility.getTagCompound(weaponItemStack) != null && !player.isSprinting()) {
			ItemAttachment<Weapon> attachment = modContext.getAttachmentManager().removeAttachment(AttachmentCategory.MAGAZINE, weaponItemStack, player);
			if(attachment instanceof ItemMagazine) {
				ItemStack attachmentItemStack = ((ItemMagazine) attachment).createItemStack();
				Tags.setAmmo(attachmentItemStack, playerWeaponState.getAmmo());

				if(!player.inventory.addItemStackToInventory(attachmentItemStack)) {
					System.err.println("Cannot add item back to the inventory: " + attachment);
				}
			} else {
				//throw new IllegalStateException();
			}

			Tags.setAmmo(weaponItemStack, 0);
			//modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.UNLOAD, null, 0), (EntityPlayerMP) player);
			compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
			
			p.setStatus(Status.GRANTED);
		} else {
			p.setStatus(Status.DENIED);
		}

		p.setStatus(Status.GRANTED);
	}
	
	private void doPermittedLoad(PlayerWeaponState c, LoadPermit permit) {
		if(permit == null) {
			System.err.println("Permit is null, something went wrong");
			return;
		}
		if(permit.getStatus() == Status.GRANTED) {
			compatibility.playSound(c.getPlayer(), c.getWeapon().getReloadSound(), 1.0F, 1.0F);
		}
	}
	
	private void doPermittedUnload(PlayerItemState<WeaponState> c, UnloadPermit p) {
	}

	private PlayerItemState<WeaponState> contextForPlayer(EntityPlayer player) {
		
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
			return (PlayerItemState<WeaponState>) modContext.getPlayerItemRegistry().getMainHandItemState(player);
		}
		return null;
	}



	

}
