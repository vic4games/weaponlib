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
import com.jimholden.conomy.items.ItemDrugBrick;
import com.jimholden.conomy.items.ItemDrugPowder;
import com.jimholden.conomy.items.ItemPackingMaterial;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.AddItemPacket;
import com.jimholden.conomy.util.packets.AddItemPacketExtract;
import com.jimholden.conomy.util.packets.AddLootPacket;
import com.jimholden.conomy.util.packets.StateAutoClient;
import com.jimholden.conomy.util.packets.StateAutoPacket;

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
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityBrickPacker extends TileEntity implements ITickable, IMachine {
	
	private String customName;
	public AdvItemHandler handler = new AdvItemHandler(4);
	public int timer;
	public int maxTime = 50;
	private boolean isWorking = false;
	private boolean startMix;
	private boolean autoMode = false;
	
	
	public TileEntityBrickPacker() {
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
		ItemStack compoundStack = handler.getStackInSlot(0);
		ItemStack packingStack = handler.getStackInSlot(1);
		if(isMixing()) return false;
		if(compoundStack.getItem() instanceof ItemDrugPowder && packingStack.getItem() instanceof ItemPackingMaterial) {
			ItemStack stackBrick = DrugComponentTool.makeBrick(compoundStack, packingStack);
			if(handler.getStackInSlot(2).isEmpty()) {
				return true;
			}
			
			if(ItemHandlerHelper.canItemStacksStack(stackBrick, handler.getStackInSlot(2))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	public boolean canStartManually() {
		if(isAuto()) return false;
		else return canStart();
	}
	
	public void toggleAuto() {
		this.autoMode = !this.autoMode;
		Main.NETWORK.sendToServer(new StateAutoPacket(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.autoMode));
	}
	
	public void setAuto(boolean state) {
		this.autoMode = state;
	}
	
	public boolean isAuto() {
		return this.autoMode;
	}
	
	
	@Override
	public void produce() {
		ItemStack compoundStack = handler.getStackInSlot(0);
		ItemStack packingMaterial = handler.getStackInSlot(1);
		System.out.println("comp: " + compoundStack + " pack: " + packingMaterial);
		if(compoundStack.getItem() instanceof ItemDrugPowder && packingMaterial.getItem() instanceof ItemPackingMaterial) {
			 
			
			/*
			ItemStack newStackOne = handler.getStackInSlot(0);
			newStackOne.shrink(1);
			ItemStack newStackTwo = handler.getStackInSlot(1);
			newStackTwo.shrink(1);
			
			handler.setStackInSlot(0, newStackOne);
			handler.setStackInSlot(1, newStackTwo);
			*/
			handler.shrinkStackInSlot(0);
			handler.shrinkStackInSlot(1);
			
			
			ItemStack stackBrick = DrugComponentTool.makeBrick(compoundStack, packingMaterial);
			
			
			handler.insertItem(2, stackBrick, false);
			
			if(isAuto()) {
				startMixing();
			}
			
			
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
			//Main.NETWORK.sendToServer(new AddItemPacket(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
			
		}
		/*
		if(!this.world.isRemote) {
			Main.NETWORK.sendToAll(new StateAutoClient(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), isAuto(), timer));
			//System.out.println(timer);
		}
		*/
		
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
	

}
