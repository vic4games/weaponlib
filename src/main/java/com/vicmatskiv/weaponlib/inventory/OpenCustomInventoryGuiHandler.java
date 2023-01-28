package com.vicmatskiv.weaponlib.inventory;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayer;

public class OpenCustomInventoryGuiHandler implements CompatibleMessageHandler<OpenCustomPlayerInventoryGuiMessage, CompatibleMessage>  {

    private ModContext modContext;

    public OpenCustomInventoryGuiHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(OpenCustomPlayerInventoryGuiMessage message, CompatibleMessageContext ctx) {
        if(ctx.isServerSide()) {
            ctx.runInMainThread(() -> {
                EntityPlayer player = ctx.getPlayer();
                player.openGui(modContext.getMod(), message.getGuiInventoryId(), 
                        compatibility.world(player), (int)player.posX, (int)player.posY, (int)player.posZ);
            });
        }
        return null;
    }
}
