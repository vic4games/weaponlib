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
import net.minecraft.client.resources.model.ModelResourceLocation;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		private WeaponRenderer renderer;
		float zoom = Weapon.DEFAULT_ZOOM;
		int maxShots = Integer.MAX_VALUE;
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

		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}

		public Builder withReloadingTime(long reloadingTime) {
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

		public Builder withRenderer(WeaponRenderer renderer) {
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
						protected float getVelocity() {
							return spawnEntitySpeed;
						}

						@Override
						protected float getInaccuracy() {
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
			
					Block block = world.getBlockState(position.getBlockPos()).getBlock();
					if (block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass
							|| block == Blocks.stained_glass_pane) {
						//world.func_147480_a(position.getBlockPos().getX(), position.getBlockPos().getY(), position.getBlockPos().getZ(), true);
						world.destroyBlock(position.getBlockPos(), true);
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
			
			modContext.registerWeapon(name, weapon);
			return weapon;
		}
	}

	private static final UUID SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID = UUID.fromString("8efa8469-0256-4f8e-bdd9-3e7b23970663");
	private static final AttributeModifier SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER = (new AttributeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID, "Slow Down While Zooming", -0.5, 2)).setSaved(false);

	private static final long DEFAULT_RELOADING_TIMEOUT_TICKS = 10;
	static final long MAX_RELOAD_TIMEOUT_TICKS = 60;
	
	private static final float DEFAULT_ZOOM = 0.75f;
	
	private static final float DEFAULT_FIRE_RATE = 0.5f;
		
	Builder builder;
	
	private ModContext modContext;

	public static enum State { READY, SHOOTING, RELOAD_REQUESTED, RELOAD_CONFIRMED, PAUSED, MODIFYING };
	
	Weapon(Builder builder, ModContext modContext) {
		this.builder = builder;
		this.modContext = modContext;
		setMaxStackSize(1);
	}
	
	public String getName() {
		return builder.name;
	}
	
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
		builder.renderer.setOwner(player);
		return super.getModel(stack, player, useRemaining);
	}

//	@Override
//	public void registerIcons(IIconRegister register) {}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack itemStack) {
		return true;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		toggleAiming(itemStack, entityPlayer);
		return itemStack;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		return true;
	}

	void toggleAiming(ItemStack itemStack, EntityPlayer entityPlayer) {
		
		ensureItemStack(itemStack);
		if(Weapon.isModifying(itemStack)) {
			return;
		}
		float currentZoom = Tags.getZoom(itemStack);
		
		if (currentZoom != 1.0f || entityPlayer.isSprinting()) {
			Tags.setZoom(itemStack, 1.0f);
			Tags.setAimed(itemStack, false);
			restoreNormalSpeed(entityPlayer);
		} else {
			float allowedZoom = Tags.getAllowedZoom(itemStack);
			if(allowedZoom > 0f) {
				Tags.setZoom(itemStack, allowedZoom);
			} else {
				allowedZoom = builder.zoom;
				Tags.setAllowedZoom(itemStack, allowedZoom);
				Tags.setZoom(itemStack, allowedZoom);
			}
			slowDown(entityPlayer);
			Tags.setAimed(itemStack, true);
		}
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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean active) {
		ensureItemStack(itemStack);
		float currentZoom = Tags.getZoom(itemStack);
		if (currentZoom != 1.0f && entity.isSprinting()) {
			Tags.setZoom(itemStack, 1.0f);
			Tags.setAimed(itemStack, false);
			restoreNormalSpeed((EntityPlayer) entity);
		}
	}

	private void ensureItemStack(ItemStack itemStack) {
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
			Tags.setAmmo(itemStack, 0);
			Tags.setZoom(itemStack, 1.0f);
			Tags.setRecoil(itemStack, builder.recoil);
			setModifying(itemStack, false);
		}
	}
	
	static void toggleLaser(ItemStack itemStack) {
		Tags.setLaser(itemStack, !Tags.isLaserOn(itemStack));
	}
	
	public static boolean isAimed(ItemStack itemStack) {
		return Tags.isAimed(itemStack);
	}

	public static boolean isZoomed(ItemStack itemStack) {
		return Tags.getZoom(itemStack) != 1.0f;
	}
	
	static void setModifying(ItemStack itemStack, boolean modifying) {
		if(modifying) {
			Tags.setState(itemStack, State.MODIFYING);
		} else {
			Tags.setState(itemStack, State.READY);
		}
	}
	
	static boolean isModifying(ItemStack itemStack) {
		return Tags.getState(itemStack) == State.MODIFYING;
	}
	
	public void changeRecoil(EntityPlayer player, float factor) {
		ItemStack itemStack = player.getHeldItem();
		ensureItemStack(itemStack);
		float recoil = builder.recoil * factor;
		Tags.setRecoil(itemStack, recoil);
		modContext.getChannel().sendTo(new ChangeSettingsMessage(this, recoil), (EntityPlayerMP) player);
	}
	
	void clientChangeRecoil(EntityPlayer player, float recoil) {
		WeaponClientStorage weaponInstanceStorage = getWeaponClientStorage(player);
		if(weaponInstanceStorage != null) {
			weaponInstanceStorage.setRecoil(recoil);
		}
	}
	
	public void changeZoom(EntityPlayer player, float factor) {
		ItemStack itemStack = player.getHeldItem();
		ensureItemStack(itemStack);
		float zoom = builder.zoom * factor;
		Tags.setAllowedZoom(itemStack, zoom);
		//modContext.getChannel().sendTo(ChangeSettingsMessage.createChangeZoomMessage(this, zoom), (EntityPlayerMP) player);
	}
	
//	void clientChangeZoom(EntityPlayer player, float zoom) {
//		WeaponClientStorage weaponInstanceStorage = getWeaponClientStorage(player);
//		if(weaponInstanceStorage != null) {
//			weaponInstanceStorage.setZoom(zoom);
//		}
//	}

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
		return builder.textureNames.get(Tags.getActiveTexture(itemStack));
	}
	
	public static boolean isActiveAttachment(ItemStack itemStack, ItemAttachment<Weapon> attachment) {
		Weapon weapon = (Weapon) itemStack.getItem();
		return weapon.modContext.getAttachmentManager().isActiveAttachment(itemStack, attachment);
	}
	
	static boolean isReloadingConfirmed(EntityPlayer player, ItemStack itemStack) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponClientStorage storage = weapon.getWeaponClientStorage(player);
		return storage != null && storage.getState() == State.RELOAD_CONFIRMED;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 0;
	}
	
	WeaponClientStorage getWeaponClientStorage(EntityPlayer player) {
		return modContext.getWeaponClientStorageManager().getWeaponClientStorage(player, this);
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

	public WeaponRenderer getRenderer() {
		return builder.renderer;
	}
}
