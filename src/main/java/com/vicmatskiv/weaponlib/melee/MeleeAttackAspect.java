package com.vicmatskiv.weaponlib.melee;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;


/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class MeleeAttackAspect implements Aspect<MeleeState, PlayerMeleeInstance> {

    private static final long ALERT_TIMEOUT = 500;
    
    private static Predicate<PlayerMeleeInstance> attackTimeoutExpired = 
            instance -> System.currentTimeMillis()>  instance.getStateUpdateTimestamp() + ALERT_TIMEOUT;
        
    private static Predicate<PlayerMeleeInstance> sprinting = instance -> instance.getPlayer().isSprinting();
             
    private static final Set<MeleeState> allowedAttackFromStates = new HashSet<>(
            Arrays.asList(MeleeState.READY));
    
    private static final Set<MeleeState> allowedUpdateFromStates = new HashSet<>(
            Arrays.asList(MeleeState.ATTACKING));
    
    private ModContext modContext;

    private StateManager<MeleeState, ? super PlayerMeleeInstance> stateManager;
    
    public MeleeAttackAspect(CommonModContext modContext) {
        this.modContext = modContext;
    }
    

    @Override
    public void setPermitManager(PermitManager permitManager) {}

    @Override
    public void setStateManager(StateManager<MeleeState, ? super PlayerMeleeInstance> stateManager) {
        this.stateManager = stateManager;
        
        stateManager
        
        .in(this).change(MeleeState.READY).to(MeleeState.ATTACKING)
        .when(sprinting.negate())
        .withAction(this::attack)
        .manual() // on start fire
        
        .in(this).change(MeleeState.ATTACKING).to(MeleeState.READY)
        .when(attackTimeoutExpired)
        .automatic()
        ;
    }
    
    void onAttackButtonClick(EntityPlayer player) {
        PlayerMeleeInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMeleeInstance.class);
        if(weaponInstance != null) {
           stateManager.changeStateFromAnyOf(this, weaponInstance, allowedAttackFromStates, MeleeState.ATTACKING);
        }
    }
    
    void onUpdate(EntityPlayer player) {
        PlayerMeleeInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMeleeInstance.class);
        if(weaponInstance != null) {
            stateManager.changeStateFromAnyOf(this, weaponInstance, allowedUpdateFromStates);
        }
    }
    
    private void attack(PlayerMeleeInstance meleeInstance) {
        EntityPlayer player = meleeInstance.getPlayer();
        ItemMelee weapon = meleeInstance.getWeapon();
    }
}
