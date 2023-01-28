package com.jimholden.conomy.render.tesr;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityKeyDoor;
import com.jimholden.conomy.blocks.tileentity.TileEntityPistolStand;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.HeadsetItem;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.render.models.KeycardDoor;
import com.jimholden.conomy.util.ModelGeometryTool;
import com.jimholden.conomy.util.Reference;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class SlidingDoorTESR extends TileEntitySpecialRenderer<TileEntityKeyDoor> {
	
	
	public static KeycardDoor model = new KeycardDoor();
	public static ResourceLocation texture = new ResourceLocation(Reference.MOD_ID + ":textures/blocks/keycardoor.png");

	//private HeadsetModel model = new HeadsetModel();
	private int tick = 0;

	
	
	
	@Override
	public void render(TileEntityKeyDoor te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		
		
		
		
		
		
		if(te.getDoorHalf() == EnumDoorHalf.UPPER) return;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		
		
	
		double d = te.getTime()/(double) 500.0;
		double g = 0.0;
		if(te.getDoorState()) {
			g = Math.max(-0.9, -1.0*d);
		} else {
			g = Math.min(0.0, 1.0*d-1);
		}
		
		
			
		EnumFacing facing = te.getDoorFacing();
	
		switch(facing) {
		case EAST:
			GL11.glTranslated(0.0, 0.0, g);
			GL11.glTranslated(0.075, 0.5, 1.0);
			GL11.glRotated(-90, 0, 1, 0);
			
			break;
		case NORTH:
			GL11.glTranslated(1.0, 0.5, 0.925);
			GL11.glRotated(0, 0, 1, 0);
			GL11.glTranslated(g, 0.0, 0.0);
			break;
		case SOUTH:
			GL11.glTranslated(0.0, 0.5, 0.075);
			GL11.glRotated(-180, 0, 1, 0);
			GL11.glTranslated(-g, 0.0, 0.0);
			break;
		case WEST:
			GL11.glTranslated(0.0, 0.0, g);
			GL11.glTranslated(0.925, 0.5, 0.0);
			GL11.glRotated(90, 0, 1, 0);
			
			break;
		}
		
		
		
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.render(null, 0.0f,  0.0f,  0.0f,  0.0f,  0.0f, 0.0625f);


		GL11.glPopMatrix();
	}
	
	private void renderItem(TileEntityKeyDoor atm, ItemStack stack) {
        if (!stack.isEmpty()) {
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableLighting();
            GlStateManager.pushMatrix();
            // Translate to the center of the block and .9 points higher
            GlStateManager.translate(.5, .3, .5);
            GlStateManager.scale(1.0f, 1.0f, 1.0f);
            GlStateManager.rotate(-8, 1, 0, 0);
            //GlStateManager.rotate(90, 0, 1, 0);
            //atm.getBlockType().getBlockState().getBaseState().getValue(BlockHorizontal.FACING);
          //  atm.getBlockType().getStateFromMeta(atm.getBlockMetadata()).getValue(BlockHorizontal.FACING);
            //
            
            //GlStateManager.rotate(90, x, y, z);
            

            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);

            GlStateManager.popMatrix();
        }
    }
	
}
