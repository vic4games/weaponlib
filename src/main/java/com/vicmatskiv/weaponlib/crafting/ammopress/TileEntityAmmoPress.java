package com.vicmatskiv.weaponlib.crafting.ammopress;

import java.util.LinkedList;

import com.vicmatskiv.weaponlib.crafting.CraftingEntry;
import com.vicmatskiv.weaponlib.crafting.CraftingGroup;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityAmmoPress extends TileEntityStation {
	
	
	
	
	public LinkedList<ItemStack> craftStack = new LinkedList<>();
	
	
	
	
	public boolean pushInventoryRefresh = false;

	

	
	private boolean makingBullets = false;
	private double currentWheelRotation = 0.0;
	private double prevWheelRotation = 0.0;
	
	public TileEntityAmmoPress() {
		// TODO Auto-generated constructor stub
	}
	
	public double getCurrentWheelRotation() {
		return currentWheelRotation;
	}
	
	public double getPreviousWheelRotation() {
		return prevWheelRotation;
	}
	
	public double getProgress() {
		if (craftingTimer == -1 || craftingDuration == -1)
			return 0.0;
		return craftingTimer / (double) craftingDuration;
	}
	
	public ItemStack getLatestStackInQueue() {
		if(this.craftStack.isEmpty()) return null;
		ItemStack stack = craftStack.peek();
		if(stack.isEmpty()) {
			
			craftStack.pop();
			return getLatestStackInQueue();
		}

		return stack;
	}
	
	@Override
	public void writeBytesForClientSync(ByteBuf buf) {
		super.writeBytesForClientSync(buf);
		
		buf.writeInt(this.craftStack.size());
		for(ItemStack stack : craftStack) {
			ByteBufUtils.writeItemStack(buf, stack);
		}
	}
	
	@Override
	public void readBytesFromClientSync(ByteBuf buf) {
		super.readBytesFromClientSync(buf);
		this.craftStack.clear();
		
		int size = buf.readInt();
		for(int i = 0; i < size; ++i) {
			this.craftStack.offer(ByteBufUtils.readItemStack(buf));
		}
		
	}
	
	public boolean hasStack() {
		return !this.craftStack.isEmpty() && getLatestStackInQueue() != null;
	}
	
	public void addStack(ItemStack stack) {
		this.craftStack.offer(stack);
	}
	
	public LinkedList<ItemStack> getCraftingQueue() {
		return this.craftStack;
	}
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		
		NBTTagList stackNBTCompound = new NBTTagList();
		int pos = 0;
		for(int i = 0; i < this.craftStack.size(); ++i) {
			ItemStack stack = this.craftStack.get(i);
			NBTTagCompound element = new NBTTagCompound();
			stack.writeToNBT(element);
			stackNBTCompound.appendTag(element);
			
		}
		compound.setTag("craftingStack", stackNBTCompound);
		

		
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(compound.hasKey("craftingStack")) {
			NBTTagList list = compound.getTagList("craftingStack", NBT.TAG_COMPOUND);
			for(int i = 0; i < list.tagCount(); ++i) {
				this.craftStack.offer(new ItemStack(list.getCompoundTagAt(i)));
			}
		}
		
		
		

	}

	
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		super.update();
		
		
	
		if(hasStack()) {
			
			boolean canCraftNextItem = true;
			for(CraftingEntry entry : ((IModernCrafting) getLatestStackInQueue().getItem()).getModernRecipe()) {
				if(!inventoryContainsEnoughItems(entry.getItem(), entry.getCount(), 22, 49)) {
				
					canCraftNextItem = false;
					break;
				}
			}
	
			if(craftingDuration == -1 && canCraftNextItem) {
				craftingDuration = 2;
			}
			
			
			
			prevCraftingTimer = craftingTimer;
			
			if(craftingDuration != -1) craftingTimer++;
			if(craftingTimer > craftingDuration) {
				
				
				craftingTimer = -1;
				prevCraftingTimer = -1;
				craftingDuration = -1;
				ItemStack stack = getLatestStackInQueue();
				
				IModernCrafting craftingRecipe = (IModernCrafting) stack.getItem();
				
				for(CraftingEntry ingredient : craftingRecipe.getModernRecipe()) {
					consumeFromInventory(ingredient.getItem(), ingredient.getCount(), 22, 49);
				}
				
				ItemStack splitOff = stack.splitStack(1);
				
				addStackToInventoryRange(splitOff, 0, 8);
				
				
 				
				
				
					
				
			}
			
			
		}
		
		
		
	
		if(this.world.isRemote && hasStack()) {
			prevWheelRotation = currentWheelRotation;
			currentWheelRotation += Math.PI/32;
			
			if(currentWheelRotation >= 2*Math.PI) {
				prevWheelRotation = 0;
				currentWheelRotation = 0;
			}
		} else if(!hasStack() && this.world.isRemote) {
			// Velocity verlet integrator
			double delta = (currentWheelRotation - prevWheelRotation) * 0.05;
			prevWheelRotation = currentWheelRotation;
			currentWheelRotation += delta;
		}
		
	}

}
