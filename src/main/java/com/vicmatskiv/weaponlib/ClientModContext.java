package com.vicmatskiv.weaponlib;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientModContext extends CommonModContext {

	private ClientEventHandler clientEventHandler;
	private Lock mainLoopLock = new ReentrantLock();
	private int modEntityID;
	private WeaponClientStorageManager weaponClientStorageManager;
	
	@Override
	public void init(Object mod, SimpleNetworkWrapper channel) {
		super.init(mod, channel);
		
		List<IResourcePack> defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(
				Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao") ; 
        defaultResourcePacks.add(new WeaponResourcePack()) ;
   
        this.weaponClientStorageManager = new WeaponClientStorageManager();
		SafeGlobals safeGlobals = new SafeGlobals();
		
		MinecraftForge.EVENT_BUS.register(new CustomGui(Minecraft.getMinecraft(), attachmentManager));
		MinecraftForge.EVENT_BUS.register(new WeaponEventHandler(safeGlobals));
		
		KeyBindings.init();	

		ClientWeaponTicker clientWeaponTicker = new ClientWeaponTicker(safeGlobals, fireManager, reloadManager);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			clientWeaponTicker.shutdown();
		}));
		
		clientWeaponTicker.start();
		clientEventHandler = new ClientEventHandler(mainLoopLock, safeGlobals);
		FMLCommonHandler.instance().bus().register(clientEventHandler);
		
		EntityRegistry.registerModEntity(WeaponSpawnEntity.class, "Ammo" + modEntityID, modEntityID++, mod, 64, 10, true);
		RenderingRegistry.registerEntityRenderingHandler(WeaponSpawnEntity.class, new SpawnEntityRenderer());
		
	}
	
	@Override
	public void registerWeapon(String name, Weapon weapon, IItemRenderer renderer) {
		super.registerWeapon(name, weapon, renderer);
		MinecraftForgeClient.registerItemRenderer(weapon, renderer);

	}
	
	@Override
	public void registerRenderableItem(String name, Item item, IItemRenderer renderer) {
		super.registerRenderableItem(name, item, renderer);
		MinecraftForgeClient.registerItemRenderer(item, renderer);
	}
	
	@Override
	protected EntityPlayer getPlayer(MessageContext ctx) {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	@Override
	public void runSyncTick(Runnable runnable) {
		mainLoopLock.lock();
		try {
			runnable.run();
		} finally {
			mainLoopLock.unlock();
		}
	}
	
	@Override
	public WeaponClientStorageManager getWeaponClientStorageManager() {
		return weaponClientStorageManager;
	}
}
