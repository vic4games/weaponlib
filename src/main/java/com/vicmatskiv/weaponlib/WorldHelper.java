package com.vicmatskiv.weaponlib;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class WorldHelper {

	public static Block getBlockAtPosition(World world, RayTraceResult position) {
		Block block = world.getBlockState(position.getBlockPos()).getBlock();
		return block;
	}

	public static void destroyBlock(World world, RayTraceResult position) {
		world.destroyBlock(position.getBlockPos(), true);
	}

	public static boolean isGlassBlock(Block block) {
		return block == Blocks.GLASS || block == Blocks.GLASS_PANE || block == Blocks.STAINED_GLASS || block == Blocks.STAINED_GLASS_PANE;
	}

//	static boolean consumeInventoryItem(InventoryPlayer inventoryPlayer, Item itemIn)
//	{
//		boolean result = false;
//		for(int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
//			ItemStack stack = inventoryPlayer.getStackInSlot(i);
//			if(stack != null && stack.getItem() == itemIn) {
//				if (--stack.stackSize <= 0)
//	            {
//					inventoryPlayer.setInventorySlotContents(i, null);
//	            }
//				result = true;
//				break;
//			}
//		}
//		
//		return result;
//	}
	
	static ItemStack consumeInventoryItem(Item item, EntityPlayer player)
    {
		ItemStack stack = findItemStack(item, player);

		if(stack != null) {
			player.inventory.deleteStack(stack);
		}
		
		return stack;
    }
	
	private static ItemStack findItemStack(Item p_146029_1_, EntityPlayer player) {
		
		ItemStack result = null;
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if(itemstack.getItem() == p_146029_1_) {
            	result = itemstack;
            	break;
            }
        }

        return result;
    }
}
