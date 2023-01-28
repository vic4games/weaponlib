package com.jimholden.conomy.client.gui.player;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityMiner;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerMiningBlock;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class GuiMiningBlock extends GuiContainer {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guiminingblock.png");
	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	private final InventoryPlayer player;
	private EntityPlayer p = null;
	private EntityPlayerMP pMP = null;
	private final TileEntityMiner tileentity;
	
	
	
	public GuiMiningBlock(EntityPlayer player, TileEntityMiner tileentity, MinecraftServer server) {
		super(new ContainerMiningBlock(player.inventory, tileentity));
		this.player = player.inventory; 
		this.tileentity = tileentity;
		this.p = player;
	}
	
	@Override
	public void initGui() {
		super.initGui();
	}
	
	public void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, int color, float scale) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		drawString(fontRendererIn, text, (int) (x/scale), (int) (y/scale), color);
		GL11.glPopMatrix();
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String tileName = this.tileentity.getDisplayName().getFormattedText();
		this.fontRenderer.drawString(TextFormatting.GREEN + "" + this.tileentity.getPower() + "TH/s", (this.xSize) - 150, 14, 4210752);
		drawScaledString(this.fontRenderer, TextFormatting.GREEN + "Miner Compat Index: " + TextFormatting.WHITE + TextFormatting.ITALIC + "Class " + this.tileentity.compatType, (this.xSize) - 118, 50, 4210752, 0.6F);
		if(this.tileentity.hasFlash()) {
			drawScaledString(this.fontRenderer, TextFormatting.GREEN + "SoftPower: " + TextFormatting.WHITE + TextFormatting.ITALIC + this.tileentity.getSoftwarePower() + "TH/s (C" + this.tileentity.getSoftwareCompat() + ")", (this.xSize) - 118, 55, 4210752, 0.6F);

		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		
	}
	

}
