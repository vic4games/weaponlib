package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.vicmatskiv.weaponlib.MagazineReloadAspect.LoadPermit;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.ChangeAttachmentPermit;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.EnterAttachmentModePermit;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.ExitAttachmentModePermit;
import com.vicmatskiv.weaponlib.WeaponReloadAspect.UnloadPermit;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExtraEntityFlags;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerEntityTrackerProvider;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSide;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.config.ConfigurationManager;
import com.vicmatskiv.weaponlib.crafting.RecipeManager;
import com.vicmatskiv.weaponlib.electronics.EntityWirelessCamera;
import com.vicmatskiv.weaponlib.electronics.HandheldState;
import com.vicmatskiv.weaponlib.electronics.PlayerHandheldInstance;
import com.vicmatskiv.weaponlib.electronics.PlayerTabletInstance;
import com.vicmatskiv.weaponlib.electronics.TabletState;
import com.vicmatskiv.weaponlib.grenade.EntityGasGrenade;
import com.vicmatskiv.weaponlib.grenade.EntityGrenade;
import com.vicmatskiv.weaponlib.grenade.EntitySmokeGrenade;
import com.vicmatskiv.weaponlib.grenade.GrenadeAttackAspect;
import com.vicmatskiv.weaponlib.grenade.GrenadeMessage;
import com.vicmatskiv.weaponlib.grenade.GrenadeMessageHandler;
import com.vicmatskiv.weaponlib.grenade.GrenadeRenderer;
import com.vicmatskiv.weaponlib.grenade.GrenadeState;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;
import com.vicmatskiv.weaponlib.grenade.PlayerGrenadeInstance;
import com.vicmatskiv.weaponlib.melee.ItemMelee;
import com.vicmatskiv.weaponlib.melee.MeleeAttachmentAspect;
import com.vicmatskiv.weaponlib.melee.MeleeAttackAspect;
import com.vicmatskiv.weaponlib.melee.MeleeRenderer;
import com.vicmatskiv.weaponlib.melee.MeleeState;
import com.vicmatskiv.weaponlib.melee.PlayerMeleeInstance;
import com.vicmatskiv.weaponlib.melee.TryAttackMessage;
import com.vicmatskiv.weaponlib.melee.TryAttackMessageHandler;
import com.vicmatskiv.weaponlib.network.NetworkPermitManager;
import com.vicmatskiv.weaponlib.network.PermitMessage;
import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.particle.SpawnParticleMessage;
import com.vicmatskiv.weaponlib.particle.SpawnParticleMessageHandler;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.StateManager;
import com.vicmatskiv.weaponlib.tracking.SyncPlayerEntityTrackerMessage;
import com.vicmatskiv.weaponlib.tracking.SyncPlayerEntityTrackerMessageMessageHandler;

public class CommonModContext implements ModContext {

    static {
        TypeRegistry.getInstance().register(LoadPermit.class);
        TypeRegistry.getInstance().register(MagazineState.class);
        TypeRegistry.getInstance().register(PlayerItemInstance.class);
        TypeRegistry.getInstance().register(PlayerWeaponInstance.class);
        TypeRegistry.getInstance().register(PlayerMagazineInstance.class);
        TypeRegistry.getInstance().register(PlayerWeaponInstance.class);
        TypeRegistry.getInstance().register(Permit.class);
        TypeRegistry.getInstance().register(EnterAttachmentModePermit.class);
        TypeRegistry.getInstance().register(ExitAttachmentModePermit.class);
        TypeRegistry.getInstance().register(ChangeAttachmentPermit.class);
        TypeRegistry.getInstance().register(UnloadPermit.class);
        TypeRegistry.getInstance().register(LoadPermit.class);
        TypeRegistry.getInstance().register(PlayerWeaponInstance.class);
        TypeRegistry.getInstance().register(WeaponState.class);
        TypeRegistry.getInstance().register(PlayerMeleeInstance.class);
        TypeRegistry.getInstance().register(PlayerGrenadeInstance.class);
        TypeRegistry.getInstance().register(PlayerTabletInstance.class);
        TypeRegistry.getInstance().register(PlayerHandheldInstance.class);
        TypeRegistry.getInstance().register(MeleeState.class);
        TypeRegistry.getInstance().register(TabletState.class);
        TypeRegistry.getInstance().register(HandheldState.class);
        TypeRegistry.getInstance().register(SpreadableExposure.class);
    }

	protected String modId;

	protected Object mod;
	
	protected CompatibleChannel channel;

	protected WeaponReloadAspect weaponReloadAspect;
	protected WeaponAttachmentAspect weaponAttachmentAspect;
	protected WeaponFireAspect weaponFireAspect;

	protected MeleeAttachmentAspect meleeAttachmentAspect;
    protected MeleeAttackAspect meleeAttackAspect;

    protected SyncManager<?> syncManager;

	protected MagazineReloadAspect magazineReloadAspect;

	protected NetworkPermitManager permitManager;

	protected PlayerItemInstanceRegistry playerItemInstanceRegistry;


	private Map<ResourceLocation, CompatibleSound> registeredSounds = new HashMap<>();

	private RecipeManager recipeManager;

	private CompatibleSound changeZoomSound;

	private CompatibleSound changeFireModeSound;

	private CompatibleSound noAmmoSound;
	
    private CompatibleSound explosionSound;
    
    private CompatibleSound nightVisionOnSound;
    
    private CompatibleSound nightVisionOffSound;

	private int modEntityID = 256;

    private GrenadeAttackAspect grenadeAttackAspect;

    protected ConfigurationManager configurationManager;

	@Override
    public void preInit(Object mod, String modId, ConfigurationManager configurationManager, CompatibleChannel channel) {
		this.mod = mod;
	    this.channel = channel;
		this.modId = modId;

		this.configurationManager = configurationManager;

		this.weaponReloadAspect = new WeaponReloadAspect(this);
		this.magazineReloadAspect = new MagazineReloadAspect(this);
		this.weaponFireAspect = new WeaponFireAspect(this);
		this.weaponAttachmentAspect = new WeaponAttachmentAspect(this);

		this.meleeAttackAspect = new MeleeAttackAspect(this);
        this.meleeAttachmentAspect = new MeleeAttachmentAspect(this);

        this.grenadeAttackAspect = new GrenadeAttackAspect(this);
        StateManager<GrenadeState, PlayerGrenadeInstance> grenadeStateManager = new StateManager<>((s1, s2) -> s1 == s2);
        grenadeAttackAspect.setStateManager(grenadeStateManager);

		this.permitManager = new NetworkPermitManager(this);

		this.syncManager = new SyncManager<>(permitManager);

        this.playerItemInstanceRegistry = new PlayerItemInstanceRegistry(syncManager);

		StateManager<WeaponState, PlayerWeaponInstance> weaponStateManager = new StateManager<>((s1, s2) -> s1 == s2);
        weaponReloadAspect.setPermitManager(permitManager);
        weaponReloadAspect.setStateManager(weaponStateManager);

        weaponFireAspect.setPermitManager(permitManager);
        weaponFireAspect.setStateManager(weaponStateManager);

        weaponAttachmentAspect.setPermitManager(permitManager);
        weaponAttachmentAspect.setStateManager(weaponStateManager);

        StateManager<MeleeState, PlayerMeleeInstance> meleeStateManager = new StateManager<>((s1, s2) -> s1 == s2);
        meleeAttackAspect.setStateManager(meleeStateManager);
        meleeAttachmentAspect.setPermitManager(permitManager);
        meleeAttachmentAspect.setStateManager(meleeStateManager);

        StateManager<MagazineState, PlayerMagazineInstance> magazineStateManager = new StateManager<>((s1, s2) -> s1 == s2);

        magazineReloadAspect.setPermitManager(permitManager);
        magazineReloadAspect.setStateManager(magazineStateManager);

		this.recipeManager = new RecipeManager();

		channel.registerMessage(new TryFireMessageHandler(weaponFireAspect),
				TryFireMessage.class, 11, CompatibleSide.SERVER);

		channel.registerMessage(permitManager,
				PermitMessage.class, 14, CompatibleSide.SERVER);

		channel.registerMessage(permitManager,
				PermitMessage.class, 15, CompatibleSide.CLIENT);

		channel.registerMessage(new TryAttackMessageHandler(meleeAttackAspect),
                TryAttackMessage.class, 16, CompatibleSide.SERVER);

		channel.registerMessage(new SyncPlayerEntityTrackerMessageMessageHandler(this),
		        SyncPlayerEntityTrackerMessage.class, 17, CompatibleSide.CLIENT);

		channel.registerMessage(new SpawnParticleMessageHandler(this),
		        SpawnParticleMessage.class, 18, CompatibleSide.CLIENT);

		channel.registerMessage(new BlockHitMessageHandler(this),
		        BlockHitMessage.class, 19, CompatibleSide.CLIENT);

		channel.registerMessage(new GrenadeMessageHandler(grenadeAttackAspect),
                GrenadeMessage.class, 20, CompatibleSide.SERVER);

		channel.registerMessage(new ExplosionMessageHandler(this),
                ExplosionMessage.class, 21, CompatibleSide.CLIENT);
		
		channel.registerMessage(new ArmorControlHandler(this),
                ArmorControlMessage.class, 22, CompatibleSide.SERVER);
		
		channel.registerMessage(new SpreadableExposureMessageHandler(this),
		        SpreadableExposureMessage.class, 23, CompatibleSide.CLIENT);
		
		channel.registerMessage(new EntityControlHandler(this),
                EntityControlMessage.class, 24, CompatibleSide.CLIENT);
		
		channel.registerMessage(new EntityControlHandler(this),
                EntityControlMessage.class, 25, CompatibleSide.SERVER);

		ServerEventHandler serverHandler = new ServerEventHandler(this, modId);
        compatibility.registerWithFmlEventBus(serverHandler);
        compatibility.registerWithEventBus(serverHandler);

		compatibility.registerWithFmlEventBus(new WeaponKeyInputHandler(this, (ctx) -> getPlayer(ctx),
				weaponAttachmentAspect, channel));

		CompatiblePlayerEntityTrackerProvider.register(this);
		//CompatibleEntityPropertyProvider.register(this);
		CompatibleExposureCapability.register(this);
		CompatibleExtraEntityFlags.register(this);

        compatibility.registerModEntity(WeaponSpawnEntity.class, "Ammo" + modEntityID, modEntityID++, mod, modId, 64, 3, true);
        compatibility.registerModEntity(EntityWirelessCamera.class, "wcam" + modEntityID, modEntityID++, mod, modId, 200, 3, true);
        compatibility.registerModEntity(EntityShellCasing.class, "ShellCasing" + modEntityID, modEntityID++, mod, modId, 64, 500, true);
        compatibility.registerModEntity(EntityGrenade.class, "Grenade" + modEntityID, modEntityID++, mod, modId, 64, 10000, false);
        compatibility.registerModEntity(EntitySmokeGrenade.class, "SmokeGrenade" + modEntityID, modEntityID++, mod, modId, 64, 10000, false);
        compatibility.registerModEntity(EntityGasGrenade.class, "GasGrenade" + modEntityID, modEntityID++, mod, modId, 64, 10000, false);
        compatibility.registerModEntity(EntitySpreadable.class, "EntitySpreadable" + modEntityID, modEntityID++, mod, modId, 64, 3, false);

//        compatibility.registerModEntity(EntityCustomMob.class, "CustomMob" + modEntityID, modEntityID++, mod, modId, 64, 3, true);
//
//        EntityRegistry.addSpawn(EntityCustomMob.class, 1, 1, 3, EnumCreatureType.MONSTER, 
//                BiomeDictionary.getBiomesForType(Type.PLAINS));
        
	}
	

    @Override
    public void init(Object mod, String modid) {}
	
	@Override
	public boolean isClient() {
	    return false;
	}

	public void registerServerSideOnly() {

	}

	@Override
	public CompatibleSound registerSound(String sound) {
	    if(sound == null) {
	        return null;
	    }
		ResourceLocation soundResourceLocation = new ResourceLocation(modId, sound);
		return registerSound(soundResourceLocation);
	}

	protected CompatibleSound registerSound(ResourceLocation soundResourceLocation) {
		CompatibleSound result = registeredSounds.get(soundResourceLocation);
		if(result == null) {
			result = new CompatibleSound(soundResourceLocation);
			registeredSounds.put(soundResourceLocation, result);
			compatibility.registerSound(result);
		}
		return result;
	}

	@Override
	public void registerWeapon(String name, Weapon weapon, WeaponRenderer renderer) {
		compatibility.registerItem(weapon, name);
	}

	private EntityPlayer getServerPlayer(CompatibleMessageContext ctx) {
		return ctx != null ? ctx.getPlayer() : null;
	}

	protected EntityPlayer getPlayer(CompatibleMessageContext ctx) {
		return getServerPlayer(ctx);
	}

	@Override
	public CompatibleChannel getChannel() {
		return channel;
	}

	@Override
	public void runSyncTick(Runnable runnable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void runInMainThread(Runnable runnable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerRenderableItem(String name, Item item, Object renderer) {
		compatibility.registerItem(item, name);
	}

	@Override
	public PlayerItemInstanceRegistry getPlayerItemInstanceRegistry() {
        return playerItemInstanceRegistry;
    }

	@Override
	public WeaponReloadAspect getWeaponReloadAspect() {
		return weaponReloadAspect;
	}

	@Override
	public WeaponFireAspect getWeaponFireAspect() {
		return weaponFireAspect;
	}

	@Override
	public WeaponAttachmentAspect getAttachmentAspect() {
		return weaponAttachmentAspect;
	}

	@Override
	public MagazineReloadAspect getMagazineReloadAspect() {
		return magazineReloadAspect;
	}

	@Override
	public MeleeAttackAspect getMeleeAttackAspect() {
	    return meleeAttackAspect;
	}

	@Override
	public MeleeAttachmentAspect getMeleeAttachmentAspect() {
	    return meleeAttachmentAspect;
	}

	@Override
	public PlayerWeaponInstance getMainHeldWeapon() {
		throw new IllegalStateException();
	}

	@Override
	public StatusMessageCenter getStatusMessageCenter() {
		throw new IllegalStateException();
	}


	@Override
	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	@Override
	public void setChangeZoomSound(String sound) {
		this.changeZoomSound = registerSound(sound.toLowerCase());
	}

	@Override
	public CompatibleSound getZoomSound() {
		return changeZoomSound;
	}

	@Override
	public CompatibleSound getChangeFireModeSound() {
		return changeFireModeSound;
	}

	@Override
	public void setChangeFireModeSound(String sound) {
		this.changeFireModeSound = registerSound(sound.toLowerCase());
	}

	@Override
	public void setNoAmmoSound(String sound) {
		this.noAmmoSound = registerSound(sound.toLowerCase());
	}

	@Override
	public CompatibleSound getNoAmmoSound() {
		return noAmmoSound;
	}

	@Override
	public void setExplosionSound(String sound) {
	    this.explosionSound = registerSound(sound.toLowerCase());
	}

	@Override
	public CompatibleSound getExplosionSound() {
	    return explosionSound;
	}
	
	@Override
    public void setNightVisionOnSound(String sound) {
        this.nightVisionOnSound = registerSound(sound.toLowerCase());
    }

    @Override
    public CompatibleSound getNightVisionOnSound() {
        return nightVisionOnSound;
    }
    
    @Override
    public void setNightVisionOffSound(String sound) {
        this.nightVisionOffSound = registerSound(sound.toLowerCase());
    }

    @Override
    public CompatibleSound getNightVisionOffSound() {
        return nightVisionOffSound;
    }

    @Override
    public void registerMeleeWeapon(String name, ItemMelee itemMelee, MeleeRenderer renderer) {
        compatibility.registerItem(itemMelee, name);
    }

    @Override
    public void registerGrenadeWeapon(String name, ItemGrenade itemMelee, GrenadeRenderer renderer) {
        compatibility.registerItem(itemMelee, name);
    }

    @Override
    public ResourceLocation getNamedResource(String name) {
        return new ResourceLocation(modId, name);
    }

    @Override
    public float getAspectRatio() {
        return 1f;
    }

    @Override
    public AttachmentContainer getGrenadeAttachmentAspect() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GrenadeAttackAspect getGrenadeAttackAspect() {
        return grenadeAttackAspect;
    }

    @Override
    public String getModId() {
        return modId;
    }
    
    @Override
    public Object getMod() {
        return mod;
    }

    @Override
    public EffectManager getEffectManager() {
        throw new IllegalStateException();
    }

    @Override
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    @Override
    public void registerRenderableEntity(Class<? extends Entity> entityClass, Object renderer) {}

    @Override
    public void setPlayerTransitionProvider(PlayerTransitionProvider playerTransitionProvider) {}

}
