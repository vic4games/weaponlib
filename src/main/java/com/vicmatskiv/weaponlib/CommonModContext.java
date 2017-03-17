package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;

import com.vicmatskiv.weaponlib.MagazineReloadAspect.LoadPermit;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.ChangeAttachmentPermit;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.EnterAttachmentModePermit;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.ExitAttachmentModePermit;
import com.vicmatskiv.weaponlib.WeaponReloadAspect.UnloadPermit;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSide;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.crafting.RecipeGenerator;
import com.vicmatskiv.weaponlib.melee.ItemMelee;
import com.vicmatskiv.weaponlib.melee.MeleeRenderer;
import com.vicmatskiv.weaponlib.network.NetworkPermitManager;
import com.vicmatskiv.weaponlib.network.PermitMessage;
import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

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

    }

	private String modId;
	
	protected CompatibleChannel channel;
	
	protected WeaponReloadAspect weaponReloadAspect;
	protected WeaponAttachmentAspect weaponAttachmentAspect;
	protected WeaponFireAspect weaponFireAspect;

    protected SyncManager<?> syncManager;
	
	protected MagazineReloadAspect magazineReloadAspect;
	
	protected NetworkPermitManager permitManager;
	
	protected PlayerItemInstanceRegistry playerItemInstanceRegistry;

	
	private Map<ResourceLocation, CompatibleSound> registeredSounds = new HashMap<>();
	
	private RecipeGenerator recipeGenerator;
	
	private CompatibleSound changeZoomSound;
	
	private CompatibleSound changeFireModeSound;
	
	private CompatibleSound noAmmoSound;


	@Override
	public void init(Object mod, String modId, CompatibleChannel channel) {
		this.channel = channel;
		this.modId = modId;
		
		this.weaponReloadAspect = new WeaponReloadAspect(this);
		this.magazineReloadAspect = new MagazineReloadAspect(this);
		this.weaponFireAspect = new WeaponFireAspect(this);
		this.weaponAttachmentAspect = new WeaponAttachmentAspect(this);
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
        
        StateManager<MagazineState, PlayerMagazineInstance> magazineStateManager = new StateManager<>((s1, s2) -> s1 == s2);

        magazineReloadAspect.setPermitManager(permitManager);
        magazineReloadAspect.setStateManager(magazineStateManager);
		
		this.recipeGenerator = new RecipeGenerator();

		channel.registerMessage(new TryFireMessageHandler(weaponFireAspect),
				TryFireMessage.class, 11, CompatibleSide.SERVER);
		
		channel.registerMessage(permitManager,
				PermitMessage.class, 14, CompatibleSide.SERVER);
		
		channel.registerMessage(permitManager,
				PermitMessage.class, 15, CompatibleSide.CLIENT);
		
		compatibility.registerWithEventBus(new ServerEventHandler(this));
		
		compatibility.registerWithFmlEventBus(new WeaponKeyInputHandler(this, (ctx) -> getPlayer(ctx), 
				weaponAttachmentAspect, channel));
	}
	
	@Override
	public CompatibleSound registerSound(String sound) {
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
	public PlayerWeaponInstance getMainHeldWeapon() {
		throw new IllegalStateException();
	}
	
	@Override
	public StatusMessageCenter getStatusMessageCenter() {
		throw new IllegalStateException();
	}


	@Override
	public RecipeGenerator getRecipeGenerator() {
		return recipeGenerator;
	}

	@Override
	public void setChangeZoomSound(String sound) {
		this.changeZoomSound = registerSound(sound);
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
		this.changeFireModeSound = registerSound(sound);
	}

	@Override
	public void setNoAmmoSound(String sound) {
		this.noAmmoSound = registerSound(sound);
	}

	@Override
	public CompatibleSound getNoAmmoSound() {
		return noAmmoSound;
	}

    @Override
    public void registerMeleeWeapon(String name, ItemMelee itemMelee, MeleeRenderer renderer) {
        compatibility.registerItem(itemMelee, name);
    }
}
