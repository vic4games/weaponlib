package com.jimholden.conomy.entity.render;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.entity.EntityRope;
import com.jimholden.conomy.entity.models.ClimbingRopeAnchor;
import com.jimholden.conomy.entity.models.GhoulModel;
import com.jimholden.conomy.entity.models.RopeModel;
import com.jimholden.conomy.items.ItemRope;
import com.jimholden.conomy.main.ModEventClientHandler;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.RopeUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityRope extends Render<EntityRope>
{
    private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("textures/entity/lead_knot.png");
    private final ClimbingRopeAnchor climbingAnchor = new ClimbingRopeAnchor();
    private final RopeModel ropeModel = new RopeModel();
    
    private static final Minecraft mc = Minecraft.getMinecraft();

    public RenderEntityRope(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    public static Vec3d ptI(Entity ent) {
    	double x = ent.lastTickPosX + (ent.posX-ent.lastTickPosX)*mc.getRenderPartialTicks(); 
    	double y = ent.lastTickPosY + (ent.posY-ent.lastTickPosY)*mc.getRenderPartialTicks(); 
    	double z = ent.lastTickPosZ + (ent.posZ-ent.lastTickPosZ)*mc.getRenderPartialTicks(); 
    	
    	return new Vec3d(x, y, z);
    	
    	
    }
    
    public void doDebugLine(EntityRope entity) {
    	EntityPlayer roper = entity.getRoper();
    	if(roper != null) {
			GL11.glBegin(GL11.GL_LINE_STRIP);
			Vec3d entR = ptI(entity);
			GL11.glVertex3d(entR.x, entR.y, entR.z);
			
			for(int r = 0; r < entity.vecList.size(); ++r) {
				Vec3d vec = entity.vecList.get(r);
				GL11.glVertex3d(vec.x, vec.y, vec.z);
			}
			Vec3d iEP = ptI(roper);
			GL11.glVertex3d(iEP.x, iEP.y, iEP.z);
	
			GL11.glEnd();
		}
    }
    
    public static void renderRope(EntityRope entity) {
    	
    	
    	
    	
    	Minecraft mc = Minecraft.getMinecraft();
    	EntityPlayer roper = entity.getRoper();
    	
    	boolean isHanging;
    	
    	// TEMP
    	//if(roper == null) return;
    	//
    	if(entity.isRopeStackEmpty()) return;
    	if(entity.isHangingRope()) {
    		isHanging = true;
    		if(entity.vecList.isEmpty()) return;
    	} else {
    		isHanging = false;
    	}
    	
    	
    	
    	
        GL11.glPushMatrix();
        mc.entityRenderer.enableLightmap();
        RenderHelper.enableStandardItemLighting();
		GL11.glTranslated(0, 0, 0);
		Vec3d entP = ptI(mc.player);
		
		GL11.glTranslated(-entP.x, -entP.y, -entP.z);
		GL11.glPushMatrix();
		GL11.glPopMatrix();
		
		
		//RenderHelper.enableStandardItemLighting();
		//net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		
		//LIGHTMAP
		int i = entity.getBrightnessForRender();

        if (entity.isBurning())
        {
            i = 15728880;
        }

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //
		
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		ItemRope iR = ((ItemRope) entity.getRopeStack().getItem());
		ResourceLocation resl = iR.getResourceLocationFromType(iR.ropeType);
		Minecraft.getMinecraft().getTextureManager().bindTexture(resl);
		
		ArrayList<Vec3d> newList = new ArrayList<Vec3d>();
		newList.addAll(entity.vecList);
		if(!isHanging) {
			newList.add(ptI(roper));
		}
		
		
		
		Vec3d n = newList.get(0);
		
		float yaw1 = (float) RopeUtil.pitchAngle(entity.getPositionVector(), newList.get(0));
		Vec3d startPosForRope = entity.getRopeStart((float) (yaw1-Math.toRadians(90)));
		
		
		RopeUtil.constructRopeRender(entity, startPosForRope, isHanging);
		
		GlStateManager.enableCull();
		mc.entityRenderer.disableLightmap();
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();
		
    }
    
    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityRope entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
    	//System.out.println("hel");
    	EntityPlayer roper = entity.getRoper();
        if(entity.isHangingRope()) {
        	if(entity.getVecList().isEmpty()) return;
        }
    	
    	GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate((float)x, (float)y, (float)z);
        float f = 0.0625F;
        GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.translate(0, -1.5, 0);
        this.bindEntityTexture(entity);
        if(entity.isRopeStackEmpty()) {
        	climbingAnchor.render(entity, 0.0F, 0.0F, 0.0F, 0.1F, 0.0F, 0.0625F);
        	GL11.glPopMatrix();
        	return;
        }
        

        float pitch = 0;
        float yaw = 0;
        
        Vec3d startPosForRope = Vec3d.ZERO;
        
        boolean isHanging;
    	if(entity.isHangingRope()) {
    		isHanging = true;
    		
    	} else {
    		isHanging = false;
    	}
    	ArrayList<Vec3d> newList = new ArrayList<Vec3d>();
		newList.addAll(entity.vecList);
		if(!isHanging) {
			newList.add(ptI(roper));
		}
		
		Vec3d n = newList.get(0);
		
		entity.getLookHelper().setLookPosition(n.x, n.y, n.z, 10.0F, 10.0F);
		float yaw1 = (float) RopeUtil.pitchAngle(entity.getPositionVector(), newList.get(0));
		yaw = (float) -Math.toDegrees(yaw1);
		startPosForRope = entity.getRopeStart((float) (yaw1-Math.toRadians(90)));
        if(!entity.isRopeStackEmpty()) {
        	climbingAnchor.render(entity, 0.0F, 0.0F, 0.0F, yaw+180, 0.0F, 0.0625F);
        } else {
        	climbingAnchor.render(entity, 0.0F, 0.0F, 0.0F, 0.1F, 0.0F, 0.0625F);
        }
        
        
        //GlStateManager.translate(0, 1.0, 0);
        
        //this.ropeModel.setRotationAngle(this.ropeModel.boxList.get(0), (float) x, (float) y, (float) z);
       /*
        if(entity.player != null) {
    	   //System.out.println("hi");
    	   Vec3d entPos = entity.getPositionVector();
    	   Vec3d pPos = entity.player.getPositionVector();
    	   
    	   Vec3d testboi = entPos.subtract(pPos).normalize().scale(-1);
           this.ropeModel.setRotationAngle(this.ropeModel.boxList.get(0), (float) testboi.x, (float) testboi.y, (float) testboi.z);
       
    	   
    	  // this.ropeModel.setrot
    	   /*
    	   double xA = Math.acos((new Vector2d(entPos.y, entPos.z)).dot(new Vector2d(pPos.y, pPos.z)));
    	   double yA = Math.acos((new Vector2d(entPos.x, entPos.z)).dot(new Vector2d(pPos.x, pPos.z)));
    	   double zA = Math.acos((new Vector2d(entPos.x, entPos.y)).dot(new Vector2d(pPos.x, pPos.y)));
    	   double n = (new Vector2d(entPos.x, entPos.y)).dot(new Vector2d(pPos.x, pPos.y));
    	   System.out.println(Math.acos(n));
    	   
//    	   /this.ropeModel.setRotationAngle(this.ropeModel.boxList.get(0), (float) xA, (float) yA, (float) zA);
       }
        */
       	//this.ropeModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0825F);

    

        GlStateManager.popMatrix();
        
        
       // renderRope(entity);
        
        /*RENDER LINE TO PLAYER
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 0);
		Vec3d entP = ptI(mc.player);
		GL11.glTranslated(-entP.x, -entP.y, -entP.z);
	//Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
		GL11.glPushMatrix();
		GL11.glPopMatrix();
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		//GlStateManager.disableTexture2D();
		//GlStateManager.color(1, 0, 0);

		//doDebugLine(entity);
		
		/*
		if(entity.player != null) {
			Tessellator t = Tessellator.getInstance();
			BufferBuilder buf = t.getBuffer();
			//GL11.glLineWidth(2.0F);
			buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			
			Vec3d entR = ptI(entity);
			buf.pos(entR.x, entR.y, entR.z).endVertex();
			
			for(int r = 0; r < entity.vecList.size(); ++r) {
				Vec3d vec = entity.vecList.get(r);
				buf.pos(vec.x, vec.y, vec.z).endVertex();
				
			}
			//buf.pos(1,1, 1);
			buf.pos(entity.player.posX, entity.player.posY, entity.player.posZ).endVertex();
			
			t.draw();
		}
		ResourceLocation resl = new ResourceLocation(Reference.MOD_ID + ":textures/entity/ropentile.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(resl);
		
		if(roper != null) {
			RopeUtil.constructRopeRender(entity, startPosForRope);
			
			/*
			Tessellator t = Tessellator.getInstance();
			BufferBuilder buf = t.getBuffer();
			//GL11.glLineWidth(2.0F);
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
			
			ArrayList<Vec3d> newList = new ArrayList<Vec3d>();
			//System.out.println(entity.vecList);
			newList.add(startPosForRope);
			newList.addAll(entity.vecList);
			newList.add(ptI(roper));
			
			for(int n = 0; n < newList.size()-1; n++) {
				RopeUtil.drawRopeSegment(entity.getPositionVector(), roper.getPositionVector(), newList.get(n), newList.get(n+1), buf);
				
			}
			
			
			t.draw();
			
		}
		
		
		
		
		//System.out.println(pointVec);
		//
		//
		//GL11.glTranslated(mc.player.posX,mc.player.posY, mc.player.posZ);
		GlStateManager.enableCull();
		//GlStateManager.enableTexture2D();
		GL11.glPopMatrix();
		*/
		
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityRope entity)
    {
        return new ResourceLocation(Reference.MOD_ID + ":" + "textures/entity/climbinganchor.png");
    }
}
