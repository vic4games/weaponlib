package com.vicmatskiv.weaponlib;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Weapon extends Item {
	
	private static final UUID SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID = UUID.fromString("8efa8469-0256-4f8e-bdd9-3e7b23970663");
	private static final AttributeModifier SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER = (new AttributeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID, "Slow Down While Zooming", -0.5, 2)).setSaved(false);

	private static final String SHOT_COUNTER_TAG = "ShotCounter";
	private static final String ZOOM_TAG = "Zoomed";
	private static final String RECOIL_TAG = "Recoil";
	private static final String AIMED_TAG = "Aimed";
		
	static final int DEFAULT_RELOADING_TIMEOUT_TICKS = 10;
	
	private static final String STOP_TIMER_TAG = "StopTimer";
	private static final String RESUME_TIMER_TAG = "ResumeTimer";
	private static final String RELOADING_TIMER_TAG = "ReloadingTimer";
	private static final String ACTIVE_TEXTURE_INDEX_TAG = "ActiveTextureIndex";
	private static final String LASER_ON_TAG = "LaserOn";
	
	private static final String AMMO_TAG = "Ammo";
	static final String PERSISTENT_STATE_TAG = "PersistentState";
	
	static final float DEFAULT_ZOOM = 0.75f;
	static final float DEFAULT_FIRE_RATE = 0.5f;
	
	public static final String STATE_TAG = "State";
	
	static final int STATE_READY = 0;
	static final int STATE_SHOOTING = 1;
	static final int STATE_PAUSED = 2;
	static final int STATE_RELOADING = 3;
	static final int STATE_MODIFYING = 4;
	
	static final int INFINITE_AMMO = -1;
	
	private static final long MAX_RELOAD_TIMEOUT_TICKS = 60;
	
	private Builder builder;
	
	private ModContext modContext;

	public static enum WeaponInstanceState { READY, SHOOTING, RELOAD_REQUESTED, RELOAD_CONFIRMED, PAUSED, MODIFYING };
	
	private Map<UUID, WeaponInstanceStorage> weaponInstanceStorages = new IdentityHashMap<>();

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
			entityPlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		} else {
			WeaponInstanceStorage weaponInstanceStorage = getWeaponInstanceStorage(entityPlayer);
			if(weaponInstanceStorage != null) {
				setZoom(itemStack, weaponInstanceStorage.getZoom());
			}
			entityPlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);

			setAimed(itemStack, true);
		}
		
		return super.onItemRightClick(itemStack, world, entityPlayer);
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean active) {
		ensureItemStack(itemStack);
		float currentZoom = getZoom(itemStack);
		if (currentZoom != 1.0f && entity.isSprinting()) {
			setZoom(itemStack, 1.0f);
			setAimed(itemStack, false);
			((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		}
	}
	
	public static boolean isZoomed(ItemStack itemStack) {
		return itemStack.stackTagCompound != null && getZoom(itemStack) != 1.0f;
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
	
	static void setRecoil(ItemStack itemStack, float recoil) {
		itemStack.stackTagCompound.setFloat(RECOIL_TAG, recoil);
	}
	
	static float getRecoil(ItemStack itemStack) {
		return itemStack.stackTagCompound.getFloat(RECOIL_TAG);
	}
	
	public void changeRecoil(EntityPlayer player, float factor) {
		ItemStack itemStack = player.getHeldItem();
		ensureItemStack(itemStack);
		float recoil = builder.recoil * factor;
		setRecoil(itemStack, recoil);
		modContext.getChannel().sendTo(new ChangeSettingsMessage(this, recoil), (EntityPlayerMP) player);
	}
	
	void clientChangeRecoil(EntityPlayer player, float recoil) {
		WeaponInstanceStorage weaponInstanceStorage = getWeaponInstanceStorage(player);
		if(weaponInstanceStorage != null) {
			weaponInstanceStorage.setRecoil(recoil);
		}
	}
	
	public void changeZoom(EntityPlayer player, float factor) {
		WeaponInstanceStorage weaponInstanceStorage = getWeaponInstanceStorage(player);
		if(weaponInstanceStorage != null) {
			weaponInstanceStorage.setZoom(builder.zoom * factor);
		}
	}
	
	int getState(ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger(STATE_TAG);
	}
	
	void setState(ItemStack itemStack, int newState) {
		itemStack.stackTagCompound.setInteger(STATE_TAG, newState);
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
	
	void changeTexture(ItemStack itemStack, EntityPlayer player) {
		ensureItemStack(itemStack);
		int currentIndex = itemStack.stackTagCompound.getInteger(ACTIVE_TEXTURE_INDEX_TAG);
		if(builder.textureNames.isEmpty()) {
			return;
		}
		if(currentIndex >= builder.textureNames.size() - 1) {
			currentIndex = 0;
		} else {
			currentIndex++;
		}
		itemStack.stackTagCompound.setInteger(ACTIVE_TEXTURE_INDEX_TAG, currentIndex);
	}
	
	String getActiveTextureName(ItemStack itemStack) {
		ensureItemStack(itemStack);
		if(builder.textureNames.isEmpty()) {
			return null;
		}
		return builder.textureNames.get(itemStack.stackTagCompound.getInteger(ACTIVE_TEXTURE_INDEX_TAG));
	}
	
	public static boolean isActiveAttachment(ItemStack itemStack, ItemAttachment<Weapon> attachment) {
		Weapon weapon = (Weapon) itemStack.getItem();
		return weapon.modContext.getAttachmentManager().isActiveAttachment(itemStack, attachment);
	}
	
	static boolean isReloadingConfirmed(EntityPlayer player, ItemStack itemStack) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponInstanceStorage storage = weapon.getWeaponInstanceStorage(player);
		return storage != null && storage.getState() == WeaponInstanceState.RELOAD_CONFIRMED;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 0;
	}

	WeaponInstanceStorage getWeaponInstanceStorage(EntityPlayer player) {
		if(player == null) return null;
		
		return weaponInstanceStorages.computeIfAbsent(player.getPersistentID(), (w) ->
			player.getHeldItem().stackTagCompound != null ?
					new WeaponInstanceStorage(WeaponInstanceState.values()[player.getHeldItem().stackTagCompound.getInteger(PERSISTENT_STATE_TAG)], 
					getAmmo(player.getHeldItem()), builder.zoom, 
					getRecoil(player.getHeldItem()), builder.fireRate) : null);
	}
	
	void clientTryFire(EntityPlayer player) {
		
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
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
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
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
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
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
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
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
				setState(itemStack, STATE_RELOADING);
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
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
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
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
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

	public boolean isLaserOn(ItemStack itemStack) {
		if(itemStack.stackTagCompound == null) {
			return false;
		}
		return itemStack.stackTagCompound.getBoolean(LASER_ON_TAG);
	}

	public void switchLaser(ItemStack itemStack) {
		ensureItemStack(itemStack);
		boolean current = itemStack.stackTagCompound.getBoolean(LASER_ON_TAG);
		itemStack.stackTagCompound.setBoolean(LASER_ON_TAG, !current);
	}

	List<CompatibleAttachment<Weapon>> getActiveAttachments(ItemStack itemStack) {
		return modContext.getAttachmentManager().getActiveAttachments(itemStack);
	}
}
