package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.items.models.masks.FaceBandanaModel;
import com.jimholden.conomy.items.models.masks.GhostMask;
import com.jimholden.conomy.items.models.masks.RedScarf;
import com.jimholden.conomy.items.models.masks.TokyoGhoulMask;
import com.jimholden.conomy.items.models.shirts.ShirtModel;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.model.ModelBiped;
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

public class FacemaskItem extends Item implements IHasModel, ICAModel, IGearType {
	
	public FacemaskItem(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		
		ModItems.ITEMS.add(this);
	}
	
	public ResourceLocation ghostMaskTex;
	public ResourceLocation faceBandanaTex;
	public ResourceLocation redscarfTex;
	public ResourceLocation tokyoGhoulFacemaskTex;
	
	
	public GhostMask standardMaskModel;
	public FaceBandanaModel faceBandanaModel;
	public RedScarf redscarfModel;
	public TokyoGhoulMask tokyoGhoulMaskModel;
	
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		ghostMaskTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/ghostmask.png");
		faceBandanaTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/facebandana.png");
		redscarfTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/redscarf.png");
		tokyoGhoulFacemaskTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/tokyoghoulmask.png");
		
		standardMaskModel = new GhostMask();
		faceBandanaModel = new FaceBandanaModel();
		redscarfModel = new RedScarf();
		tokyoGhoulMaskModel = new TokyoGhoulMask();
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTex() {
		
		if(ghostMaskTex == null) {
			clientInit();
		}
		
		
		if(this == ModItems.GHOSTFACEMASK) {
			return ghostMaskTex;
		} else if(this == ModItems.FACEBANDANA) {
			return faceBandanaTex;
		} else if(this == ModItems.REDSCARF) {
			return redscarfTex;
		} else if(this == ModItems.TOKYOGHOULMASK) {
			return tokyoGhoulFacemaskTex;
		}
		
		
		else {
			return null;
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel() {
		if(standardMaskModel == null) {
			clientInit();
		}
		
		
		if(this == ModItems.GHOSTFACEMASK) {
			return standardMaskModel;
		} else if(this == ModItems.FACEBANDANA) {
			return faceBandanaModel;
		} else if(this == ModItems.REDSCARF) {
			return redscarfModel;
		} else if(this == ModItems.TOKYOGHOULMASK) {
			return tokyoGhoulMaskModel;
		}
		else {
		
			return null;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		
		
		if(itemStack != ItemStack.EMPTY) {
			if(itemStack.getItem() instanceof FacemaskItem) {
				GhostMask model = new GhostMask();
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
		return EnumGear.MASK;
	}
	
}
