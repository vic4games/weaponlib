package com.vicmatskiv.weaponlib.vehicle;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3d;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityRenderer;
import com.vicmatskiv.weaponlib.compatibility.Interceptors;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;
import com.vicmatskiv.weaponlib.shader.DynamicShaderContext;
import com.vicmatskiv.weaponlib.shader.DynamicShaderGroupManager;
import com.vicmatskiv.weaponlib.vehicle.collisions.GJKResult;
import com.vicmatskiv.weaponlib.vehicle.collisions.OBBCollider;
import com.vicmatskiv.weaponlib.vehicle.collisions.OreintedBB;
import com.vicmatskiv.weaponlib.vehicle.collisions.RigidBody;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.solver.SuspensionSolver;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.solver.WheelSolver;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.stability.InertialStabilizer;
import com.vicmatskiv.weaponlib.vehicle.render.SuspensionModel;

import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.debug.DebugRendererSolidFace;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.client.shader.ShaderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import scala.actors.threadpool.Arrays;
import scala.collection.parallel.ParIterableLike.Min;

public class RenderVehicle2 extends CompatibleEntityRenderer
{
    private static ThreadLocal<Matrix4f> cameraTransformMatrix = new ThreadLocal<>();

	private static ResourceLocation field_110782_f;
	
	private StatefulRenderer<VehicleRenderableState> mainRenderer;
	
	private VehicleRenderableState currentRenderableState;
	
	
	public static Matrix4f tm = new Matrix4f();
	
	
	public static SuspensionSolver susSolve = new SuspensionSolver(271, 1.0);
	
	
	
	public RenderVehicle2(StatefulRenderer<VehicleRenderableState> mainRenderer)
	{
		this.shadowSize = 0.5F;
		this.mainRenderer = mainRenderer;
	}
	
	public RigidBody bruhBody = null;
	
	
	public void rigidBodyTest(EntityVehicle v) {
		try {
			
		
		DebugRenderer.setupBasicRender();
		
		Vec3d playerPos = Minecraft.getMinecraft().player.getPositionVector();
		
		GL11.glTranslated(-playerPos.x, -playerPos.y, -playerPos.z);
		//bruhBody = null;
		
		Vec3d posVec = v.getPositionVector();
		if(this.bruhBody == null) {
			this.bruhBody = new RigidBody(v.world, posVec.x, posVec.y+10, posVec.z);
			OreintedBB obb = new OreintedBB(new AxisAlignedBB(-1, -1, -1, 1, 1, 1));
			obb.setupPhysically(175);
			this.bruhBody.addColliders(obb);
		}
		
		this.bruhBody.minecraftTimestep();
		
		
		Vec3d p = this.bruhBody.position;
		Vec3d adjP = v.getPositionVector().subtract(p);
		GL11.glTranslated(p.x, p.y, p.z);
		this.bruhBody.colliders.get(0).renderOBB();
		
		DebugRenderer.destructBasicRender();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("fuck!");
		}
	}
	
	
	
	public void OBBTest() {
	
		DebugRenderer.setupBasicRender();
		
		GL11.glTranslated(0.0, -5.0, 0.0);
		OreintedBB ob2 = new OreintedBB(new AxisAlignedBB(-1, -1, -1, 1, 1, 1));
		ob2.move(1.0, 2.1, 0.0);
		ob2.rotate(Math.toRadians(0.0), Math.toRadians(0.0), Math.toRadians(45.0));
		
		
		
		
		
		OreintedBB ob = new OreintedBB(new AxisAlignedBB(-1, -1, -1, 1, 1, 1));
		ob.rotate(Math.toRadians(0.0), Math.toRadians(0.0), Math.toRadians(15.0));
		
		// test raytrace
//		/Vec3d o = ob.doRayTrace(new Vec3d(-2, 0, 0), new Vec3d(2, 0, 0)).hitVec;
		
		// test collision
		GJKResult result = OBBCollider.areColliding(ob, ob2);
		if(result.status == GJKResult.Status.COLLIDING) {
			Vec3d sepVec = result.separationVector.scale(result.penetrationDepth);
			ob2.move(sepVec.x, sepVec.y, sepVec.z);
			
			DebugRenderer.renderPoint(result.contactPointA, new Vec3d(0, 0, 1));
			DebugRenderer.renderPoint(result.contactPointB, new Vec3d(0, 0, 1));
			
		}
		
		
		ob.renderOBB();
		ob2.renderOBB();
		
		DebugRenderer.destructBasicRender();
		//System.out.println(OBBCollider.areColliding(ob, ob2));
		
		/*
		
		Vec3d startP = new Vec3d(3, -2, 2);
		
		// check cso support point
		Vec3d supPo = OBBCollider.CSOSupport(ob, ob2, startP);
		
		
		// test support
		Vec3d o2 = ob.support(startP);
		
		
		// test support on sec
		Vec3d o3 = ob2.support(startP);
		
		
		//System.out.println(o2);
		o = o2;
		
		//System.out.println(o);
		
		ob.renderOBB();
		ob2.renderOBB();
		
		
		
		
		GL11.glTranslated(0.0, -5.0, 0.0);
		DebugRenderer.renderPoint(o2, new Vec3d(1.0, 0.0, 0.0));
		DebugRenderer.renderPoint(o3, new Vec3d(0.5, 0.5, 0.5));
		DebugRenderer.renderPoint(startP, new Vec3d(0.0, 1.0, 0.0));
		
		DebugRenderer.renderPoint(supPo, new Vec3d(0.0, 0.0, 1.0));
		
		DebugRenderer.renderLine(Vec3d.ZERO, startP, new Vec3d(0.0, 1.0, 0.0));
	
		
		DebugRenderer.destructBasicRender();
		//GL11.glTranslated(0.0, -5.0, 0.0);
		*/
		
		
	}

	
	
	public void renderPlane(Vec3d[] list) {
		int red = 1;
		int blue = 0;
		int green = 0;
		int alpha = 1;
		DebugRenderer.setupBasicRender();
		DebugRenderer.renderLine(list[0], list[1], new Vec3d(1, 0, 0));
		DebugRenderer.renderLine(list[1], list[3], new Vec3d(1, 0, 0));
		DebugRenderer.renderLine(list[3], list[2], new Vec3d(1, 0, 0));
		DebugRenderer.renderLine(list[0], list[2], new Vec3d(1, 0, 0));
        DebugRenderer.destructBasicRender();
       
	}
	
	
	public void renderVehicle(EntityVehicle entityVehicle, double posX, double posY, double posZ, float rotationYaw, float par9)
	{

		// RENDER CUSTOM BOUNDING BOX
		GL11.glPushMatrix();
		DebugRenderer.setupBasicRender();	
		GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
		if(Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox()) {
			entityVehicle.oreintedBoundingBox.renderOBB();
		}
		DebugRenderer.destructBasicRender();
		GL11.glPopMatrix();
		
		
		
		

		GL11.glPushMatrix();
		invertCameraTransform();
		EntityVehicle v = entityVehicle;
		float pt = Minecraft.getMinecraft().getRenderPartialTicks();
		

		/*
		double preX = v.prevPosX - v.posX;
		double preY = v.prevPosY - v.posY;
		double preZ = v.prevPosZ - v.posZ;
		double newPosX = preX + (v.posX-preX)*pt;
		double newPosY = preY + (v.posY-preY)*pt;
		double newPosZ = preZ + (v.posZ-preZ)*pt;
		
		System.out.println(posX + " | " + posY + " | " + posZ);*/
		GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
		
		//GL11.glTranslated(0.0, 0.0, 0.5);
		
		
		
		float muRoll = (float) ((1 - Math.cos(Minecraft.getMinecraft().getRenderPartialTicks() * Math.PI)) / 2f);
		float roll = (entityVehicle.prevRotationRollH+entityVehicle.prevRotationRoll) + ((entityVehicle.rotationRoll+entityVehicle.rotationRollH)-(entityVehicle.prevRotationRoll+entityVehicle.prevRotationRollH))*muRoll;
		
		
		
		
		// debug
		GL11.glRotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
		
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) { 
			GL11.glRotatef(roll, 0.0f, 0.0f, 1.0f);
		} else {
			GL11.glRotatef(roll, 0.0f, 0.0f, 1.0f);
		}
		
		
		
		/*
		if(entityVehicle.getSolver().angles != null) {
			GL11.glRotated(-entityVehicle.getSolver().angles[2], 0.0, 0.0, 1.0);
			GL11.glRotated(-entityVehicle.getSolver().angles[0]-90f, 1.0, 0.0, 0.0);
		}
		*/
		
		float mu2 = Minecraft.getMinecraft().getRenderPartialTicks();
		//float mu2 = (float) ((1 - Math.cos(Minecraft.getMinecraft().getRenderPartialTicks() * Math.PI)) / 2f);
		float interpPitch = entityVehicle.prevRotationPitch + (entityVehicle.rotationPitch-entityVehicle.prevRotationPitch)*mu2;
		

		// debug DD
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
			GL11.glRotatef(interpPitch, 1.0F, 0.0F, 0.0F);
		} else {

			GL11.glRotatef(interpPitch, 1.0F, 0.0F, 0.0F);

			
		}
		//GL11.glRotatef(interpPitch, 1.0F, 0.0F, 0.0F);
		

		/* wtf does this even do???
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			double interp = 1.0*(roll/45.0);
			System.out.println("fuck " + interp);
			GL11.glTranslated(interp, 0.0, 0.0);
		}*/ 

		
		
//		Matrix4f cM = MatrixHelper.captureMatrix();
		
		//cM.m33 = 0;
		//MatrixHelper.applyMatrix(cM);
		
		
/*
		if(entityVehicle.getSolver().rotMat != null) {
			
			Matrix3d k = entityVehicle.getSolver().rotMat;
			
			double[] dBuf = new double[] {k.m00, k.m01, k.m02, 0,
					k.m10, k.m11, k.m12, 0,
					k.m20, k.m21, k.m22, 0,
				  0, 0, 0, 0};
			DoubleBuffer db = DoubleBuffer.wrap(dBuf);
			
			
			GL11.glMultMatrix(db);
		
		}*/
		
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {

			//GL11.glTranslated(0.0, (Math.abs(entityVehicle.rotationPitch)/90.0)*2.0, 0.0);

		} else {
			
			if(!entityVehicle.onGround && entityVehicle.rotationPitch > 3) {

				
				//GL11.glTranslated(0.0, -entityVehicle.getInterpolatedLiftOffset(), 0.0);
			}
			
			
		}
		
		//GL11.glRotatef(MathHelper.wrapAngleTo180_float(par1HCEntityMongoose.getRotateWheelSpeed()*100F), 1.0F, 0.0F, 0.0F);
		
		
		
		
		//if(entityVehicle.rotationPitch > 5) {
			Vec3d startLift = InterpolationKit.interpolatedEntityPosition(entityVehicle);
			Vec3d endLift = startLift.subtract(new Vec3d(0, 10, 0).rotatePitch((float) Math.toRadians(entityVehicle.rotationPitch)).rotateYaw((float) Math.toRadians(-rotationYaw)));
			RayTraceResult rtr = entityVehicle.world.rayTraceBlocks(startLift, endLift, false, true, false);
			if(rtr != null) {
				entityVehicle.prevLiftOffset = entityVehicle.liftOffset;
				entityVehicle.liftOffset = (float) rtr.hitVec.subtract(startLift).lengthVector();
				
			}
			
			
			//GL11.glTranslated(0.0, -entityVehicle.getInterpolatedLiftOffset(), 0.0);
		//}
		
		for(Entity pass : entityVehicle.getPassengers()) {
			
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && pass == Minecraft.getMinecraft().player) continue;
			
			GL11.glPushMatrix();
			int i = entityVehicle.getPassengers().indexOf(pass);
	        Vec3d seatOffset = entityVehicle.getConfiguration().getSeatAtIndex(i).getSeatPosition();
	        
			GL11.glTranslated(seatOffset.x, seatOffset.y, seatOffset.z);
			
			if(!(pass instanceof EntityPlayer)) {
				Minecraft.getMinecraft().getRenderManager().doRenderEntity(pass, 0, 0, 0, -pass.rotationYaw, Minecraft.getMinecraft().getRenderPartialTicks(), true);		
			} else {
				EntityPlayer player = (EntityPlayer) pass;
				RenderManager rManager = Minecraft.getMinecraft().getRenderManager();
				Render<Entity> render = rManager.getEntityRenderObject(pass);
				
				

				
				player.rotationYaw += entityVehicle.deltaRotation;
				player.setRotationYawHead(player.getRotationYawHead() + entityVehicle.deltaRotation);
	            

				entityVehicle.applyYawToEntity(player);
				player.limbSwing = 39;
				render.doRender(player, 0, 0, 0, pass.rotationYaw, Minecraft.getMinecraft().getRenderPartialTicks());		
				player.limbSwing = 89;
				
			}
			
			
			
			//Minecraft.getMinecraft().getRenderManager().doRenderEntity(pass, 0, 0, 0, -pass.rotationYaw, Minecraft.getMinecraft().getRenderPartialTicks(), true);		
			GL11.glPopMatrix();
			
		}
		
		float f4 = 0.75F;
		GL11.glScalef(f4, f4, f4);
		GL11.glScalef(0.6F / f4, 0.6F / f4, 0.6F / f4);
		//this.bindEntityTexture(entityVehicle);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		//this.model.render(entityVehicle, 0.0625F);
		
		VehicleRenderableState renderState = null;
		switch(entityVehicle.getState()) {
		case OFF:
		    
		case IDLE:
		    renderState = VehicleRenderableState.IDLE;
		    break;
        case STARTING_TO_DRIVE:
            renderState = VehicleRenderableState.PREPARED_TO_DRIVE;
            break;
        case STOPPING:
            renderState = VehicleRenderableState.STOPPING;
            break;
        case DRIVING:
            renderState = VehicleRenderableState.DRIVING;
            break;
        case STARTING_TO_SHIFT:
        	renderState = VehicleRenderableState.STARTING_SHIFT;
        	break;
        case SHIFTING:
        	renderState = VehicleRenderableState.SHIFTING;
        	break;
        case FINISHING_SHIFT:
        	renderState = VehicleRenderableState.FINISHING_SHIFT;
        	break;
        
		}
		
		

		
		
	
		
		
		
		
		
		// RUN THE HIERARCHIAL RENDERING
		PartRenderContext<VehicleRenderableState> context = new PartRenderContext<>();
		context.setState(renderState);
		context.setEntity(entityVehicle);
		context.setScale(0.0625f);
		mainRenderer.render(context);
		

		
		
	
		
		
		
		
		
		
		EntityPlayer player = Minecraft.getMinecraft().player;
		int gameView = Minecraft.getMinecraft().gameSettings.thirdPersonView;
		boolean isPlayerRiding = player.isRiding();
		boolean isRidingVehicle = isPlayerRiding;
		if(isRidingVehicle) isRidingVehicle = ((player.getRidingEntity() instanceof EntityVehicle));
		
		
		/*
		 * A better method of this is to combine the textures
		 * THIS SHOULD BE DONE LATER ON!!!
		 */
		if(entityVehicle.isBraking) {
			if(gameView == 0 && isRidingVehicle && (EntityVehicle) player.getRidingEntity() == entityVehicle) {
				
			} else {
				GlStateManager.enableBlend();
				GL11.glScaled(1.0001, 1.0001, 1.0001);
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				ResourceLocation loc = new ResourceLocation("mw" + ":" + "textures/entity/audis4lights.png");
				
				context.renderAlternateTexture(loc);
				
				mainRenderer.render(context);
				GlStateManager.disableBlend();
			}
			
		}
		
		ResourceLocation loc = new ResourceLocation("mw" + ":" + "textures/entity/suspensionblue.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
		
		
		
		
		/* suspension
		
		float s = 1.625f;
		double defaultRide = 1.0;
		entityVehicle.rideOffset = defaultRide + susSolve.getStretch();
		ArrayList<WheelSolver> solver = entityVehicle.getSolver().wheels;
		for(WheelSolver w : solver) {
			GL11.glPushMatrix();
			Vec3d r = w.relativePosition;
			
			w.getSuspension().springRate = 33000;
			
			
			GL11.glTranslatef((float) (r.x*s)+0.05f, (float) (-1.5f-entityVehicle.rideOffset)-0.1f, (float) (r.z*s)+0.2f);
			GL11.glRotated(0, 1.0, 0.0, 0.0);
			GL11.glScaled(1.0, 0.25 + (w.getSuspension().getStretch()*-1)*0.3, 1.0);
			GL11.glTranslated(0.0, -1.5, 0.0);
			(new SuspensionModel()).render(entityVehicle, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
			GL11.glPopMatrix();
			
		}
		*/
		
		
		
		//DebugRenderer.setupBasicRender();
		
		// suspension
		//GL11.glTranslatef(-1.6f*s, (float) (-1.5f-entityVehicle.rideOffset), 1.75f*s);
		
		/*
		if(entityVehicle.ticksExisted > 200) entityVehicle.ticksExisted = 0;
		float interp = (entityVehicle.ticksExisted-1)+(entityVehicle.ticksExisted-(entityVehicle.ticksExisted-1))*Minecraft.getMinecraft().getRenderPartialTicks();
		double tE = entityVehicle.ticksExisted;
		
		double dV = Math.sin(interp*2)/(Math.max(1.0, tE*tE/9000));
		
		double f = (entityVehicle.ticksExisted/200.0);
		*/
		//System.out.println("fuck");
	
		
		
		
		//System.out.println(entityVehicle.rideOffset);
		
		/*
		double dV = entityVehicle.getSolver().rearAxel.rightWheel.rideHeight;
		
		
		
		susSolve.springRate = 19000;
		
		
		
		susSolve.applyForce(-entityVehicle.mass*9.81);
		//System.out.println(susSolve.currentLength + " | " + susSolve.length);
		
		GL11.glRotated(0, 1.0, 0.0, 0.0);
		GL11.glScaled(1.0, 1.0 + (susSolve.getStretch()*-1), 1.0);
		GL11.glTranslated(0.0, -1.5, 0.0);
		
		(new SuspensionModel()).render(entityVehicle, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
		*/
		
		//DebugRenderer.destructBasicRender();
		
		
		
		/*
		GL11.glTranslated(0.0, -3.5, 0.0);
		GL11.glScaled(-1, -1, 1);
		GL11.glRotated(180, 0, 1, 0);
		Vec3d[] vL = entityVehicle.calculateTerrainPlane();
		//System.out.println(Arrays.toString(vL));
		boolean flag = true;
		
		if(vL == null) flag = false;
		if(vL != null) {
			for(Vec3d v : vL) {
				if(v == null) {
					flag = false;
					break;
				}
			}
		}
		
		
		if(flag) {
			//System.out.println(Arrays.toString(vL));
			renderPlane(vL);
		}
		*/
		
		
		/*
		GlStateManager.enableBlend();
		GL11.glScaled(1.001, 1.001, 1.001);
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
		
		mainRenderer.render(context);
		
		GlStateManager.disableBlend();
		*/
		
		// test raycast
		
		
		//
		
		
		/*
		if(Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox()) {
			GL11.glPushMatrix();
			
			DebugRenderer.setupBasicRender();
			entityVehicle.getOreintedBoundingBox().renderOBB();
			DebugRenderer.destructBasicRender();
			GL11.glPopMatrix();
		}*/
		
		
		
		
		/*
		// test side slip
		double angle = entityVehicle.solver.getSideSlipAngle();
		//System.out.println(Math.toDegrees(angle));
		
		
		Vec3d eV = new Vec3d(0, 0, -1);
		eV = eV.rotateYaw(-(float) angle);
		
		
		DebugRenderer.setupBasicRender();
		GL11.glTranslated(0.0, -3.5, 0.0);
		DebugRenderer.renderLine(Vec3d.ZERO, eV, new Vec3d(1,0,0));
		DebugRenderer.destructBasicRender();
		*/
		
		// end test
		
		//rigidBodyTest(entityVehicle);
		//OBBTest();
		
//		double d0 = RenderManager.renderPosX - (entityVehicle.posX - entityVehicle.lastTickPosX) * (double)par9; // - (RenderManager.renderPosX - (entityVehicle.lastTickPosX + (entityVehicle.posX - entityVehicle.lastTickPosX) * (double)par9));
//		double d1 = RenderManager.renderPosY; // - (RenderManager.renderPosY - (entityVehicle.lastTickPosY + (entityVehicle.posY - entityVehicle.lastTickPosY) * (double)par9));
//		double d2 = RenderManager.renderPosZ - (entityVehicle.posZ - entityVehicle.lastTickPosZ) * (double)par9; // - (RenderManager.renderPosZ - (entityVehicle.lastTickPosZ + (entityVehicle.posZ - entityVehicle.lastTickPosZ) * (double)par9));
//
//	        
		GL11.glPopMatrix();
//		
//		    GL11.glPushMatrix();
//	        GL11.glDepthMask(false);
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glDisable(GL11.GL_CULL_FACE);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        
//	        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
//	        GL11.glLineWidth(2.0F);
//	        
//	        //AxisAlignedBB fbb = entityVehicle.getFrontBoundingBox().copy();
//
////	        AxisAlignedBB cbb = entityVehicle.getBoundingBox().copy();
////	        cbb.offset(-entityVehicle.posX, -entityVehicle.posY, -entityVehicle.posZ);
////	        RenderGlobal.drawOutlinedBoundingBox(cbb, 16777215);
//	        
//	        //System.out.println("Rendering " + entityVehicle + " " + entityVehicle.getFrontBoundingBox());
//	        //d0 = d1 = d2 = 0;
//	        RenderGlobal.drawOutlinedBoundingBox(entityVehicle.getBoundingBox().getOffsetBoundingBox(-d0, -d1, -d2), 0x00D0FF00);
//	        AxisAlignedBB fbb = entityVehicle.getFrontBoundingBox().getOffsetBoundingBox(-d0, -d1, -d2);
//	        RenderGlobal.drawOutlinedBoundingBox(fbb, 0xFF00FF00);
//	        
//	        AxisAlignedBB rbb = entityVehicle.getRearBoundingBox().getOffsetBoundingBox(-d0, -d1, -d2);
//	        RenderGlobal.drawOutlinedBoundingBox(rbb, 0xFF0000FF);
//	        
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_CULL_FACE);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        GL11.glDepthMask(true);
//	        GL11.glPopMatrix();
	}
	
	public static Vector3d matrixToEuler(Matrix3f rotation) {
		
		
		double sy = Math.sqrt(rotation.m00*rotation.m00 + rotation.m10*rotation.m10);
		boolean singular = sy < 1e-6;
		double x,y,z;
		if(!singular) {
			x = Math.atan2( rotation.m21,rotation.m22);
			y = Math.atan2(-rotation.m20,sy);
			z = Math.atan2( rotation.m10,rotation.m00);
		} else {
			x = Math.atan2(-rotation.m12, rotation.m11);
			y = Math.atan2(-rotation.m20, sy);
			z = 0;
		}
		return new Vector3d(x,y,z);
	}

    private void invertCameraTransform() {
        Matrix4f currentTransformMatrix = cameraTransformMatrix.get();
		if(currentTransformMatrix != null) {
		    Matrix4f inverse = Matrix4f.invert(currentTransformMatrix, null);
		    Matrix4f currentMatrix = MatrixHelper.captureMatrix();
		    Matrix4f composite = Matrix4f.mul(inverse, currentMatrix, null);
	        MatrixHelper.loadMatrix(composite);
		}
    }
	
	public static void captureCameraTransform(Matrix4f transformMatrix) {
	    cameraTransformMatrix.set(transformMatrix);
	}

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return null;
    }

	@Override
	public void doCompatibleRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.renderVehicle((EntityVehicle)par1Entity, par2, par4, par6, par8, par9);
	}
}