package com.vicmatskiv.weaponlib.melee;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;


/*
 * On a client side this class is used from within a separate client "ticker" thread
 */
public class MeleeAttackAspect implements Aspect<MeleeState, PlayerMeleeInstance> {
    
    private static final Logger logger = LogManager.getLogger(MeleeAttackAspect.class);

    private static final long STUB_DURATION = 250;
    
    private static final long HEAVY_STUB_DURATION = 250;
    
    private static Predicate<PlayerMeleeInstance> attackTimeoutExpired = 
            instance -> System.currentTimeMillis() >  instance.getStateUpdateTimestamp() 
                + instance.getWeapon().getPrepareStubTimeout() + STUB_DURATION;
            
    private static Predicate<PlayerMeleeInstance> heavyAttackTimeoutExpired = 
            instance -> System.currentTimeMillis() >  instance.getStateUpdateTimestamp() 
                + instance.getWeapon().getPrepareHeavyStubTimeout() + HEAVY_STUB_DURATION;

    private static Predicate<PlayerMeleeInstance> readyToStab = 
            instance -> System.currentTimeMillis()>  instance.getStateUpdateTimestamp() + instance.getWeapon().getPrepareStubTimeout();

    private static Predicate<PlayerMeleeInstance> readyToHeavyStab = 
            instance -> System.currentTimeMillis()>  instance.getStateUpdateTimestamp() + instance.getWeapon().getPrepareHeavyStubTimeout();

    private static Predicate<PlayerMeleeInstance> sprinting = instance -> instance.getPlayer().isSprinting();
             
    private static final Set<MeleeState> allowedAttackFromStates = new HashSet<>(
            Arrays.asList(MeleeState.READY));
    
    private static final Set<MeleeState> allowedUpdateFromStates = new HashSet<>(
            Arrays.asList(MeleeState.ATTACKING, MeleeState.HEAVY_ATTACKING, 
                    MeleeState.ATTACKING_STABBING, MeleeState.HEAVY_ATTACKING_STABBING));
    
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
        .manual() // on start fire
        
        .in(this).change(MeleeState.ATTACKING).to(MeleeState.ATTACKING_STABBING)
        .withAction(i -> attack(i, false))
        .when(readyToStab)
        .automatic()
        
        .in(this).change(MeleeState.ATTACKING_STABBING).to(MeleeState.READY)
        .when(attackTimeoutExpired)
        .automatic()
        
        .in(this).change(MeleeState.READY).to(MeleeState.HEAVY_ATTACKING)
        .when(sprinting.negate())
        .manual()
        
        .in(this).change(MeleeState.HEAVY_ATTACKING).to(MeleeState.HEAVY_ATTACKING_STABBING)
        .withAction(i -> attack(i, false))
        .when(readyToHeavyStab)
        .automatic()
        
        .in(this).change(MeleeState.HEAVY_ATTACKING_STABBING).to(MeleeState.READY)
        .when(heavyAttackTimeoutExpired)
        .automatic()
        ;
    }
    
    void onAttackButtonClick(EntityPlayer player) {
        PlayerMeleeInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMeleeInstance.class);
        if(weaponInstance != null) {
           stateManager.changeStateFromAnyOf(this, weaponInstance, allowedAttackFromStates, MeleeState.ATTACKING);
        }
    }
    
    void onHeavyAttackButtonClick(EntityPlayer player) {
        PlayerMeleeInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMeleeInstance.class);
        if(weaponInstance != null) {
           stateManager.changeStateFromAnyOf(this, weaponInstance, allowedAttackFromStates, MeleeState.HEAVY_ATTACKING);
        }
    }
    
    void onUpdate(EntityPlayer player) {
        PlayerMeleeInstance weaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMeleeInstance.class);
        if(weaponInstance != null) {
            stateManager.changeStateFromAnyOf(this, weaponInstance, allowedUpdateFromStates);
        }
    }
    
    private void attack(PlayerMeleeInstance meleeInstance, boolean isHeavyAttack) {
        Minecraft mc = Minecraft.getMinecraft();

        CompatibleRayTraceResult objectMouseOver = compatibility.getObjectMouseOver();
        if (objectMouseOver != null) {
            EntityPlayer player = compatibility.clientPlayer();
            World world = compatibility.world(player);
            switch (objectMouseOver.getTypeOfHit())
            {
                case ENTITY:
                    attackEntity(objectMouseOver.getEntityHit(), player, meleeInstance, isHeavyAttack);
                    break;
                case BLOCK:
                    //TODO: implement compatibility for material and click block
                    int i = mc.objectMouseOver.blockX;
                    int j = mc.objectMouseOver.blockY;
                    int k = mc.objectMouseOver.blockZ;

                    Block blockHit = compatibility.getBlockAtPosition(world, objectMouseOver);
                    
                    if (blockHit.getMaterial() != Material.air) {
                        mc.playerController.clickBlock(i, j, k, mc.objectMouseOver.sideHit);
                    }
                default:
                    break;
            }
        }
    
    }

    private void attackEntity(Entity entity, EntityPlayer player, PlayerMeleeInstance instance, boolean isHeavyAttack) {
        modContext.getChannel().getChannel().sendToServer(new TryAttackMessage(instance, entity, isHeavyAttack));
        entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 
                instance.getWeapon().getDamage(isHeavyAttack));
    }

    public void serverAttack(EntityPlayer player, PlayerMeleeInstance instance, Entity entity, boolean isHeavyAttack) {
        logger.debug("Player {} hits {} with {} in state {}", player, entity, instance, instance.getState());
        entity.attackEntityFrom(DamageSource.causePlayerDamage(player), instance.getWeapon().getDamage(isHeavyAttack));
    }
}
