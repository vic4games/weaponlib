package com.vicmatskiv.weaponlib.crafting.workbench;

import com.vicmatskiv.weaponlib.crafting.CraftingEntry;
import com.vicmatskiv.weaponlib.crafting.CraftingGroup;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;
import com.vicmatskiv.weaponlib.crafting.items.CraftingItem;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityWorkbench extends TileEntityStation {


	public IModernCrafting craftingTarget;

	// For the client.
	public String craftingTargetName;


	public int ticker;



	public TileEntityWorkbench() {
	}

	@Override
	public void onLoad() {
		super.onLoad();

	}



	@Override
	public NBTTagCompound getUpdateTag() {
		return super.getUpdateTag();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return super.getUpdatePacket();
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}
	
	
	@Override
	public void writeBytesForClientSync(ByteBuf buf) {
		super.writeBytesForClientSync(buf);
		if(this.craftingTarget != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, this.craftingTarget.getItem().getUnlocalizedName());
		} else {
			buf.writeBoolean(false);
		}
		
	}
	
	@Override
	public void readBytesFromClientSync(ByteBuf buf) {
		super.readBytesFromClientSync(buf);
		if(buf.readBoolean()) {
			this.craftingTargetName = ByteBufUtils.readUTF8String(buf);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (craftingTimer != -1) {
			compound.setInteger("craftingTargetID", this.craftingTarget.getCraftingGroup().getID());
			compound.setString("craftingTargetName", this.craftingTarget.getItem().getUnlocalizedName());

		}
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("craftingTimer") && compound.hasKey("craftingDuration")) {
			this.craftingTarget = CraftingRegistry.getModernCrafting(
					CraftingGroup.getValue(compound.getInteger("craftingTargetID")),
					compound.getString("craftingTargetName"));
		}

	}

	public void setTimer(int time, int duration) {
		this.craftingTimer = time;
		this.craftingDuration = duration;
	}

	

	@Override
	public void update() {
		super.update();
	
		
		if (this.craftingTimer != -1) {
			this.craftingTimer++;
		}

		

		if (getProgress() >= 1) {
			craftingTimer = -1;
			craftingDuration = -1;


			if (!this.world.isRemote) {
				addStackToInventoryRange(new ItemStack(this.craftingTarget.getItem()), 0, 9);
			}

		}

	}

}
