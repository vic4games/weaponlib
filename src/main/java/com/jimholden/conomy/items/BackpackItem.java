package com.jimholden.conomy.items;

import java.util.ArrayList;
import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.DuffleBag;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.backpacks.OakleyMechanismBackpack;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackItem extends Item implements IHasModel, ISaveableItem, ICAModel, IGearType {
	
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

	
	private int slots;
	private double baseWeight;
	
	public BackpackItem(String name, int slots, double baseWeight)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		this.slots = slots;
		this.baseWeight = baseWeight;
		ModItems.ITEMS.add(this);
		
		
	}
	
	
	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			int size = 40;
			ItemStackHandler handler = new ItemStackHandler(size);
			//ItemStackHandler handler = new ItemStackHandler(this.slots);
			compound = new NBTTagCompound();
			compound.setInteger("size", this.slots);
			compound.setTag("inventory", handler.serializeNBT());
			compound.setDouble("weight", this.baseWeight);
			stack.setTagCompound(compound);
		}
	}
	
	
	public void saveInv(ItemStack stack, ItemStackHandler handler) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setInteger("size", this.slots);
		compound.setTag("inventory", handler.serializeNBT());
		recalculateWeight(stack);
	}
	
	public ItemStackHandler getInv(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		ItemStackHandler handler = new ItemStackHandler(compound.getInteger("size"));
		handler.deserializeNBT(compound.getCompoundTag("inventory"));
		return handler;
	}
	
	public int getSize(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getInteger("size");
		
	}
	

	public ResourceLocation f5SwitchbladeTex;
	public ResourceLocation oakleyMechanismTex;
	public ResourceLocation duffleBagTex;
	
	public F5SwitchbladeBackpack f5SwitchbladeModel;
	public OakleyMechanismBackpack oakleyMechanismModel;
	public DuffleBag duffleBagModel;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		f5SwitchbladeTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/f5backpack.png");
		oakleyMechanismTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/oakleymechanism.png");
		duffleBagTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/dufflebag.png");
		
		f5SwitchbladeModel = new F5SwitchbladeBackpack();
		oakleyMechanismModel = new OakleyMechanismBackpack();
		duffleBagModel = new DuffleBag();
		
	}
	
	
	public ResourceLocation getTex() {
		
		if(f5SwitchbladeTex == null) {
			clientInit();
		}
		//duffleBagTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/dufflebag.png");
		
		if(this == ModItems.F5SWITCHBLADE) {
			return f5SwitchbladeTex;
		} else if(this == ModItems.OAKLEYMECHANISM) {
			return oakleyMechanismTex;
		} else if(this == ModItems.DUFFLEBAG) {
			return duffleBagTex;
		}
		
		return new ResourceLocation(Reference.MOD_ID + ":textures/gear/f5backpack.png");
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel() {
		if(f5SwitchbladeModel == null) {
			clientInit();
		}
		
		if(this == ModItems.F5SWITCHBLADE) {
			return f5SwitchbladeModel;
		} else if(this == ModItems.OAKLEYMECHANISM) {
			return oakleyMechanismModel;
		} else if(this == ModItems.DUFFLEBAG) {
			return duffleBagModel;
		}
		// TODO Auto-generated method stub
		return null;
	}

   

	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ensureNBTUpdated(stack);
		tooltip.add("Weight: " + stack.getTagCompound().getDouble("weight"));
		
		//ensureNBTUpdated(stack);
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	 
	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}


	@Override
	public void recalculateWeight(ItemStack stack) {
		ensureNBTUpdated(stack);
		ItemStackHandler handler = getInv(stack);
		NBTTagCompound compound = stack.getTagCompound();
		double totalWeight = 0;
		for(int x = 0; x < handler.getSlots(); ++x) {
			ItemStack itemStack = handler.getStackInSlot(x);
			if(!itemStack.isEmpty() && itemStack.hasTagCompound()) {
				totalWeight += itemStack.getTagCompound().getDouble("weight")*itemStack.getCount();
			}
		}
		compound.setDouble("weight", this.baseWeight + totalWeight);
		stack.setTagCompound(compound);
		
	}


	@Override
	public EnumGear getGearType() {
		// TODO Auto-generated method stub
		return EnumGear.BACKPACK;
	}




}
