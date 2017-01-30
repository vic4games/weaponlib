package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class CompatibleMessageContext {
	
	private MessageContext ctx;

	public CompatibleMessageContext(MessageContext ctx) {
		this.ctx = ctx;
	}

	public boolean isServerSide() {
		return ctx.side == Side.SERVER;
	}

	public EntityPlayer getPlayer() {
		return ctx.getServerHandler().playerEntity;
	}
	
	public void runInMainThread(Runnable runnable) {
		runnable.run();
	}

}
