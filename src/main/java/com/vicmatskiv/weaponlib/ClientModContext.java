package com.vicmatskiv.weaponlib;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ClientModContext extends CommonModContext {

	private ClientEventHandler clientEventHandler;
	private Lock mainLoopLock = new ReentrantLock();
	private int modEntityID;
	private WeaponClientStorageManager weaponClientStorageManager;
	private Queue<Runnable> runInClientThreadQueue = new LinkedBlockingQueue<>();
	
	private RenderingRegistry rendererRegistry;
	
	@SuppressWarnings("deprecation")
	@Override
	public void init(Object mod, String modId, SimpleNetworkWrapper channel) {
		super.init(mod, modId, channel);
		
		rendererRegistry = new RenderingRegistry(modId);
		
		ModelLoaderRegistry.registerLoader(rendererRegistry);
		
		List<IResourcePack> defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(
				Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao") ; 
        defaultResourcePacks.add(new WeaponResourcePack()) ;
   
        this.weaponClientStorageManager = new WeaponClientStorageManager();
		SafeGlobals safeGlobals = new SafeGlobals();
		
		MinecraftForge.EVENT_BUS.register(new CustomGui(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new WeaponEventHandler(safeGlobals));
		
		KeyBindings.init();	

		ClientWeaponTicker clientWeaponTicker = new ClientWeaponTicker(safeGlobals, fireManager, reloadManager);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			clientWeaponTicker.shutdown();
		}));
		
		clientWeaponTicker.start();
		clientEventHandler = new ClientEventHandler(mainLoopLock, safeGlobals, runInClientThreadQueue);
		MinecraftForge.EVENT_BUS.register(clientEventHandler);
		
		MinecraftForge.EVENT_BUS.register(rendererRegistry);
				
		ResourceLocation entityResourceLocation = null;
		EntityRegistry.registerModEntity(entityResourceLocation, WeaponSpawnEntity.class, "Ammo" + modEntityID, modEntityID++, mod, 64, 10, true);
		
		// TODO: do something about it
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(WeaponSpawnEntity.class, 
				new SpawnEntityRenderer(Minecraft.getMinecraft().getRenderManager()));
	}
	
	
	@Override
	public void registerWeapon(String name, Weapon weapon) {
		super.registerWeapon(name, weapon);
		rendererRegistry.register(weapon, weapon.getName(), weapon.getRenderer());
	}
	
	@Override
	public void registerRenderableItem(String name, Item item, ModelSourceRenderer renderer) {
		super.registerRenderableItem(name, item, renderer);
		rendererRegistry.register(item, name, renderer);
	}
	
	@Override
	protected EntityPlayer getPlayer(MessageContext ctx) {
		return Minecraft.getMinecraft().player;
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
	public void runInMainThread(Runnable runnable) {
		runInClientThreadQueue.add(runnable);
	}
	
	@Override
	public WeaponClientStorageManager getWeaponClientStorageManager() {
		return weaponClientStorageManager;
	}
}
