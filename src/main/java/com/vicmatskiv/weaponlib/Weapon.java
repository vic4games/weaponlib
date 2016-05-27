package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
	
	private static final String ACTIVE_ATTACHMENT_TAG = "ActiveAttachments";
	private static final String PREVIOUSLY_SELECTED_ATTACHMENT_TAG = "PreviouslySelectedAttachments";
	
	private static final String SHOT_COUNTER_TAG = "ShotCounter";
	public static final String ZOOM_TAG = "Zoomed";
	public static final String RECOIL_TAG = "Recoil";
	static final String AIMED_TAG = "Aimed";
	
	private static final int RESUME_TIMEOUT_TICKS = 4;
	
	private static final int DEFAULT_RELOADING_TIMEOUT_TICKS = 10;
	
	@SuppressWarnings("unused")
	private static final String RECOIL_TIMER_TAG = "RecoilTimer";
	
	private static final String STOP_TIMER_TAG = "StopTimer";
	private static final String RESUME_TIMER_TAG = "ResumeTimer";
	private static final String RELOADING_TIMER_TAG = "ReloadingTimer";
	private static final String ACTIVE_TEXTURE_INDEX_TAG = "ActiveTextureIndex";
	
	private static final String AMMO_TAG = "Ammo";
	private static final String PERSISTENT_STATE_TAG = "PersistentState";
	
	private static final float DEFAULT_ZOOM = 0.75f;
	private static final float DEFAULT_FIRE_RATE = 0.5f;
	
	public static final String STATE_TAG = "State";
	
	public static final int STATE_READY = 0;
	public static final int STATE_SHOOTING = 1;
	public static final int STATE_PAUSED = 2;
	public static final int STATE_RELOADING = 3;
	public static final int STATE_MODIFYING = 4;
	
	public static final int VIEW_BOBBING_ON = 1;
	public static final int VIEW_BOBBING_OFF = 2;
	
	public static final int INFINITE_AMMO = -1;
	
	private static final long MAX_RELOAD_TIMEOUT_TICKS = 60;
	
	public static class Builder {
		
		private static final float DEFAULT_SPAWN_ENTITY_SPEED = 10f;
		private String name;
		private List<String> textureNames = new ArrayList<>();
		private int ammoCapacity = 1;
		private float recoil = 1.0F;
		private String shootSound;
		private String silencedShootSound;
		private String reloadSound;
		@SuppressWarnings("unused")
		private String exceededMaxShotsSound;
		private ItemAmmo ammo;
		private float fireRate = DEFAULT_FIRE_RATE;
		private CreativeTabs creativeTab;
		private IItemRenderer renderer;
		private float zoom = DEFAULT_ZOOM;
		private int maxShots = Integer.MAX_VALUE;
		private String crosshair;
		private String crosshairRunning;
		private String crosshairZoomed;
		private BiFunction<Weapon, EntityPlayer, ? extends WeaponSpawnEntity> spawnEntityWith;
		private float spawnEntityDamage;
		private float spawnEntityExplosionRadius;
		private float spawnEntityGravityVelocity;
		private int reloadingTimeout = DEFAULT_RELOADING_TIMEOUT_TICKS;
		private String modId;
		@SuppressWarnings("unused")
		private int resumeTimeout = RESUME_TIMEOUT_TICKS;
		
		private boolean crosshairFullScreen = false;
		private boolean crosshairZoomedFullScreen = false;
		
		private Map<ItemAttachment<Weapon>, CompatibleAttachment<Weapon>> compatibleAttachments = new HashMap<>();
		private ModelBase ammoModel;
		private String ammoModelTextureName;
		
		private float spawnEntitySpeed = DEFAULT_SPAWN_ENTITY_SPEED;
		private Class<? extends WeaponSpawnEntity> spawnEntityClass;
		private ImpactHandler blockImpactHandler;
		private long pumpTimeoutMilliseconds;
		
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
			if(fireRate >= 1 || fireRate <= 0) {
				throw new IllegalArgumentException("Invalid fire rate " + fireRate);
			}
			this.fireRate = fireRate;
			return this;
		}
		
		public Builder withTextureNames(String...textureNames) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			for(String textureName: textureNames) {
				this.textureNames.add(textureName + ".png");
			}
			return this;
		}
		
		public Builder withCrosshair(String crosshair) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshair = modId + ":" + "textures/crosshairs/" + crosshair + ".png";
			return this;
		}
		
		public Builder withCrosshair(String crosshair, boolean fullScreen) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshair = modId + ":" + "textures/crosshairs/" + crosshair + ".png";
			this.crosshairFullScreen = fullScreen;
			return this;
		}
		
		public Builder withCrosshairRunning(String crosshairRunning) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshairRunning = modId + ":" + "textures/crosshairs/" + crosshairRunning + ".png";
			return this;
		}
		
		public Builder withCrosshairZoomed(String crosshairZoomed) {
			return withCrosshairZoomed(crosshairZoomed, true);
		}
		
		public Builder withCrosshairZoomed(String crosshairZoomed, boolean fullScreen) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.crosshairZoomed = modId + ":" + "textures/crosshairs/" + crosshairZoomed + ".png";
			this.crosshairZoomedFullScreen = fullScreen;
			return this;
		}
		
		public Builder withShootSound(String shootSound) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.shootSound = modId + ":" + shootSound;
			return this;
		}
		
		public Builder withSilencedShootSound(String silencedShootSound) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.silencedShootSound = modId + ":" + silencedShootSound;
			return this;
		}
		
		public Builder withReloadSound(String reloadSound) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.reloadSound = modId + ":" + reloadSound;
			return this;
		}
		
		public Builder withExceededMaxShotsSound(String shootSound) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			this.exceededMaxShotsSound = modId + ":" + shootSound;
			return this;
		}
		
		public Builder withCreativeTab(CreativeTabs creativeTab) {
			this.creativeTab = creativeTab;
			return this;
		}
		
//		public Builder withSpawnEntity(Function<EntityPlayer, WeaponSpawnEntity> spawnEntityWith) {
//			this.spawnEntityWith = spawnEntityWith;
//			return this;
//		}
		
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

		public Builder withRenderer(IItemRenderer renderer) {
			this.renderer = renderer;
			return this;
		}
		
		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, Consumer<ModelBase> positioner) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner));
			return this;
		}
		
		public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, boolean isDefault, Consumer<ModelBase> positioner) {
			compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner, isDefault));
			return this;
		}
		
		public Builder withCompatibleAttachment(AttachmentCategory category, ModelBase attachmentModel, String textureName,
				Consumer<ModelBase> positioner) {
			ItemAttachment<Weapon> item = new ItemAttachment<>(modId, category, attachmentModel, textureName, null);
			compatibleAttachments.put(item, new CompatibleAttachment<>(item, positioner));
			return this;
		}
		
		public Builder withCompatibleAttachment(AttachmentCategory category, ModelBase attachmentModel, String textureName, String crosshair,
				Consumer<ModelBase> positioner) {
			ItemAttachment<Weapon> item = new ItemAttachment<>(modId, category, attachmentModel, textureName, crosshair);
			compatibleAttachments.put(item, new CompatibleAttachment<>(item, positioner));
			return this;
		}
		
		public Builder withCompatibleAttachment(CompatibleAttachment<Weapon> compatibleAttachment) {
			compatibleAttachments.put(compatibleAttachment.getAttachment(), compatibleAttachment);
			return this;
		}
		
		public Builder withResumeTimeout(int resumeTimeout) {
			this.resumeTimeout = resumeTimeout;
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
		
//		public Builder withSpawnEntityClass(Class<? extends WeaponSpawnEntity> spawnEntityClass) {
//			this.spawnEntityClass = spawnEntityClass;
//			return this;
//		}
		
//		public Builder withSpawnEntity(Function<EntityPlayer, ? extends WeaponSpawnEntity> spawnEntityWith) {
//			this.spawnEntityWith = spawnEntityWith;
//			return this;
//		}
		
		public Weapon build(ModContext modContext) {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			
			if(name == null) {
				throw new IllegalStateException("Weapon name not provided");
			}
			
//			if(textureName == null) {
//				textureName = modId + ":" + name;
//			}
			
			if(shootSound == null) {
				shootSound = modId + ":" + name;
			}
			
			if(silencedShootSound == null) {
				silencedShootSound = shootSound;
			}
			
			if(reloadSound == null) {
				reloadSound = modId + ":" + "reload";
			}
			
			if(spawnEntityClass == null) {
				spawnEntityClass = WeaponSpawnEntity.class;
			}
			
			if(spawnEntityWith == null) {
				spawnEntityWith = (weapon, player) -> new WeaponSpawnEntity(weapon, player.worldObj, player, spawnEntitySpeed,
						spawnEntityGravityVelocity, spawnEntityDamage, spawnEntityExplosionRadius) {

							@Override
							protected float getGravityVelocity() {
								return spawnEntityGravityVelocity;
							}

							@Override
							protected float func_70182_d() {
								return spawnEntitySpeed;
							}
					
				};
			}
			
			if(crosshairRunning == null) {
				crosshairRunning = crosshair;
			}
			
			if(crosshairZoomed == null) {
				crosshairZoomed = crosshair;
			}
			
			if(blockImpactHandler == null) {
				blockImpactHandler = (world, player, entity, position) -> {
					Block block = world.getBlock(position.blockX, position.blockY, position.blockZ);
					if(block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass 
							|| block == Blocks.stained_glass_pane) {
						world.func_147480_a(position.blockX, position.blockY, position.blockZ, true);
					}
				 };
			}
			
			Weapon weapon = new Weapon(this, modContext);
			weapon.setCreativeTab(creativeTab);
			weapon.setUnlocalizedName(name);
			if(ammo != null) {
				ammo.addCompatibleWeapon(weapon);
			}
			for(ItemAttachment<Weapon> attachment: this.compatibleAttachments.keySet()) {
				attachment.addCompatibleWeapon(weapon);
			}
			modContext.registerWeapon(name, weapon, renderer);
			return weapon;
		}

	}
	
	private Builder builder;
	
	private ModContext modContext;

	private Weapon(Builder builder, ModContext modContext) {
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
		ensureItemStack(itemStack);
		float currentZoom = itemStack.stackTagCompound.getFloat(ZOOM_TAG);
		if (currentZoom != 1.0f || entityLiving.isSprinting()) {
			itemStack.stackTagCompound.setFloat(ZOOM_TAG, 1.0f);
			itemStack.stackTagCompound.setBoolean(AIMED_TAG, false);
		} else {
			WeaponInstanceStorage weaponInstanceStorage = getWeaponInstanceStorage((EntityPlayer) entityLiving);
			if(weaponInstanceStorage != null) {
				itemStack.stackTagCompound.setFloat(ZOOM_TAG, weaponInstanceStorage.getZoom());
			}
			
			itemStack.stackTagCompound.setBoolean(AIMED_TAG, true);
		}
		return true;
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean active) {
		float currentZoom = itemStack.stackTagCompound.getFloat(ZOOM_TAG);
		if (currentZoom != 1.0f && entity.isSprinting()) {
			itemStack.stackTagCompound.setFloat(ZOOM_TAG, 1.0f);
			itemStack.stackTagCompound.setBoolean(AIMED_TAG, false);
		}
	}
	
	public static boolean isZoomed(ItemStack itemStack) {
		return itemStack.stackTagCompound != null && itemStack.stackTagCompound.getFloat(ZOOM_TAG) != 1.0f;
	}

	private void ensureItemStack(ItemStack itemStack) {
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
			itemStack.stackTagCompound.setInteger(AMMO_TAG, 0);
			itemStack.stackTagCompound.setInteger(SHOT_COUNTER_TAG, 0);
			itemStack.stackTagCompound.setFloat(ZOOM_TAG, 1.0f);
			itemStack.stackTagCompound.setFloat(RECOIL_TAG, builder.recoil);
			itemStack.stackTagCompound.setLong(STOP_TIMER_TAG, 0);
			itemStack.stackTagCompound.setLong(RESUME_TIMER_TAG, 0);
			setState(itemStack, STATE_READY);
		}
	}
	
	public void changeRecoil(EntityPlayer player, float factor) {
		ItemStack itemStack = player.getHeldItem();
		ensureItemStack(itemStack);
		//float recoil = itemStack.stackTagCompound.getFloat(RECOIL_TAG) * factor;
		float recoil = builder.recoil * factor;
		itemStack.stackTagCompound.setFloat(RECOIL_TAG, recoil);
		modContext.getChannel().sendTo(new ChangeSettingsMessage(this, recoil), (EntityPlayerMP) player);
		//builder.recoil = builder.recoil * factor;
	}
	
	public void clientChangeRecoil(EntityPlayer player, float recoil) {
		
		WeaponInstanceStorage weaponInstanceStorage = getWeaponInstanceStorage(player);
		if(weaponInstanceStorage != null) {
			weaponInstanceStorage.recoil = recoil;
		}
	}
	
	public void changeZoom(EntityPlayer player, float factor) {
		WeaponInstanceStorage weaponInstanceStorage = getWeaponInstanceStorage(player);
		if(weaponInstanceStorage != null) {
			weaponInstanceStorage.zoom = builder.zoom * factor;
		}
		//builder.zoom = builder.zoom * factor;
	}
	
	int getShotCount(ItemStack itemStack) {
		if(itemStack.stackTagCompound != null) {
			return itemStack.stackTagCompound.getInteger(SHOT_COUNTER_TAG);
		}
		return 0;
	}
	
	int getMaxShots() {
		return builder.maxShots;
	}
	
	int getState(ItemStack itemStack) {
		return itemStack.stackTagCompound.getInteger(STATE_TAG);
	}

	void setState(ItemStack itemStack, int newState) {
		itemStack.stackTagCompound.setInteger(STATE_TAG, newState);
	}
	
	void enterAttachmentSelectionMode(ItemStack itemStack) {
		ensureItemStack(itemStack);
		int activeAttachmentsIds[] = ensureActiveAttachments(itemStack);
		itemStack.stackTagCompound.setIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG, 
				Arrays.copyOf(activeAttachmentsIds, activeAttachmentsIds.length));
		
		((Weapon) itemStack.getItem()).setState(itemStack, Weapon.STATE_MODIFYING);
		itemStack.stackTagCompound.setInteger(PERSISTENT_STATE_TAG, WeaponInstanceState.MODIFYING.ordinal());
	}
	
	void exitAttachmentSelectionMode(ItemStack itemStack, EntityPlayer player) {
		ensureItemStack(itemStack);
		
		int activeAttachmentsIds[] = itemStack.stackTagCompound.getIntArray(ACTIVE_ATTACHMENT_TAG);
		int previouslySelectedAttachmentIds[] = itemStack.stackTagCompound.getIntArray(PREVIOUSLY_SELECTED_ATTACHMENT_TAG);
		for(int i = 0; i < activeAttachmentsIds.length; i++) {
			if(activeAttachmentsIds[i] != previouslySelectedAttachmentIds[i]) {
				Item newItem = Item.getItemById(activeAttachmentsIds[i]);
				Item oldItem = Item.getItemById(previouslySelectedAttachmentIds[i]);
				player.inventory.consumeInventoryItem(newItem);
				if(!player.inventory.addItemStackToInventory(new ItemStack(oldItem))) {
					System.err.println("Cannot add item back to the inventory: " + oldItem);
				}
			}
		}
		
		((Weapon) itemStack.getItem()).setState(itemStack, Weapon.STATE_READY);
		itemStack.stackTagCompound.setInteger(PERSISTENT_STATE_TAG, WeaponInstanceState.READY.ordinal());
	}

	String getCrosshair(ItemStack itemStack, EntityPlayer thePlayer) {
		if(isZoomed(itemStack)) {
			String crosshair = null;
			ItemAttachment<Weapon> scopeAttachment = getActiveAttachment(itemStack, AttachmentCategory.SCOPE);
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
	
	boolean isCrosshairZoomedFullScreen() {
		return builder.crosshairZoomedFullScreen;
	}
	
	void changeTexture(ItemStack itemStack, EntityPlayer player) {
		ensureItemStack(itemStack);
		//System.out.println("Weapon changing texture");
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
	
	@SuppressWarnings("unchecked")
	void changeAttachment(AttachmentCategory attachmentCategory, ItemStack itemStack, EntityPlayer player) {
		ensureItemStack(itemStack);
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[attachmentCategory.ordinal()];
		ItemAttachment<Weapon> item = null;
		if(activeAttachmentIdForThisCategory > 0) {
			item = (ItemAttachment<Weapon>) Item.getItemById(activeAttachmentIdForThisCategory);
			if(item != null && item.getRemove() != null) {
				item.getRemove().apply(item, this, player);
			}
		}
		
		ItemAttachment<Weapon> nextAttachment = nextCompatibleAttachment(attachmentCategory, item, player);

		if(nextAttachment != null && nextAttachment.getApply() != null) {
			nextAttachment.getApply().apply(nextAttachment, this, player);
		}
		
		activeAttachmentsIds[attachmentCategory.ordinal()] = Item.getIdFromItem(nextAttachment);;
		
		itemStack.stackTagCompound.setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
	}
	
	private ItemAttachment<Weapon> nextCompatibleAttachment(AttachmentCategory category, Item currentAttachment, EntityPlayer player) {
		
		ItemAttachment<Weapon> nextAttachment = null;
		boolean foundCurrent = false;
		for (int i = 0; i < 36; i++) {
			ItemStack itemStack = player.inventory.getStackInSlot(i);
			if(itemStack != null) {
				if(itemStack.getItem() instanceof ItemAttachment) {
					@SuppressWarnings("unchecked")
					ItemAttachment<Weapon> attachmentItemFromInventory = (ItemAttachment<Weapon>) itemStack.getItem();
					
					if(attachmentItemFromInventory.getCategory() == category 
							&& builder.compatibleAttachments.containsKey(attachmentItemFromInventory)) {

						if(foundCurrent || currentAttachment == null) {
							nextAttachment = attachmentItemFromInventory;
							break;
						} else if(currentAttachment == attachmentItemFromInventory) {
							foundCurrent = true;
						}
					}
				}
			}
			
		}
		
		return nextAttachment;
	}
	
	public ItemAttachment<Weapon> getActiveAttachment (ItemStack itemStack, AttachmentCategory category) {
		ensureItemStack(itemStack);
		
		ItemAttachment<Weapon> itemAttachment = null;
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<Weapon> compatibleAttachment = builder.compatibleAttachments.get(item);
				if(compatibleAttachment != null && category == compatibleAttachment.getAttachment().getCategory()) {
					itemAttachment = compatibleAttachment.getAttachment();
					break;
				}
			}
			
		}
		return itemAttachment;
	}
	
	List<CompatibleAttachment<Weapon>> getActiveAttachments (ItemStack itemStack) {
		ensureItemStack(itemStack);
		
		List<CompatibleAttachment<Weapon>> activeAttachments = new ArrayList<>();
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<Weapon> compatibleAttachment = builder.compatibleAttachments.get(item);
				if(compatibleAttachment != null) {
					activeAttachments.add(compatibleAttachment);
				}
				
			}
			
		}
		return activeAttachments;
	}

	private int[] ensureActiveAttachments(ItemStack itemStack) {
		int activeAttachmentsIds[] = itemStack.stackTagCompound.getIntArray(ACTIVE_ATTACHMENT_TAG);
		
		if(activeAttachmentsIds == null || activeAttachmentsIds.length != AttachmentCategory.values.length) {
			activeAttachmentsIds = new int[AttachmentCategory.values.length];
			itemStack.stackTagCompound.setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
			for(CompatibleAttachment<Weapon> attachment: this.builder.compatibleAttachments.values()) {
				if(attachment.isDefault()) {
					activeAttachmentsIds[attachment.getAttachment().getCategory().ordinal()] = Item.getIdFromItem(attachment.getAttachment());
				}
			}
		}
		return activeAttachmentsIds;
	}
	
	public static boolean isActiveAttachment(ItemStack itemStack, ItemAttachment<Weapon> attachment) {
		Weapon weapon = (Weapon) itemStack.getItem();
		int[] activeAttachmentsIds = weapon.ensureActiveAttachments(itemStack);
		return Arrays.stream(activeAttachmentsIds).anyMatch((attachmentId) -> attachment == Item.getItemById(attachmentId));
	}
	
	private boolean isSilencerOn(ItemStack itemStack) {
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[AttachmentCategory.SILENCER.ordinal()];
		return activeAttachmentIdForThisCategory > 0;
	}
	
	public static float reloadingProgress(EntityPlayer player, ItemStack itemStack) {
		Weapon weapon = (Weapon) itemStack.getItem();
		WeaponInstanceStorage storage = weapon.getWeaponInstanceStorage(player);
		if(storage != null && storage.getState() == WeaponInstanceState.RELOADING) {
			long totalWorldTime = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
			long timeLeftToReload = storage.reloadingStopsAt.get() - totalWorldTime;
			float progress = timeLeftToReload > 0 ? 1.0f - (float)timeLeftToReload / weapon.builder.reloadingTimeout : 0;
			return progress;
		} else {
			return 0;
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack p_77626_1_) {
		return 0;
	}
	
	public static boolean isAimed(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() instanceof Weapon && 
				itemStack.stackTagCompound != null && itemStack.stackTagCompound.getBoolean(Weapon.AIMED_TAG);
	}

	Random random = new Random();
	
	public static enum WeaponInstanceState { READY, SHOOTING, RELOADING, PAUSED, MODIFYING };
	
	private Map<UUID, WeaponInstanceStorage> weaponInstanceStorages = new IdentityHashMap<>();
	
	static class WeaponInstanceStorage {
		private AtomicInteger currentAmmo;
		private AtomicLong reloadingStopsAt;
		private long lastShotFiredAt;
		private int shots;
		private float zoom;
		private float recoil;
		
		private AtomicReference<WeaponInstanceState> state;

		public WeaponInstanceStorage(WeaponInstanceState state, int currentAmmo, float zoom, float recoil) {
			this.currentAmmo = new AtomicInteger(currentAmmo);
			this.reloadingStopsAt = new AtomicLong();
			this.state = new AtomicReference<>(state);
			this.zoom = zoom;
			this.recoil = recoil;
		}

		public WeaponInstanceState getState() {
			return state.get();
		}

		public void setState(WeaponInstanceState state) {
			this.state.set(state);
		}
		
		public float getZoom() {
			return zoom;
		}
		
		public void setZoom(float zoom) {
			this.zoom = zoom;
		}

		public float getRecoil() {
			return recoil;
		}

		public void setRecoil(float recoil) {
			this.recoil = recoil;
		}
	}
	
	private WeaponInstanceStorage getWeaponInstanceStorage(EntityPlayer player) {
		
		if(player == null) return null;
		return weaponInstanceStorages.computeIfAbsent(player.getPersistentID(), (w) ->
			player.getHeldItem().stackTagCompound != null ?
					new WeaponInstanceStorage(WeaponInstanceState.values()[player.getHeldItem().stackTagCompound.getInteger(PERSISTENT_STATE_TAG)], 
					player.getHeldItem().stackTagCompound.getInteger(AMMO_TAG), builder.zoom, player.getHeldItem().stackTagCompound.getFloat(RECOIL_TAG)) : null);
	}

	public void clientTryFire(EntityPlayer player) {
		
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
		if(storage == null) return;
		
		boolean readyToShootAccordingToFireRate = System.currentTimeMillis() - storage.lastShotFiredAt >= 50f / builder.fireRate;
		if(!player.isSprinting() 
				&& (storage.getState() == WeaponInstanceState.READY || storage.getState() == WeaponInstanceState.SHOOTING)
				&& readyToShootAccordingToFireRate
				&& storage.shots < builder.maxShots
				&& storage.currentAmmo.getAndAccumulate(0, (current, ignore) -> current > 0 ? current - 1 : 0) > 0) {
			storage.setState(WeaponInstanceState.SHOOTING);
			modContext.getChannel().sendToServer(new TryFireMessage(true));
			modContext.runSyncTick(() -> player.playSound(isSilencerOn(player.getHeldItem()) ? builder.silencedShootSound : builder.shootSound, 1F, 1F));
			
			player.rotationPitch = player.rotationPitch - storage.recoil;						
			float rotationYawFactor = -1.0f + random.nextFloat() * 2.0f;
			//System.out.println("Recoil: " + storage.recoil);
			player.rotationYaw = player.rotationYaw + storage.recoil * rotationYawFactor;
			storage.lastShotFiredAt = System.currentTimeMillis();
			storage.shots++;
		}
	}


	public void tryFire(EntityPlayer player, ItemStack itemStack) {
		int currentAmmo = itemStack.stackTagCompound.getInteger(AMMO_TAG);
		if(currentAmmo > 0) {
			if(!isZoomed(itemStack)) {
				itemStack.stackTagCompound.setBoolean(AIMED_TAG, true);
			}
			itemStack.stackTagCompound.setInteger(AMMO_TAG, currentAmmo - 1);
			player.worldObj.spawnEntityInWorld(builder.spawnEntityWith.apply(this, player));
			player.worldObj.playSoundToNearExcept(player, isSilencerOn(itemStack) ? builder.silencedShootSound : builder.shootSound, 1.0F, 1.0F);
		} else {
			System.err.println("Invalid state: attempted to fire a weapon without ammo");
		}
	}
	
	public void tryStopFire(EntityPlayer player, ItemStack itemStack) {
		if(!isZoomed(itemStack)) {
			itemStack.stackTagCompound.setBoolean(AIMED_TAG, false);
		}
	}

	public void clientTryStopFire(EntityPlayer player) {
		//EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
		if(storage == null) return;
		//System.out.println("Trying to stop fire");
		if(storage.getState() == WeaponInstanceState.SHOOTING) {
			storage.shots = 0;
			if(storage.lastShotFiredAt + builder.pumpTimeoutMilliseconds <= System.currentTimeMillis()) {
				//System.out.println("Timeout passed, getting ready");
				storage.setState(WeaponInstanceState.READY);
			} else {
				storage.setState(WeaponInstanceState.PAUSED);
				//System.out.println("Timeout not passed, waiting");
			}
			modContext.getChannel().sendToServer(new TryFireMessage(false));
		}
	}

	// Client
	public void initiateReload(ItemStack itemStack, EntityPlayer player) {
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
		if(storage == null) return;
		if(storage.getState() != WeaponInstanceState.RELOADING && storage.currentAmmo.get() < builder.ammoCapacity) {
			storage.reloadingStopsAt.set(player.worldObj.getTotalWorldTime() + MAX_RELOAD_TIMEOUT_TICKS);
			storage.setState(WeaponInstanceState.RELOADING);
			modContext.getChannel().sendToServer(new ReloadMessage(this));
		}
	}
	
	// Client
	public void completeReload(ItemStack itemStack, EntityPlayer player, int ammo, boolean quietly) {
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
		if(storage == null) return;
		if(storage.getState() == WeaponInstanceState.RELOADING) {
			storage.currentAmmo.set(ammo);
			if(ammo > 0 && !quietly) {
				long reloadingStopsAt = player.worldObj.getTotalWorldTime() + builder.reloadingTimeout;
				storage.reloadingStopsAt.set(reloadingStopsAt);
				player.playSound(builder.reloadSound, 1.0F, 1.0F);
			} else {
				storage.setState(WeaponInstanceState.READY);
			}
		}
	}
	
	// Server
	public void reload(ItemStack itemStack, EntityPlayer player) {
		if (itemStack.stackTagCompound != null && !player.isSprinting()) {
			if (player.inventory.consumeInventoryItem(builder.ammo)) {
				long totalWorldTime = player.worldObj.getTotalWorldTime();
				itemStack.stackTagCompound.setLong(RELOADING_TIMER_TAG, totalWorldTime + builder.reloadingTimeout);
				setState(itemStack, STATE_RELOADING);
				itemStack.stackTagCompound.setInteger(AMMO_TAG, builder.ammoCapacity);
				modContext.getChannel().sendTo(new ReloadMessage(this, builder.ammoCapacity), (EntityPlayerMP) player);
				player.worldObj.playSoundToNearExcept(player, builder.reloadSound, 1.0F, 1.0F);
			} else {
				itemStack.stackTagCompound.setInteger(AMMO_TAG, 0);
				modContext.getChannel().sendTo(new ReloadMessage(this, 0), (EntityPlayerMP) player);
			}
		}
	}

	// Client
	public void tick(EntityPlayer player) {
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
		if(storage != null) {
			if(storage.getState() == WeaponInstanceState.RELOADING) {
				long totalWorldTime = player.worldObj.getTotalWorldTime();
				if(storage.reloadingStopsAt.get() <= totalWorldTime) {
					storage.setState(WeaponInstanceState.READY);
				}
			} else if(storage.getState() == WeaponInstanceState.PAUSED 
					&& storage.lastShotFiredAt + builder.pumpTimeoutMilliseconds <= System.currentTimeMillis()) {
				
				//System.out.println("Timeout passed, getting ready");
				storage.setState(WeaponInstanceState.READY);
			}
		}
	}

	public void switchClientAttachmentSelectionMode(ItemStack itemStack, EntityPlayer player) {
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
		if(storage == null) return;
		if(storage.getState() != WeaponInstanceState.MODIFYING) {
			storage.setState(WeaponInstanceState.MODIFYING);
		} else {
			storage.setState(WeaponInstanceState.READY);
		}
    	modContext.getChannel().sendToServer(new AttachmentModeMessage());
	}
	
	public int getCurrentAmmo(EntityPlayer player) {
		WeaponInstanceStorage storage = getWeaponInstanceStorage(player);
		if(storage == null) return 0;
		return storage.currentAmmo.get();
	}

	public int getAmmoCapacity() {
		return builder.ammoCapacity;
	}

	public ItemAmmo getAmmo() {
		return builder.ammo;
	}
	
	public ModelBase getAmmoModel() {
		return builder.ammoModel;
	}
	
	public String getAmmoModelTextureName() {
		return builder.ammoModelTextureName;
	}
	
	void onSpawnEntityBlockImpact(World world, EntityPlayer player, WeaponSpawnEntity entity, MovingObjectPosition position) {
		if(builder.blockImpactHandler != null) {
			builder.blockImpactHandler.onImpact(world, player, entity, position);
		}
	}
}