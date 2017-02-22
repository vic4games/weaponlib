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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;


/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class FireAspect implements Aspect<WeaponState, PlayerWeaponInstance> {

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
	
	private static Predicate<PlayerWeaponInstance> readyToShootAccordingToFireRate = s -> 
		System.currentTimeMillis() - s.getLastFireTimestamp() >= 50f / s.getWeapon().builder.fireRate;
    
	private static Predicate<PlayerWeaponInstance> readyToShootAccordingToFireMode = 
			s -> s.getSeriesShotCount() < s.getWeapon().builder.maxShots;
             
	private static Predicate<PlayerWeaponInstance> hasAmmo = s -> true;
             
	private static Predicate<PlayerWeaponInstance> ejectSpentRoundRequired = s -> s.getWeapon().ejectSpentRoundRequired();
	         
	private static Predicate<PlayerWeaponInstance> ejectSpentRoundTimeoutExpired = s -> 
		System.currentTimeMillis() >= s.getWeapon().builder.pumpTimeoutMilliseconds + s.getStateUpdateTimestamp();
                          
	private static Predicate<PlayerWeaponInstance> sprinting = s -> s.getPlayer().isSprinting();
             
	private static final Set<WeaponState> allowedFireOrEjectFromStates = new HashSet<>(
			Arrays.asList(WeaponState.READY, WeaponState.PAUSED, WeaponState.EJECT_REQUIRED));
	
	private static final Set<WeaponState> allowedUpdateFromStates = new HashSet<>(
			Arrays.asList(WeaponState.EJECTING, WeaponState.PAUSED, WeaponState.FIRING, 
					WeaponState.RECOILED, WeaponState.PAUSED));
	
	private ModContext modContext;

	private StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager;

	private PermitManager permitManager;
	
	public FireAspect(CommonModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager) {
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
		.withAction(PlayerWeaponInstance::resetCurrentSeries)
		.manual() // on stop
		
		;
	}

	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
	}
	
	void onFireButtonClick(EntityPlayer player) {
		PlayerWeaponInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
		if(weaponInstance != null) {
			stateManager.changeStateFromAnyOf(this, weaponInstance, allowedFireOrEjectFromStates, WeaponState.FIRING, WeaponState.EJECTING);
		}
	}
	
	void onFireButtonRelease(EntityPlayer player) {
		//System.out.println("Releasing trigger");
		PlayerWeaponInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
		if(weaponInstance != null) {
			stateManager.changeState(this, weaponInstance, WeaponState.EJECT_REQUIRED, WeaponState.READY);
		}
	}
	
	void onUpdate(EntityPlayer player) {
		//System.out.println("Updating...");
		PlayerWeaponInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
		if(weaponInstance != null) {
			stateManager.changeStateFromAnyOf(this, weaponInstance, allowedUpdateFromStates);
		}
	}
	
	private void fire(PlayerWeaponInstance weaponInstance) {
		EntityPlayer player = weaponInstance.getPlayer();
		Weapon weapon = (Weapon) weaponInstance.getItem();
		//WeaponClientStorage storage = null; //context.getStorage();
		Random random = player.getRNG();
		
//		modContext.getChannel().getChannel().sendToServer(new TryFireMessage(true));
		ItemStack heldItem = compatibility.getHeldItemMainHand(player); // TODO: move out
//
//		modContext.runSyncTick(() -> {
//			compatibility.playSound(player, modContext.getAttachmentManager().isSilencerOn(heldItem) ? weapon.getSilencedShootSound() : weapon.getShootSound(), 1F, 1F);
//		});
		
		compatibility.playSound(player, modContext.getAttachmentManager().isSilencerOn(heldItem) ? weapon.getSilencedShootSound() : weapon.getShootSound(), 1F, 1F);

		player.rotationPitch = player.rotationPitch - weaponInstance.getRecoil();						
		float rotationYawFactor = -1.0f + random.nextFloat() * 2.0f;
		player.rotationYaw = player.rotationYaw + weaponInstance.getRecoil() * rotationYawFactor;
		
		if(weapon.builder.flashIntensity > 0) {
			EffectManager.getInstance().spawnFlashParticle(player, weapon.builder.flashIntensity,
					Weapon.isZoomed(player, heldItem) ? FLASH_X_OFFSET_ZOOMED : compatibility.getEffectOffsetX(),
							compatibility.getEffectOffsetY());
		}
		
		EffectManager.getInstance().spawnSmokeParticle(player, compatibility.getEffectOffsetX(),
				compatibility.getEffectOffsetY());
			
		
		weaponInstance.setSeriesShotCount(weaponInstance.getSeriesShotCount() + 1);
		weaponInstance.setLastFireTimestamp(System.currentTimeMillis());
	}

	private void ejectSpentRound(PlayerWeaponInstance weaponInstance) {
		EntityPlayer player = weaponInstance.getPlayer();
		ItemStack itemStack = compatibility.getHeldItemMainHand(player); // TODO: move out
		if(!Tags.isAimed(itemStack)) {
			Weapon weapon = weaponInstance.getWeapon();
			compatibility.playSound(player, weapon.getEjectSpentRoundSound(), 1F, 1F);
		}
	}
}
