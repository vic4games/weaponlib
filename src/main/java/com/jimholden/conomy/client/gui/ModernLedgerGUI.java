package com.jimholden.conomy.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.jimholden.conomy.client.gui.engine.AdvancedGUI;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.client.gui.engine.buttons.Alignment;
import com.jimholden.conomy.client.gui.engine.buttons.LedgerButton;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.Margins;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.client.gui.engine.elements.DisplayElement;
import com.jimholden.conomy.client.gui.engine.elements.IconElement;
import com.jimholden.conomy.client.gui.engine.elements.ScrollListElement;
import com.jimholden.conomy.client.gui.networking.GUINetworkLedger;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public class ModernLedgerGUI extends AdvancedGUI {
	
	public double credits = 0;
	public ArrayList<String> nearbyLedgers = new ArrayList<>();
	public boolean isOn = false;

	public String key;
	public Supplier<String> ledgerKey = () -> key;

	public ScrollListElement scroll;
	public LedgerButton lb;
	 
	public Supplier<String> transaction = () -> " " + getTransferAmount() + "";
	public Supplier<String> usdEquiv = () -> Interchange.formatUSD(Interchange.getMRCtoUSDRatio()*getTransferAmount()) + "";
	
	public Supplier<Boolean> ledgerState = () -> isOn;
 	public Supplier<ArrayList<String>> arraySupplier = () -> nearbyLedgers;
 	public Supplier<String> creditString = () -> " " + credits;
 	
 	public Supplier<Double> xOffsetLogo = () -> (double) -ClientProxy.newFontRenderer.getStringWidth(transaction.get())*0.2;
 	

	public static final ResourceLocation MRC = new ResourceLocation(Reference.MOD_ID + ":textures/gui/mrclogo.png");
	
	
 	
 	public int doubleClickTimer = 0;
 	public long doubleClickStartTime = 0;
 
	public ModernLedgerGUI(EntityPlayer p) {
		super(p);
	}
	
	
	@Override
	public int getAdvancedID() {
		
		return 1;
	}
	
	public double getTransferAmount() {
		return Math.round((lb.progress)*credits*100)/100.0;
	}
	
	@Override
	public void initGui() {
		
		super.initGui();
		
		GuiPage loading = writePage(50, 50);
		loading.advancedDecor(true, 0x2f3640, 0x20252c, 15, true);
		loading.addElement(new DisplayElement(FrameAlignment.CENTER, new StringElement("LOADING...", Alignment.CENTER, 0xffffff, 0.75), 3, 1.0, loading));
		
		Icon ico = IconSheet.getIcon(MRC, 256, 256, 1);
		
		Color niceScreenGray = new Color(0x2f3640).darker();
		GuiPage main = writePage(300, 50);
		main.advancedDecor(true, 0x2f3640, 0x20252c, 15, true);
		
		DisplayElement key = new DisplayElement(FrameAlignment.TOPLEFT, new StringElement(ledgerKey, Alignment.RIGHT, 0xffffff, 0.8), 3, 1.0, main, new Margins(-15, 5));
		DisplayElement credits = new DisplayElement(FrameAlignment.TOPLEFT, new StringElement(creditString, Alignment.RIGHT, 0xfbc531, 0.8), 3, 1.0, main, new Margins(-21, 21));
		IconElement logo2 = new IconElement(165, 48.5, ico, 4, 0.02, main);
		
		
		
		
		DisplayElement testerer = new DisplayElement(380, 50, new StringElement(transaction, Alignment.CENTER, 0xfbc531, 0.8), 3, 1.0, main);
		DisplayElement usdEquivalentDisplay = new DisplayElement(380, 55, new StringElement(usdEquiv, Alignment.CENTER, 0x1dd1a1, 0.5), 3, 1.0, main);
	
		
		
		//Minecraft.getMinecraft().getTextureManager().getTexture(MRC).setBlurMipmap(true, true);
		IconElement logo = new IconElement(370, 50.25, ico, 4, 0.0175, main, xOffsetLogo);
		
		

		
		scroll = new ScrollListElement(this.arraySupplier, 250, 30, 100, 40, 1.0, main);
		
		 lb = new LedgerButton(42, this.ledgerState, 425, 50, IInfoDisplay.NONE, 15, main);
		main.addElement(logo2);
	//	main.addElement(tiel);
		main.addElement(key);
		main.addElement(logo);
		main.addElement(credits);
		main.addElement(scroll);
		main.addElement(testerer);
		main.addElement(usdEquivalentDisplay);
		
		
		
		
		if(index == 0) {
			NBTTagCompound getInfo = writePacket(GUINetworkLedger.UPDATE_INFO);
			sendPacket(getInfo);
		}
	}
	
	@Override
	public void updateScreen() {
		if(doubleClickTimer != 0) {
			if(System.currentTimeMillis()-doubleClickStartTime > 150) {
				doubleClickTimer = 0;
				lb.angle = 0;
			}
		}
		
		super.updateScreen();
	}
	
	@Override
	public void clientUpdate(NBTTagCompound nbt) {
		int op = getOpCode(nbt);
	
		switch(op) {
		case GUINetworkLedger.UPDATE_INFO: 
			
			this.credits = nbt.getDouble("credits");
			this.isOn = nbt.getBoolean("state");
			this.key = nbt.getString("key");
			
			this.nearbyLedgers.clear();
			NBTTagList nearby = nbt.getTagList("nearby", NBT.TAG_STRING);
			for(int n  = 0; n < nearby.tagCount(); ++n) {
				this.nearbyLedgers.add(nearby.getStringTagAt(n));
			}
			
			setActivePage(1);
			break;
		case GUINetworkLedger.SEND_TRANSACTION:
			if(nbt.getBoolean("result")) {
				lb.fillerTimer.tick();
			}
			break;
		}
		
		// TODO Auto-generated method stub
		super.clientUpdate(nbt);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 42) {
			LedgerButton ledgerButton = (LedgerButton) button;
			if(ledgerButton.angle == 0.0) {
				sendPacket(writePacket(GUINetworkLedger.TOGGLE_STATE));
			} else if(this.doubleClickTimer == 0) {
				this.doubleClickTimer++;
				this.doubleClickStartTime = System.currentTimeMillis();
			} else if(this.doubleClickTimer != 0) {
				if(System.currentTimeMillis()-this.doubleClickStartTime < 250) {
					this.doubleClickTimer = 0;
					
					if(scroll.selectedIndex != -1) {
						try {
							NBTTagCompound transaction = writePacket(GUINetworkLedger.SEND_TRANSACTION);
							transaction.setDouble("amount", getTransferAmount());
							transaction.setString("sender", this.key);
							transaction.setString("recv", nearbyLedgers.get(scroll.selectedIndex));
							sendPacket(transaction);
						} catch(Exception e) {
							e.printStackTrace();
						}
						
					} else {
						scroll.fail();
					}
					
					
				} else {
					this.doubleClickTimer = 0;
					ledgerButton.angle = 0.0;
				}
			} else {
			
			}
		
			
			
			
			
		}
		super.actionPerformed(button);
	}
	

}
