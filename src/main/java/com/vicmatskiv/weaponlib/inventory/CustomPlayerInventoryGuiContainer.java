package com.vicmatskiv.weaponlib.inventory;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.vicmatskiv.weaponlib.compatibility.CompatibleGuiContainer;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
public class CustomPlayerInventoryGuiContainer extends CompatibleGuiContainer {
    /**
     * x size of the inventory window in pixels. Defined as float, passed as int
     */
    private float xSize_lo;

    /**
     * y size of the inventory window in pixels. Defined as float, passed as
     * int.
     */
    private float ySize_lo;

    /**
     * Normally I use '(ModInfo.MOD_ID, "textures/...")', but it can be done
     * this way as well
     */
    private static final ResourceLocation iconLocation = new ResourceLocation("mw", "textures/gui/custom_inventory.png");


    /**
     * Could use IInventory type to be more generic, but this way will save an
     * import...
     */
    private final CustomPlayerInventory inventory;

    public CustomPlayerInventoryGuiContainer(EntityPlayer player, InventoryPlayer inventoryPlayer, CustomPlayerInventory inventoryCustom) {
        super(new CustomPlayerInventoryContainer(player, inventoryPlayer, inventoryCustom));
        this.inventory = inventoryCustom;
        // if you need the player for something later on, store it in a local
        // variable here as well
    }
    
    @Override
    public void initGui() {
        // TODO Auto-generated method stub
        super.initGui();
        
        int cornerX = guiLeft;
        int cornerY = guiTop;
        this.buttonList.clear();
        
        InventoryTabs iventoryTabs = InventoryTabs.getInstance();
        iventoryTabs.updateTabValues(cornerX, cornerY, CustomPlayerInventoryTab.class);
        iventoryTabs.addTabsToList(this.buttonList);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
        xSize_lo = mouseX;
        ySize_lo = mouseY;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // This method will simply draw inventory names on the screen - you
        // could do without it entirely
        // if that's not important to you, since we are overriding the default
        // inventory rather than
        // creating a specific type of inventory

//        String s = this.inventory.hasCustomInventoryName() ? this.inventory.getInventoryName()
//                : I18n.getString(this.inventory.getInventoryName());
//        // with the name "Custom Inventory", the 'Cu' will be drawn in the first
//        // slot
//        fontRendererObj.drawString(s, this.xSize - fontRendererObj.getStringWidth(s), 12, 4210752);
//        // this just adds "Inventory" above the player's inventory below
//        fontRendererObj.drawString(I18n.getString("container.inventory"), 80, this.ySize - 96, 4210752);
    
//        String s = StatCollector.translateToLocal(inventory.getInventoryName());
//        fontRendererObj.drawString(s, this.xSize / this.fontRendererObj.getStringWidth(s) / 2, 0, 4210752);
//        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 26, this.ySize - 96 + 4, 4210752);
   
//        String s = inventory.hasCustomInventoryName() ? inventory.getInventoryName() : I18n.format(inventory.getInventoryName());
//        // with the name "Custom Inventory", the 'Cu' will be drawn in the first slot
//        fontRendererObj.drawString(s, xSize - fontRendererObj.getStringWidth(s), 12, 4210752);
//        // this just adds "Inventory" above the player's inventory below
//        fontRendererObj.drawString(I18n.format("container.inventory"), 80, ySize - 96, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(iconLocation);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawPlayerModel(guiLeft + 51, guiTop + 75, 30, guiLeft + 51 - xSize_lo, guiTop + 25 - ySize_lo, compatibility.clientPlayer());
    }

    /**
     * Copied straight out of vanilla - renders the player model on screen
     */
    public static void drawPlayerModel(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 50.0F);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan(pitch / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = (float) Math.atan(yaw / 40.0F) * 20.0F;
        entity.rotationYaw = (float) Math.atan(yaw / 40.0F) * 40.0F;
        entity.rotationPitch = -((float) Math.atan(pitch / 40.0F)) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, (float)compatibility.getEntityYOffset(entity), 0.0F);
        
        setPlayerViewY(180f);
        renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static ThreadLocal<CustomPlayerInventoryGuiContainer> threadLocalGuiContainer = new ThreadLocal<>();
    
    @Override
    protected void compatibleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        try {
            threadLocalGuiContainer.set(this);
            super.compatibleMouseClicked(mouseX, mouseY, mouseButton);
        } finally {
            threadLocalGuiContainer.remove();
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        try {
            threadLocalGuiContainer.set(this);
            super.mouseReleased(mouseX, mouseY, state);
        } finally {
            threadLocalGuiContainer.remove();
        }
    }
    
    protected static CustomPlayerInventoryGuiContainer getClickedGuiContainer() { 
        return threadLocalGuiContainer.get();
    }
}