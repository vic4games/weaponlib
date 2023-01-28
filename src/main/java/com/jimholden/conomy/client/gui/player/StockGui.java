package com.jimholden.conomy.client.gui.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ibm.icu.text.DecimalFormat;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.clans.threads.ConcurrentExecutionManager;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.stocks.Stock;
import com.jimholden.conomy.stocks.StockCache;
import com.jimholden.conomy.stocks.StockNotification;
import com.jimholden.conomy.stocks.StockToast;
import com.jimholden.conomy.stocks.StockTool;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.AddLootPacket;
import com.jimholden.conomy.util.packets.LedgerTransfer;
import com.jimholden.conomy.util.packets.LedgerTransferThree;
import com.jimholden.conomy.util.packets.LedgerTransferTwo;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.RequestServerStockData;
import com.jimholden.conomy.util.packets.stock.RegisterStockPacket;
import com.jimholden.conomy.util.packets.stock.RemoveStockPacket;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.advancements.GuiAdvancement;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.swing.event.KeyTyped;


public class StockGui extends GuiScreen {
	
	Minecraft minecraft = Minecraft.getMinecraft();
	Stock currentStock = null;
	EntityPlayer p;
	ScaledResolution sr = new ScaledResolution(minecraft);
	double screenHeight = sr.getScaledHeight_double();
	double screenWidth = sr.getScaledWidth_double();
	public GuiButton buyStock;
	public GuiButton sellStock;
	public GuiButton getStock;
	public GuiButton closePortion;
	public StockToast toast;
	public GuiButton confirm;
	public GuiButton cancel;
	
	public GuiTextField transferAmount;
	public GuiTextField investAmount;
	String selectedkey;
	int shareRequest;
	String lockedKey;
	private int displayAmount;
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guistock.png");
	private int reqMoney = 0;
	public int ticker;
	
	int pageSelected = 1;
	int totalPages = 1;
	int highlightedOption = 0;
	int maxOptions = 1;
	private boolean confirmationScreen = false;
	private int guiTop;
	private int guiLeft;
	private int ySize = 124;
	private int xSize = 131;
	private int offsetFromLeft;
	private int offsetFromTop;
	public boolean isOrdering;
	public boolean isBuyOrder;
	private ICredit balance;
	public boolean isClosing;
	
	
	public double roundToTwoDec(double toRound) {
		return Math.round(toRound*100)/100;
	}
	
	public StockGui(EntityPlayer p) {
		this.p = p;
		this.guiLeft = (this.width - this.xSize) / 2;
	    this.guiTop = (this.height - this.ySize) / 2;
	    this.offsetFromLeft = guiLeft + 225;
	    this.offsetFromTop = guiTop + 120;
	    
	    
	    //this.offsetFromLeft = (this.width - 256)/2;
		//this.offsetFromTop = this.height - (this.height/4);
	}
	

	public void drawMenuBackground(int tint)
    {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURES);
        
        //this.drawTexturedModalRect(this.guiLeft + 250, this.guiTop + 120, 0, 0, this.xSize, this.ySize);
        GL11.glPushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.8f);
        this.drawTexturedModalRect(this.offsetFromLeft, this.offsetFromTop, 0, 0, this.xSize, this.ySize);
        GL11.glPopMatrix();
        
        if(this.currentStock != null) {
        	if(this.currentStock.isOpen) {
        		//this.drawTexturedModalRect(this.offsetFromLeft + 116, this.offsetFromTop + 38, 0, 0, 100, 100);
        		this.drawTexturedModalRect(this.offsetFromLeft + 116, this.offsetFromTop + 38, 131, 54, 10, 10);
    			//this.drawTexturedModalRect(this.offsetFromLeft + 116, this.offsetFromTop + 38, 131, 54, 10, 10);
    		} else {
    			this.drawTexturedModalRect(this.offsetFromLeft + 116, this.offsetFromTop + 38, 131, 64, 10, 10);
    		}
        }
        
        
        /*
        if(!confirmationScreen)
        	this.drawTexturedModalRect(this.guiLeft, this.guiTop+111, 0, 111, this.xSize, 39);
        else
        	this.drawTexturedModalRect(this.guiLeft, this.guiTop+111, 0, 149, this.xSize, 39);
        	*/
        //this.drawTexturedModalRect(this.guiLeft + 44, this.guiTop + 36, 176, 14, 1, 16);
		
        

    }
	
	public void drawTransactionScreen(int tint)
    {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURES);
        //this.drawTexturedModalRect(this.guiLeft, this.guiTop+105, 0, 112, this.xSize, this.ySize);
        //this.drawTexturedModalRect(this.guiLeft + 44, this.guiTop+36, 176, 14, 1, 16);
        
    }

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
	}
	
	@Override // 177, 0 | 187, 11
	public void initGui() {
		super.initGui();
		// 59, 16
		
		//this.upButton = new GuiButtonImage(3, this.guiLeft+1, this.guiTop + 45, 11, 11, 176, 0, 11, TEXTURES);
		//this.downButton = new GuiButtonImage(4, this.guiLeft+1, this.guiTop + 55, 11, 11, 186, 0, 11, TEXTURES);
		
		/*
		this.transfer = new GuiButtonImage(4, this.guiLeft+115, this.guiTop + 130, 15, 15, 197, 0, 15, TEXTURES);
		this.confirm = new GuiButtonImage(5, this.guiLeft + 5, this.guiTop + 134, 11, 11, 176, 149, 11, TEXTURES);
		this.cancel = new GuiButtonImage(7, this.guiLeft + 160, this.guiTop + 134, 11, 11, 186, 149, 11, TEXTURES);
		this.leftPage = new GuiButtonExt(1, this.guiLeft + 12, this.guiTop + 87, 20, 20,"<");
		this.rightPage = new GuiButtonExt(2, this.guiLeft + 143, this.guiTop + 87, 20, 20,">");
		*/
		
		this.buyStock = new GuiButtonImage(1, offsetFromLeft + 5, offsetFromTop + 101, 58, 7, 131, 0, 7, TEXTURES);
		this.sellStock = new GuiButtonImage(2, offsetFromLeft + 67, offsetFromTop + 101, 58, 7, 189, 0, 7, TEXTURES);
		this.getStock = new GuiButtonImage(3, offsetFromLeft + 59, offsetFromTop + 16, 10, 10, 151, 34, 10, TEXTURES);
		this.closePortion = new GuiButtonImage(4, offsetFromLeft + 37, offsetFromTop + 101, 58, 10, 131, 14, 10, TEXTURES);
		this.confirm = new GuiButtonImage(5, offsetFromLeft + 5, offsetFromTop + 101, 10, 10, 161, 34, 10, TEXTURES);
		this.cancel = new GuiButtonImage(6, offsetFromLeft + 116, offsetFromTop + 101, 10, 10, 171, 34, 10, TEXTURES);
		//116, 101
		FontRenderer fontrenderer2 = minecraft.fontRenderer;
		transferAmount = new GuiTextField(6, fontrenderer2, this.offsetFromLeft + 72, this.offsetFromTop + 18, 52, 13);
		this.investAmount = new GuiTextField(7, fontrenderer2, offsetFromLeft + 40, offsetFromTop + 102, 52, 13);
		this.buttonList.add(buyStock);
		this.buttonList.add(sellStock);
		this.buttonList.add(getStock);
		this.buttonList.add(closePortion);
		this.buttonList.add(confirm);
		this.buttonList.add(cancel);
		confirm.visible = false;
		cancel.visible = false;
		closePortion.visible = false;
		investAmount.setVisible(false);
		/*
		this.buttonList.add(leftPage);
		this.buttonList.add(rightPage);
		this.buttonList.add(upButton);
		this.buttonList.add(downButton);
		this.buttonList.add(transfer);
		this.buttonList.add(confirm);
		this.buttonList.add(cancel);
		this.cancel.visible = false;
		this.confirm.visible = false;
		*/
		
		
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
	
	public void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, int color, float scale) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		drawString(fontRendererIn, text, (int) (x/scale), (int) (y/scale), color);
		GL11.glPopMatrix();
	}
	
	public void displayNotification(String title, String subText) {
		
		
		ITextComponent TITLE = new TextComponentString(title);
	    ITextComponent DESCRIPTION = new TextComponentString(subText);
	    this.toast = new StockToast(TutorialToast.Icons.WOODEN_PLANKS, TITLE, DESCRIPTION, true, 0.05F);
		mc.getToastGui().add(toast);
		
		
		//StockNotification newStock = new StockNotification("fuck", "shit", this.mc);
		
	}
	
	
	
	@Override //37, 101
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawMenuBackground(3);
		transferAmount.drawTextBox();
		transferAmount.setMaxStringLength(10);
		
		investAmount.drawTextBox();
		investAmount.setMaxStringLength(10);
		
		ticker += 1;
		if(ticker > 19) {
			ticker = 0;
			if(this.currentStock != null) {
				Runnable runnable = () -> {
					try {
						this.currentStock = StockTool.getStockData(this.currentStock.symbol);
					} catch (Exception e) {}
				    try {
						Thread.currentThread().join();
					} catch (InterruptedException e) {
						//this.currentStock = null;
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				};
			}
		}
		
		FontRenderer fontrenderer = mc.fontRenderer;
		if(this.currentStock != null) {
			if(StockCache.hasStock(this.currentStock.symbol)) {
				closePortion.visible = true;
			} else {
				closePortion.visible = false;
			}
			
		}
		
		this.balance = p.getCapability(CreditProvider.CREDIT_CAP, null);
		
		//5, 27
		drawScaledString(fontrenderer, TextFormatting.GREEN + "$" + this.balance.getBalance(), offsetFromLeft + 5, offsetFromTop + 27, 0xe0e0e0, 0.8F);
		
		if(this.currentStock != null) {
			if(!this.currentStock.isOpen || StockCache.hasStock(this.currentStock.symbol) || isOrdering || isClosing) {
				buyStock.visible = false;
				sellStock.visible = false;
			} else {
				buyStock.visible = true;
				sellStock.visible = true;
			}
		} else {
			buyStock.visible = false;
			sellStock.visible = false;
		}
		
		
		
		if(this.currentStock != null) {
			
			String percentChange = null;
			float scale = 0.8F;
			double roundOff = (double) Math.round(Math.abs(this.currentStock.percentChange) * 100) / 100;
			if(this.currentStock.percentChange > 0) {
				percentChange = TextFormatting.GREEN + "+" + roundOff;
			} else {
				percentChange = TextFormatting.RED + "-" + roundOff;
			}// 116, 38
			if(this.currentStock.companyName.length() > 16) {
				scale = 0.7F;
			}
			
			
			drawScaledString(fontrenderer, this.currentStock.companyName + " " + percentChange, offsetFromLeft + 5, offsetFromTop + 42, 0xe0e0e0, 0.8F);
			drawScaledString(fontrenderer, this.currentStock.exchange, offsetFromLeft + 5, offsetFromTop + 50, 0xe0e0e0, 0.8F);
			drawScaledString(fontrenderer, TextFormatting.YELLOW + this.currentStock.currency + this.currentStock.regularMarketPrice, offsetFromLeft + 5, offsetFromTop + 58, 0xe0e0e0, 0.8F);
			
			if(StockCache.hasStock(this.currentStock.symbol)) {
				int shares = StockCache.getStockShares(this.currentStock.symbol);
				double initialPrice = StockCache.getInitialStockPrice(this.currentStock.symbol);
				String percentChangeString = StockTool.getFormatStockPercent(initialPrice, this.currentStock.regularMarketPrice, StockCache.getStockType(this.currentStock.symbol));
				/*
				double percentDifference = Math.round(((this.currentStock.regularMarketPrice - initialPrice)/initialPrice)*10000)/100;
				String percentChangeString = null;
				if(percentDifference > 0) {
					percentChangeString = TextFormatting.GREEN + "+%" + Math.abs(percentDifference);
				} else {
					percentChangeString = TextFormatting.RED + "-%" + Math.abs(percentDifference);
				}
				*/
				drawScaledString(fontrenderer, TextFormatting.RED + "" + shares + " shares" + TextFormatting.AQUA + " @ " + TextFormatting.YELLOW + this.currentStock.currency + roundToTwoDec(initialPrice) + "/share", offsetFromLeft + 5, offsetFromTop + 75, 0xe0e0e0, 0.8F);
				drawScaledString(fontrenderer, percentChangeString, offsetFromLeft + 5, offsetFromTop + 83, 0xe0e0e0, 0.8F);
			}
			
			
			if(isOrdering && this.currentStock != null) {
				String buyType;
				if(isBuyOrder) {
					buyType = "BUY";
				} else {
					buyType = "SELL";
				}
				drawScaledString(fontrenderer, TextFormatting.RED + "" + shareRequest + " shares (" + buyType + ")", offsetFromLeft + 40, offsetFromTop + 80, 0xe0e0e0, 0.8F);
				drawScaledString(fontrenderer, TextFormatting.YELLOW + "Total: " + roundToTwoDec(shareRequest*this.currentStock.regularMarketPrice), offsetFromLeft + 40, offsetFromTop + 88, 0xe0e0e0, 0.8F);
			}
			//this.drawTexturedModalRect(this.offsetFromLeft + 116, this.offsetFromTop + 38, 0, 0, 100, 100);
			/*
			if(this.currentStock.isOpen) {
				this.drawTexturedModalRect(this.offsetFromLeft + 116, this.offsetFromTop + 38, 131, 54, 10, 10);
			} else {
				this.drawTexturedModalRect(this.offsetFromLeft + 116, this.offsetFromTop + 38, 131, 64, 10, 10);
			}
			*/
			
			//drawString(fontrenderer, this.currentStock.companyName + " " + percentChange, offsetFromLeft + 5, offsetFromTop + 42, 0xe0e0e0);
			//drawString(fontrenderer, this.currentStock.exchange, offsetFromLeft + 5, offsetFromTop + 50, 0xe0e0e0);
		}
		
	
		
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		transferAmount.mouseClicked(mouseX, mouseY, mouseButton);
		investAmount.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		Character c = typedChar;
		
		transferAmount.textboxKeyTyped(typedChar, keyCode);
		if(!c.isAlphabetic(c)) {
			investAmount.textboxKeyTyped(typedChar, keyCode);
		}
		
		if(isOrdering) {
			try {
				shareRequest = Integer.parseInt(investAmount.getText());
			} catch (Exception e) {
				shareRequest = 0;
			}
			
		}
		
		// test
		
		String symbolToSearch = transferAmount.getText().toUpperCase();
		Runnable runnable = () -> {
			try {
				this.currentStock = StockTool.getStockData(symbolToSearch);
			} catch (Exception e) {}
		    try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				//this.currentStock = null;
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		};
		
	
		Thread thread = new Thread(runnable);
		thread.start();
		
		if(this.currentStock != null) {
			Main.NETWORK.sendToServer(new RequestServerStockData(this.currentStock.symbol));
		}
		
		//System.out.println("")
		
		//if(this.currentStock != null) {
		//	System.out.println("ShareCount: " + StockCache.getStockShares(this.currentStock.symbol));
		//}
		
		
		//
		
		
		if (transferAmount.isFocused())
			return;
		if (investAmount.isFocused()) return;
		super.keyTyped(typedChar, keyCode);
	}

	
	
	@Override
	public void updateScreen() {
		transferAmount.updateCursorCounter();
		investAmount.updateCursorCounter();
		super.updateScreen();
	}
	
	
	
	
	
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button == this.buyStock) {
			isOrdering = true;
			isBuyOrder = true;
			transferAmount.setVisible(false);
			investAmount.setVisible(true);
			confirm.visible = true;
			cancel.visible = true;
		}
		if(button == this.sellStock) {
			isOrdering = true;
			isBuyOrder = false;
			transferAmount.setVisible(false);
			investAmount.setVisible(true);
			confirm.visible = true;
			cancel.visible = true;
		}
		if(button == this.cancel) {
			transferAmount.setVisible(true);
			investAmount.setVisible(false);
			isOrdering = false;
			isClosing = false;
			confirm.visible = false;
			cancel.visible = false;
		}
		if(button == this.confirm) {
			if(!isClosing)
			{
				if(investAmount.getText() == "") return;
				double totalCost = this.currentStock.regularMarketPrice * Double.parseDouble(investAmount.getText());
				int requestedShares = Integer.parseInt(investAmount.getText());
				if(totalCost > this.balance.getBalance()) {
					investAmount.setText("");
					return;
				}
				System.out.println(requestedShares + " | " + this.currentStock.regularMarketPrice);
				Main.NETWORK.sendToServer(new MessageUpdateCredits(this.balance.getBalance() - ((int) totalCost)));
				Main.NETWORK.sendToServer(new RegisterStockPacket(this.currentStock.symbol, requestedShares, this.currentStock.regularMarketPrice, this.isBuyOrder));
				Main.NETWORK.sendToServer(new RequestServerStockData(this.currentStock.symbol));
				transferAmount.setVisible(true);
				investAmount.setVisible(false);
				isOrdering = false;
				confirm.visible = false;
				cancel.visible = false;
			} else {
				int shares = StockCache.getStockShares(this.currentStock.symbol);
				float initialCost = (float) StockCache.getInitialStockPrice(this.currentStock.symbol);
				int totalCost = StockTool.calculateProfit(initialCost, this.currentStock.regularMarketPrice, shares, isBuyOrder);
				//double totalCost = this.currentStock.regularMarketPrice*shares;
				Main.NETWORK.sendToServer(new MessageUpdateCredits(this.balance.getBalance() + totalCost));
				Main.NETWORK.sendToServer(new RemoveStockPacket(this.currentStock.symbol));
				Main.NETWORK.sendToServer(new RequestServerStockData(this.currentStock.symbol));
				StockNotification.newStockNotification(this.mc, initialCost, this.currentStock.regularMarketPrice, shares, this.currentStock.symbol);
				transferAmount.setVisible(true);
				investAmount.setVisible(false);
				isClosing = false;
				confirm.visible = false;
				cancel.visible = false;
			}
		}
		if(button == this.closePortion) {
			transferAmount.setVisible(false);
			isClosing = true;
			confirm.visible = true;
			cancel.visible = true;
		}
		super.actionPerformed(button);
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



