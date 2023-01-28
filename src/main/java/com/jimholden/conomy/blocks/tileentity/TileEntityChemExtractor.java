package com.jimholden.conomy.blocks.tileentity;

import java.util.ArrayList;

import com.google.common.primitives.Ints;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.IMachine;
import com.jimholden.conomy.containers.slots.AdvItemHandler;
import com.jimholden.conomy.drugs.DrugCombiner;
import com.jimholden.conomy.drugs.DrugExtractRecipe;
import com.jimholden.conomy.drugs.IChemical;
import com.jimholden.conomy.drugs.components.DrugComponentPreset;
import com.jimholden.conomy.drugs.components.DrugComponentTool;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemBaseComponent;
import com.jimholden.conomy.items.ItemDrugPowder;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.AddItemPacket;
import com.jimholden.conomy.util.packets.AddItemPacketExtract;
import com.jimholden.conomy.util.packets.AddLootPacket;
import com.jimholden.conomy.util.packets.StateAutoClient;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityChemExtractor extends TileEntity implements ITickable, IMachine {
	
	private String customName;
	public AdvItemHandler handler = new AdvItemHandler(4);
	public int timer;
	public int maxTime = 100;
	private boolean isWorking = false;
	private boolean startMix;
	
	
	public TileEntityChemExtractor() {
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return this.hasCustomName() ? this.customName : "container.atm";
	}

	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return this.customName != null && !this.customName.isEmpty();
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	public void startMixing() {
		this.isWorking = true;
	}
	
	
	public boolean isMixing() {
		return this.isWorking;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
		
	}
	
	public boolean canStart() {
		ItemStack stackBase = handler.getStackInSlot(0);
		if(stackBase.getItem() instanceof ItemBaseComponent) {
			System.out.println("yes");
			ItemStack outputStack = ((ItemBaseComponent) stackBase.getItem()).getOutputStack();
			double weight = ((ItemBaseComponent) stackBase.getItem()).getWeight(stackBase);
			if(handler.getStackInSlot(1).isEmpty() && handler.getStackInSlot(2).isEmpty() && handler.getStackInSlot(3).isEmpty()) {
				return true;
			}
			
			if(ItemHandlerHelper.canItemStacksStack(outputStack, handler.getStackInSlot(1)) && ItemHandlerHelper.canItemStacksStack(outputStack, handler.getStackInSlot(2)) && ItemHandlerHelper.canItemStacksStack(outputStack, handler.getStackInSlot(3))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	
	
	
	public void produce() {
		ItemStack stackBase = handler.getStackInSlot(0);
		if(stackBase.getItem() instanceof ItemBaseComponent) {
			
			ItemStack stack = ((ItemBaseComponent) stackBase.getItem()).getOutputStack();
			((IChemical) stack.getItem()).setToxicity(stack, ((ItemBaseComponent) stackBase.getItem()).getToxicity());
			//double weight = ((ItemBaseComponent) stackBase.getItem()).getWeight(stackBase);
			
			handler.setStackInSlot(0, ItemStack.EMPTY);

			ItemStack stackOne = stack.copy();
			ItemStack stackTwo = stack.copy();
			ItemStack stackThree = stack.copy();
			
			handler.insertItem(1, stackOne, false);
			handler.insertItem(2, stackTwo, false);
			handler.insertItem(3, stackThree, false);
			
			
			/*
			 * 
			 * ItemStack stack = ((ItemBaseComponent) stackBase.getItem()).getRecipe();
			double weight = ((ItemBaseComponent) stackBase.getItem()).getWeight(stackBase);
			
			handler.setStackInSlot(0, ItemStack.EMPTY);

			ItemStack stackOne = recipe.getItemOne(weight);
			ItemStack stackTwo = recipe.getItemTwo(weight);
			ItemStack stackThree = recipe.getItemThree(weight);
			
			handler.insertItem(1, stackOne, false);
			handler.insertItem(2, stackTwo, false);
			handler.insertItem(3, stackThree, false);
			
			*/
			/*
			handler.setStackInSlot(1, stackOne);
			handler.setStackInSlot(2, stackTwo);
			handler.setStackInSlot(3, stackThree);
			*/
			
		}
		
	}
	
	@Override
	public void update() {
		
		
		if(this.world.isRemote) return;
		if(isWorking) {
			timer++;
		}
		
		if(timer >= maxTime) {
			timer = 0;
			isWorking = false;
			
			
			produce();
		}
		
		if(!this.world.isRemote) {
			Main.NETWORK.sendToAll(new StateAutoClient(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), isAuto(), timer));
		}
		
		
		
	}
		
	

	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Inventory", this.handler.serializeNBT());
		return compound;
		
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
     }

	@Override
	public void setTimer(int val) {
		this.timer = val;
		
	}

	@Override
	public int getTimer() {
		// TODO Auto-generated method stub
		return this.timer;
	}

	@Override
	public void setAuto(boolean state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAuto() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
