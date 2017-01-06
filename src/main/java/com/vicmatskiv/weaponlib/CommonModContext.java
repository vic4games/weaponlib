package com.vicmatskiv.weaponlib;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.MinecraftForge;

public class CommonModContext implements ModContext {
	
	protected SimpleNetworkWrapper channel;
	
	protected AttachmentManager attachmentManager;
	protected FireManager fireManager;
	protected ReloadManager reloadManager;

	@Override
	public void init(Object mod, SimpleNetworkWrapper channel) {
		this.channel = channel;
		
		this.attachmentManager = new AttachmentManager(this);
		this.fireManager = new FireManager(this);
		this.reloadManager = new ReloadManager(this);
		
		channel.registerMessage(new ReloadMessageHandler(reloadManager, (ctx) -> getServerPlayer(ctx)),
				ReloadMessage.class, 1, Side.SERVER);
		
		channel.registerMessage(new ReloadMessageHandler(reloadManager, (ctx) -> getPlayer(ctx)),
				ReloadMessage.class, 2, Side.CLIENT);
		
		channel.registerMessage(new AttachmentModeMessageHandler(attachmentManager),
				AttachmentModeMessage.class, 3, Side.SERVER);
		
		channel.registerMessage(new AttachmentModeMessageHandler(attachmentManager),
				AttachmentModeMessage.class, 4, Side.CLIENT);
		
		channel.registerMessage(new ChangeAttachmentMessageHandler(attachmentManager),
				ChangeAttachmentMessage.class, 5, Side.SERVER);
		
		channel.registerMessage(new ChangeAttachmentMessageHandler(attachmentManager),
				ChangeAttachmentMessage.class, 6, Side.CLIENT);
		
		channel.registerMessage(new ChangeTextureMessageHandler(attachmentManager),
				ChangeTextureMessage.class, 7, Side.SERVER);
		
		channel.registerMessage(new ChangeTextureMessageHandler(attachmentManager),
				ChangeTextureMessage.class, 8, Side.CLIENT);
		
		channel.registerMessage(new ChangeSettingMessageHandler((ctx) -> getPlayer(ctx)),
				ChangeSettingsMessage.class, 9, Side.CLIENT);

		channel.registerMessage(new TryFireMessageHandler(fireManager),
				TryFireMessage.class, 11, Side.SERVER);
		
		channel.registerMessage(LaserSwitchMessageHandler.class,
				LaserSwitchMessage.class, 12, Side.SERVER);
		
		channel.registerMessage(LaserSwitchMessageHandler.class,
				LaserSwitchMessage.class, 13, Side.CLIENT);
		
		MinecraftForge.EVENT_BUS.register(attachmentManager); 
		
		FMLCommonHandler.instance().bus().register(new WeaponKeyInputHandler((ctx) -> getPlayer(ctx), 
				attachmentManager, reloadManager, channel));
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

	@Override
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	@Override
	public WeaponClientStorageManager getWeaponClientStorageManager() {
		throw new IllegalStateException("Attempted to get instance of " + WeaponClientStorageManager.class.getSimpleName());
	}

	@Override
	public void registerRenderableItem(String name, Item item, IItemRenderer renderer) {
		GameRegistry.registerItem(item, name);
	}
	
	
}
