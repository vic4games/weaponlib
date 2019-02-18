package com.vicmatskiv.weaponlib.inventory;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleCustomPlayerInventoryCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityInventorySyncHandler implements CompatibleMessageHandler<EntityInventorySyncMessage, CompatibleMessage>  {

    private ModContext modContext;

    public EntityInventorySyncHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(EntityInventorySyncMessage message, CompatibleMessageContext ctx) {
        if(ctx.isServerSide()) {
            ctx.runInMainThread(() -> {
            });
        } else {
            compatibility.runInMainClientThread(() -> {
                EntityPlayer player = compatibility.clientPlayer();
                Entity targetEntity = message.getEntity(compatibility.world(player));

                if(targetEntity != player || (targetEntity == player && !message.isExcludeEntity())) {
                    CustomPlayerInventory inventory = message.getInventory();
                    inventory.setContext(modContext);
                    inventory.setOwner((EntityPlayer) targetEntity);
                    CompatibleCustomPlayerInventoryCapability.setInventory((EntityLivingBase) targetEntity, inventory);
                }
            });
        }
        return null;
    }
}
