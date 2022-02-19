package com.vicmatskiv.weaponlib.render;

import com.vicmatskiv.weaponlib.compatibility.CompatibleShellRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class ModernUtil {
	
	public static void setupLighting(Vec3d position) {
		GlStateManager.enableLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		CompatibleShellRenderer.setupLightmapCoords(Minecraft.getMinecraft().player.getPositionVector().addVector(0, 1, 0));
		
	}
	
	public static void destructLighting(Vec3d position) {
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GlStateManager.disableLighting();
	}

}
