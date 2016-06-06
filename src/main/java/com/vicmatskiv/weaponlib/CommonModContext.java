package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.IItemRenderer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;


public class CommonModContext implements ModContext {
	
	protected SimpleNetworkWrapper channel;

	@Override
	public void init(Object mod, SimpleNetworkWrapper channel) {
		this.channel = channel;
		
		channel.registerMessage(new ReloadMessageHandler((ctx) -> getServerPlayer(ctx)),
				ReloadMessage.class, 1, Side.SERVER);
		
		channel.registerMessage(new ReloadMessageHandler((ctx) -> getPlayer(ctx)),
				ReloadMessage.class, 2, Side.CLIENT);
		
		channel.registerMessage(AttachmentModeMessageHandler.class,
				AttachmentModeMessage.class, 3, Side.SERVER);
		
		channel.registerMessage(AttachmentModeMessageHandler.class,
				AttachmentModeMessage.class, 4, Side.CLIENT);
		
		channel.registerMessage(ChangeAttachmentMessageHandler.class,
				ChangeAttachmentMessage.class, 5, Side.SERVER);
		
		channel.registerMessage(ChangeAttachmentMessageHandler.class,
				ChangeAttachmentMessage.class, 6, Side.CLIENT);
		
		channel.registerMessage(ChangeTextureMessageHandler.class,
				ChangeTextureMessage.class, 7, Side.SERVER);
		
		channel.registerMessage(ChangeTextureMessageHandler.class,
				ChangeTextureMessage.class, 8, Side.CLIENT);
		
		channel.registerMessage(new ChangeSettingMessageHandler((ctx) -> getPlayer(ctx)),
				ChangeSettingsMessage.class, 9, Side.CLIENT);

		channel.registerMessage(TryFireMessageHandler.class,
				TryFireMessage.class, 11, Side.SERVER);
		
		channel.registerMessage(LaserSwitchMessageHandler.class,
				LaserSwitchMessage.class, 12, Side.SERVER);
		
		channel.registerMessage(LaserSwitchMessageHandler.class,
				LaserSwitchMessage.class, 13, Side.CLIENT);
		
		FMLCommonHandler.instance().bus().register(new WeaponKeyInputHandler((ctx) -> getPlayer(ctx), 
				channel));
	}

	@Override
	public void registerWeapon(String name, Weapon weapon, IItemRenderer renderer) {
		GameRegistry.registerItem(weapon, name);
	}
	
	private EntityPlayer getServerPlayer(MessageContext ctx) {
		return ctx != null ? ctx.getServerHandler().playerEntity : null;
	}
	
	protected EntityPlayer getPlayer(MessageContext ctx) {
		return getServerPlayer(ctx);
	}

	@Override
	public SimpleNetworkWrapper getChannel() {
		return channel;
	}

	@Override
	public void runSyncTick(Runnable runnable) {
		throw new UnsupportedOperationException();
	}
	
	
}
