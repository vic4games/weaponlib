package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Random;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.StateManager;
import com.vicmatskiv.weaponlib.state.StateManager.Result;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class FireAspect extends CommonWeaponAspect {

	static final ManagedState FIRING = new ManagedState();
	static final ManagedState STOPPED = new ManagedState();
	static final ManagedState EJECTED_SPENT_ROUND = new ManagedState();
	static final ManagedState EJECT_SPENT_ROUND_REQUIRED = new ManagedState();
	
	static class FireAspectContext extends CommonWeaponAspectContext {
		private EntityPlayer player;
		private Weapon weapon;
		private Random random;
		private WeaponClientStorage storage;
		public EntityPlayer getPlayer() {
			return player;
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
		
		.in(FireAspectContext.class).change(READY).to(FIRING)
			.when(sprinting.negate().and(readyToShootAccordingToFireRate).and(readyToShootAccordingToFireMode).and(hasAmmo))
			.allowed()
		
		.in(FireAspectContext.class).change(FIRING).to(STOPPED)
			.withAction((c, f, t) -> resetShots(c))
			.allowed()
		
		.in(FireAspectContext.class).change(STOPPED).to(EJECTED_SPENT_ROUND)
			.when(ejectSpentRoundRequired)
			.withAction((c, f, t) -> ejectSpentRound(c))
			.allowed()
		
		.in(FireAspectContext.class).change(STOPPED).to(READY)
			.when(ejectSpentRoundRequired.negate())
			.allowed()
		
		.in(FireAspectContext.class).change(EJECTED_SPENT_ROUND).to(READY)
			.when(ejectSpentRoundTimeoutExpired)
			.allowed();
	}
	
	void onFireButtonClick(FireAspectContext context) {
		Result result = stateManager.changeState(context, FIRING, EJECTED_SPENT_ROUND);
		if(result.getState() == FIRING) {
			fire(context);
		}
	}
	
	void onFireButtonRelease(FireAspectContext context) {
		stateManager.changeState(context, STOPPED);
	}
	
	void onUpdate(FireAspectContext context) {
		stateManager.changeState(context, READY);
	}
	

	private void fire(FireAspectContext context) {
		EntityPlayer player = context.getPlayer();
		modContext.getChannel().getChannel().sendToServer(new TryFireMessage(true));
		ItemStack heldItem = compatibility.getHeldItemMainHand(player);

//		modContext.runSyncTick(() -> {
//			compatibility.playSound(player, modContext.getAttachmentManager().isSilencerOn(heldItem) ? weapon.getSilencedShootSound() : weapon.getShootSound(), 1F, 1F);
//		});
//
//		
//		player.rotationPitch = player.rotationPitch - storage.getRecoil();						
//		float rotationYawFactor = -1.0f + random.nextFloat() * 2.0f;
//		player.rotationYaw = player.rotationYaw + storage.getRecoil() * rotationYawFactor;
//		
//		if(weapon.builder.flashIntensity > 0) {
//			EffectManager.getInstance().spawnFlashParticle(player, weapon.builder.flashIntensity,
//					Weapon.isZoomed(player, heldItem) ? FLASH_X_OFFSET_ZOOMED : compatibility.getEffectOffsetX(),
//							compatibility.getEffectOffsetY());
//		}
//		
//		EffectManager.getInstance().spawnSmokeParticle(player, compatibility.getEffectOffsetX(),
//				compatibility.getEffectOffsetY());
//		
//		storage.setLastShotFiredAt(System.currentTimeMillis());
//		
//		storage.addShot();
	}

	private void ejectSpentRound(FireAspectContext context) {
	}
	
	private void resetShots(FireAspectContext context) {
	}

}
