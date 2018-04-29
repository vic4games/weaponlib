package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;


/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class WeaponFireAspect implements Aspect<WeaponState, PlayerWeaponInstance> {

    private static final float FLASH_X_OFFSET_ZOOMED = 0;

    private static final long ALERT_TIMEOUT = 500;

    private static Predicate<PlayerWeaponInstance> readyToShootAccordingToFireRate = instance ->
        System.currentTimeMillis() - instance.getLastFireTimestamp() >= 50f / instance.getWeapon().builder.fireRate;

    private static Predicate<PlayerWeaponInstance> readyToShootAccordingToFireMode =
            instance -> instance.getSeriesShotCount() < instance.getMaxShots();

    private static Predicate<PlayerWeaponInstance> hasAmmo = instance -> instance.getAmmo() > 0;

    private static Predicate<PlayerWeaponInstance> ejectSpentRoundRequired = instance -> instance.getWeapon().ejectSpentRoundRequired();

    private static Predicate<PlayerWeaponInstance> ejectSpentRoundTimeoutExpired = instance ->
        System.currentTimeMillis() >= instance.getWeapon().builder.pumpTimeoutMilliseconds + instance.getStateUpdateTimestamp();

    private static Predicate<PlayerWeaponInstance> alertTimeoutExpired = instance ->
        System.currentTimeMillis() >= ALERT_TIMEOUT + instance.getStateUpdateTimestamp();


    private static Predicate<PlayerWeaponInstance> sprinting = instance -> instance.getPlayer().isSprinting();

    private static final Set<WeaponState> allowedFireOrEjectFromStates = new HashSet<>(
            Arrays.asList(WeaponState.READY, WeaponState.PAUSED, WeaponState.EJECT_REQUIRED));

    private static final Set<WeaponState> allowedUpdateFromStates = new HashSet<>(
            Arrays.asList(WeaponState.EJECTING, WeaponState.PAUSED, WeaponState.FIRING,
                    WeaponState.RECOILED, WeaponState.PAUSED, WeaponState.ALERT));

    private ModContext modContext;

    private StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager;

    public WeaponFireAspect(CommonModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public void setPermitManager(PermitManager permitManager) {}

    @Override
    public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager) {
        this.stateManager = stateManager;

        stateManager

        .in(this).change(WeaponState.READY).to(WeaponState.ALERT)
        .when(hasAmmo.negate())
        .withAction(this::cannotFire)
        .manual() // on start fire

        .in(this).change(WeaponState.ALERT).to(WeaponState.READY)
        .when(alertTimeoutExpired)
        .automatic() //

        .in(this).change(WeaponState.READY).to(WeaponState.FIRING)
        .when(hasAmmo.and(sprinting.negate()).and(readyToShootAccordingToFireRate))
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

    void onFireButtonClick(EntityPlayer player) {
        PlayerWeaponInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if(weaponInstance != null) {
           stateManager.changeStateFromAnyOf(this, weaponInstance, allowedFireOrEjectFromStates, WeaponState.FIRING, WeaponState.EJECTING, WeaponState.ALERT);
        }
    }

    void onFireButtonRelease(EntityPlayer player) {
        PlayerWeaponInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if(weaponInstance != null) {
            stateManager.changeState(this, weaponInstance, WeaponState.EJECT_REQUIRED, WeaponState.READY);
        }
    }

    void onUpdate(EntityPlayer player) {
        PlayerWeaponInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if(weaponInstance != null) {
            stateManager.changeStateFromAnyOf(this, weaponInstance, allowedUpdateFromStates);
        }
    }

    private void cannotFire(PlayerWeaponInstance weaponInstance) {
        if(weaponInstance.getAmmo() == 0) {
            String message;
            if(weaponInstance.getWeapon().getAmmoCapacity() == 0
                    && modContext.getAttachmentAspect().getActiveAttachment(weaponInstance, AttachmentCategory.MAGAZINE) == null) {
                message = compatibility.getLocalizedString("gui.noMagazine");
            } else {
                message = compatibility.getLocalizedString("gui.noAmmo");
            }
            modContext.getStatusMessageCenter().addAlertMessage(message, 3, 250, 200);
            if(weaponInstance.getPlayer() instanceof EntityPlayer) {
                compatibility.playSound((EntityPlayer)weaponInstance.getPlayer(), modContext.getNoAmmoSound(), 1F, 1F);
            }
        }
    }

    private void fire(PlayerWeaponInstance weaponInstance) {
        EntityLivingBase player = weaponInstance.getPlayer();
        Weapon weapon = (Weapon) weaponInstance.getItem();
        Random random = player.getRNG();

        modContext.getChannel().getChannel().sendToServer(new TryFireMessage(true));

        boolean silencerOn = modContext.getAttachmentAspect().isSilencerOn(weaponInstance);
        compatibility.playSound(player, silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound(),
                silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume(), 1F);

        if(weaponInstance.getAmmo() == 1 && weapon.getEndOfShootSound() != null) {
            compatibility.playSound(player, weapon.getEndOfShootSound(), 1F, 1F);
        }
        
        player.rotationPitch = player.rotationPitch - weaponInstance.getRecoil();
        float rotationYawFactor = -1.0f + random.nextFloat() * 2.0f;
        player.rotationYaw = player.rotationYaw + weaponInstance.getRecoil() * rotationYawFactor;

        Boolean muzzleFlash = modContext.getConfigurationManager().getProjectiles().isMuzzleEffects();
        if(muzzleFlash == null || muzzleFlash) {
            if(weapon.builder.flashIntensity > 0) {
                modContext.getEffectManager().spawnFlashParticle(player, weapon.builder.flashIntensity,
                        weapon.builder.flashScale.get(),
                        weaponInstance.isAimed() ? FLASH_X_OFFSET_ZOOMED : compatibility.getEffectOffsetX()
                                + weapon.builder.flashOffsetX.get(),
                                compatibility.getEffectOffsetY() + weapon.builder.flashOffsetY.get());
            }

            modContext.getEffectManager().spawnSmokeParticle(player, compatibility.getEffectOffsetX()
                    + weapon.builder.smokeOffsetX.get(),
                    compatibility.getEffectOffsetY() + weapon.builder.smokeOffsetY.get());
        }

        weaponInstance.setSeriesShotCount(weaponInstance.getSeriesShotCount() + 1);
        weaponInstance.setLastFireTimestamp(System.currentTimeMillis());
        weaponInstance.setAmmo(weaponInstance.getAmmo() - 1);
    }

    private void ejectSpentRound(PlayerWeaponInstance weaponInstance) {
        EntityLivingBase player = weaponInstance.getPlayer();
        compatibility.playSound(player, weaponInstance.getWeapon().getEjectSpentRoundSound(), 1F, 1F);
    }

    //(weapon, player) 
    public void serverFire(EntityLivingBase player, ItemStack itemStack) {
        serverFire(player, itemStack, null);
    }
    
    public void serverFire(EntityLivingBase player, ItemStack itemStack, BiFunction<Weapon, EntityLivingBase, ? extends WeaponSpawnEntity> spawnEntityWith) {
        if(!(itemStack.getItem() instanceof Weapon)) {
            return;
        }

        Weapon weapon = (Weapon) itemStack.getItem();

        if(spawnEntityWith == null) {
            spawnEntityWith = weapon.builder.spawnEntityWith;
        }
        
        for(int i = 0; i < weapon.builder.pellets; i++) {
            WeaponSpawnEntity spawnEntity = spawnEntityWith.apply(weapon, player);
            compatibility.spawnEntity(player, spawnEntity);
        }

        PlayerWeaponInstance playerWeaponInstance = Tags.getInstance(itemStack, PlayerWeaponInstance.class);

        if(weapon.isShellCasingEjectEnabled() && playerWeaponInstance != null)  {
            EntityShellCasing entityShellCasing = weapon.builder.spawnShellWith.apply(playerWeaponInstance, player);
            if(entityShellCasing != null) {
                compatibility.spawnEntity(player, entityShellCasing);
            }
        }

        boolean silencerOn = playerWeaponInstance != null && modContext.getAttachmentAspect().isSilencerOn(playerWeaponInstance);
        compatibility.playSoundToNearExcept(player,
                silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound(),
                        silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume(), 1.0F);

    }

}
