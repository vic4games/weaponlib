package com.vicmatskiv.weaponlib.crafting.workbench;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTileEntity;
import com.vicmatskiv.weaponlib.crafting.base.TESRStation;
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRWorkbench extends TESRStation<TileEntityWorkbench> {



	
	public TESRWorkbench(ModelBase model, ResourceLocation location) {
		super(model, location);
	}

	@Override
	public void render(TileEntityWorkbench te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		
		
		GL11.glPushMatrix();
        this.bindTexture(this.location);
      
      //  GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GlStateManager.enableTexture2D();
       // GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x + 0.5f, (float)y + 1.5f, (float)z + 0.5f);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        
       GlStateManager.rotate(180f + 90f * ((TileEntityStation) te).getSide(), 0, 1, 0);
        GlStateManager.scale(10, 10, 10);
      
        model.render((Entity)null, 0f, 0f, 0f, 0f, 0f, 0.00625f);
        GlStateManager.disableRescaleNormal();
      //  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
	}
	
	
	
}
