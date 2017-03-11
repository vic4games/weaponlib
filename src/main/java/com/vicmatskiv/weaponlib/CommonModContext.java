package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;

import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSide;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.crafting.RecipeGenerator;
import com.vicmatskiv.weaponlib.network.NetworkPermitManager;
import com.vicmatskiv.weaponlib.network.PermitMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class CommonModContext implements ModContext {

	private String modId;
	
	protected CompatibleChannel channel;
	
	protected WeaponReloadAspect weaponReloadAspect;
	protected WeaponAttachmentAspect weaponAttachmentAspect;
	protected WeaponFireAspect weaponFireAspect;
	
	protected MagazineReloadAspect magazineReloadAspect;
	
	protected NetworkPermitManager permitManager;
	
	private Map<ResourceLocation, CompatibleSound> registeredSounds = new HashMap<>();
	
	private RecipeGenerator recipeGenerator;
	
	private CompatibleSound changeZoomSound;
	
	private CompatibleSound changeFireModeSound;
	
	private CompatibleSound noAmmoSound;


	@Override
	public void init(Object mod, String modId, CompatibleChannel channel) {
		this.channel = channel;
		this.modId = modId;
		
		this.weaponReloadAspect = new WeaponReloadAspect(this);
		this.magazineReloadAspect = new MagazineReloadAspect(this);
		this.weaponFireAspect = new WeaponFireAspect(this);
		this.weaponAttachmentAspect = new WeaponAttachmentAspect(this);
		this.permitManager = new NetworkPermitManager(this);
		
		this.recipeGenerator = new RecipeGenerator();

		channel.registerMessage(new TryFireMessageHandler(weaponFireAspect),
				TryFireMessage.class, 11, CompatibleSide.SERVER);
		
		channel.registerMessage(permitManager,
				PermitMessage.class, 14, CompatibleSide.SERVER);
		
		channel.registerMessage(permitManager,
				PermitMessage.class, 15, CompatibleSide.CLIENT);
		
		compatibility.registerWithEventBus(new ServerEventHandler(this));
		
		compatibility.registerWithFmlEventBus(new WeaponKeyInputHandler(this, (ctx) -> getPlayer(ctx), 
				weaponAttachmentAspect, channel));
	}
	
	@Override
	public CompatibleSound registerSound(String sound) {
		ResourceLocation soundResourceLocation = new ResourceLocation(modId, sound);
		return registerSound(soundResourceLocation);
	}
	
	protected CompatibleSound registerSound(ResourceLocation soundResourceLocation) {
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
	public void registerRenderableItem(String name, Item item, Object renderer) {
		compatibility.registerItem(item, name);
	}

	@Override
	public PlayerItemInstanceRegistry getPlayerItemInstanceRegistry() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WeaponReloadAspect getWeaponReloadAspect() {
		return weaponReloadAspect;
	}

	@Override
	public WeaponFireAspect getWeaponFireAspect() {
		return weaponFireAspect;
	}

	@Override
	public WeaponAttachmentAspect getAttachmentAspect() {
		return weaponAttachmentAspect;
	}

	@Override
	public MagazineReloadAspect getMagazineReloadAspect() {
		return magazineReloadAspect;
	}


	@Override
	public PlayerWeaponInstance getMainHeldWeapon() {
		throw new IllegalStateException();
	}
	
	@Override
	public StatusMessageCenter getStatusMessageCenter() {
		throw new IllegalStateException();
	}


	@Override
	public RecipeGenerator getRecipeGenerator() {
		return recipeGenerator;
	}

	@Override
	public void setChangeZoomSound(String sound) {
		this.changeZoomSound = registerSound(sound);
	}
	
	@Override
	public CompatibleSound getZoomSound() {
		return changeZoomSound;
	}

	@Override
	public CompatibleSound getChangeFireModeSound() {
		return changeFireModeSound;
	}

	@Override
	public void setChangeFireModeSound(String sound) {
		this.changeFireModeSound = registerSound(sound);
	}

	@Override
	public void setNoAmmoSound(String sound) {
		this.noAmmoSound = registerSound(sound);
	}

	@Override
	public CompatibleSound getNoAmmoSound() {
		return noAmmoSound;
	}
}
