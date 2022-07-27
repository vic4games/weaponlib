package com.vicmatskiv.weaponlib.crafting.workbench;

import com.vicmatskiv.weaponlib.compatibility.CompatibleGuiContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class GUIContainerWorkbench extends CompatibleGuiContainer {

	private EntityPlayer player;
	private InventoryPlayer inventory;
	
	public GUIContainerWorkbench(EntityPlayer player, InventoryPlayer inventory, TileEntityWorkbench tileEntityWorkbench) {
		super(new ContainerWorkbench(player, inventory, tileEntityWorkbench));
		
		this.player = player;
		this.inventory = inventory;
	}
	
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		
	}
	
	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		super.onGuiClosed();
	}
	

}
