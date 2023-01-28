package com.jimholden.conomy.client.gui.engine;

import java.io.IOException;
import java.util.ArrayList;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.client.gui.engine.buttons.ConomyButton;
import com.jimholden.conomy.client.gui.engine.fields.ConomyTextField;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.packets.AdvGUIClientPacket;
import com.jimholden.conomy.util.packets.AdvGUIPacket;
import com.jimholden.conomy.util.packets.GuiRedirectPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.math.BlockPos;

public class AdvancedGUI extends GuiScreen  {
	
	public int index = 0;
	public ArrayList<GuiPage> pages = new ArrayList<>();
	
	public ArrayList<ConomyTextField> textFields = new ArrayList<ConomyTextField>();
	
	public EntityPlayer player;
	
	int xSize = 176;
	//int ySize = 112;
	int ySize = 112;
	int guiLeft = 135;
	int guiTop = 60;
	
	
	public int redirect;
	public BlockPos redirectGUIPos;
	
	public NBTTagCompound guiDataBuffer = new NBTTagCompound();
	
	public AdvancedGUI(EntityPlayer p, int redirect, BlockPos pos) {
		this.player = p;
		buildGUI();
		this.redirect = redirect;
		this.redirectGUIPos = pos;
	}
	
	
	public NBTTagCompound getGUINBT() {
		return guiDataBuffer;
	}
	
	
	public AdvancedGUI(EntityPlayer p) {
		this.player = p;
		buildGUI();
	}
	
	@Override
	public void initGui() {
		textFields.clear();
		pages.clear();
		
		
		
	}
	
	public void redirect() {
		Main.NETWORK.sendToServer(new GuiRedirectPacket(Minecraft.getMinecraft().player.getEntityId(), this.redirect, redirectGUIPos));
		Minecraft.getMinecraft().player.openGui(Main.instance, this.redirect, Minecraft.getMinecraft().player.world, redirectGUIPos.getX(), redirectGUIPos.getY(), redirectGUIPos.getZ());
	}
	
	public int getAdvancedID() {
		return 0;
	}
	
	
	public void buildGUI() {
		
	}
	
	public void fireTrigger(GuiPage page) {
		for(GuiButton b : this.buttonList) {
			if(b instanceof ConomyButton) {
				ConomyButton cB = (ConomyButton) b;
				if(cB.parentPage == page) {
					cB.trigger();
				}
			}
		}
	}
	
	public void addToButtonList(ConomyButton cb) {
		this.buttonList.add(cb);
	}
	
	
	public void registerTextField(ConomyTextField ctf) {
		this.textFields.add(ctf);
	}
	
	public void setActivePage(int index) {
		if(this.index != index) {
			
			this.index = index;
			System.out.println("Changin page");
			pages.get(index).setVisible(true);
			
			refreshVisibility();
			
			
		}
	}
	
	public void refreshVisibility() {
		this.pages.get(index).setVisible(true);

		for(GuiButton cb : this.buttonList) {
			if(cb instanceof ConomyButton) {
				
				((ConomyButton) cb).updateVisibility();
			}
		}
		for(ConomyTextField ctf : this.textFields) {
			ctf.updateVisibility();
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for(ConomyTextField ctf : textFields) {
			ctf.textboxKeyTyped(typedChar, keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		System.out.println("CLCIKERS");
		System.out.println("clicked @" + this.pages.get(index).getHeight());
		
		
		for(ConomyTextField ctf : textFields) {
			ctf.mouseClicked(mouseX, mouseY, mouseButton);
		}
		
		for(GuiPage p : pages) {
			for(GuiElement ge : p.elements) {
				if(ge instanceof IMouseClick) {
					((IMouseClick) ge).mouseClicked(mouseX, mouseY, mouseButton);
				}
			}
		}
		
		 if (mouseButton == 0)
	        {
	            for (int i = 0; i < this.buttonList.size(); ++i)
	            {
	                GuiButton guibutton = this.buttonList.get(i);
	                if(guibutton instanceof ConomyButton && !((ConomyButton) guibutton).getPageVisibiity()) continue;
	               
	                System.out.println("YOLO: " + i + " | " + index);
	                
	                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
	                {
	                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
	                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
	                        break;
	                    guibutton = event.getButton();
	                    this.selectedButton = guibutton;
	                    guibutton.playPressSound(this.mc.getSoundHandler());
	                    this.actionPerformed(guibutton);
	                    if(guibutton.displayString.equals("cancel")) break;
	                    
	                    if (this.equals(this.mc.currentScreen))
	                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
	                }
	            }
	        }
		//super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		GUItil.initializeMultisample();
		drawDefaultBackground();
		renderGUI();
		
		for(ConomyTextField ctf : textFields) {
			ctf.drawTextBox();
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		GUItil.unapplyMultisample();
	}
	
	@Override
	public void drawDefaultBackground() {
		
		super.drawDefaultBackground();
	}
	
	public NBTTagCompound writePacket(int op) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("op", op);
		nbt.setInteger("playerID", Minecraft.getMinecraft().player.getEntityId());
		return nbt;
	}
	
	public void sendPacket(NBTTagCompound nbt) {
		Main.NETWORK.sendToServer(new AdvGUIPacket(getAdvancedID(), nbt));
	}
	
	public int getOpCode(NBTTagCompound nbt) {
		return nbt.getInteger("op");
	}
	
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// TODO Auto-generated method stub
		super.actionPerformed(button);
	}
	
	public void clientUpdate(NBTTagCompound nbt) {
		
	}
	

	
	
	
	@Override
	public void updateScreen() {
		this.pages.get(index).setVisible(true);
		refreshVisibility();
		for(GuiPage p : this.pages) {
			
			if(p.getVisibility()) p.tick();
		}
		super.updateScreen();
	}
	
	public GuiPage writePage(double w, double h) {
		GuiPage page = new GuiPage(w, h);
		pages.add(page);
		page.parentGUI = this;
		return page;
	}
	
	public void renderGUI() {
		refreshVisibility();
		pages.get(index).render();
		
	}

	
	/**
	 * 
	 */
	
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int xP, int yP)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + 0), (double)(y + yP), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + xP), (double)(y + yP), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + xP), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }

	
	
}
