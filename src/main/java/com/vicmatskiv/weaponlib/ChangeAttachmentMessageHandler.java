package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayer;

public class ChangeAttachmentMessageHandler implements CompatibleMessageHandler<ChangeAttachmentMessage, CompatibleMessage> {
	
	private WeaponAttachmentAspect attachmentAspect;

	public ChangeAttachmentMessageHandler(WeaponAttachmentAspect attachmentManager) {
		this.attachmentAspect = attachmentManager;
	}

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(ChangeAttachmentMessage message, CompatibleMessageContext ctx) {
		if(ctx.isServerSide()) {
			EntityPlayer player = ctx.getPlayer();
			ctx.runInMainThread(() -> {
				attachmentAspect.changeAttachment(message.getAttachmentCategory(), compatibility.getHeldItemMainHand(player), player);
			});
		}
		
		return null;
	}
}
