package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Random;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.Weapon.State;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.StateManager;
import com.vicmatskiv.weaponlib.state.StateManager.Result;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class FireAspect extends CommonWeaponAspect {

//	static final ManagedState FIRING = new ManagedState();
//	static final ManagedState STOPPED = new ManagedState();
//	static final ManagedState EJECTED_SPENT_ROUND = new ManagedState();
//	static final ManagedState EJECT_SPENT_ROUND_REQUIRED = new ManagedState();
	
	static class FireAspectContext extends CommonWeaponAspectContext {
		
		public FireAspectContext(WeaponClientStorage weaponClientStorage) {
			super(weaponClientStorage);
		}
		private EntityPlayer player;
		private Weapon weapon;
		private WeaponClientStorage storage;
		private Random random;
		
		public EntityPlayer getPlayer() {
			return player;
		}
		public Weapon getWeapon() {
			return weapon;
		}
		public WeaponClientStorage getStorage() {
			return storage;
		}
		public Random getRandom() {
			return random;
		}
	}
	
	private static final float FLASH_X_OFFSET_ZOOMED = 0;
	
	private static Predicate<FireAspectContext> readyToShootAccordingToFireRate;
    
	private static Predicate<FireAspectContext> readyToShootAccordingToFireMode;
             
	private static Predicate<FireAspectContext> hasAmmo;
             
	private static Predicate<FireAspectContext> ejectSpentRoundRequired;
	         
	private static Predicate<FireAspectContext> ejectSpentRoundTimeoutExpired;
                          
	private static Predicate<FireAspectContext> sprinting;
             
	private ModContext modContext;
	
	       
	@Override
	public void setStateManager(StateManager stateManager) {
		super.setStateManager(stateManager);
		
		stateManager
		
		.in(FireAspectContext.class).change(WeaponState.READY).to(WeaponState.FIRING)
			.when(sprinting.negate().and(readyToShootAccordingToFireRate).and(readyToShootAccordingToFireMode).and(hasAmmo))
			.allowed()
		
		.in(FireAspectContext.class).change(WeaponState.FIRING).to(WeaponState.STOPPED)
			.withAction((c, f, t) -> resetShots(c))
			.allowed()
		
		.in(FireAspectContext.class).change(WeaponState.STOPPED).to(WeaponState.EJECTED_SPENT_ROUND)
			.when(ejectSpentRoundRequired)
			.withAction((c, f, t) -> ejectSpentRound(c))
			.allowed()
		
		.in(FireAspectContext.class).change(WeaponState.STOPPED).to(WeaponState.READY)
			.when(ejectSpentRoundRequired.negate())
			.allowed()
		
		.in(FireAspectContext.class).change(WeaponState.EJECTED_SPENT_ROUND).to(WeaponState.READY)
			.when(ejectSpentRoundTimeoutExpired)
			.allowed();
	}
	
	void onFireButtonClick(FireAspectContext context) {
		Result result = stateManager.changeState(context, WeaponState.FIRING, WeaponState.EJECTED_SPENT_ROUND);
		if(result.getState() == WeaponState.FIRING) {
			fire(context);
		}
	}
	
	void onFireButtonRelease(FireAspectContext context) {
		stateManager.changeState(context, WeaponState.STOPPED);
	}
	
	void onUpdate(FireAspectContext context) {
		stateManager.changeState(context, WeaponState.READY);
	}
	
	private void fire(FireAspectContext context) {
		EntityPlayer player = context.getPlayer();
		Weapon weapon = context.getWeapon();
		WeaponClientStorage storage = context.getStorage();
		Random random = context.getRandom();
		
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

	private void ejectSpentRound(FireAspectContext context) {
		EntityPlayer player = context.getPlayer();
		ItemStack itemStack = compatibility.getHeldItemMainHand(player); // TODO: move out
		if(!Tags.isAimed(itemStack)) {
			WeaponClientStorage storage = context.getStorage();
			storage.setEjectSpentRoundStartedAt(System.currentTimeMillis());
			storage.setState(State.EJECT_SPENT_ROUND);
			modContext.runSyncTick(() -> {
				Weapon weapon = context.getWeapon();
				compatibility.playSound(player, weapon.getEjectSpentRoundSound(), 1F, 1F);
			});
		}
	}
	
	private void resetShots(FireAspectContext context) {
		WeaponClientStorage storage = context.getStorage();
		storage.resetShots();
		modContext.runInMainThread(() -> {
			modContext.getChannel().getChannel().sendToServer(new TryFireMessage(false));
		});
	}

}
