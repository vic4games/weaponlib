package com.jimholden.conomy.client.gui.player;

import java.io.IOException;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerNode;
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


public class GuiNode extends GuiContainer {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guinode.png");
	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	private final InventoryPlayer player;
	private EntityPlayer p = null;
	private EntityPlayerMP pMP = null;
	private final TileEntityNode tileentity;
	ICredit iC = null;
	private int displayAmountACC;
	private int displayAmountDEV;
	private int bal;
	public GuiButton transferBank;
	public GuiButton transferDevice;
	public GuiTextField transferAmount;
	private float conversionFactor = 0.235F;
	
	
	
	public GuiNode(EntityPlayer player, TileEntityNode tileentity, MinecraftServer server) {
		super(new ContainerNode(player.inventory, tileentity));
		this.player = player.inventory; 
		// the player is converted to an inventory player!
		this.tileentity = tileentity;
		this.p = player;
		Minecraft mc = Minecraft.getMinecraft();
		
		System.out.println(player);
		System.out.println(server);
		EntityPlayer pMP = p.world.getPlayerEntityByName(player.getName());
		String user = player.getName().toString();
	}
	
	@Override
	public void initGui() {
		super.initGui();
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String tileName = this.tileentity.getDisplayName().getFormattedText();
		double devMoney = this.tileentity.deviceBalance();
		//this.fontRenderer.drawString(tileName, (this.xSize - this.fontRenderer.getStringWidth(tileName)) - 125, 6, 4210752);
		this.fontRenderer.drawString(TextFormatting.GREEN + "MRC" + this.tileentity.deviceBalance(), (this.xSize) - 150, 14, 4210752);
		this.fontRenderer.drawString(TextFormatting.RED + "" + this.tileentity.totalPower + "TH/s", (this.xSize) - 55, 14, 4210752);
		this.fontRenderer.drawString(TextFormatting.AQUA + "250" + "MB/s", (this.xSize) - 55, 33, 4210752);
		
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
