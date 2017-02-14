package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.network.UniversalObject;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.Permit.Status;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;
import com.vicmatskiv.weaponlib.state.StateManager.Result;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ReloadAspect extends CommonWeaponAspect {
	
	public static class UnloadPermit extends Permit {
		
		public UnloadPermit() {}
		
		public UnloadPermit(ManagedState state) {
			super(state);
		}
	}
	
	public static class LoadPermit extends Permit {
		
		public LoadPermit() {}
		
		public LoadPermit(ManagedState state) {
			super(state);
		}
	}
	
	public static class ReloadContext extends CommonWeaponAspectContext {
		private int inventoryItemIndex;
		private Weapon weapon;
		private ItemStack itemStack;
		
		public ReloadContext() {}
		
		public ReloadContext(WeaponClientStorage storage, EntityPlayer player) {
			super(storage);
			setPlayer(player);
			itemStack = compatibility.getHeldItemMainHand(player);
			weapon = (Weapon) itemStack.getItem();
			inventoryItemIndex = compatibility.getCurrentInventoryItemIndex(player);
		}


		public Weapon getWeapon() {
			return weapon;
		}

		public ItemStack getItemStack() {
			if(this.itemStack == null) {
				ItemStack itemStack = compatibility.getInventoryItemStack(getPlayer(), inventoryItemIndex);
				this.itemStack = itemStack.getItem() == weapon ? itemStack : null;
			}
			
			return this.itemStack;
		}
		
		@Override
		public boolean init(ByteBuf buf) {
			super.init(buf);
			Item item = Item.getItemById(buf.readInt());
			if(!(item instanceof Weapon)) {
				return false;
			}
			weapon = (Weapon) item;
			inventoryItemIndex = buf.readInt();
			return true;
		}
		
		@Override
		public void serialize(ByteBuf buf) {
			super.serialize(buf);
			buf.writeInt(Item.getIdFromItem(weapon));
			buf.writeInt(inventoryItemIndex);
		}
	}
	
	private static Predicate<ReloadContext> supportsDirectBulletLoad = 
			(c) -> ((Weapon)c.getItemStack().getItem()).getAmmoCapacity() > 0;
			
	private static Predicate<ReloadContext> magazineAttached = 
			(c) -> AttachmentManager.getActiveAttachment(c.getItemStack(), AttachmentCategory.MAGAZINE) != null;
		
	private static Predicate<ReloadContext> reloadAnimationCompleted = (c) -> false;
	private static Predicate<ReloadContext> unloadAnimationCompleted = (c) -> true;
	private static Predicate<ReloadContext> quietReload = (c) -> false;
	
	private ModContext modContext;
	
	
	
	public ReloadAspect(ModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	public void setStateManager(StateManager stateManager) {
		if(permitManager == null) {
			throw new IllegalStateException("Permit manager not initialized");
		}
		super.setStateManager(stateManager);
		
		stateManager
		
		.in(ReloadContext.class).change(WeaponState.READY).to(WeaponState.LOAD)
			.when(supportsDirectBulletLoad.or(magazineAttached.negate()))
			.withPermit((s, c) -> new LoadPermit(s), permitManager)
			.withAction((c, f, t) -> doPermittedLoad(c))
			.allowed()

		.in(ReloadContext.class).change(WeaponState.LOAD).to(WeaponState.READY)
			.when(reloadAnimationCompleted)
			.allowed()
			
		.in(ReloadContext.class).change(WeaponState.READY).to(WeaponState.UNLOAD)
			.when(magazineAttached)
			.withPermit((s, c) -> new UnloadPermit(s), permitManager)
			.withAction((c, f, t) -> doPermittedUnload(c))
			.allowed()
		
		.in(ReloadContext.class).change(WeaponState.UNLOAD).to(WeaponState.READY)
			.when(unloadAnimationCompleted.or(quietReload))
			.allowed();
	}
	
	@Override
	public void setPermitManager(PermitManager<UniversalObject> permitManager) {
		this.permitManager = permitManager;
		permitManager.registerEvaluator(LoadPermit.class, (p, c) -> { evaluateLoad(p, ReloadContext.class.cast(c)); });
		permitManager.registerEvaluator(UnloadPermit.class, (p, c) -> { evaluateUnload(p, ReloadContext.class.cast(c)); });
	}
	

	void onReloadAction(ReloadContext context) {
		Result result = stateManager.changeState(context, WeaponState.LOAD, WeaponState.UNLOAD);
		//System.out.println("State changed: " + result.isStateChanged() + ", current: " + result.getState());
	}

	void onUpdate(ReloadContext context) {
		stateManager.changeState(context, WeaponState.READY);
	}
	
	private void evaluateLoad(LoadPermit p, ReloadContext reloadContext) {

		ItemStack weaponItemStack = reloadContext.getItemStack();
		
		if(weaponItemStack == null) {
			// Since reload request was sent for an item, the item was removed from the original slot
			return;
		}
		
		Weapon weapon = reloadContext.getWeapon();
		EntityPlayer player = reloadContext.getPlayer();
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
	
	private void evaluateUnload(UnloadPermit p, ReloadContext context) {
		System.out.println("Evaluating unload");
		p.setStatus(Status.GRANTED);
	}
	
	private void doPermittedLoad(ReloadContext c) {
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
	
	private void doPermittedUnload(ReloadContext c) {
		System.out.println("Doing permitted unload");
	}

	public ReloadContext contextForPlayer(EntityPlayer player) {
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
			WeaponClientStorage storage = modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, 
					(Weapon) itemStack.getItem());
			return new ReloadContext(storage, player);
		}
		return null;
	}

}
