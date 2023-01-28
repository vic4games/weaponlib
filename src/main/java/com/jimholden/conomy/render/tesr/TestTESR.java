package com.jimholden.conomy.render.tesr;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.blocks.ATMBlock;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.items.HeadsetItem;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.util.ModelGeometryTool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class TestTESR extends TileEntitySpecialRenderer<TileEntityATM> {

	private HeadsetModel model = new HeadsetModel();
	private int tick = 0;

	
	@Override
	public void render(TileEntityATM te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		//if(1+1==2) return;
		GL11.glPushMatrix();
		
		
		
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(-0.38F, -0.42F, -0.05F);

	//	Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/creeper.json"));
		ItemStack stack = te.getStackInSlot(Minecraft.getMinecraft().player.getUniqueID());
		renderItem(te, stack);
		 
		//Minecraft.getMinecraft().entityRenderer.stopUseShader();
		

		GL11.glPopMatrix();

	}
	
	private void renderItem(TileEntityATM atm, ItemStack stack) {
        if (!stack.isEmpty()) {
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableLighting();
            GlStateManager.pushMatrix();
            // Translate to the center of the block and .9 points higher
            
            Item i = stack.getItem();
          //  Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/creeper.json"));
            if(i instanceof OpenDimeBase) {
            	GlStateManager.translate(.5, .9, .5);
                GlStateManager.scale(.7f, .7f, .7f);

                GL11.glRotated(245, 0.0, 0.0, 1.0);
                
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);

            } else if(i instanceof LedgerBase) {
            	GlStateManager.translate(.5, .75, .525);
                GlStateManager.scale(1f, 1f, 1f);

                GL11.glRotated(-90, 0.0, 1.0, 0.0);
                GL11.glRotated(-45, 1.0, 0.0, 0.0);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);

            }
          //  Minecraft.getMinecraft().entityRenderer.stopUseShader();
            
            
            GlStateManager.popMatrix();
        }
    }
	
}
