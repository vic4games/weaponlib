package com.jimholden.conomy.client.gui;

import java.io.IOException;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityBrickPacker;
import com.jimholden.conomy.blocks.tileentity.TileEntityChemExtractor;
import com.jimholden.conomy.blocks.tileentity.TileEntityCompoundMixer;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.blocks.tileentity.TileEntityPillPress;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.client.gui.button.ChemButton;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerBrickPacker;
import com.jimholden.conomy.containers.ContainerChemExtractor;
import com.jimholden.conomy.containers.ContainerCompoundMixer;
import com.jimholden.conomy.containers.ContainerNode;
import com.jimholden.conomy.containers.ContainerPillPress;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.AddItemPacket;
import com.jimholden.conomy.util.packets.MessageGetCredits;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.ServerStartMixPacket;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;

import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class GuiPillPress extends GuiContainer {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guipillpress.png");
	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	private final TileEntityPillPress tileentity;
	public GuiButton startReaction;
	public GuiButton autoMode;
	
	
	
	public GuiPillPress(EntityPlayer player, TileEntityPillPress tileentity, MinecraftServer server) {
		super(new ContainerPillPress(player.inventory, tileentity));
		System.out.println("called");
		this.tileentity = tileentity;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		this.startReaction = new ChemButton(0, this.guiLeft+132, this.guiTop + 52, 18, 18, 176, 0, 18, TEXTURES);
		this.autoMode = new ChemButton(1, this.guiLeft+132, this.guiTop + 18, 18, 18, 176, 36, 18, TEXTURES);
		this.buttonList.add(this.startReaction);
		this.buttonList.add(this.autoMode);
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String tileName = this.tileentity.getDisplayName().getFormattedText();
		this.mc.getTextureManager().bindTexture(TEXTURES);
		
		if(tileentity.isMixing()) {
			this.drawTexturedModalRect(137, 42, 194, 0, 8, 8);
		} else {
			this.drawTexturedModalRect(137, 42, 194, 8, 8, 8);
		}
		
		if(tileentity.isAuto()) {
			this.drawTexturedModalRect(137, 8, 194, 0, 8, 8);
		} else {
			this.drawTexturedModalRect(137, 8, 194, 8, 8, 8);
		}
		
		//this.drawTexturedModalRect(68, 19, 0, 166, 19, 44);
		int progress = (int) ((int) 19-(19*((float) this.tileentity.timer/this.tileentity.maxTime)));
		int progress2 = (int) ((int) 44-(44*((float) this.tileentity.timer/this.tileentity.maxTime)));
		
		//this.drawTexturedModalRect(68, 19, 0, 166, 19, 44);
		
		this.drawTexturedModalRect(52, 32, 0, 166, 5, 44-progress2);
		this.drawTexturedModalRect(62, 32, 5, 166, 28, 19-progress);
		
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
	
	
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button == this.startReaction) {
			if(this.tileentity.canStartManually()) {
				Main.NETWORK.sendToServer(new ServerStartMixPacket(this.tileentity.getPos().getX(), this.tileentity.getPos().getY(), this.tileentity.getPos().getZ()));
				//System.out.println("te");
				//this.tileentity.startMixing();
			}
			
		}
		if(button == this.autoMode) {
			this.tileentity.toggleAuto();
			if(this.tileentity.canStart()) {
				Main.NETWORK.sendToServer(new ServerStartMixPacket(this.tileentity.getPos().getX(), this.tileentity.getPos().getY(), this.tileentity.getPos().getZ()));
				//this.tileentity.startMixing();
			}
		}
	}

}
