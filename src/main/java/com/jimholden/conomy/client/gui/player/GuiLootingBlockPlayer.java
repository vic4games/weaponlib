package com.jimholden.conomy.client.gui.player;

import java.util.List;

import javax.rmi.CORBA.Tie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityLootingBlock;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerLootingBlock;
import com.jimholden.conomy.sound.LootingSound;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.AddLootPacket;
import com.jimholden.conomy.util.packets.LootUpdateClientPacket;
import com.jimholden.conomy.util.packets.MessageGetCredits;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
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
import net.minecraftforge.items.SlotItemHandler;


public class GuiLootingBlockPlayer extends GuiContainer {
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guilootingplayer.png");
	FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	private final InventoryPlayer player;
	private EntityPlayer p = null;
	private EntityPlayerMP pMP = null;
	public final TileEntityLootingBlock tileentity;
	ICredit iC = null;
	private int displayAmountACC;
	private int displayAmountDEV;
	private int bal;
	public GuiButton transferBank;
	public GuiButton transferDevice;
	public GuiTextField transferAmount;
	private float conversionFactor = 0.235F;
	private boolean canGrabItems = false;
	public float tick = 0.0F;
	public float maxTicking = 50F;
	boolean startAnimTick = false;
	private float animationTick = 0.0F;
	private ArrayList<Integer> animList = new ArrayList();
	private boolean isOnCooldown = false;
	private int cooldownTimer = 0;
	private int cooldownTime = 300;
	
	
	
	public GuiLootingBlockPlayer(EntityPlayer player, TileEntityLootingBlock tileentity, MinecraftServer server) {
		super(new ContainerLootingBlock(player.inventory, tileentity, false));
		this.player = player.inventory; 
		this.tileentity = tileentity;
		System.out.println("OT: " + tileentity.openTime);
		this.p = player;
		this.maxTicking = tileentity.lootingTime;
	}
	
	@Override
	public void initGui() {
		super.initGui();
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
	//	String tileName = this.tileentity.getDisplayName().getFormattedText();
		//this.fontRenderer.drawString(tileName, (this.xSize - this.fontRenderer.getStringWidth(tileName)) - 118, 6, 4210752);
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
		GlStateManager.enableBlend();
		this.mc.getTextureManager().bindTexture(TEXTURES);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		if(!canGrabItems) {
			int count = 0;
			for(int y = 0; y < 2; y++)
			{
				for(int x = 0; x < 7; x++) {
					count++;
					int slot = count-1;
					if(tileentity.lootboxInventory.getStackInSlot(slot).isEmpty() && !tileentity.isOnCooldown()) {
						
						this.drawTexturedModalRect(this.guiLeft+21 + x*18, this.guiTop + 20 + y*18, 0, 210, 18, 18);
					} else {
						this.drawTexturedModalRect(this.guiLeft+21 + x*18, this.guiTop + 20 + y*18, 162, 210, 18, 18);
					}
					
				}
			}
		} else {
			if(startAnimTick) {
				int count = 0;
				for(int y = 0; y < 2; y++)
				{
					for(int x = 0; x < 7; x++) {
						count++;
						int slot = count-1;
						int cycle = (int) animationTick;
						this.drawTexturedModalRect(this.guiLeft+21 + x*18, this.guiTop + 20 + y*18, 18*cycle, 210, 18, 18);
					}
				}
				
			} else {
				int count = 0;
				for(int y = 0; y < 2; y++)
				{
					for(int x = 0; x < 7; x++) {
						count++;
						int slot = count-1;
						
						int cycle = (int) animationTick;
						this.drawTexturedModalRect(this.guiLeft+21 + x*18, this.guiTop + 20 + y*18, 162, 210, 18, 18);
					}
				}
				
			}
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		
		
		int progress = (int) (160.0*(tick/maxTicking));
		this.drawTexturedModalRect(this.guiLeft + 22, this.guiTop + 61, 0, 202, progress, 8);
		
		/*
		if(tileentity.isOnCooldown())
		{
			this.drawTexturedModalRect(this.guiLeft + 100, this.guiTop + 40, 0, 228, 4, 4);
		} else {
			this.drawTexturedModalRect(this.guiLeft + 100, this.guiTop + 40, 4, 228, 8, 4);
		}
		*/
		
	}
	


	
	
	@Override
	public void updateScreen() {
		//System.out.println(tileentity.handlerEmpty.getStackInSlot(0));
		//System.out.println(tileentity.lootingTime);
		
		
		
		/*
		if(tick == 0) {
			//System.out.println("yo");
			//this.mc.getSoundHandler().playSound(new LootingSound(this));
		}
		 */
		 

		
		if(startAnimTick) {
			animationTick += 1;
			if(animationTick > 9) {
				startAnimTick = false;
			}
			for(int x = 0; x < tileentity.lootboxInventory.getSlots(); x++) {
				//if(!tileentity.handlerEmpty.getStackInSlot(x).isEmpty()) {
					animList.add(x);
			//	}
				
			}
			/*
			for(int x = 0; x < tileentity.forcedAnimList.size(); ++x) {
				
				animList.add(tileentity.forcedAnimList.get(x));
			}*/
		}
		
		if(tick < maxTicking)
		{
			
			
			tick += 1;
		}
		if(tick < maxTicking*0.8 && tick % 2 == 0) {
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.LOOTSEARCH, 1.0F));
		}
		
		if(tick > (maxTicking*0.8) && !canGrabItems) {
			if(!tileentity.isOnCooldown()) startAnimTick = true;
			canGrabItems = true;
			Main.NETWORK.sendToServer(new AddLootPacket(tileentity.getPos().getX(), tileentity.getPos().getY(), tileentity.getPos().getZ()));
		}
		
		super.updateScreen();
	}
	
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		//System.out.println("hi!");
		
		super.actionPerformed(button);
	}

}
