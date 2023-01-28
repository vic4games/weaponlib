package com.jimholden.conomy.blocks.tileentity;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

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
import net.minecraft.nbt.NBTTagList;
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
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityATM extends ModdedTileEntityBase {

	public HashMap<UUID, ItemStackHandler> itemMap = new HashMap<>();





	public ItemStack getStackInSlot(UUID uuid) {
		if(itemMap.isEmpty()) return ItemStack.EMPTY;
		return itemMap.get(uuid).getStackInSlot(0);
	}

	
	public ItemStackHandler getHandler(UUID uuid) {
		if(itemMap.isEmpty() || !itemMap.containsKey(uuid)) {
			itemMap.put(uuid, new ItemStackHandler(1));
			markDirty();
		}
		
		
		return itemMap.get(uuid);
	}

	
	public boolean isCompatItem(UUID uuid)
	{
		ItemStack itemstack = getStackInSlot(uuid);
		if((itemstack.getItem() instanceof OpenDimeBase) || (itemstack.getItem() instanceof LedgerBase))
		{
			return true;
		}
		else return false;
	}
	
	
	
	public void setDeviceBalance(UUID uuid, double credits)
	{
		ItemStack itemstack = getStackInSlot(uuid);
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
	
	public double deviceBalance(UUID uuid)
	{
		ItemStack itemstack = getStackInSlot(uuid);
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
	


	@Override
	public void updateContainingBlockInfo() {
		// TODO Auto-generated method stub
		super.updateContainingBlockInfo();
	}
	
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 1;
	}



	public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
		
	}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}
	
	public String getGuiID()
	{
		return "conomy:atm";
	}



	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		
		NBTTagList list = compound.getTagList("itemMap", NBT.TAG_COMPOUND);
		
		for(int x = 0; x < list.tagCount(); ++x)  {
			NBTTagCompound comp = (NBTTagCompound) list.get(x);
			ItemStackHandler handler = new ItemStackHandler(1);
			handler.deserializeNBT((NBTTagCompound) comp.getTag("Item"));
			this.itemMap.put(comp.getUniqueId("UUID"), handler);
		}

	}
	
	
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		NBTTagList list = new NBTTagList();
		
		for(Entry<UUID, ItemStackHandler> entry : this.itemMap.entrySet()) {
			NBTTagCompound nbtEntry = new NBTTagCompound();
			nbtEntry.setUniqueId("UUID", entry.getKey());
			nbtEntry.setTag("Item", entry.getValue().serializeNBT());
			list.appendTag(nbtEntry);
		}
		
		compound.setTag("itemMap", list);

		
		return compound;
	}



	
	

	
	
	
	

}
