package com.jimholden.conomy.proxy;

import com.jimholden.conomy.client.gui.player.LedgerGui;
import com.jimholden.conomy.teisr.TEISRBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy 
{
	public void registerRenderInfo() { }
	
	public void registerItemRenderer(Item item, int meta, String id) {}
	
	public void showAdvancedGUI(EntityPlayer player, int id) {}
	
	public void showLedgerGUI(EntityPlayer player) {}
	
	public void showStockGUI(EntityPlayer player) {}

	public void showDeathGUI(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}
	
	public void openBankWRedirect(EntityPlayer player, BlockPos pos, int gui) {}
	
	public void registerTEISRUnique(Item item, TEISRBase teisr, ModelBiped model) {
	}

	public void registerRenderLayers() {}

	public void registerFont() {
		// TODO Auto-generated method stub
		
	}

}
