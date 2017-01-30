package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayer;

public class ChangeTextureMessageHandler implements CompatibleMessageHandler<ChangeTextureMessage, CompatibleMessage> {
	
	private AttachmentManager attachmentManager;

	ChangeTextureMessageHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(ChangeTextureMessage message, CompatibleMessageContext ctx) {
		EntityPlayer player = ctx.getPlayer();
		ctx.runInMainThread(() -> {
			attachmentManager.changeTexture(compatibility.getHeldItemMainHand(player), player);
		});
		
		return null;
	}

}
