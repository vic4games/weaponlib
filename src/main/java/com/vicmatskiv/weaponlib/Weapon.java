package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

public class Weapon extends Item {
	
	public static class Builder {

		private static final float DEFAULT_SPAWN_ENTITY_SPEED = 10f;
		
		String name;
		List<String> textureNames = new ArrayList<>();
		int ammoCapacity = 1;
		float recoil = 1.0F;
		String shootSound;
		String silencedShootSound;
		String reloadSound;
		@SuppressWarnings("unused")
		private String exceededMaxShotsSound;
		ItemAmmo ammo;
		float fireRate = Weapon.DEFAULT_FIRE_RATE;
		private CreativeTabs creativeTab;
		private IItemRenderer renderer;
		float zoom = Weapon.DEFAULT_ZOOM;
		int maxShots = Integer.MAX_VALUE;
		String crosshair;
		String crosshairRunning;
		String crosshairZoomed;
		BiFunction<Weapon, EntityPlayer, ? extends WeaponSpawnEntity> spawnEntityWith;
		private float spawnEntityDamage;
		private float spawnEntityExplosionRadius;
		private float spawnEntityGravityVelocity;
		int reloadingTimeout = Weapon.DEFAULT_RELOADING_TIMEOUT_TICKS;
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

		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}

		public Builder withReloadingTime(int reloadingTime) {
			this.reloadingTimeout = reloadingTime;
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

		public Builder withMaxShots(int maxShots) {
			this.maxShots = maxShots;
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
			this.shootSound = modId + ":" + shootSound;
			return this;
		}

		public Builder withSilencedShootSound(String silencedShootSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.silencedShootSound = modId + ":" + silencedShootSound;
			return this;
		}

		public Builder withReloadSound(String reloadSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.reloadSound = modId + ":" + reloadSound;
			return this;
		}

		public Builder withExceededMaxShotsSound(String shootSound) {
			if (modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.exceededMaxShotsSound = modId + ":" + shootSound;
			return this;
		}

		public Builder withCreativeTab(CreativeTabs creativeTab) {
			this.creativeTab = creativeTab;
			return this;
		}

		// public Builder withSpawnEntity(Function<EntityPlayer, WeaponSpawnEntity>
		// spawnEntityWith) {
		// this.spawnEntityWith = spawnEntityWith;
		// return this;
		// }

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

		public Builder withRenderer(IItemRenderer renderer) {
			this.renderer = renderer;
			return this;
		}

		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, Consumer<ModelBase> positioner) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner));
			return this;
		}

		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, boolean isDefault,
				Consumer<ModelBase> positioner) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner, isDefault));
			return this;
		}

		public Builder withCompatibleAttachment(AttachmentCategory category, ModelBase attachmentModel, String textureName,
				Consumer<ModelBase> positioner) {
			ItemAttachment<Weapon> item = new ItemAttachment<>(modId, category, attachmentModel, textureName, null);
			compatibleAttachments.put(item, new CompatibleAttachment<>(item, positioner));
			return this;
		}

		public Builder withCompatibleAttachment(AttachmentCategory category, ModelBase attachmentModel, String textureName,
				String crosshair, Consumer<ModelBase> positioner) {
			ItemAttachment<Weapon> item = new ItemAttachment<>(modId, category, attachmentModel, textureName, crosshair);
			compatibleAttachments.put(item, new CompatibleAttachment<>(item, positioner));
			return this;
		}

		public Builder withCompatibleAttachment(CompatibleAttachment<Weapon> compatibleAttachment) {
			compatibleAttachments.put(compatibleAttachment.getAttachment(), compatibleAttachment);
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

			// if(textureName == null) {
			// textureName = modId + ":" + name;
			// }

			if (shootSound == null) {
				shootSound = modId + ":" + name;
			}

			if (silencedShootSound == null) {
				silencedShootSound = shootSound;
			}

			if (reloadSound == null) {
				reloadSound = modId + ":" + "reload";
			}

			if (spawnEntityClass == null) {
				spawnEntityClass = WeaponSpawnEntity.class;
			}

			if (spawnEntityWith == null) {

				spawnEntityWith = (weapon, player) -> {
					WeaponSpawnEntity spawnEntity = new WeaponSpawnEntity(weapon, player.worldObj, player, spawnEntitySpeed,
							spawnEntityGravityVelocity, spawnEntityDamage, spawnEntityExplosionRadius) {

						@Override
						protected float getGravityVelocity() {
							return spawnEntityGravityVelocity;
						}

						@Override
						protected float func_70182_d() {
							return spawnEntitySpeed;
						}

						@Override
						float getInaccuracy() {
							return inaccuracy;
						}

					};

					return spawnEntity;
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
					Block block = world.getBlock(position.blockX, position.blockY, position.blockZ);
					if (block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass
							|| block == Blocks.stained_glass_pane) {
						world.func_147480_a(position.blockX, position.blockY, position.blockZ, true);
					}
				};
			}

			Weapon weapon = new Weapon(this, modContext);
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

	
	private static final UUID SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID = UUID.fromString("8efa8469-0256-4f8e-bdd9-3e7b23970663");
	private static final AttributeModifier SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER = (new AttributeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID, "Slow Down While Zooming", -0.5, 2)).setSaved(false);

	private static final String SHOT_COUNTER_TAG = "ShotCounter";
	private static final String ZOOM_TAG = "Zoomed";
	private static final String RECOIL_TAG = "Recoil";
	private static final String AIMED_TAG = "Aimed";
	private static final String STOP_TIMER_TAG = "StopTimer";
	private static final String RESUME_TIMER_TAG = "ResumeTimer";
	private static final String RELOADING_TIMER_TAG = "ReloadingTimer";
	private static final String ACTIVE_TEXTURE_INDEX_TAG = "ActiveTextureIndex";
	private static final String LASER_ON_TAG = "LaserOn";
	private static final String AMMO_TAG = "Ammo";
	static final String PERSISTENT_STATE_TAG = "PersistentState";
	public static final String STATE_TAG = "State";

	static final int DEFAULT_RELOADING_TIMEOUT_TICKS = 10;
	static final float DEFAULT_ZOOM = 0.75f;
	static final float DEFAULT_FIRE_RATE = 0.5f;
	
	static final int STATE_READY = 0;
	static final int STATE_MODIFYING = 4;
	
	static final int INFINITE_AMMO = -1;
	
	private static final long MAX_RELOAD_TIMEOUT_TICKS = 60;
	
	Builder builder;
	
	private ModContext modContext;

	public static enum WeaponInstanceState { READY, SHOOTING, RELOAD_REQUESTED, RELOAD_CONFIRMED, PAUSED, MODIFYING };
	
	Weapon(Builder builder, ModContext modContext) {
		this.builder = builder;
		this.modContext = modContext;
		setMaxStackSize(1);
	}
	
	public String getName() {
		return builder.name;
	}

	@Override
	public void registerIcons(IIconRegister register) {}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack itemStack) {
		return true;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		ensureItemStack(itemStack);
		
		float currentZoom = getZoom(itemStack);
		
		if (currentZoom != 1.0f || entityPlayer.isSprinting()) {
			setZoom(itemStack, 1.0f);
			setAimed(itemStack, false);
			restoreNormalSpeed(entityPlayer);
		} else {
			WeaponClientStorage weaponInstanceStorage = getWeaponClientStorage(entityPlayer);
			if(weaponInstanceStorage != null) {
				setZoom(itemStack, weaponInstanceStorage.getZoom());
			}
			slowDown(entityPlayer);
			setAimed(itemStack, true);
		}
		
		return super.onItemRightClick(itemStack, world, entityPlayer);
	}

	private static void restoreNormalSpeed(EntityPlayer entityPlayer) {
		if(entityPlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.getModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER.getID()) != null) {
			entityPlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.removeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		} else {
			System.err.println("Attempted to remove modifier that was not applied: " + SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		}
	}

	private static void slowDown(EntityPlayer entityPlayer) {
		if(entityPlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.getModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER.getID()) == null) {
			entityPlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.applyModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		}  else {
			System.err.println("Attempted to add duplicate modifier: " + SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		}
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean active) {
		ensureItemStack(itemStack);
		float currentZoom = getZoom(itemStack);
		if (currentZoom != 1.0f && entity.isSprinting()) {
			setZoom(itemStack, 1.0f);
			setAimed(itemStack, false);
			restoreNormalSpeed((EntityPlayer) entity);
		}
	}

	private void ensureItemStack(ItemStack itemStack) {
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
			setAmmo(itemStack, 0);
			itemStack.stackTagCompound.setInteger(SHOT_COUNTER_TAG, 0);
			setZoom(itemStack, 1.0f);
			setRecoil(itemStack, builder.recoil);
			itemStack.stackTagCompound.setLong(STOP_TIMER_TAG, 0);
			itemStack.stackTagCompound.setLong(RESUME_TIMER_TAG, 0);
			setState(itemStack, STATE_READY);
		}
	}
	
	static boolean isLaserOn(ItemStack itemStack) {
		if(itemStack.stackTagCompound == null) {
			return false;
		}
		return itemStack.stackTagCompound.getBoolean(LASER_ON_TAG);
	}
	
	static void setLaser(ItemStack itemStack, boolean enabled) {
		itemStack.stackTagCompound.setBoolean(LASER_ON_TAG, enabled);
	}

	static void toggleLaser(ItemStack itemStack) {
		setLaser(itemStack, !isLaserOn(itemStack));
	}
	
	static int getAmmo(ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger(AMMO_TAG);
	}
	
	static void setAmmo(ItemStack itemStack, int ammo) {
		itemStack.stackTagCompound.setInteger(AMMO_TAG, ammo);
	}
	
	static void setAimed(ItemStack itemStack, boolean aimed) {
		itemStack.stackTagCompound.setBoolean(AIMED_TAG, aimed);
	}
	
	public static boolean isAimed(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() instanceof Weapon && 
				itemStack.stackTagCompound != null && itemStack.stackTagCompound.getBoolean(AIMED_TAG);
	}

	static float getZoom(ItemStack itemStack) {
		return itemStack.stackTagCompound.getFloat(ZOOM_TAG);
	}
	
	static void setZoom(ItemStack itemStack, float zoom) {
		itemStack.stackTagCompound.setFloat(ZOOM_TAG, zoom);
	}
	
	public static boolean isZoomed(ItemStack itemStack) {
		return itemStack.stackTagCompound != null && getZoom(itemStack) != 1.0f;
	}
	
	static void setActiveTexture(ItemStack itemStack, int currentIndex) {
		itemStack.stackTagCompound.setInteger(ACTIVE_TEXTURE_INDEX_TAG, currentIndex);
	}

	static int getActiveTexture(ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger(ACTIVE_TEXTURE_INDEX_TAG);
	}
	
	static void setRecoil(ItemStack itemStack, float recoil) {
		itemStack.stackTagCompound.setFloat(RECOIL_TAG, recoil);
	}
	
	static float getRecoil(ItemStack itemStack) {
		return itemStack.stackTagCompound.getFloat(RECOIL_TAG);
	}
	
	int getState(ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger(STATE_TAG);
	}
	
	void setState(ItemStack itemStack, int newState) {
		itemStack.stackTagCompound.setInteger(STATE_TAG, newState);
	}
	
	public void changeRecoil(EntityPlayer player, float factor) {
		ItemStack itemStack = player.getHeldItem();
		ensureItemStack(itemStack);
		float recoil = builder.recoil * factor;
		setRecoil(itemStack, recoil);
		modContext.getChannel().sendTo(new ChangeSettingsMessage(this, recoil), (EntityPlayerMP) player);
	}
	
	void clientChangeRecoil(EntityPlayer player, float recoil) {
		WeaponClientStorage weaponInstanceStorage = getWeaponClientStorage(player);
		if(weaponInstanceStorage != null) {
			weaponInstanceStorage.setRecoil(recoil);
		}
	}
	
	public void changeZoom(EntityPlayer player, float factor) {
		WeaponClientStorage weaponInstanceStorage = getWeaponClientStorage(player);
		if(weaponInstanceStorage != null) {
			weaponInstanceStorage.setZoom(builder.zoom * factor);
		}
	}

	Map<ItemAttachment<Weapon>, CompatibleAttachment<Weapon>> getCompatibleAttachments() {
		return builder.compatibleAttachments;
	}

	String getCrosshair(ItemStack itemStack, EntityPlayer thePlayer) {
		if(isZoomed(itemStack)) {
			String crosshair = null;
			ItemAttachment<Weapon> scopeAttachment = modContext.getAttachmentManager().getActiveAttachment(itemStack, AttachmentCategory.SCOPE);
			if(scopeAttachment != null) {
				crosshair = scopeAttachment.getCrosshair();
			}
			if(crosshair == null) {
				crosshair = builder.crosshairZoomed;
			}
			return crosshair;
		} else if(thePlayer.isSprinting()){
			return builder.crosshairRunning;
		}
		return builder.crosshair;
	}
	
	boolean isCrosshairFullScreen(ItemStack itemStack) {
		if(isZoomed(itemStack)) {
			return builder.crosshairZoomedFullScreen;
		}
		return builder.crosshairFullScreen;
		
	}
	
	String getActiveTextureName(ItemStack itemStack) {
		ensureItemStack(itemStack);
		if(builder.textureNames.isEmpty()) {
			return null;
		}
		return builder.textureNames.get(getActiveTexture(itemStack));
	}
	
	public static boolean isActiveAttachment(ItemStack itemStack, ItemAttachment<Weapon> attachment) {
		Weapon weapon = (Weapon) itemStack.getItem();
		return weapon.modContext.getAttachmentManager().isActiveAttachment(itemStack, attachment);
	}
	
	static boolean isReloadingConfirmed(EntityPlayer player, ItemStack itemStack) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = weapon.getWeaponClientStorage(player);
		return storage != null && storage.getState() == WeaponInstanceState.RELOAD_CONFIRMED;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 0;
	}
	
	WeaponClientStorage getWeaponClientStorage(EntityPlayer player) {
		return modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, this);
	}
	
	void clientTryFire(EntityPlayer player) {
		
		WeaponClientStorage storage = getWeaponClientStorage(player);
		if(storage == null) return;
		
		boolean readyToShootAccordingToFireRate = System.currentTimeMillis() - storage.getLastShotFiredAt() >= 50f / builder.fireRate;
		if(!player.isSprinting() 
				&& (storage.getState() == WeaponInstanceState.READY || storage.getState() == WeaponInstanceState.SHOOTING)
				&& readyToShootAccordingToFireRate
				&& storage.getShots() < builder.maxShots
				&& storage.getCurrentAmmo().getAndAccumulate(0, (current, ignore) -> current > 0 ? current - 1 : 0) > 0) {
			storage.setState(WeaponInstanceState.SHOOTING);
			modContext.getChannel().sendToServer(new TryFireMessage(true));
			ItemStack heldItem = player.getHeldItem();
			
			modContext.runSyncTick(() -> {
				player.playSound(modContext.getAttachmentManager().isSilencerOn(heldItem) ? builder.silencedShootSound : builder.shootSound, 1F, 1F);
			});
			
			player.rotationPitch = player.rotationPitch - storage.getRecoil();						
			float rotationYawFactor = -1.0f + itemRand.nextFloat() * 2.0f;
			player.rotationYaw = player.rotationYaw + storage.getRecoil() * rotationYawFactor;
			
			if(builder.flashIntensity > 0) {
				EffectManager.getInstance().spawnFlashParticle(player, builder.flashIntensity);
			}
			
			EffectManager.getInstance().spawnSmokeParticle(player);
			
			storage.setLastShotFiredAt(System.currentTimeMillis());
			
			storage.addShot();
		}
	}

	void tryFire(EntityPlayer player, ItemStack itemStack) {
		int currentAmmo = getAmmo(itemStack);
		if(currentAmmo > 0) {
			if(!isZoomed(itemStack)) {
				setAimed(itemStack, true);
			}
			setAmmo(itemStack, currentAmmo - 1);
			for(int i = 0; i < builder.pellets; i++) {
				player.worldObj.spawnEntityInWorld(builder.spawnEntityWith.apply(this, player));
			}
			player.worldObj.playSoundToNearExcept(player, modContext.getAttachmentManager().isSilencerOn(itemStack) ? builder.silencedShootSound : builder.shootSound, 1.0F, 1.0F);
		} else {
			System.err.println("Invalid state: attempted to fire a weapon without ammo");
		}
	}
	
	void tryStopFire(EntityPlayer player, ItemStack itemStack) {
		if(!isZoomed(itemStack)) {
			setAimed(itemStack, false);
		}
	}

	void clientTryStopFire(EntityPlayer player) {
		WeaponClientStorage storage = getWeaponClientStorage(player);
		if(storage == null) return;
		if(storage.getState() == WeaponInstanceState.SHOOTING) {
			storage.resetShots();
			if(storage.getLastShotFiredAt() + builder.pumpTimeoutMilliseconds <= System.currentTimeMillis()) {
				storage.setState(WeaponInstanceState.READY);
			} else {
				storage.setState(WeaponInstanceState.PAUSED);
			}
			modContext.getChannel().sendToServer(new TryFireMessage(false));
		}
	}

	// Client
	void initiateReload(ItemStack itemStack, EntityPlayer player) {
		WeaponClientStorage storage = getWeaponClientStorage(player);
		if(storage == null) return;
		if(storage.getState() != WeaponInstanceState.RELOAD_REQUESTED 
				&& storage.getState() != WeaponInstanceState.RELOAD_CONFIRMED &&storage.getCurrentAmmo().get() < builder.ammoCapacity) {
			storage.getReloadingStopsAt().set(player.worldObj.getTotalWorldTime() + MAX_RELOAD_TIMEOUT_TICKS);
			storage.setState(WeaponInstanceState.RELOAD_REQUESTED);
			modContext.getChannel().sendToServer(new ReloadMessage(this));
		}
	}
	
	// Client
	void completeReload(ItemStack itemStack, EntityPlayer player, int ammo, boolean quietly) {
		WeaponClientStorage storage = getWeaponClientStorage(player);
		if(storage == null) return;
		if(storage.getState() == WeaponInstanceState.RELOAD_REQUESTED) {
			storage.getCurrentAmmo().set(ammo);
			if(ammo > 0 && !quietly) {
				storage.setState(WeaponInstanceState.RELOAD_CONFIRMED);
				long reloadingStopsAt = player.worldObj.getTotalWorldTime() + builder.reloadingTimeout;
				storage.getReloadingStopsAt().set(reloadingStopsAt);
				player.playSound(builder.reloadSound, 1.0F, 1.0F);
			} else {
				storage.setState(WeaponInstanceState.READY);
			}
		}
	}
	
	// Server
	void reload(ItemStack itemStack, EntityPlayer player) {
		if (itemStack.stackTagCompound != null && !player.isSprinting()) {
			if (player.inventory.consumeInventoryItem(builder.ammo)) {
				long totalWorldTime = player.worldObj.getTotalWorldTime();
				itemStack.stackTagCompound.setLong(RELOADING_TIMER_TAG, totalWorldTime + builder.reloadingTimeout);
				//setState(itemStack, STATE_RELOADING);
				setAmmo(itemStack, builder.ammoCapacity);
				modContext.getChannel().sendTo(new ReloadMessage(this, builder.ammoCapacity), (EntityPlayerMP) player);
				player.worldObj.playSoundToNearExcept(player, builder.reloadSound, 1.0F, 1.0F);
			} else {
				setAmmo(itemStack, 0);
				modContext.getChannel().sendTo(new ReloadMessage(this, 0), (EntityPlayerMP) player);
			}
		}
	}

	// Client
	void tick(EntityPlayer player) {
		WeaponClientStorage storage = getWeaponClientStorage(player);
		if(storage != null) {
			
			if(storage.getState() == WeaponInstanceState.RELOAD_REQUESTED || storage.getState() == WeaponInstanceState.RELOAD_CONFIRMED) {
				long totalWorldTime = player.worldObj.getTotalWorldTime();
				if(storage.getReloadingStopsAt().get() <= totalWorldTime) {
					storage.setState(WeaponInstanceState.READY);
				}
			} else if(storage.getState() == WeaponInstanceState.PAUSED 
					&& storage.getLastShotFiredAt() + builder.pumpTimeoutMilliseconds <= System.currentTimeMillis()) {
				
				storage.setState(WeaponInstanceState.READY);
			}
		}
	}
	
	int getCurrentAmmo(EntityPlayer player) {
		WeaponClientStorage storage = getWeaponClientStorage(player);
		if(storage == null) return 0;
		return storage.getCurrentAmmo().get();
	}

	int getAmmoCapacity() {
		return builder.ammoCapacity;
	}
	
	ModelBase getAmmoModel() {
		return builder.ammoModel;
	}
	
	String getAmmoModelTextureName() {
		return builder.ammoModelTextureName;
	}
	
	void onSpawnEntityBlockImpact(World world, EntityPlayer player, WeaponSpawnEntity entity, MovingObjectPosition position) {
		if(builder.blockImpactHandler != null) {
			builder.blockImpactHandler.onImpact(world, player, entity, position);
		}
	}

	List<CompatibleAttachment<Weapon>> getActiveAttachments(ItemStack itemStack) {
		return modContext.getAttachmentManager().getActiveAttachments(itemStack);
	}
}
