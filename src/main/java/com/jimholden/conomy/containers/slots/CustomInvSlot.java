package com.jimholden.conomy.containers.slots;

import javax.annotation.Nonnull;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.util.packets.InventoryServerPacket;
import com.jimholden.conomy.util.packets.stock.RegisterStockPacket;
import com.mojang.realmsclient.dto.PlayerInfo;

import net.minecraft.client.renderer.texture.Stitcher.Slot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class CustomInvSlot extends SlotItemHandler {
	
	public float scale;
	public EntityPlayer player;

	public CustomInvSlot(EntityPlayer player, IItemHandler itemHandler, int index, int xPosition, int yPosition, float scale) {
		super(itemHandler, index, (int) ((float)xPosition/scale), (int) ((float)yPosition/scale));
		this.player = player;
		this.scale = scale;
	}
	
	@Override
    public void putStack(@Nonnull ItemStack stack)
    {
		player.getCapability(InvProvider.EXTRAINV, null).setStackInSlot(this.getSlotIndex(), stack);
        this.onSlotChanged();
    }
	
	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		// TODO Auto-generated method stub
		return thePlayer.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(this.getSlotIndex());
	}
	
	
	@Override
	public ItemStack getStack() {
		// TODO Auto-generated method stub
		return player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(this.getSlotIndex());
	}
	
	/*
	@Override
	public void onSlotChanged() {
		Main.NETWORK.sendToServer(new InventoryServerPacket(this.getSlotIndex(), this.getStack(), this.player.getEntityId()));
		super.onSlotChanged();
	}
	*/
	
	
	public int getRenderSize() {
		return (int) (16.0F*scale);
		
	}
	

	

}
