package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;
import com.vicmatskiv.weaponlib.state.StateManager.VoidPostAction;

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
	
	private static Predicate<PlayerWeaponState> readyToShootAccordingToFireRate = s -> 
		System.currentTimeMillis() - s.getLastFireTimestamp() >= 50f / s.getWeapon().builder.fireRate;
    
	private static Predicate<PlayerWeaponState> readyToShootAccordingToFireMode = 
			s -> s.getSeriesShotCount() < s.getWeapon().builder.maxShots;
             
	private static Predicate<PlayerWeaponState> hasAmmo = s -> true;
             
	private static Predicate<PlayerWeaponState> ejectSpentRoundRequired = s -> s.getWeapon().ejectSpentRoundRequired();
	         
	private static Predicate<PlayerWeaponState> ejectSpentRoundTimeoutExpired = s -> 
		System.currentTimeMillis() >= s.getWeapon().builder.pumpTimeoutMilliseconds + s.getStateUpdateTimestamp();
                          
	private static Predicate<PlayerWeaponState> sprinting = s -> s.getPlayer().isSprinting();
             
	private static final Set<WeaponState> allowedFireOrEjectFromStates = new HashSet<>(
			Arrays.asList(WeaponState.READY, WeaponState.PAUSED, WeaponState.EJECT_REQUIRED));
	
	private static final Set<WeaponState> allowedUpdateFromStates = new HashSet<>(
			Arrays.asList(WeaponState.EJECTING, WeaponState.PAUSED, WeaponState.FIRING, 
					WeaponState.RECOILED, WeaponState.PAUSED));
	
	private ModContext modContext;

	private StateManager<WeaponState, ? super PlayerWeaponState> stateManager;

	private PermitManager permitManager;
	
	public FireAspect(CommonModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponState> stateManager) {
		this.stateManager = stateManager;
		
		stateManager
		
		.in(this).change(WeaponState.READY).to(WeaponState.FIRING)
		.when(hasAmmo.and(sprinting.negate()))
		.withAction(this::fire)
		.manual() // on start fire
		
		.in(this).change(WeaponState.FIRING).to(WeaponState.RECOILED)
		.automatic() // unconditional
		
		.in(this).change(WeaponState.RECOILED).to(WeaponState.PAUSED)
		.automatic() // unconditional
		
		.in(this).change(WeaponState.PAUSED).to(WeaponState.EJECT_REQUIRED)
		.when(ejectSpentRoundRequired)
		.manual() // on stop
		
		.in(this).change(WeaponState.EJECT_REQUIRED).to(WeaponState.EJECTING)
		.withAction(this::ejectSpentRound)
		.manual() // on fire ?
		
		.in(this).change(WeaponState.EJECTING).to(WeaponState.READY)
		.when(ejectSpentRoundTimeoutExpired) // TODO: enforce it only if a trigger was released
		.automatic() // on stop fire and eject animation completed
		
		.in(this).change(WeaponState.PAUSED).to(WeaponState.FIRING)
		.when(hasAmmo.and(sprinting.negate()).and(readyToShootAccordingToFireMode).and(readyToShootAccordingToFireRate))
		.withAction(this::fire)
		.manual() // on fire
		
		.in(this).change(WeaponState.PAUSED).to(WeaponState.READY)
		.when(ejectSpentRoundRequired.negate())
		.withAction(PlayerWeaponState::resetCurrentSeries)
		.manual() // on stop
		
		;
	}

	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
	}
	
	void onFireButtonClick(EntityPlayer player) {
		PlayerWeaponState extendedState = (PlayerWeaponState) contextForPlayer(player); // TODO: take care of slot changes?
		if(extendedState != null) {
			stateManager.changeStateFromAnyOf(this, extendedState, allowedFireOrEjectFromStates, WeaponState.FIRING, WeaponState.EJECTING);
		}
	}
	
	void onFireButtonRelease(EntityPlayer player) {
		//System.out.println("Releasing trigger");
		PlayerWeaponState extendedState = (PlayerWeaponState) contextForPlayer(player); // TODO: take care of slot changes?
		if(extendedState != null) {
			stateManager.changeState(this, extendedState, WeaponState.EJECT_REQUIRED, WeaponState.READY);
		}
	}
	
	void onUpdate(EntityPlayer player) {
		//System.out.println("Updating...");
		PlayerWeaponState extendedState = (PlayerWeaponState) contextForPlayer(player); // TODO: take care of slot changes?
		if(extendedState != null) {
			stateManager.changeStateFromAnyOf(this, extendedState, allowedUpdateFromStates);
		}
	}
	
	private void fire(PlayerWeaponState extendedState) {
		EntityPlayer player = extendedState.getPlayer();
		Weapon weapon = (Weapon) extendedState.getItem();
		//WeaponClientStorage storage = null; //context.getStorage();
		Random random = player.getRNG();
		
//		modContext.getChannel().getChannel().sendToServer(new TryFireMessage(true));
		ItemStack heldItem = compatibility.getHeldItemMainHand(player); // TODO: move out
//
//		modContext.runSyncTick(() -> {
//			compatibility.playSound(player, modContext.getAttachmentManager().isSilencerOn(heldItem) ? weapon.getSilencedShootSound() : weapon.getShootSound(), 1F, 1F);
//		});
		
		compatibility.playSound(player, modContext.getAttachmentManager().isSilencerOn(heldItem) ? weapon.getSilencedShootSound() : weapon.getShootSound(), 1F, 1F);

		player.rotationPitch = player.rotationPitch - extendedState.getRecoil();						
		float rotationYawFactor = -1.0f + random.nextFloat() * 2.0f;
		player.rotationYaw = player.rotationYaw + extendedState.getRecoil() * rotationYawFactor;
		
		if(weapon.builder.flashIntensity > 0) {
			EffectManager.getInstance().spawnFlashParticle(player, weapon.builder.flashIntensity,
					Weapon.isZoomed(player, heldItem) ? FLASH_X_OFFSET_ZOOMED : compatibility.getEffectOffsetX(),
							compatibility.getEffectOffsetY());
		}
		
		EffectManager.getInstance().spawnSmokeParticle(player, compatibility.getEffectOffsetX(),
				compatibility.getEffectOffsetY());
			
		
		extendedState.setSeriesShotCount(extendedState.getSeriesShotCount() + 1);
		extendedState.setLastFireTimestamp(System.currentTimeMillis());
	}

	private void ejectSpentRound(PlayerWeaponState extendedState) {
		EntityPlayer player = extendedState.getPlayer();
		ItemStack itemStack = compatibility.getHeldItemMainHand(player); // TODO: move out
		if(!Tags.isAimed(itemStack)) {
			Weapon weapon = extendedState.getWeapon();
			compatibility.playSound(player, weapon.getEjectSpentRoundSound(), 1F, 1F);
		}
	}

	private PlayerItemState<WeaponState> contextForPlayer(EntityPlayer player) {
		
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
			return (PlayerItemState<WeaponState>) modContext.getPlayerItemRegistry().getMainHandItemState(player);
		}
		return null;
	}

}
