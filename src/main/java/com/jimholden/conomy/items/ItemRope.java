package com.jimholden.conomy.items;

import java.util.List;

import com.jimholden.conomy.drugs.DrugNaming;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRope extends ItemBase {
	
	public static final ResourceLocation ROPE_REG = new ResourceLocation(Reference.MOD_ID + ":textures/entity/ropentile.png");
	public static final ResourceLocation ROPE_BLUE = new ResourceLocation(Reference.MOD_ID + ":textures/entity/blueropentile.png");
	public static final ResourceLocation ROPE_STEEL = new ResourceLocation(Reference.MOD_ID + ":textures/entity/steelropentile.png");
	public static final ResourceLocation ROPE_BUNGEE = new ResourceLocation(Reference.MOD_ID + ":textures/entity/bungeeropentile.png");
	public static final ResourceLocation ROPE_RED = new ResourceLocation(Reference.MOD_ID + ":textures/entity/redropentile.png");
	public static final ResourceLocation ROPE_SPECTRA = new ResourceLocation(Reference.MOD_ID + ":textures/entity/spectraropentile.png");
	
	
	
	public int ropeType;
	private float elasticity;

	public ItemRope(String name, int ropeType, float elasticity) {
		super(name);
		this.ropeType = ropeType;
		this.elasticity = elasticity;
	}
	
	
	public int getRopeTypeID(ItemStack stack) {
		ensureNBTUpdated(stack);
		return stack.getTagCompound().getInteger("ropeType");
	}
	
	public float getRopeMaxLength(ItemStack stack) {
		ensureNBTUpdated(stack);
		return stack.getTagCompound().getFloat("length");
	}
	
	public void setRopeMaxLength(ItemStack stack, float newLength) {
		ensureNBTUpdated(stack);
		stack.getTagCompound().setFloat("length", newLength);
	}
	
	public float getElasticity() {
		return this.elasticity;
	}
	
	@SideOnly(Side.CLIENT)
	public static ResourceLocation getResourceLocationFromType(int ropeTypeID) {
		switch(ropeTypeID) {
			case 1:
				return ROPE_REG;
			case 2:
				return ROPE_BLUE;
			case 3:
				return ROPE_STEEL;
			case 4:
				return ROPE_BUNGEE;
			case 5:
				return ROPE_RED;
			case 6:
				return ROPE_SPECTRA;
		}
		return null;
	}
	
	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setInteger("ropeType", this.ropeType);
			compound.setFloat("length", 15.0F);
			stack.setTagCompound(compound);
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		tooltip.add("Elasticity: " + TextFormatting.YELLOW + this.elasticity + "kN/m");
		tooltip.add("Length: " + TextFormatting.YELLOW + getRopeMaxLength(stack) + "m");
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	

}
