package com.vicmatskiv.weaponlib;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonModContext implements ModContext {
	
	protected SimpleNetworkWrapper channel;
	
	protected AttachmentManager attachmentManager;
	protected FireManager fireManager;
	protected ReloadManager reloadManager;
	private String modId;

	@Override
	public void init(Object mod, String modId, SimpleNetworkWrapper channel) {
		this.channel = channel;
		this.modId = modId;
		
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
		
		channel.registerMessage(new ChangeSettingMessageHandler((ctx) -> getPlayer(ctx)),
				ChangeSettingsMessage.class, 10, Side.SERVER);

		channel.registerMessage(new TryFireMessageHandler(fireManager),
				TryFireMessage.class, 11, Side.SERVER);
		
		channel.registerMessage(LaserSwitchMessageHandler.class,
				LaserSwitchMessage.class, 12, Side.SERVER);
		
		channel.registerMessage(LaserSwitchMessageHandler.class,
				LaserSwitchMessage.class, 13, Side.CLIENT);
		
		MinecraftForge.EVENT_BUS.register(attachmentManager); 
		
		MinecraftForge.EVENT_BUS.register(new WeaponKeyInputHandler((ctx) -> getPlayer(ctx), 
				attachmentManager, reloadManager, channel));
	}
	
	private Map<ResourceLocation, SoundEvent> registeredSounds = new HashMap<>();
	
	@Override
	public SoundEvent registerSound(String sound) {
		ResourceLocation soundResourceLocation = new ResourceLocation(modId, sound);
		SoundEvent result = registeredSounds.get(soundResourceLocation);
		if(result == null) {
			result = new SoundEvent(soundResourceLocation);
			registeredSounds.put(soundResourceLocation, result);
			GameRegistry.register(result, soundResourceLocation);
		}
		return result;
	}

	@Override
	public void registerWeapon(String name, Weapon weapon) {
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
	public void runInMainThread(Runnable runnable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	@Override
	public WeaponClientStorageManager getWeaponClientStorageManager() {
		return null;
		//throw new IllegalStateException("Attempted to get instance of " + WeaponClientStorageManager.class.getSimpleName());
	}

	@Override
	public void registerRenderableItem(String name, Item item, ModelSourceRenderer renderer) {
		GameRegistry.registerItem(item, name);
	}
	
	
}
