package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.glasses.SunglassesModel;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GlassesItem extends Item implements IHasModel, ICAModel, IGearType {
	
	public GlassesItem(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		// TODO Auto-generated method stub
		if (stack.getItem() == ModItems.HEADSET) {
			return "conomy:textures/gear/sunglasses.png";
		}
		return super.getArmorTexture(stack, entity, slot, type);
	}
	
	@Override
	public ResourceLocation getTex() {
		return new ResourceLocation(Reference.MOD_ID + ":textures/gear/sunglasses.png");
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		
		
		if(itemStack != ItemStack.EMPTY) {
			if(itemStack.getItem() instanceof GlassesItem) {
				SunglassesModel model = new SunglassesModel();
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
	public ModelBiped getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clientInit() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public EnumGear getGearType() {
		// TODO Auto-generated method stub
		return EnumGear.GLASSES;
	}
	
}
