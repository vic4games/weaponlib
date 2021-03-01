package com.vicmatskiv.weaponlib.vehicle;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderVehicle2 extends CompatibleEntityRenderer
{
    private static ThreadLocal<Matrix4f> cameraTransformMatrix = new ThreadLocal<>();

	private static ResourceLocation field_110782_f;
	
	private StatefulRenderer<VehicleRenderableState> mainRenderer;
	
	private VehicleRenderableState currentRenderableState;
	
	public RenderVehicle2(StatefulRenderer<VehicleRenderableState> mainRenderer)
	{
		this.shadowSize = 0.5F;
		this.mainRenderer = mainRenderer;
	}

	public void renderVehicle(EntityVehicle entityVehicle, double posX, double posY, double posZ, float rotationYaw, float par9)
	{
//	    System.out.println("Rendering pitch " + entityVehicle.rotationPitch);
		//field_110782_f = textureResource; //new ResourceLocation("missing-texture"); //Halocraft.MODID+par1HCEntityMongoose.getEntityTexture());
		
		GL11.glPushMatrix();
		invertCameraTransform();
		
		float ptiYaw = entityVehicle.prevRotationYaw + (entityVehicle.rotationYaw-entityVehicle.prevRotationYaw)*Minecraft.getMinecraft().getRenderPartialTicks();
		
		
		GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
		GL11.glRotatef(180.0F - ptiYaw, 0.0F, 1.0F, 0.0F);
		
		//GL11.glRotatef((float) entityVehicle.forwardLean, 1, 0, 0);
		//GL11.glRotatef((float) entityVehicle.sideLean, 0, 0, 1);
		
		GL11.glRotatef(entityVehicle.rotationPitch, 1.0F, 0.0F, 0.0F);
		//GL11.glRotatef(MathHelper.wrapAngleTo180_float(par1HCEntityMongoose.getRotateWheelSpeed()*100F), 1.0F, 0.0F, 0.0F);
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
		}
		
		
		PartRenderContext<VehicleRenderableState> context = new PartRenderContext<>();
		context.setState(renderState);
		context.setEntity(entityVehicle);
		context.setScale(0.0625f);
		mainRenderer.render(context);
		
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