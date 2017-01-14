package com.vicmatskiv.weaponlib;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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

	static boolean consumeInventoryItem(InventoryPlayer inventoryPlayer, Item itemIn)
	{
		boolean result = false;
		for(int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
			ItemStack stack = inventoryPlayer.getStackInSlot(i);
			if(stack != null && stack.getItem() == itemIn) {
				if (--stack.stackSize <= 0)
	            {
					inventoryPlayer.setInventorySlotContents(i, null);
	            }
				result = true;
				break;
			}
		}
		
		return result;
	}

	static ItemStack itemStackForItem(Item item, Predicate<ItemStack> condition, EntityPlayer player) {
	    ItemStack result = null;
		
		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == item
	        		&& condition.test(stack)) {
	            result = stack;
	            break;
	        }
		}
	
	    return result;
	}
	
	static ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player)
	{
		ItemStack result = null;
		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(stack != null && stack.getItem() == item && condition.test(stack)) {
				if (--stack.stackSize <= 0) {
					player.inventory.setInventorySlotContents(i, null);
	            }
				result = stack;
				break;
			}
		}
		
		return result;
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
