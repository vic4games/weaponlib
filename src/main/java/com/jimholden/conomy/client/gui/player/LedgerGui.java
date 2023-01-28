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


public class LedgerGui extends GuiScreen {
	
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
	
	
	
	public LedgerGui(EntityPlayer p) {
		this.p = p;
		
		
		
	}
	

	public void drawMenuBackground(int tint)
    {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURES);
        
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(!confirmationScreen)
        	this.drawTexturedModalRect(this.guiLeft, this.guiTop+111, 0, 111, this.xSize, 39);
        else
        	this.drawTexturedModalRect(this.guiLeft, this.guiTop+111, 0, 149, this.xSize, 39);
        //this.drawTexturedModalRect(this.guiLeft + 44, this.guiTop + 36, 176, 14, 1, 16);
        
        if(!(((LedgerBase) p.getHeldItemMainhand().getItem()).getState(p.getHeldItemMainhand())))
		{
			this.drawTexturedModalRect(this.guiLeft + 12, this.guiTop + 34, 0, 206, 151, 50);
		}
		
        

    }
	
	public void drawTransactionScreen(int tint)
    {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURES);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+105, 0, 112, this.xSize, this.ySize);
        //this.drawTexturedModalRect(this.guiLeft + 44, this.guiTop+36, 176, 14, 1, 16);
        
    }

	@Override
	public void drawDefaultBackground() {
		// TODO Auto-generated method stub
		super.drawDefaultBackground();
	}
	
	@Override // 177, 0 | 187, 11
	public void initGui() {
		super.initGui();
		int offsetFromLeft = (this.width - 256)/2;
		int offsetFromTop = this.height - (this.height/4);
		//this.transfer = new GuiButton(0, this.guiLeft + 173, this.guiTop, 25, 50, "T");
		
		this.upButton = new GuiButtonImage(3, this.guiLeft+1, this.guiTop + 45, 11, 11, 176, 0, 11, TEXTURES);
		this.downButton = new GuiButtonImage(4, this.guiLeft+1, this.guiTop + 55, 11, 11, 186, 0, 11, TEXTURES);
		//this.transfer = new GuiButton(0, offsetFromLeft - 10, offsetFromTop - 20, "Transfer");
		//this.transfer = new GuiButton(0, this.guiLeft + 173, this.guiTop, "T");
		//drawTexturedModalRect(xCoord, yCoord, minU, minV, maxU, maxV);
		
		//this.confirm = new GuiButtonImage(5, this.guiLeft+10, this.guiTop + 45, 11, 11, 176, 0, 11, TEXTURES);
		
		this.transfer = new GuiButtonImage(4, this.guiLeft+115, this.guiTop + 130, 15, 15, 197, 0, 15, TEXTURES);
		this.confirm = new GuiButtonImage(5, this.guiLeft + 5, this.guiTop + 134, 11, 11, 176, 149, 11, TEXTURES);
		this.cancel = new GuiButtonImage(7, this.guiLeft + 160, this.guiTop + 134, 11, 11, 186, 149, 11, TEXTURES);
		this.leftPage = new GuiButtonExt(1, this.guiLeft + 12, this.guiTop + 87, 20, 20,"<");
		this.rightPage = new GuiButtonExt(2, this.guiLeft + 143, this.guiTop + 87, 20, 20,">");
		
		
		//this.cancel = new GuiButtonImage(6, this.guiLeft+115, this.guiTop + 155, 15, 15, 186, 0, 11, TEXTURES);
		FontRenderer fontrenderer2 = minecraft.fontRenderer;
		//this.transferAmount = new GuiTextField(5, fontrenderer2, this.guiLeft, this.guiTop - 35, 40, 20);
		
		transferAmount = new GuiTextField(6, fontrenderer2, this.guiLeft + 52, this.guiTop + 131, 59, 14);
		
		
		
		//
		this.buttonList.add(leftPage);
		this.buttonList.add(rightPage);
		this.buttonList.add(upButton);
		this.buttonList.add(downButton);
		this.buttonList.add(transfer);
		this.buttonList.add(confirm);
		this.buttonList.add(cancel);
		this.cancel.visible = false;
		this.confirm.visible = false;
		//transfer.visible = false;

		
		
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
		this.drawMenuBackground(3);
		transferAmount.drawTextBox();
		transferAmount.setMaxStringLength(10);
		/*
		if(this.confirmationScreen) {
			this.cancel.visible = true;
			this.confirm.visible = true;
			this.transfer.visible = false;
			this.transferAmount.setVisible(false);
		}
		else {
			this.cancel.visible = false;
			this.confirm.visible = false;
			this.transfer.visible = true;
			this.transferAmount.setVisible(true);
		}
		*/
		//this.drawTransactionScreen(3);
		// 0, 11
		//this.drawTexturedModalRect(this.guiLeft + 173, this.guiTop, 176, 11, 55, 17);
		FontRenderer fontrenderer = mc.fontRenderer;
		ItemStack stack = p.getHeldItemMainhand();
		String bal = "" + ((LedgerBase) p.getHeldItemMainhand().getItem()).getBalance(stack); 
		if(Integer.parseInt(bal) > this.displayAmount) {
			if(Integer.parseInt(bal)-this.displayAmount >= 30)
			{
				this.displayAmount += (Integer.parseInt(bal)-this.displayAmount) / 10;
			}
			else
			{
				this.displayAmount++;
			}
			
		}
		if(Integer.parseInt(bal) < this.displayAmount) {
			if(this.displayAmount-Integer.parseInt(bal) >= 30)
			{
				this.displayAmount -= (this.displayAmount-Integer.parseInt(bal)) / 10;
			}
			else
			{
				this.displayAmount--;
			}
			
		}
		//drawString(fontrenderer, TextFormatting.GREEN + "MRC" + bal, this.guiLeft + 13, this.guiTop + 8, 0xe0e0e0);
		drawString(fontrenderer, TextFormatting.GREEN + "MRC" + this.displayAmount, this.guiLeft + 13, this.guiTop + 8, 0xe0e0e0);
		if(((LedgerBase) p.getHeldItemMainhand().getItem()).getState(p.getHeldItemMainhand()))
		{
			drawString(fontrenderer, TextFormatting.GREEN + "" + pageSelected + "/" + totalPages, this.guiLeft + 77, this.guiTop + 92, 0xe0e0e0);
		}
		else
		{
			drawString(fontrenderer, TextFormatting.RED + "N/A", this.guiLeft + 79, this.guiTop + 93, 0xe0e0e0);
		}
		
		///////////////
		
		//List<EntityPlayer> playersL = p.world.getEntitiesWithinAABB(EntityPlayer.class, p.getEntityBoundingBox().expand(80, 80, 80));
		List<EntityPlayer> playersL = p.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(p.posX-10, p.posY-10, p.posZ-10, p.posX+10, p.posY+10, p.posZ+10));
		if(!playersL.isEmpty()) {
			List list = new ArrayList(); 
			for(int y = 0; y < playersL.size(); y++)
			{
				EntityPlayer targetL = playersL.get(y);
				ItemStack stack2 = p.getHeldItemMainhand();
				ItemStack targetStack = null;
				
				for(int i = 0; i < targetL.inventory.getSizeInventory(); i++) {
					if(targetL.inventory.getStackInSlot(i).getItem() instanceof LedgerBase) {
						targetStack = targetL.inventory.getStackInSlot(i);

						if(((LedgerBase) targetStack.getItem()).getState(targetStack) && ((LedgerBase) targetStack.getItem()).getKey(targetStack) != ((LedgerBase) p.getHeldItemMainhand().getItem()).getKey(p.getHeldItemMainhand())) {
							//p.sendMessage(new TextComponentString(TextFormatting.RED + "" + ((LedgerBase) targetStack.getItem()).getKey(targetStack)));
							list.add(((LedgerBase) targetStack.getItem()).getKey(targetStack));
							//break;
						}
						
					}
					
				}
				
			}
			
			
			totalPages = Math.round((float) Math.ceil(list.size()/6.0));
			//p.sendMessage(new TextComponentString(TextFormatting.RED + "" + list.toString()));
			// for(int x = 0; x < list.size(); x++) {
			
			/* make trimmed list
			List trimList = new ArrayList();
			int toStart = (pageSelected-1)*6;
			int toEnd = trimList.size()-toStart;
			if(toEnd > 6) toEnd = 6;
			for(int n = toStart; n < toEnd; n++) {
				trimList.add(list.get(n));
			}
			p.sendMessage(new TextComponentString(TextFormatting.RED + "" + toStart + "" + trimList.toString() + toEnd));
			*/
			int toStart = (pageSelected-1)*6;
			int toEnd = (list.size()-toStart)+toStart;
			List<String> trimList = new ArrayList<String>(list.subList(toStart, toEnd));
			maxOptions = trimList.size();
			//p.sendMessage(new TextComponentString(TextFormatting.RED + "" + toStart + "" + trimList.toString() + toEnd));
			//System.out.println((((LedgerBase) p.getHeldItemMainhand().getItem()).getState(p.getHeldItemMainhand())));
			
			if(((LedgerBase) p.getHeldItemMainhand().getItem()).getState(p.getHeldItemMainhand()))
			{
				int toRender = trimList.size();
				if(toRender > 6) toRender = 6;

				for(int x = 0; x < toRender; x++) {
					if(x == highlightedOption) {
						drawString(fontrenderer, TextFormatting.YELLOW + ">" + trimList.get(x).toString(), this.guiLeft + 13, this.guiTop + 35 + x*8, 0xe0e0e0);
					}
					else {
						drawString(fontrenderer, TextFormatting.GREEN + "" + trimList.get(x).toString(), this.guiLeft + 13, this.guiTop + 35 + x*8, 0xe0e0e0);
						
					}
				}
				if(highlightedOption > maxOptions-1) highlightedOption = maxOptions-1; 
				if(highlightedOption < 0) highlightedOption = 0;
				if(highlightedOption < trimList.size())
				{
					if(!this.confirmationScreen)
					{
						selectedkey = trimList.get(highlightedOption).toString();
						drawCenteredString(fontrenderer, TextFormatting.GREEN + trimList.get(highlightedOption).toString(), this.guiLeft + 77, this.guiTop + 119, 0xe0e0e0);
					}
					else
					{
						selectedkey = trimList.get(highlightedOption).toString();
						drawCenteredString(fontrenderer, TextFormatting.WHITE + trimList.get(highlightedOption).toString(), this.guiLeft + 87, this.guiTop + 116, 0xe0e0e0);
						drawCenteredString(fontrenderer, TextFormatting.WHITE + "MRC" + this.reqMoney, this.guiLeft + 87, this.guiTop + 125, 0xe0e0e0);
					}
					
					
				}
			}
			else
			{
				drawCenteredString(fontrenderer, TextFormatting.RED + "NO POWER", this.guiLeft + 83, this.guiTop + 119, 0xe0e0e0);
			}
			
			
			
			//drawCenteredString(fontrenderer, TextFormatting.AQUA + "" + ((float)list.size()/6), this.guiLeft + 65, this.guiTop + 125, 0xe0e0e0);
			//drawCenteredString(fontrenderer, TextFormatting.AQUA + "" + ((float)Math.ceil(list.size()/6.0)), this.guiLeft + 65, this.guiTop + 135, 0xe0e0e0);
			
		}
		
		///////////////
		
		// TODO Auto-generated method stub
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		transferAmount.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
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
	
	
	
	
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// TODO Auto-generated method stub
		/*
		 List<EntityPlayer> playersL = p.world.getEntitiesWithinAABB(EntityPlayer.class, p.getEntityBoundingBox().expand(80, 80, 80));
		if(!playersL.isEmpty()) {
			EntityPlayer targetL = playersL.get(0);
			ItemStack stack2 = p.getHeldItemMainhand();
			ItemStack targetStack = null;
			List list = new ArrayList(); 
			for(int i = 0; i < targetL.inventory.getSizeInventory(); i++) {
				if(targetL.inventory.getStackInSlot(i).getItem() instanceof LedgerBase) {
					targetStack = targetL.inventory.getStackInSlot(i);

					if(((LedgerBase) targetStack.getItem()).getState(targetStack)) {
						//p.sendMessage(new TextComponentString(TextFormatting.RED + "" + ((LedgerBase) targetStack.getItem()).getKey(targetStack)));
						list.add(((LedgerBase) targetStack.getItem()).getKey(targetStack));
						//break;
					}
					
				}
				
			}
		  
		 */
		
		
		
		if(button == this.transfer) {
			if(transferAmount.getText() == "") return;
			ItemStack stack = p.getHeldItemMainhand();
			double currentBalance = ((LedgerBase) stack.getItem()).getBalance(stack);
			//p.sendMessage(new TextComponentString(currentBalance + "h"));
			if(currentBalance < this.reqMoney || currentBalance == 0) {
				transferAmount.setText("");
				return;
			}
			
			this.confirmationScreen = true;
			this.lockedKey = this.selectedkey;
			this.cancel.visible = true;
			this.confirm.visible = true;
			this.transfer.visible = false;
			this.transferAmount.setVisible(false);
			this.reqMoney = Integer.parseInt(transferAmount.getText());
			
		}
		if(button == this.rightPage) {
			if(!this.confirmationScreen)
			{
				pageSelected += 1;
				if(pageSelected > totalPages) pageSelected = totalPages;
			}
			
		}
		
		if(button == this.leftPage)
		{
			if(!this.confirmationScreen)
			{
				pageSelected -= 1;
				if(pageSelected < 1) pageSelected = 1;
			}
			
		}
		if(button == this.downButton)
		{
			if(!this.confirmationScreen)
			{
				highlightedOption += 1;
				if(highlightedOption > maxOptions-1) highlightedOption = maxOptions-1; 
			}
			
		}
		if(button == this.upButton)
		{
			if(!this.confirmationScreen)
			{
				highlightedOption -= 1;
				if(highlightedOption < 0) highlightedOption = 0; 
			}
			
		}
		if(button == this.confirm)
		{
			if(transferAmount.getText() == "") return;
			int toTransfer = this.reqMoney;
			//int toTransfer = Integer.parseInt(transferAmount.getText());
			//List<EntityPlayer> players = p.world.getEntitiesWithinAABB(EntityPlayer.class, p.getEntityBoundingBox().expand(80, 80, 80));
			
			List<EntityPlayer> players = p.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(p.posX-10, p.posY-10, p.posZ-10, p.posX+10, p.posY+10, p.posZ+10));
			int slot = 0;
			if(!players.isEmpty()) {
				List list2 = new ArrayList(); 
				for(int t = 0; t < players.size();) {
					
					EntityPlayer target = players.get(t);
					ItemStack stack = p.getHeldItemMainhand();
					double currentBalance = ((LedgerBase) stack.getItem()).getBalance(stack);
					if(currentBalance < toTransfer) {
						transferAmount.setText("");
						
						return;
					}

					for(int x = 0; x < target.inventory.getSizeInventory(); x++)
					{
						//p.sendMessage(new TextComponentString(target.inventory.getStackInSlot(x).getItem().toString()));
						if(target.inventory.getStackInSlot(x).getItem() instanceof LedgerBase)
						{
							if(((LedgerBase) target.inventory.getStackInSlot(x).getItem()).getKey(target.inventory.getStackInSlot(x)).equals(selectedkey))
							{
								slot = x;
								break;
							}
						}
					}
					Main.NETWORK.sendToServer(new LedgerTransferThree(toTransfer, ((LedgerBase) stack.getItem()).getKey(stack), selectedkey));
					//Main.NETWORK.sendToServer(new LedgerTransferTwo(toTransfer, selectedkey, p, target));
					//Main.NETWORK.sendToServer(new LedgerTransfer(toTransfer, slot, p, target));
					break;
					
				}
				
			
			}
		}
		if(button == this.cancel)
		{
			this.confirmationScreen = false;
			this.cancel.visible = false;
			this.confirm.visible = false;
			this.transfer.visible = true;
			this.transferAmount.setVisible(true);
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



