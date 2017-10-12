package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityJoinWorldEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleLivingUpdateEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleStartTrackingEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleStopTrackingEvent;
import com.vicmatskiv.weaponlib.electronics.ItemHandheld;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;
import com.vicmatskiv.weaponlib.tracking.SyncPlayerEntityTrackerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * TODO: rename to common event handler, since it's invoked on both sides
 */
public class ServerEventHandler extends CompatibleServerEventHandler {

    private static final Logger logger = LogManager.getLogger(ServerEventHandler.class);

    private ModContext modContext;
    private String modId;

    public ServerEventHandler(ModContext modContext, String modId) {
        this.modContext = modContext;
        this.modId = modId;
    }
    
    @Override
    protected void onCompatibleLivingUpdateEvent(CompatibleLivingUpdateEvent e) {
        
        if(!compatibility.world(e.getEntity()).isRemote) {
            SpreadableExposure exposure = CompatibleExposureCapability.getExposure(e.getEntity(), SpreadableExposure.class);
            if(exposure != null) {
                
                boolean stillEffective = exposure.isEffective();
                exposure.update(e.getEntity());
                if(e.getEntity() instanceof EntityPlayerMP && 
                        System.currentTimeMillis() - exposure.getLastSyncTimestamp() > 500) {
                    modContext.getChannel().getChannel().sendTo(
                            new SpreadableExposureMessage(stillEffective ? exposure : null),
                            (EntityPlayerMP) e.getEntity());
                    exposure.setLastSyncTimestamp(System.currentTimeMillis()); 
                }
                if(!stillEffective) {
                    CompatibleExposureCapability.removeExposure(e.getEntity(), SpreadableExposure.class);
                }
                
                ItemStack itemStack = compatibility.getHeldItemMainHand(e.getEntityLiving());
                if(itemStack != null && itemStack.getItem() instanceof ItemHandheld) {
                    compatibility.ensureTagCompound(itemStack);
                    NBTTagCompound nbt = compatibility.getTagCompound(itemStack);
                    nbt.setFloat("dose", exposure.getLastDose());
                }
            }
        }
    }

    @Override
    protected void onCompatibleItemToss(ItemTossEvent itemTossEvent) {}

    @Override
    protected void onCompatibleEntityJoinWorld(CompatibleEntityJoinWorldEvent e) {
        if(e.getEntity() instanceof Contextual) {
            ((Contextual)e.getEntity()).setContext(modContext);
        }
        if(e.getEntity() instanceof EntityPlayerMP && !e.getWorld().isRemote) {
            logger.debug("Player {} joined the world", e.getEntity());
            PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.getEntity());
            if(tracker != null) {
                modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                        (EntityPlayerMP)e.getEntity());
            }
        }
    }

    @Override
    protected void onCompatiblePlayerStartedTracking(CompatibleStartTrackingEvent e) {
        if(e.getTarget() instanceof EntityProjectile || e.getTarget() instanceof EntityBounceable) {
            return;
        }
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.getEntity());
        if (tracker != null && tracker.updateTrackableEntity(e.getTarget())) {
            logger.debug("Player {} started tracking {} with uuid {}", e.getPlayer(), e.getTarget(), e.getTarget().getUniqueID());
            modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                    (EntityPlayerMP)e.getPlayer());
        }
    }

    @Override
    protected void onCompatiblePlayerStoppedTracking(CompatibleStopTrackingEvent e) {
        if(e.getTarget() instanceof EntityProjectile || e.getTarget() instanceof EntityBounceable) {
            return;
        }
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.getEntity());
        if (tracker != null && tracker.updateTrackableEntity(e.getTarget())) {
            logger.debug("Player {} stopped tracking {}", e.getPlayer(), e.getTarget());
            modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                    (EntityPlayerMP)e.getPlayer());
        }
    }

    @Override
    protected void onCompatibleLivingDeathEvent(LivingDeathEvent e) {

    }

    @Override
    public String getModId() {
        return modId;
    }


}
