package com.jimholden.conomy.items;

import java.awt.Color;
import java.util.List;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.drugs.DrugNaming;
import com.jimholden.conomy.drugs.IChemical;
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

public class ItemPackingMaterial extends ItemBase implements IHasModel {
	
	private int matType;

	public ItemPackingMaterial(String name, int matType) {
		super(name);
		this.matType = matType;
		// TODO Auto-generated constructor stub
	}
	
	public int getMatType() {
		return this.matType;
	}
	
	
	
	
	
	

}
