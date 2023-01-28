package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.bodyarmor.BodyArmorModelThree;
import com.jimholden.conomy.items.models.jackets.JeepSpirit;
import com.jimholden.conomy.items.models.jackets.LeonsJacket;
import com.jimholden.conomy.items.models.pants.Gorka4Pants;
import com.jimholden.conomy.items.models.pants.StandardPantsModel;
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

public class ItemPants extends ItemArmor implements IHasModel, ICAModel {
	
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

	
	public ItemPants(String name)
	{
		super(Main.shirtAndPantsMat, 2, EntityEquipmentSlot.LEGS);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		
		ModItems.ITEMS.add(this);
		
		
	}
	

	
	public ItemPants(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		// TODO Auto-generated constructor stub
	}








	public ResourceLocation khakiJeans;
	public ResourceLocation jeans;
	public ResourceLocation blackJeans;
	public ResourceLocation gymPants;
	public ResourceLocation gorka4Pants;
	
	public StandardPantsModel model;
	public Gorka4Pants gorka4Model;
	
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		khakiJeans = new ResourceLocation(Reference.MOD_ID + ":textures/gear/khakijeans.png");
		jeans = new ResourceLocation(Reference.MOD_ID + ":textures/gear/jeans.png");
		blackJeans = new ResourceLocation(Reference.MOD_ID + ":textures/gear/blackjeans.png");
		gymPants = new ResourceLocation(Reference.MOD_ID + ":textures/gear/gympants.png");
		gorka4Pants = new ResourceLocation(Reference.MOD_ID + ":textures/gear/gorka4.png");
		
		model = new StandardPantsModel();
		gorka4Model = new Gorka4Pants();
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		if(khakiJeans == null) {
			clientInit();
		}
		
		if(this == ModItems.KHAKIJEANS) {
			return khakiJeans.toString();
		} else if(this == ModItems.JEANS) {
			return jeans.toString();
		} else if(this == ModItems.BLACKJEANS) {
			return blackJeans.toString();
		} else if(this == ModItems.GYMPANTS) {
			return gymPants.toString();
		} else if(this == ModItems.GORKA4PANTS) {
			return gorka4Pants.toString();
		}
		
		else {
			return null;
		}
	}
	
	@Override
	public ResourceLocation getTex() {
		if(khakiJeans == null) {
			clientInit();
		}
		
		if(this == ModItems.KHAKIJEANS) {
			return khakiJeans;
		} else if(this == ModItems.JEANS) {
			return jeans;
		} else if(this == ModItems.BLACKJEANS) {
			return blackJeans;
		} else if(this == ModItems.GYMPANTS) {
			return gymPants;
		}else if(this == ModItems.GORKA4PANTS) {
			return gorka4Pants;
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
		
		if(this == ModItems.GORKA4PANTS) {
			return gorka4Model;
		} else {
			return model;
		}
		
		
	}

   


	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
}