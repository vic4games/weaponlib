package com.vicmatskiv.weaponlib.mission;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

public class MissionOfferingSyncHandler implements CompatibleMessageHandler<MissionOfferingSyncMessage, CompatibleMessage>  {

    private ModContext modContext;

    public MissionOfferingSyncHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(MissionOfferingSyncMessage message, CompatibleMessageContext ctx) {
        if(!ctx.isServerSide()){
            compatibility.runInMainClientThread(() -> {
                modContext.getMissionManager().updateOfferings(message.getMissionOfferings());
            });
        }
        return null;
    }
}
