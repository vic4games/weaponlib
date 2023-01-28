package com.jimholden.conomy.client.gui.player;

import java.io.IOException;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.client.gui.NewInventory;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.containers.ContainerLootBody;
import com.jimholden.conomy.containers.ContainerLootBody2;
import com.jimholden.conomy.containers.GuiTestContainer;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.MessageGetCredits;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class GuiLootBody extends GuiContainer {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/inventorynew.png");

	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	private EntityPlayer player = null;
	private EntityPlayer target = null;
	int ySize = 186;
	int xSize = 208;
	
	
	
	
	public GuiLootBody(EntityPlayer player, EntityPlayer target, MinecraftServer server) {
	//	super(new ContainerLootBody2(player.inventory, target.getCapability(InvProvider.EXTRAINV, null).getHandler()));
		//IInvCapa capa = ;
		//super(new ContainerLootBody2(player.inventory, target.getCapability(InvProvider.EXTRAINV, null).getHandler()));
		super(new ContainerLootBody2(player.inventory, target));
		//super(new ContainerLootBody(player.inventory, target));
		this.player = player;
		this.target = target;
	}
	
	@Override
	public void initGui() {
		
		super.initGui();
		this.guiLeft += 200;
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		drawDefaultBackground();
		
		this.mc.getTextureManager().bindTexture(NewInventory.GRAY_IMAGE);
        GlStateManager.color(1.0F, 0.8F, 0.8F, 0.2F);
        
        this.drawTexturedModalRect(this.guiLeft,this.guiTop, 0, 0, 120, 160);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		//GuiContainerCreative
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 125, 160);
		NewInventory.drawEntityOnScreen((int) (this.guiLeft+33), this.guiTop + 80, 30, (float)(this.guiLeft + 51) - mouseX, (float)(this.guiTop + 75 - 50) - mouseY, target);
	    
		
		
		
		GlStateManager.disableLighting();
    	GlStateManager.disableDepth();
    	GlStateManager.enableTexture2D();
		for(int f = 0; f < this.inventorySlots.inventorySlots.size(); ++f) {
			Slot s = this.inventorySlots.inventorySlots.get(f);
			//System.out.println("hi!");
			if(s.inventory == player.inventory && s.getSlotIndex() >= 0 && s.getSlotIndex() < 9) {
			//	System.out.println("hi!");
				
	        	
	        	
	        	this.mc.getTextureManager().bindTexture(GuiTestContainer.BACKPACK_SLOT);
	        	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        	
	        	this.drawTexturedModalRect(this.guiLeft + s.xPos, this.guiTop + s.yPos, 16, 0, 16, 16);
	        	//this.drawTexturedModalRect(i, j, 0, 0, 16, 16);
	        	
				
			}
			if(s.inventory == target.inventory && s.getSlotIndex() >= 0 && s.getSlotIndex() < 9) {
				//	System.out.println("hi!");
					
		        	
		        	
		        	this.mc.getTextureManager().bindTexture(GuiTestContainer.BACKPACK_SLOT);
		        	GlStateManager.color(1.0F, 0.8F, 0.8F, 0.2F);
		        	
		        	this.drawTexturedModalRect(this.guiLeft + s.xPos, this.guiTop + s.yPos, 16, 0, 16, 16);
		        	//this.drawTexturedModalRect(i, j, 0, 0, 16, 16);
		        	
					
				}
		}
		
		GlStateManager.enableTexture2D();
    	GlStateManager.enableDepth();
    	GlStateManager.enableLighting();
		
		//this.drawTexturedModalRect(this.guiLeft, this.guiTop-25, 0, 0, this.xSize, this.ySize);
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}

	
	
	@Override
	public void updateScreen() {
		super.updateScreen();
	}
	
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}

}
