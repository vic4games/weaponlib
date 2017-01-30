package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public interface CompatibleMessageHandler<Request extends CompatibleMessage, Response extends CompatibleMessage> extends IMessageHandler<Request, Response> {
	
	@Override
	public default Response onMessage(Request message, MessageContext ctx) {
		return onCompatibleMessage(message, new CompatibleMessageContext(ctx));
	}

	public <T extends CompatibleMessage> T onCompatibleMessage(Request compatibleMessage, CompatibleMessageContext ctx);
}
