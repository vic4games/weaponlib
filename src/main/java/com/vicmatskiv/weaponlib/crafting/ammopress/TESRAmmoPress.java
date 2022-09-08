package com.vicmatskiv.weaponlib.crafting.ammopress;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.crafting.ammopress.model.AmmoPress;
import com.vicmatskiv.weaponlib.crafting.base.TESRStation;
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;
import com.vicmatskiv.weaponlib.crafting.workbench.TESRWorkbench;
import com.vicmatskiv.weaponlib.numerical.LerpedValue;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRAmmoPress extends TESRStation<TileEntityAmmoPress> {

	public TESRAmmoPress(ModelBase model, ResourceLocation loc) {
		super(model, loc);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void render(TileEntityAmmoPress te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		
	
		
		//model = new AmmoPress();
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
        
        double interp = InterpolationKit.interpolateValue(te.getPreviousWheelRotation(), te.getCurrentWheelRotation(), Minecraft.getMinecraft().getRenderPartialTicks());
        
      
        model.render((Entity)null, (float) interp, 0f, 0f, 0f, 0f, 0.00625f);
        GlStateManager.disableRescaleNormal();
      //  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
	}

}
