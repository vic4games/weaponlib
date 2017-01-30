package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayer;

public class AttachmentModeMessageHandler implements CompatibleMessageHandler<AttachmentModeMessage, CompatibleMessage> {
	
	private AttachmentManager attachmentManager;

	public AttachmentModeMessageHandler(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(AttachmentModeMessage message, CompatibleMessageContext ctx) {
		if(ctx.isServerSide()) {
			EntityPlayer player = ctx.getPlayer();
			ctx.runInMainThread(() -> {
				attachmentManager.toggleServerAttachmentSelectionMode(compatibility.getHeldItemMainHand(player), player);
			});
		}
		
		return null;
	}
}
