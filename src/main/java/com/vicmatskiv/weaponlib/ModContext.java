package com.vicmatskiv.weaponlib;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public interface ModContext {
	
	public void init(Object mod, String modId, SimpleNetworkWrapper channel);

	public void registerWeapon(String name, Weapon weapon);
	
	public SimpleNetworkWrapper getChannel();
	
	public void runSyncTick(Runnable runnable);
	
	public void runInMainThread(Runnable runnable);
	
	public AttachmentManager getAttachmentManager();
	
	public WeaponClientStorageManager getWeaponClientStorageManager();

	public void registerRenderableItem(String name, Item weapon, ModelSourceRenderer renderer);

	public void registerSound(SoundEvent reloadSound, ResourceLocation reloadSoundLocation);
	
}
