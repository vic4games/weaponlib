package com.jimholden.conomy.blocks.tileentity;

import java.util.ArrayList;

import com.google.common.primitives.Ints;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;

import net.minecraft.entity.player.EntityPlayer;
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

public class TileEntityNode extends TileEntity implements ITickable {
	private String customName;
	public ArrayList<TileEntityMiner> nodes = new ArrayList<TileEntityMiner>();
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	public ArrayList<Integer> connX = new ArrayList<Integer>();
	public ArrayList<Integer> connY = new ArrayList<Integer>();
	public ArrayList<Integer> connZ = new ArrayList<Integer>();
	public ItemStackHandler handler = new ItemStackHandler(1);
	public int totalPower;
	private ForgeChunkManager.Ticket ticket;
	private ChunkPos chunk;
	//public ArrayList<BlockPos> connT = new ArrayList<BlockPos>();
	
	public TileEntityNode() {
		System.out.println("New node entity created.");
		//this.miningPower = 50;
		// TODO Auto-generated constructor stub
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
	
	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
		
	}
	
	public void unforceChunkLoad()
	{
		ForgeChunkManager.unforceChunk(ticket, chunk);
		release();
	}
	
	
	public void forceChunkLoad()
	{
		if(ticket == null)
		{
			ticket = ForgeChunkManager.requestTicket(Main.instance, world, ForgeChunkManager.Type.NORMAL);
		}
		
		ticket.getModData().setInteger("xPos", getPos().getX());
        ticket.getModData().setInteger("yPos", getPos().getY());
        ticket.getModData().setInteger("zPos", getPos().getZ());
        if(chunk == null)
        {
        	chunk = new ChunkPos(getPos().getX()/16, getPos().getZ()/16);
        }
        ForgeChunkManager.forceChunk(ticket, chunk);

		
	}
	
	
	public boolean isCompatItem()
	{
		ItemStack itemstack = this.handler.getStackInSlot(0);
		if((itemstack.getItem() instanceof OpenDimeBase) || (itemstack.getItem() instanceof LedgerBase))
		{
			return true;
		}
		else return false;
	}

	public double deviceBalance()
	{
		ItemStack itemstack = this.handler.getStackInSlot(0);
		if(itemstack.getItem() instanceof OpenDimeBase)
		{
			return ((OpenDimeBase) itemstack.getItem()).getBalance(itemstack);

		}
		if(itemstack.getItem() instanceof LedgerBase)
		{
			return ((LedgerBase) itemstack.getItem()).getBalance(itemstack);
		}
		else {
			return 0;
		}
	}

	public void setDeviceBalance(int credits)
	{
		ItemStack itemstack = this.handler.getStackInSlot(0);
		if(itemstack.getItem() instanceof OpenDimeBase)
		{
			((OpenDimeBase) itemstack.getItem()).setBalance(credits, itemstack);
			this.markDirty();
			

		}
		if(itemstack.getItem() instanceof LedgerBase)
		{
			((LedgerBase) itemstack.getItem()).setBalance(credits, itemstack);
			this.markDirty();
		}
	}
	
	public void setTicket(Ticket t) {
        if (ticket != t && ticket != null && ticket.world == this.getWorld()) {
            for (ChunkPos chunk : ticket.getChunkList()) {
                if (ForgeChunkManager.getPersistentChunksFor(this.getWorld()).keys().contains(chunk)) {
                    ForgeChunkManager.unforceChunk(ticket, chunk);
                }
            }
            ForgeChunkManager.releaseTicket(ticket);
        }
        System.out.println("Ticket setter set.");
        ticket = t;
    }

	
	public void forceChunks(Ticket ticket)
	{
		setTicket(ticket);
		if(chunk == null)
        {
        	chunk = new ChunkPos(getPos().getX()/16, getPos().getZ()/16);
        	System.out.println("ChunkForcer called.");
        }
		System.out.println("Forcing chunk at " + chunk + " with ticket " + ticket);
	    ForgeChunkManager.forceChunk(ticket, chunk);
	}
	

	
	public void release() {
        setTicket(null);
    }

	@Override
	public void update() {
		if(!world.isRemote)
		{
			int tempPower = 0;
			
			for(int x = 0; x < this.connX.size(); x++)
			{
				if(world.getTileEntity(this.getConnectedBlock(x)) != null)
				{
					tempPower += ((TileEntityMiner) world.getTileEntity(this.getConnectedBlock(x))).getPower();
					
				}
				else
				{
					this.removeConnection(x);
				}
				
			}
			if(this.totalPower != tempPower) this.totalPower = tempPower;
			// TODO Auto-generated method stub
			
			if (ticket != null && ticket.world != this.getWorld()) {
                release();
            }
			
		
			
			// CHUNKS BABY!
			
			if(ticket == null)
			{
				ticket = ForgeChunkManager.requestTicket(Main.instance, world, ForgeChunkManager.Type.NORMAL);
				if(ticket != null)
				{
					ticket.getModData().setInteger("xPos", getPos().getX());
			        ticket.getModData().setInteger("yPos", getPos().getY());
			        ticket.getModData().setInteger("zPos", getPos().getZ());
			        forceChunks(ticket);
			        //System.out.println("forcing");
			        ForgeChunkManager.forceChunk(ticket, new ChunkPos(getPos().getX()/16, getPos().getZ()/16));
				}
			}
			
			
			
			
			
		}
		}
		
	
	public int getPower()
	{
		return this.totalPower;
	}
	
	public BlockPos getConnectedBlock(int index)
	{
		return new BlockPos(this.connX.get(index), this.connY.get(index), this.connZ.get(index));
	}
	
	public void newConnection(int x, int y, int z)
	{
		this.connX.add(x);
		this.connY.add(y);
		this.connZ.add(z);
	}
	
	public void removeConnection(int index)
	{
		this.connX.remove(index);
		this.connY.remove(index);
		this.connZ.remove(index);
	}
	
	public boolean checkConnection(int x, int y, int z)
	{
		if(connX.contains(x) && connY.contains(y) && connZ.contains(z))
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	public ArrayList toArrayList(int[] arr)
	{
		ArrayList list = new ArrayList();
		for(int x = 0; x < arr.length; x++)
		{
			list.add(arr[x]);
		}
		return list;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
		this.connX = toArrayList(compound.getIntArray("ConX"));
		this.connY = toArrayList(compound.getIntArray("ConY"));
		this.connZ = toArrayList(compound.getIntArray("ConZ"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		super.writeToNBT(compound);
		compound.setIntArray("ConX", Ints.toArray(connX));
		compound.setIntArray("ConY", Ints.toArray(connY));
		compound.setIntArray("ConZ", Ints.toArray(connZ));
		compound.setTag("Inventory", this.handler.serializeNBT());
		return compound;
		
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
     }
	

}
