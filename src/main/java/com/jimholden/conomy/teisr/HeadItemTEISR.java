package com.jimholden.conomy.teisr;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.items.HeadsetItem;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HeadItemTEISR extends TEISRBase {
	
	public static final HeadItemTEISR INSTANCE = new HeadItemTEISR();
	

	
	public ModelBiped model;
	
	public HeadItemTEISR(){
	}
	
	public HeadItemTEISR(ModelBiped model) {
		this.model = model;
	}
	
	@Override
	public void renderByItem(ItemStack stack) {
		//GlStateManager.pushMatrix();
		GlStateManager.pushMatrix();
		//System.out.println("yooo");
		//GlStateManager.enableLighting();
		GL11.glTranslated(0.5, 0.5, 0.5);
		
		//RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().renderEngine.bindTexture(((ICAModel) stack.getItem()).getTex());
		
		/*"display": {
		"thirdperson_righthand": {
			"rotation": [98, 0, 0],
			"translation": [4.5, -5.25, -13],
			"scale": [0.55, 0.55, 0.55]
		},
		"thirdperson_lefthand": {
			"rotation": [98, 0, 0],
			"translation": [4.5, -5.25, -13],
			"scale": [0.55, 0.55, 0.55]
		},
		"firstperson_righthand": {
			"rotation": [0, 45, 0],
			"translation": [0, -1.5, 8],
			"scale": [0.4, 0.4, 0.4]
		},
		"firstperson_lefthand": {
			"rotation": [0, -90, 25],
			"translation": [1.13, 3.2, 1.13],
			"scale": [0.68, 0.68, 0.68]
		},
		"ground": {
			"translation": [0, 2, 0],
			"scale": [0.5, 0.5, 0.5]
		},
		"gui": {
			"rotation": [30, 225, 0],
			"translation": [-25.5, 7, 0],
			"scale": [0.625, 0.625, 0.625]
		},
		"head": {
			"rotation": [0, 180, 0],
			"translation": [0, 13, 7]
		},
		"fixed": {
			"rotation": [0, 180, 0]
		}
		*/
		
		
		switch(type){
		case FIRST_PERSON_LEFT_HAND:
		case FIRST_PERSON_RIGHT_HAND:
			GL11.glTranslated(-0.5, 0, 0.3);
			GL11.glRotated(0, 0, 1, 0);
			GL11.glRotated(45, 1, 1, 0);
			break;
		case THIRD_PERSON_LEFT_HAND:
		case THIRD_PERSON_RIGHT_HAND:
			GL11.glTranslated(0.0, 0.5, 0.0);
			GL11.glRotated(0, 0, 1, 0);
			GL11.glRotated(180, 1, 0, 0);
			break;
		case HEAD:
		case FIXED:
		case GROUND:
			GL11.glTranslated(0, 0.675, 0);
			GL11.glScaled(0.9, 0.9, 0.9);
			GL11.glRotated(0, 1, 0, 0);
			GL11.glRotated(0, 0, 1, 0);
			GL11.glRotated(180, 0, 0, 1);
			break;
		case GUI:
		//	System.out.println("test");
			//GlStateManager.enableLighting();
			GL11.glTranslated(0.675, 0, 0);
			GL11.glScaled(1.3, 1.3, 1.3);
			GL11.glRotated(30, 1, 0, 0);
			GL11.glRotated(5, 0, 1, 0);
			GL11.glRotated(90, 0, 0, 1);
			//GlStateManager.disableLighting();
			//GL11.glScaled(0.625, 0.625, 0.625);
			
			
			/*
			GL11.glTranslated(-25.5, 7, 0);
			GL11.glRotated(30, 1, 0, 0);
			GL11.glRotated(225, 0, 1, 0);
			GL11.glScaled(0.625, 0.625, 0.625);
			*/
			//GL11.glRotated(1, 0, 0, 1);
			

			/*
			GL11.glTranslated(0, 0, 0);
			GL11.glRotated(0, 0, 1, 0);
			GL11.glRotated(30, 1, 0, 0);
			*/
			break;
		case NONE:
			break;
		}
		
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableLighting();
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.disableLighting();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		//RenderHelper.disableStandardItemLighting();
		//GlStateManager
		GlStateManager.popMatrix();
		
		//GlStateManager.popMatrix();
		//
	}
	
	
	
}