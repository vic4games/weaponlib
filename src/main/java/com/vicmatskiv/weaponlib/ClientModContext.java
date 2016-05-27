package com.vicmatskiv.weaponlib;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ClientModContext extends CommonModContext {

	private ClientEventHandler clientEventHandler;
	private Lock mainLoopLock = new ReentrantLock();
	private int modEntityID;
	//private Object mod;
	
	@Override
	public void init(Object mod, SimpleNetworkWrapper channel) {
		super.init(mod, channel);
		//this.mod = mod;
		SafeGlobals safeGlobals = new SafeGlobals();
		
		MinecraftForge.EVENT_BUS.register(new CustomGui(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new WeaponEventHandler(safeGlobals));
		
		KeyBindings.init();	

		ClientWeaponTicker clientWeaponTicker = new ClientWeaponTicker(safeGlobals);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			clientWeaponTicker.shutdown();
		}));
		
		clientWeaponTicker.start();
		clientEventHandler = new ClientEventHandler(mainLoopLock, safeGlobals);
		FMLCommonHandler.instance().bus().register(clientEventHandler);
		
		EntityRegistry.registerModEntity(WeaponSpawnEntity.class, "Ammo" + modEntityID, modEntityID++, mod, 64, 10, true);
		RenderingRegistry.registerEntityRenderingHandler(WeaponSpawnEntity.class, 
				new SpawnEntityRenderer(/*weapon.getAmmoModel(), weapon.getAmmoModelTextureName()*/));
		
		//EntityRegistry.registerModEntity(WeaponSpawnEntity.class, "Ammo", ++modEntityID, mod, 64, 10, true);
	}
	
	@Override
	public void registerWeapon(String name, Weapon weapon, IItemRenderer renderer) {
		super.registerWeapon(name, weapon, renderer);
		MinecraftForgeClient.registerItemRenderer(weapon, renderer);

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
}
