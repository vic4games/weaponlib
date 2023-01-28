package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class ItemChemicalBriefcase extends Item implements IHasModel {
	
	
	
	public ItemChemicalBriefcase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
		
		ModItems.ITEMS.add(this);
	}
	
	
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		int handNum;
		if(handIn == EnumHand.MAIN_HAND) {
			handNum = 0;
		} else {
			handNum = 1;
		}
		playerIn.openGui(Main.instance, Reference.GUI_BRIEFCASE, worldIn, handNum, 0, 0);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	public void saveInv(ItemStack stack, ItemStackHandler handler) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setTag("Inventory", handler.serializeNBT());
	}
	
	public void ensureNBTUpdated(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			compound = new NBTTagCompound();
			ItemStackHandler handle = new ItemStackHandler(36);
			compound.setTag("Inventory", handle.serializeNBT());
			stack.setTagCompound(compound);
		}
	}
	
	
	public ItemStackHandler getInv(ItemStack stack) {
		ensureNBTUpdated(stack);
		NBTTagCompound compound = stack.getTagCompound();
		ItemStackHandler handler = new ItemStackHandler(36);
		handler.deserializeNBT(compound.getCompoundTag("Inventory"));
		return handler;
	}
	
	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}

}
