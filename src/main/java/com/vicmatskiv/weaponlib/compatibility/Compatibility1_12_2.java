package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.Explosion;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.ai.EntityCustomMob;
import com.vicmatskiv.weaponlib.compatibility.CompatibleParticle.CompatibleParticleBreaking;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderManager;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

@SuppressWarnings("deprecation")
public class Compatibility1_12_2 implements Compatibility {

    private static final float DEFAULT_SHELL_CASING_FORWARD_OFFSET = 0.1f;

    private static DamageSource GENERIC_DAMAGE_SOURCE = new DamageSource("thrown");

    private static CompatibleMathHelper mathHelper = new CompatibleMathHelper();

    @Override
    public World world(Entity entity) {
        return entity.world;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EntityPlayer clientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setClientPlayer(EntityPlayer player) {
        Minecraft.getMinecraft().player = (EntityPlayerSP) player;
    }

    @Override
    public void spawnEntity(EntityLivingBase player, Entity entity) {
        if(player != null) {
            player.world.spawnEntity(entity);
        }
    }

    @Override
    public void moveParticle(CompatibleParticle particle, double motionX, double motionY, double motionZ) {
        particle.move(motionX, motionY, motionZ);
    }

    @Override
    public int getStackSize(ItemStack consumedStack) {
        return consumedStack.getCount();
    }

    @Override
    public NBTTagCompound getTagCompound(ItemStack itemStack) {
        return itemStack.getTagCompound();
    }

    @Override
    public ItemStack getItemStack(ItemTossEvent event) {
        return event.getEntityItem().getItem();
    }

    @Override
    public EntityPlayer getPlayer(ItemTossEvent event) {
        return event.getPlayer();
    }

    @Override
    public ItemStack getHeldItemMainHand(EntityLivingBase player) {
        return player.getHeldItemMainhand();
    }

    @Override
    public boolean consumeInventoryItem(EntityPlayer player, Item item) {
        return consumeInventoryItem(player.inventory, item);
    }

    @Override
    public void ensureTagCompound(ItemStack itemStack) {
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
    }

    @Override
    public void playSound(EntityLivingBase player, CompatibleSound sound, float volume, float pitch) {
        if(sound != null) {
            player.playSound(sound.getSound(), volume, pitch);
        }
    }

    @Override
    public IAttribute getMovementSpeedAttribute() {
        return SharedMonsterAttributes.MOVEMENT_SPEED;
    }

    @Override
    public void setTagCompound(ItemStack itemStack, NBTTagCompound tagCompound) {
        itemStack.setTagCompound(tagCompound);
    }

    @Override
    public boolean isClientSide() {
        return FMLCommonHandler.instance().getSide() == Side.CLIENT;
    }

    @Override
    public CompatibleMathHelper getMathHelper() {
        return mathHelper;
    }

    @Override
    public void playSoundToNearExcept(EntityLivingBase player, CompatibleSound sound, float volume, float pitch) {
        player.world.playSound(player instanceof EntityPlayer ? (EntityPlayer) player : null, player.posX, player.posY, player.posZ, sound.getSound(),
                player.getSoundCategory(), volume, pitch);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public EntityPlayer getClientPlayer() {
        return FMLClientHandler.instance().getClientPlayerEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ScaledResolution getResolution(Pre event) {
        return event.getResolution();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ElementType getEventType(Pre event) {
        return event.getType();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getHelmet() {
        return Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    }

    @Override
    public CompatibleVec3 getLookVec(EntityPlayer player) {
        return new CompatibleVec3(player.getLookVec());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerKeyBinding(KeyBinding key) {
        ClientRegistry.registerKeyBinding(key);
    }

    @Override
    public void registerWithEventBus(Object object) {
        MinecraftForge.EVENT_BUS.register(object);
    }

    @Override
    public void registerWithFmlEventBus(Object object) {
        MinecraftForge.EVENT_BUS.register(object);
    }

    @Override
    public void registerSound(CompatibleSound sound) {
        sound.getSound().setRegistryName(sound.getResourceLocation());
        ForgeRegistries.SOUND_EVENTS.register(sound.getSound());
        //GameRegistry.register(sound.getSound(), sound.getResourceLocation());
    }

    @Override
    public void registerItem(Item item, String name) {
        item.setRegistryName("mw", name); // temporary hack
        ForgeRegistries.ITEMS.register(item);
        //GameRegistry.register(item, new ResourceLocation("mw", name)); // temporary hack
    }

    @Override
    public void registerItem(String modId, Item item, String name) {
        if(item.getRegistryName() == null) {
            String registryName = item.getUnlocalizedName().toLowerCase();
            int indexOfPrefix = registryName.indexOf("." + modId);
            if(indexOfPrefix > 0) {
                registryName = registryName.substring(indexOfPrefix + modId.length() + 2);
            }
            item.setRegistryName(modId, registryName);
        }
        //GameRegistry.register(item);
        ForgeRegistries.ITEMS.register(item);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void runInMainClientThread(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @Override
    public void registerModEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod,
            String modId, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity
            (new ResourceLocation(modId, entityName), entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderingRegistry(CompatibleRenderingRegistry rendererRegistry) {
        MinecraftForge.EVENT_BUS.register(rendererRegistry);
        ModelLoaderRegistry.registerLoader(rendererRegistry);
    }

    @Override
    public <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String... fieldNames) {
        return ObfuscationReflectionHelper.getPrivateValue(classToAccess, instance, fieldNames);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getButton(MouseEvent event) {
        return event.getButton();
    }

    @Override
    public EntityPlayer getEntity(FOVUpdateEvent event) {
        return event.getEntity();
    }

    @Override
    public EntityLivingBase getEntity(@SuppressWarnings("rawtypes") RenderLivingEvent.Pre event) {
        return event.getEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setNewFov(FOVUpdateEvent event, float fov) {
        event.setNewfov(fov);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderPlayer getRenderer(@SuppressWarnings("rawtypes") RenderLivingEvent.Pre event) {
        return (RenderPlayer) event.getRenderer();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(GuiOpenEvent event) {
        return event.getGui();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setAimed(RenderPlayer rp, boolean aimed) {
        if (aimed) {
            rp.getMainModel().leftArmPose = ArmPose.BOW_AND_ARROW;
            rp.getMainModel().rightArmPose = ArmPose.BOW_AND_ARROW;
        } else {
            rp.getMainModel().leftArmPose = ArmPose.EMPTY;
            rp.getMainModel().rightArmPose = ArmPose.ITEM;
        }
    }

    @Override
    public CompatibleRayTraceResult getObjectMouseOver() {
        return CompatibleRayTraceResult.fromRayTraceResult(Minecraft.getMinecraft().objectMouseOver);
    }

    @Override
    public CompatibleBlockState getBlockAtPosition(World world, CompatibleRayTraceResult position) {
        IBlockState blockState = world.getBlockState(
                new BlockPos(position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ()));
        return CompatibleBlockState.fromBlockState(blockState);
    }

    @Override
    public void destroyBlock(World world, CompatibleRayTraceResult position) {
        world.destroyBlock(new BlockPos(position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ()),
                true);
    }

    @Override
    public boolean consumeInventoryItem(InventoryPlayer inventoryPlayer, Item item) {
        boolean result = false;
        for (int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
            ItemStack stack = inventoryPlayer.getStackInSlot(i);
            if (stack != null && stack.getItem() == item) {
                stack.shrink(1);
                if (stack.getCount() <= 0) {
                    inventoryPlayer.setInventorySlotContents(i, null);
                }
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    public ItemStack itemStackForItem(Item item, Predicate<ItemStack> condition, EntityPlayer player) {
        ItemStack result = null;

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == item && condition.test(stack)) {
                result = stack;
                break;
            }
        }

        return result;
    }

    @Override
    public boolean isGlassBlock(CompatibleBlockState blockState) {
        Block block = blockState.getBlockState().getBlock();
        return block == Blocks.GLASS || block == Blocks.GLASS_PANE || block == Blocks.STAINED_GLASS
                || block == Blocks.STAINED_GLASS_PANE;
    }

    @Override
    public float getEffectOffsetX() {
		return -0.02f;
    }

    @Override
    public float getEffectOffsetY() {
		return -1.65f;
    }

    @Override
    public float getEffectScaleFactor() {
        return 2.0f;
    }

    @Override
    public int getCurrentInventoryItemIndex(EntityPlayer player) {
        return player.inventory.currentItem;
    }

    @Override
    public boolean addItemToPlayerInventory(EntityPlayer player, Item item, int slot) {
        boolean result = false;
        if(slot == -1) {
            player.inventory.addItemStackToInventory(new ItemStack(item));
        } else if(player.inventory.mainInventory.get(slot) == null) {
            player.inventory.mainInventory.set(slot, new ItemStack(item));
        }
        return result;
    }

    @Override
    public ItemStack getInventoryItemStack(EntityPlayer player, int inventoryItemIndex) {
        return player.inventory.getStackInSlot(inventoryItemIndex);
    }

    @Override
    public int getInventorySlot(EntityPlayer player, ItemStack itemStack) {
        int slot = -1;
        for(int i = 0; i < player.inventory.mainInventory.size(); i++) {
            if(player.inventory.mainInventory.get(i) == itemStack) {
                slot = i;
                break;
            }
        }
        return slot;    }

    @Override
    public boolean consumeInventoryItemFromSlot(EntityPlayer player, int slot) {
        if(player.inventory.getStackInSlot(slot) == null) {
            return false;
        }

        player.inventory.getStackInSlot(slot).shrink(1);
        if (player.inventory.mainInventory.get(slot).getCount() <= 0) {
            player.inventory.removeStackFromSlot(slot);
        }
        return true;
    }

    @Override
    public void addShapedRecipe(ItemStack itemStack, Object... materials) {
        //GameRegistry.addShapedRecipe(itemStack, materials);
        ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, itemStack, materials)
                .setMirrored(false)
                .setRegistryName("mw", itemStack.getItem().getUnlocalizedName() + "_recipe"));

    }

    @Override
    public void addShapedOreRecipe(ItemStack itemStack, Object... materials) {
        //GameRegistry.addRecipe(new ShapedOreRecipe(itemStack, materials).setMirrored(false));
        ForgeRegistries.RECIPES.register(
                new ShapedOreRecipe(null, itemStack, materials)
                .setMirrored(false)
                .setRegistryName("mw", itemStack.getItem().getUnlocalizedName() + "_recipe") // TODO: temporary hack
                );
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void disableLightMap() {
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void enableLightMap() {
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
    }

    @Override
    public void registerBlock(String modId, Block block, String name) {
        if(block.getRegistryName() == null) {
            if(block.getUnlocalizedName().length() < modId.length() + 2 + 5) {
                throw new IllegalArgumentException("Unlocalize block name too short " + block.getUnlocalizedName());
            }
            String unlocalizedName = block.getUnlocalizedName().toLowerCase();
            String registryName = unlocalizedName.substring(5 + modId.length() + 1);
            block.setRegistryName(modId, registryName);
        }

        //GameRegistry.register(block);
        ForgeRegistries.BLOCKS.register(block);
        ItemBlock itemBlock = new ItemBlock(block);
        //GameRegistry.register(itemBlock.setRegistryName(block.getRegistryName()));
        itemBlock.setRegistryName(block.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);
    }

    @Override
    public void registerWorldGenerator(CompatibleWorldGenerator generator, int modGenerationWeight) {
        GameRegistry.registerWorldGenerator(generator, modGenerationWeight);
    }

    @Override
    public ArmorMaterial addArmorMaterial(String name, String textureName, int durability, int[] reductionAmounts,
            int enchantability, CompatibleSound soundOnEquip, float toughness) {
        return EnumHelper.addArmorMaterial(name, textureName, durability, reductionAmounts, enchantability,
                soundOnEquip != null ? soundOnEquip.getSound() : null, toughness);
    }

    @Override
    public boolean inventoryHasFreeSlots(EntityPlayer player) {
        boolean result = false;
        for(int i = 0; i < player.inventory.mainInventory.size(); i++) {
            if(player.inventory.getStackInSlot(i).isEmpty()) {
                result = true;
                break;
            }
        }
        return result;
    }

//    @Override
//    public void addBlockHitEffect(CompatibleRayTraceResult position) {
//        for(int i = 0; i < 6; i++) {
//            Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(
//                    position.getBlockPos().getBlockPos(), position.getSideHit().getEnumFacing());
//        }
//    }

    @Override
    public String getDisplayName(EntityPlayer player) {
        return player.getDisplayNameString();
    }

    @Override
    public void clickBlock(CompatibleBlockPos blockPos, CompatibleEnumFacing sideHit) {
        Minecraft.getMinecraft().playerController.clickBlock(blockPos.getBlockPos(), sideHit.getEnumFacing());
    }

    @Override
    public boolean isAirBlock(World world, CompatibleBlockPos blockPos) {
        return world.isAirBlock(blockPos.getBlockPos());
    }

    @Override
    public void addChatMessage(Entity entity, String message) {
        entity.sendMessage(new TextComponentString(message));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderGlobal createCompatibleRenderGlobal() {
        return /*Minecraft.getMinecraft().renderGlobal; //*/ new CompatibleRenderGlobal(Minecraft.getMinecraft());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public CompatibleParticleManager createCompatibleParticleManager(WorldClient world) {
        return new CompatibleParticleManager(world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Entity getRenderViewEntity() {
        return Minecraft.getMinecraft().getRenderViewEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setRenderViewEntity(Entity entity) {
        Minecraft.getMinecraft().setRenderViewEntity(entity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public CompatibleParticleManager getCompatibleParticleManager() {
        return new CompatibleParticleManager(Minecraft.getMinecraft().effectRenderer);
    }

    @Override
    public void addBlockHitEffect(int x, int y, int z, CompatibleEnumFacing sideHit) {
        for(int i = 0; i < 6; i++) {
            Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(
                    new BlockPos(x, y, z), sideHit.getEnumFacing());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addBreakingParticle(ModContext modContext, double x, double y, double z) {
        double yOffset = 1;
        CompatibleParticleBreaking particle = CompatibleParticle.createParticleBreaking(
                modContext, world(clientPlayer()), x, y + yOffset, z);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    @Override
    public float getAspectRatio(ModContext modContext) {
        return modContext.getAspectRatio();
    }
    
    private static int findGreatesItemIndex(Collection<? extends Item> compatibleItems, Comparator<ItemStack> comparator, EntityPlayer player) {
        ItemStack maxStack = null;
        int maxItemIndex = -1;
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            if (player.inventory.getStackInSlot(i) != null
                    && compatibleItems.contains(player.inventory.getStackInSlot(i).getItem())
                    && (maxStack == null || comparator.compare(player.inventory.getStackInSlot(i), maxStack) > 0)) {
                maxStack = player.inventory.getStackInSlot(i);
                maxItemIndex = i;
            }
        }
        return maxItemIndex;
    }
    
    @Override
    public ItemStack tryConsumingCompatibleItem(Collection<? extends Item> compatibleItems, Comparator<ItemStack> comparator, EntityPlayer player) {

        int maxSize = 1;
//        if(maxSize <= 0) {
//            return null;
//        }

        int i = findGreatesItemIndex(compatibleItems, comparator, player);

        if (i < 0) {
            return null;
        } else {
            ItemStack stackInSlot = player.inventory.getStackInSlot(i);
            int consumedStackSize = maxSize >= getStackSize(stackInSlot) ? getStackSize(stackInSlot) : maxSize;
            ItemStack result = stackInSlot.splitStack(consumedStackSize);
            if (getStackSize(stackInSlot) <= 0) {
                player.inventory.removeStackFromSlot(i);
            }
            return result;
        }
    }


    @Override
    public void setStackSize(ItemStack itemStack, int size) {
        itemStack.setCount(size);
    }

    private static int itemSlotIndex(Item item, Predicate<ItemStack> condition, EntityPlayer player) {
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            if (player.inventory.getStackInSlot(i) != null
                    && player.inventory.getStackInSlot(i).getItem() == item
                    && condition.test(player.inventory.getStackInSlot(i))) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player, int maxSize) {

        if(maxSize <= 0) {
            return null;
        }

        int i = itemSlotIndex(item, condition, player);

        if (i < 0) {
            return null;
        } else {
            ItemStack stackInSlot = player.inventory.getStackInSlot(i);
            int consumedStackSize = maxSize >= getStackSize(stackInSlot) ? getStackSize(stackInSlot) : maxSize;
            ItemStack result = stackInSlot.splitStack(consumedStackSize);
            if (getStackSize(stackInSlot) <= 0) {
                player.inventory.removeStackFromSlot(i);
            }
            return result;
        }
    }

    public ItemStack tryConsumingCompatibleItem(List<? extends Item> compatibleParts, int maxSize,
            EntityPlayer player, @SuppressWarnings("unchecked") Predicate<ItemStack> ...conditions) {
        ItemStack resultStack = null;
        for(Predicate<ItemStack> condition: conditions) {
            for(Item item: compatibleParts) {
                if((resultStack = consumeInventoryItem(item, condition, player, maxSize)) != null) {
                    break;
                }
            }
            if(resultStack != null) break;
        }

        return resultStack;
    }

    @Override
    public CompatibleRayTraceResult rayTraceBlocks(Entity entity, CompatibleVec3 vec3, CompatibleVec3 vec31) {
        return CompatibleRayTraceResult.fromRayTraceResult(entity.getEntityWorld().rayTraceBlocks(vec3.getVec(), vec31.getVec()));
    }

    @Override
    public CompatibleAxisAlignedBB expandEntityBoundingBox(Entity entity1, double f1, double f2, double f3) {
        return new CompatibleAxisAlignedBB(entity1.getEntityBoundingBox().expand(f1, f2, f3));
    }

    @Override
    public CompatibleAxisAlignedBB getBoundingBox(Entity entity) {
        return new CompatibleAxisAlignedBB(entity.getEntityBoundingBox());
    }

    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(World world, Entity entity, CompatibleAxisAlignedBB boundingBox) {
        return world.getEntitiesWithinAABBExcludingEntity(entity, boundingBox.getBoundingBox());
    }

    @Override
    public void spawnParticle(World world, String particleName, double xCoord, double yCoord, double zCoord,
            double xSpeed, double ySpeed, double zSpeed) {
        EnumParticleTypes particleType = EnumParticleTypes.getByName(particleName);
        if(particleType != null) {
            world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public CompatibleBlockState getBlockAtPosition(World world, CompatibleBlockPos blockPos) {
        return CompatibleBlockState.fromBlockState(world.getBlockState(blockPos.getBlockPos()));
    }

    @Override
    public Item findItemByName(String modId, String itemName) {
        return Item.REGISTRY.getObject(new ResourceLocation(modId, itemName));
    }

    @Override
    public String getPlayerName(EntityPlayer player) {
        return player.getName();
    }

    @Override
    public boolean isBlockPenetratableByBullets(Block block) {
        return block == Blocks.AIR
                || block == Blocks.TALLGRASS
                || block == Blocks.LEAVES
                || block == Blocks.LEAVES2
                || block == Blocks.FIRE
                || block == Blocks.HAY_BLOCK
                || block == Blocks.DOUBLE_PLANT
                || block == Blocks.WEB
                || block == Blocks.WHEAT;
    }

    @Override
    public boolean canCollideCheck(Block block, CompatibleBlockState metadata, boolean hitIfLiquid) {
        return block.canCollideCheck(metadata.getBlockState(), hitIfLiquid);
    }

    @Override
    public float getCompatibleShellCasingForwardOffset() {
        return DEFAULT_SHELL_CASING_FORWARD_OFFSET ;
    }


    @Override
    public boolean madeFromHardMaterial(CompatibleBlockState blockState) {
        Material material = blockState.getBlockState().getMaterial();

        return material == Material.ROCK
                || material == Material.IRON
                || material == Material.ICE
                || material == Material.WOOD;
    }

    @Override
    public void playSoundAtEntity(Entity entity, CompatibleSound sound, float volume, float pitch) {
        if(sound != null) {
            entity.playSound(sound.getSound(), volume, pitch);
        }
    }

    @Override
    public double getBlockDensity(World world, CompatibleVec3 vec3, CompatibleAxisAlignedBB boundingBox) {
        return world.getBlockDensity(vec3.getVec(), boundingBox.getBoundingBox());
    }

    @Override
    public boolean isImmuneToExplosions(Entity entity) {
        return entity.isImmuneToExplosions();
    }

    @Override
    public boolean isAirBlock(CompatibleBlockState blockState) {
        return blockState.getBlockState().getBlock() == Blocks.AIR;
    }

    private net.minecraft.world.Explosion getCompatibleExplosion(Explosion e) {
        return new net.minecraft.world.Explosion(
                e.getWorld(), e.getExploder(),
                e.getExplosionX(), e.getExplosionY(), e.getExplosionZ(),
                e.getExplosionSize(), false, true);
    }

    @Override
    public boolean canDropBlockFromExplosion(CompatibleBlockState blockState, Explosion e) {
        return blockState.getBlockState().getBlock().canDropFromExplosion(getCompatibleExplosion(e));
    }

    @Override
    public void onBlockExploded(World world, CompatibleBlockState blockState, CompatibleBlockPos blockpos, Explosion explosion) {
        blockState.getBlockState().getBlock().onBlockExploded(world, blockpos.getBlockPos(), getCompatibleExplosion(explosion));
    }

    @Override
    public float getExplosionResistance(World worldObj, CompatibleBlockState blockState, CompatibleBlockPos blockpos, Entity entity,
            Explosion explosion) {
        return blockState.getBlockState().getBlock().getExplosionResistance(entity);
    }

    @Override
    public float getExplosionResistance(World worldObj, Entity exploder, Explosion explosion,
            CompatibleBlockPos blockpos, CompatibleBlockState blockState) {
        return exploder.getExplosionResistance(getCompatibleExplosion(explosion), worldObj, blockpos.getBlockPos(), blockState.getBlockState());
    }

    @Override
    public boolean isSpectator(EntityPlayer entityplayer) {
        return entityplayer.isSpectator();
    }

    @Override
    public boolean isCreative(EntityPlayer entityplayer) {
        return entityplayer.isCreative();
    }

    @Override
    public void setBlockToFire(World world, CompatibleBlockPos blockpos1) {
        world.setBlockState(blockpos1.getBlockPos(), Blocks.FIRE.getDefaultState());
    }

    @Override
    public DamageSource getDamageSource(Explosion explosion) {
        return DamageSource.causeExplosionDamage(getCompatibleExplosion(explosion));
    }

    @Override
    public double getBlastDamageReduction(EntityLivingBase entity, double d10) {
        return EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
    }

    @Override
    public boolean verifyExplosion(World worldObj, Entity exploder, Explosion explosion, CompatibleBlockPos blockpos,
            CompatibleBlockState blockState, float f) {
        return exploder.canExplosionDestroyBlock(getCompatibleExplosion(explosion), worldObj, blockpos.getBlockPos(), blockState.getBlockState(), f);
//        return exploder.verifyExplosion(getCompatibleExplosion(explosion), worldObj, blockpos.getBlockPos(),
//                blockState.getBlockState(), f);
    }

    @Override
    public boolean isFullBlock(CompatibleBlockState blockState) {
        return blockState.getBlockState().isFullBlock();
    }

    @Override
    public void dropBlockAsItemWithChance(World world, CompatibleBlockState blockState, CompatibleBlockPos blockpos, float f, int i) {
        blockState.getBlockState().getBlock().dropBlockAsItemWithChance(world, blockpos.getBlockPos(), blockState.getBlockState(), f, i);
    }

    @Override
    public CompatibleBlockState getBlockBelow(World world, CompatibleBlockPos blockPos) {
        return CompatibleBlockState.fromBlockState(world.getBlockState(blockPos.getBlockPos().down()));
    }

    @Override
    public void playSound(World world, double posX, double posY, double posZ, CompatibleSound sound,
            float volume, float pitch) {
        if(sound != null) {
            world.playSound(posX, posY, posZ, sound.getSound(), SoundCategory.BLOCKS, volume, pitch, false);
        }
    }

    @Override
    public boolean isBlockPenetratableByGrenades(Block block) {
        return block == Blocks.AIR
                || block == Blocks.TALLGRASS
                || block == Blocks.LEAVES
                || block == Blocks.LEAVES2
                || block == Blocks.FIRE
                || block == Blocks.HAY_BLOCK
                || block == Blocks.DOUBLE_PLANT
                || block == Blocks.WEB
                || block == Blocks.WHEAT;

    }

    @Override
    public DamageSource genericDamageSource() {
        return GENERIC_DAMAGE_SOURCE;
    }

    @Override
    public boolean isCollided(CompatibleParticle particle) {
        return particle.isCollided();
    }

    @Override
    public ItemStack createItemStack(CompatibleItems compatibleItem, int stackSize, int damage) {
        return new ItemStack(compatibleItem.getItem(), stackSize, damage);
    }

    @Override
    public void addSmelting(Block block, ItemStack output, float f) {
        GameRegistry.addSmelting(block, output, f);
    }

    @Override
    public void addSmelting(Item item, ItemStack output, float f) {
        GameRegistry.addSmelting(item, output, f);
    }

    @Override
    public boolean isFlying(EntityPlayer player) {
        return player.capabilities.isFlying;
    }

    @Override
    public String getLocalizedString(String format, Object... args) {
        return I18n.translateToLocalFormatted(format, args);
    }

    @Override
    public void registerEgg(ModContext context, Class<? extends Entity> entityClass, String entityName,
            int primaryEggColor, int secondaryEggColor) {
        EntityRegistry.registerEgg(EntityList.getKey(entityClass), primaryEggColor, secondaryEggColor);
    }

    @Override
    public void addSpawn(Class<? extends EntityLiving> entity, int weightedProb, int min, int max,
            CompatibleBiomeType... biomeTypes) {
        
        
        Set<Biome> biomes = new HashSet<>();
        for(CompatibleBiomeType compatibleType: biomeTypes) {
            for(Type incompatibleType: compatibleType.getTypes()) {
                Set<Biome> biomesForType = BiomeDictionary.getBiomes(incompatibleType);
                for(Biome b: biomesForType) {
                    biomes.add(b);
                }
            }
        }
        
        for(CompatibleBiomeType compatibleType: biomeTypes) {
            for(Type incompatibleType: compatibleType.getTypes()) {
                for(Biome biome: ForgeRegistries.BIOMES) {
                    Set<Type> types = BiomeDictionary.getTypes(biome);
                    if(types.contains(incompatibleType)) {
                        biomes.add(biome);
                    }
                }
            }
        }
        
        EntityRegistry.addSpawn(entity, weightedProb, min, max, EnumCreatureType.MONSTER, biomes.toArray(new Biome[0]));
    }

    @Override
    public void setItemStackToSlot(Entity entity, CompatibleEntityEquipmentSlot compatibleEquipmentSlot, ItemStack itemStack) {
        entity.setItemStackToSlot(compatibleEquipmentSlot.getSlot(), itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ShaderUniform getShaderUniform(ShaderManager shaderManager, String uniformName) {
        return shaderManager.getShaderUniform(uniformName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setUniform(ShaderUniform uniform, float value) {
        uniform.set(value);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setUniform(ShaderUniform uniform, float value1, float value2) {
        uniform.set(value1, value2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setUniform(ShaderUniform uniform, float value1, float value2, float value3) {
        uniform.set(value1, value2, value3);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setUniform(ShaderUniform uniform, float value1, float value2, float value3, float value4) {
        uniform.set(value1, value2, value3, value4);
    }
    
    @Override
    public void setEntityAttribute(EntityLivingBase entity, CompatibleSharedMonsterAttributes attributes, double value) {
        entity.getEntityAttribute(attributes.getAttributes()).setBaseValue(value);
    }

    @Override
    public EnumDifficulty getDifficulty(World world) {
        return world.getDifficulty();
    }
    
    @Override
    public void addStat(EntityPlayer entityplayer, CompatibleAchievement compatibleAchievement) {
        entityplayer.addStat(compatibleAchievement.getAchievement());
    }

    @Override
    public float getLightBrightness(World world, CompatibleBlockPos pos) {
        return world.getLightBrightness(pos.getBlockPos());
    }

    @Override
    public boolean isStrafingSupported() {
        return true;
    }

    @Override
    public void strafe(EntityCustomMob entity, float forward, float strafe) {
        entity.getMoveHelper().strafe(forward, strafe);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void useShader(EntityRenderer entityRenderer, boolean value) {
        ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, entityRenderer, value, "useShader", "field_175083_ad");
    }

    @Override
    public boolean is3dRenderable(Item item) {
        return item instanceof ItemBlock;
    }
    
    @Override
    public float getCompatibleAimingRotationYaw(EntityLivingBase thrower) {
        return thrower.rotationYaw;
    }

    @Override
    public <T> void setPrivateValue(Class<T> class1, T instance, Object value, String...fieldNames) {
        ObfuscationReflectionHelper.setPrivateValue(class1, instance, value, fieldNames);
    }

    @Override
    public ItemStack getHelmet(EntityLivingBase entity) {
        return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    }
    
    @Override
    public CompatibleVec3 getLookVec(EntityLivingBase player) {
        return new CompatibleVec3(player.getLookVec());
    }

    @Override
    public ItemStack createItemStack(NBTTagCompound tagCompound) {
        return new ItemStack(tagCompound);
    }
    
    @Override
    public EntityAITarget createAINearestAttackableTarget(EntityLivingBase e, Class<? extends EntityLivingBase> targetClass,
            boolean checkSight) {
        return new EntityAINearestAttackableTarget<>((EntityCreature)e, targetClass, checkSight);
    }
    
    @Override
    public EntityAIBase createAiAvoidEntity(EntityLivingBase e, Class<? extends EntityLivingBase> entityClassToAvoid,
            float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        return new EntityAIAvoidEntity<>((EntityCreature)e, entityClassToAvoid, avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    @Override
    public ShaderGroup getShaderGroup(EntityRenderer entityRenderer) {
        return entityRenderer.getShaderGroup();
    }

    @Override
    public void setShaderGroup(EntityRenderer entityRenderer, ShaderGroup shaderGroup) {
        compatibility.setPrivateValue(EntityRenderer.class, entityRenderer, shaderGroup, "shaderGroup", "field_147707_d");
    }

    @Override
    public Entity getTrueDamageSource(DamageSource cause) {
        return cause.getTrueSource();
    }
    
    @Override
    public WorldType getWorldType(World world) {
        return world.getWorldType();
    }
}