package com.jimholden.conomy.blocks.tileentity;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityPistolStand extends TileEntity implements ITickable {

	public ItemStackHandler handler = new ItemStackHandler(1);
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	private String customName;
	private int deviceBalance;
	public int serverBal;
	
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		else return false;
		
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) this.handler;
		return super.getCapability(capability, facing);
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
	


	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return this.inventory.size();
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		for(ItemStack stack : this.inventory) {
			if(!stack.isEmpty()) return false;
		}
		return true;
	}
	
	

	public ItemStack getStackInSlot(int index) {
		// TODO Auto-generated method stub
		return this.handler.getStackInSlot(index);
	}

	public ItemStack decrStackSize(int index, int count) {
		// TODO Auto-generated method stub
		return ItemStackHelper.getAndSplit(this.inventory, index, count);
	}

	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return ItemStackHelper.getAndRemove(this.inventory, index);
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemstack = (ItemStack) this.inventory.get(index);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
		this.inventory.set(index, stack);
		if(stack.getCount() > this.getInventoryStackLimit()) stack.setCount(this.getInventoryStackLimit());
		if(index == 0) {
			ItemStack stack1 = (ItemStack) this.inventory.get(index);
			this.markDirty();
		}
		
		
	}
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		// TODO Auto-generated method stub
		return super.getUpdatePacket();
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
	
	public void setDeviceBalance(int credits)
	{
		ItemStack itemstack = this.handler.getStackInSlot(0);
		//System.out.println(itemstack);
		//System.out.println(this.world.isRemote);
		if(itemstack.getItem() instanceof OpenDimeBase)
		{
			//System.out.println("Woah! I was called! " + this.handler.getStackInSlot(0) + " | " + credits);
			//System.out.println("Player is trying to set dev bal");
			((OpenDimeBase) itemstack.getItem()).setBalance(credits, itemstack);
			this.markDirty();
			//IItemHandler handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			//Main.NETWORK.sendTo(new MessageUpdateCredits(current.getBalance()), (EntityPlayerMP) p);
		//	Main.NETWORK.sendToAll(new UpdateDeviceTile(getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0), pos.getX(), pos.getY(), pos.getZ()));
			//Main.NETWORK.sendToServer(new UpdateDeviceTile(credits, pos.getX(), pos.getY(), pos.getZ(), 0));
			//Main.NETWORK.sendToAllAround(new UpdateDeviceTile(credits, pos.getX(), pos.getY(), pos.getZ(), 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 100));
			//Main.NETWORK.sendToAll(new UpdateDeviceTile(credits, pos.getX(), pos.getY(), pos.getZ(), 0));
			/*
			setField(0, credits);
			System.out.println("da stack ~ " + this.handler.getStackInSlot(0));
			System.out.println("da stack ~ " + ((OpenDimeBase) this.handler.getStackInSlot(0).getItem()).getBalance(this.handler.getStackInSlot(0)));
			this.markDirty();
			System.out.println("dbal: " + this.deviceBalance);
			
			*/
			

		}
		if(itemstack.getItem() instanceof LedgerBase)
		{
			((LedgerBase) itemstack.getItem()).setBalance(credits, itemstack);
			this.markDirty();
		}
	}
	
	public double deviceBalance()
	{
		ItemStack itemstack = this.handler.getStackInSlot(0);
		if(itemstack.getItem() instanceof OpenDimeBase)
		{
			//System.out.println("I, MR. TILEENTITY, say that " + ((OpenDimeBase) itemstack.getItem()).getBalance(itemstack));
			
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
	
	public boolean canTransfer()
	{
		if((this.inventory.get(0).getItem() instanceof OpenDimeBase) || (this.inventory.get(0).getItem() instanceof LedgerBase)) return true;
		else return false;
	}

	@Override
	public void updateContainingBlockInfo() {
		// TODO Auto-generated method stub
		super.updateContainingBlockInfo();
	}
	
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 1;
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}

	public void openInventory(EntityPlayer player) {
		System.out.println(player.getServer());
		// TODO Auto-generated method stub
		
	}

	public void closeInventory(EntityPlayer player) {
		System.out.println("closed");
		// TODO Auto-generated method stub
		
	}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public String getGuiID()
	{
		return "conomy:atm";
	}

	public int getField(int id) {
		switch(id) 
		{
		case 0:
			return this.deviceBalance;
		default:
			return this.deviceBalance;
		}
	}

	public void setField(int id, int value) {
		switch(id) 
		{
		case 0:
			this.deviceBalance = value;
			break;
		}
		
	}

	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void clear() {
		this.inventory.clear();
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
		this.deviceBalance = compound.getInteger("Bal");
		ItemStackHelper.loadAllItems(compound, this.inventory);
		if(compound.hasKey("CustomName", 8)) this.setCustomName(compound.getString("CustomName"));
	}
	
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("Bal", this.deviceBalance);
		compound.setTag("Inventory", this.handler.serializeNBT());
		if(this.hasCustomName()) compound.setString("CustomName", this.customName);
		return compound;
	}


	@Override
	public void update() {
		
		/*
		if(handler.getStackInSlot(0).getItem() instanceof OpenDimeBase)
		{
			//return;
			System.out.print(this.world.isRemote);
			System.out.print(((OpenDimeBase) handler.getStackInSlot(0).getItem()).getBalance(handler.getStackInSlot(0)) + "\n");
		}
		System.out.print("end");
		*/
		// TODO Auto-generated method stub
		
	}
	
	

	
	
	
	

}
