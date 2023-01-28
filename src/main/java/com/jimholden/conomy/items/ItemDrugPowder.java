package com.jimholden.conomy.items;

import java.awt.Color;
import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.drugs.Drug;
import com.jimholden.conomy.drugs.DrugCache;
import com.jimholden.conomy.drugs.DrugNaming;
import com.jimholden.conomy.drugs.IChemical;
import com.jimholden.conomy.drugs.fxpreset.DrugEffect;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

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

public class ItemDrugPowder extends ItemFood implements IHasModel, IChemical {	
	
	
	public ItemDrugPowder(int amount, float saturation, boolean isWolfFood, String name, String drugName, int drugType, float potency, double weight, boolean synthetic)
	{
		super(amount, saturation, isWolfFood);
		setUnlocalizedName(name);
		
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		this.drugType = drugType;
		this.potency = potency;
		this.weight = weight;
		this.synthetic = synthetic;
		this.drugName = drugName;
		
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		
		if(worldIn.isRemote) {
			//DrugCache.addDrug(new Drug("lsd", 300, 3000, 300, 0.0001F, 0.0F, 0.03F, 0.03F, 0.03F, 0.0F, 0.0F, 0.0001F, 0.0F, 0.0F, 1.0F, 0.1F));
			
			int type = getDrugType(stack);
			
			switch(type) {
			case 5:
				DrugCache.addDrug(new Drug("alc", 600, 6000, 600, 0.0F, 0.0F, 0.01F, 0.0F, 0.0F, 0.0F, 0.001F, 0.05F, 0.1F, 0.1F, 0.5F, 0.1F));
			case 14:
				DrugCache.addDrug(new Drug("lsd", 300, 3000, 300, 0.0001F, 0.0F, 0.03F, 0.03F, 0.03F, 0.0F, 0.0F, 0.0001F, 0.0F, 0.0F, 1.0F, 0.1F));
			case 15:
				DrugCache.addDrug(new Drug("stim", 600, 6000, 600, 0.0F, 0.0F, 0.01F, 0.0F, 0.0F, 0.0F, 0.0001F, 0.05F, 0.0F, 0.0F, 0.0F, 0.0F));
			case 16:
				DrugCache.addDrug(new Drug("depressent", 600, 6000, 600, 0.0F, 0.03F, 0.01F, 0.0F, 0.0F, 0.0F, 0.01F, 0.01F, 0.0F, 0.0F, 0.0F, 0.0F));
			}
			
		}
		
		
		super.onFoodEaten(stack, worldIn, player);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		// TODO Auto-generated method stub
		return EnumAction.BOW;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }
	
	public boolean isBaseComponent() {
		return false;
	}
	
	/*
	public ItemDrugPowder(String name, String drugName, int drugType, float potency, double weight, boolean synthetic)
	{
		setUnlocalizedName(name);
		
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		this.drugType = drugType;
		this.potency = potency;
		this.weight = weight;
		this.synthetic = synthetic;
		this.drugName = drugName;
		
		
		ModItems.ITEMS.add(this);
	}
	*/
	
	


	private int drugType;
	private float potency;
	private boolean synthetic;
	private double weight;
	private String drugName;
	
	
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return getName(stack);
	}
	
	
	public int getColor(ItemStack stack) {
		int colorRGB = Color.HSBtoRGB(((float) getDrugType(stack))/5.0F, getPotency(stack)*2.0F, 0.4F);
		
		//System.out.println(colorRGB);
		//0x2F329F
		return colorRGB;
	}
	
	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setString("drugname", this.drugName);
			compound.setInteger("drugType", this.drugType);
			compound.setFloat("potency", this.potency);
			compound.setBoolean("synthetic", this.synthetic);
			compound.setDouble("weight", this.weight);
			compound.setFloat("toxicity", 0.0F);
			stack.setTagCompound(compound);
		}
	}
	
	public String getName(ItemStack stack) {
		ensureNBTUpdated(stack);
		float pot = getPotency(stack);
		return stack.getTagCompound().getString("drugname");
	}
	
	/*
	 * SETTERS
	 * These will set a NBT value
	 * 
	 */
	
	@Override
	public void setToxicity(ItemStack stack, float toxicity) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setFloat("toxicity", toxicity);
	}
	
	public void setDrugName(ItemStack stack, String name) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setString("drugname", name);
	}
	
	
	public void setPotency(ItemStack stack, float potency) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setFloat("potency", potency);
	}
	
	public void setSynthetic(ItemStack stack, boolean syn) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setBoolean("synthetic", syn);
	}
	
	public void setWeight(ItemStack stack, double weight) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setDouble("weight", weight);
	}
	
	public void setDrugType(ItemStack stack, int drugType) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setInteger("drugType", drugType);
	}
	

	
	/*
	 * GETTERs
	 * These will get a NBT value
	 * 
	 */
	
	@Override
	public float getToxicity(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getFloat("toxicity");
	}
	
	
	public String getDrugName(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getString("drugname");
	}
	
	public float getPotency(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getFloat("potency");
	}
	
	public int getDrugType(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getInteger("drugType");
	}
	
	public boolean getSynthetic(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getBoolean("synthetic");
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
		tooltip.add("Chemical Type: " + TextFormatting.YELLOW + DrugNaming.getDrugTypeByID(compound.getInteger("drugType")));
		tooltip.add("Potency: " + TextFormatting.YELLOW + "%" + compound.getFloat("potency")*100);
		tooltip.add("Weight: " + TextFormatting.GREEN + compound.getDouble("weight") + "g");
		tooltip.add("Toxicity: " + TextFormatting.GREEN + "%" + compound.getFloat("toxicity")*100);
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}



}
