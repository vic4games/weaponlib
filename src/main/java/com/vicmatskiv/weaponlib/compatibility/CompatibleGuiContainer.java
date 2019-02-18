package com.vicmatskiv.weaponlib.compatibility;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;

public abstract class CompatibleGuiContainer extends GuiContainer {

    public CompatibleGuiContainer(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }
    
    public static void renderEntityWithPosYaw(EntityLivingBase entity, double x, double y, double z, float yaw,
            float partialTicks) {
        Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, x, y, z, yaw, partialTicks, true);
    }

    protected static void setPlayerViewY(float f) {
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
    }
    
    @Override
    protected final void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        compatibleMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void compatibleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
