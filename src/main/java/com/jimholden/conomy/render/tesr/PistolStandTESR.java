package com.jimholden.conomy.render.tesr;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.jimholden.conomy.blocks.MinerBlock;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityPistolStand;
import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.drugs.DrugRenderer;
import com.jimholden.conomy.entity.models.GhoulModel;
import com.jimholden.conomy.entity.models.RagdollGhoul;
import com.jimholden.conomy.entity.rope.ChainedRopeSimulation;
import com.jimholden.conomy.entity.rope.FABRIK;
import com.jimholden.conomy.entity.rope.RopeSimulation;
import com.jimholden.conomy.entity.rope.RopeSimulation.Point;
import com.jimholden.conomy.entity.rope.RopeSimulation.Stick;
import com.jimholden.conomy.items.HeadsetItem;
import com.jimholden.conomy.items.ItemRope;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.render.LUTRenderer;
import com.jimholden.conomy.shaders.Shader;
import com.jimholden.conomy.shaders.ShaderManager;
import com.jimholden.conomy.util.ModelGeometryTool;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.RopeUtil;
import com.jimholden.conomy.util.VectorUtil;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PistolStandTESR extends TileEntitySpecialRenderer<TileEntityPistolStand> {
	public static Shader shader = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/stand"));
	
	
	public static RagdollGhoul model = new RagdollGhoul();
	//private HeadsetModel model = new HeadsetModel();
	private int tick = 0;

	public static RopeSimulation simulation = null;
	public static FABRIK fab = null;
	
	public static ChainedRopeSimulation crs = null;
	
	
	@Override
	public void render(TileEntityPistolStand te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		
		//System.out.println("REAL: " + (new Vec3d(x, y, z)));
		
		//GL11.glPushMatrix();
		
	
		/*
		GL11.glTranslated(x, y, z);
		ItemStack stack = te.getStackInSlot(0);
		renderItem(te, stack);
		GL11.glPopMatrix();
		*/
		
		
		/*
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		ItemStack stack = te.getStackInSlot(0);
		renderItem(te, stack);
		GL11.glPopMatrix();
		*/
		
		model = new RagdollGhoul();
		//simulation = null;
		
		if(crs == null) {
			crs = new ChainedRopeSimulation();
			
			crs.addSimpleJointedLigament(new Vec3d(0.4, 0.45, 0));
			//crs.addSimpleJointedLigament(Vec3d.ZERO);
			crs.addSimpleJointedLigament(new Vec3d(-0.4, 0.45, 0));
		}
		
		//simulation = null;
		if(simulation == null) {
			simulation = new RopeSimulation();
		
			
			
			
			crs.sims.add(simulation);
			
			
			
			
			Point head = new Point("head", new Vec3d(0, 0.75, 0), false);
			Point collar = new Point("collar", new Vec3d(0, 0.5, 0), false);
		
			//left arm
			
			Point leftShoulder = new Point("leftShoulder", new Vec3d(0.4, 0.45, 0.0), false);
			Point leftElbow = new Point("leftElbow", new Vec3d(0.5, 0.1, 0), false);
			Point leftHand = new Point("leftHand", new Vec3d(0.5, -0.3, 0), false);
			
			// right arm
			
			Point rightShoulder = new Point("rightShoulder", new Vec3d(-0.4, 0.45, 0.0), false);
			Point rightElbow = new Point("rightElbow", new Vec3d(-0.5, 0.1, 0), false);
			Point rightHand = new Point("rightHand", new Vec3d(-0.5,  -0.3, 0), false);
			
			Point waistRight = new Point("waistRight", new Vec3d(-0.25, -0.15, 0.0), false);
			Point waistLeft = new Point("waistLeft", new Vec3d(0.25, -0.15, 0.0), false);
			
			Point hipLeft = new Point("hipLeft", new Vec3d(0.3, -0.3, 0.0), false);
			Point hipRight = new Point("hipRight", new Vec3d(-0.3, -0.3, 0.0), false);
			Point hipCenter = new Point("hipCenter", new Vec3d(0, -0.3, 0), false);
			
			// left leg
			Point leftKnee = new Point("leftKnee", new Vec3d(0.3, -0.7, 0.0), false);
			Point leftFoot = new Point("leftFoot", new Vec3d(0.25, -1.0, 0.0), false);
		
			// right leg
						Point rightKnee = new Point("rightKnee", new Vec3d(-0.3, -0.7, 0), false);
						Point rightFoot = new Point("rightFoot", new Vec3d(-0.25, -1.0, 0), false);
					
						
			// 0 = head
			// 1 = collar
			// 2 = leftShoulder
			// 3 = rightShoulder
			// 4 = leftElbow
			// 5 = rightElbow
			// 6 = leftHand
			// 7 = rightHand
			// 8 = hip
			// 9 = leftKnee
	        // 10 = leftFoot
		    // 11 = rightKnee
		    // 12 = rightFoot
			
			simulation.connect(head, collar);
			
	
			simulation.connect(collar, leftShoulder);
			simulation.connect(collar, rightShoulder);
			
			simulation.connect(leftShoulder, rightShoulder);
			
			simulation.connect(leftShoulder, leftElbow);
			simulation.connect(rightShoulder, rightElbow);
			
			simulation.connect(leftElbow, leftHand);
			
			simulation.connect(rightElbow, rightHand);
			
			
			
			//simulation.connect(collar, hip);
			simulation.connect(collar, waistLeft);
			simulation.connect(collar, waistRight);
			simulation.connect(leftShoulder, waistLeft);
			simulation.connect(rightShoulder, waistRight);
			
			simulation.connect(rightShoulder, waistLeft);
			simulation.connect(leftShoulder, waistRight);
			
			
			simulation.connect(waistRight, waistLeft);
			
			simulation.connect(waistLeft, hipLeft);
			simulation.connect(waistRight, hipRight);
			simulation.connect(hipLeft, hipRight);
			
			simulation.connect(hipRight, waistLeft);
			simulation.connect(hipLeft, waistRight);
			simulation.connect(hipCenter, waistLeft);
			simulation.connect(hipCenter, waistRight);
			
			simulation.connect(hipLeft, leftKnee);
			simulation.connect(leftKnee, leftFoot);
			
			simulation.connectC3(leftKnee, rightKnee, 0.3);
			simulation.connectC3(leftElbow, rightElbow, 0.4);
			
			simulation.connectC3(head, leftShoulder, 0.35);
			simulation.connectC3(head, rightShoulder, 0.35);
			
			
			
			simulation.connect(hipRight, rightKnee);
			simulation.connect(rightKnee, rightFoot);
			
			rightShoulder.prevPos = rightShoulder.prevPos.subtract(0, 0, -0.013);
			
			
			//simulation.connect(hip, rightShoulder);
			//simulation.connect(hip, leftShoulder);
			simulation.printPoints();
		}
	
		//crs.simulate(x, y, z, 0.05);
		
		simulation.simulate(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), 0.05);
		
		/*
		if(Minecraft.getMinecraft().player.ticksExisted % 400 == 0) {
			System.out.println("hi");
			simulation = null;
		}
		
		if(fab == null) {
			fab = new FABRIK();
			
			fab.newPoint(new Vec3d(0, 1, 0));
			fab.newPoint(new Vec3d(0, 2, 0));
			
			fab.autoSticks();
		}
		
		
		
		if(simulation == null) {
			
			simulation = new RopeSimulation();
			
			simulation.generateChain(3, 16);
			
			
			
			
			
		}
		
		//System.out.println(simulation.points.get(2).pos);
		Point s = simulation.points.get(simulation.points.size()-1);
		
		int ticks = Minecraft.getMinecraft().player.ticksExisted%360;
		
		double interp = (ticks-1) + (ticks-(ticks-1))*Minecraft.getMinecraft().getRenderPartialTicks();

		simulation.simulate(0.05);
		
		fab.simulate(0.05, new Vec3d(0.5, 0.5, 0));
		*/
		//System.out.println(simulation.points.get(0).pos);
		
		
		GL11.glPushMatrix();
		GlStateManager.enableTexture2D();
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID + ":" + "textures/entity/ghoul.png"));
		//GL11.glRotated(180, 1, 0, 0);
		GL11.glTranslated(x, y+0.5, z);
		//GL11.glScaled(-1, -1, -1);
		GlStateManager.disableCull();
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(180, 0, 1, 0);
		GlStateManager.enableAlpha();
		//GlStateManager.enableBlend();
		//GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
		//model.srender2(crs);
		model.specialRender(simulation);
		//GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableBlend();
		GL11.glPopMatrix();
		
		
		// debug render
		//crs.basicDebugRender(x, y, z);
		
		
		GL11.glPushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GL11.glTranslated(x, y, z);

		//model.specialRender();
		
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.disableDepth();
		
		
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(ItemRope.ROPE_RED);
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
	//	GlStateManager.enableBlend();
		GL11.glLineWidth(1.0f);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		/*
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		for(Stick stick : simulation.sticks) {
			RopeUtil.drawRopeSegment(Vec3d.ZERO, stick.pointA.pos, stick.pointB.pos, bb, stick.length);
		}*/
		
		bb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		for(Stick stick: simulation.sticks) {
			//if(1+1==2) continue;
			if(stick.c3Constraint) {
				continue;
			}
			Vec3d a = stick.pointA.pos;
			Vec3d b = stick.pointB.pos;
			bb.pos(a.x, a.y, a.z).endVertex();
			bb.pos(b.x, b.y, b.z).endVertex();
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		}
		
		t.draw();
		
		//GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glPopMatrix();
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		

	}
	
	private void renderItem(TileEntityPistolStand atm, ItemStack stack) {
        if (!stack.isEmpty()) {
        	 RenderHelper.enableStandardItemLighting();
             GlStateManager.enableLighting();
             GlStateManager.pushMatrix();
             
             // Translate to the center of the block and .9 points higher
             GlStateManager.translate(.5, .3, .5);
             GlStateManager.scale(1.0f, 1.0f, 1.0f);
             GlStateManager.rotate(-8, 1, 0, 0);
             /*
             try {
				//Minecraft.getMinecraft().getTextureManager().getTexture(new ResourceLocation(Reference.MOD_ID, "shaders/stand")).loadTexture(Minecraft.getMinecraft().getResourceManager());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
             
             shader = ShaderManager.loadShader(new ResourceLocation(Reference.MOD_ID, "shaders/stand"));
         	
             
            

             shader.use();
             int time = GL20.glGetUniformLocation(shader.getShaderId(), "time");
             int ticks = Minecraft.getMinecraft().player.ticksExisted;
             double rT = ticks + (ticks-(ticks-1))*Minecraft.getMinecraft().getRenderPartialTicks(); 
             
             float val = (float) (((Minecraft.getMinecraft().player.ticksExisted%50)/50.0)*10000);
             
             GL20.glUniform1f(GL20.glGetUniformLocation(shader.getShaderId(), "temperature"), (float) val);
            
             GL20.glUniform1f(time, (float) rT);
             
            // GUItil.initializeMultisample();
             
             Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
             shader.release();
             GlStateManager.popMatrix();
             
          //   GUItil.unapplyMultisample();
             
        	/*
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableLighting();
            GlStateManager.pushMatrix();
            
            // Translate to the center of the block and .9 points higher
            GlStateManager.translate(.5, .3, .5);
            GlStateManager.scale(1.0f, 1.0f, 1.0f);
            GlStateManager.rotate(-8, 1, 0, 0);

            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);

            GlStateManager.popMatrix();
            */
        }
    }
	
}
