package com.jimholden.conomy.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;

import org.apache.http.impl.conn.Wire;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.ATMBlock;
import com.jimholden.conomy.client.gui.engine.AdvancedGUI;
import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.client.gui.engine.IconSheet.Icon;
import com.jimholden.conomy.client.gui.engine.buttons.AdvancedButton;
import com.jimholden.conomy.client.gui.engine.buttons.Alignment;
import com.jimholden.conomy.client.gui.engine.buttons.CardButton;
import com.jimholden.conomy.client.gui.engine.buttons.ConomyButton;
import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;
import com.jimholden.conomy.client.gui.engine.buttons.SimpleButton;
import com.jimholden.conomy.client.gui.engine.display.FrameAlignment;
import com.jimholden.conomy.client.gui.engine.display.IInfoDisplay;
import com.jimholden.conomy.client.gui.engine.display.IconDisplay;
import com.jimholden.conomy.client.gui.engine.display.Margins;
import com.jimholden.conomy.client.gui.engine.display.StringElement;
import com.jimholden.conomy.client.gui.engine.elements.AdvancedScroll;
import com.jimholden.conomy.client.gui.engine.elements.DisplayElement;
import com.jimholden.conomy.client.gui.engine.elements.IconElement;
import com.jimholden.conomy.client.gui.engine.elements.ScrollBlock;
import com.jimholden.conomy.client.gui.engine.elements.ScrollListElement;
import com.jimholden.conomy.client.gui.engine.elements.ShapeElement;
import com.jimholden.conomy.client.gui.engine.elements.scrollblocks.AccountScrollBlock;
import com.jimholden.conomy.client.gui.engine.elements.scrollblocks.TransactionScrollBlock;
import com.jimholden.conomy.client.gui.engine.fields.ConomyTextField;
import com.jimholden.conomy.client.gui.networking.GUINetworkBank;
import com.jimholden.conomy.client.gui.player.GuiATM;
import com.jimholden.conomy.economy.ClientDataManager;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.economy.banking.Account;
import com.jimholden.conomy.economy.banking.Bank;
import com.jimholden.conomy.economy.banking.BankRegistry;
import com.jimholden.conomy.economy.banking.FinancialPlayer;
import com.jimholden.conomy.economy.banking.Account.Type;
import com.jimholden.conomy.economy.data.EconomyDatabase;
import com.jimholden.conomy.economy.record.Transaction;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.AdvGUIPacket;
import com.typesafe.config.ConfigException.Null;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;

public class BankingGUI extends AdvancedGUI {
	
	public static final ResourceLocation BANKING_ICONS = new ResourceLocation(Reference.MOD_ID + ":textures/gui/bankco.png");
	
	
	private ConomyTextField initialDigit;
	private ConomyTextField secondaryID;
	
	private ConomyTextField transferTo;
	private ConomyTextField transferAmount;
	
	private ScrollListElement scroller;
	private ScrollListElement personalScroll;
	
	public Supplier<IconSheet.Icon> iconSupplier = () -> IconSheet.getIcon(BANKING_ICONS, 256, 32, getGUINBT().hasKey("selBank") ? ClientDataManager.byID(getGUINBT().getInteger("selBank")).getIconID() : 0);
	

	public ArrayList<String> transferAccounts = new ArrayList<>();
	public Supplier<ArrayList<String>> tAccountsList = () -> transferAccounts;
	
	public ArrayList<String> personalAccounts = new ArrayList<>();
	
	public Supplier<String> getAccountWorth = () -> {
		
		if(ClientDataManager.clientPlayer == null || personalScroll.getSelectedIndex() == -1) {
			return "(" +  Interchange.formatUSD(0.0) + ")";
		}
		
		
		
		
		return "(" + Interchange.formatUSD(ClientDataManager.clientPlayer.getAccounts().get(personalScroll.getSelectedIndex()).getMoney()) + ")";
	};
	
	public BankingGUI(EntityPlayer p, int redirect, BlockPos redirectGUIPos) {
		super(p, redirect, redirectGUIPos);
	//	ClientDataManager.clientBankInformation.clear();
	}
	
	
	public BankingGUI(EntityPlayer p) {
		super(p);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getAdvancedID() {
		return 0;
	}

	@Override
	public void initGui() {
		super.initGui();
		
		System.out.println("Called");
	//ClientDataManager.clientBankInformation.clear();
		// important for
		pages.clear();
		
		int buttons = 0;
		
		GuiPage loading = writePage(50, 50);
		loading.addElement(new DisplayElement(FrameAlignment.BOTTOMCENTER, new StringElement("Loading...", Alignment.CENTER, 0xfeca57, 0.5), 3, 1.0, loading));
		
	
		
		
		// main page
		GuiPage welcomePage = writePage(250, 125);
		
		//SimpleButton sm = new SimpleButton(4, FrameAlignment.CENTER, new StringElement("fuck", Alignment.BELOW, 0xffffff, 0.5), 15, welcomePage);
		//SimpleButton sm2 = new SimpleButton(5, 152.0, 78, new StringElement("fml lol", Alignment.BELOW, 0xffffff, 0.5), 15, welcomePage);
		
		if(!ClientDataManager.clientBankInformation.isEmpty()) {
			for(Bank b : ClientDataManager.clientBankInformation) {
				IconSheet.Icon bI = IconSheet.getIcon(BANKING_ICONS, 256, 32, b.getIconID());
				CardButton advButton = new CardButton(bI, buttons, FrameAlignment.CENTER, new StringElement(b.getName(), Alignment.BELOW, 0xffffff, 0.5), 35, welcomePage);
				
				advButton.addTextRow(new StringElement(b.getName(), Alignment.CENTER, 0xffffff, 0.7, advButton), 5);
				advButton.addTextRow(new StringElement(" (" + b.getFoundingDate().getYear() + ")", Alignment.CENTER, 0xffffff, 0.5, advButton), 10);
				advButton.addTextRow(new StringElement("\"" + b.getMission() + "\"", Alignment.CENTER, 0x1dd1a1, 0.5, advButton), 5);
				//advButton.addTextRow(b.getCurrentCEO());
				//advButton.addTextRow(b.getMission());
				
				buttons++;
				//break;
				
			}
		}

		
		
		
		
		GuiPage accountCreation = writePage(175, 50);
		
		
		initialDigit = new ConomyTextField(buttons, ClientProxy.newFontRenderer, 150, 35, 15, 15, 1, IInfoDisplay.NONE, accountCreation, false);
		secondaryID = new ConomyTextField(buttons, ClientProxy.newFontRenderer, 170, 35, 15, 40, 7, IInfoDisplay.NONE, accountCreation, false);
		
		SimpleButton sb = new SimpleButton(23, FrameAlignment.BOTTOMCENTER, new StringElement("Register", Alignment.CENTER, 0xffffff, 0.5), 8, accountCreation);
		
		IconElement ico = new IconElement(FrameAlignment.TOPRIGHT, iconSupplier, 30, 0.5, accountCreation);
		accountCreation.addElement(ico);
		
		accountCreation.addElement(new DisplayElement(FrameAlignment.TOPCENTER, new StringElement("Create a Bank ID (ex. j.holden)", Alignment.CENTER, 0xffffff, 0.5), 2, 1.0, accountCreation));
		accountCreation.addElement(new DisplayElement(167, 47, new StringElement(".", Alignment.CENTER, 0xfeca57, 1.0), 2, 1.0, accountCreation));
		
	//	System.out.println(accountCreation.getX() + " | " + accountCreation.getY());
		
		
		//DisplayElement element = new Displa
		
		registerTextField(initialDigit);
		registerTextField(secondaryID);
		
		
		GuiPage success = writePage(50, 50);
		success.advancedDecor(true, 0xffffff, 0xffffff, 10, true);
		DisplayElement successText = new DisplayElement(FrameAlignment.CENTER, new StringElement("Success", Alignment.CENTER, 0x4cd137, 1, false), 2, 1.0, success, new Margins(0, 0));
		success.addElement(successText);
		
		
		GuiPage financialInformationScreen = writePage(250, 150);
		if(ClientDataManager.clientPlayer != null) {
			financialInformationScreen.addElement(new DisplayElement(FrameAlignment.TOPLEFT, new StringElement(ClientDataManager.clientPlayer.getBankID(), Alignment.RIGHT, 0xe84118, 1.0), 2, 1.0, financialInformationScreen, new Margins(-6, 3)));
			IconElement ico3 = new IconElement(FrameAlignment.TOPRIGHT, IconSheet.getIcon(BANKING_ICONS, 256, 32, ClientDataManager.byID(ClientDataManager.clientPlayer.getBank()).getIconID()), 30, 0.5, financialInformationScreen);
			financialInformationScreen.addElement(ico3);
			financialInformationScreen.addElement(new DisplayElement(FrameAlignment.TOPLEFT, new StringElement(ClientDataManager.byID(ClientDataManager.clientPlayer.getBank()).getName(), Alignment.RIGHT, 0xfbc531, 1.0), 2, 1.0, financialInformationScreen, new Margins(-6, 10)));
			ArrayList<ScrollBlock> sBlock = new ArrayList<>();
			double moneyTotal = 0;
			for(Account a : ClientDataManager.clientPlayer.getAccounts()) {
				moneyTotal += a.getMoney();
				sBlock.add(new AccountScrollBlock(a, 20));
			}
			
			sBlock.add(new AccountScrollBlock(new Account("a", Account.Type.CHEQUEING, 1800), 20));
			sBlock.add(new AccountScrollBlock(new Account("a", Account.Type.CHEQUEING, 1800), 20));
			sBlock.add(new AccountScrollBlock(new Account("a", Account.Type.CHEQUEING, 1800), 20));
			sBlock.add(new AccountScrollBlock(new Account("a", Account.Type.CHEQUEING, 1800), 20));
			sBlock.add(new AccountScrollBlock(new Account("a", Account.Type.CHEQUEING, 1800), 20));
			sBlock.add(new AccountScrollBlock(new Account("a", Account.Type.CHEQUEING, 1800), 20));

			financialInformationScreen.addElement(new DisplayElement(FrameAlignment.TOPLEFT, new StringElement("Total: " + Interchange.formatUSD(moneyTotal), Alignment.RIGHT, 0x4cd137, 0.7), 2, 1.0, financialInformationScreen, new Margins(-6, 17)));
			
			
			financialInformationScreen.addElement(new DisplayElement(132, 116, new StringElement("Accounts", Alignment.RIGHT, 0xffffff, 0.7), 2, 1.0, financialInformationScreen));
			financialInformationScreen.addElement(new DisplayElement(223, 116, new StringElement("Recent Transactions", Alignment.RIGHT, 0xffffff, 0.7), 2, 1.0, financialInformationScreen));
			
			
			AdvancedScroll advScroll = new AdvancedScroll(sBlock, 130, 120, 80, 90, 1.0, financialInformationScreen);
			financialInformationScreen.addElement(advScroll);
			
			ArrayList<ScrollBlock> recents = new ArrayList<>();
			for(Transaction t : ClientDataManager.clientPlayer.getRecentTransactions()) {
				recents.add(new TransactionScrollBlock(t, 20));
			}
			
			AdvancedScroll history = new AdvancedScroll(recents, 220, 120, 120, 50, 1.0, financialInformationScreen);
			financialInformationScreen.addElement(history);
		//	SimpleButton addAccount = new SimpleButton(102, 139, 176, new IconDisplay(IconSheet.getIcon(GuiATM.BUTTON_TEXTURES, 256, 64, 3), Alignment.CENTER, 0xffffff, -0.05), 5, financialInformationScreen);
		//	addAccount.setButtonColor(new Color(0x4cd137));
			this.personalAccounts.clear();
			for(Account a : ClientDataManager.clientPlayer.getAccounts()) {
				
				this.personalAccounts.add(a.getAccountName());
			}
		}
		
		SimpleButton transferButton = new SimpleButton(78, FrameAlignment.BOTTOMRIGHT, new StringElement("Transfer", Alignment.CENTER, 0xffffff, 0.5, false), 8, financialInformationScreen);
		
		
		
		
		
		
		GuiPage transferScreen = writePage(250, 50);
		transferScreen.advancedDecor(true, 0xfbc531, 0x1c2127, 10, true);
		
		this.transferAmount = new ConomyTextField(buttons, ClientProxy.newFontRenderer, 200, 43.5, 15, 45, 9, new StringElement("Amount ($)", Alignment.ABOVE, 0xffffff, 0.7), transferScreen, false);
		
		
		
		this.transferTo = new ConomyTextField(buttons, ClientProxy.newFontRenderer, 140, 43.5, 15, 45, 9, new StringElement("Transfer to...", Alignment.ABOVE, 0xffffff, 0.7), transferScreen, false);
		
		
		 scroller = new ScrollListElement(this.tAccountsList, 260, 40, 40, 20, 1.0, transferScreen);
		
		 personalScroll = new ScrollListElement(this.personalAccounts, 200, 60, 45, 11.5, 1.0, transferScreen);
		transferScreen.addElement(scroller);
		transferScreen.addElement(personalScroll);
		if(ClientDataManager.clientPlayer != null) {
			transferScreen.addElement(new DisplayElement(FrameAlignment.TOPLEFT, new StringElement(ClientDataManager.clientPlayer.getBankID(), Alignment.RIGHT, 0xfbc531, 0.8), 2, 1.0, transferScreen, new Margins(-12, 4)));
			
		}
		
		transferScreen.addElement(new DisplayElement(117, 63, new StringElement("Your account:", Alignment.LEFT, 0xffffff, 0.55), 2, 1.0, accountCreation));
		transferScreen.addElement(new DisplayElement(206, 37, new StringElement("Receiver Acc:", Alignment.CENTER, 0xffffff, 0.55), 2, 1.0, accountCreation));
		transferScreen.addElement(new DisplayElement(172, 66, new StringElement(this.getAccountWorth, Alignment.RIGHT, 0xfeca57, 0.7), 2, 1.0, accountCreation));
		
		SimpleButton commitTransfer = new SimpleButton(45, 330, 50, new StringElement("Transfer", Alignment.CENTER, 0xffffff, 0.5), 9, transferScreen);
		SimpleButton backButton = new SimpleButton(92, 137, 67, new IconDisplay(IconSheet.getIcon(GuiATM.BUTTON_TEXTURES, 256, 64, 2), Alignment.CENTER, 0xffffff, -0.15), 5, transferScreen);
		
		
		registerTextField(transferTo);
		registerTextField(this.transferAmount);
		
		
		if(ClientDataManager.clientBankInformation.isEmpty()) {
			setActivePage(0);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("op", GUINetworkBank.GET_BANKS);
			nbt.setInteger("playerID", Minecraft.getMinecraft().player.getEntityId());
			Main.NETWORK.sendToServer(new AdvGUIPacket(getAdvancedID(), nbt));
			
		} else {
			
			if(ClientDataManager.clientPlayer != null) {
				setActivePage(4);
			} else {
				setActivePage(1);
			}
		}
		
		
		
		
		
		//index = 2;
		
		welcomePage.addElement(new DisplayElement(FrameAlignment.TOPCENTER, new StringElement("Choose your Bank", Alignment.CENTER, 0xfeca57, 1.0), 3, 1.0, welcomePage));
		
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		if(this.transferAmount.isFocused()) {
			if((!Character.isAlphabetic(typedChar) && typedChar != '-') || typedChar == '.') {
				
				if(typedChar == '.' && (transferAmount.getText().length() > 1 ? transferAmount.getText().charAt(transferAmount.getText().length()-1) == '.' : false)) return;
			
				transferAmount.textboxKeyTyped(typedChar, keyCode);
			}
			if(transferAmount.getText().length() != 0.0) {
				if(Double.parseDouble(transferAmount.getText()) > Double.parseDouble(getAccountWorth.get().replace("($", "").replace(")", ""))) {
					this.transferAmount.color = 0xff6b6b;
					
				} else {
					this.transferAmount.color = 0x1dd1a1;
					
					
				}
			}
			
		
		} else {
			super.keyTyped(typedChar, keyCode);
		}
		
		if(this.transferTo.isFocused()) {
			if(index == 5) {
		
				NBTTagCompound tag = writePacket(GUINetworkBank.TRANSFER_VERIFY);
				tag.setString("tBID", this.transferTo.getText());
				sendPacket(tag);
			}
			
		}
	
		
		
		
		if(index == 2) {
			
			if(!this.secondaryID.getText().equals("") && !this.initialDigit.getText().equals("")) {
				NBTTagCompound tag = writePacket(GUINetworkBank.VERIFY_ID);
				tag.setString("bankID", this.initialDigit.getText() + "." + this.secondaryID.getText());
				sendPacket(tag);
				
				
			}
		}
		
	}
	
	@Override
	public void clientUpdate(NBTTagCompound nbt) {
		int op = nbt.getInteger("op");
		
		if(nbt.getInteger("op") == GUINetworkBank.GET_BANKS) {
			
			ClientDataManager.loadBanks(nbt.getTagList("banks", NBT.TAG_COMPOUND));
			
			NBTTagCompound fpNBT = (NBTTagCompound) nbt.getTag("fp");
			if(!fpNBT.getBoolean("exists")) {
				ClientDataManager.clientPlayer = null;
			} else {
				ClientDataManager.loadFinancialPlayer(fpNBT);
			}
			ClientDataManager.setStatus(true);
			
			if(redirect != 0.0) {
			//	System.out.println("tried to");
				//redirect();
			} else {
				
				initGui();
				refreshVisibility();
			}
			
			
		} else if (op == GUINetworkBank.VERIFY_ID) {
			boolean taken = nbt.getBoolean("taken");
			
			if(taken) {
				this.initialDigit.color = 0xff6b6b;
				this.secondaryID.color = 0xff6b6b;
			} else {
				this.initialDigit.color = 0x1dd1a1;
				this.secondaryID.color = 0x1dd1a1;
			}
		} else if(op == GUINetworkBank.ATTEMPT_REGISTER) {
			
			boolean result = nbt.getBoolean("result");
			if(!result) {
				for(GuiButton b : this.buttonList) {
					if(b.id == 23) ((ConomyButton) b).fail();
				} 
				
			}else {
				ClientDataManager.getUpdate();
				setActivePage(3);
			}
		} else if(op == GUINetworkBank.TRANSFER_VERIFY) {
			boolean exists = nbt.getBoolean("result");
			
			if(exists) {
				this.transferTo.color = 0x1dd1a1;
				
				transferAccounts.clear();
				NBTTagList list = nbt.getTagList("accounts", NBT.TAG_COMPOUND);
				for(int x = 0; x < list.tagCount(); ++x) {
					NBTTagCompound comp = (NBTTagCompound) list.get(x);
					this.transferAccounts.add(comp.getString("accountName"));
				}
			} else {
				this.transferTo.color = 0xff6b6b;
				
			}
			
			
			
			System.out.println("TRANSFER VERIFY");
			System.out.println(nbt);
		} else if(op == GUINetworkBank.SEND_TRANSFER) {
			ClientDataManager.getUpdate();
		}
		
		super.clientUpdate(nbt);
	}
	

	
	protected void actionPerformed(net.minecraft.client.gui.GuiButton button) throws java.io.IOException {
		System.out.println("click");
		if(button instanceof ConomyButton) {
			
			if(button.id == 78 && ((ConomyButton) button).getPageVisibiity()) {
					System.out.println(this);
					System.out.println("hello " + index + " | " + this.pages.indexOf(((ConomyButton) button).parentPage) + " | " + ((ConomyButton) button).parentPage + " | " + ((ConomyButton) button).getPageVisibiity());
					setActivePage(5);
					button.displayString = "cancel";
					System.out.println("yuh");
				
				return;
			}
			if(button.id == 92 && ((ConomyButton) button).getPageVisibiity()) {
				System.out.println(this);
				System.out.println(button.id + " | " + index + " | " + this.pages.indexOf(((ConomyButton) button).parentPage) + " | " + ((ConomyButton) button).parentPage + " | " + ((ConomyButton) button).getPageVisibiity());
				if(index == 5) {
					setActivePage(4);
				}
				return;
			}
			if(button.id == 45) {
			
				if(this.transferTo.getText().length() == 0 || this.transferAmount.getText().length() == 0 || (Double.parseDouble(transferAmount.getText()) > Double.parseDouble(getAccountWorth.get().replace("($", "").replace(")", "")))) {
					((ConomyButton) button).fail();
					return;
				} else if (scroller.getSelectedIndex() == -1 || personalScroll.getSelectedIndex() == -1) {
					
					if(scroller.getSelectedIndex() == -1 && personalScroll.getSelectedIndex() == -1) {
						personalScroll.fail();
						scroller.fail();
					} else if(scroller.getSelectedIndex() == -1) {
						scroller.fail();
					} else {
						personalScroll.fail();
					}
					
					return;
					
					
				} else {
					if(ClientDataManager.clientPlayer == null) return;
					FinancialPlayer fp = ClientDataManager.clientPlayer;
					NBTTagCompound sendTransfer = writePacket(GUINetworkBank.SEND_TRANSFER);
					sendTransfer.setString("sender", fp.getBankID());
					sendTransfer.setString("receiver", transferTo.getText());
					sendTransfer.setString("receiverAccount", scroller.getArray().get(scroller.getSelectedIndex()));
					sendTransfer.setString("senderAccount", personalScroll.getArray().get(personalScroll.getSelectedIndex()));
					sendTransfer.setDouble("amount", Double.parseDouble(transferAmount.getText()));
					sendPacket(sendTransfer);
					
					
				}
			}
			if(button.id == 23) {
				if(initialDigit.isBlank() || secondaryID.isBlank()) {
					((ConomyButton) button).fail();
				} else {
					NBTTagCompound attemptRegister = writePacket(GUINetworkBank.ATTEMPT_REGISTER);
					attemptRegister.setString("bid", initialDigit.getText() + "." + this.secondaryID.getText());
					attemptRegister.setInteger("bank", getGUINBT().getInteger("selBank"));
					sendPacket(attemptRegister);
				}
			}
			
		}
		
		if(button instanceof CardButton) {
			CardButton card = ((CardButton) button);
			
			
			
			Bank b = ClientDataManager.getBankFromIconID(card.icon.id);
			getGUINBT().setInteger("selBank", b.getBankID());
		
			
			setActivePage(2);
		
		}
		
		//NBTTagCompound comp = new NBTTagCompound();
		//comp.setInteger("playerID", Minecraft.getMinecraft().player.getEntityId());
		//Main.NETWORK.sendToServer(new AdvGUIPacket(comp));
	};
	

}
