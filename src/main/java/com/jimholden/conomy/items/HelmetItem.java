package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.HXSuit.HXHelmet;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
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
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HelmetItem extends ItemArmor implements IHasModel, ICAModel{
	
	public HelmetItem(String name, ArmorMaterial mat, EntityEquipmentSlot slot) {
		super(mat, 1, slot);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		// TODO Auto-generated method stub
		if (stack.getItem() == ModItems.HXHELMET) {
			return "conomy:textures/gear/hxsuitnotrans.png";
		}
		return super.getArmorTexture(stack, entity, slot, type);
	}
	
	@Override
	public ResourceLocation getTex() {
		if(this == ModItems.HXHELMET) {
			return new ResourceLocation(Reference.MOD_ID + ":textures/gear/hxsuit.png");
		}
		if(this == ModItems.HEADSET) {
			return new ResourceLocation(Reference.MOD_ID + ":textures/gear/headset.png");
		}
		if(this == ModItems.USMCHEADSET) {
			return new ResourceLocation(Reference.MOD_ID + ":textures/gear/usmcheadset.png");
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel() {
	
		if(this == ModItems.HEADSET) {
			HeadsetModel model = new HeadsetModel();
			return model;
		}
		if(this == ModItems.USMCHEADSET) {
			USMCHeadsetModel model = new USMCHeadsetModel();
			return model;
		}
		
	
		return null;
		
	}

	public HXHelmet HXHELMET_MODEL = null;
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		HXHELMET_MODEL = new HXHelmet();
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		
		if(HXHELMET_MODEL == null) {
			clientInit();
		}
		
		
		ItemStack stack = itemStack;
		if(stack.getItem() == ModItems.HXHELMET) {
			return HXHELMET_MODEL;
		}
		/*
		if(stack.getItem() == ModItems.HEADSET) {
			HeadsetModel model = new HeadsetModel();
			return model;
		}
		if(stack.getItem() == ModItems.USMCHEADSET) {
			USMCHeadsetModel model = new USMCHeadsetModel();
			return model;
		}
		*/
		
		/*
		if(itemStack != ItemStack.EMPTY) {
			if(itemStack.getItem() instanceof HeadsetItem) {
				HeadsetModel model = new HeadsetModel();
				return model;
				//float scaler = 0.1F;
				//model.render(entityLiving, scaler, scaler, scaler,scaler,scaler,scaler);
				//model.head.showModel = armorSlot == EntityEquipmentSlot.HEAD;
			
			}
		} */
		return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
		
	}



	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
}