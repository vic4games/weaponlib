package com.jimholden.conomy.items;

import java.awt.Color;
import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.drugs.DrugExtractRecipe;
import com.jimholden.conomy.drugs.DrugNaming;
import com.jimholden.conomy.drugs.DrugProperty;
import com.jimholden.conomy.drugs.IChemical;
import com.jimholden.conomy.drugs.components.DrugComponentPreset;
import com.jimholden.conomy.drugs.components.DrugComponentTool;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.NumberUtil;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemBaseComponent extends Item implements IHasModel {	
	

	private double weight;
	private DrugProperty output;
	private double toxicity;
	
	
	public ItemBaseComponent(String name, double weight, DrugProperty output, float toxicity)
	{
		setUnlocalizedName(name);
		
		setRegistryName(name);
		setCreativeTab(Main.DRUGTAB);
		this.weight = weight;
		this.output = output;
		this.toxicity = toxicity;
		
		
		ModItems.ITEMS.add(this);
	}
	
	public DrugProperty getRecipe() {
		return this.output;
	}
	
	public ItemStack getOutputStack() {
		ItemStack stack = DrugComponentTool.getFromPreset(this.output);
		((ItemDrugPowder) stack.getItem()).setWeight(stack, NumberUtil.roundToDecimal(0.333F*weight, 2));
		return stack;
	}
	
	


	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setDouble("weight", this.weight);
			stack.setTagCompound(compound);
		}
	}
	
	/*
	 * SETTERS
	 * These will set a NBT value
	 * 
	 */
	

	public void setWeight(ItemStack stack, double weight) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setDouble("weight", weight);
	}
	

	
	/*
	 * GETTERs
	 * These will get a NBT value
	 * 
	 */
	
	
	public float getToxicity() {
		return (float) this.toxicity;
	}
	
	public double getWeight(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getInteger("weight");
	}
	
	
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		tooltip.add(TextFormatting.RED + "Base Component");
		tooltip.add("Weight: " + TextFormatting.GREEN + compound.getDouble("weight") + "g");
		//tooltip.add("Toxicity: " + TextFormatting.RED + "%" + compound.getDouble("toxicity") + "g");
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}

}
