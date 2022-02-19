package com.vicmatskiv.weaponlib.compatibility;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class CompatibleShellRenderer {
	
	

	private static final Minecraft mc = Minecraft.getMinecraft();
	
	

	private static int shadowDisplayList = -1;
	
	
	
	/**
	 * Sets the lightmap texture coordinates
	 */
	public static void setupLightmapCoords(Vec3d position) {
		int i = Minecraft.getMinecraft().world.getCombinedLight(new BlockPos(position.x, position.y, position.z), 0);
		float f = (float) (i & 65535);
		float f1 = (float) (i >> 16);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);

	}

	public static void render(ArrayList<Shell> shells) {
		
		
		
		GlStateManager.pushMatrix();

		GlStateManager.enableTexture2D();

		// Interpolate player's position for rendering
		EntityPlayerSP pla = mc.player;

		float interpX = (float) MatrixHelper.solveLerp(pla.prevPosX, pla.posX,
				Minecraft.getMinecraft().getRenderPartialTicks());
		float interpY = (float) MatrixHelper.solveLerp(pla.prevPosY, pla.posY,
				Minecraft.getMinecraft().getRenderPartialTicks());
		float interpZ = (float) MatrixHelper.solveLerp(pla.prevPosZ, pla.posZ,
				Minecraft.getMinecraft().getRenderPartialTicks());
		GlStateManager.translate(-interpX, -interpY, -interpZ);
		
		
		
		GlStateManager.enableCull();
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().entityRenderer.enableLightmap();

		
	
		for (Shell sh : shells) {

			GlStateManager.pushMatrix();
			
			// setup lightmap
			setupLightmapCoords(sh.pos);
			
			
			// interpolate pos & rot
			Vec3d iP = MatrixHelper.lerpVectors(sh.prevPos, sh.pos, Minecraft.getMinecraft().getRenderPartialTicks());
			Vec3d iR = MatrixHelper.lerpVectors(sh.prevRot, sh.rot, Minecraft.getMinecraft().getRenderPartialTicks());
			
			// translate last
			GlStateManager.translate(iP.x, iP.y, iP.z);

			
			if(sh.getHeight() > -3) {
				
				
				GlStateManager.enableTexture2D();
				ResourceLocation shadow = new ResourceLocation("textures/misc/shadow.png");
				Minecraft.getMinecraft().getTextureManager().bindTexture(shadow);

				GlStateManager.enableBlend();
				Tessellator t = Tessellator.getInstance();
				BufferBuilder bb = t.getBuffer();
				bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				double yOff = sh.getHeight();
				if (yOff != 0) {
					yOff += 0.01;
				} else {
					yOff -= 0.03;
				}

				double shadowSize = Math.max(0.05 * (-yOff * 5), 0.06);

				GlStateManager.color(0, 0, 0, (float) (1f - shadowSize) * 0.5f);

				bb.pos(1 * shadowSize, yOff, 1 * shadowSize).tex(0, 0).endVertex();
				bb.pos(1 * shadowSize, yOff, -1 * shadowSize).tex(1, 0).endVertex();
				bb.pos(-1 * shadowSize, yOff, -1 * shadowSize).tex(1, 1).endVertex();
				bb.pos(-1 * shadowSize, yOff, 1 * shadowSize).tex(0, 1).endVertex();

				t.draw();
				
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.disableBlend();
				
			}
			
		

			Minecraft.getMinecraft().getTextureManager()
					.bindTexture(new ResourceLocation("mw:textures/entity/boo6.png"));

			GlStateManager.rotate((float) iR.x, 1, 0, 0);
			GlStateManager.rotate((float) iR.y, 0, 1, 0);
			GlStateManager.rotate((float) iR.z, 0, 0, 1);
			//GlStateManager.scale(0.1, 0.1, 0.01);
			
			CompatibleClientEventHandler.bulletShell.render();
			
			//ShellParticleSimulator.bulletModel.render(null, 0f, 0f, 0f, 0f, 0f, 1f);
			
			
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.popMatrix();
			
			
			

			

		}
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GlStateManager.popMatrix();

	}
}
