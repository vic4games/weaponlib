package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleCustomPlayerInventoryCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityJoinWorldEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExtraEntityFlags;
import com.vicmatskiv.weaponlib.compatibility.CompatibleLivingDeathEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleLivingHurtEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleLivingUpdateEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleServerEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleStartTrackingEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleStopTrackingEvent;
import com.vicmatskiv.weaponlib.electronics.ItemHandheld;
import com.vicmatskiv.weaponlib.inventory.CustomPlayerInventory;
import com.vicmatskiv.weaponlib.inventory.EntityInventorySyncMessage;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;
import com.vicmatskiv.weaponlib.tracking.SyncPlayerEntityTrackerMessage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.item.ItemTossEvent;

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
    public ModContext getModContext() {
        return modContext;
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
            EntityPlayer player = (EntityPlayer) e.getEntity();
            modContext.getChannel().getChannel().sendTo(
                    new EntityControlMessage(player, CompatibleExtraEntityFlags.getFlags(player)),
                    (EntityPlayerMP)e.getEntity());
                        
            modContext.getChannel().getChannel().sendToAll(
                    new EntityInventorySyncMessage(e.getEntity(), 
                            CompatibleCustomPlayerInventoryCapability.getInventory(player), false));
        }
    }

    @Override
    protected void onCompatiblePlayerStartedTracking(CompatibleStartTrackingEvent e) {
        if(e.getTarget() instanceof EntityPlayer && !compatibility.world(e.getTarget()).isRemote) {
            modContext.getChannel().getChannel().sendTo(
                    new EntityInventorySyncMessage(e.getTarget(), 
                            CompatibleCustomPlayerInventoryCapability.getInventory((EntityLivingBase) e.getTarget()), false), 
                            (EntityPlayerMP) e.getPlayer());
            System.out.println("Player " + e.getPlayer() + " started tracking "  + e.getTarget());
            return;
        }
        if(e.getTarget() instanceof EntityProjectile || e.getTarget() instanceof EntityBounceable) {
            return;
        }
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker((EntityPlayer) e.getEntity());
        if (tracker != null && tracker.updateTrackableEntity(e.getTarget())) {
            logger.debug("Player {} started tracking {} with uuid {}", e.getPlayer(), e.getTarget(), e.getTarget().getUniqueID());
            modContext.getChannel().getChannel().sendTo(new SyncPlayerEntityTrackerMessage(tracker),
                    (EntityPlayerMP)e.getPlayer());
            
            EntityPlayer player = (EntityPlayer) e.getEntity();
            modContext.getChannel().getChannel().sendTo(
                    new EntityControlMessage(player, CompatibleExtraEntityFlags.getFlags(player)),
                    (EntityPlayerMP)e.getEntity());
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
            
            EntityPlayer player = (EntityPlayer) e.getEntity();
            modContext.getChannel().getChannel().sendTo(
                    new EntityControlMessage(player, CompatibleExtraEntityFlags.getFlags(player)),
                    (EntityPlayerMP)e.getEntity());
        }
    }

    @Override
    protected void onCompatibleLivingDeathEvent(CompatibleLivingDeathEvent event) {

        final EntityLivingBase entity = event.getEntity();
        if(entity instanceof EntityPlayer && !compatibility.world(entity).isRemote) {
            CustomPlayerInventory inventory = CompatibleCustomPlayerInventoryCapability.getInventory(entity);
         
            for(int slotIndex = 0; slotIndex < inventory.getSizeInventory(); slotIndex++) {
                ItemStack stackInSlot = inventory.getStackInSlot(slotIndex);
                if(stackInSlot != null) {
                    compatibility.dropItem((EntityPlayer)entity, stackInSlot, true, false);
                    inventory.setInventorySlotContents(slotIndex, null);
                }
            }
        }
    }

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    protected void onCompatibleLivingHurtEvent(CompatibleLivingHurtEvent e) {
        CustomPlayerInventory inventory = CompatibleCustomPlayerInventoryCapability
                .getInventory(e.getEntityLiving());
        if (inventory != null && inventory.getStackInSlot(1) != null) {
            compatibility.applyArmor(e, e.getEntityLiving(),
                    new ItemStack[] { inventory.getStackInSlot(1) }, e.getDamageSource(), e.getAmount());
        }
    }
}
