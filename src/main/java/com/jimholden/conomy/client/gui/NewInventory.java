package com.jimholden.conomy.client.gui;

import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;


import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.client.gui.engine.IconSheet;
import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.RigItem;
import com.jimholden.conomy.main.ModEventClientHandler;
import com.jimholden.conomy.medical.ConsciousCapability;
import com.jimholden.conomy.medical.ConsciousProvider;
import com.jimholden.conomy.medical.IConscious;
import com.jimholden.conomy.render.RenderTool;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.animations.AnimationPlayTool;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NewInventory extends NewInventoryEffectRenderer implements IRecipeShownListener
{
	
	public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(Reference.MOD_ID + ":textures/gui/inventorynew.png");
	public static final ResourceLocation GRAY_IMAGE = new ResourceLocation(Reference.MOD_ID + ":textures/gui/grayimage.png");
	public static final ResourceLocation BACKPACK_SLOT = new ResourceLocation(Reference.MOD_ID + ":textures/items/backpackslot.png");
	
	
	
    /** The old x position of the mouse pointer */
    private float oldMouseX;
    /** The old y position of the mouse pointer */
    private float oldMouseY;
    private GuiButtonImage recipeButton;
    private final GuiRecipeBook recipeBookGui = new GuiRecipeBook();
    private boolean widthTooNarrow;
    private boolean buttonClicked;
    private boolean isOverlay;
    
    private int adjX = 0;
    private int adjY = 0;
    

    public NewInventory(EntityPlayer player, boolean isRemote, boolean isOverlay)
    {
    	
    	//super(player.inventoryContainer);
    	super(new ContainerInvExtend(player.inventory, isRemote, player));
    	
        this.isOverlay = isOverlay;
        this.allowUserInput = true;
    }
    
    public void drawTexturedModalRectBack(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV, int height, int width)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(xCoord + 0.0F), (double)(yCoord + (float)width), (double)this.zLevel).tex((double)((float)(minU + 0) * 0.00390625F), (double)((float)(minV + maxV) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(xCoord + (float)height), (double)(yCoord + (float)width), (double)this.zLevel).tex((double)((float)(minU + maxU) * 0.00390625F), (double)((float)(minV + maxV) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(xCoord + (float)height), (double)(yCoord + 0.0F), (double)this.zLevel).tex((double)((float)(minU + maxU) * 0.00390625F), (double)((float)(minV + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(xCoord + 0.0F), (double)(yCoord + 0.0F), (double)this.zLevel).tex((double)((float)(minU + 0) * 0.00390625F), (double)((float)(minV + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    
    
    public void drawBackRect(int x, int y, int textureX, int textureY, int texW, int texH, int width, int height)
    {
		float zLevel = -5.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX); 
        
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + texH) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + texW) * 0.00390625F), (double)((float)(textureY + texH) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + texW) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
        //GlStateManager.scale(1, 1, 1);
        
    }
    
    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
    	// TODO Auto-generated method stub
    	super.setWorldAndResolution(mc, width, height);
    }
    
    public static void drawRect(int left, int top, int right, int bottom, int color)
    {
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

	public void drawTexturedModalRectScaled(int x, int y, int textureX, int textureY, int width, int height, float scaled)
    {
		float zLevel = -5.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX); 
        x = (int) ((float) x/scaled);
        y = (int) ((float) y/scaled);
        GlStateManager.scale(scaled, scaled, scaled);
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
        GlStateManager.scale(1, 1, 1);
        
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (this.mc.playerController.isInCreativeMode() && !this.isOverlay)
        {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.player));
        }

        //this.recipeBookGui.tick();
    }
    
    
    public void drawScaledString(FontRenderer fontRendererIn, String text, int x, int y, int color, float scale) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		drawString(fontRendererIn, text, (int) (x/scale), (int) (y/scale), color);
		GL11.glPopMatrix();
	}

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();

        if (this.mc.playerController.isInCreativeMode() && !this.isOverlay)
        {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.player));
        }
        else
        {
            super.initGui();
        }

       // this.widthTooNarrow = this.width < 379;
       // this.recipeBookGui.func_194303_a(this.width, this.height, this.mc, this.widthTooNarrow, ((ContainerInvExtend)this.inventorySlots).craftMatrix);
     //   this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
      //  this.recipeButton = new GuiButtonImage(10, this.guiLeft + 104, this.height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
      //  this.buttonList.add(this.recipeButton);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    	//this.fontRenderer.drawString(TextFormatting.GREEN + "MRC" + this.tileentity.deviceBalance(), (this.xSize) - 150, 14, 4210752);
    	EntityPlayer player = Minecraft.getMinecraft().player;
    	IInvCapa capa = Minecraft.getMinecraft().player.getCapability(InvProvider.EXTRAINV, null);
    	this.fontRenderer.drawString(TextFormatting.YELLOW + "Pockets", 135, 72, 4210752);
    	if(capa.getStackInSlot(4).getItem() instanceof BackpackItem) {
    		this.fontRenderer.drawString(TextFormatting.YELLOW + capa.getStackInSlot(4).getDisplayName(),  135, 100, 4210752);
    	}
    	
    	if(capa.getStackInSlot(5).getItem() instanceof RigItem) {
    		this.fontRenderer.drawString(TextFormatting.YELLOW + capa.getStackInSlot(5).getDisplayName(), 135, -32, 4210752);
    	}
    	//drawString(fontRenderer, TextFormatting.GREEN + "Weight: ", (this.xSize / 2 - 10) + 7, this.adjY+0, 4210752);
    //	drawScaledString(fontRenderer, TextFormatting.GREEN + "" + player.getCapability(ConsciousProvider.CONSCIOUS, null).getWeight(), 80, 150, 4210752, 0.65F);
    //	drawScaledString(fontRenderer, TextFormatting.GREEN + "/90.5kg", 95, 150, 4210752, 0.6F);
    	//this.fontRenderer.drawString(TextFormatting.GREEN + "/90.5kg", 60, 30, 4210752);
    	
        //this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
    }

    public int previousBlood = 0;
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	if(!this.isOverlay) {
    		this.drawDefaultBackground();
    	}
        
        /*
        this.hasActivePotionEffects = !this.recipeBookGui.isVisible();

        if (this.recipeBookGui.isVisible() && this.widthTooNarrow)
        {
            this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, false, partialTicks);
        }
		*/
    	
    	int curBlood = Minecraft.getMinecraft().player.getCapability(ConsciousProvider.CONSCIOUS, null).getBlood();
     
    	double interp = AnimationPlayTool.lerp(previousBlood, curBlood, Minecraft.getMinecraft().getRenderPartialTicks());
    	
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
       // this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
        double yOffset = 25;
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        IConscious capa = Minecraft.getMinecraft().player.getCapability(ConsciousProvider.CONSCIOUS, null);
        
        GUItil.initializeMultisample();
        GUItil.renderProgressBar(Color.PINK, "Health", this.guiLeft+8, this.guiTop+92+yOffset, 55, 6, Minecraft.getMinecraft().player.getHealth(), 20);
        GUItil.renderProgressBar(Color.blue.brighter().brighter(), "Thirst", this.guiLeft+8, this.guiTop+110+yOffset, 55, 6, Minecraft.getMinecraft().player.getCapability(ConsciousProvider.CONSCIOUS, null).getWaterLevel(), 20);
        
        GUItil.renderProgressBar(new Color(0xdc143c).darker(), "Blood", this.guiLeft+8, this.guiTop+119+yOffset, 55, 6, interp, 36000);
         GUItil.renderProgressBar(Color.GREEN.brighter().brighter(), "Hunger", this.guiLeft+8, this.guiTop+101+yOffset, 55, 6, Minecraft.getMinecraft().player.getFoodStats().getFoodLevel(), 20);
        
         
         if(capa.getLegHealth() != ConsciousCapability.MAX_LEG_HEALTH) {
        	 if(capa.hasSplint()) {
        		 GUItil.renderHalfCircle(Color.gray, 0.5f, this.guiLeft + 15, this.guiTop + 101, 13, 11, 90, 90+360);
         		
        		 GUItil.renderHalfCircle(Color.RED, 1.0f, this.guiLeft + 15, this.guiTop + 101, 13, 11, 90, 90+360*(capa.getLegHealth()/(double) ConsciousCapability.MAX_LEG_HEALTH));
        		 IconSheet.getIcon(BankingGUI.BANKING_ICONS, 256, 32, 3).render(this.guiLeft+15, this.guiTop+101, 0.3f);
                 
        	 } else {
        		 
        		 IconSheet.getIcon(BankingGUI.BANKING_ICONS, 256, 32, 2).render(this.guiLeft+15, this.guiTop+101, 0.3f);
                 
        	 }
        	
         }
         
         GUItil.unapplyMultisample();
        this.previousBlood = curBlood;
        //GUItil.renderProgressBar(Color.RED.darker(), this.guiLeft+8, this.guiTop+92, 55, 10, Minecraft.getMinecraft().player.getCapability(ConsciousProvider.CONSCIOUS, null).getBlood(), 36000);
        
        GlStateManager.color(1.0f, 1.0f, 1.0f); 
      
    }
    
    

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        //System.out.println(scaledresolution.getScaledWidth());
        float x = 0 / 255.0F * scaledresolution.getScaledWidth();
        
        //System.out.println(this.guiLeft + " | " + this.guiTop);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.adjX = i;
        this.adjY = j;
        
        //int x = Minecraft.getMinecraft().displayWidth;
        //int y = Minecraft.getMinecraft().displayHeight;
        //float ratio = (float) y/(float) x;
        //ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        //System.out.println(scaledresolution.getScaledHeight());
        
        
        GlStateManager.pushMatrix();
        //GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        //GlStateManager.scale(1.36F, 1.36F, 1.36F);
        this.mc.getTextureManager().bindTexture(GRAY_IMAGE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        
        this.drawTexturedModalRect(i,j, 0, 0, 120, 160);
        //drawRect((int) x, 0, 125, 250, 282828);
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        //this.drawTexturedModalRect(i-155,j, 0, 0, 125, 187);
        
        // test: this.drawBackRect(0, 0, 0, 0, 125, 187, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        this.drawTexturedModalRect(i,j, 0, 0, 125, 160);
        //this.drawTexturedModalRectBack(x, 0, 0, 0, 125, 187, (int) (scaledresolution.getScaledHeight()*0.66844919786), scaledresolution.getScaledHeight());
        
        //GlStateManager.scale(1.0F, 1.0F, 1.0F);
        
        //GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
        
        /*
        GL11.glPushMatrix();
        drawTexturedModalRectScaled(0, 0, 0, 0, this.xSize, this.ySize, 1.0F);
        GL11.glPopMatrix();
        */
        drawEntityOnScreen((int) (i+33), j + 80, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.player);
        
        /*
        for(int f = 0; f < 6; ++f) {
			this.addSlotToContainer(new Slot(playerInv, 2+x, 135 + x * 18, 120));
			
		} */
        
        /*
        this.mc.getTextureManager().bindTexture(BACKPACK_SLOT);
        for(int r = 0; r < this.inventorySlots.inventorySlots.size(); r++) {
        	Slot slot = this.inventorySlots.inventorySlots.get(r);
        	this.drawTexturedModalRect(slot.xPos, slot.yPos, 0, 0, 18, 18);
        	
        }
        */
        
    
    }

    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
     * pointY
     */
    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY)
    {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	/*
        if (!this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton))
        {
            if (!this.widthTooNarrow || !this.recipeBookGui.isVisible())
            {
                super.mouseClicked(mouseX, mouseY, mouseButton);
            }
        } */
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.buttonClicked)
        {
            this.buttonClicked = false;
        }
        else
        {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_)
    {
        boolean flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + this.xSize || p_193983_2_ >= p_193983_4_ + this.ySize;
        return flag;
        // return this.recipeBookGui.hasClickedOutside(p_193983_1_, p_193983_2_, this.guiLeft, this.guiTop, this.xSize, this.ySize) && flag;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 10)
        {
           // this.recipeBookGui.initVisuals(this.widthTooNarrow, ((ContainerPlayer)this.inventorySlots).craftMatrix);
          //  this.recipeBookGui.toggleVisibility();
           // this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            //this.recipeButton.setPosition(this.guiLeft + 104, this.height / 2 - 22);
           // this.buttonClicked = true;
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    	super.keyTyped(typedChar, keyCode);
    	/*
        if (!this.recipeBookGui.keyPressed(typedChar, keyCode))
        {
            super.keyTyped(typedChar, keyCode);
        }
        */
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        //this.recipeBookGui.slotClicked(slotIn);
    }

    /*
    public void recipesUpdated()
    {
        this.recipeBookGui.recipesUpdated();
    }
    */

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
    	mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.INVCLOSE, 1.0F));
       // this.recipeBookGui.removed();
        super.onGuiClosed();
    }
    
    

	@Override
	public void recipesUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GuiRecipeBook func_194310_f() {
		// TODO Auto-generated method stub
		return null;
	}

    /*
    public GuiRecipeBook func_194310_f()
    {
        return this.recipeBookGui;
    }
    */
}