package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.medical.ConsciousProvider;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWater extends ItemFood implements IHasModel {

	public int amount = 0;
	
	public ItemWater(String name, int amount) {
		super(0, false);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		this.amount = amount;
		ModItems.ITEMS.add(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }
	
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		// TODO Auto-generated method stub
		return EnumAction.DRINK;
	}
	
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if(!worldIn.isRemote) {
			player.getCapability(ConsciousProvider.CONSCIOUS, null).increaseWaterLevel(this.amount);
		}
		if(this == ModItems.WATERBOTTLE) {
			player.addItemStackToInventory(new ItemStack(ModItems.EMPTYWATERBOTTLE));
		} else if(this == ModItems.BEERBOTTLE) {
			player.addItemStackToInventory(new ItemStack(ModItems.EMPTYBEERBOTTLE));
		} else if(this == ModItems.SODABOTTLE) {
			player.addItemStackToInventory(new ItemStack(ModItems.EMPTYSODABOTTLE));
		} 
		
		super.onFoodEaten(stack, worldIn, player);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	

}
