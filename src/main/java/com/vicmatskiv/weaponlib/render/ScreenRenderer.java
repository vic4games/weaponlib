package com.vicmatskiv.weaponlib.render;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.model.CameraModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class ScreenRenderer {
	
	public static void renderModelOnScreen(double posX, double posY, double posZ, int scale, double yaw, double pitch, double roll, ModelBase model, ResourceLocation loc)
    {
		
	
		
		//float bruh = (float) ((Minecraft.getMinecraft().player.ticksExisted%200)/200.0);
        float scalef = 0.0625f;
		
        GlStateManager.enableColorMaterial();
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        
        
        
        GlStateManager.translate((float)posX, (float)posY, (float) posZ);
        
        /*
        GlStateManager.rotate((float) roll, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate((float) pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate((float) yaw, 0.0f, 1.0f, 0.0f);
        */
        
        GL11.glScaled(scalef, scalef, scalef);
       
        
       
        
        
        
       RenderHelper.enableGUIStandardItemLighting();
   

        
        Minecraft.getMinecraft().renderEngine.bindTexture(loc);
       
        
        model.render(null, (float) roll, (float) pitch, (float) yaw, 0.0f, 0.0f, scale);
        

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableCull();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

}
