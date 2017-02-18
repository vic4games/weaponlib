package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Random;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;
import com.vicmatskiv.weaponlib.state.StateManager.Result;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class FireAspect implements Aspect<WeaponState, PlayerWeaponState> {

//	static final ManagedState FIRING = new ManagedState();
//	static final ManagedState STOPPED = new ManagedState();
//	static final ManagedState EJECTED_SPENT_ROUND = new ManagedState();
//	static final ManagedState EJECT_SPENT_ROUND_REQUIRED = new ManagedState();
	
//	static class FireAspectContext extends PlayerItemState<WeaponState> {
//		
//		public FireAspectContext(WeaponClientStorage weaponClientStorage) {
//			
//		}
//		private EntityPlayer player;
//		private Weapon weapon;
//		private WeaponClientStorage storage;
//		private Random random;
//		
//		public EntityPlayer getPlayer() {
//			return player;
//		}
//		public Weapon getWeapon() {
//			return weapon;
//		}
//		public WeaponClientStorage getStorage() {
//			return storage;
//		}
//		public Random getRandom() {
//			return random;
//		}
//	}
	
	private static final float FLASH_X_OFFSET_ZOOMED = 0;
	
	private static Predicate<PlayerWeaponState> readyToShootAccordingToFireRate;
    
	private static Predicate<PlayerWeaponState> readyToShootAccordingToFireMode;
             
	private static Predicate<PlayerWeaponState> hasAmmo;
             
	private static Predicate<PlayerWeaponState> ejectSpentRoundRequired;
	         
	private static Predicate<PlayerWeaponState> ejectSpentRoundTimeoutExpired;
                          
	private static Predicate<PlayerWeaponState> sprinting;
             
	private ModContext modContext;

	private StateManager<WeaponState, ? super PlayerWeaponState> stateManager;

	private PermitManager permitManager;
	
	@Override
	public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponState> stateManager) {
		this.stateManager = stateManager;
		
		stateManager
		
		.in(this).change(WeaponState.READY).to(WeaponState.FIRING)
			.when(sprinting.negate().and(readyToShootAccordingToFireRate).and(readyToShootAccordingToFireMode).and(hasAmmo))
			.allowed()
		
		.in(this).change(WeaponState.FIRING).to(WeaponState.STOPPED)
			.withAction((c, f, t, p) -> resetShots(c))
			.allowed()
		
		.in(this).change(WeaponState.STOPPED).to(WeaponState.EJECTED_SPENT_ROUND)
			.when(ejectSpentRoundRequired)
			.withAction((c, f, t, p) -> ejectSpentRound(c))
			.allowed()
		
		.in(this).change(WeaponState.STOPPED).to(WeaponState.READY)
			.when(ejectSpentRoundRequired.negate())
			.allowed()
		
		.in(this).change(WeaponState.EJECTED_SPENT_ROUND).to(WeaponState.READY)
			.when(ejectSpentRoundTimeoutExpired)
			.allowed();
	}
	
	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
	}
	
	void onFireButtonClick(PlayerWeaponState context) {
		Result result = stateManager.changeState(this, context, WeaponState.FIRING, WeaponState.EJECTED_SPENT_ROUND);
		if(result.getState() == WeaponState.FIRING) {
			fire(context);
		}
	}
	
	void onFireButtonRelease(PlayerWeaponState context) {
		stateManager.changeState(this, context, WeaponState.STOPPED);
	}
	
	void onUpdate(PlayerWeaponState context) {
		stateManager.changeState(this, context, WeaponState.READY);
	}
	
	private void fire(PlayerItemState<WeaponState> context) {
		EntityPlayer player = context.getPlayer();
		Weapon weapon = (Weapon) context.getItem();
		WeaponClientStorage storage = null; //context.getStorage();
		Random random = player.getRNG();
		
		modContext.getChannel().getChannel().sendToServer(new TryFireMessage(true));
		ItemStack heldItem = compatibility.getHeldItemMainHand(player); // TODO: move out

		modContext.runSyncTick(() -> {
			compatibility.playSound(player, modContext.getAttachmentManager().isSilencerOn(heldItem) ? weapon.getSilencedShootSound() : weapon.getShootSound(), 1F, 1F);
		});

		
		player.rotationPitch = player.rotationPitch - storage.getRecoil();						
		float rotationYawFactor = -1.0f + random.nextFloat() * 2.0f;
		player.rotationYaw = player.rotationYaw + storage.getRecoil() * rotationYawFactor;
		
		if(weapon.builder.flashIntensity > 0) {
			EffectManager.getInstance().spawnFlashParticle(player, weapon.builder.flashIntensity,
					Weapon.isZoomed(player, heldItem) ? FLASH_X_OFFSET_ZOOMED : compatibility.getEffectOffsetX(),
							compatibility.getEffectOffsetY());
		}
		
		EffectManager.getInstance().spawnSmokeParticle(player, compatibility.getEffectOffsetX(),
				compatibility.getEffectOffsetY());
				
		storage.addShot();
	}

	private void ejectSpentRound(PlayerItemState<WeaponState> context) {
		EntityPlayer player = context.getPlayer();
		ItemStack itemStack = compatibility.getHeldItemMainHand(player); // TODO: move out
		if(!Tags.isAimed(itemStack)) {
//			WeaponClientStorage storage = context.getStorage();
//			storage.setEjectSpentRoundStartedAt(System.currentTimeMillis());
//			storage.setState(State.EJECT_SPENT_ROUND);
//			modContext.runSyncTick(() -> {
//				Weapon weapon = context.getWeapon();
//				compatibility.playSound(player, weapon.getEjectSpentRoundSound(), 1F, 1F);
//			});
		}
	}
	
	private void resetShots(PlayerItemState<WeaponState> context) {
//		WeaponClientStorage storage = context.getStorage();
//		storage.resetShots();
//		modContext.runInMainThread(() -> {
//			modContext.getChannel().getChannel().sendToServer(new TryFireMessage(false));
//		});
	}

	

}
