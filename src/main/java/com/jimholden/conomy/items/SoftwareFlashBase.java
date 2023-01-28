package com.jimholden.conomy.items;

import java.util.List;

import org.apache.commons.logging.Log;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

import ibxm.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
public class SoftwareFlashBase extends Item implements IHasModel {
	
	private int compatIndex;
	private int power;
	
	
	public SoftwareFlashBase(String name, int compatIndex, int power)
	{
		this.compatIndex = compatIndex;
		this.power = power;
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		NBTTagCompound compound = stack.getTagCompound();
		compound = new NBTTagCompound();
		compound.setInteger("compatIndex", this.compatIndex);
		compound.setInteger("power", this.power);
		stack.setTagCompound(compound);
		
		super.onCreated(stack, worldIn, playerIn);
	}
	
	public int getCompatIndex(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setInteger("compatIndex", this.compatIndex);
			compound.setInteger("power", this.power);
			stack.setTagCompound(compound);
		}
		return compound.getInteger("compatIndex");
	}
	
	public int getPower(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setFloat("compatIndex", this.compatIndex);
			compound.setInteger("power", this.power);
			stack.setTagCompound(compound);
		}
		return compound.getInteger("power");
	}
	
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setInteger("compatIndex", this.compatIndex);
			compound.setInteger("power", this.power);
			stack.setTagCompound(compound);
			
		}
		tooltip.add("Compat Index: " + compound.getInteger("compatIndex"));
		tooltip.add("Power: " + compound.getInteger("power"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	  
	


	

	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	// help
}
