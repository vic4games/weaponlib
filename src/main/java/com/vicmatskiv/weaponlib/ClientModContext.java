package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vicmatskiv.weaponlib.command.DebugCommand;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderingRegistry;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWorldRenderer;
import com.vicmatskiv.weaponlib.electronics.EntityWirelessCamera;
import com.vicmatskiv.weaponlib.melee.ItemMelee;
import com.vicmatskiv.weaponlib.melee.MeleeRenderer;
import com.vicmatskiv.weaponlib.melee.PlayerMeleeInstance;
import com.vicmatskiv.weaponlib.perspective.PerspectiveManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.ClientCommandHandler;

public class ClientModContext extends CommonModContext {

	private ClientEventHandler clientEventHandler;
	private Lock mainLoopLock = new ReentrantLock();
	private int modEntityID;
	private Queue<Runnable> runInClientThreadQueue = new LinkedBlockingQueue<>();
	
	private CompatibleRenderingRegistry rendererRegistry;
	
	private Framebuffer framebuffer;
	private CompatibleWorldRenderer entityRenderer;
	private SafeGlobals safeGlobals = new SafeGlobals();
	//static ReloadAspect.ReloadContext context;
	
	
	private StatusMessageCenter statusMessageCenter;
	
	private PerspectiveManager viewManager;
	
	@Override
	public void init(Object mod, String modId, CompatibleChannel channel) {
		super.init(mod, modId, channel);
		
		ClientCommandHandler.instance.registerCommand(new DebugCommand());
		
		this.statusMessageCenter = new StatusMessageCenter();
		
		rendererRegistry = new CompatibleRenderingRegistry(modId);

		List<IResourcePack> defaultResourcePacks = compatibility.getPrivateValue(
				Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao") ; 
        defaultResourcePacks.add(new WeaponResourcePack()) ;
   
                
		compatibility.registerWithEventBus(new CustomGui(Minecraft.getMinecraft(), this, weaponAttachmentAspect));
		compatibility.registerWithEventBus(new WeaponEventHandler(this, safeGlobals));
		
		KeyBindings.init();
		
		ClientWeaponTicker clientWeaponTicker = new ClientWeaponTicker(this);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			clientWeaponTicker.shutdown();
		}));
		
		clientWeaponTicker.start();
		clientEventHandler = new ClientEventHandler(this, mainLoopLock, safeGlobals, runInClientThreadQueue);
		compatibility.registerWithFmlEventBus(clientEventHandler);
		
		compatibility.registerRenderingRegistry(rendererRegistry);
		
		compatibility.registerModEntity(WeaponSpawnEntity.class, "Ammo" + modEntityID, modEntityID++, mod, 64, 10, true);
	    compatibility.registerModEntity(EntityWirelessCamera.class, "wcam" + modEntityID, modEntityID++, mod, 200, 10, true);

		rendererRegistry.registerEntityRenderingHandler(WeaponSpawnEntity.class, new SpawnEntityRenderer());
	
		this.viewManager = new PerspectiveManager(this);
	}
	
	@Override
	public void registerServerSideOnly() {}
	
	public PerspectiveManager getViewManager() {
        return viewManager;
    }
	
	public CompatibleWorldRenderer getSecondWorldRenderer() {
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
		if(framebuffer == null) {
			framebuffer = new Framebuffer(200, 200, true);
	        framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		}
		
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
	public PlayerItemInstanceRegistry getPlayerItemInstanceRegistry() {
		return playerItemInstanceRegistry;
	}
	
	protected SyncManager<?> getSyncManager() {
		return syncManager;
	}
	
	@Override
	public PlayerWeaponInstance getMainHeldWeapon() {
		return getPlayerItemInstanceRegistry().getMainHandItemInstance(compatibility.clientPlayer(), 
				PlayerWeaponInstance.class);
	}
	
	@Override
	public StatusMessageCenter getStatusMessageCenter() {
		return statusMessageCenter;
	}

    public PlayerMeleeInstance getMainHeldMeleeWeapon() {
        return getPlayerItemInstanceRegistry().getMainHandItemInstance(compatibility.clientPlayer(), 
                PlayerMeleeInstance.class);
    }
    
    @Override
    public void registerMeleeWeapon(String name, ItemMelee itemMelee, MeleeRenderer renderer) {
        super.registerMeleeWeapon(name, itemMelee, renderer);
        rendererRegistry.register(itemMelee, itemMelee.getName(), itemMelee.getRenderer());
        renderer.setClientModContext(this);
    }
}
