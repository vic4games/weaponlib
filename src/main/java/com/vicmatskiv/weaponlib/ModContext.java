package com.vicmatskiv.weaponlib;

import net.minecraftforge.client.IItemRenderer;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;


public interface ModContext {
	
	public void init(Object mod, SimpleNetworkWrapper channel);

	public void registerWeapon(String name, Weapon weapon, IItemRenderer renderer);
	
	public SimpleNetworkWrapper getChannel();
	
	public void runSyncTick(Runnable runnable);
	
	public AttachmentManager getAttachmentManager();
	
	public WeaponClientStorageManager getWeaponClientStorageManager();
	
}
