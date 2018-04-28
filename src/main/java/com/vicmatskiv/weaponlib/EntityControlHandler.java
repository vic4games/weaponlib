package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleExtraEntityFlags;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTargetPoint;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityControlHandler implements CompatibleMessageHandler<EntityControlMessage, CompatibleMessage>  {

    private ModContext modContext;

    public EntityControlHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(EntityControlMessage message, CompatibleMessageContext ctx) {
        if(ctx.isServerSide()) {
            ctx.runInMainThread(() -> {
                EntityPlayer player = ctx.getPlayer();
                CompatibleExtraEntityFlags.setFlags(player, message.getFlags(), message.getValues());
                CompatibleTargetPoint point = new CompatibleTargetPoint(player.dimension, 
                        player.posX, player.posY, player.posZ, 200);
                int updatedFlags = CompatibleExtraEntityFlags.getFlags(player);
                if((updatedFlags & CompatibleExtraEntityFlags.PRONING) != 0) {
                    setSize(player, 0.6f, 0.6f); //player.width, player.width);
                }
                //System.out.println("Set flags to: " + updatedFlags + " for " + player);
                modContext.getChannel().sendToAllAround(new EntityControlMessage(player, updatedFlags), point);
            });
        } else {
            compatibility.runInMainClientThread(() -> {
                EntityPlayer player = compatibility.clientPlayer();
                Entity targetEntity = message.getEntity(compatibility.world(player));
                //System.out.println("Setting flags to: " + Integer.toBinaryString(message.getValues()) + " for " + targetEntity);
                CompatibleExtraEntityFlags.setFlags(targetEntity, message.getFlags(), message.getValues());
            });
        }
        return null;
    }
    
    protected void setSize(EntityPlayer entityPlayer, float width, float height)
    {
        if (width != entityPlayer.width || height != entityPlayer.height)
        {
            float f = entityPlayer.width;
            entityPlayer.width = width;
            entityPlayer.height = height;
            AxisAlignedBB axisalignedbb = entityPlayer.getEntityBoundingBox();
            entityPlayer.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entityPlayer.width, axisalignedbb.minY + (double)entityPlayer.height, axisalignedbb.minZ + (double)entityPlayer.width));

//            if (entityPlayer.width > f && !entityPlayer.firstUpdate && !entityPlayer.worldObj.isRemote)
//            {
//                this.moveEntity((double)(f - this.width), 0.0D, (double)(f - this.width));
//            }
        }
    }
}
