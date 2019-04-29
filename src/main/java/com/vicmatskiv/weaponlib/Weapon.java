package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleItem;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTargetPoint;
import com.vicmatskiv.weaponlib.config.Gun;
import com.vicmatskiv.weaponlib.crafting.CraftingComplexity;
import com.vicmatskiv.weaponlib.crafting.OptionsMetadata;
import com.vicmatskiv.weaponlib.model.Shell;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Weapon extends CompatibleItem implements PlayerItemInstanceFactory<PlayerWeaponInstance, WeaponState>, 
AttachmentContainer, Reloadable, Inspectable, Modifiable, Updatable {

    private static final Logger logger = LogManager.getLogger(Weapon.class);

    public static enum ShellCasingEjectDirection { LEFT, RIGHT };

    public static class Builder {

        private static final float DEFAULT_SPAWN_ENTITY_SPEED = 10f;
        private static final float DEFAULT_INACCURACY = 1f;
        private static final String DEFAULT_SHELL_CASING_TEXTURE_NAME = "weaponlib:/com/vicmatskiv/weaponlib/resources/shell.png";
        private static final float DEFAULT_SHELL_CASING_VELOCITY = 0.1f;
        private static final float DEFAULT_SHELL_CASING_GRAVITY_VELOCITY = 0.05f;
        private static final float DEFAULT_SHELL_CASING_INACCURACY = 20f;
       

        String name;
        List<String> textureNames = new ArrayList<>();
        int ammoCapacity = 0;
        float recoil = 1.0F;

        private String shootSound;
        private String silencedShootSound;
        private String reloadSound;
        private String reloadIterationSound;
        private String inspectSound;
        private String drawSound;
        private String allReloadIterationsCompletedSound;
        private String unloadSound;
        private String ejectSpentRoundSound;
        private String endOfShootSound;
        private String burstShootSound;
        private String silencedBurstShootSound;

        @SuppressWarnings("unused")
        private String exceededMaxShotsSound;
        ItemAmmo ammo;
        float fireRate = Weapon.DEFAULT_FIRE_RATE;
        private CreativeTabs creativeTab;
        private WeaponRenderer renderer;
        //float zoom = Weapon.DEFAULT_ZOOM;
        List<Integer> maxShots = new ArrayList<>();
        String crosshair;
        String crosshairRunning;
        String crosshairZoomed;
        BiFunction<Weapon, EntityLivingBase, ? extends WeaponSpawnEntity> spawnEntityWith;
        BiFunction<PlayerWeaponInstance, EntityLivingBase, ? extends EntityShellCasing> spawnShellWith;
        private float spawnEntityDamage;
        private float spawnEntityExplosionRadius;
        private boolean isDestroyingBlocks = true;
        private float spawnEntityGravityVelocity;
        long reloadingTimeout = Weapon.DEFAULT_RELOADING_TIMEOUT_TICKS;
        long loadIterationTimeout = Weapon.DEFAULT_LOAD_ITERATION_TIMEOUT_TICKS;

        private String modId;

        boolean crosshairFullScreen = false;
        boolean crosshairZoomedFullScreen = false;

        Map<ItemAttachment<Weapon>, CompatibleAttachment<Weapon>> compatibleAttachments = new HashMap<>();
        ModelBase ammoModel;
        String ammoModelTextureName;
        ModelBase shellCasingModel;
        String shellCasingModelTextureName;

        private float spawnEntitySpeed = DEFAULT_SPAWN_ENTITY_SPEED;
        private Class<? extends WeaponSpawnEntity> spawnEntityClass;
        ImpactHandler blockImpactHandler;
        long pumpTimeoutMilliseconds;
        long burstTimeoutMilliseconds = Weapon.DEFAULT_BURST_TIMEOUT_MILLISECONDS;

        private float inaccuracy = DEFAULT_INACCURACY;

        int pellets = 1;

        float flashIntensity = 0.2f;

        Supplier<Float> flashScale = () -> 1f;

        Supplier<Float> flashOffsetX = () -> 0f;

        Supplier<Float> flashOffsetY = () -> 0f;

        Supplier<Float> smokeOffsetX = () -> 0f;

        Supplier<Float> smokeOffsetY = () -> 0f;

        long unloadingTimeout = Weapon.DEFAULT_UNLOADING_TIMEOUT_TICKS;

        private boolean ejectSpentRoundRequired;

        public int maxBulletsPerReload;

        private Function<ItemStack, List<String>> informationProvider;

        private CraftingComplexity craftingComplexity;

        private Object[] craftingMaterials;

        private float shellCasingForwardOffset = Weapon.DEFAULT_SHELL_CASING_FORWARD_OFFSET;

        private float shellCasingVerticalOffset = Weapon.DEFAULT_SHELL_CASING_VERTICAL_OFFSET;

        private float shellCasingSideOffset = Weapon.DEFAULT_SHELL_CASING_SIDE_OFFSET;

        private float shellCasingSideOffsetAimed = Weapon.DEFAULT_SHELL_CASING_SIDE_OFFSET_AIMED;

        public boolean shellCasingEjectEnabled = true;
        
        private boolean hasIteratedLoad;

        private ShellCasingEjectDirection shellCasingEjectDirection = ShellCasingEjectDirection.RIGHT;

        private float silencedShootSoundVolume = Weapon.DEFAULT_SILENCED_SHOOT_SOUND_VOLUME;
        private float shootSoundVolume = Weapon.DEFAULT_SHOOT_SOUND_VOLUME;
        private Object[] craftingRecipe;
        public boolean isOneClickBurstAllowed;
        String flashTexture;
        
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
        
        public Builder withIteratedLoad() {
            this.hasIteratedLoad = true;
            return this;
        }

        public Builder withRecoil(float recoil) {
            this.recoil = recoil;
            return this;
        }

        @Deprecated
        public Builder withZoom(float zoom) {
            //this.zoom = zoom;
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
        
        public Builder withOneClickBurst() {
            this.isOneClickBurstAllowed = true;
            return this;
        }
        
        public Builder withBurstTimeout(long burstTimeoutMilliseconds) {
            this.burstTimeoutMilliseconds = burstTimeoutMilliseconds;
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
                this.textureNames.add(textureName.toLowerCase() + ".png");
            }
            return this;
        }

        public Builder withCrosshair(String crosshair) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.crosshair = modId + ":" + "textures/crosshairs/" + crosshair.toLowerCase() + ".png";
            return this;
        }

        public Builder withCrosshair(String crosshair, boolean fullScreen) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.crosshair = modId + ":" + "textures/crosshairs/" + crosshair.toLowerCase() + ".png";
            this.crosshairFullScreen = fullScreen;
            return this;
        }

        public Builder withCrosshairRunning(String crosshairRunning) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.crosshairRunning = modId + ":" + "textures/crosshairs/" + crosshairRunning.toLowerCase() + ".png";
            return this;
        }

        public Builder withCrosshairZoomed(String crosshairZoomed) {
            return withCrosshairZoomed(crosshairZoomed, true);
        }

        public Builder withCrosshairZoomed(String crosshairZoomed, boolean fullScreen) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.crosshairZoomed = modId + ":" + "textures/crosshairs/" + crosshairZoomed.toLowerCase() + ".png";
            this.crosshairZoomedFullScreen = fullScreen;
            return this;
        }

        public Builder withShootSound(String shootSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.shootSound = shootSound.toLowerCase(); //modId + ":" + shootSound;
            return this;
        }
        
        public Builder withEndOfShootSound(String endOfShootSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.endOfShootSound = endOfShootSound.toLowerCase(); //modId + ":" + shootSound;
            return this;
        }

        public Builder withEjectSpentRoundSound(String ejectSpentRoundSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.ejectSpentRoundSound = ejectSpentRoundSound.toLowerCase();
            return this;
        }

        public Builder withSilencedShootSound(String silencedShootSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.silencedShootSound = silencedShootSound.toLowerCase();
            return this;
        }
        
        public Builder withBurstShootSound(String burstShootSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.burstShootSound = burstShootSound.toLowerCase(); //modId + ":" + shootSound;
            return this;
        }
        
        public Builder withSilencedBurstShootSound(String silencedBurstShootSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.silencedBurstShootSound = silencedBurstShootSound.toLowerCase(); //modId + ":" + shootSound;
            return this;
        }

        public Builder withReloadSound(String reloadSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.reloadSound = reloadSound.toLowerCase(); //modId + ":" + reloadSound;
            return this;
        }
        
        public Builder withReloadIterationSound(String reloadIterationSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.reloadIterationSound = reloadIterationSound.toLowerCase(); //modId + ":" + reloadSound;
            return this;
        }
        
        public Builder withInspectSound(String inspectSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.inspectSound = inspectSound.toLowerCase(); //modId + ":" + reloadSound;
            return this;
        }
        
        public Builder withDrawSound(String drawSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.drawSound = drawSound.toLowerCase(); //modId + ":" + reloadSound;
            return this;
        }
        
        public Builder withAllReloadIterationsCompletedSound(String allReloadIterationCompletedSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.allReloadIterationsCompletedSound = allReloadIterationCompletedSound.toLowerCase(); //modId + ":" + reloadSound;
            return this;
        }

        public Builder withUnloadSound(String unloadSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.unloadSound = unloadSound.toLowerCase();
            return this;
        }

        public Builder withShootSoundVolume(float volume) {
            this.shootSoundVolume = volume;
            return this;
        }

        public Builder withSilenceShootSoundVolume(float volume) {
            this.silencedShootSoundVolume = volume;
            return this;
        }

        public Builder withExceededMaxShotsSound(String shootSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.exceededMaxShotsSound = shootSound.toLowerCase(); //modId + ":" + shootSound;
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
        
        public Builder withDestroyingBlocks(boolean isDestroyingBlocks) {
            this.isDestroyingBlocks = isDestroyingBlocks;
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

        public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, ItemAttachment.ApplyHandler2<Weapon> applyHandler,
                ItemAttachment.ApplyHandler2<Weapon> removeHandler) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, applyHandler, removeHandler));
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, BiConsumer<EntityLivingBase, ItemStack> positioning, Consumer<ModelBase> modelPositioning) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, modelPositioning, false));
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, BiConsumer<EntityLivingBase, ItemStack> positioning) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, null, false));
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, Consumer<ModelBase> positioner) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner));
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, boolean isDefault,
                BiConsumer<EntityLivingBase, ItemStack> positioning, Consumer<ModelBase> modelPositioning) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, modelPositioning, isDefault));
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<Weapon> attachment, boolean isDefault, boolean isPermanent,
                BiConsumer<EntityLivingBase, ItemStack> positioning, Consumer<ModelBase> modelPositioning) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, modelPositioning, isDefault, isPermanent));
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
            this.ammoModelTextureName = modId + ":" + "textures/models/" + ammoModelTextureName.toLowerCase() + ".png";
            return this;
        }

        public Builder withSpawnEntityBlockImpactHandler(ImpactHandler impactHandler) {
            this.blockImpactHandler = impactHandler;
            return this;
        }

        public Builder withShellCasingEjectEnabled(boolean shellCasingEjectEnabled) {
            this.shellCasingEjectEnabled = shellCasingEjectEnabled;
            return this;
        }

        public Builder withShellCasingModel(ModelBase shellCasingModel) {
            this.shellCasingModel = shellCasingModel;
            return this;
        }

        public Builder withShellCasingModelTexture(String shellModelTextureName) {
            this.shellCasingModelTextureName = modId + ":" + "textures/models/" + shellModelTextureName.toLowerCase() + ".png";
            return this;
        }

        public Builder withShellCasingForwardOffset(float shellCasingForwardOffset) {
            this.shellCasingForwardOffset = shellCasingForwardOffset;
            return this;
        }

        public Builder withShellCasingVerticalOffset(float shellCasingVerticalOffset) {
            this.shellCasingVerticalOffset = shellCasingVerticalOffset;
            return this;
        }

        public Builder withShellCasingSideOffset(float shellCasingSideOffset) {
            this.shellCasingSideOffset = shellCasingSideOffset;
            return this;
        }

        public Builder withShellCasingSideOffsetAimed(float shellCasingSideOffsetAimed) {
            this.shellCasingSideOffsetAimed = shellCasingSideOffsetAimed;
            return this;
        }

        public Builder withShellCasingEjectDirection(ShellCasingEjectDirection shellCasingEjectDirection) {
            this.shellCasingEjectDirection = shellCasingEjectDirection;
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

        public Builder withFlashScale(Supplier<Float> flashScale) {
            this.flashScale = flashScale;
            return this;
        }

        public Builder withFlashOffsetX(Supplier<Float> flashOffsetX) {
            this.flashOffsetX = flashOffsetX;
            return this;
        }

        public Builder withFlashOffsetY(Supplier<Float> flashOffsetY) {
            this.flashOffsetY = flashOffsetY;
            return this;
        }
        
        public Builder withFlashTexture(String flashTexture) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.flashTexture = modId + ":" + "textures/particle/" + flashTexture.toLowerCase() + ".png";
            return this;
        }

        public Builder withSmokeOffsetX(Supplier<Float> smokeOffsetX) {
            this.smokeOffsetX = smokeOffsetX;
            return this;
        }

        public Builder withSmokeOffsetY(Supplier<Float> smokeOffsetY) {
            this.smokeOffsetY = smokeOffsetY;
            return this;
        }

        public Builder withCrafting(CraftingComplexity craftingComplexity, Object...craftingMaterials) {
            if(craftingComplexity == null) {
                throw new IllegalArgumentException("Crafting complexity not set");
            }
            if(craftingMaterials.length < 2) {
                throw new IllegalArgumentException("2 or more materials required for crafting");
            }
            this.craftingComplexity = craftingComplexity;
            this.craftingMaterials = craftingMaterials;
            return this;
        }

        public Builder withCraftingRecipe(Object...craftingRecipe) {
            this.craftingRecipe = craftingRecipe;
            return this;
        }

        public Weapon build(ModContext modContext) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }

            if (name == null) {
                throw new IllegalStateException("Weapon name not provided");
            }
            
            Gun gunConfig = modContext.getConfigurationManager().getGun(name);
            
            if(gunConfig != null) {
                spawnEntityDamage *= gunConfig.getDamage();
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
                    WeaponSpawnEntity bullet = new WeaponSpawnEntity(weapon, compatibility.world(player), player, spawnEntitySpeed,
                            spawnEntityGravityVelocity, inaccuracy, spawnEntityDamage, spawnEntityExplosionRadius, isDestroyingBlocks);
                    bullet.setPositionAndDirection();
                    return bullet;
                };
            }

            if(shellCasingModel == null) {
                shellCasingModel = new Shell();
            }

            if(shellCasingModelTextureName == null) {
                shellCasingModelTextureName = DEFAULT_SHELL_CASING_TEXTURE_NAME;
            }

            if(spawnShellWith == null) {
                spawnShellWith = (weaponInstance, player) -> {
                    EntityShellCasing shell = new EntityShellCasing(weaponInstance, compatibility.world(player), player,
                            DEFAULT_SHELL_CASING_VELOCITY, DEFAULT_SHELL_CASING_GRAVITY_VELOCITY, DEFAULT_SHELL_CASING_INACCURACY);
                    shell.setPositionAndDirection();
                    return shell;
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
                    CompatibleBlockState blockState = compatibility.getBlockAtPosition(world, position);
                    Boolean canDestroyGlassBlocks = modContext.getConfigurationManager().getProjectiles().isDestroyGlassBlocks();
                    if (canDestroyGlassBlocks != null && canDestroyGlassBlocks && compatibility.isGlassBlock(blockState)) {
                        compatibility.destroyBlock(world, position);
                    } else  {
                        //compatibility.addBlockHitEffect(position);
                        //compatibility.playSound(world, posX, posY, posZ, explosionSound, volume, pitch);
                        CompatibleTargetPoint point = new CompatibleTargetPoint(entity.dimension,
                                position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ(), 100);
                        modContext.getChannel().sendToAllAround(
                                new BlockHitMessage(position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ(), position.getSideHit()), point);
                        
                        MaterialImpactSound materialImpactSound = modContext.getMaterialImpactSound(blockState, entity);
                        if(materialImpactSound != null) {
                            compatibility.playSound(world, position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ(), 
                                    materialImpactSound.getSound(), materialImpactSound.getVolume(), 1f);
                        }
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
            if(this.endOfShootSound != null) {
                weapon.endOfShootSound = modContext.registerSound(this.endOfShootSound);
            }
            
            weapon.burstShootSound = modContext.registerSound(this.burstShootSound);
            weapon.silencedBurstShootSound = modContext.registerSound(this.silencedBurstShootSound);

            weapon.reloadSound = modContext.registerSound(this.reloadSound);
            weapon.reloadIterationSound = modContext.registerSound(this.reloadIterationSound);
            weapon.inspectSound = modContext.registerSound(this.inspectSound);
            weapon.drawSound = modContext.registerSound(this.drawSound);

            weapon.allReloadIterationsCompletedSound = modContext.registerSound(this.allReloadIterationsCompletedSound);
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

            if(gunConfig == null || gunConfig.isEnabled()) {
                modContext.registerWeapon(name, weapon, renderer);

                if(craftingRecipe != null && craftingRecipe.length >= 2) {
                    ItemStack itemStack = new ItemStack(weapon);
                    List<Object> registeredRecipe = modContext.getRecipeManager().registerShapedRecipe(weapon, craftingRecipe);
                    boolean hasOres = Arrays.stream(craftingRecipe).anyMatch(r -> r instanceof String);
                    if(hasOres) {
                        compatibility.addShapedOreRecipe(itemStack, registeredRecipe.toArray());
                    } else {
                        compatibility.addShapedRecipe(itemStack, registeredRecipe.toArray());
                    }
                } else if(craftingComplexity != null) {
                    OptionsMetadata optionsMetadata = new OptionsMetadata.OptionMetadataBuilder()
                            .withSlotCount(9)
                            .build(craftingComplexity, Arrays.copyOf(craftingMaterials, craftingMaterials.length));

                    List<Object> shape = modContext.getRecipeManager().createShapedRecipe(weapon, weapon.getName(), optionsMetadata);

                    if(optionsMetadata.hasOres()) {
                        compatibility.addShapedOreRecipe(new ItemStack(weapon), shape.toArray());
                    } else {
                        compatibility.addShapedRecipe(new ItemStack(weapon), shape.toArray());
                    }

                } else {
                    System.err.println("!!!No recipe defined for weapon " + name);
                }
            }


            return weapon;
        }
    }

    private static final long DEFAULT_RELOADING_TIMEOUT_TICKS = 10;
    private static final long DEFAULT_UNLOADING_TIMEOUT_TICKS = 10;
    private static final long DEFAULT_LOAD_ITERATION_TIMEOUT_TICKS = 10;
    
    static final long MAX_RELOAD_TIMEOUT_TICKS = 60;
    static final long MAX_UNLOAD_TIMEOUT_TICKS = 60;
    
    private static final long DEFAULT_BURST_TIMEOUT_MILLISECONDS = 150;

    public static final float DEFAULT_SHELL_CASING_FORWARD_OFFSET = 0.1f;
    public static final float DEFAULT_SHELL_CASING_VERTICAL_OFFSET = 0.0f;
    public static final float DEFAULT_SHELL_CASING_SIDE_OFFSET = 0.15f;
    public static final float DEFAULT_SHELL_CASING_SIDE_OFFSET_AIMED = 0.05f;

    //private static final float DEFAULT_ZOOM = 0.75f;
    private static final float DEFAULT_FIRE_RATE = 0.5f;

    private static final float DEFAULT_SILENCED_SHOOT_SOUND_VOLUME = 0.7f;
    private static final float DEFAULT_SHOOT_SOUND_VOLUME = 10f;

    Builder builder;

    private ModContext modContext;

    private CompatibleSound shootSound;
    private CompatibleSound endOfShootSound;
    private CompatibleSound silencedShootSound;
    private CompatibleSound reloadSound;
    private CompatibleSound reloadIterationSound;
    private CompatibleSound inspectSound;
    private CompatibleSound drawSound;
    private CompatibleSound allReloadIterationsCompletedSound;
    private CompatibleSound unloadSound;
    private CompatibleSound ejectSpentRoundSound;
    private CompatibleSound burstShootSound;
    private CompatibleSound silencedBurstShootSound;

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
    
    public CompatibleSound getBurstShootSound() {
        return burstShootSound;
    }
    
    public CompatibleSound getSilencedBurstShootSound() {
        return silencedBurstShootSound;
    }
    
    public CompatibleSound getEndOfShootSound() {
        return endOfShootSound;
    }

    public CompatibleSound getSilencedShootSound() {
        return silencedShootSound;
    }

    public CompatibleSound getReloadSound() {
        return reloadSound;
    }
    

    public CompatibleSound getReloadIterationSound() {
        return reloadIterationSound;
    }
    
    public CompatibleSound getInspectSound() {
        return inspectSound;
    }
    
    public CompatibleSound getDrawSound() {
        return drawSound;
    }
    
    public CompatibleSound getAllReloadIterationsCompletedSound() {
        return allReloadIterationsCompletedSound;
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

    void toggleAiming() {
        PlayerWeaponInstance mainHandHeldWeaponInstance = modContext.getMainHeldWeapon();
        if(mainHandHeldWeaponInstance != null
                && (mainHandHeldWeaponInstance.getState() == WeaponState.READY
                || mainHandHeldWeaponInstance.getState() == WeaponState.EJECT_REQUIRED)
                ) {
            mainHandHeldWeaponInstance.setAimed(!mainHandHeldWeaponInstance.isAimed());
        }
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean active) {
    }

    public void changeRecoil(EntityLivingBase player, float factor) {
        PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
        if(instance != null) {
            float recoil = instance.getWeapon().builder.recoil * factor;
            logger.debug("Changing recoil to {} for instance {}", recoil, instance);
            instance.setRecoil(recoil);
        }
    }

    public Map<ItemAttachment<Weapon>, CompatibleAttachment<Weapon>> getCompatibleAttachments() {
        return builder.compatibleAttachments;
    }

    public Collection<CompatibleAttachment<? extends AttachmentContainer>> getCompatibleAttachments(AttachmentCategory...categories) {
        Collection<CompatibleAttachment<Weapon>> c = builder.compatibleAttachments.values();
        List<AttachmentCategory> inputCategoryList = Arrays.asList(categories);
        return c.stream().filter(e -> inputCategoryList.contains(e.getAttachment().getCategory())).collect(Collectors.toList());
    }

    String getCrosshair(PlayerWeaponInstance weaponInstance) {
        if(weaponInstance.isAimed()) {
            String crosshair = null;
            ItemAttachment<Weapon> scopeAttachment = WeaponAttachmentAspect.getActiveAttachment(AttachmentCategory.SCOPE, weaponInstance);
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

    public static boolean isActiveAttachment(PlayerWeaponInstance weaponInstance, ItemAttachment<Weapon> attachment) {
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

    }

    public int getAmmoCapacity() {
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

    ModelBase getShellCasingModel() {
        return builder.shellCasingModel;
    }

    String getShellCasingTextureName() {
        return builder.shellCasingModelTextureName;
    }

    void onSpawnEntityBlockImpact(World world, EntityPlayer player, WeaponSpawnEntity entity, CompatibleRayTraceResult position) {
        if(builder.blockImpactHandler != null) {
            builder.blockImpactHandler.onImpact(world, player, entity, position);
        }
    }

    @Override
    public List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(EntityLivingBase player, ItemStack itemStack) {
        return modContext.getAttachmentAspect().getActiveAttachments(player, itemStack);
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
    
    @Override
    public void addInformation(ItemStack itemStack, List<String> info, boolean flag) {
        if(info != null && builder.informationProvider != null) {
            info.addAll(builder.informationProvider.apply(itemStack));
        }
    }

    @Override
    public void reloadMainHeldItemForPlayer(EntityPlayer player) {
        modContext.getWeaponReloadAspect().reloadMainHeldItem(player);
    }
    
    @Override
    public void inspectMainHeldItemForPlayer(EntityPlayer player) {
        modContext.getWeaponReloadAspect().inspectMainHeldItem(player);
    }

    @Override
    public void update(EntityPlayer player) {        
        modContext.getWeaponReloadAspect().updateMainHeldItem(player);
        modContext.getWeaponFireAspect().onUpdate(player);
        modContext.getAttachmentAspect().updateMainHeldItem(player);
    }

    public void tryFire(EntityPlayer player) {
        modContext.getWeaponFireAspect().onFireButtonDown(player);
    }

    public void tryStopFire(EntityPlayer player) {
        modContext.getWeaponFireAspect().onFireButtonRelease(player);
    }

    @Override
    public PlayerWeaponInstance createItemInstance(EntityLivingBase player, ItemStack itemStack, int slot){
        PlayerWeaponInstance instance = new PlayerWeaponInstance(slot, player, itemStack);
        //state.setAmmo(Tags.getAmmo(itemStack)); // TODO: get ammo properly
        instance.setState(WeaponState.READY);
        instance.setRecoil(builder.recoil);
        instance.setMaxShots(builder.maxShots.get(0));

        for(CompatibleAttachment<Weapon> compatibleAttachment: ((Weapon) itemStack.getItem()).getCompatibleAttachments().values()) {
            ItemAttachment<Weapon> attachment = compatibleAttachment.getAttachment();
            if(compatibleAttachment.isDefault() && attachment.getApply2() != null) {
                attachment.apply2.apply(attachment, instance);
            }
        }
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
            message = compatibility.getLocalizedString("gui.firearmMode.semi");
        } else if(result == Integer.MAX_VALUE) {
            message = compatibility.getLocalizedString("gui.firearmMode.auto");
        } else {
            message = compatibility.getLocalizedString("gui.firearmMode.burst");
        }
        logger.debug("Changed fire mode of {} to {}", instance, result);

        modContext.getStatusMessageCenter().addMessage(compatibility.getLocalizedString(
                "gui.firearmMode", message), 1000);

        compatibility.playSound(instance.getPlayer(),  modContext.getChangeFireModeSound(), 1F, 1F);
    }

    public long getTotalReloadingDuration() {
        //logger.debug("Total load duration " + builder.renderer.getTotalReloadingDuration());
        return builder.renderer.getTotalReloadingDuration();
    }
    
    public long getPrepareFirstLoadIterationAnimationDuration() {
        return builder.renderer.getPrepareFirstLoadIterationAnimationDuration();
    }
    
    public long getAllLoadIterationAnimationsCompletedDuration() {
        return builder.renderer.getAllLoadIterationAnimationsCompletedDuration();
    }
    
    public long getTotalLoadIterationDuration() {
        return builder.renderer.getTotalLoadIterationDuration();
    }

    public long getTotalUnloadingDuration() {
        return builder.renderer.getTotalUnloadingDuration();
    }
    
    public long getTotalDrawingDuration() {
        return builder.renderer.getTotalDrawingDuration();
    }

    public boolean hasRecoilPositioning() {
        return builder.renderer.hasRecoilPositioning();
    }

    void incrementZoom(PlayerWeaponInstance instance) {
        Item scopeItem = instance.getAttachmentItemWithCategory(AttachmentCategory.SCOPE);
        if(scopeItem instanceof ItemScope && ((ItemScope) scopeItem).isOptical()) {
            float minZoom = ((ItemScope) scopeItem).getMinZoom();
            float maxZoom = ((ItemScope) scopeItem).getMaxZoom();
            float increment = (minZoom - maxZoom) / 20f;
            float zoom = instance.getZoom();

            if(zoom > maxZoom) {
                zoom = Math.max(zoom - increment, maxZoom);
            }

            instance.setZoom(zoom);

            float ratio = (minZoom - zoom) / (minZoom - maxZoom);

            modContext.getStatusMessageCenter().addMessage(
                    compatibility.getLocalizedString("gui.currentZoom", Math.round(ratio * 100)), 800);
            compatibility.playSound(instance.getPlayer(),  modContext.getZoomSound(), 1F, 1F);
            logger.debug("Changed optical zoom to {}", instance.getZoom());
        } else {
            logger.debug("Cannot change non-optical zoom");
        }
    }

    void decrementZoom(PlayerWeaponInstance instance) {
        Item scopeItem = instance.getAttachmentItemWithCategory(AttachmentCategory.SCOPE);
        if(scopeItem instanceof ItemScope && ((ItemScope) scopeItem).isOptical()) {
            float minZoom = ((ItemScope) scopeItem).getMinZoom();
            float maxZoom = ((ItemScope) scopeItem).getMaxZoom();
            float increment = (minZoom - maxZoom) / 20f;
            float zoom = instance.getZoom();

            if(zoom < minZoom) {
                zoom = Math.min(zoom + increment, minZoom);
            }
            instance.setZoom(zoom);

            float ratio = (minZoom - zoom) / (minZoom - maxZoom);
            modContext.getStatusMessageCenter().addMessage(
                    compatibility.getLocalizedString("gui.currentZoom", Math.round(ratio * 100)), 800);
            compatibility.playSound(instance.getPlayer(),  modContext.getZoomSound(), 1F, 1F);
            logger.debug("Changed optical zoom to {}", zoom);
        } else {
            logger.debug("Cannot change non-optical zoom");
        }
    }

    public ItemAttachment.ApplyHandler2<Weapon> getEquivalentHandler(AttachmentCategory attachmentCategory) {
        ItemAttachment.ApplyHandler2<Weapon> handler = (a, i) -> {};
        switch(attachmentCategory) {
        case SCOPE:
            handler = (a, i) -> {
                //i.setZoom(builder.zoom);
            };
            break;
        case GRIP:
            handler = (a, i) -> {
                i.setRecoil(builder.recoil);
            };
            break;
        default:
            break;
        }
        return handler;
    }

    public String getTextureName() {
        return builder.textureNames.get(0);
    }

    public float getRecoil() {
        return builder.recoil;
    }

    public ModContext getModContext() {
        return modContext;
    }

    public float getShellCasingVerticalOffset() {
        return builder.shellCasingVerticalOffset;
    }

    public float getShellCasingForwardOffset() {
        return builder.shellCasingForwardOffset;
    }

    public float getShellCasingSideOffset() {
        return builder.shellCasingSideOffset;
    }

    public float getShellCasingSideOffsetAimed() {
        return builder.shellCasingSideOffsetAimed;
    }

    public boolean isShellCasingEjectEnabled() {
        return builder.shellCasingEjectEnabled;
    }

    public ShellCasingEjectDirection getShellCasingEjectDirection() {
        return builder.shellCasingEjectDirection;
    }

    public float getSilencedShootSoundVolume() {
        return builder.silencedShootSoundVolume;
    }

    public float getShootSoundVolume() {
        return builder.shootSoundVolume;
    }

    public boolean hasIteratedLoad() {
        return builder.hasIteratedLoad;
    }
    
    public float getSpawnEntityVelocity() {
        return builder.spawnEntitySpeed;
    }
    
    public float getSpawnEntityGravityVelocity() {
        return builder.spawnEntityGravityVelocity;
    }

    public float getSpawnEntityDamage() {
        return builder.spawnEntityDamage;
    }
    
    public float getSpawnEntityExplosionRadius() {
        return builder.spawnEntityExplosionRadius;
    }
    
    public float getInaccuracy() {
        return builder.inaccuracy;
    }
}
