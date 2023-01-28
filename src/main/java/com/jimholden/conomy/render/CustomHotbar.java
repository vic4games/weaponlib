package com.jimholden.conomy.render;

import com.jimholden.conomy.client.gui.TimedConsumableTracker;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class CustomHotbar {
	
	public static final Minecraft mc = Minecraft.getMinecraft();
	public static final ResourceLocation MODDED_HOTBAR = new ResourceLocation(Reference.MOD_ID + ":textures/gui/widgetsmodified.png");
	public static float zLevel;
	protected final static RenderItem itemRenderer = mc.getRenderItem();
	public static int prevIndex;
	public static float hotbarTimer;
	public static float downTimer;
	public static float staticTimer;
	
	
	protected static void renderHotbarItem(int p_184044_1_, int p_184044_2_, float p_184044_3_, EntityPlayer player, ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            float f = (float)stack.getAnimationsToGo() - p_184044_3_;

            if (f > 0.0F)
            {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float)(p_184044_1_ + 8), (float)(p_184044_2_ + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(p_184044_1_ + 8)), (float)(-(p_184044_2_ + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(player, stack, p_184044_1_, p_184044_2_);

            if (f > 0.0F)
            {
                GlStateManager.popMatrix();
            }

            itemRenderer.renderItemOverlays(mc.fontRenderer, stack, p_184044_1_, p_184044_2_);
        }
    }
	
	public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }
	
	 public static void renderHotbar(ScaledResolution sr, float partialTicks)
	    {
	        if (mc.getRenderViewEntity() instanceof EntityPlayer)
	        {
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	            mc.getTextureManager().bindTexture(MODDED_HOTBAR);
	            EntityPlayer entityplayer = (EntityPlayer)mc.getRenderViewEntity();
	            ItemStack itemstack = entityplayer.getHeldItemOffhand();
	            EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
	            if(mc.player.inventory.currentItem != prevIndex) {
	            	if(TimedConsumableTracker.isConsuming()) {
	            		TimedConsumableTracker.cancel();
	            	}
	            	prevIndex = mc.player.inventory.currentItem;
	            	downTimer = 0.0F;
	            	staticTimer = 0.0F;
	            	//System.out.println("SLOT CHANGE");
	            }
	            if(staticTimer < 150.0F) {
	            	staticTimer += 1;
	            }
	            if(downTimer < 50.0F && staticTimer == 150.0F) {
	            	downTimer += 0.5F;
	            }
	            int i = sr.getScaledWidth() / 2;
	            float f = zLevel;
	            int j = 182;
	            int k = 91;
	            int d = (int) downTimer;
	            zLevel = -90.0F;
	            drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22 + d, 0, 0, 182, 22);
	            drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1 + d, 0, 22, 24, 22);

	            if (!itemstack.isEmpty())
	            {
	                if (enumhandside == EnumHandSide.LEFT)
	                {
	                    drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23 + d, 24, 22, 29, 24);
	                }
	                else
	                {
	                    drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23 + d, 53, 22, 29, 24);
	                }
	            }

	            zLevel = f;
	            GlStateManager.enableRescaleNormal();
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            RenderHelper.enableGUIStandardItemLighting();

	            for (int l = 0; l < 9; ++l)
	            {
	                int i1 = i - 90 + l * 20 + 2;
	                int j1 = sr.getScaledHeight() - 16 - 3 + d;
	                renderHotbarItem(i1, j1, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l));
	            }

	            if (!itemstack.isEmpty())
	            {
	                int l1 = sr.getScaledHeight() - 16 - 3 + d;

	                if (enumhandside == EnumHandSide.LEFT)
	                {
	                    renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack);
	                }
	                else
	                {
	                    renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack);
	                }
	            }

	            if (mc.gameSettings.attackIndicator == 2)
	            {
	                float f1 = mc.player.getCooledAttackStrength(0.0F);

	                if (f1 < 1.0F)
	                {
	                    int i2 = sr.getScaledHeight() - 20 + d;
	                    int j2 = i + 91 + 6;

	                    if (enumhandside == EnumHandSide.RIGHT)
	                    {
	                        j2 = i - 91 - 22;
	                    }

	                    mc.getTextureManager().bindTexture(Gui.ICONS);
	                    int k1 = (int)(f1 * 19.0F);
	                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	                    drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
	                    drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
	                }
	            }

	            RenderHelper.disableStandardItemLighting();
	            GlStateManager.disableRescaleNormal();
	            GlStateManager.disableBlend();
	        }
	    }

}
