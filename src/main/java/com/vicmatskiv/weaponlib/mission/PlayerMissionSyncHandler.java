package com.vicmatskiv.weaponlib.mission;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMissionCapability;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerMissionSyncHandler implements CompatibleMessageHandler<PlayerMissionSyncMessage, CompatibleMessage>  {

    @SuppressWarnings("unused")
    private ModContext modContext;

    public PlayerMissionSyncHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(PlayerMissionSyncMessage message, CompatibleMessageContext ctx) {
        if(!ctx.isServerSide()){
            compatibility.runInMainClientThread(() -> {
                EntityPlayer player = compatibility.clientPlayer();
                CompatibleMissionCapability.updateMissions(player, message.getMissions());
            });
        }
        return null;
    }
}
