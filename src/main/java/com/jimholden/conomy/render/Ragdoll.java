package com.jimholden.conomy.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

import com.jimholden.conomy.entity.models.RagdollGhoul;
import com.jimholden.conomy.entity.render.RenderEntityRope;
import com.jimholden.conomy.entity.render.RenderModZombie;
import com.jimholden.conomy.entity.rope.RopeSimulation;
import com.jimholden.conomy.entity.rope.RopeSimulation.Point;
import com.jimholden.conomy.entity.rope.RopeSimulation.Stick;
import com.jimholden.conomy.items.ItemRope;
import com.jimholden.conomy.util.RopeUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class Ragdoll {

	public RopeSimulation simulation;
	public double x, y, z;
	public RagdollGhoul model;

	public Ragdoll(double x, double y, double z, double width, double height, double hipMod) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		
		simulation = new RopeSimulation();

		Point head = new Point("head", new Vec3d(0, 0.75, 0), false);
		Point collar = new Point("collar", new Vec3d(0, 0.5, 0), false);

		// left arm

		Point leftShoulder = new Point("leftShoulder", new Vec3d(0.4-width, 0.45, 0.0), false);
		Point leftElbow = new Point("leftElbow", new Vec3d(0.5, 0.1, 0), false);
		Point leftHand = new Point("leftHand", new Vec3d(0.5, -0.3, 0), false);

		// right arm

		Point rightShoulder = new Point("rightShoulder", new Vec3d(-0.4+width, 0.45, 0.0), false);
		Point rightElbow = new Point("rightElbow", new Vec3d(-0.5, 0.1, 0), false);
		Point rightHand = new Point("rightHand", new Vec3d(-0.5, -0.3, 0), false);

		Point waistRight = new Point("waistRight", new Vec3d(-0.25+width, -0.15-height, 0.0), false);
		Point waistLeft = new Point("waistLeft", new Vec3d(0.25-width, -0.15-height, 0.0), false);

		Point hipLeft = new Point("hipLeft", new Vec3d(0.3-width-hipMod, -0.3-height, 0.0), false);
		Point hipRight = new Point("hipRight", new Vec3d(-0.3+width+hipMod, -0.3-height, 0.0), false);
		Point hipCenter = new Point("hipCenter", new Vec3d(0, -0.3-height, 0), false);

		// left leg
		Point leftKnee = new Point("leftKnee", new Vec3d(0.3, -0.7-height, 0.0), false);
		Point leftFoot = new Point("leftFoot", new Vec3d(0.25, -1.0-height, 0.0), false);

		// right leg
		Point rightKnee = new Point("rightKnee", new Vec3d(-0.3, -0.7-height, 0), false);
		Point rightFoot = new Point("rightFoot", new Vec3d(-0.25, -1.0-height, 0), false);

		simulation.connect(head, collar);

		simulation.connect(collar, leftShoulder);
		simulation.connect(collar, rightShoulder);

		simulation.connect(leftShoulder, rightShoulder);

		simulation.connect(leftShoulder, leftElbow);
		simulation.connect(rightShoulder, rightElbow);

		simulation.connect(leftElbow, leftHand);

		simulation.connect(rightElbow, rightHand);

		// simulation.connect(collar, hip);
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

		simulation.connectC3(leftFoot, hipLeft, 0.35);

		simulation.connect(hipLeft, hipCenter);
		simulation.connect(hipRight, hipCenter);
		simulation.connect(hipRight, rightKnee);
		simulation.connect(rightKnee, rightFoot);

		simulation.connectC3(head, hipCenter, 0.5);
		
		simulation.connectC3(rightKnee, leftElbow, 0.5);
		simulation.connectC3(leftKnee, rightElbow, 0.5);
		simulation.connectC3(head, rightElbow, 0.3);
		simulation.connectC3(head, leftElbow, 0.3);
		simulation.connectC3(leftShoulder, leftHand, 0.3);
		
		
		simulation.connectC3(collar, leftElbow, 0.3);
		simulation.connectC3(collar, rightElbow, 0.3);
		
		
		//simulation.points.get(0).prevPos = new Vec3d(0, -1, -0.5);
		simulation.printPoints();
	}
	
	public void render() {
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableDepth();
		//model = null;
		if(model == null) {
			model = new RagdollGhoul();
			model.setupRagdoll();
		}
		simulation.simulate(x, y, z, 0.05);
		
		
		
		EntityPlayer p = Minecraft.getMinecraft().player;
		
		Vec3d rel = RenderEntityRope.ptI(p).scale(-1);
		
		
		// setup lightmap
		int i = RopeUtil.getBrightnessForRender(rel.scale(-1));
		
	
		
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
		
		
		//Vec3d rel = new Vec3d(x, y, z).subtract(p.getPositionVector());
		
		GL11.glPointSize(1.0f);
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GlStateManager.pushMatrix();
		GlStateManager.translate(rel.x, rel.y, rel.z);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex3d(x, y, z);
		GL11.glEnd();
		GlStateManager.popMatrix();
	
		
	//	System.out.println("yo");
		
		GL11.glPushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(rel.x, rel.y, rel.z);
		GlStateManager.enableTexture2D();
		Minecraft.getMinecraft().getTextureManager().bindTexture(RenderModZombie.TEXTURES);
		model.specialRender2(simulation);
		GlStateManager.disableTexture2D();
		//
		GlStateManager.enableLighting();
		GlStateManager.disableDepth();
		
		boolean skele = false;
		if(!skele) {
			GlStateManager.popMatrix();
			return;
		}
		
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
	//	GlStateManager.enableBlend();
		GL11.glLineWidth(1.0f);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		
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
		

		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glPopMatrix();
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		
	}

}
