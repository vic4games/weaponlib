package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderingRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class ClientModContext extends CommonModContext {

	private ClientEventHandler clientEventHandler;
	private Lock mainLoopLock = new ReentrantLock();
	private int modEntityID;
	private WeaponClientStorageManager weaponClientStorageManager;
	private Queue<Runnable> runInClientThreadQueue = new LinkedBlockingQueue<>();
	
	private CompatibleRenderingRegistry rendererRegistry;
	
	private Framebuffer framebuffer;
	
	@Override
	public void init(Object mod, String modId, CompatibleChannel channel) {
		super.init(mod, modId, channel);
		
		this.framebuffer = new Framebuffer(200, 200, true);
        this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		
		rendererRegistry = new CompatibleRenderingRegistry(modId);

		List<IResourcePack> defaultResourcePacks = compatibility.getPrivateValue(
				Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao") ; 
        defaultResourcePacks.add(new WeaponResourcePack()) ;
   
        this.weaponClientStorageManager = new WeaponClientStorageManager();
		SafeGlobals safeGlobals = new SafeGlobals();
		
		compatibility.registerWithEventBus(new CustomGui(Minecraft.getMinecraft(), attachmentManager));
		compatibility.registerWithEventBus(new WeaponEventHandler(safeGlobals));
		
		KeyBindings.init();	

		ClientWeaponTicker clientWeaponTicker = new ClientWeaponTicker(safeGlobals, fireManager, reloadManager);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			clientWeaponTicker.shutdown();
		}));
		
		clientWeaponTicker.start();
		clientEventHandler = new ClientEventHandler(this, mainLoopLock, safeGlobals, runInClientThreadQueue);
		compatibility.registerWithFmlEventBus(clientEventHandler);
		
		compatibility.registerRenderingRegistry(rendererRegistry);
		
		compatibility.registerModEntity(WeaponSpawnEntity.class, "Ammo" + modEntityID, modEntityID++, mod, 64, 10, true);
		
		rendererRegistry.registerEntityRenderingHandler(WeaponSpawnEntity.class, new SpawnEntityRenderer());
	}
	
	public Framebuffer getFramebuffer() {
		return framebuffer;
	}

	@Override
	public void registerWeapon(String name, Weapon weapon, WeaponRenderer renderer) {
		super.registerWeapon(name, weapon, renderer);
		rendererRegistry.register(weapon, weapon.getName(), weapon.getRenderer());
		renderer.setClientModContext(this);
	}
	
	@Override
	public void registerRenderableItem(String name, Item item, Object renderer) {
		super.registerRenderableItem(name, item, renderer);
		rendererRegistry.register(item, name, renderer);
	}
	
	@Override
	protected EntityPlayer getPlayer(CompatibleMessageContext ctx) {
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
	public void runInMainThread(Runnable runnable) {
		runInClientThreadQueue.add(runnable);
	}
	
	@Override
	public WeaponClientStorageManager getWeaponClientStorageManager() {
		return weaponClientStorageManager;
	}
}
