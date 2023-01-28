package com.jimholden.conomy.client.gui.player;

import java.io.IOException;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityLootingBlock;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerLootingBlock;
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


public class GuiLootingBlock extends GuiContainer {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guilooting.png");
	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	private final InventoryPlayer player;
	private EntityPlayer p = null;
	private EntityPlayerMP pMP = null;
	private final TileEntityLootingBlock tileentity;
	ICredit iC = null;
	private int displayAmountACC;
	private int displayAmountDEV;
	private int bal;
	public GuiButton transferBank;
	public GuiButton transferDevice;
	public GuiTextField transferAmount;
	private float conversionFactor = 0.235F;
	
	
	
	public GuiLootingBlock(EntityPlayer player, TileEntityLootingBlock tileentity, MinecraftServer server) {
		super(new ContainerLootingBlock(player.inventory, tileentity, true));
		this.ySize = 176;
		this.xSize = 176;
		this.player = player.inventory; 
		// the player is converted to an inventory player!
		this.tileentity = tileentity;
		this.p = player;

	}
	
	@Override
	public void initGui() {
		super.initGui();
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		String tileName = this.tileentity.getDisplayName().getFormattedText();
		this.fontRenderer.drawString(tileName, (this.xSize - this.fontRenderer.getStringWidth(tileName)) - 125, 6, 4210752);
		
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		//transferAmount.drawTextBox();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		
		
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		this.fontRenderer.drawString("CLID: " + tileentity.clid, this.guiLeft+9, this.guiTop+74, 0x1dd1a1);
		
	}
	


	
	
	@Override
	public void updateScreen() {
		
		super.updateScreen();
	}
	
	private int toMRC(int USD)
	{
		
		return Math.round(((float) USD)/conversionFactor);
		
	}
	
	private int toUSD(int MRC)
	{
		
		return Math.round(((float) MRC)*conversionFactor);
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}

}
