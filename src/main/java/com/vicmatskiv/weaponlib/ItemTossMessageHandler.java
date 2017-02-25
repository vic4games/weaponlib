package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

public class ItemTossMessageHandler implements CompatibleMessageHandler<ItemTossMessage, CompatibleMessage> {

	private ModContext modContext;

	public ItemTossMessageHandler(ModContext modContext) {
		this.modContext = modContext;
	}
	
	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(ItemTossMessage message, CompatibleMessageContext ctx) {
		onClientMessage(message, ctx);
		return null;
	}

	private void onClientMessage(ItemTossMessage message, CompatibleMessageContext ctx) {
		modContext.getPlayerItemInstanceRegistry().removeItemInstance(compatibility.clientPlayer(), message.getSlot());
	}

}