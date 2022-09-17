package com.vicmatskiv.weaponlib.crafting.base;

import java.util.HashMap;
import java.util.LinkedList;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTileEntity;
import com.vicmatskiv.weaponlib.network.packets.StationPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityStation extends TileEntity implements ITickable, ISidedInventory {
	
	/*
	 * Contents: 9 for crafting output 4 for dismantling slots 10 dismantling
	 * inventory 27 for main inventory + ------------------------- 50 slots total
	 * 
	 */
	public ItemStackHandler mainInventory = new ItemStackHandler(50);

	public int[] dismantleStatus = new int[] { -1, -1, -1, -1 };
	public int[] dismantleDuration = new int[] { -1, -1, -1, -1 };
	
	public int prevCraftingTimer = -1;
	public int craftingTimer = -1;
	public int craftingDuration = -1;
	
	
	private int side;
	
	public TileEntityStation() {
		
	}
	
	public void setSide(int side) {
		this.side = side;
	}
	
	public int getSide() {
		return side;
	}
	
	
	public void setDismantling(int[] instant, int[] lengths) {
		this.dismantleStatus = instant;
		this.dismantleDuration = lengths;
	}


	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	
	public boolean inventoryContainsEnoughItems(Item item, int quantity, int start, int end) {
		int count = 0;
		for(int i = start; i <= end; ++i) {
			ItemStack slotStack = mainInventory.getStackInSlot(i);
			if(slotStack.getItem() == item) {
				count += slotStack.getCount();
				if(count >= quantity) return true;
			}
		}
		
		
		
		return count >= quantity;
	}
	
	public boolean consumeFromInventory(Item item, int quantity, int start, int end) {
		LinkedList<ItemStack> stackQueue = new LinkedList<>();
		
		int consumedSimulated = 0;
		for(int i = start; i <= end; ++i) {
			ItemStack slotStack = mainInventory.getStackInSlot(i);
			
			if(slotStack.getItem() == item) {
				stackQueue.add(slotStack);
				consumedSimulated += slotStack.getCount();
				if(consumedSimulated >= quantity) {
					break;
				}
			}
			
		}
		
		if(consumedSimulated >= quantity) {
			
			for(ItemStack s : stackQueue) {
				int toConsume = Math.min(quantity, s.getCount());
				s.shrink(toConsume);
				quantity -= toConsume;
				if(quantity == 0) {
					return true;
				}
			}
			
		} else {
			// Failed
			return false;
		}
		return tileEntityInvalid;
	}
	
	public void addStackToInventoryRange(ItemStack stack, int start, int end) {

		for (int i = start; i <= end; ++i) {
			if (ItemStack.areItemsEqual(mainInventory.getStackInSlot(i), stack)) {
				ItemStack inInventory = mainInventory.getStackInSlot(i);
				if (inInventory.getCount() + stack.getCount() <= inInventory.getMaxStackSize()) {
					inInventory.grow(stack.getCount());
					stack.shrink(stack.getCount());
				} else if (inInventory.getCount() >= inInventory.getMaxStackSize()) {
					continue;
				} else if (inInventory.getCount() + inInventory.getCount() >= inInventory.getMaxStackSize()) {
					int difference = inInventory.getMaxStackSize() - inInventory.getCount();
					inInventory.grow(difference);
					stack.shrink(difference);
					continue;
				}
			}
		}

		if (stack.getCount() > 0) {
			for (int i = start; i <= end; ++i) {
				if (mainInventory.getStackInSlot(i).isEmpty()) {
					mainInventory.setStackInSlot(i, stack);
					break;
				}
			}
		}
	}
	
	
	/**
	 * Happens on the client
	 * 
	 * @param buf
	 */
	public void readBytesFromClientSync(ByteBuf buf) {
		this.craftingTimer = buf.readInt();
		this.craftingDuration = buf.readInt();
		for(int i = 0; i < dismantleStatus.length; ++i) dismantleStatus[i] = buf.readInt();
		for(int i = 0; i < dismantleDuration.length; ++i) dismantleDuration[i] = buf.readInt();
		
	}

	/**
	 * Happens server-side
	 * 
	 * @param buf
	 */
	public void writeBytesForClientSync(ByteBuf buf) {
		buf.writeInt(this.craftingTimer);
		buf.writeInt(this.craftingDuration);
		for(int i = 0; i < dismantleStatus.length; ++i) buf.writeInt(dismantleStatus[i]);
		for(int i = 0; i < dismantleDuration.length; ++i) buf.writeInt(dismantleDuration[i]);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("mainInventory", mainInventory.serializeNBT());
		compound.setInteger("craftingTimer", craftingTimer);
		compound.setInteger("craftingDuration", craftingDuration);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("mainInventory"))
			this.mainInventory.deserializeNBT((NBTTagCompound) compound.getTag("mainInventory"));
		if(compound.hasKey("craftingTimer") && compound.hasKey("craftingDuration")) {
			this.craftingTimer = compound.getInteger("craftingTimer");
			this.craftingDuration = compound.getInteger("craftingDuration");
		}
		
	}
	
	/*
	 *  Sided inventory
	 * 
	 */

	@Override
	public int getSizeInventory() {
		return mainInventory.getSlots();
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return mainInventory.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack s = mainInventory.getStackInSlot(index);
		s.shrink(count);
		return s;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return mainInventory.extractItem(index, mainInventory.getStackInSlot(index).getCount(), false);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		mainInventory.setStackInSlot(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side == EnumFacing.DOWN) {
			return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
		} else {
			return new int[] { 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49 };
		}
 		
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}
	
	
	

}
