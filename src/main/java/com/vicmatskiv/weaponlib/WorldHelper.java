package com.vicmatskiv.weaponlib;

import java.util.List;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHelper {

	public static Block getBlockAtPosition(World world, CompatibleRayTraceResult position) {
		Block block = world.getBlockState(new BlockPos(position.getBlockPosX(), position.getBlockPosY(),
				position.getBlockPosZ())).getBlock();
		return block;
	}

	public static void destroyBlock(World world, CompatibleRayTraceResult position) {
		world.destroyBlock(new BlockPos(position.getBlockPosX(), position.getBlockPosY(),
				position.getBlockPosZ()), true);
	}

	public static boolean isGlassBlock(Block block) {
		return block == Blocks.GLASS || block == Blocks.GLASS_PANE || block == Blocks.STAINED_GLASS || block == Blocks.STAINED_GLASS_PANE;
	}

	public static boolean consumeInventoryItem(InventoryPlayer inventoryPlayer, Item itemIn)
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
	

//	static ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player)
//	{
//		ItemStack result = null;
//		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
//			ItemStack stack = player.inventory.getStackInSlot(i);
//			if(stack != null && stack.getItem() == item && condition.test(stack)) {
//				if (--stack.stackSize <= 0) {
//					player.inventory.setInventorySlotContents(i, null);
//	            }
//				result = stack;
//				break;
//			}
//		}
//	}
	
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

	private static ItemStack consumeInventoryItem(Item item, Predicate<ItemStack> condition, EntityPlayer player, int maxSize) {
		
		if(maxSize <= 0) {
			return null;
		}
		
	    int i = itemSlotIndex(item, condition, player);
	
		if (i < 0) {
			return null;
		} else {
			ItemStack stackInSlot = player.inventory.mainInventory[i];
			int consumedStackSize = maxSize >= stackInSlot.stackSize ? stackInSlot.stackSize : maxSize;
			ItemStack result = stackInSlot.splitStack(consumedStackSize);
			if (stackInSlot.stackSize <= 0) {
				player.inventory.mainInventory[i] = null;
			}
			return result;
		}
	}

	static ItemStack tryConsumingCompatibleItem(List<? extends Item> compatibleParts, int maxSize, EntityPlayer player) {
		return tryConsumingCompatibleItem(compatibleParts, maxSize, player, i -> true);
	}
	
	@SafeVarargs
	static ItemStack tryConsumingCompatibleItem(List<? extends Item> compatibleParts, int maxSize, 
			EntityPlayer player, Predicate<ItemStack> ...conditions) {
		ItemStack resultStack = null;
		for(Predicate<ItemStack> condition: conditions) {
			for(Item item: compatibleParts) {
				if((resultStack = consumeInventoryItem(item, condition, player, maxSize)) != null) {
					break;
				}
			}
			if(resultStack != null) break;
		}
		
		return resultStack;
	}
}
