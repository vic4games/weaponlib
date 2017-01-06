package com.vicmatskiv.weaponlib;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;


public interface ModContext {
	
	public void init(Object mod, SimpleNetworkWrapper channel);

	public void registerWeapon(String name, Weapon weapon, IItemRenderer renderer);
	
	public SimpleNetworkWrapper getChannel();
	
	public void runSyncTick(Runnable runnable);
	
	public AttachmentManager getAttachmentManager();
	
	public WeaponClientStorageManager getWeaponClientStorageManager();

	public void registerRenderableItem(String name, Item weapon, IItemRenderer renderer);
	
}
