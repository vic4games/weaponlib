package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.bodyarmor.BodyArmorModelThree;
import com.jimholden.conomy.items.models.jackets.AlphaFLJacket;
import com.jimholden.conomy.items.models.jackets.BlackHalwoodTuxedo;
import com.jimholden.conomy.items.models.jackets.Gorka4Jacket;
import com.jimholden.conomy.items.models.jackets.JeepSpirit;
import com.jimholden.conomy.items.models.jackets.LeonsJacket;
import com.jimholden.conomy.items.models.rigs.TritonRig;
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

public class ItemJacket extends Item implements IHasModel, ICAModel, IGearType {
	
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

	
	public ItemJacket(String name)
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
	
	public ResourceLocation leonsJacket;
	public ResourceLocation jeepSpiritJacket;
	public ResourceLocation alphaFLJacket;
	public ResourceLocation blackHalwoodJacket;
	public ResourceLocation gorka4;
	
	public LeonsJacket leonsJacketModel;
	public JeepSpirit jeepJacketModel;
	public AlphaFLJacket alphaFLModel;
	public BlackHalwoodTuxedo blackHalwoodJacketModel;
	public Gorka4Jacket gorkaModel;
	
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		leonsJacket = new ResourceLocation(Reference.MOD_ID + ":textures/gear/leonsjacket.png");
		jeepSpiritJacket = new ResourceLocation(Reference.MOD_ID + ":textures/gear/jeepspirit.png");
		alphaFLJacket = new ResourceLocation(Reference.MOD_ID + ":textures/gear/alphafljacket.png");
		blackHalwoodJacket = new ResourceLocation(Reference.MOD_ID + ":textures/gear/blackhalwoodtuxedo.png");
		gorka4 = new ResourceLocation(Reference.MOD_ID + ":textures/gear/gorka4.png");
		
		leonsJacketModel = new LeonsJacket();
		jeepJacketModel = new JeepSpirit();
		alphaFLModel = new AlphaFLJacket();
		blackHalwoodJacketModel = new BlackHalwoodTuxedo();
		gorkaModel = new Gorka4Jacket();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTex() {
		
		if(leonsJacket == null) {
			clientInit();
		}
		
		if(this == ModItems.LEONSJACKET) {
			return leonsJacket;
		} else if(this == ModItems.JEEPSPIRITJACKET) {
			return jeepSpiritJacket;
		} else if(this == ModItems.ALPHAFLJACKET) {
			return alphaFLJacket;
		} else if(this == ModItems.BLACKHALWOODTUXEDO) {
			return blackHalwoodJacket;
		} else if(this == ModItems.GORKA4JACKET) {
			return gorka4;
		}
		else {
			return null;
		}
		
		
	}
	
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel() {
		
		if(leonsJacketModel == null) {
			clientInit();
		}
		
		if(this == ModItems.LEONSJACKET) {
			return leonsJacketModel;
		} else if(this == ModItems.JEEPSPIRITJACKET) {
			return jeepJacketModel;
		} else if(this == ModItems.ALPHAFLJACKET) {
			return alphaFLModel;
		} else if(this == ModItems.BLACKHALWOODTUXEDO) {
			return blackHalwoodJacketModel;
		} else if(this == ModItems.GORKA4JACKET) {
			return gorkaModel;
		}
		
		else {
			return null;
		}
		
	}
	
	


   


	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	@Override
	public EnumGear getGearType() {
		// TODO Auto-generated method stub
		return EnumGear.JACKET;
	}
}