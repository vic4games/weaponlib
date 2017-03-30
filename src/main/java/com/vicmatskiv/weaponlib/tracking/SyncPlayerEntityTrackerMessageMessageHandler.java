package com.vicmatskiv.weaponlib.tracking;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerEntityTrackerProvider;

public class SyncPlayerEntityTrackerMessageMessageHandler implements CompatibleMessageHandler<SyncPlayerEntityTrackerMessage, CompatibleMessage> {
	
	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(SyncPlayerEntityTrackerMessage message, CompatibleMessageContext ctx) {
		if(!ctx.isServerSide()) {
		    ctx.runInMainThread(() -> {
		        CompatiblePlayerEntityTrackerProvider.setTracker(compatibility.clientPlayer(), message.getTracker().apply(compatibility.world(compatibility.clientPlayer())));
		    });
		}
		return null;
	}

}
