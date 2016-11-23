package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.IItemRenderer;

public class Builder {

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