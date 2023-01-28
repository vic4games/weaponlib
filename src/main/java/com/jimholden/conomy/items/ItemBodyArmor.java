package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.HXSuit.HXBodySuit;
import com.jimholden.conomy.items.models.HXSuit.HXVest;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.bodyarmor.BodyArmorModelThree;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.items.models.headset.USMCHeadsetModel;
import com.jimholden.conomy.items.models.rigs.AtlasT7;
import com.jimholden.conomy.items.models.rigs.MOLLEPlateCarrier;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public class ItemBodyArmor extends Item implements IHasModel, IGearType, ICAModel {
	
	/*-+
	
	public BackpackItem(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		
		ModItems.ITEMS.add(this);
		
		// TODO Auto-generated constructor stub
	}
*/

	
	public ItemBodyArmor(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		
		ModItems.ITEMS.add(this);
		
		
	}
	
	
	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			int size = 40;
			ItemStackHandler handler = new ItemStackHandler(size);
			for(int x = 0; x < size; x++) {
				handler.setStackInSlot(x, new ItemStack(Items.STONE_AXE));
			}
			compound = new NBTTagCompound();
			compound.setInteger("size", handler.getSlots());
			compound.setTag("inventory", handler.serializeNBT());
			stack.setTagCompound(compound);
		}
	}
	
	@Override
	public ResourceLocation getTex() {
		if(level3BodyArmorTex == null) {
			clientInit();
		}
		
		if(this == ModItems.BODYARMORIII) {
			return level3BodyArmorTex;
		} 
		else if(this == ModItems.HXBODY) {
			return hxBodyTex;
		}
		else if(this == ModItems.HXVEST) {
			return mollePlateTex;
		}
		else if(this == ModItems.MOLLEPLATECARRIER) {
			return mollePlateTex;
		} else if(this == ModItems.ATLAST7) {
			return atlasT7Tex;
		}
		return null;
	}
	
	public ResourceLocation level3BodyArmorTex;
	public ResourceLocation hxBodyTex;
	public ResourceLocation hxVestTex;
	public ResourceLocation atlasT7Tex;
	public ResourceLocation mollePlateTex;
	
	public BodyArmorModelThree BODYARMOR_MODEL = null;
	public HXBodySuit HXBODYSUIT = null;
	public HXVest HXVEST = null;
	public MOLLEPlateCarrier mollePlateCarrier = null;
	public AtlasT7 atlasT7Model = null;
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		BODYARMOR_MODEL = new BodyArmorModelThree();
		HXBODYSUIT = new HXBodySuit();
		HXVEST = new HXVest();
		atlasT7Model = new AtlasT7();
		mollePlateCarrier = new MOLLEPlateCarrier();
		
		level3BodyArmorTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/bodyarmorlevel3.png");
		hxBodyTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/hxsuit.png");;
		hxVestTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/hxsuit.png");
		atlasT7Tex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/atlast7.png");
		mollePlateTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/molleplatecarrier.png");
	}
	
	
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel() {
	
		if(BODYARMOR_MODEL == null) {
			clientInit();
		}
		
		if(this == ModItems.BODYARMORIII) {
			return BODYARMOR_MODEL;
		}
		if(this == ModItems.HXBODY) {
			//HXBodySuit model = new HXBodySuit();
			return HXBODYSUIT;
		}
		if(this == ModItems.HXVEST) {
			return HXVEST;
		} else if(this == ModItems.MOLLEPLATECARRIER) {
			return mollePlateCarrier;
		} else if(this == ModItems.ATLAST7) {
			return atlasT7Model;
		}
		
		
	
		return null;
		
	}

	
	/*

	public ResourceLocation getTex() {
		return new ResourceLocation(Reference.MOD_ID + ":textures/gear/bodyarmorlevel3.png");
	}
	
	
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		
		
		
		
		if(itemStack.getItem() == ModItems)
		
		
		if(itemStack != ItemStack.EMPTY) {
			if(itemStack.getItem() instanceof ItemBodyArmor) {
				BodyArmorModelThree model = new BodyArmorModelThree();
				return model;
				//float scaler = 0.1F;
				//model.render(entityLiving, scaler, scaler, scaler,scaler,scaler,scaler);
				//model.head.showModel = armorSlot == EntityEquipmentSlot.HEAD;
			
			}
		}
		return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
		
	}
	

   */


	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}


	@Override
	public EnumGear getGearType() {
		// TODO Auto-generated method stub
		return EnumGear.BODYARMOR;
	}


	
}