package com.jimholden.conomy.entity.render;

import com.jimholden.conomy.entity.EntityTestVes;
import com.jimholden.conomy.entity.models.TestCar;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderVes extends RenderBoat {

	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/entity/audi.texture.png");
	public static TestCar carModel = new TestCar();
	
	public RenderVes(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	/*
	@Override
	public void doRender(EntityTestVes entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		//GlStateManager.scale(0.6, 0.6, 0.6);
        
        //GlStateManager.rotate(-(180+entity.rotationYaw), 0, 1, 0);
        //GlStateManager.rotate(180, 0, 0, 1);
        this.setupTranslation(x+1.25, y+0.3F, z+1.5);
        this.setupRotation(entity, entityYaw, partialTicks);
        
        GlStateManager.scale(0.6, 0.6, 0.6);
        
        this.bindEntityTexture(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }
      // carModel = new TestCar();
        this.carModel.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	*/
	


}
