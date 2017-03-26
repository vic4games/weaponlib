package com.vicmatskiv.weaponlib.compatibility;

import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
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
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class Compatibility1_7_10 implements Compatibility {
	
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
	@SideOnly(Side.CLIENT)
	public void setClientPlayer(EntityPlayer player) {
	    Minecraft.getMinecraft().thePlayer = (EntityClientPlayerMP) player;
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
		return consumedStack.stackSize;
	}
	
	@Override
	public NBTTagCompound getTagCompound(ItemStack itemStack) {
		return itemStack.stackTagCompound;
	}

	@Override
	public ItemStack getItemStack(ItemTossEvent event) {
		return event.entityItem.getEntityItem();
	}
	
	@Override
	public EntityPlayer getPlayer(ItemTossEvent event) {
		return event.player;
	}
	
	@Override
	public ItemStack getHeldItemMainHand(EntityLivingBase player) {
		return player.getHeldItem();
	}

	@Override
	public boolean consumeInventoryItem(EntityPlayer player, Item item) {
		return player.inventory.consumeInventoryItem(item);
	}

	@Override
	public void ensureTagCompound(ItemStack itemStack) {
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
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
		return SharedMonsterAttributes.movementSpeed;
	}

	@Override
	public void setTagCompound(ItemStack itemStack, NBTTagCompound tagCompound) {
		itemStack.stackTagCompound = tagCompound;
	}

	@Override
	public WeaponSpawnEntity getSpawnEntity(Weapon weapon, World world, EntityPlayer player, float spawnEntitySpeed,
			float gravityVelocity, float inaccuracy, float spawnEntityDamage, float spawnEntityExplosionRadius, 
			Material... damageableBlockMaterials) {
		return new WeaponSpawnEntity(weapon, world, player, spawnEntitySpeed,
				gravityVelocity, inaccuracy, spawnEntityDamage, spawnEntityExplosionRadius) {

			@Override
			protected float getGravityVelocity() {
				return gravityVelocity;
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
	public void playSoundToNearExcept(EntityPlayer player, CompatibleSound sound, float f, float g) {
		player.worldObj.playSoundToNearExcept(player, sound.getSound(), 1.0F, 1.0F);

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
		return event.resolution;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ElementType getEventType(Pre event) {
		return event.type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getHelmet() {
		return Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(4);
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
	public void registerWithFmlEventBus(Object object) {
		FMLCommonHandler.instance().bus().register(object);
	}

	@Override
	public void registerWithEventBus(Object object) {
		MinecraftForge.EVENT_BUS.register(object); 
	}

	@Override
	public void registerSound(CompatibleSound sound) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void registerItem(Item item, String name) {
		GameRegistry.registerItem(item, name);
	}

	@Override
	public void registerItem(String modId, Item item, String name) {
		GameRegistry.registerItem(item, name);
	}

	@Override
	public void runInMainClientThread(Runnable runnable) {
		runnable.run();
	}

	@Override
	public void registerModEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, 
			int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	@Override
	public void registerRenderingRegistry(CompatibleRenderingRegistry rendererRegistry) {
		// Not required in 1.7.10
	}

	@Override
	public <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String... fieldNames) {
		return ObfuscationReflectionHelper.getPrivateValue(classToAccess, instance, fieldNames);
	}

	@Override
	public int getButton(MouseEvent event) {
		return event.button;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EntityPlayer getEntity(FOVUpdateEvent event) {
		return event.entity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EntityLivingBase getEntity(RenderLivingEvent.Pre event) {
		return event.entity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setNewFov(FOVUpdateEvent event, float fov) {
		event.newfov = fov;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderPlayer getRenderer(RenderLivingEvent.Pre event) {
		return (RenderPlayer) event.renderer;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(GuiOpenEvent event) {
		return event.gui;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setAimed(RenderPlayer rp, boolean aimed) {
		rp.modelBipedMain.aimedBow = aimed;
	}

	@Override
	public CompatibleRayTraceResult getObjectMouseOver() {
		return new CompatibleRayTraceResult(Minecraft.getMinecraft().objectMouseOver);
	}

	@Override
	public ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player, int maxSize) {
		throw new UnsupportedOperationException("Implement me");
	}
	
	@Override
	public boolean consumeInventoryItem(InventoryPlayer inventoryPlayer, Item item) {
		return inventoryPlayer.consumeInventoryItem(item);
	}

	@Override
	public Block getBlockAtPosition(World world, CompatibleRayTraceResult position) {
		return world.getBlock(position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ());
	}

	@Override
	public void destroyBlock(World world, CompatibleRayTraceResult position) {
		world.func_147480_a(position.getBlockPosX(), position.getBlockPosY(), position.getBlockPosZ(), true);
	}

	@Override
	public ItemStack itemStackForItem(Item item, Predicate<ItemStack> condition, EntityPlayer player) {
		ItemStack result = null;
		for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
	        if (player.inventory.mainInventory[i] != null 
	        		&& player.inventory.mainInventory[i].getItem() == item
	        		&& condition.test(player.inventory.mainInventory[i])) {
	            result = player.inventory.mainInventory[i];
	            break;
	        }
	    }
	
	    return result;
	}

	@Override
	public boolean isGlassBlock(Block block) {
		return block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass 
				|| block == Blocks.stained_glass_pane;
	}

	@Override
	public float getEffectOffsetX() {
		return 0f;
	}

	@Override
	public float getEffectOffsetY() {
		return 0f;
	}

	@Override
	public float getEffectScaleFactor() {
		return 2.3f;
	}

	@Override
	public int getCurrentInventoryItemIndex(EntityPlayer player) {
		return player.inventory.currentItem;
	}

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
		return slot;
	}
	
	public boolean addItemToPlayerInventory(EntityPlayer player, final Item item, int slot) {
		boolean result = false;
		if(slot == -1) {
			player.inventory.addItemStackToInventory(new ItemStack(item));
		} else if(player.inventory.mainInventory[slot] == null) {
        	player.inventory.mainInventory[slot] = new ItemStack(item);
        }
        return result;
    }

	@Override
	public boolean consumeInventoryItemFromSlot(EntityPlayer player, int slot) {
		
		if(player.inventory.mainInventory[slot] == null) {
			return false;
		}
		
		if (--player.inventory.mainInventory[slot].stackSize <= 0) {
			player.inventory.mainInventory[slot] = null;
        }
		return true;
	}

	@Override
	public void addShapedRecipe(ItemStack itemStack,  Object... materials) {
		GameRegistry.addShapedRecipe(itemStack, materials);
	}
	
	@Override
	public void addShapedOreRecipe(ItemStack itemStack, Object... materials) {
	    GameRegistry.addRecipe(new ShapedOreRecipe(itemStack, materials));
	}

	@Override
	public void disableLightMap() {
		Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
	}

	@Override
	public void enableLightMap() {
		Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
	}

	@Override
	public void registerBlock(String modId, Block block, String name){
		GameRegistry.registerBlock(block, name);
	}

	@Override
	public void registerWorldGenerator(IWorldGenerator generator, int modGenerationWeight) {
		GameRegistry.registerWorldGenerator(generator, modGenerationWeight);
	}

	@Override
	public ArmorMaterial addArmorMaterial(String name, String textureName, int durability, int[] reductionAmounts,
			int enchantability, CompatibleSound soundOnEquip, float toughness) {
		return EnumHelper.addArmorMaterial(name, durability, reductionAmounts, enchantability);
	}

	@Override
	public boolean inventoryHasFreeSlots(EntityPlayer player) {
		boolean result = false;
		for(int i = 0; i < player.inventory.mainInventory.length; i++) {
			if(player.inventory.mainInventory[i] == null) {
				result = true;
				break;
			}
		}
		return result;
	}
}
