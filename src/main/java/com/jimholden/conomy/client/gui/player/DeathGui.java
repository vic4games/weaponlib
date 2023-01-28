package com.jimholden.conomy.client.gui.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.LedgerTransfer;
import com.jimholden.conomy.util.packets.LedgerTransferThree;
import com.jimholden.conomy.util.packets.LedgerTransferTwo;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;

import ibxm.Player;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.swing.event.KeyTyped;


public class DeathGui extends GuiScreen {
	
	Minecraft minecraft = Minecraft.getMinecraft();
	EntityPlayer p;
	ScaledResolution sr = new ScaledResolution(minecraft);
	double screenHeight = sr.getScaledHeight_double();
	double screenWidth = sr.getScaledWidth_double();
	public GuiButton transfer;
	public GuiButton leftPage;
	public GuiButton rightPage;
	public GuiButton upButton;
	public GuiButton downButton;
	public GuiTextField transferAmount;
	public GuiButton confirm;
	public GuiButton cancel;
	String selectedkey;
	String lockedKey;
	private int displayAmount;
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guiledger.png");
	private int reqMoney = 0;
	int xSize = 176;
	//int ySize = 112;
	int ySize = 112;
	int guiLeft = 135;
	int guiTop = 60;
	
	int pageSelected = 1;
	int totalPages = 1;
	int highlightedOption = 0;
	int maxOptions = 1;
	private boolean confirmationScreen = false;
	
	
	
	public DeathGui(EntityPlayer p) {
		this.p = p;
		
		
		
	}
	

	public void drawMenuBackground(int tint)
    {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURES);
        

    }
	

	@Override
	public void drawDefaultBackground() {
		// TODO Auto-generated method stub
		super.drawDefaultBackground();
	}
	
	@Override // 177, 0 | 187, 11
	public void initGui() {
		super.initGui();
	}
	

	
	@Override
	public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		// TODO Auto-generated method stub
		super.drawCenteredString(fontRendererIn, text, x, y, color);
	}
	
	@Override
	public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		// TODO Auto-generated method stub
		super.drawString(fontRendererIn, text, x, y, color);
	}
	
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		super.drawScreen(mouseX, mouseY, partialTicks);
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
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public void onGuiClosed() {
		
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}
	
	

}



