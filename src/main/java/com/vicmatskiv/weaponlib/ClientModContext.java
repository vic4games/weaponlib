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
import com.vicmatskiv.weaponlib.compatibility.CompatibleWorldRenderer;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.shader.Framebuffer;
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
	private CompatibleWorldRenderer entityRenderer;
	private SafeGlobals safeGlobals = new SafeGlobals();
	//static ReloadAspect.ReloadContext context;
	
	private SyncManager<?> syncManager;
	
	private PlayerItemInstanceRegistry playerItemInstanceRegistry;
	
	@Override
	public void init(Object mod, String modId, CompatibleChannel channel) {
		super.init(mod, modId, channel);
		
		this.framebuffer = new Framebuffer(200, 200, true);
        this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		
		rendererRegistry = new CompatibleRenderingRegistry(modId);

		List<IResourcePack> defaultResourcePacks = compatibility.getPrivateValue(
				Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao") ; 
        defaultResourcePacks.add(new WeaponResourcePack()) ;
   
        this.syncManager = new SyncManager<>(permitManager);
        
        this.playerItemInstanceRegistry = new PlayerItemInstanceRegistry(syncManager);
        
        this.weaponClientStorageManager = new WeaponClientStorageManager();
		
		
		compatibility.registerWithEventBus(new CustomGui(Minecraft.getMinecraft(), attachmentManager));
		compatibility.registerWithEventBus(new WeaponEventHandler(this, safeGlobals));
		
		KeyBindings.init();
		
		StateManager<WeaponState, PlayerItemInstance<WeaponState>> stateManager = new StateManager<>((s1, s2) -> s1 == s2); // implement comparator properly, ref equality will not work on server after deserialization
		
		
		weaponReloadAspect.setPermitManager(permitManager);
		weaponReloadAspect.setStateManager(stateManager);
		
		weaponFireAspect.setPermitManager(permitManager);
		weaponFireAspect.setStateManager(stateManager);

		ClientWeaponTicker clientWeaponTicker = new ClientWeaponTicker(this, fireManager, reloadManager);
		
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
	
	protected CompatibleWorldRenderer getSecondWorldRenderer() {
		if(this.entityRenderer == null) {
			this.entityRenderer = new CompatibleWorldRenderer(Minecraft.getMinecraft(), 
	        		Minecraft.getMinecraft().getResourceManager());
		}
		return this.entityRenderer;
	}
	
	public SafeGlobals getSafeGlobals() {
		return safeGlobals;
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
		return compatibility.clientPlayer();
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

	@Override
	public PlayerItemInstanceRegistry getPlayerItemInstanceRegistry() {
		return playerItemInstanceRegistry;
	}
	
	protected SyncManager<?> getSyncManager() {
		return syncManager;
	}
}
