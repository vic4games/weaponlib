package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBottle extends ItemDrugPowder implements IHasModel {

	public ItemBottle(int amount, float saturation, boolean isWolfFood, String name, String drugName, int drugType,
			float potency, double weight, boolean synthetic) {
		super(amount, saturation, isWolfFood, name, drugName, drugType, potency, weight, synthetic);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		// TODO Auto-generated method stub
		return EnumAction.DRINK;
	}
	

}
