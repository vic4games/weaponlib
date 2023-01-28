package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.rigs.AtlasT7;
import com.jimholden.conomy.items.models.rigs.MOLLEPlateCarrier;
import com.jimholden.conomy.items.models.rigs.TritonRig;
import com.jimholden.conomy.main.ModEventClientHandler;
import com.jimholden.conomy.teisr.HeadItemTEISR;
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

public class RigItem extends Item implements IHasModel, ISaveableItem, ICAModel, IGearType {
	
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

	
	private int size;
	private double baseWeight;
	
	public RigItem(String name, int slots, double baseWeight)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.CLOTHINGITEMTAB);
		this.size = slots;
		this.baseWeight = baseWeight;
		
		
		ModItems.ITEMS.add(this);
		
		
		
	}
	
	
	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			int size = 40;
			ItemStackHandler handler = new ItemStackHandler(size);
			compound = new NBTTagCompound();
			compound.setInteger("size", this.size);
			compound.setTag("inventory", handler.serializeNBT());
			compound.setDouble("weight", this.baseWeight);
			stack.setTagCompound(compound);
		}
	}
	
	
	public void saveInv(ItemStack stack, ItemStackHandler handler) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setInteger("size", this.size);
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
	
	public ResourceLocation tritonRigTex;

	
	
	public TritonRig tritonRigModel;

	
	
	

	public ResourceLocation getTex() {

		if(tritonRigTex == null) {
			clientInit();
		}
		
		if(this == ModItems.TRITONRIG) {
			return tritonRigTex;
		} 
		// TODO Auto-generated method stub
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public void clientInit() {
		tritonRigTex = new ResourceLocation(Reference.MOD_ID + ":textures/gear/tritonm43.png");
		
		
		
		tritonRigModel = new TritonRig();
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel() {
		
		if(tritonRigModel == null) {
			clientInit();
		}
		
		if(this == ModItems.TRITONRIG) {
			return tritonRigModel;
		} 
		// TODO Auto-generated method stub
		return null;
	}
	
	

	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.getTagCompound() == null) {
			ensureNBTUpdated(stack);
		}
		
		//ensureNBTUpdated(stack);
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	 
	
	@Override
	public void registerModels() {
		//
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
				totalWeight += itemStack.getTagCompound().getDouble("weight");
			}
		}
		compound.setDouble("weight", this.baseWeight + totalWeight);
		stack.setTagCompound(compound);
		
	}
	
	@Override
	public EnumGear getGearType() {
		// TODO Auto-generated method stub
		return EnumGear.RIG;
	}

}