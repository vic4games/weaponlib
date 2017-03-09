package com.vicmatskiv.weaponlib.compatibility;

import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public interface Compatibility {
	
	public World world(Entity entity);
	
	public EntityPlayer clientPlayer();
	
	public WeaponSpawnEntity getSpawnEntity(Weapon weapon, World world, EntityPlayer player, float speed, 
			float gravityVelocity, float inaccuracy, float damage, float explosionRadius, Material...damageableBlockMaterials);
	
	public IAttribute getMovementSpeedAttribute();
	
	public NBTTagCompound getTagCompound(ItemStack itemStack);
	
	public void setTagCompound(ItemStack itemStack, NBTTagCompound tagCompound);

	public ItemStack getItemStack(ItemTossEvent event);
	
	public EntityPlayer getPlayer(ItemTossEvent event);
	
	public ItemStack getHeldItemMainHand(EntityLivingBase player);
	
	public boolean consumeInventoryItem(EntityPlayer player, Item item);
	
	public int getCurrentInventoryItemIndex(EntityPlayer player);
	
	public void ensureTagCompound(ItemStack itemStack);
	
	public void playSound(EntityPlayer player, CompatibleSound sound, float volume, float pitch);
	
	public void playSoundToNearExcept(EntityPlayer player, CompatibleSound object, float volume, float pitch);

	public boolean isClientSide();

	public CompatibleMathHelper getMathHelper();

	public EntityPlayer getClientPlayer();

	public FontRenderer getFontRenderer();

	public ScaledResolution getResolution(Pre event);

	public ElementType getEventType(Pre event);

	public ItemStack getHelmet();

	public CompatibleVec3 getLookVec(EntityPlayer player);

	public void registerKeyBinding(KeyBinding key);

	public void registerWithEventBus(Object object);

	public void registerWithFmlEventBus(Object object);

	public void registerSound(CompatibleSound sound);

	public void registerItem(Item item, String name);

	public void runInMainClientThread(Runnable runnable);

	public void registerModEntity(Class<WeaponSpawnEntity> class1, String string, int i, Object mod, int j, int k,
			boolean b);

	public void registerRenderingRegistry(CompatibleRenderingRegistry rendererRegistry);

	public <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String... fieldNames);

	public int getButton(MouseEvent event);

	public EntityPlayer getEntity(FOVUpdateEvent event);

	public EntityLivingBase getEntity(RenderLivingEvent.Pre event);

	public void setNewFov(FOVUpdateEvent event, float fov);

	public RenderPlayer getRenderer(RenderLivingEvent.Pre event);

	public GuiScreen getGui(GuiOpenEvent event);

	public void setAimed(RenderPlayer rp, boolean aimed);

	public CompatibleRayTraceResult getObjectMouseOver();

	public Block getBlockAtPosition(World world, CompatibleRayTraceResult position);

	public void destroyBlock(World world, CompatibleRayTraceResult position);
	
	public boolean addItemToPlayerInventory(EntityPlayer player, final Item item, int slot);

	public boolean consumeInventoryItem(InventoryPlayer inventoryPlayer, Item item);

	public ItemStack itemStackForItem(Item item, Predicate<ItemStack> condition, EntityPlayer player);

	public boolean isGlassBlock(Block block);

	public float getEffectOffsetX();
	
	public float getEffectOffsetY();
	
	public float getEffectScaleFactor();

	public void spawnEntity(EntityPlayer player, Entity entity);

	public void moveParticle(CompatibleParticle particle, double motionX, double motionY, double motionZ);

	public int getStackSize(ItemStack consumedStack);

	public ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player, int maxSize);

	public ItemStack getInventoryItemStack(EntityPlayer player, int inventoryItemIndex);
	
	public int getInventorySlot(EntityPlayer player, ItemStack itemStack);

	public boolean consumeInventoryItemFromSlot(EntityPlayer player, int nextAttachmentSlot);

	public void addShapedRecipe(ItemStack itemStack, Object... materials);

	public void disableLightMap();

	public void enableLightMap();

	public void registerBlock(String modId, Block block, String name);
}
