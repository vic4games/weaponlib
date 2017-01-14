package com.vicmatskiv.weaponlib;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class WorldHelper {

	public static Block getBlockAtPosition(World world, MovingObjectPosition position) {
		Block block = world.getBlockState(position.getBlockPos()).getBlock();
		return block;
	}

	public static void destroyBlock(World world, MovingObjectPosition position) {
		world.destroyBlock(position.getBlockPos(), true);
	}

	public static boolean isGlassBlock(Block block) {
		return block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass || block == Blocks.stained_glass_pane;
	}

	static ItemStack itemStackForItem(Item item, Predicate<ItemStack> condition, EntityPlayer player) {
	    ItemStack result = null;
		for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
	        if (player.inventory.mainInventory[i] != null 
	        		&& player.inventory.mainInventory[i].getItem() == item
	        		&& condition.test(player.inventory.mainInventory[i])) {
	            result = player.inventory.mainInventory[i];
	            break;
	        }
	    }
	
	    return result;
	}
	
	private static int itemSlotIndex(Item item, Predicate<ItemStack> condition, EntityPlayer player) {
	    for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
	        if (player.inventory.mainInventory[i] != null 
	        		&& player.inventory.mainInventory[i].getItem() == item
	        		&& condition.test(player.inventory.mainInventory[i])) {
	            return i;
	        }
	    }
	
	    return -1;
	}

	private static ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player) {
		ItemStack result = null;
	    int i = itemSlotIndex(item, condition, player);
	
		if (i < 0) {
			return null;
		} else {
	
			result = player.inventory.mainInventory[i];
			if (--player.inventory.mainInventory[i].stackSize <= 0) {
				player.inventory.mainInventory[i] = null;
			}
	
			return result;
		}
	}

	static ItemStack tryConsumingCompatibleItem(List<? extends Item> compatibleParts, EntityPlayer player) {
		return tryConsumingCompatibleItem(compatibleParts, player, i -> true);
	}
	
	@SafeVarargs
	static ItemStack tryConsumingCompatibleItem(List<? extends Item> compatibleParts, EntityPlayer player, Predicate<ItemStack> ...conditions) {
		ItemStack resultStack = null;
		for(Predicate<ItemStack> condition: conditions) {
			for(Item item: compatibleParts) {
				if((resultStack = consumeInventoryItem(item, condition, player)) != null) {
					break;
				}
			}
			if(resultStack != null) break;
		}
		
		return resultStack;
	}
}
