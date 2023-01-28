package com.jimholden.conomy.blocks.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Ints;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.drugs.DrugCombiner;
import com.jimholden.conomy.drugs.IChemical;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemDrugPowder;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.AddItemPacket;
import com.jimholden.conomy.util.packets.AddLootPacket;

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
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityChemInfuser extends TileEntity implements ITickable {
	
	private String customName;
	public ItemStackHandler handler = new ItemStackHandler(4);
	public int timer;
	public int maxTime = 200;
	private boolean isWorking = false;
	private boolean startMix;
	
	
	public TileEntityChemInfuser() {
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
	
	
	
	
	public void produce() {
		ItemStack stackOne = handler.getStackInSlot(0);
		ItemStack stackTwo = handler.getStackInSlot(1);
		ItemStack stackThree = handler.getStackInSlot(2);
		
		List<ItemStack> list = Arrays.asList(stackOne, stackTwo, stackThree); 
	
		
		if(stackOne.getItem() instanceof IChemical && stackTwo.getItem() instanceof IChemical) {
			handler.setStackInSlot(0, ItemStack.EMPTY);
			handler.setStackInSlot(1, ItemStack.EMPTY);
			handler.setStackInSlot(2, ItemStack.EMPTY);
			
			
			float potencyTotal = 0.0F;
			for(ItemStack stack : list) {
				if(!stack.isEmpty()) {
					if(stack.getItem() instanceof IChemical) {
						potencyTotal += ((IChemical) stack.getItem()).getPotency(stack);
					}
					
				}
				
				
			}
			/*
			float potencyOne = ((IChemical) stackOne.getItem()).getPotency(stackOne);
			float potencyTwo = ((IChemical) stackTwo.getItem()).getPotency(stackTwo);
			if
			float potencyThree = ((IChemical) stackThree.getItem()).getPotency(stackThree);
			*/
			
			int drugTypeOne;
			if(stackOne != ItemStack.EMPTY) {
				drugTypeOne = ((IChemical) stackOne.getItem()).getDrugType(stackOne);
			} else {
				drugTypeOne = 0;
			}
			
			int drugTypeTwo;
			if(stackTwo != ItemStack.EMPTY) {
				drugTypeTwo = ((IChemical) stackTwo.getItem()).getDrugType(stackTwo);
			} else {
				drugTypeTwo = 0;
			}
			
			
			int drugTypeThree;
			if(stackThree != ItemStack.EMPTY) {
				drugTypeThree = ((IChemical) stackThree.getItem()).getDrugType(stackThree);
			} else {
				drugTypeThree = 0;
			}
			
			System.out.println("One: " + drugTypeOne + " | " + drugTypeTwo + " | " + drugTypeThree + " |");
			ItemStack newDrug = new ItemStack(ModItems.POWDER);
			((ItemDrugPowder) newDrug.getItem()).setDrugName(newDrug, DrugCombiner.combineName(drugTypeOne, drugTypeTwo, drugTypeThree));
			((ItemDrugPowder) newDrug.getItem()).setPotency(newDrug, potencyTotal);
			
			
			
			
			handler.setStackInSlot(3, newDrug);
			
		}
		
	}
	
	@Override
	public void update() {
		if(isWorking) {
			timer++;
		}
		
		if(timer >= maxTime) {
			timer = 0;
			isWorking = false;
			
			
		
			ItemStack newStack = new ItemStack(Blocks.GRASS);
			newStack.grow(5);
			Main.NETWORK.sendToServer(new AddItemPacket(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
			
		}
		
		
		
	}
		
	

	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		super.writeToNBT(compound);
		compound.setTag("Inventory", this.handler.serializeNBT());
		return compound;
		
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
     }
	

}
