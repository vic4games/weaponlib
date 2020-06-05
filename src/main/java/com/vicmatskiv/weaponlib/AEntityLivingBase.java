package com.vicmatskiv.weaponlib;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.vicmatskiv.weaponlib.compatibility.Interceptors;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.CombatRules;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AEntityLivingBase extends EntityLivingBase
{
    private static final Logger LOG = LogManager.getLogger();
    private static final UUID SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier SPRINTING_SPEED_BOOST = (new AttributeModifier(SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
    protected static final DataParameter<Byte> HAND_STATES = EntityDataManager.<Byte>createKey(AEntityLivingBase.class, DataSerializers.BYTE);
    private static final DataParameter<Float> HEALTH = EntityDataManager.<Float>createKey(AEntityLivingBase.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> POTION_EFFECTS = EntityDataManager.<Integer>createKey(AEntityLivingBase.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> HIDE_PARTICLES = EntityDataManager.<Boolean>createKey(AEntityLivingBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ARROW_COUNT_IN_ENTITY = EntityDataManager.<Integer>createKey(AEntityLivingBase.class, DataSerializers.VARINT);
    private AbstractAttributeMap attributeMap;
    private final CombatTracker _combatTracker = new CombatTracker(this);
    private final Map<Potion, PotionEffect> activePotionsMap = Maps.<Potion, PotionEffect>newHashMap();
    private final NonNullList<ItemStack> handInventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
    /** The array of item stacks that are used for armor in a living inventory. */
    private final NonNullList<ItemStack> armorArray = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
    /** Whether an arm swing is currently in progress. */
    public boolean isSwingInProgress;
    public EnumHand swingingHand;
    public int swingProgressInt;
    public int arrowHitTimer;
    /** The amount of time remaining this entity should act 'hurt'. (Visual appearance of red tint) */
    public int hurtTime;
    /** What the hurt time was max set to last. */
    public int maxHurtTime;
    /** The yaw at which this entity was last attacked from. */
    public float attackedAtYaw;
    /** The amount of time remaining this entity should act 'dead', i.e. have a corpse in the world. */
    public int deathTime;
    public float prevSwingProgress;
    public float swingProgress;
    protected int ticksSinceLastSwing;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float randomUnused2;
    public float randomUnused1;
    public float renderYawOffset;
    public float prevRenderYawOffset;
    /** Entity head rotation yaw */
    public float rotationYawHead;
    /** Entity head rotation yaw at previous tick */
    public float prevRotationYawHead;
    /** A factor used to determine how far this entity will move each tick if it is jumping or falling. */
    public float jumpMovementFactor = 0.02F;
    /** The most recent player that has attacked this entity */
    protected EntityPlayer attackingPlayer;
    /**
     * Set to 60 when hit by the player or the player's wolf, then decrements. Used to determine whether the entity
     * should drop items on death.
     */
    protected int recentlyHit;
    /** This gets set on entity death, but never used. Looks like a duplicate of isDead */
    protected boolean dead;
    /** The age of this EntityLiving (used to determine when it dies) */
    protected int idleTime;
    protected float prevOnGroundSpeedFactor;
    protected float onGroundSpeedFactor;
    protected float movedDistance;
    protected float prevMovedDistance;
    protected float unused180;
    /** The score value of the Mob, the amount of points the mob is worth. */
    protected int scoreValue;
    /** Damage taken in the last hit. Mobs are resistant to damage less than this for a short time after taking damage. */
    protected float lastDamage;
    /** used to check whether entity is jumping. */
    protected boolean isJumping;
    public float moveStrafing;
    public float moveVertical;
    public float moveForward;
    public float randomYawVelocity;
    /** The number of updates over which the new position and rotation are to be applied to the entity. */
    protected int newPosRotationIncrements;
    /** The X position the entity will be interpolated to. Used for teleporting. */
    protected double interpTargetX;
    /** The Y position the entity will be interpolated to. Used for teleporting. */
    protected double interpTargetY;
    /** The Z position the entity will be interpolated to. Used for teleporting. */
    protected double interpTargetZ;
    /** The yaw rotation the entity will be interpolated to. Used for teleporting. */
    protected double interpTargetYaw;
    /** The pitch rotation the entity will be interpolated to. Used for teleporting. */
    protected double interpTargetPitch;
    /** Whether the DataWatcher needs to be updated with the active potions */
    private boolean potionsNeedUpdate = true;
    /**
     * Set immediately after this entity is attacked by another AEntityLivingBase, allowing AI tasks to see who the
     * attacker was and handle accordingly. Reset to null after 100 ticks have passed.
     */
    private AEntityLivingBase revengeTarget;
    private int revengeTimer;
    private AEntityLivingBase lastAttackedEntity;
    /** Holds the value of ticksExisted when setLastAttacker was last called. */
    private int lastAttackedEntityTime;
    /**
     * A factor used to determine how far this entity will move each tick if it is walking on land. Adjusted by speed,
     * and slipperiness of the current block.
     */
    private float landMovementFactor;
    /** Number of ticks since last jump */
    private int jumpTicks;
    private float absorptionAmount;
    protected ItemStack activeItemStack = ItemStack.EMPTY;
    protected int activeItemStackUseCount;
    protected int ticksElytraFlying;
    /** The BlockPos the entity had during the previous tick. */
    private BlockPos prevBlockpos;
    private DamageSource lastDamageSource;
    private long lastDamageStamp;

    /**
     * Called by the /kill command.
     */
    public void onKillCommand()
    {
        this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public AEntityLivingBase(World worldIn)
    {
        super(worldIn);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.randomUnused1 = (float)((Math.random() + 1.0D) * 0.009999999776482582D);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.randomUnused2 = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * (Math.PI * 2D));
        this.rotationYawHead = this.rotationYaw;
        this.stepHeight = 0.6F;
    }

    protected void entityInit()
    {
        this.dataManager.register(HAND_STATES, Byte.valueOf((byte)0));
        this.dataManager.register(POTION_EFFECTS, Integer.valueOf(0));
        this.dataManager.register(HIDE_PARTICLES, Boolean.valueOf(false));
        this.dataManager.register(ARROW_COUNT_IN_ENTITY, Integer.valueOf(0));
        this.dataManager.register(HEALTH, Float.valueOf(1.0F));
    }

    protected void applyEntityAttributes()
    {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ARMOR);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
    }

    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
        if (!this.isInWater())
        {
            this.handleWaterMovement();
        }

        if (!this.world.isRemote && this.fallDistance > 3.0F && onGroundIn)
        {
            float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);

            if (!state.getBlock().isAir(state, world, pos))
            {
                double d0 = Math.min((double)(0.2F + f / 15.0F), 2.5D);
                int i = (int)(150.0D * d0);
                if (!state.getBlock().addLandingEffects(state, (WorldServer)this.world, pos, state, this, i))
                ((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(state));
            }
        }

        super.updateFallState(y, onGroundIn, state, pos);
    }

    public boolean canBreatheUnderwater()
    {
        return false;
    }

    
    protected void frostWalk(BlockPos pos)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, this);

        if (i > 0)
        {
            EnchantmentFrostWalker.freezeNearby(this, this.world, pos, i);
        }
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return false;
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.deathTime == 20)
        {
            if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot")))
            {
                int i = this.getExperiencePoints(this.attackingPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
                while (i > 0)
                {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
                }
            }

            this.setDead();

            for (int k = 0; k < 20; ++k)
            {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1);
            }
        }
    }

    /**
     * Entity won't drop items or experience points if this returns false
     */
    protected boolean canDropLoot()
    {
        return !this.isChild();
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int air)
    {
        int i = EnchantmentHelper.getRespirationModifier(this);
        return i > 0 && this.rand.nextInt(i + 1) > 0 ? air : air - 1;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer player)
    {
        return 0;
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean isPlayer()
    {
        return false;
    }

    public Random getRNG()
    {
        return this.rand;
    }

    @Nullable
    public AEntityLivingBase getRevengeTarget()
    {
        return this.revengeTarget;
    }

    public int getRevengeTimer()
    {
        return this.revengeTimer;
    }

    /**
     * Hint to AI tasks that we were attacked by the passed AEntityLivingBase and should retaliate. Is not guaranteed to
     * change our actual active target (for example if we are currently busy attacking someone else)
     */
    public void setRevengeTarget(@Nullable AEntityLivingBase livingBase)
    {
        this.revengeTarget = livingBase;
        this.revengeTimer = this.ticksExisted;
        net.minecraftforge.common.ForgeHooks.onLivingSetAttackTarget(this, livingBase);
    }

    public AEntityLivingBase getLastAttackedEntity()
    {
        return this.lastAttackedEntity;
    }

    public int getLastAttackedEntityTime()
    {
        return this.lastAttackedEntityTime;
    }

    public void setLastAttackedEntity(Entity entityIn)
    {
        if (entityIn instanceof AEntityLivingBase)
        {
            this.lastAttackedEntity = (AEntityLivingBase)entityIn;
        }
        else
        {
            this.lastAttackedEntity = null;
        }

        this.lastAttackedEntityTime = this.ticksExisted;
    }

    public int getIdleTime()
    {
        return this.idleTime;
    }

    protected void playEquipSound(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            SoundEvent soundevent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
            Item item = stack.getItem();

            if (item instanceof ItemArmor)
            {
                soundevent = ((ItemArmor)item).getArmorMaterial().getSoundEvent();
            }
            else if (item == Items.ELYTRA)
            {
                soundevent = SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA;
            }

            this.playSound(soundevent, 1.0F, 1.0F);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setFloat("Health", this.getHealth());
        compound.setShort("HurtTime", (short)this.hurtTime);
        compound.setInteger("HurtByTimestamp", this.revengeTimer);
        compound.setShort("DeathTime", (short)this.deathTime);
        compound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

        for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
        {
            ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);

            if (!itemstack.isEmpty())
            {
                this.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(entityequipmentslot));
            }
        }

        compound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

        for (EntityEquipmentSlot entityequipmentslot1 : EntityEquipmentSlot.values())
        {
            ItemStack itemstack1 = this.getItemStackFromSlot(entityequipmentslot1);

            if (!itemstack1.isEmpty())
            {
                this.getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(entityequipmentslot1));
            }
        }

        if (!this.activePotionsMap.isEmpty())
        {
            NBTTagList nbttaglist = new NBTTagList();

            for (PotionEffect potioneffect : this.activePotionsMap.values())
            {
                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            compound.setTag("ActiveEffects", nbttaglist);
        }

        compound.setBoolean("FallFlying", this.isElytraFlying());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        this.setAbsorptionAmount(compound.getFloat("AbsorptionAmount"));

        if (compound.hasKey("Attributes", 9) && this.world != null && !this.world.isRemote)
        {
            SharedMonsterAttributes.setAttributeModifiers(this.getAttributeMap(), compound.getTagList("Attributes", 10));
        }

        if (compound.hasKey("ActiveEffects", 9))
        {
            NBTTagList nbttaglist = compound.getTagList("ActiveEffects", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);

                if (potioneffect != null)
                {
                    this.activePotionsMap.put(potioneffect.getPotion(), potioneffect);
                }
            }
        }

        if (compound.hasKey("Health", 99))
        {
            this.setHealth(compound.getFloat("Health"));
        }

        this.hurtTime = compound.getShort("HurtTime");
        this.deathTime = compound.getShort("DeathTime");
        this.revengeTimer = compound.getInteger("HurtByTimestamp");

        if (compound.hasKey("Team", 8))
        {
            String s = compound.getString("Team");
            boolean flag = this.world.getScoreboard().addPlayerToTeam(this.getCachedUniqueIdString(), s);

            if (!flag)
            {
                LOG.warn("Unable to add mob to team \"" + s + "\" (that team probably doesn't exist)");
            }
        }

        if (compound.getBoolean("FallFlying"))
        {
            this.setFlag(7, true);
        }
    }

    protected void updatePotionEffects()
    {
        Iterator<Potion> iterator = this.activePotionsMap.keySet().iterator();

        try
        {
            while (iterator.hasNext())
            {
                Potion potion = iterator.next();
                PotionEffect potioneffect = this.activePotionsMap.get(potion);

                if (!potioneffect.onUpdate(this))
                {
                    if (!this.world.isRemote)
                    {
                        iterator.remove();
                        this.onFinishedPotionEffect(potioneffect);
                    }
                }
                else if (potioneffect.getDuration() % 600 == 0)
                {
                    this.onChangedPotionEffect(potioneffect, false);
                }
            }
        }
        catch (ConcurrentModificationException var11)
        {
            ;
        }

        if (this.potionsNeedUpdate)
        {
            if (!this.world.isRemote)
            {
                this.updatePotionMetadata();
            }

            this.potionsNeedUpdate = false;
        }

        int i = ((Integer)this.dataManager.get(POTION_EFFECTS)).intValue();
        boolean flag1 = ((Boolean)this.dataManager.get(HIDE_PARTICLES)).booleanValue();

        if (i > 0)
        {
            boolean flag;

            if (this.isInvisible())
            {
                flag = this.rand.nextInt(15) == 0;
            }
            else
            {
                flag = this.rand.nextBoolean();
            }

            if (flag1)
            {
                flag &= this.rand.nextInt(5) == 0;
            }

            if (flag && i > 0)
            {
                double d0 = (double)(i >> 16 & 255) / 255.0D;
                double d1 = (double)(i >> 8 & 255) / 255.0D;
                double d2 = (double)(i >> 0 & 255) / 255.0D;
                this.world.spawnParticle(flag1 ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2);
            }
        }
    }

    /**
     * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
     * ambience, and invisibility metadata values
     */
    protected void updatePotionMetadata()
    {
        if (this.activePotionsMap.isEmpty())
        {
            this.resetPotionEffectMetadata();
            this.setInvisible(false);
        }
        else
        {
            Collection<PotionEffect> collection = this.activePotionsMap.values();
            net.minecraftforge.event.entity.living.PotionColorCalculationEvent event = new net.minecraftforge.event.entity.living.PotionColorCalculationEvent(this, PotionUtils.getPotionColorFromEffectList(collection), areAllPotionsAmbient(collection), collection);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            this.dataManager.set(HIDE_PARTICLES, event.areParticlesHidden());
            this.dataManager.set(POTION_EFFECTS, event.getColor());
            this.setInvisible(this.isPotionActive(MobEffects.INVISIBILITY));
        }
    }

    /**
     * Returns true if all of the potion effects in the specified collection are ambient.
     */
    public static boolean areAllPotionsAmbient(Collection<PotionEffect> potionEffects)
    {
        for (PotionEffect potioneffect : potionEffects)
        {
            if (!potioneffect.getIsAmbient())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Resets the potion effect color and ambience metadata values
     */
    protected void resetPotionEffectMetadata()
    {
        this.dataManager.set(HIDE_PARTICLES, Boolean.valueOf(false));
        this.dataManager.set(POTION_EFFECTS, Integer.valueOf(0));
    }

    public void clearActivePotions()
    {
        if (!this.world.isRemote)
        {
            Iterator<PotionEffect> iterator = this.activePotionsMap.values().iterator();

            while (iterator.hasNext())
            {
                this.onFinishedPotionEffect(iterator.next());
                iterator.remove();
            }
        }
    }

    public Collection<PotionEffect> getActivePotionEffects()
    {
        return this.activePotionsMap.values();
    }

    public Map<Potion, PotionEffect> getActivePotionMap()
    {
        return this.activePotionsMap;
    }

    public boolean isPotionActive(Potion potionIn)
    {
        return this.activePotionsMap.containsKey(potionIn);
    }

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null otherwise.
     */
    @Nullable
    public PotionEffect getActivePotionEffect(Potion potionIn)
    {
        return this.activePotionsMap.get(potionIn);
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(PotionEffect potioneffectIn)
    {
        if (this.isPotionApplicable(potioneffectIn))
        {
            PotionEffect potioneffect = this.activePotionsMap.get(potioneffectIn.getPotion());

            if (potioneffect == null)
            {
                this.activePotionsMap.put(potioneffectIn.getPotion(), potioneffectIn);
                this.onNewPotionEffect(potioneffectIn);
            }
            else
            {
                potioneffect.combine(potioneffectIn);
                this.onChangedPotionEffect(potioneffect, true);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn)
    {
        if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
        {
            Potion potion = potioneffectIn.getPotion();

            if (potion == MobEffects.REGENERATION || potion == MobEffects.POISON)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if this entity is undead.
     */
    public boolean isEntityUndead()
    {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Removes the given potion effect from the active potion map and returns it. Does not call cleanup callbacks for
     * the end of the potion effect.
     */
    @Nullable
    public PotionEffect removeActivePotionEffect(@Nullable Potion potioneffectin)
    {
        return this.activePotionsMap.remove(potioneffectin);
    }

    /**
     * Removes the given potion effect.
     */
    public void removePotionEffect(Potion potionIn)
    {
        PotionEffect potioneffect = this.removeActivePotionEffect(potionIn);

        if (potioneffect != null)
        {
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    protected void onNewPotionEffect(PotionEffect id)
    {
        this.potionsNeedUpdate = true;

        if (!this.world.isRemote)
        {
            id.getPotion().applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_)
    {
        this.potionsNeedUpdate = true;

        if (p_70695_2_ && !this.world.isRemote)
        {
            Potion potion = id.getPotion();
            potion.removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
            potion.applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(PotionEffect effect)
    {
        this.potionsNeedUpdate = true;

        if (!this.world.isRemote)
        {
            effect.getPotion().removeAttributesModifiersFromEntity(this, this.getAttributeMap(), effect.getAmplifier());
        }
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(float healAmount)
    {
        healAmount = net.minecraftforge.event.ForgeEventFactory.onLivingHeal(this, healAmount);
        if (healAmount <= 0) return;
        float f = this.getHealth();

        if (f > 0.0F)
        {
            this.setHealth(f + healAmount);
        }
    }


    public void setHealth(float health)
    {
        this.dataManager.set(HEALTH, Float.valueOf(MathHelper.clamp(health, 0.0F, this.getMaxHealth())));
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(this, source, amount)) return false;
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (this.world.isRemote)
        {
            return false;
        }
        else
        {
            this.idleTime = 0;

            if (this.getHealth() <= 0.0F)
            {
                return false;
            }
            else if (source.isFireDamage() && this.isPotionActive(MobEffects.FIRE_RESISTANCE))
            {
                return false;
            }
            else
            {
                float f = amount;

                if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty())
                {
                    this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
                    amount *= 0.75F;
                }

                boolean flag = false;

                if (amount > 0.0F && true) //this.canBlockDamageSource(source))
                {
                    this.damageShield(amount);
                    amount = 0.0F;

                    if (!source.isProjectile())
                    {
                        Entity entity = source.getImmediateSource();

                        if (entity instanceof AEntityLivingBase)
                        {
                            this.blockUsingShield((AEntityLivingBase)entity);
                        }
                    }

                    flag = true;
                }

                this.limbSwingAmount = 1.5F;
                boolean flag1 = true;

                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
                {
                    if (amount <= this.lastDamage)
                    {
                        return false;
                    }

                    this.damageEntity(source, amount - this.lastDamage);
                    this.lastDamage = amount;
                    flag1 = false;
                }
                else
                {
                    this.lastDamage = amount;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(source, amount);
                    this.maxHurtTime = 10;
                    this.hurtTime = this.maxHurtTime;
                }

                this.attackedAtYaw = 0.0F;
                Entity entity1 = source.getTrueSource();

                if (entity1 != null)
                {
                    if (entity1 instanceof AEntityLivingBase)
                    {
                        this.setRevengeTarget((AEntityLivingBase)entity1);
                    }

                    if (entity1 instanceof EntityPlayer)
                    {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer)entity1;
                    }
                    else if (entity1 instanceof net.minecraft.entity.passive.EntityTameable)
                    {
                        net.minecraft.entity.passive.EntityTameable entitywolf = (net.minecraft.entity.passive.EntityTameable)entity1;

                        if (entitywolf.isTamed())
                        {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }
                }

                if (flag1)
                {
                    if (flag)
                    {
                        this.world.setEntityState(this, (byte)29);
                    }
                    else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage())
                    {
                        this.world.setEntityState(this, (byte)33);
                    }
                    else
                    {
                        byte b0;

                        if (source == DamageSource.DROWN)
                        {
                            b0 = 36;
                        }
                        else if (source.isFireDamage())
                        {
                            b0 = 37;
                        }
                        else
                        {
                            b0 = 2;
                        }

                        this.world.setEntityState(this, b0);
                    }

                    if (source != DamageSource.DROWN && (!flag || amount > 0.0F))
                    {
                        this.setBeenAttacked();
                    }

                    if (entity1 != null)
                    {
                        double d1 = entity1.posX - this.posX;
                        double d0;

                        for (d0 = entity1.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                        {
                            d1 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * (180D / Math.PI) - (double)this.rotationYaw);
                        this.knockBack(entity1, 0.4f, d1, d0);
                    }
                    else
                    {
                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
                    }
                }

                if (this.getHealth() <= 0.0F)
                {
                    if (true)
                    {
                        SoundEvent soundevent = this.getDeathSound();

                        if (flag1 && soundevent != null)
                        {
                            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
                        }

                        this.onDeath(source);
                    }
                }
                else if (flag1)
                {
                    this.playHurtSound(source);
                }

                boolean flag2 = !flag || amount > 0.0F;

                if (flag2)
                {
                    this.lastDamageSource = source;
                    this.lastDamageStamp = this.world.getTotalWorldTime();
                }


                if (entity1 instanceof EntityPlayerMP)
                {
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((EntityPlayerMP)entity1, this, source, f, amount, flag);
                }

                return flag2;
            }
        }
    }
}