package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.bodyarmor.BodyArmorModelThree;
import com.jimholden.conomy.items.models.jackets.JeepSpirit;
import com.jimholden.conomy.items.models.jackets.LeonsJacket;
import com.jimholden.conomy.items.models.pants.StandardPantsModel;
import com.jimholden.conomy.items.models.rigs.TritonRig;
import com.jimholden.conomy.items.models.shirts.ShirtModel;
import com.jimholden.conomy.items.models.shoes.BlackShoes;
import com.jimholden.conomy.items.models.shoes.Gorka4Boots;
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

public class ItemShoes extends ItemArmor implements IHasModel, ICAModel {
	
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

	
	public ItemShoes(String name)
	{
		super(Main.shirtAndPantsMat, 1, EntityEquipmentSlot.FEET);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		
		ModItems.ITEMS.add(this);
		
		
	}
	

	
	public ItemShoes(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		// TODO Auto-generated constructor stub
	}








	public ResourceLocation blackShoes;
	public ResourceLocation gorka4;
	
	public BlackShoes model;
	public Gorka4Boots gorkaBoots;
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		blackShoes = new ResourceLocation(Reference.MOD_ID + ":textures/gear/blackshoes.png");
		gorka4 = new ResourceLocation(Reference.MOD_ID + ":textures/gear/gorka4.png");
		
		model = new BlackShoes();
		gorkaBoots = new Gorka4Boots();
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		if(blackShoes == null) {
			clientInit();
		}
		
		if(this == ModItems.BLACKSHOES) {
			return blackShoes.toString();
		} else if(this == ModItems.GORKA4BOOTS) { 
			return gorka4.toString();
		}
		else {
			return null;
		}
	}
	
	@Override
	public ResourceLocation getTex() {
		if(blackShoes == null) {
			clientInit();
		}
		
		if(this == ModItems.BLACKSHOES) {
			return blackShoes;
		} else if(this == ModItems.GORKA4BOOTS) {
			return gorka4;
		}
		else {
			return null;
		}
	}
	
	@Override
	public ModelBiped getModel() {
		// TODO Auto-generated method stub
		//return new StandardPantsModel();
		return getArmorModel(null, null, null, null);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		if(model == null) {
			clientInit();
		}
		
		if(this == ModItems.BLACKSHOES) {
			return model;
		} else if(this == ModItems.GORKA4BOOTS) {
			return gorkaBoots;
		}
		else {
			return null;
		}
		

	}

   


	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
}