package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;

import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSide;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.network.NetworkPermitManager;
import com.vicmatskiv.weaponlib.network.PermitMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class CommonModContext implements ModContext {
	
	protected CompatibleChannel channel;
	
	protected AttachmentManager attachmentManager;
	protected FireManager fireManager;
	protected ReloadManager reloadManager;
	protected ReloadAspect reloadAspect;
	
	protected NetworkPermitManager permitManager;
	
	private String modId;
	
	private Map<ResourceLocation, CompatibleSound> registeredSounds = new HashMap<>();

	@Override
	public void init(Object mod, String modId, CompatibleChannel channel) {
		this.channel = channel;
		this.modId = modId;
		
		this.attachmentManager = new AttachmentManager(this);
		this.fireManager = new FireManager(this);
		this.reloadManager = new ReloadManager(this);
		this.reloadAspect = new ReloadAspect(this);
		this.permitManager = new NetworkPermitManager(this);
		
		channel.registerMessage(new ReloadMessageHandler(reloadManager, (ctx) -> getServerPlayer(ctx)),
				ReloadMessage.class, 1, CompatibleSide.SERVER);
		
		channel.registerMessage(new ReloadMessageHandler(reloadManager, (ctx) -> getPlayer(ctx)),
				ReloadMessage.class, 2, CompatibleSide.CLIENT);
		
		channel.registerMessage(new AttachmentModeMessageHandler(attachmentManager),
				AttachmentModeMessage.class, 3, CompatibleSide.SERVER);
		
		channel.registerMessage(new AttachmentModeMessageHandler(attachmentManager),
				AttachmentModeMessage.class, 4, CompatibleSide.CLIENT);
		
		channel.registerMessage(new ChangeAttachmentMessageHandler(attachmentManager),
				ChangeAttachmentMessage.class, 5, CompatibleSide.SERVER);
		
		channel.registerMessage(new ChangeAttachmentMessageHandler(attachmentManager),
				ChangeAttachmentMessage.class, 6, CompatibleSide.CLIENT);
		
		channel.registerMessage(new ChangeTextureMessageHandler(attachmentManager),
				ChangeTextureMessage.class, 7, CompatibleSide.SERVER);
		
		channel.registerMessage(new ChangeTextureMessageHandler(attachmentManager),
				ChangeTextureMessage.class, 8, CompatibleSide.CLIENT);
		
		channel.registerMessage(new ChangeSettingMessageHandler((ctx) -> getPlayer(ctx)),
				ChangeSettingsMessage.class, 9, CompatibleSide.CLIENT);
		
		channel.registerMessage(new ChangeSettingMessageHandler((ctx) -> getPlayer(ctx)),
				ChangeSettingsMessage.class, 10, CompatibleSide.SERVER);

		channel.registerMessage(new TryFireMessageHandler(fireManager),
				TryFireMessage.class, 11, CompatibleSide.SERVER);
		
		channel.registerMessage(new LaserSwitchMessageHandler(),
				LaserSwitchMessage.class, 12, CompatibleSide.SERVER);
		
		channel.registerMessage(new LaserSwitchMessageHandler(),
				LaserSwitchMessage.class, 13, CompatibleSide.CLIENT);
		
		channel.registerMessage(permitManager,
				PermitMessage.class, 14, CompatibleSide.SERVER);
		
		channel.registerMessage(permitManager,
				PermitMessage.class, 15, CompatibleSide.CLIENT);
		
		compatibility.registerWithEventBus(new ServerEventHandler(attachmentManager));
		
		compatibility.registerWithFmlEventBus(new WeaponKeyInputHandler((ctx) -> getPlayer(ctx), 
				attachmentManager, reloadManager, reloadAspect, channel));
	}
	
	
	@Override
	public CompatibleSound registerSound(String sound) {
		ResourceLocation soundResourceLocation = new ResourceLocation(modId, sound);
		CompatibleSound result = registeredSounds.get(soundResourceLocation);
		if(result == null) {
			result = new CompatibleSound(soundResourceLocation);
			registeredSounds.put(soundResourceLocation, result);
			compatibility.registerSound(result);
		}
		return result;
	}
	
	@Override
	public void registerWeapon(String name, Weapon weapon, WeaponRenderer renderer) {
		compatibility.registerItem(weapon, name);
	}
	
	private EntityPlayer getServerPlayer(CompatibleMessageContext ctx) {
		return ctx != null ? ctx.getPlayer() : null;
	}
	
	protected EntityPlayer getPlayer(CompatibleMessageContext ctx) {
		return getServerPlayer(ctx);
	}

	@Override
	public CompatibleChannel getChannel() {
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
	public void registerRenderableItem(String name, Item item, Object renderer) {
		compatibility.registerItem(item, name);
	}


	@Override
	public PlayerItemRegistry getPlayerItemRegistry() {
		throw new UnsupportedOperationException();
	}
}
