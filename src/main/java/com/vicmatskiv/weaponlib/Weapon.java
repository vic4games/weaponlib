package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleItem;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class Weapon extends CompatibleItem implements 
	PlayerItemInstanceFactory<PlayerWeaponInstance, WeaponState>, AttachmentContainer, Reloadable, Modifiable, Updatable {
	
	private static final Logger logger = LogManager.getLogger(Weapon.class);

	public static class Builder {

	private static final float DEFAULT_SPAWN_ENTITY_SPEED = 10f;
		
		String name;
		List<String> textureNames = new ArrayList<>();
		int ammoCapacity = 0;
		float recoil = 1.0F;

		private String shootSound;
		private String silencedShootSound;
		private String reloadSound;
		private String unloadSound;
		private String ejectSpentRoundSound;

		@SuppressWarnings("unused")
		private String exceededMaxShotsSound;
		ItemAmmo ammo;
		float fireRate = Weapon.DEFAULT_FIRE_RATE;
		private CreativeTabs creativeTab;
		private WeaponRenderer renderer;
		float zoom = Weapon.DEFAULT_ZOOM;
		List<Integer> maxShots = new ArrayList<>();
		String crosshair;
		String crosshairRunning;
		String crosshairZoomed;
		BiFunction<Weapon, EntityPlayer, ? extends WeaponSpawnEntity> spawnEntityWith;
		private float spawnEntityDamage;
		private float spawnEntityExplosionRadius;
		private float spawnEntityGravityVelocity;
		long reloadingTimeout = Weapon.DEFAULT_RELOADING_TIMEOUT_TICKS;
		private String modId;

		boolean crosshairFullScreen = false;
		boolean crosshairZoomedFullScreen = false;

		Map<ItemAttachment<Weapon>, CompatibleAttachment<Weapon>> compatibleAttachments = new HashMap<>();
		ModelBase ammoModel;
		String ammoModelTextureName;

		private float spawnEntitySpeed = DEFAULT_SPAWN_ENTITY_SPEED;
		private Class<? extends WeaponSpawnEntity> spawnEntityClass;
		ImpactHandler blockImpactHandler;
		long pumpTimeoutMilliseconds;

		private float inaccuracy = WeaponSpawnEntity.DEFAULT_INACCURACY;

		int pellets = 1;

		float flashIntensity = 0.7f;

		long unloadingTimeout = Weapon.DEFAULT_UNLOADING_TIMEOUT_TICKS;
		
		private boolean ejectSpentRoundRequired;

		public int maxBulletsPerReload;

		private Function<ItemStack, List<String>> informationProvider;


		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withEjectRoundRequired() {
			this.ejectSpentRoundRequired = true;
			return this;
		}
		
		public Builder withInformationProvider(Function<ItemStack, List<String>> informationProvider) {
			this.informationProvider = informationProvider;
			return this;
		}

		public Builder withReloadingTime(long reloadingTime) {
			this.reloadingTimeout = reloadingTime;
			return this;
		}

		public Builder withUnloadingTime(long unloadingTime) {
			this.unloadingTimeout = unloadingTime;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withAmmoCapacity(int ammoCapacity) {
			this.ammoCapacity = ammoCapacity;
			return this;
		}
		
		public Builder withMaxBulletsPerReload(int maxBulletsPerReload) {
			this.maxBulletsPerReload = maxBulletsPerReload;
			return this;
		}

		public Builder withRecoil(float recoil) {
			this.recoil = recoil;
			return this;
		}

		public Builder withZoom(float zoom) {
			this.zoom = zoom;
			return this;
		}

		public Builder withAmmo(ItemAmmo ammo) {
			this.ammo = ammo;
			return this;
		}

		public Builder withMaxShots(int... maxShots) {
			for(int m: maxShots) {
				this.maxShots.add(m);
			}
			return this;
		}

		public Builder withFireRate(float fireRate) {
			if (fireRate >= 1 || fireRate <= 0) {
				throw new IllegalArgumentException("Invalid fire rate " + fireRate);
			}
			this.fireRate = fireRate;
			return this;
		}

		public Builder withTextureNames(String... textureNames) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			for (String textureName : textureNames) {
				this.textureNames.add(textureName + ".png");
			}
			return this;
		}

		public Builder withCrosshair(String crosshair) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshair = modId + ":" + "textures/crosshairs/" + crosshair + ".png";
			return this;
		}

		public Builder withCrosshair(String crosshair, boolean fullScreen) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshair = modId + ":" + "textures/crosshairs/" + crosshair + ".png";
			this.crosshairFullScreen = fullScreen;
			return this;
		}

		public Builder withCrosshairRunning(String crosshairRunning) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshairRunning = modId + ":" + "textures/crosshairs/" + crosshairRunning + ".png";
			return this;
		}

		public Builder withCrosshairZoomed(String crosshairZoomed) {
			return withCrosshairZoomed(crosshairZoomed, true);
		}

		public Builder withCrosshairZoomed(String crosshairZoomed, boolean fullScreen) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshairZoomed = modId + ":" + "textures/crosshairs/" + crosshairZoomed + ".png";
			this.crosshairZoomedFullScreen = fullScreen;
			return this;
		}

		public Builder withShootSound(String shootSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.shootSound = shootSound; //modId + ":" + shootSound;
			return this;
		}
		
		public Builder withEjectSpentRoundSound(String ejectSpentRoundSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.ejectSpentRoundSound = ejectSpentRoundSound;
			return this;
		}

		public Builder withSilencedShootSound(String silencedShootSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.silencedShootSound = silencedShootSound;
			return this;
		}

		public Builder withReloadSound(String reloadSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.reloadSound = reloadSound; //modId + ":" + reloadSound;
			return this;
		}

		public Builder withUnloadSound(String unloadSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.unloadSound = unloadSound;
			return this;
		}

		public Builder withExceededMaxShotsSound(String shootSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.exceededMaxShotsSound = shootSound; //modId + ":" + shootSound;
			return this;
		}

		public Builder withCreativeTab(CreativeTabs creativeTab) {
			this.creativeTab = creativeTab;
			return this;
		}

		public Builder withSpawnEntityDamage(float spawnEntityDamage) {
			this.spawnEntityDamage = spawnEntityDamage;
			return this;
		}

		public Builder withSpawnEntitySpeed(float spawnEntitySpeed) {
			this.spawnEntitySpeed = spawnEntitySpeed;
			return this;
		}

		public Builder withSpawnEntityExplosionRadius(float spawnEntityExplosionRadius) {
			this.spawnEntityExplosionRadius = spawnEntityExplosionRadius;
			return this;
		}

		public Builder withSpawnEntityGravityVelocity(float spawnEntityGravityVelocity) {
			this.spawnEntityGravityVelocity = spawnEntityGravityVelocity;
			return this;
		}

		public Builder withInaccuracy(float inaccuracy) {
			this.inaccuracy = inaccuracy;
			return this;
		}

		public Builder withRenderer(WeaponRenderer renderer) {
			this.renderer = renderer;
			return this;
		}
		
		public Builder withCompatibleBullet(ItemBullet bullet, Consumer<ModelBase> positioner) {
			compatibleAttachments.put(bullet, new CompatibleAttachment<>(bullet, positioner));
			return this;
		}
		
		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, BiConsumer<EntityPlayer, ItemStack> positioning, Consumer<ModelBase> modelPositioning) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, modelPositioning, false));
			return this;
		}
		
		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, BiConsumer<EntityPlayer, ItemStack> positioning) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, null, false));
			return this;
		}

		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, Consumer<ModelBase> positioner) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner));
			return this;
		}

		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, boolean isDefault,
				BiConsumer<EntityPlayer, ItemStack> positioning, Consumer<ModelBase> modelPositioning) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, modelPositioning, isDefault));
			return this;
		}
		
		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, boolean isDefault,
				Consumer<ModelBase> positioner) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner, isDefault));
			return this;
		}

		public Builder withSpawnEntityModel(ModelBase ammoModel) {
			this.ammoModel = ammoModel;
			return this;
		}

		public Builder withSpawnEntityModelTexture(String ammoModelTextureName) {
			this.ammoModelTextureName = modId + ":" + "textures/models/" + ammoModelTextureName + ".png";
			return this;
		}

		public Builder withSpawnEntityBlockImpactHandler(ImpactHandler impactHandler) {
			this.blockImpactHandler = impactHandler;
			return this;
		}

		public Builder withPumpTimeout(long pumpTimeoutMilliseconds) {
			this.pumpTimeoutMilliseconds = pumpTimeoutMilliseconds;
			return this;
		}

		public Builder withPellets(int pellets) {
			if (pellets < 1) {
				throw new IllegalArgumentException("Pellet count must be >= 1");
			}
			this.pellets = pellets;
			return this;
		}

		public Builder withFlashIntensity(float flashIntensity) {
			if (flashIntensity < 0f || flashIntensity > 1f) {
				throw new IllegalArgumentException("Invalid flash intencity");
			}
			this.flashIntensity = flashIntensity;
			return this;
		}

		public Weapon build(ModContext modContext) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}

			if (name == null) {
				throw new IllegalStateException("Weapon name not provided");
			}

			if (shootSound == null) {
				shootSound = name;
			}

			if (silencedShootSound == null) {
				silencedShootSound = shootSound;
			}

			if (reloadSound == null) {
				reloadSound = "reload";
			}

			if (unloadSound == null) {
				unloadSound = "unload";
			}

			if (spawnEntityClass == null) {
				spawnEntityClass = WeaponSpawnEntity.class;
			}

			if (spawnEntityWith == null) {

				spawnEntityWith = (weapon, player) -> {
					return compatibility.getSpawnEntity(weapon, compatibility.world(player), player, spawnEntitySpeed,
							spawnEntityGravityVelocity, inaccuracy, spawnEntityDamage, spawnEntityExplosionRadius);
				};
			}

			if (crosshairRunning == null) {
				crosshairRunning = crosshair;
			}

			if (crosshairZoomed == null) {
				crosshairZoomed = crosshair;
			}

			if (blockImpactHandler == null) {
				blockImpactHandler = (world, player, entity, position) -> {
					Block block = WorldHelper.getBlockAtPosition(world, position);
					if (WorldHelper.isGlassBlock(block)) {
						WorldHelper.destroyBlock(world, position);
					}
				};
			}
			
			if(maxBulletsPerReload == 0) {
				maxBulletsPerReload = ammoCapacity;
			}
			
			if(maxShots.isEmpty()) {
				maxShots.add(Integer.MAX_VALUE);
			}

			Weapon weapon = new Weapon(this, modContext);
			
			weapon.shootSound = modContext.registerSound(this.shootSound);
			weapon.reloadSound = modContext.registerSound(this.reloadSound);
			weapon.unloadSound = modContext.registerSound(this.unloadSound);
			weapon.silencedShootSound = modContext.registerSound(this.silencedShootSound);
			
			if(ejectSpentRoundSound != null) {
				weapon.ejectSpentRoundSound = modContext.registerSound(this.ejectSpentRoundSound);
			}
			
			weapon.setCreativeTab(creativeTab);
			weapon.setUnlocalizedName(name);
			if (ammo != null) {
				ammo.addCompatibleWeapon(weapon);
			}
			
			for (ItemAttachment<Weapon> attachment : this.compatibleAttachments.keySet()) {
				attachment.addCompatibleWeapon(weapon);
			}
			modContext.registerWeapon(name, weapon, renderer);
			return weapon;
		}
	}

	private static final long DEFAULT_RELOADING_TIMEOUT_TICKS = 10;
	private static final long DEFAULT_UNLOADING_TIMEOUT_TICKS = 10;
	static final long MAX_RELOAD_TIMEOUT_TICKS = 60;
	static final long MAX_UNLOAD_TIMEOUT_TICKS = 60;
	
	private static final float DEFAULT_ZOOM = 0.75f;
	
	private static final float DEFAULT_FIRE_RATE = 0.5f;
		
	Builder builder;
	
	private ModContext modContext;
	
	private CompatibleSound shootSound;
	private CompatibleSound silencedShootSound;
	private CompatibleSound reloadSound;
	private CompatibleSound unloadSound;
	private CompatibleSound ejectSpentRoundSound;

	public static enum State { READY, SHOOTING, RELOAD_REQUESTED, RELOAD_CONFIRMED, UNLOAD_STARTED, UNLOAD_REQUESTED_FROM_SERVER, UNLOAD_CONFIRMED, PAUSED, MODIFYING, EJECT_SPENT_ROUND};
	
	Weapon(Builder builder, ModContext modContext) {
		this.builder = builder;
		this.modContext = modContext;
		setMaxStackSize(1);
	}
	
	public String getName() {
		return builder.name;
	}

	public CompatibleSound getShootSound() {
		return shootSound;
	}

	public CompatibleSound getSilencedShootSound() {
		return silencedShootSound;
	}

	public CompatibleSound getReloadSound() {
		return reloadSound;
	}
	
	public CompatibleSound getUnloadSound() {
		return unloadSound;
	}
	
	public CompatibleSound getEjectSpentRoundSound() {
		return ejectSpentRoundSound;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack itemStack) {
		return true;
	}
	
	@Override
	protected ItemStack onCompatibleItemRightClick(ItemStack itemStack, World world, EntityPlayer player, boolean mainHand) {
		if(mainHand && world.isRemote) {
			toggleAiming();
		}
		return itemStack;
	}

	private void toggleAiming() {
		PlayerWeaponInstance mainHandHeldWeaponInstance = modContext.getMainHeldWeapon();
		if(mainHandHeldWeaponInstance != null && mainHandHeldWeaponInstance.getState() == WeaponState.READY) {
			mainHandHeldWeaponInstance.setAimed(!mainHandHeldWeaponInstance.isAimed());
		}
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean active) {
	}

	private void ensureItemStack(ItemStack itemStack) {
		if (compatibility.getTagCompound(itemStack) == null) {
			compatibility.setTagCompound(itemStack, new NBTTagCompound());
			Tags.setAmmo(itemStack, 0);
		}
	}
	
	static void toggleLaser(ItemStack itemStack) {
		Tags.setLaser(itemStack, !Tags.isLaserOn(itemStack));
	}
	
//	public static boolean isAimed(ItemStack itemStack) {
//		return Tags.isAimed(itemStack);
//	}
//
//	public static boolean isZoomed(EntityPlayer player, ItemStack itemStack) {
//		return Tags.getZoom(itemStack) != 1.0f;
//	}
	
	
	public void changeRecoil(EntityPlayer player, float factor) {
		PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
		if(instance != null) {
			float recoil = instance.getWeapon().builder.recoil * factor;
			logger.debug("Changing recoil to " + recoil + " for instance " + instance);
			instance.setRecoil(recoil);
		}
	}
	
	public void changeZoom(EntityPlayer player, float zoom, boolean attachmentOnlyMode) {
		PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
		if(instance != null) {
			instance.setZoom(zoom);
		}
//		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
//		if(itemStack != null) {
//			ensureItemStack(itemStack);
//			float zoom = builder.zoom * factor;
//			Tags.setAllowedZoom(itemStack, zoom, attachmentOnlyMode);
//		}
	}
	
	public void changeZoom(EntityPlayer player, float factor) {
		changeZoom(player, factor, false);
	}
	
	Map<ItemAttachment<Weapon>, CompatibleAttachment<Weapon>> getCompatibleAttachments() {
		return builder.compatibleAttachments;
	}

	String getCrosshair(PlayerWeaponInstance weaponInstance) {
		if(weaponInstance.isAimed()) {
			String crosshair = null;
			ItemAttachment<Weapon> scopeAttachment = modContext.getAttachmentAspect().getActiveAttachment(AttachmentCategory.SCOPE, weaponInstance);
			if(scopeAttachment != null) {
				crosshair = scopeAttachment.getCrosshair();
			}
			if(crosshair == null) {
				crosshair = builder.crosshairZoomed;
			}
			return crosshair;
		} else if(weaponInstance.getPlayer().isSprinting()){
			return builder.crosshairRunning;
		}
		return builder.crosshair;
	}
	
//	boolean isCrosshairFullScreen(ItemStack itemStack) {
//		if(isZoomed(null, itemStack)) {
//			return builder.crosshairZoomedFullScreen;
//		}
//		return builder.crosshairFullScreen;
//		
//	}
	
	public String getActiveTextureName(ItemStack itemStack) {
		ensureItemStack(itemStack);
		if(builder.textureNames.isEmpty()) {
			return null;
		}
		return builder.textureNames.get(Tags.getActiveTexture(itemStack));
	}
	
	public static boolean isActiveAttachment(ItemStack itemStack, ItemAttachment<Weapon> attachment) {
//		Weapon weapon = (Weapon) itemStack.getItem();
//		return weapon.modContext.getAttachmentAspect().isActiveAttachment(itemStack, attachment);
		PlayerWeaponInstance weaponInstance = Tags.getInstance(itemStack, PlayerWeaponInstance.class);
		return weaponInstance != null ? 
				WeaponAttachmentAspect.isActiveAttachment(attachment, weaponInstance) : false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 0;
	}
	
	int getCurrentAmmo(EntityPlayer player) {
		PlayerWeaponInstance state = modContext.getMainHeldWeapon();
		return state.getAmmo();
		
//		WeaponClientStorage storage = getWeaponClientStorage(player);
//		if(storage == null) return 0;
//		return storage.getCurrentAmmo().get();
		
	}

	int getAmmoCapacity() {
		return builder.ammoCapacity;
	}
	
	int getMaxBulletsPerReload() {
		return builder.maxBulletsPerReload;
	}
	
	ModelBase getAmmoModel() {
		return builder.ammoModel;
	}
	
	String getAmmoModelTextureName() {
		return builder.ammoModelTextureName;
	}
	
	void onSpawnEntityBlockImpact(World world, EntityPlayer player, WeaponSpawnEntity entity, CompatibleRayTraceResult position) {
		if(builder.blockImpactHandler != null) {
			builder.blockImpactHandler.onImpact(world, player, entity, position);
		}
	}
	
	@Override
	public List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(ItemStack itemStack) {
		return modContext.getAttachmentAspect().getActiveAttachments(itemStack);
	}
	
	long getUnloadTimeoutTicks() {
		return builder.unloadingTimeout;
	}
	
	boolean ejectSpentRoundRequired() {
		return builder.ejectSpentRoundRequired;
	}
	
	List<ItemMagazine> getCompatibleMagazines() {
		return builder.compatibleAttachments.keySet().stream()
				.filter(a -> a instanceof ItemMagazine)
				.map(a -> (ItemMagazine)a)
				.collect(Collectors.toList());
	}

	public WeaponRenderer getRenderer() {
		return builder.renderer;
	}
	
	List<ItemAttachment<Weapon>> getCompatibleAttachments(Class<? extends ItemAttachment<Weapon>> target) {
		return builder.compatibleAttachments.entrySet().stream()
				.filter(e -> target.isInstance(e.getKey()))
				.map(e -> e.getKey())
				.collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer,
			@SuppressWarnings("rawtypes") List list, boolean p_77624_4_) {
		if(list != null && builder.informationProvider != null) {
			list.addAll(builder.informationProvider.apply(itemStack));
		}
	}

	@Override
	public void reloadMainHeldItemForPlayer(EntityPlayer player) {
		modContext.getWeaponReloadAspect().reloadMainHeldItem(player);
	}

	@Override
	public void update(EntityPlayer player) {
		modContext.getWeaponReloadAspect().updateMainHeldItem(player);
		modContext.getWeaponFireAspect().onUpdate(player);
		modContext.getAttachmentAspect().updateMainHeldItem(player);
	}

	public void tryFire(EntityPlayer player) {
		modContext.getWeaponFireAspect().onFireButtonClick(player);
	}

	public void tryStopFire(EntityPlayer player) {
		modContext.getWeaponFireAspect().onFireButtonRelease(player);
	}

	@Override
	public PlayerWeaponInstance createItemInstance(EntityPlayer player, ItemStack itemStack, int slot){
		PlayerWeaponInstance instance = new PlayerWeaponInstance(slot, player, itemStack);
		//state.setAmmo(Tags.getAmmo(itemStack)); // TODO: get ammo properly
		instance.setState(WeaponState.READY);
		instance.setRecoil(builder.recoil);
		instance.setMaxShots(builder.maxShots.get(0));
		return instance;
	}

	@Override
	public void toggleClientAttachmentSelectionMode(EntityPlayer player) {
		modContext.getAttachmentAspect().toggleClientAttachmentSelectionMode(player);
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack itemStack, EntityPlayer player) {
		// Server side only method
		PlayerWeaponInstance instance = (PlayerWeaponInstance) Tags.getInstance(itemStack);
		return instance == null || instance.getState() == WeaponState.READY;
	}

	void changeFireMode(PlayerWeaponInstance instance) {
		int result;
		Iterator<Integer> it = builder.maxShots.iterator();
		while(it.hasNext()) {
			if(instance.getMaxShots() == it.next()) {
				break;
			}
		}
		if(it.hasNext()) {
			result = it.next();
		} else {
			result = builder.maxShots.get(0);
		}
		
		instance.setMaxShots(result);
		String message;
		if(result == 1) {
			message = "Semi";
		} else if(result == Integer.MAX_VALUE) {
			message = "Auto";
		} else {
			message = "Burst";
		}
		logger.debug("Changed fire mode of " + instance + " to " + result);
		
		instance.getPlayer().addChatMessage(new ChatComponentText("Fire mode changed to " + message));
	}
}
