package com.jimholden.conomy.blocks.building;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ModdedFenceGate extends BlockFenceGate implements IHasModel {

	public ModdedFenceGate(String name, EnumType p_i46394_1_) {
		super(p_i46394_1_);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
	}
	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}

}
