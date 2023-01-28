package com.jimholden.conomy.entity.render;

import org.lwjgl.util.vector.Matrix4f;

import com.jimholden.conomy.entity.EntityRemountRope;
import com.jimholden.conomy.entity.EntityRope;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRemount extends Render<EntityRemountRope> {

	public RenderRemount(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityRemountRope entity, double x, double y, double z, float entityYaw, float partialTicks) {}

	@Override
	public boolean shouldRender(EntityRemountRope livingEntity, ICamera camera, double camX, double camY, double camZ) {
		return true;
	}
	
	
	@Override
	protected ResourceLocation getEntityTexture(EntityRemountRope entity) {
		return null;
	}
	
}
