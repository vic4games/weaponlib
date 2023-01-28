package com.jimholden.conomy.client.gui.player;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.capabilities.ICredit;
import com.jimholden.conomy.client.gui.BankingGUI;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.client.gui.engine.buttons.Alignment;
import com.jimholden.conomy.client.gui.engine.buttons.ConomyButton;
import com.jimholden.conomy.client.gui.engine.buttons.LedgerButton;
import com.jimholden.conomy.client.gui.engine.buttons.SimpleButton;
import com.jimholden.conomy.client.gui.engine.buttons.ToggleSwitch;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.IconDisplay;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.client.gui.engine.elements.IconElement;
import com.jimholden.conomy.client.gui.engine.fields.ConomyTextField;
import com.jimholden.conomy.containers.ContainerATM;
import com.jimholden.conomy.containers.slots.SlotATMInput;
import com.jimholden.conomy.economy.ClientDataManager;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.economy.banking.Bank;
import com.jimholden.conomy.economy.banking.FinancialPlayer;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.proxy.CommonProxy;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.MessageGetCredits;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;

import akka.routing.CustomRouterConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;


public class GuiATM extends GuiContainer {
	public static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/buttonicons.png");
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/guiatm.png");
	FontRenderer fr = ClientProxy.newFontRenderer;
	private final InventoryPlayer player;
	private EntityPlayer p = null;
	private EntityPlayerMP pMP = null;
	private final TileEntityATM tileentity;
	ICredit iC = null;
	private int displayAmountACC;
	private int displayAmountDEV;
	private int bal;
	public GuiButton transferBank;
	public GuiButton transferDevice;
	public GuiTextField transferAmount;
	private float conversionFactor = 0.235F;
	
	public ToggleSwitch toggle;
	
	public boolean loaded = false;
	
	public ConomyTextField ctf;
	
	public UUID playerUUID;
	
	public boolean transferToDevice = true;
	
	public GuiATM(EntityPlayer player, TileEntityATM tileentity, MinecraftServer server) {
		super(new ContainerATM(player.inventory, tileentity));
		this.player = player.inventory; 
		this.fontRenderer = ClientProxy.newFontRenderer;
		// the player is converted to an inventory player!
		this.tileentity = tileentity;
		this.p = player;
		Minecraft mc = Minecraft.getMinecraft();
		
		this.playerUUID = player.getUniqueID();
		
	//	System.out.println(player);
		//System.out.println(server);
		EntityPlayer pMP = p.world.getPlayerEntityByName(player.getName());
		String user = player.getName().toString();

		
		//System.out.println("USER: " + user);
		this.iC = p.getCapability(CreditProvider.CREDIT_CAP, null);
		
		
		if(player.world.isRemote) {
			
			ClientDataManager.getUpdate();
		}
		
		//ClientDataManager.clientBankInformation = new ArrayList<>();
		//EntityPlayerMP pMP = server.getPlayerList();
		//System.out.println("Bruh does this work???" + tileentity.getPlayerBalance(user)); v
		//this.bal = Main.NETWORK.sendTo(new MessageGetCredits(), (EntityPlayerMP) p);
		//System.out.println("Player: " + this.p + " | Balance: $" + this.bal);
		
		// TODO Auto-generated constructor stub
		
	}
	
	@Override
	public void initGui() {
		if(ClientDataManager.clientPlayer == null && player.player.world.isRemote) {
			Minecraft.getMinecraft().player.closeScreen();
			Main.proxy.showAdvancedGUI(player.player, 0);
		}
		super.initGui();
		
		this.ctf = new ConomyTextField(5, ClientProxy.newFontRenderer, this.guiLeft+65, this.guiTop+90, 15, 32, 15, IInfoDisplay.NONE, null, true);
		
		this.toggle = new ToggleSwitch(6, this.guiLeft+60, this.guiTop+110, new StringElement("Use USD as base?", Alignment.RIGHT, 0xffffff, 0.5), 1);
		this.toggle.setChecked(true);
		
		SimpleButton swap = new SimpleButton(7, this.guiLeft+82.5, this.guiTop+43.5, new IconDisplay(IconSheet.getIcon(BUTTON_TEXTURES, 256, 64, 1), Alignment.CENTER, 0xfffff, 0.15), 5);
		SimpleButton transfer = new SimpleButton(8, this.guiLeft+82.5, this.guiTop+75.5, new StringElement("Transfer", Alignment.CENTER, 0xffffff, 0.4), 7);
		
		this.buttonList.add(transfer);
		this.buttonList.add(swap);
		//LedgerButton sb = new LedgerButton(3, this.guiLeft+55, this.guiTop+15, IInfoDisplay.NONE, 4);
		//this.buttonList.add(sb);
		
		this.buttonList.add(toggle);
		
		
		this.transferBank = new GuiButtonImage(0, this.guiLeft+55, this.guiTop + 15, 9, 9, 176, 0, 9, TEXTURES);
		this.transferDevice = new GuiButtonImage(2, this.guiLeft+55, this.guiTop + 38, 9, 9, 176, 0, 9, TEXTURES);
		transferAmount = new GuiTextField(1, fontRenderer, this.guiLeft+85, this.guiTop + 54, 59, 14);
		this.buttonList.add(transferBank);
		this.buttonList.add(transferDevice);
		transferAmount.setTextColor(0xFFFFFF);
		transferAmount.setMaxStringLength(10);
		this.transferBank.visible = false;
		this.transferDevice.visible = false;
		this.transferAmount.setVisible(false);
	}
	

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		//ClientDataManager.clientBankInformation = new ArrayList<>();
		//Main.proxy.openBankWRedirect(Minecraft.getMinecraft().player, tileentity.getPos(), Reference.GUI_ATM);
		
	
		if(!ClientDataManager.isLoaded()) return;
		
		
		
		String tileName = tileentity.getDisplayName().getFormattedText();
		
		FontRenderer fr = ClientProxy.newFontRenderer;
		
		GUItil.drawScaledString(fr, tileName, 0, -10, 0xffffff, 1.0f);
		
		ItemStack stack = tileentity.getStackInSlot(playerUUID);
		String key = "";
		double credits = 0.0;
		
		if(stack.getItem() instanceof OpenDimeBase) {
			OpenDimeBase odb = (OpenDimeBase) stack.getItem();
			credits = odb.getBalance(stack);
			key = stack.getTagCompound().getString("pkey");
		}
		String usd = Interchange.formatUSD(credits*Interchange.getMRCtoUSDRatio());
		GUItil.drawScaledCenteredString(fr, key, 26, 28, 0x1dd1a1, 0.4f);
		GUItil.drawScaledCenteredString(fr, credits + "", 26, 52, 0xfbc531, 0.75f);
		GUItil.drawScaledCenteredString(fr, usd, 26, 57, 0x1dd1a1, 0.6f);
		
		
		double iconX = 142;
		double iconY = 46;
		double sphereSize = 13;
		try {
			if(ctf.getText().length() != 0.0) {
				String toRender = "";
				if(this.toggle.checked()) {
					GUItil.renderMRC(87.5, 84, 0xfbc531, Double.parseDouble(ctf.getText())/Interchange.getMRCtoUSDRatio(), 0.6);
				} else {
					GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, Interchange.formatUSD(Double.parseDouble(ctf.getText())*Interchange.getMRCtoUSDRatio()), 87.5, 84, 0x4cd137, 0.6f, true);
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		double cash = 0.0;
		if(ctf.getText().length() != 0 && ClientDataManager.clientPlayer != null) {
			if(!this.toggle.checked()) {
				cash = Double.parseDouble(ctf.getText())*Interchange.getMRCtoUSDRatio();
			} else {
				cash = Double.parseDouble(ctf.getText());
			}
			FinancialPlayer fp = ClientDataManager.clientPlayer;
			double comp  = fp.getBalance();
			if(!transferToDevice) comp = ((OpenDimeBase) tileentity.getStackInSlot(playerUUID).getItem()).getBalance(tileentity.getStackInSlot(playerUUID));
			
			if(cash > comp) {
				ctf.changeColor(0xe84118);
			} else {
				ctf.changeColor(0x4cd137);
			}
		}
		
		if(this.toggle.checked()) {
			GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, "$", 62, 93.5, 0x4cd137, 1f);
		} else {
			GUItil.MRC_ICON.render(58, 97.5, 0.025);
		}
		
		//Minecraft.getMinecraft().getTextureManager().getTexture(BUTTON_TEXTURES).setBlurMipmap(true, true);
		Icon ico2 = IconSheet.getIcon(BUTTON_TEXTURES, 256, 64, 2);
		double m = 1;
		if(!transferToDevice) m = -1;
		ico2.render(58, 43.5, -0.1*m);
		ico2.render(107, 43.5, -0.1*m);
		
		if(ClientDataManager.clientPlayer == null) {
			
		} else {
			FinancialPlayer fp = ClientDataManager.clientPlayer;
			
			Bank b = ClientDataManager.byID(fp.getBank());
		
			if(b == null) {
				return;
			}
			
			Icon i = IconSheet.getIcon(BankingGUI.BANKING_ICONS, 256, 32, b.getIconID());
			Minecraft.getMinecraft().getTextureManager().bindTexture(BankingGUI.ICONS);
			i.render(iconX, iconY, 0.3);
			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
			GUItil.renderCircleOutline(Color.YELLOW, 1.0, iconX-0.2, iconY, sphereSize, sphereSize-0.5);
			GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
			GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, fp.getBankID(), iconX, iconY-sphereSize-12, 0xfffff, 0.5f, true);
			GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, b.getName(), iconX, iconY-sphereSize-5, 0xfffff, 0.5f, true);
			GUItil.drawScaledCenteredString(ClientProxy.newFontRenderer, Interchange.formatUSD(fp.getBalance()), iconX, iconY+1+sphereSize, 0xff9f43, 0.5f, true);
			GUItil.renderMRC(iconX, iconY+5+sphereSize, 0xffffff, fp.getBalance()/Interchange.getMRCtoUSDRatio(), 0.5);
		}
		
		
		
		
	}
	
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		/*
		if(!loaded) {
			if(ClientDataManager.isLoaded()) {
				loaded = true;
			} else {
				Main.proxy.openBankWRedirect(Minecraft.getMinecraft().player, tileentity.getPos(), Reference.GUI_ATM);
			}
			return;
		} else {
			
		}
		
		//if(!loaded) return;
		if(!loaded) return;*/
	
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.ctf.drawTextBox();
		
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		
		for(Slot slot : this.inventorySlots.inventorySlots) {
			
			if(!slot.isEnabled()) continue;
			double x = this.guiLeft + slot.xPos;
			double y = this.guiTop + slot.yPos;
			if((slot instanceof SlotItemHandler) && slot.slotNumber == 0) {
				
				GUItil.renderRoundedRectangle(Color.gray, 1.0, x-1, y-1, x+17, y+17, 2, 0.5);
			} else {
				GUItil.renderRoundedRectangle(Color.yellow.darker(), 1.0, x-1, y-1, x+17, y+17, 2, 0.5);
			}
			
			
		}
		
		
		GUItil.renderRoundedRectangle(Color.gray, 1.0, this.guiLeft, this.guiTop, this.guiLeft+this.xSize, this.guiTop+this.ySize, 4, 1);
		
		//this.mc.getTextureManager().bindTexture(TEXTURES);
		//this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		ctf.mouseClicked(mouseX, mouseY, mouseButton);
		transferAmount.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		boolean comboFlag = GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode) || GuiScreen.isKeyComboCtrlV(keyCode) || GuiScreen.isKeyComboCtrlX(keyCode);
 		
		if((!Character.isAlphabetic(typedChar) && typedChar != '-') || typedChar == '.' || comboFlag) {
			
			if(typedChar == '.' && ctf.getText().charAt(ctf.getText().length()-1) == '.') return;
			this.ctf.textboxKeyTyped(typedChar, keyCode);
		}
		
		Character c = typedChar;
		if(!c.isAlphabetic(c)) {
			transferAmount.textboxKeyTyped(typedChar, keyCode);
		}
		
		if (transferAmount.isFocused())
			return;
		super.keyTyped(typedChar, keyCode);
	}

	
	
	@Override
	public void updateScreen() {
		transferAmount.updateCursorCounter();
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
		if(button.id == 7) {
			this.transferToDevice = !this.transferToDevice;
		}
		
		if(button.id == 8) {
			double cash = 0.0;
			if(ctf.getText().length() != 0 && ClientDataManager.clientPlayer != null) {
				if(!this.toggle.checked()) {
					cash = Double.parseDouble(ctf.getText())*Interchange.getMRCtoUSDRatio();
				} else {
					cash = Double.parseDouble(ctf.getText());
				}
				
				
				
				FinancialPlayer fp = ClientDataManager.clientPlayer;
				double comp  = fp.getBalance();
				if(!transferToDevice) comp = ((OpenDimeBase) tileentity.getStackInSlot(playerUUID).getItem()).getBalance(tileentity.getStackInSlot(playerUUID));
				
				if(cash > comp) {
					((ConomyButton) button).fail();
				} else {
					Main.NETWORK.sendToServer(new UpdateDeviceTile(Minecraft.getMinecraft().player.getEntityId(), cash, this.transferToDevice, tileentity.getPos().getX(), tileentity.getPos().getY(), tileentity.getPos().getZ()));
					ClientDataManager.getUpdate();
				}
			}
		}
		/*
		if(button == this.transferBank)
		{
			if(transferAmount.getText() == "") return;
			int toTransfer = Integer.parseInt(transferAmount.getText());
			if(tileentity.isCompatItem())
			{
				double devBal = tileentity.deviceBalance();
				if(toTransfer > devBal) {
					transferAmount.setText("");
					return;
				}
				//tileentity.setDeviceBalance(devBal - toTransfer);
				//this.iC.add(toTransfer);
				this.iC.add(toUSD(toTransfer));
				Main.NETWORK.sendToServer(new MessageUpdateCredits(this.iC.getBalance()));
			//	Main.NETWORK.sendToServer(new UpdateDeviceTile((int) (devBal - toTransfer), this.tileentity.getPos().getX(), this.tileentity.getPos().getY(), this.tileentity.getPos().getZ()));
			}
			
			
		}
		
		if(button == this.transferDevice)
		{
			if(transferAmount.getText() == "") return;
			int toTransfer = Integer.parseInt(transferAmount.getText());
			if(tileentity.isCompatItem())
			{
				double devBal = tileentity.deviceBalance();
				if(toTransfer > this.bal) {
					transferAmount.setText("");
					return;
				}
				//tileentity.setDeviceBalance(devBal + toTransfer);
				this.iC.remove(toTransfer);
				Main.NETWORK.sendToServer(new MessageUpdateCredits(this.iC.getBalance()));
				//Main.NETWORK.sendToServer(new UpdateDeviceTile(devBal + toTransfer, this.tileentity.getPos().getX(), this.tileentity.getPos().getY(), this.tileentity.getPos().getZ()));
				//Main.NETWORK.sendToServer(new UpdateDeviceTile((int) (devBal + toMRC(toTransfer)), this.tileentity.getPos().getX(), this.tileentity.getPos().getY(), this.tileentity.getPos().getZ()));
				
			}
			
		}*/
		super.actionPerformed(button);
	}

}
