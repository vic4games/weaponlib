package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vicmatskiv.weaponlib.command.DebugCommand;
import com.vicmatskiv.weaponlib.command.MainCommand;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderingRegistry;
import com.vicmatskiv.weaponlib.config.ConfigurationManager;
import com.vicmatskiv.weaponlib.electronics.EntityWirelessCamera;
import com.vicmatskiv.weaponlib.electronics.WirelessCameraRenderer;
import com.vicmatskiv.weaponlib.grenade.EntityGrenade;
import com.vicmatskiv.weaponlib.grenade.EntityGrenadeRenderer;
import com.vicmatskiv.weaponlib.grenade.EntitySmokeGrenade;
import com.vicmatskiv.weaponlib.grenade.GrenadeRenderer;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;
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
	private Queue<Runnable> runInClientThreadQueue = new LinkedBlockingQueue<>();

	private CompatibleRenderingRegistry rendererRegistry;

	private SafeGlobals safeGlobals = new SafeGlobals();

	private StatusMessageCenter statusMessageCenter;

	private PerspectiveManager viewManager;

	private float aspectRatio;
    private Framebuffer inventoryFramebuffer;

    private Map<Object, Integer> inventoryTextureMap;

    private EffectManager effectManager;

	@Override
    public void init(Object mod, String modId, ConfigurationManager configurationManager, CompatibleChannel channel) {
		super.init(mod, modId, configurationManager, channel);

		aspectRatio = (float)Minecraft.getMinecraft().displayWidth / Minecraft.getMinecraft().displayHeight;

		ClientCommandHandler.instance.registerCommand(new DebugCommand(modId));

		ClientCommandHandler.instance.registerCommand(new MainCommand(modId, this));

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

		rendererRegistry.registerEntityRenderingHandler(WeaponSpawnEntity.class, new SpawnEntityRenderer());
		rendererRegistry.registerEntityRenderingHandler(EntityWirelessCamera.class, new WirelessCameraRenderer(modId)); //new RenderSnowball(Items.snowball));
	    rendererRegistry.registerEntityRenderingHandler(EntityShellCasing.class, new ShellCasingRenderer()); //new RenderSnowball(Items.snowball));
        rendererRegistry.registerEntityRenderingHandler(EntityGrenade.class, new EntityGrenadeRenderer()); //new RenderSnowball(Items.snowball));
        rendererRegistry.registerEntityRenderingHandler(EntitySmokeGrenade.class, new EntityGrenadeRenderer()); //new RenderSnowball(Items.snowball));

		this.viewManager = new PerspectiveManager(this);
		this.inventoryTextureMap = new HashMap<>();

		this.effectManager = new ClientEffectManager();
	}

	@Override
	public void registerServerSideOnly() {}

	public PerspectiveManager getViewManager() {
        return viewManager;
    }

	public SafeGlobals getSafeGlobals() {
		return safeGlobals;
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

    @Override
    public void registerGrenadeWeapon(String name, ItemGrenade itemGrenade, GrenadeRenderer renderer) {
        super.registerGrenadeWeapon(name, itemGrenade, renderer);
        rendererRegistry.register(itemGrenade, itemGrenade.getName(), itemGrenade.getRenderer());
        renderer.setClientModContext(this);
    }

    @Override
    public float getAspectRatio() {
        return aspectRatio;
    }

    public Framebuffer getInventoryFramebuffer() {
        if(inventoryFramebuffer == null) {
            inventoryFramebuffer = new Framebuffer(256, 256, true);
            inventoryFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        }
        return inventoryFramebuffer;
    }

    public Map<Object, Integer> getInventoryTextureMap() {
        return inventoryTextureMap;
    }

    public String getModId() {
        return modId;
    }

    @Override
    public EffectManager getEffectManager() {
        return effectManager;
    }
}
