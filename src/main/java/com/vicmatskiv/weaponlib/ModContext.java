package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;

import net.minecraft.item.Item;

public interface ModContext {
	
	public void init(Object mod, String modId, CompatibleChannel channel);

	public void registerWeapon(String name, Weapon weapon, WeaponRenderer renderer);
	
	public CompatibleChannel getChannel();
	
	public void runSyncTick(Runnable runnable);
	
	public AttachmentManager getAttachmentManager();
	
	public WeaponClientStorageManager getWeaponClientStorageManager();

	public void registerRenderableItem(String name, Item weapon, Object renderer);

	//TODO: append mod id in 1.7.10
	public CompatibleSound registerSound(String sound);

	public void runInMainThread(Runnable runnable);

	public PlayerItemRegistry getPlayerItemRegistry();

	public WeaponReloadAspect getWeaponReloadAspect();
	
	public FireAspect getWeaponFireAspect();

}
