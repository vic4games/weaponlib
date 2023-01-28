package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.bodyarmor.BodyArmorModelThree;
import com.jimholden.conomy.items.models.jackets.JeepSpirit;
import com.jimholden.conomy.items.models.jackets.LeonsJacket;
import com.jimholden.conomy.items.models.rigs.TritonRig;
import com.jimholden.conomy.items.models.shirts.ShirtModel;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public class ItemShirt extends ItemArmor implements IHasModel, ICAModel {
	
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
	private ItemStack renderStack;
	
	public ItemShirt(String name)
	{
		
		super(Main.shirtAndPantsMat, 1, EntityEquipmentSlot.CHEST);
		setUnlocalizedName(name);
		setRegistryName(name);
		
		setCreativeTab(Main.CLOTHINGITEMTAB);
		this.renderStack = new ItemStack(this);
		ModItems.ITEMS.add(this);
		
		
	}
	

	
	public ItemShirt(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		// TODO Auto-generated constructor stub
	}








	public ResourceLocation blackShirt;
	public ResourceLocation formalShirt;
	public ResourceLocation multicamoBlack;
	public ResourceLocation shirtForest;
	public ResourceLocation navyBlueShirt;
	public ResourceLocation blackFormalShirt;
	
	
	public ShirtModel model;
	
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		blackShirt = new ResourceLocation(Reference.MOD_ID + ":textures/gear/blackshirt.png");
		formalShirt = new ResourceLocation(Reference.MOD_ID + ":textures/gear/formalwhiteshirt.png");
		multicamoBlack = new ResourceLocation(Reference.MOD_ID + ":textures/gear/militaryblackmulticamo.png");
		shirtForest = new ResourceLocation(Reference.MOD_ID + ":textures/gear/tacticalmilitaryshirtforest.png");
		navyBlueShirt = new ResourceLocation(Reference.MOD_ID + ":textures/gear/navyblueshirt.png");
		blackFormalShirt = new ResourceLocation(Reference.MOD_ID + ":textures/gear/blackformalshirt.png");
		
		model = new ShirtModel();
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		if(blackShirt == null) {
			clientInit();
		}
		
		if(this == ModItems.BLACKSHIRT) {
			return blackShirt.toString();
		} else if(this == ModItems.FORMALSHIRT) {
			return formalShirt.toString();
		} else if(this == ModItems.BLACKMULTICAMOSHIRT) {
			return multicamoBlack.toString();
		} else if(this == ModItems.FORESTMILITARYSHIRT) {
			return shirtForest.toString();
		} else if(this == ModItems.NAVYBLUESHIRT) {
			return navyBlueShirt.toString();
		} else if(this == ModItems.BLACKFORMALSHIRT) {
			return blackFormalShirt.toString();
		}
		
		else {
			return null;
		}
	}
	
	@Override
	public ModelBiped getModel() {
		// TODO Auto-generated method stub
		return getArmorModel(null, null, null, null);
	}
	
	@Override
	public ResourceLocation getTex() {
		if(blackShirt == null) {
			clientInit();
		}
		
		if(this == ModItems.BLACKSHIRT) {
			return blackShirt;
		} else if(this == ModItems.FORMALSHIRT) {
			return formalShirt;
		} else if(this == ModItems.BLACKMULTICAMOSHIRT) {
			return multicamoBlack;
		} else if(this == ModItems.FORESTMILITARYSHIRT) {
			return shirtForest;
		} else if(this == ModItems.NAVYBLUESHIRT) {
			return navyBlueShirt;
		} else if(this == ModItems.BLACKFORMALSHIRT) {
			return blackFormalShirt;
		}
		
		else {
			return null;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		if(model == null) {
			clientInit();
		}
		
		return model;
	}

   


	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
}