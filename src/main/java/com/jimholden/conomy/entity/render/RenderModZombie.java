package com.jimholden.conomy.entity.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.entity.models.GhoulModel;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.shaders.Shader;
import com.jimholden.conomy.shaders.ShaderManager;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.RopeUtil;
import com.jimholden.conomy.util.VectorUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RenderModZombie extends RenderLiving<EntityLiving> {
	// https://www.shadertoy.com/view/tlfSRS
	
	public static Shader ZOMBIE_DECAY = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/zombie"));
	
	
	public static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":" + "textures/entity/ghoul.png");

	public RenderModZombie(RenderManager manager) {
		super(manager, new GhoulModel(), 0.5F);
	}
	
	public RenderModZombie(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
		super(rendermanagerIn, new GhoulModel(), shadowsizeIn);
		// TODO Auto-generated constructor stub
	}
	


	
	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
		/*
		
		
		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 0);
		Minecraft mc = Minecraft.getMinecraft();
		//System.out.println(startVec + " | " + endVec);
		Vec3d pVec = VectorUtil.ptI(mc.player);
		GL11.glTranslated(-pVec.x, -pVec.y, -pVec.z);
		//GL11.glTranslatef(0.5f, 1.0f, 0.5f);

		GL11.glPushMatrix();
		GL11.glPopMatrix();
		//GL11.glTranslatef(0, -0.5F, 0);
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0, 0, 0);
		
		EntityBaseZombie zombie2 = (EntityBaseZombie) entity;
		
		Vec3d startVec = zombie2.getPositionVector().addVector(0, zombie2.getEyeHeight(), 0);
		Vec3d endVec = zombie2.getPositionVector().addVector(0, zombie2.getEyeHeight(), 0).add(zombie2.getLookVec());
		
		float pitch = -zombie2.rotationPitch;
		float yaw = -zombie2.rotationYaw;
		Vec3d pY = Vec3d.fromPitchYaw(pitch, yaw+90).add(startVec);
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(startVec.x, startVec.y, startVec.z);
		GL11.glVertex3d(pY.x, pY.y, pY.z);
		GL11.glEnd();
		
		
		Vec3d sv2 = mc.player.getPositionEyes(1.0F);
		Vec3d ev2 = startVec.add(mc.player.getLookVec().scale(3));
		
		
		
		Vec3d sv3 = RopeUtil.rotateVector(startVec, zombie2.getPositionVector(), pitch, yaw);
		Vec3d ev3 = RopeUtil.rotateVector(endVec, zombie2.getPositionVector(), pitch, yaw);
		
		
		GlStateManager.color(1.0F, 0.0F, 0.0F);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(sv3.x, sv3.y, sv3.z);
		GL11.glVertex3d(ev3.x, ev3.y, ev3.z);
		GL11.glEnd();
		
		

		GL11.glTranslated(mc.player.posX,mc.player.posY, mc.player.posZ);
		GlStateManager.enableCull();
		GlStateManager.enableTexture2D();
		GL11.glPopMatrix();
		
		*/
		
		
		boolean reload = true;
		if(reload) {
			ZOMBIE_DECAY = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/zombie"));
			
		}
		
		/*
		ZOMBIE_DECAY.use();
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0+3);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID + ":" + "textures/shaders/noise_1.png"));
		GL20.glUniform1i(GL20.glGetUniformLocation(ZOMBIE_DECAY.getShaderId(), "noise"), 3);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		
		int time = GL20.glGetUniformLocation(ZOMBIE_DECAY.getShaderId(), "time");
        int ticks = Minecraft.getMinecraft().player.ticksExisted;
        double rT = ticks + (ticks-(ticks-1))*Minecraft.getMinecraft().getRenderPartialTicks(); 
        GL20.glUniform1f(time, (float) rT/15f);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();*/
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		//ZOMBIE_DECAY.release();
		
		
	//	super.doRender(entity, x, y, z, entityYaw, partialTicks);
		/*
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogf(GL11.GL_FOG_START, 0.01f);
		GL11.glFogf(GL11.GL_FOG_END, 10f);
		GL11.glDisable(GL11.GL_FOG);*/
		
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity) {
		// TODO Auto-generated method stub
		return TEXTURES;
	}
	
	@Override
	protected void applyRotations(EntityLiving entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
		// TODO Auto-generated method stub
		super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
	}

}
