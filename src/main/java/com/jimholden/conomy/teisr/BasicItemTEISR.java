package com.jimholden.conomy.teisr;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.items.HeadsetItem;
import com.jimholden.conomy.items.models.ICAModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BasicItemTEISR extends TEISRBase {
	
	public static final BasicItemTEISR INSTANCE = new BasicItemTEISR();
	
	public static final ResourceLocation HOOK_TEX = new ResourceLocation(Reference.MOD_ID + ":" + "textures/entity/climbinganchor.png");

	
	public ModelBase model;
	
	public BasicItemTEISR(){
	}
	
	public BasicItemTEISR(ModelBase model) {
		this.model = model;
	}
	
	@Override
	public void renderByItem(ItemStack stack) {
		//System.out.println("sup homie");
		GlStateManager.pushMatrix();
		GlStateManager.enableLighting();
		GL11.glTranslated(0.5, 0.5, 0.5);
		Minecraft.getMinecraft().renderEngine.bindTexture(HOOK_TEX);
		
		switch(type){
		case FIRST_PERSON_LEFT_HAND:
		case FIRST_PERSON_RIGHT_HAND:
			GL11.glTranslated(1.13, 5.45, 0.88);
			//GL11.glRotated(0, 0, 1, 0);
			//GL11.glRotated(45, 1, 0, 0);
			break;
		case THIRD_PERSON_LEFT_HAND:
			GL11.glTranslated(0.0, 1.4, 0);
			GL11.glRotated(180, 0, 0, 1);
			break;
		case THIRD_PERSON_RIGHT_HAND:
			GL11.glTranslated(0.0, 1.4, 0);
			GL11.glRotated(180, 0, 0, 1);
			break;
		case HEAD:
		case FIXED:
		case GROUND:
			GL11.glTranslated(0, 1.0, 0);
			GL11.glScaled(1.0, 1.0, 1.0);
			GL11.glRotated(0, 1, 0, 0);
			GL11.glRotated(0, 0, 1, 0);
			GL11.glRotated(180, 0, 0, 1);
			break;
		case GUI:
			GlStateManager.enableLighting();
			GL11.glTranslated(2.10, 0.5, -0.03);
			GL11.glScaled(1.5, 1.5, 1.5);
			GL11.glRotated(45, 1, 0, 0);
			GL11.glRotated(5, 0, 1, 0);
			GL11.glRotated(90, 0, 0, 1);
			GlStateManager.disableLighting();
			break;
		case NONE:
			break;
		}
		
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableLighting();
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.disableLighting();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}
	
	
	
}