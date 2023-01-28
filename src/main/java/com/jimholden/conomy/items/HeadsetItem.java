package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.headset.CatEars;
import com.jimholden.conomy.items.models.headset.Comtacs;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.items.models.headset.Plantronics800HD;
import com.jimholden.conomy.items.models.headset.USMCHeadsetModel;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HeadsetItem extends ItemArmor implements IHasModel, ICAModel, IGearType {
	public static final ResourceLocation headset = new ResourceLocation(Reference.MOD_ID + ":textures/gear/headset.png");
	
	
	public HeadsetItem(String name, ArmorMaterial mat, EntityEquipmentSlot slot) {
		super(mat, 1, slot);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		// TODO Auto-generated method stub
		if (stack.getItem() == ModItems.HEADSET) {
			return "conomy:textures/items/headset.png";
		}
		return super.getArmorTexture(stack, entity, slot, type);
	}
	
	public ResourceLocation headsetTex = null;
	public ResourceLocation usmcHeadsetTex = null;
	public ResourceLocation comtacsTex = null;
	public ResourceLocation plantronicsTex = null;
	public ResourceLocation catEarsTex;
	
	public HeadsetModel headsetModel  = null;
	public USMCHeadsetModel usmcModel = null; 
	public Comtacs comtacsModel  = null;
	public Plantronics800HD plantronicsModel = null; 
	public CatEars catEarsModel;
	
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		headsetTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/headset.png");
		usmcHeadsetTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/usmcheadset.png");
		comtacsTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/comtacs.png");
		plantronicsTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/plantronics800hd.png");
		catEarsTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/catears.png");
		
		headsetModel = new HeadsetModel();
		usmcModel = new USMCHeadsetModel();
		comtacsModel = new Comtacs();
		plantronicsModel = new Plantronics800HD();
		catEarsModel = new CatEars();
		
	}
	

	
	@Override
	public ResourceLocation getTex() {
		if(headsetTex == null) {
			clientInit();
		}
		
		if(this == ModItems.HEADSET) {
			return headsetTex;
		}
		if(this == ModItems.USMCHEADSET) {
			return usmcHeadsetTex;
		}
		if(this == ModItems.COMTACS) {
			return comtacsTex;
		}
		if(this == ModItems.PLANTRONICS) {
			return plantronicsTex;
		}
		if(this == ModItems.CATEARS) {
			return catEarsTex;
		}
		return null;
	}
	
	
	
	
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel() {
		
		if(headsetModel == null) {
			clientInit();
		}
	
		if(this == ModItems.HEADSET) {
			return headsetModel;
		}
		if(this == ModItems.USMCHEADSET) {
			return usmcModel;
		}
		if(this == ModItems.COMTACS) {
			return comtacsModel;
		}
		if(this == ModItems.PLANTRONICS) {
			return plantronicsModel;
		}
		if(this == ModItems.CATEARS) {
			return catEarsModel;
		}
	
		return null;
		
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		
		
		ItemStack stack = itemStack;
		if(stack.getItem() == ModItems.HEADSET) {
			HeadsetModel model = new HeadsetModel();
			return model;
		}
		if(stack.getItem() == ModItems.USMCHEADSET) {
			USMCHeadsetModel model = new USMCHeadsetModel();
			return model;
		}
		
		
		
		if(itemStack != ItemStack.EMPTY) {
			if(itemStack.getItem() instanceof HeadsetItem) {
				HeadsetModel model = new HeadsetModel();
				return model;
				//float scaler = 0.1F;
				//model.render(entityLiving, scaler, scaler, scaler,scaler,scaler,scaler);
				//model.head.showModel = armorSlot == EntityEquipmentSlot.HEAD;
			
			}
		}
		return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
		
	}



	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	@Override
	public EnumGear getGearType() {
		// TODO Auto-generated method stub
		return EnumGear.HEADSET;
	}
	
}
