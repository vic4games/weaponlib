package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

public class SyncExtendedPlayerPropertiesMessageHandler implements CompatibleMessageHandler<SyncExtendedPlayerPropertiesMessage, CompatibleMessage> {
	
	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(SyncExtendedPlayerPropertiesMessage message, CompatibleMessageContext ctx) {
		if(!ctx.isServerSide()) {
			ExtendedPlayerProperties extendedPlayerProperties = message.getExtendedPlayerProperties();
			extendedPlayerProperties.init(compatibility.clientPlayer(), compatibility.world(compatibility.clientPlayer()));
            ExtendedPlayerProperties.set(compatibility.clientPlayer(), extendedPlayerProperties);
		}
		return null;
	}

}
