package com.vicmatskiv.weaponlib.grenade;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.Explosion;
import com.vicmatskiv.weaponlib.LightExposure;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTracing;
import com.vicmatskiv.weaponlib.compatibility.CompatibleVec3;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFlashGrenade extends AbstractEntityGrenade {

    private static final Logger logger = LogManager.getLogger(EntityFlashGrenade.class);

    public static int MAX_EFFECTIVE_DISTANCE = 15;
    private long explosionTimeout;
    private float explosionStrength;
    private boolean destroyBlocks;
    private long activationTimestamp;

    public int effectiveDistance;

    public static class Builder {

        private long explosionTimeout;
        private float explosionStrength;
        private boolean isDestroyingBlocks = true;
        private long activationTimestamp;
        private EntityLivingBase thrower;
        private ItemGrenade itemGrenade;
        private float velocity = ItemGrenade.DEFAULT_VELOCITY;
        private float gravityVelocity = ItemGrenade.DEFAULT_GRAVITY_VELOCITY;
        private float rotationSlowdownFactor = ItemGrenade.DEFAULT_ROTATION_SLOWDOWN_FACTOR;
        private int effectiveDistance = MAX_EFFECTIVE_DISTANCE;

        public Builder withActivationTimestamp(long activationTimestamp) {
            this.activationTimestamp = activationTimestamp;
            return this;
        }

        public Builder withExplosionTimeout(long explosionTimeout) {
            this.explosionTimeout = explosionTimeout;
            return this;
        }

        public Builder withThrower(EntityLivingBase thrower) {
            this.thrower = thrower;
            return this;
        }

        public Builder withExplosionStrength(float explosionStrength) {
            this.explosionStrength = explosionStrength;
            return this;
        }
        
        public Builder withEffectiveDistance(int effectiveDistance) {
            this.effectiveDistance = effectiveDistance > MAX_EFFECTIVE_DISTANCE ? MAX_EFFECTIVE_DISTANCE : effectiveDistance;
            return this;
        }

        public Builder withGrenade(ItemGrenade itemGrenade) {
            this.itemGrenade = itemGrenade;
            return this;
        }

        public Builder withVelocity(float velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder withGravityVelocity(float gravityVelocity) {
            this.gravityVelocity = gravityVelocity;
            return this;
        }

        public Builder withRotationSlowdownFactor(float rotationSlowdownFactor) {
            this.rotationSlowdownFactor = rotationSlowdownFactor;
            return this;
        }
        
        public Builder withDestroyingBlocks(boolean isDestroyingBlocks) {
            this.isDestroyingBlocks = isDestroyingBlocks;
            return this;
        }

        public EntityFlashGrenade build(ModContext modContext) {
            EntityFlashGrenade entityGrenade = new EntityFlashGrenade(modContext, itemGrenade, thrower, velocity,
                    gravityVelocity, rotationSlowdownFactor);
            entityGrenade.activationTimestamp = activationTimestamp;
            entityGrenade.explosionTimeout = explosionTimeout;
            entityGrenade.explosionStrength = explosionStrength;
            entityGrenade.itemGrenade = itemGrenade;
            entityGrenade.destroyBlocks = isDestroyingBlocks;
            entityGrenade.effectiveDistance = effectiveDistance;

            return entityGrenade;
        }

    }

    private EntityFlashGrenade(ModContext modContext, ItemGrenade itemGrenade, EntityLivingBase thrower, float velocity, float gravityVelocity, float rotationSlowdownFactor) {
        super(modContext, itemGrenade, thrower, velocity, gravityVelocity, rotationSlowdownFactor);
    }

    public EntityFlashGrenade(World world) {
        super(world);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeLong(activationTimestamp);
        buffer.writeLong(explosionTimeout);
        buffer.writeFloat(explosionStrength);
        buffer.writeBoolean(destroyBlocks);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        activationTimestamp = buffer.readLong();
        explosionTimeout = buffer.readLong();
        explosionStrength = buffer.readFloat();
        destroyBlocks = buffer.readBoolean();
    }

    @Override
    public void onGrenadeUpdate() {
        if (!compatibility.world(this).isRemote && explosionTimeout > 0
                && System.currentTimeMillis() > activationTimestamp + explosionTimeout) {
            explode();
            return;
        }
    }

    @Override
    public void onBounce(CompatibleRayTraceResult movingobjectposition) {
//        System.out.println("Bounce");
        if(explosionTimeout == ItemGrenade.EXPLODE_ON_IMPACT && !compatibility.world(this).isRemote) {
            explode();
        } else {
            super.onBounce(movingobjectposition);
        }
    }

    private void explode() {

        logger.debug("Exploding {}", this);
        
//        explosionStrength = 0.1f;
        Explosion.createServerSideExplosion(modContext, compatibility.world(this), this,
                this.posX, this.posY, this.posZ, explosionStrength, false, false, false, 1f, 1f, 1.5f, 1f, null, null, 
                modContext.getFlashExplosionSound());


        List<?> nearbyEntities = compatibility.getEntitiesWithinAABBExcludingEntity(compatibility.world(this), this,
                compatibility.getBoundingBox(this).expand(effectiveDistance, effectiveDistance, effectiveDistance));

        Float damageCoefficient = modContext.getConfigurationManager().getExplosions().getDamage();

//        float effectiveRadius = itemGrenade.getEffectiveRadius() * damageCoefficient; // 5 block sphere with this entity as a center

        for(Object nearbyEntityObject: nearbyEntities) {
            Entity nearbyEntity = (Entity)nearbyEntityObject;
            if(nearbyEntity instanceof EntityPlayer) {

                final CompatibleVec3 grenadePos = new CompatibleVec3(this.posX, this.posY, this.posZ);
                BiPredicate<Block, CompatibleBlockState> isCollidable = (block, blockMetadata) -> 
                    !isTransparentBlock(block)
                    && compatibility.canCollideCheck(block, blockMetadata, false);
                
                EntityPlayer player = (EntityPlayer) nearbyEntity;
                Vec3d playerLookVec = player.getLook(1f);
                Vec3d playerEyePosition = player.getPositionEyes(1f);
                Vec3d playerGrenadeVector = playerEyePosition.subtractReverse(new Vec3d(this.posX, this.posY, this.posZ));
                
                double dotProduct = playerLookVec.dotProduct(playerGrenadeVector);
                double cos = dotProduct / 
                        (MathHelper.sqrt(playerLookVec.lengthSquared()) * MathHelper.sqrt(playerGrenadeVector.lengthSquared()));
                
                System.out.println("Cos: " + cos);
                
                float exposureFactor = (float) ((cos + 1f)/ 2f);
                exposureFactor *= exposureFactor;
                
                System.out.println("Exposure factor: " + exposureFactor);
                
                final CompatibleVec3 compatiblePlayerEyePos = new CompatibleVec3(playerEyePosition.x, playerEyePosition.y, playerEyePosition.z);
                CompatibleRayTraceResult rayTraceResult = CompatibleRayTracing.rayTraceBlocks(compatibility.world(this), grenadePos, compatiblePlayerEyePos, isCollidable);

                if(rayTraceResult == null) {
                    float dose = exposureFactor * (1f - (float)playerGrenadeVector.lengthSquared() / (effectiveDistance * effectiveDistance));
                    if(dose < 0) {
                        dose = 0f;
                    }
                    LightExposure exposure = CompatibleExposureCapability.getExposure(nearbyEntity, LightExposure.class);
                    if(exposure == null) {
                        System.out.println("Entity " + nearbyEntity + " exposed to light dose " + dose);
                        exposure = new LightExposure(compatibility.world(nearbyEntity).getTotalWorldTime(), 400, dose);
                        CompatibleExposureCapability.updateExposure(nearbyEntity, exposure);
                    } else {
                        float totalDose = exposure.getTotalDose() + dose;
                        if(totalDose > 1f) {
                            totalDose = 1f;
                        }
                        System.out.println("Entity " + nearbyEntity + " exposed to light dose " + totalDose);
                        exposure.setTotalDose(totalDose);
                        CompatibleExposureCapability.updateExposure(nearbyEntity, exposure);
                    }
                } else {
                    CompatibleVec3 hitVec = rayTraceResult.getHitVec();
                    System.out.println("Hit vec: " + hitVec);
                }
            }
        }
        
        this.setDead();
    }

    public ItemGrenade getItemGrenade() {
        return itemGrenade;
    }
    
    private boolean isTransparentBlock(Block block) {
    	return block == Blocks.SAPLING
        || block == Blocks.LEAVES
        || block == Blocks.LEAVES2
        || block == Blocks.GLASS
        || block == Blocks.BED
        || block == Blocks.GOLDEN_RAIL
        || block == Blocks.DETECTOR_RAIL
        || block == Blocks.WEB
        || block == Blocks.TALLGRASS
        || block == Blocks.DEADBUSH
        || block == Blocks.PISTON_HEAD
        || block == Blocks.PISTON_EXTENSION
        || block == Blocks.YELLOW_FLOWER
        || block == Blocks.RED_FLOWER
        || block == Blocks.BROWN_MUSHROOM
        || block == Blocks.RED_MUSHROOM
        || block == Blocks.STONE_SLAB
        || block == Blocks.TORCH
        || block == Blocks.FIRE
        || block == Blocks.MOB_SPAWNER
        || block == Blocks.OAK_STAIRS
        || block == Blocks.REDSTONE_WIRE
        || block == Blocks.WHEAT
        || block == Blocks.STANDING_SIGN
        || block == Blocks.LADDER
        || block == Blocks.RAIL
        || block == Blocks.STONE_STAIRS
        || block == Blocks.WALL_SIGN
        || block == Blocks.LEVER
        || block == Blocks.STONE_PRESSURE_PLATE
        || block == Blocks.WOODEN_PRESSURE_PLATE
        || block == Blocks.UNLIT_REDSTONE_TORCH
        || block == Blocks.REDSTONE_TORCH
        || block == Blocks.STONE_BUTTON
        || block == Blocks.SNOW_LAYER
        || block == Blocks.REEDS
        || block == Blocks.OAK_FENCE
        || block == Blocks.SPRUCE_FENCE
        || block == Blocks.BIRCH_FENCE
        || block == Blocks.JUNGLE_FENCE
        || block == Blocks.DARK_OAK_FENCE
        || block == Blocks.ACACIA_FENCE
        || block == Blocks.PORTAL
        || block == Blocks.CAKE
        || block == Blocks.UNPOWERED_REPEATER
        || block == Blocks.POWERED_REPEATER
        || block == Blocks.MONSTER_EGG
        || block == Blocks.IRON_BARS
        || block == Blocks.GLASS_PANE
        || block == Blocks.PUMPKIN_STEM
        || block == Blocks.MELON_STEM
        || block == Blocks.VINE
        || block == Blocks.OAK_FENCE_GATE
        || block == Blocks.SPRUCE_FENCE_GATE
        || block == Blocks.BIRCH_FENCE_GATE
        || block == Blocks.JUNGLE_FENCE_GATE
        || block == Blocks.DARK_OAK_FENCE_GATE
        || block == Blocks.ACACIA_FENCE_GATE
        || block == Blocks.BRICK_STAIRS
        || block == Blocks.STONE_BRICK_STAIRS
        || block == Blocks.WATERLILY
        || block == Blocks.NETHER_BRICK_FENCE
        || block == Blocks.NETHER_BRICK_STAIRS
        || block == Blocks.NETHER_WART
        || block == Blocks.ENCHANTING_TABLE
        || block == Blocks.BREWING_STAND
        || block == Blocks.DRAGON_EGG
        || block == Blocks.REDSTONE_LAMP
        || block == Blocks.LIT_REDSTONE_LAMP
        || block == Blocks.WOODEN_SLAB
        || block == Blocks.COCOA
        || block == Blocks.SANDSTONE_STAIRS
        || block == Blocks.TRIPWIRE_HOOK
        || block == Blocks.TRIPWIRE
        || block == Blocks.SPRUCE_STAIRS
        || block == Blocks.BIRCH_STAIRS
        || block == Blocks.JUNGLE_STAIRS
        || block == Blocks.FLOWER_POT
        || block == Blocks.CARROTS
        || block == Blocks.POTATOES
        || block == Blocks.WOODEN_BUTTON
        || block == Blocks.SKULL
        || block == Blocks.ANVIL
        || block == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE
        || block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE
        || block == Blocks.UNPOWERED_COMPARATOR
        || block == Blocks.POWERED_COMPARATOR
        || block == Blocks.DAYLIGHT_DETECTOR
        || block == Blocks.DAYLIGHT_DETECTOR_INVERTED
        || block == Blocks.HOPPER
        || block == Blocks.QUARTZ_STAIRS
        || block == Blocks.ACTIVATOR_RAIL
        || block == Blocks.DROPPER
        || block == Blocks.BARRIER
        || block == Blocks.CARPET
        || block == Blocks.ACACIA_STAIRS
        || block == Blocks.DARK_OAK_STAIRS
        || block == Blocks.DOUBLE_PLANT
        || block == Blocks.STAINED_GLASS
        || block == Blocks.STAINED_GLASS_PANE
        || block == Blocks.STANDING_BANNER
        || block == Blocks.WALL_BANNER
        || block == Blocks.RED_SANDSTONE_STAIRS
        || block == Blocks.STONE_SLAB2
        || block == Blocks.END_ROD
        || block == Blocks.BEETROOTS
        || block == Blocks.STRUCTURE_VOID
        || block == Blocks.STRUCTURE_BLOCK;
    }
}
