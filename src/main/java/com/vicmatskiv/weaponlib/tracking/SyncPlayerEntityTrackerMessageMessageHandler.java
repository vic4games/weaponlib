package com.vicmatskiv.weaponlib.tracking;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerEntityTrackerProvider;

public class SyncPlayerEntityTrackerMessageMessageHandler implements CompatibleMessageHandler<SyncPlayerEntityTrackerMessage, CompatibleMessage> {

    private ModContext modContext;

	public SyncPlayerEntityTrackerMessageMessageHandler(ModContext modContex) {
        this.modContext = modContex;
    }

    @Override
	public <T extends CompatibleMessage> T onCompatibleMessage(SyncPlayerEntityTrackerMessage message, CompatibleMessageContext ctx) {
		if(!ctx.isServerSide()) {
		    compatibility.runInMainClientThread(() -> {
		        CompatiblePlayerEntityTrackerProvider.setTracker(compatibility.clientPlayer(), message.getTracker());
		        if(message.getStatusMessage() != null) {
		            modContext.getStatusMessageCenter().addMessage(message.getStatusMessage(), 1000);
		        }

		    });
		}
		return null;
	}

}
