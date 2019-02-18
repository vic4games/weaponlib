package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

public class CompatibleRenderItem {
        
    private RenderItem getRenderItem() {
        return Minecraft.getMinecraft().getRenderItem();
    }

    public void setZLevel(float zLevel) {
        getRenderItem().zLevel = zLevel;
    }

    public void renderItemAndEffectIntoGUI(FontRenderer fontRenderer, TextureManager renderEngine, ItemStack itemStack,
            int x, int y) {
        getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
    }

    public void renderItemOverlayIntoGUI(FontRenderer fontRenderer, TextureManager renderEngine, ItemStack itemStack,
            int x, int y) {
        getRenderItem().renderItemOverlays(fontRenderer, itemStack, x, y);
    }

    
}
