package com.jimholden.conomy.client.gui.player;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Supplier;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.client.gui.engine.buttons.Alignment;
import com.jimholden.conomy.client.gui.engine.buttons.CheckFillButton;
import com.jimholden.conomy.client.gui.engine.buttons.ConomyButton;
import com.jimholden.conomy.client.gui.engine.buttons.SimpleButton;
import com.jimholden.conomy.client.gui.engine.buttons.Slider;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.IconDisplay;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.client.gui.engine.elements.AdvancedScroll;
import com.jimholden.conomy.client.gui.engine.elements.ScrollBlock;
import com.jimholden.conomy.client.gui.engine.elements.scrollblocks.TradeScrollBlock;
import com.jimholden.conomy.client.gui.engine.elements.scrollblocks.TransactionScrollBlock;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.ContainerBriefcase;
import com.jimholden.conomy.containers.ContainerLootBody;
import com.jimholden.conomy.containers.ContainerTrader;
import com.jimholden.conomy.economy.FinancialDummy;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.economy.data.Trade;
import com.jimholden.conomy.economy.record.Transaction;
import com.jimholden.conomy.economy.record.Transaction.Type;
import com.jimholden.conomy.entity.EntityTrader;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.ItemChemicalBriefcase;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.MessageGetCredits;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;
import com.jimholden.conomy.util.packets.economy.FinancialServerPacket;
import com.jimholden.conomy.util.packets.economy.TraderServerPacket;

import net.java.games.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import scala.swing.event.Key;
import scala.tools.nsc.doc.model.ImplicitMemberShadowing;

public class GuiTrader extends GuiContainer {

	public EntityPlayer player;
	public EntityTrader trader;

	public boolean slotToggle = false;

	public AdvancedScroll advScroll;

	// inertia
	public double scrollVelocity = 0.0;
	public double scrollFriction = 0.0;
	public double prevScrollPosition = 0;
	public double scrollPosition = 0.0;
	
	public boolean sortUp = true;
	
	
	// scroll
	public ArrayList<ScrollBlock> tsb = new ArrayList<>(); 
	public Supplier<ArrayList<ScrollBlock>> tsbSupplier = () -> tsb;
	
	// selling
	public ItemStackHandler stackHandler = new ItemStackHandler(1);
	
	
	public boolean inBuyMenu = true;

	public GuiTrader(EntityPlayer player, EntityTrader trader, MinecraftServer server) {
		super(new ContainerTrader(player.inventory, trader));

		this.player = player;
		this.trader = trader;

		this.xSize = 225;
		this.ySize = 150;
		
		
		System.out.println(player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(0));
		System.out.println(player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(1));
		System.out.println(player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(2));
		System.out.println(player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(3));
		System.out.println(player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(4));
		
	}

	public double getInterpolatedScrollPosition() {
		return this.prevScrollPosition + (this.scrollPosition - this.prevScrollPosition) * Minecraft.getMinecraft().getRenderPartialTicks();
	}

	@Override
	public void initGui() {
		super.initGui();
		Main.NETWORK.sendToServer(new TraderServerPacket(player.getEntityId(), trader.getEntityId(), new NBTTagCompound()));
		System.out.println("original left: " + guiLeft);
		guiLeft -= 67;

		// Sort by price button
		SimpleButton sortPrice = new SimpleButton(1, guiLeft + 185, guiTop + 12.5, new IconDisplay(
				IconSheet.getIcon(GuiATM.BUTTON_TEXTURES, 256, 64, 4), Alignment.CENTER, 0xffffff, 0.075f*(this.sortUp ? 1 : -1)), 5);
		sortPrice.setButtonColor(new Color(0xffa502));

	

		// buy & sell buttons
		CheckFillButton buyButton = new CheckFillButton(2, "Buy", guiLeft + 120, guiTop + 12.5, IInfoDisplay.NONE, 8);
		buyButton.setColor(new Color(0x5f27cd));
		CheckFillButton sellButton = new CheckFillButton(3, "Sell", guiLeft + 150, guiTop + 12.5, IInfoDisplay.NONE, 8);
		sellButton.setColor(new Color(0x5f27cd));
		buyButton.setChecked(true);

		// crypto/bank buttons
		// buy & sell buttons
		CheckFillButton cryptoButton = new CheckFillButton(4, "Crypto", guiLeft + 75, guiTop + 110, IInfoDisplay.NONE,
				7);
		cryptoButton.setColor(new Color(0x00d2d3));
		CheckFillButton bankButton = new CheckFillButton(5, "Bank", guiLeft + 25, guiTop + 110, IInfoDisplay.NONE, 7);
		bankButton.setColor(new Color(0x00d2d3));
		cryptoButton.setChecked(true);
		
		
		SimpleButton sellItem = new SimpleButton(6, guiLeft + 162, guiTop + 115, new StringElement("Sell Item", Alignment.CENTER, 0xffffff, 0.45f),
				7);
	
		sellItem.setButtonColor(new Color(0x00d2d3));
		
		SimpleButton clear = new SimpleButton(7, guiLeft + 162, guiTop + 130, new StringElement("Clear", Alignment.CENTER, 0xffffff, 0.45f),
				5);
		clear.visible = false;
		sellItem.visible = false;
	
		clear.setButtonColor(new Color(0x2f3640));
		/*CheckFillButton bankButton = new CheckFillButton(5, "Bank", guiLeft + 25, guiTop + 110, IInfoDisplay.NONE, 7);
		bankButton.setColor(new Color(0x00d2d3));
		cryptoButton.setChecked(true);
*/

		/*
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 100, new ItemStack(ModItems.ADD_MEDICINE)), 20));
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 99, new ItemStack(ModItems.ALPHAFLJACKET)), 20));
		
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 98, new ItemStack(ModItems.COMTACS)), 20));
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 43, new ItemStack(ModItems.ADD_MEDICINE)), 20));
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 34, new ItemStack(ModItems.ALPHAFLJACKET)), 20));
		
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 67, new ItemStack(ModItems.COMTACS)), 20));
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 32, new ItemStack(ModItems.ADD_MEDICINE)), 20));
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 13, new ItemStack(ModItems.ALPHAFLJACKET)), 20));
		
		tsb.add(new TradeScrollBlock(new Trade(20, 15, 75, new ItemStack(ModItems.COMTACS)), 20));
		
		*/
		
		
		
		
		GuiPage debug = new GuiPage(100, 250);
		debug.x = this.guiLeft;
		debug.y = this.guiTop;
		
		this.advScroll = new AdvancedScroll(tsbSupplier, 100, 25, 125, 125, 1, debug);
		
		buttonList.add(sortPrice);
		
		buttonList.add(buyButton);
		buttonList.add(sellButton);
		buttonList.add(cryptoButton);
		buttonList.add(bankButton);
		buttonList.add(sellItem);
		buttonList.add(clear);
		

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		
		
		/*
		Color scrollColor = new Color(0x2f3640).darker().darker();
		
		
		double sX = 100.5;
		double sY = 25.0;
		
		double spacing = 0;
		
		GUItil.renderRectangle(scrollColor, 1.0, sX, sY, 99, 124.5);
		
		GUItil.performScissor(sX, sY, 99*2, 124.5*2);
		GlStateManager.enableTexture2D();
		for(TradeScrollBlock block : this.tsb) {
			block.renderScroll(sX, sY+spacing, 99);
			spacing += 20;
		}
		GUItil.endScissor();
		*/
		
		
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		if(this.inBuyMenu) this.advScroll.render();
		
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		
		// trader
		
		
		
	}
	
	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GlStateManager.color(1.0F, 1.0F, 1.0F);
		// this.mc.getTextureManager().bindTexture(TEXTURES);
		Color col = Color.gray;

		int corner = 2;

		GlStateManager.enableBlend();
		//GUItil.renderRectangle(Color.black.darker().darker(), 0.3f, this.guiLeft, this.guiTop, xSize, ySize);

		// main
		GUItil.renderRoundedRectangle(col, 1.0f, this.guiLeft, this.guiTop, this.guiLeft + this.xSize,
				this.guiTop + this.ySize, corner, 0.5);
		GUItil.drawScaledString(ClientProxy.newFontRenderer, "Information", guiLeft, guiTop, 0xffffff, 1);

		GUItil.renderRoundedRectangle(col, 1.0f, this.guiLeft + 100, this.guiTop, this.guiLeft + this.xSize,
				this.guiTop + 25, corner, 0.5);

		GUItil.renderRoundedRectangle(col, 1.0f, this.guiLeft + 100, this.guiTop, this.guiLeft + this.xSize,
				this.guiTop + this.ySize, corner, 0.5);

		// payment area
		GlStateManager.enableBlend();
		GUItil.renderRectangle(Color.black, 0.6f, this.guiLeft + 0.2, this.guiTop + 85 + 0.2, 100 - 0.2, 65 - 0.2);

		GlStateManager.enableTexture2D();
		GUItil.drawScaledString(ClientProxy.newFontRenderer, "Payment", guiLeft, guiTop + 85, 0xffffff, 1);

		GUItil.renderRoundedRectangle(col, 1.0f, this.guiLeft, this.guiTop + 85, this.guiLeft + 100, this.guiTop + 150,
				corner, 0.5);

		GlStateManager.enableBlend();
		GUItil.renderRectangle(Color.black, 0.6f, this.guiLeft, this.guiTop + 155, 173, 20);
		GUItil.renderRoundedRectangle(col, 1.0f, this.guiLeft, this.guiTop + 155, this.guiLeft + 173, this.guiTop + 175,
				corner, 0.5);

		GlStateManager.enableBlend();
		GUItil.renderRectangle(new Color(0x00d2d3), Math.abs(Math.sin(0.1*Minecraft.getMinecraft().player.ticksExisted))-0.5, this.guiLeft + 42, this.guiTop + 122, 16, 16);
		
		GUItil.renderRoundedRectangle(new Color(0x00d2d3), 1.0f, this.guiLeft + 41, this.guiTop + 121, this.guiLeft + 42 + 17,
				this.guiTop + 122 + 17, corner, 1);

		// this.drawTexturedModalRect(this.guiLeft, this.guiTop-25, 0, 0, this.xSize,
		// this.ySize);
		
		
		// render trader into the GUI
		GUItil.renderCircle(new Color(0x2f3640).darker().darker().darker(), 0.2, this.guiLeft+20, this.guiTop+25, 16);
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GuiInventory.drawEntityOnScreen(this.guiLeft+21, this.guiTop+37, (int) 10.0, -100, -50, this.trader);
		GlStateManager.disableDepth();
		
		// render the trader "lore" into GUI
		GUItil.renderRoundedRectangle(new Color(0x2f3640).darker(), 1.0, this.guiLeft+5, this.guiTop+45, this.guiLeft+95, this.guiTop+75, 5, 15);
	
		
		
		if(!inBuyMenu) {
			
			
			
			GUItil.renderRectangle(new Color(0x2f3640).darker().darker(), 1, this.guiLeft+100.5, this.guiTop+25, 124, 124);
			GUItil.renderCircle(new Color(0x2f3640).darker().darker().darker(), 1.0, this.guiLeft+162.5, this.guiTop+65, 16);
			ContainerTrader ct = (ContainerTrader) Minecraft.getMinecraft().player.openContainer;
			if(!ct.stacker.getStackInSlot(0).isEmpty()) {
				
				
			//	System.out.println(ct.econSlot.getStack());
				
				RenderItem item = Minecraft.getMinecraft().getRenderItem();
				GlStateManager.enableTexture2D();
				RenderHelper.enableGUIStandardItemLighting();
				item.renderItemIntoGUI(ct.stacker.getStackInSlot(0), this.guiLeft+155, this.guiTop+58);
				RenderHelper.disableStandardItemLighting();
				int count = ct.stacker.getStackInSlot(0).getCount();
				if(count > 0) {
					GUItil.drawScaledString(ClientProxy.newFontRenderer, "x" + count, this.guiLeft+180, this.guiTop+61, 0xffffff, 1.5f);
					
				}
				GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, Interchange.formatUSD(1.50*count), this.guiLeft+162, this.guiTop+92, 0x4cd137, 1.0f);
				GUItil.renderMRC(this.guiLeft+162, this.guiTop+100, 0xfbc531, (1.50*count)/Interchange.getMRCtoUSDRatio(), 0.6f);
				
			}
			
			
			
		}
		
		
		GlStateManager.enableTexture2D();
		net.minecraftforge.common.MinecraftForge.EVENT_BUS
				.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
	}

	public ItemStack getSellingItem() {
		return this.stackHandler.getStackInSlot(0);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(this.inBuyMenu) {
			this.advScroll.mouseClicked(mouseX, mouseY, mouseButton);
			
			boolean inBoxedArea = mouseX > this.guiLeft+100.5 && mouseX < this.guiLeft+100.5+124 && mouseY > this.guiTop+25 && mouseY < this.guiTop+25+124;
			if(inBoxedArea && !this.player.inventory.getItemStack().isEmpty()) {
				
				inBuyMenu = false;
				this.buttonList.get(5).visible = true;
				this.buttonList.get(6).visible = true;
				this.buttonList.get(0).visible = false;
				/*
				if(getSellingItem().isEmpty()) {
					this.stackHandler.setStackInSlot(0, this.player.inventory.getItemStack());
				//	toSellCount += this.player.inventory.getItemStack().getCount();
					this.player.inventory.setItemStack(ItemStack.EMPTY);
				} else {
					if(ItemStack.areItemsEqual(getSellingItem(), this.player.inventory.getItemStack())) {
						getSellingItem().grow(this.player.inventory.getItemStack().getCount());
						this.player.inventory.setItemStack(ItemStack.EMPTY);
					}
				}*/
			}
		} else {
			
			
			
		}
		
		
		
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

		ConomyButton cb = (ConomyButton) button;
		
		if(button.id == 1) {
			
			if(sortUp) {
				this.tsb.sort((a, b) -> ((TradeScrollBlock) a).compareTo((TradeScrollBlock) b));
			} else {
				this.tsb.sort((a, b) -> ((TradeScrollBlock) b).compareTo((TradeScrollBlock) a));
				
			}
			sortUp = !sortUp;
		}
		
		if (button.id == 2) {
			CheckFillButton original = (CheckFillButton) button;
			CheckFillButton opposing = (CheckFillButton) buttonList.get(2);

			original.setChecked(true);
			opposing.setChecked(false);
			
			if(!this.inBuyMenu) {
				inBuyMenu = true;
				this.buttonList.get(5).visible = false;
				this.buttonList.get(6).visible = false;
				this.buttonList.get(0).visible = true;
			}

		}

		if (button.id == 3) {
			CheckFillButton original = (CheckFillButton) button;
			CheckFillButton opposing = (CheckFillButton) buttonList.get(1);

			original.setChecked(true);
			opposing.setChecked(false);

			System.out.println("sell");
			if(this.inBuyMenu) {
				
				inBuyMenu = false;
				this.buttonList.get(5).visible = true;
				this.buttonList.get(6).visible = true;
				this.buttonList.get(0).visible = false;
			}
			
		}

		if (button.id == 4) {
			CheckFillButton original = (CheckFillButton) button;
			CheckFillButton opposing = (CheckFillButton) buttonList.get(4);

			original.setChecked(true);
			opposing.setChecked(false);

		}

		if (button.id == 5) {
			CheckFillButton original = (CheckFillButton) button;
			CheckFillButton opposing = (CheckFillButton) buttonList.get(3);

			original.setChecked(true);
			opposing.setChecked(false);

		}
		
		if(button.id == 7) {
			// clear
			/*
			this. = ItemStack.EMPTY;
			this.toSellCount = 0;
			*/
		}
		if(button.id == 6) {
			// sell
			System.out.println("sell");
		}

	}

}
