package com.jimholden.conomy.items;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import javax.annotation.Nullable;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.annotation.meta.param;

public class MemoryItem extends Item implements IHasModel {
	public static int dataLimit = 32000000;
	public double dataStored = 0;
	public float accuracy = 0.0F;
	public float precision = 0.0F;
	public float corruption = 0.0F;
	public int information;
	public float analysis = 0.0F;
	
	
	public MemoryItem(String name, int maxSize)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
		this.dataLimit = maxSize;
		ModItems.ITEMS.add(this);
	}
	
	public String analysisQuality(float analysis)
	{
		int percentage = (int) (analysis*100);
		if(percentage == 0)
		{
			return "Raw Data (" + (percentage) + "%)";
		}
		if(0 < percentage && 5 >= percentage)
		{
			return "Poor (" + (percentage) + "%)";
		}
		if(5 < percentage && 15 >= percentage)
		{
			return "Okay (" + (percentage) + "%)";
		}
		if(15 < percentage && 45 >= percentage)
		{
			return "Decent (" + (percentage) + "%)";
		}
		if(45 < percentage && 60 >= percentage)
		{
			return "Standard (" + (percentage) + "%)";
		}
		if(60 < percentage && 85 >= percentage)
		{
			return "Okay (" + (percentage) + "%)";
		}
		if(85 < percentage && 100 >= percentage)
		{
			return "Excellent (" + (percentage) + "%)";
		}
		else
		{
			return "Unsure.";
		}
		
	}
	
	public static String memToString(double d) {
	    if (-1000 < d && d < 1000) {
	        return d + "B";
	    }
	    CharacterIterator ci = new StringCharacterIterator("kMGTPE");
	    while (d <= -999_950 || d >= 999_950) {
	        d /= 1000;
	        ci.next();
	    }
	    return String.format("%.1f%cB", d / 1000.0, ci.current());
	}
	
	private int getNBTInt(String key, ItemStack stack)
	{
		this.nullCheck(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getInteger(key);	
	}
	
	private double getNBTDouble(String key, ItemStack stack)
	{
		this.nullCheck(stack);
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getDouble(key);	
	}
	
	public int getMemoryCap(ItemStack stack) {
		return getNBTInt("dataCap", stack);
	}
	
	public double getCurrentMemory(ItemStack stack) {
		return getNBTDouble("dataSize", stack);
	}
	
	public boolean isFull(ItemStack stack)
	{
		if(getCurrentMemory(stack) == getMemoryCap(stack))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	

	
	public void setDataTotal(double data, ItemStack stack)
	{
		NBTTagCompound compound = stack.getTagCompound();
		nullCheck(stack);
		compound.setDouble("dataSize", data);
		stack.setTagCompound(compound);
		
	}
	
	public void setAnalysis(float analysis, ItemStack stack)
	{
		NBTTagCompound compound = stack.getTagCompound();
		nullCheck(stack);
		compound.setFloat("analysis", analysis);
		stack.setTagCompound(compound);
		
	}
	
	public float getQuality(ItemStack stack)
	{
		NBTTagCompound compound = stack.getTagCompound();
		float accuracy = compound.getFloat("accuracy");
		float precision = compound.getFloat("precision");
		float corruption = compound.getFloat("corruption");
		float information = (float) compound.getDouble("dataSize")/100;
		float analysis = compound.getFloat("analysis");
		float quality = (((int) (Math.pow(information, 2) * (Math.sqrt(Math.pow(accuracy, 2) + Math.pow(precision, 2))))) - (corruption*information));
		return quality;
		
	}
	
	public boolean nullCheck(ItemStack stack)
	{
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setDouble("dataSize", this.dataStored);
			compound.setInteger("dataCap", this.dataLimit);
			compound.setFloat("accuracy", this.accuracy);
			compound.setFloat("precision", this.precision);
			compound.setFloat("corruption", this.corruption);
			compound.setFloat("analysis", this.analysis);
			compound.setString("class", "N/A");
			stack.setTagCompound(compound);
		}
		return true;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItemMainhand();
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	
	
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setDouble("dataSize", this.dataStored);
			compound.setInteger("dataCap", this.dataLimit);
			compound.setFloat("accuracy", this.accuracy);
			compound.setFloat("precision", this.precision);
			compound.setFloat("corruption", this.corruption);
			compound.setFloat("analysis", this.analysis);
			compound.setString("class", "N/A");
			stack.setTagCompound(compound);
		}
		tooltip.add(TextFormatting.YELLOW + "Memory: " + memToString(compound.getDouble("dataSize")) + "/" + memToString(compound.getInteger("dataCap")));
		//System.out.println(compound.getFloat("analysis"));
		tooltip.add(TextFormatting.GREEN + "Analysis: " + analysisQuality(compound.getFloat("analysis")));
		tooltip.add(TextFormatting.RED + "Class: " + compound.getString("class"));
		//System.out.println(compound.getString("class"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	  
	


	

	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	// help
}