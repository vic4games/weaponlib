package com.vicmatskiv.weaponlib.ai;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.Configurable;
import com.vicmatskiv.weaponlib.Contextual;
import com.vicmatskiv.weaponlib.CustomArmor;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ItemMagazine;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.PlayerItemInstanceFactory;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.Tags;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect;
import com.vicmatskiv.weaponlib.WeaponFireAspect;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;
import com.vicmatskiv.weaponlib.ai.EntityConfiguration.Equipment;
import com.vicmatskiv.weaponlib.ai.EntityConfiguration.TexturedModel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleAchievement;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockPos;
import com.vicmatskiv.weaponlib.compatibility.CompatibleDataManager;
import com.vicmatskiv.weaponlib.compatibility.CompatibleDifficulty;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityEquipmentSlot;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityMob;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSharedMonsterAttributes;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.grenade.GrenadeAttackAspect;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;
import com.vicmatskiv.weaponlib.grenade.PlayerGrenadeInstance;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;

public class EntityCustomMob extends CompatibleEntityMob implements IRangedAttackMob, Contextual, Configurable<EntityConfiguration> {

    private static final float FLAT_WORLD_SPAWN_CHANCE = 0.01f;
    private static final CompatibleDataManager.Key VARIANT = CompatibleDataManager.createKey(EntityCustomMob.class, int.class);
    private static final CompatibleDataManager.Key SWINGING_ARMS = CompatibleDataManager.createKey(EntityCustomMob.class, boolean.class);

    private ModContext modContext;
    
    private EntityConfiguration configuration;
    
    private ItemStack secondaryEquipment;

    public EntityCustomMob(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 1.99F);
    }
    
    @Override
    public EntityConfiguration getConfiguration() {
        if(configuration == null) {
            configuration = EntityClassFactory.getInstance().getConfiguration(getClass());
        }
        return configuration;
    }

    @Override
    protected void initEntityAI() {
        getConfiguration().addAiTasks(this, this.tasks);
        getConfiguration().addAiTargetTasks(this, this.targetTasks);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        compatibility.setEntityAttribute(this, CompatibleSharedMonsterAttributes.FOLLOW_RANGE, getConfiguration().getFollowRange());
        compatibility.setEntityAttribute(this, CompatibleSharedMonsterAttributes.MOVEMENT_SPEED, getConfiguration().getMaxSpeed());
        compatibility.setEntityAttribute(this, CompatibleSharedMonsterAttributes.MAX_HEALTH, getConfiguration().getMaxHealth());
    }

    protected void entityInit() {
        super.entityInit();
        compatibleDataManager.register(VARIANT, Integer.valueOf(0));
        compatibleDataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
    }
    
    @Override
    protected CompatibleSound getCompatibleAmbientSound() {
        return getConfiguration().getAmbientSound();
    }

    @Override
    protected CompatibleSound getCompatibleHurtSound() {
        return getConfiguration().getHurtSound();
    }
    
    @Override
    protected CompatibleSound getCompatibleDeathSound() {
        return getConfiguration().getDeathSound();
    }

    @Override
    protected void playStepSound(CompatibleBlockPos pos, Block blockIn) {
        compatibility.playSound(this, getConfiguration().getStepSound(), 0.15F, 1.0F);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return getConfiguration().getCreatureAttribute();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    public void onDeath(DamageSource cause) {
        ItemStack itemStack = compatibility.getHeldItemMainHand(this); //getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        
        if(!compatibility.world(this).isRemote && itemStack != null) {
            initAmmo(itemStack);
        }
        
        super.onDeath(cause);

        Entity trueDamageSource = compatibility.getTrueDamageSource(cause);
        if (trueDamageSource instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)trueDamageSource;
            compatibility.addStat(entityplayer, CompatibleAchievement.KILL_ENEMY);
        }
        
        if(secondaryEquipment != null) {
            if(rand.nextFloat() < getConfiguration().getPrimaryEquipmentDropChance()) {
                entityDropItem(secondaryEquipment, 0);
            }
        }
    }

    private void initAmmo(ItemStack itemStack) {
        PlayerItemInstance<?> instance = Tags.getInstance(itemStack);
        if(instance instanceof PlayerWeaponInstance) {
            PlayerWeaponInstance weaponInstance = (PlayerWeaponInstance) instance;
            ItemMagazine existingMagazine = (ItemMagazine) WeaponAttachmentAspect.getActiveAttachment(
                    AttachmentCategory.MAGAZINE, weaponInstance);
            int maxAmmo = 0;
            if(existingMagazine != null) {
                maxAmmo = existingMagazine.getAmmo();
            } else if(weaponInstance.getWeapon().getAmmoCapacity() > 0) {
                maxAmmo = weaponInstance.getWeapon().getAmmoCapacity();
            }
            if(maxAmmo > 0) {
                weaponInstance.setAmmo(rand.nextInt(maxAmmo));
                Tags.setInstance(itemStack, weaponInstance);
            }
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return getConfiguration().getLootTable();
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    @Override
    protected void setEquipmentBasedOnDifficulty(CompatibleDifficulty difficulty) {
        setArmorEquipment();   
        setPrimaryEquipment();
        setSecondaryEquipment();
    }

    private void setArmorEquipment() {
        EntityConfiguration configuration = getConfiguration();
        setCompatibleInventoryArmorDropChances(configuration.getArmorDropChance());
        for(CustomArmor armor: configuration.getArmorSet()) {
            compatibility.setItemStackToSlot(this, armor.getCompatibleEquipmentSlot(), new ItemStack(armor));
        }
    }

    private void setSecondaryEquipment() {
        EntityConfiguration configuration = getConfiguration();
        Equipment secondaryEquipment = configuration.getSecondaryEquipmentOptions().pick(compatibility.getDifficulty(compatibility.world(this)));
        if(secondaryEquipment != null) {
            ItemStack equipmentItemStack = new ItemStack(secondaryEquipment.item);
            if(secondaryEquipment.item instanceof ItemGrenade) {
                initGrenade(secondaryEquipment, equipmentItemStack);
            }
            this.secondaryEquipment = equipmentItemStack;
        }
    }

    private void setPrimaryEquipment() {
        EntityConfiguration configuration = getConfiguration();
        Equipment equipment = configuration.getEquipmentOptions().pick(compatibility.getDifficulty(compatibility.world(this)));
                
        setCompatibleInventoryHandsDropChances(configuration.getPrimaryEquipmentDropChance());
        if (equipment != null) {
            ItemStack equipmentItemStack = new ItemStack(equipment.item);
            if(equipment.item instanceof Weapon) {
                initWeaponWithAttachments(equipment, equipmentItemStack);
            } else if(equipment.item instanceof ItemGrenade) {
                initGrenade(equipment, equipmentItemStack);
            }
           
            compatibility.setItemStackToSlot(this, CompatibleEntityEquipmentSlot.MAIN_HAND, equipmentItemStack);
        }
    }

    @SuppressWarnings("unchecked")
    private void initWeaponWithAttachments(Equipment equipment, ItemStack itemStack) {
        if(equipment.attachments != null && equipment.item instanceof Weapon && equipment.item instanceof PlayerItemInstanceFactory) {
            PlayerWeaponInstance weaponInstance = (PlayerWeaponInstance) ((PlayerItemInstanceFactory<?, ?>)equipment.item).createItemInstance(this, new ItemStack(equipment.item), 0);
            for(ItemAttachment<?> attachment: equipment.attachments) {
                Set<ItemAttachment<Weapon>> compatibleAttachments = weaponInstance.getWeapon().getCompatibleAttachments().keySet();
                compatibleAttachments.contains(attachment);
                WeaponAttachmentAspect.addAttachment((ItemAttachment<Weapon>) attachment, weaponInstance);
            }
            Tags.setInstance(itemStack, weaponInstance);
        }
    }
    
    private void initGrenade(Equipment equipment, ItemStack itemStack) {
        if(equipment.item instanceof ItemGrenade) {
            PlayerGrenadeInstance grenadeInstance = (PlayerGrenadeInstance) ((PlayerItemInstanceFactory<?, ?>)equipment.item)
                    .createItemInstance(this, new ItemStack(equipment.item), 0);
            grenadeInstance.setThrowingFar(true);
            Tags.setInstance(itemStack, grenadeInstance);
        }
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    @Override
    public IEntityLivingData onCompatibleSpawn(CompatibleDifficulty difficulty, @Nullable IEntityLivingData livingdata)
    {
        livingdata = super.onCompatibleSpawn(difficulty, livingdata);
        
        List<TexturedModel> variants = this.getConfiguration().getTexturedModelVariants();
        int variant = 0;
        if(!variants.isEmpty()) {
            variant = this.rand.nextInt(variants.size());
        }
        setVariant(variant);

        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * difficulty.getClampedAdditionalDifficulty());

        return livingdata;
    }

    /**
     * Attack the specified entity using a ranged attack.
     *  
     * @param distanceFactor How far the target is, normalized and clamped between 0.1 and 1.0
     */
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if(modContext == null) {
            return;
        }
        
        ItemStack itemStack = compatibility.getHeldItemMainHand(this);
        
        if(itemStack == null) {
            return;
        }
        
        if(itemStack.getItem() instanceof Weapon) {
            WeaponFireAspect fireAspect = modContext.getWeaponFireAspect();

            BiFunction<Weapon, EntityLivingBase, ? extends WeaponSpawnEntity> spawnEntityWith = (weapon, player) -> {
                int difficultyId = compatibility.getDifficulty(compatibility.world(this)).getDifficultyId();
                float inaccuracy = weapon.getInaccuracy() + (3f - difficultyId) * 0.5f; // * 2 + distanceFactor * 3f;
                WeaponSpawnEntity bullet = new WeaponSpawnEntity(weapon, compatibility.world(player), player, 
                        weapon.getSpawnEntityVelocity(),
                        weapon.getSpawnEntityGravityVelocity(), 
                        inaccuracy, 
                        weapon.getSpawnEntityDamage(), 
                        weapon.getSpawnEntityExplosionRadius());
                bullet.setPositionAndDirection();
                return bullet;
            };
            
            fireAspect.serverFire(this, itemStack, spawnEntityWith);
        } else if(itemStack.getItem() instanceof ItemGrenade) {
            float rotationPitchAdjustment = 20f;
            this.rotationPitch -= rotationPitchAdjustment;
            PlayerGrenadeInstance grenadeInstance = (PlayerGrenadeInstance) Tags.getInstance(itemStack);
            GrenadeAttackAspect.serverThrowGrenade(modContext, this, grenadeInstance, System.currentTimeMillis() + 2000L);
            this.rotationPitch += rotationPitchAdjustment;
        }
    }
    
    void attackWithSecondaryEquipment(EntityLivingBase target, float distanceFactor) {
        if(modContext == null) {
            return;
        }
                
        if(secondaryEquipment == null
                /*|| this.rand.nextFloat() >= getConfiguration().getSecondaryEquipmentUseChance()*/) {
            return;
        }
        
        if(secondaryEquipment.getItem() instanceof ItemGrenade) {
            float rotationPitchAdjustment = 20f;
            this.rotationPitch -= rotationPitchAdjustment;
            PlayerGrenadeInstance grenadeInstance = (PlayerGrenadeInstance) Tags.getInstance(secondaryEquipment);
            GrenadeAttackAspect.serverThrowGrenade(modContext, this, grenadeInstance, System.currentTimeMillis() + 2000L);
            this.rotationPitch += rotationPitchAdjustment;
        }
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        int variant = compound.getInteger("Variant");
        setVariant(variant);
                
        NBTTagCompound secondaryNbt = compound.getCompoundTag("Secondary");
        if(secondaryNbt != null) {
            this.secondaryEquipment = compatibility.createItemStack(secondaryNbt);
        }
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Variant", getVariant());
        
        if(secondaryEquipment != null) {
            compound.setTag("Secondary", secondaryEquipment.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public float getEyeHeight() {
        return 1.74F;
    }

    /**
     * Returns the Y Offset of this entity.
     */
    @Override
    public double getYOffset() {
        return -0.35D;
    }
    
    public int getVariant() {
        return this.compatibleDataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.compatibleDataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public boolean isSwingingArms() {
        return this.compatibleDataManager.get(SWINGING_ARMS).booleanValue();
    }

    public void setSwingingArms(boolean swingingArms) {
        this.compatibleDataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
    }
    
    @Override
    protected boolean isValidLightLevel() {
        Predicate<Entity> predicate = getConfiguration().isValidLightLevel();
        return predicate != null ? predicate.test(this) : super.isValidLightLevel();
    }
    
    @Override
    public float getCompatibleBlockPathWeight(CompatibleBlockPos pos) {
        return getConfiguration().getMaxTolerableLightBrightness() - compatibility.getLightBrightness(compatibility.world(this), pos);
    }
    
    @Override
    public boolean getCanSpawnHere() {
        boolean canSpawn = compatibility.world(this).getWorldType() != WorldType.FLAT
            || rand.nextFloat() > (1f - FLAT_WORLD_SPAWN_CHANCE);
        Predicate<Entity> predicate = getConfiguration().getCanSpawnHere();
        return canSpawn && (predicate != null ? predicate.test(this) : super.getCanSpawnHere());
    }

    @Override
    public void setContext(ModContext modContext) {
        this.modContext = modContext;
    }
    
    public ItemStack getSecondaryEquipment() {
        return secondaryEquipment;
    }
}