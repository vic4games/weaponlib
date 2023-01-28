package com.jimholden.conomy.client.gui.player;

import java.io.IOException;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerBriefcase;
import com.jimholden.conomy.containers.ContainerLootBody;
import com.jimholden.conomy.items.ItemChemicalBriefcase;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.MessageGetCredits;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;

import net.java.games.input.Keyboard;
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
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import scala.swing.event.Key;


public class GuiChemBriefcase extends GuiContainer {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/briefcase.png");
	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	private EntityPlayer player = null;
	private ItemStackHandler target = null;
	private ItemChemicalBriefcase briefcase;
	int ySize = 186;
	int xSize = 208;
	public GuiTextField transferAmount;
	
	
	
	
	public GuiChemBriefcase(EntityPlayer player, int hand, MinecraftServer server) {
		super(new ContainerBriefcase(player, hand));
		this.player = player;
		ItemStack stack = null;
		if(hand == 0) {
			stack = player.getHeldItemMainhand();
		} else {
			stack = player.getHeldItemOffhand();
		}
		
		this.target = ((ItemChemicalBriefcase) stack.getItem()).getInv(stack);
		this.briefcase = ((ItemChemicalBriefcase) stack.getItem());
	}
	
	@Override
	public void initGui() {
		super.initGui();
		FontRenderer fontrenderer2 = Minecraft.getMinecraft().fontRenderer;
		transferAmount = new GuiTextField(6, fontrenderer2, 52, 131, 59, 14);
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		transferAmount.drawTextBox();
		transferAmount.setMaxStringLength(10);
		transferAmount.drawTextBox();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop-25, 0, 0, this.xSize, this.ySize);
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		transferAmount.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		Character c = typedChar;
		transferAmount.textboxKeyTyped(typedChar, keyCode);
		if (transferAmount.isFocused())
			return;
		super.keyTyped(typedChar, keyCode);
	}


	
	@Override
	public void updateScreen() {
		transferAmount.updateCursorCounter();
		super.updateScreen();
	}
	
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}

}
