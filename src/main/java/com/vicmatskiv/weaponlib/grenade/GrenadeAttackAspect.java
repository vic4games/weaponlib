package com.vicmatskiv.weaponlib.grenade;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;


/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class GrenadeAttackAspect implements Aspect<GrenadeState, PlayerGrenadeInstance> {

    private static final Logger logger = LogManager.getLogger(GrenadeAttackAspect.class);

    @SuppressWarnings("unused")
    private static final long ALERT_TIMEOUT = 300;

    private Predicate<PlayerGrenadeInstance> hasSafetyPin = instance -> instance.getWeapon().hasSafetyPin();

    private static Predicate<PlayerGrenadeInstance> reequipTimeoutExpired =
            instance -> System.currentTimeMillis() >  instance.getStateUpdateTimestamp()
                + instance.getWeapon().getReequipTimeout();

    private static Predicate<PlayerGrenadeInstance> takingOffSafetyPinCompleted = instance ->
            System.currentTimeMillis() >= instance.getStateUpdateTimestamp()
                + instance.getWeapon().getTotalTakeSafetyPinOffDuration() * 1.1;

    private static Predicate<PlayerGrenadeInstance> throwingCompleted = instance ->
            System.currentTimeMillis() >= instance.getStateUpdateTimestamp()
                + instance.getWeapon().getTotalThrowingDuration() * 1.1;

    private static Predicate<PlayerGrenadeInstance> explosionTimeoutExpired = instance ->
            System.currentTimeMillis() >= instance.getStateUpdateTimestamp()
                + instance.getWeapon().getExplosionTimeout();

    private static final Set<GrenadeState> allowedAttackFromStates = new HashSet<>(
            Arrays.asList(GrenadeState.READY, GrenadeState.SAFETY_PIN_OFF));

    private static final Set<GrenadeState> allowedUpdateFromStates = new HashSet<>(
            Arrays.asList(GrenadeState.TAKING_SAFETY_PING_OFF, GrenadeState.SAFETY_PIN_OFF, GrenadeState.THROWING, GrenadeState.THROWN));

    private static final int SAFETY_IN_ALERT_TIMEOUT = 1000;

    private ModContext modContext;

    private StateManager<GrenadeState, ? super PlayerGrenadeInstance> stateManager;

    public GrenadeAttackAspect(CommonModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public void setPermitManager(PermitManager permitManager) {}

    @Override
    public void setStateManager(StateManager<GrenadeState, ? super PlayerGrenadeInstance> stateManager) {
        this.stateManager = stateManager;

        stateManager

        .in(this)
        .change(GrenadeState.READY).to(GrenadeState.TAKING_SAFETY_PING_OFF)
        .withAction(i -> takingSafetyPinOff(i))
        .when(hasSafetyPin)
        .manual()

        .in(this).change(GrenadeState.TAKING_SAFETY_PING_OFF).to(GrenadeState.SAFETY_PIN_OFF)
        .withAction(i -> takeSafetyPinOff(i))
        .when(takingOffSafetyPinCompleted)
        .automatic()

        .in(this).change(GrenadeState.SAFETY_PIN_OFF).to(GrenadeState.EXPLODED_IN_HANDS)
        .withAction(i -> explode(i))
        .when(explosionTimeoutExpired)
        .automatic()

        .in(this).change(GrenadeState.READY).to(GrenadeState.THROWING)
        .when(hasSafetyPin.negate())
        .manual()

        .in(this).change(GrenadeState.THROWING).to(GrenadeState.THROWN)
        .withAction(i -> throwIt(i))
        .when(throwingCompleted)
        .automatic()

        .in(this).change(GrenadeState.SAFETY_PIN_OFF).to(GrenadeState.THROWING)
        .manual()

        .in(this).change(GrenadeState.THROWN).to(GrenadeState.READY)
        .withAction(i -> reequip(i))
        .when(reequipTimeoutExpired)
        .automatic()
        ;
    }

    private void explode(PlayerGrenadeInstance instance) {
        logger.debug("Exploding!");
        modContext.getChannel().getChannel().sendToServer(new GrenadeMessage(instance, 0));
    }

    private void throwIt(PlayerGrenadeInstance instance) {
        logger.debug("Throwing with state " + instance.getState());
        long activationTimestamp = instance.getWeapon().getExplosionTimeout() > 0 ? instance.getActivationTimestamp() : ItemGrenade.EXPLODE_ON_IMPACT;
        modContext.getChannel().getChannel().sendToServer(new GrenadeMessage(instance, activationTimestamp));
    }

    private void reequip(PlayerGrenadeInstance instance) {
        logger.debug("Reequipping");
    }

    private void takingSafetyPinOff(PlayerGrenadeInstance instance) {
        //compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getUnloadSound(), 1.0F, 1.0F);
        logger.debug("Taking safety pin off");
    }

    private void takeSafetyPinOff(PlayerGrenadeInstance instance) {
        logger.debug("Safety pin is off");
        instance.setActivationTimestamp(System.currentTimeMillis());
    }

    void onAttackButtonClick(EntityPlayer player) {
        PlayerGrenadeInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerGrenadeInstance.class);
        if(weaponInstance != null) {
           stateManager.changeStateFromAnyOf(this, weaponInstance, allowedAttackFromStates,
                   GrenadeState.TAKING_SAFETY_PING_OFF, GrenadeState.THROWING);
        }
    }

    void onUpdate(EntityPlayer player) {
        PlayerGrenadeInstance grenadeInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerGrenadeInstance.class);
        if(grenadeInstance != null) {
            if(grenadeInstance.getState() == GrenadeState.SAFETY_PIN_OFF && System.currentTimeMillis()
                    > grenadeInstance.getLastSafetyPinAlertTimestamp() + SAFETY_IN_ALERT_TIMEOUT) {
                long remainingTimeUntilExplosion = grenadeInstance.getWeapon().getExplosionTimeout() - (
                                System.currentTimeMillis() - grenadeInstance.getActivationTimestamp());

                if(remainingTimeUntilExplosion < 0) {
                    remainingTimeUntilExplosion = 0;
                }
                modContext.getStatusMessageCenter().addAlertMessage(
                        "Explodes in " + Math.round(remainingTimeUntilExplosion / 1000f) + " sec",
                        1, 1000, 0);
                grenadeInstance.setLastSafetyPinAlertTimestamp(System.currentTimeMillis());
            }
            stateManager.changeStateFromAnyOf(this, grenadeInstance, allowedUpdateFromStates);
        }
    }

    public void serverThrowGrenade(EntityPlayer player, PlayerGrenadeInstance instance, long activationTimestamp) {
        logger.debug("Throwing grenade");
        if(activationTimestamp == 0) {
            // explode immediately
            compatibility.world(player).createExplosion(player, player.posX, player.posY, player.posZ,
                    instance.getWeapon().getExplosionStrength(), true);

        } else {
            EntityGrenade entityGrenade = new EntityGrenade.Builder()
                    .withThrower(player)
                    .withActivationTimestamp(activationTimestamp)
                    .withExplosionStrength(instance.getWeapon().getExplosionStrength())
                    .withExplosionTimeout(instance.getWeapon().getExplosionTimeout())
                    .build(modContext);
            compatibility.spawnEntity(player, entityGrenade);
        }

        //player.inventory.mainInventory[instance.getItemInventoryIndex()] = null;

        compatibility.consumeInventoryItemFromSlot(player, instance.getItemInventoryIndex());
    }

    int getParticleCount(float damage) {
        return (int) (-0.11 * (damage - 30) * (damage - 30) + 100);
    }
}
