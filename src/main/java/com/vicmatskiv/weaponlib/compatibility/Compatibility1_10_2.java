package com.vicmatskiv.weaponlib.compatibility;

import java.util.Iterator;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;
import com.vicmatskiv.weaponlib.WorldHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Compatibility1_10_2 implements Compatibility {

	private static CompatibleMathHelper mathHelper = new CompatibleMathHelper();

	@Override
	public World world(Entity entity) {
		return entity.worldObj;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EntityPlayer clientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public void spawnEntity(EntityPlayer player, Entity entity) {
		player.worldObj.spawnEntityInWorld(entity);
	}

	@Override
	public void moveParticle(CompatibleParticle particle, double motionX, double motionY, double motionZ) {
		particle.moveEntity(motionX, motionY, motionZ);
	}

	@Override
	public int getStackSize(ItemStack consumedStack) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player, int maxSize) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public NBTTagCompound getTagCompound(ItemStack itemStack) {
		return itemStack.getTagCompound();
	}

	@Override
	public ItemStack getItemStack(ItemTossEvent event) {
		return event.getEntityItem().getEntityItem();
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
		return WorldHelper.consumeInventoryItem(player.inventory, item);
	}

	@Override
	public void ensureTagCompound(ItemStack itemStack) {
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
	}

	@Override
	public void playSound(EntityPlayer player, CompatibleSound sound, float volume, float pitch) {
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
	public WeaponSpawnEntity getSpawnEntity(Weapon weapon, World world, EntityPlayer player, float speed,
			float gravityVelocity, float inaccuracy, float damage, float explosionRadius,
			Material... damageableBlockMaterials) {
		return new WeaponSpawnEntity(weapon, player.worldObj, player, speed, gravityVelocity, inaccuracy, damage,
				explosionRadius);
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
	public void playSoundToNearExcept(EntityPlayer player, CompatibleSound sound, float volume, float pitch) {
		player.worldObj.playSound(player, player.posX, player.posY, player.posZ, sound.getSound(),
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
		return Minecraft.getMinecraft().fontRendererObj;
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
		Iterator<ItemStack> equipmentIterator = Minecraft.getMinecraft().thePlayer.getEquipmentAndArmor().iterator();
		return equipmentIterator.hasNext() ? equipmentIterator.next() : null;
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
		GameRegistry.register(sound.getSound(), sound.getResourceLocation());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void registerItem(String modId, Item item, String name) {
		GameRegistry.registerItem(item, name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runInMainClientThread(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}

	@Override
	public void registerModEntity(Class<WeaponSpawnEntity> class1, String string, int i, Object mod, int j, int k,
			boolean b) {
	}

	@Override
	public void registerRenderingRegistry(CompatibleRenderingRegistry rendererRegistry) {
		MinecraftForge.EVENT_BUS.register(rendererRegistry);
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
		return new CompatibleRayTraceResult(Minecraft.getMinecraft().objectMouseOver);
	}

	@Override
	public Block getBlockAtPosition(World world, CompatibleRayTraceResult position) {
		Block block = world
				.getBlockState(new BlockPos(position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ()))
				.getBlock();
		return block;
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
				if (--stack.stackSize <= 0) {
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
	public boolean isGlassBlock(Block block) {
		return block == Blocks.GLASS || block == Blocks.GLASS_PANE || block == Blocks.STAINED_GLASS
				|| block == Blocks.STAINED_GLASS_PANE;
	}

	@Override
	public float getEffectOffsetX() {
		return 0f;
	}

	@Override
	public float getEffectOffsetY() {
		return -1.6f;
	}

	@Override
	public float getEffectScaleFactor() {
		return 1f;
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
		} else if(player.inventory.mainInventory[slot] == null) {
        	player.inventory.mainInventory[slot] = new ItemStack(item);
        }
        return result;	}

	@Override
	public ItemStack getInventoryItemStack(EntityPlayer player, int inventoryItemIndex) {
		return player.inventory.getStackInSlot(inventoryItemIndex);
	}

	@Override
	public int getInventorySlot(EntityPlayer player, ItemStack itemStack) {
		int slot = -1;
		for(int i = 0; i < player.inventory.mainInventory.length; i++) {
			if(player.inventory.mainInventory[i] == itemStack) {
				slot = i;
				break;
			}
		}
		return slot;	}

	@Override
	public boolean consumeInventoryItemFromSlot(EntityPlayer player, int slot) {
		if(player.inventory.mainInventory[slot] == null) {
			return false;
		}
		
		if (--player.inventory.mainInventory[slot].stackSize <= 0) {
			player.inventory.mainInventory[slot] = null;
        }
		return true;	}

	@Override
	public void addShapedRecipe(ItemStack itemStack, Object... materials) {
		throw new UnsupportedOperationException("Implement me");
	}

	@Override
	public void disableLightMap() {
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
	}

	@Override
	public void enableLightMap() {
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}

	@Override
	public void registerBlock(String modId, Block block, String name){
		GameRegistry.registerBlock(block, name);
	}
}
