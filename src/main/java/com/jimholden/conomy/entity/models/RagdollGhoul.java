package com.jimholden.conomy.entity.models;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.jimholden.conomy.entity.rope.ChainedRopeSimulation;
import com.jimholden.conomy.entity.rope.RopeSimulation;
import com.jimholden.conomy.main.ModEventClientHandler;
import com.jimholden.conomy.render.ConomyDebugRender;
import com.jimholden.conomy.render.LUTRenderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

// Made with Blockbench 4.0.5
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public class RagdollGhoul extends ModelBase {
	private final ModelRenderer master;
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer body_r1;
	private final ModelRenderer rightarm;
	private final ModelRenderer rightarm_r1;
	private final ModelRenderer leftarm;
	private final ModelRenderer leftarm_r1;
	private final ModelRenderer rightleg;
	private final ModelRenderer rightleg_r1;
	private final ModelRenderer leftleg;
	private final ModelRenderer leftleg_r1;

	public RagdollGhoul() {
		
		textureWidth = 64;
		textureHeight = 64;

		master = new ModelRenderer(this);
		master.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -23.5F, -0.3F);
		master.addChild(head);
		setRotationAngle(head, 0.0873F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 5, 8, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 13, -4.0F, -3.0F, -2.0F, 8, 3, 6, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -3.0F, -4.0F, 2, 3, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 13, 3.0F, -3.0F, -4.0F, 1, 3, 2, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, -23.5F, -0.4F);
		master.addChild(body);
		setRotationAngle(body, 0.192F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 24, 24, -4.0F, 0.0F, -2.0F, 8, 6, 4, 0.0F, false));

		body_r1 = new ModelRenderer(this);
		body_r1.setRotationPoint(4.0F, 6.0F, 2.0F);
		body.addChild(body_r1);
		setRotationAngle(body_r1, -0.2705F, 0.0F, 0.0F);
		body_r1.cubeList.add(new ModelBox(body_r1, 0, 22, -7.999F, 0.0F, -4.0F, 8, 6, 4, 0.0F, false));

		rightarm = new ModelRenderer(this);
		rightarm.setRotationPoint(-5.0F, 2.0F, 0.1F);
		body.addChild(rightarm);
		setRotationAngle(rightarm, 0.0436F, 0.0F, 0.0873F);
		rightarm.cubeList.add(new ModelBox(rightarm, 32, 44, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		rightarm_r1 = new ModelRenderer(this);
		rightarm_r1.setRotationPoint(-1.0F, 4.0F, 0.0F);
		rightarm.addChild(rightarm_r1);
		setRotationAngle(rightarm_r1, -0.0873F, 0.0F, -0.0436F);
		rightarm_r1.cubeList.add(new ModelBox(rightarm_r1, 16, 34, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, 2.0F, 0.05F);
		body.addChild(leftarm);
		setRotationAngle(leftarm, 0.0436F, 0.0F, -0.0873F);
		leftarm.cubeList.add(new ModelBox(leftarm, 16, 44, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		leftarm_r1 = new ModelRenderer(this);
		leftarm_r1.setRotationPoint(1.0F, 4.0F, 0.0F);
		leftarm.addChild(leftarm_r1);
		setRotationAngle(leftarm_r1, -0.1309F, 0.0F, 0.0436F);
		leftarm_r1.cubeList.add(new ModelBox(leftarm_r1, 32, 34, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, 11.2F, 0.7F);
		body.addChild(rightleg);
		setRotationAngle(rightleg, -0.1309F, 0.0F, 0.0436F);
		rightleg.cubeList.add(new ModelBox(rightleg, 0, 32, -2.0166F, -0.1217F, -3.9962F, 4, 7, 4, 0.0F, false));

		rightleg_r1 = new ModelRenderer(this);
		rightleg_r1.setRotationPoint(0.0F, 7.0F, 0.0F);
		rightleg.addChild(rightleg_r1);
		setRotationAngle(rightleg_r1, 0.2618F, 0.0F, -0.0436F);
		rightleg_r1.cubeList.add(new ModelBox(rightleg_r1, 40, 0, -2.0114F, -0.8827F, -3.8639F, 4, 6, 4, 0.0F, false));

		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, 11.2F, 0.7F);
		body.addChild(leftleg);
		setRotationAngle(leftleg, -0.1309F, 0.0F, -0.0873F);
		leftleg.cubeList.add(new ModelBox(leftleg, 28, 9, -1.9667F, -0.1207F, -3.9961F, 4, 7, 4, 0.0F, false));

		leftleg_r1 = new ModelRenderer(this);
		leftleg_r1.setRotationPoint(0.0F, 7.0F, 0.0F);
		leftleg.addChild(leftleg_r1);
		setRotationAngle(leftleg_r1, 0.2618F, 0.0F, 0.0436F);
		leftleg_r1.cubeList.add(new ModelBox(leftleg_r1, 0, 43, -1.9718F, -0.8835F, -3.8744F, 4, 6, 4, 0.0F, false));
		
		
		if(defaults == null) {
			defaults = new HashMap<>();
			addDefault(head);
			addDefault(body);
			addDefault(leftarm);
			addDefault(rightarm);
			addDefault(leftleg);
			addDefault(rightleg);
		}
	}

	public HashMap<ModelRenderer, Vec3d> defaults;

	public void addDefault(ModelRenderer m) {
		Vec3d p = new Vec3d(m.offsetX, m.offsetY, m.offsetZ);
		defaults.put(m, p);
	}

	public double angleBetweenVecs(Vector3d v1, Vector3d v2) {

		double p1 = v1.dot(v2);

		// System.out.println(v2);
		// System.out.println(v1.z*v2.z);

		double p2 = (v1.length() * v2.length());

		// System.out.println(p1 + " | " + p2);
		return Math.acos(p1 / p2);
	}

	public void applyStandardBoneTransform(RopeSimulation sim, ModelRenderer lig, int p1, int p2) {
		Vec3d a = sim.points.get(p1).pos;
		Vec3d b = sim.points.get(p2).pos;

		Vec3d rs = b.subtract(a);
		Vector3d t1 = new Vector3d(1.0, 0.0, 0.0);
		Vector3d t2 = new Vector3d(rs.x, rs.y, 0.0);
		float theX = (float) (angleBetweenVecs(t1, t2) + Math.PI / 2);

		Vector3d t3 = new Vector3d(0.0, 0.0, 1.0);
		Vector3d t4 = new Vector3d(0.0, rs.y, rs.z);
		float theZ = (float) (angleBetweenVecs(t3, t4) + Math.PI / 2);

		
		
		lig.rotateAngleX = (float) -theZ;
		lig.rotateAngleY = (float) Math.PI;
		lig.rotateAngleZ = (float) theX;
	}
	
	public void applyBodyTransform(RopeSimulation sim, ModelRenderer lig, int p1, int p2, int p3) {
		Vec3d a = sim.points.get(p1).pos;
		Vec3d b = sim.points.get(p2).pos;
		Vec3d c = sim.points.get(p3).pos;

		Vec3d rs = b.subtract(a);
		Vector3d t1 = new Vector3d(1.0, 0.0, 0.0);
		Vector3d t2 = new Vector3d(rs.x, rs.y, 0.0);
		float theX = (float) (angleBetweenVecs(t1, t2) + Math.PI / 2);

		Vector3d t3 = new Vector3d(0.0, 0.0, 1.0);
		Vector3d t4 = new Vector3d(0.0, rs.y, rs.z);
		float theZ = (float) (angleBetweenVecs(t3, t4) + Math.PI / 2);

		
		
		lig.rotateAngleX = (float) -theZ;
		//lig.rotateAngleY = (float) Math.PI;
		lig.rotateAngleZ = (float) theX;
	}

	public void applyLigamentTransforms(RopeSimulation sim, ModelRenderer lig, int top, int mid, int bottom) {

		Vec3d a = sim.points.get(top).pos;
		Vec3d b = sim.points.get(mid).pos;

		Vec3d rs = b.subtract(a);
		Vector3d t1 = new Vector3d(1.0, 0.0, 0.0);
		Vector3d t2 = new Vector3d(rs.x, rs.y, 0.0);
		float theX = (float) (angleBetweenVecs(t1, t2) + Math.PI / 2);

		Vector3d t3 = new Vector3d(0.0, 0.0, 1.0);
		Vector3d t4 = new Vector3d(0.0, rs.y, rs.z);
		float theZ = (float) (angleBetweenVecs(t3, t4) + Math.PI / 2);

		lig.rotateAngleX = (float) -theZ;
		lig.rotateAngleY = (float) Math.PI;
		lig.rotateAngleZ = (float) theX;

		ModelRenderer joint = lig.childModels.get(0);

		// lower
		a = sim.points.get(mid).pos;
		b = sim.points.get(bottom).pos;

		rs = b.subtract(a);
		t1 = new Vector3d(1.0, 0.0, 0.0);
		t2 = new Vector3d(rs.x, rs.y, 0.0);
		theX = (float) (angleBetweenVecs(t1, t2) - Math.PI / 2);

		t3 = new Vector3d(0.0, 0.0, 1.0);
		t4 = new Vector3d(0.0, rs.y, rs.z);
		theZ = (float) (angleBetweenVecs(t3, t4) - Math.PI / 2);

		joint.rotateAngleX = (float) -theZ;
		joint.rotateAngleZ = (float) theX;

	}

	public void applyPos(Vec3d p, ModelRenderer m) {
		p = p.add(defaults.get(m));
		m.offsetX = (float) p.x;
		m.offsetY = (float) p.y;
		m.offsetZ = (float) p.z;
	}

	public void applyRot(Vec3d p, ModelRenderer m) {
		m.rotateAngleX = (float) Math.toRadians(p.x);
		m.rotateAngleY = (float) Math.toRadians(p.y);
		m.rotateAngleZ = (float) Math.toRadians(p.z);
	}

	public void srender2(ChainedRopeSimulation crs) {

		/*
		 * GlStateManager.disableTexture2D(); GL11.glBegin(GL11.GL_LINES);
		 * 
		 * GlStateManager.color(1.0f, 0.0f, 0.0f); GL11.glVertex3d(0.0, 0.0, 0.0);
		 * GL11.glVertex3d(1.0, 0.0, 0.0);
		 * 
		 * GlStateManager.color(0.0f, 1.0f, 0.0f); GL11.glVertex3d(0.0, 0.0, 0.0);
		 * GL11.glVertex3d(0.0, 1.0, 0.0);
		 * 
		 * GlStateManager.color(0.0f, 0.0f, 1.0f); GL11.glVertex3d(0.0, 0.0, 0.0);
		 * GL11.glVertex3d(0.0, 0.0, 1.0); GL11.glEnd(); GlStateManager.color(1.0f,
		 * 1.0f, 1.0f); GlStateManager.enableTexture2D();
		 */

		RopeSimulation one = crs.sims.get(0);

		applyPos(new Vec3d(-0.1, 0, 0), rightarm);
		// applyLigamentTransforms(one, rightarm, 0);
		// applyLigamentTransforms(one, rightarmlower, 1);

		RopeSimulation two = crs.sims.get(1);

		applyPos(new Vec3d(0.1, 0.0, 0), leftarm);
		// applyPos(two.getPointDifference(0).scale(-1), leftarm);
		// applyLigamentTransforms(one, leftarm, 0);
		// applyLigamentTransforms(one, leftarmlower, 1);

		/*
		 * float pitch = (float) RopeUtil.getPitch(one.points.get(0).pos,
		 * one.points.get(1).pos); float yaw = (float) RopeUtil.getPitch(new Vec3d(0.0,
		 * 1.0, 0), one.points.get(1).pos.normalize()); float roll = (float)
		 * RopeUtil.getPitch(new Vec3d(1.0, 0.0, 0.0),
		 * one.points.get(1).pos.normalize());
		 * 
		 * 
		 * 
		 * Vec3d a = one.points.get(0).pos; Vec3d b = one.points.get(1).pos;
		 */
		// System.out.println(one.getPointDifference(2));

		// System.out.println("D: " +
		// Math.abs(b.subtract(a).x)/one.sticks.get(0).length);

		// System.out.println("G: " + Math.acos((0.3*0.3)/0.2));
		// Math.sin

		/*
		 * Vec3d rs = b.subtract(a); Vector3d t1 = new Vector3d(1.0, 0.0, 0.0); Vector3d
		 * t2 = new Vector3d(rs.x, rs.y, 0.0); float theX = (float)
		 * (angleBetweenVecs(t1, t2)+Math.PI/2);
		 * 
		 * Vector3d t3 = new Vector3d(0.0, 0.0, 1.0); Vector3d t4 = new Vector3d(0.0,
		 * rs.y, rs.z); float theZ = (float) (angleBetweenVecs(t3, t4)+Math.PI/2);
		 * 
		 * 
		 * rightarm.rotateAngleY = (float) 0.0f; rightarm.rotateAngleX = (float) -theZ;
		 * rightarm.rotateAngleZ = (float) theX;
		 */
		/*
		 * float pitch2 = (float) RopeUtil.getPitch(one.points.get(1).pos,
		 * one.points.get(2).pos);
		 * 
		 * 
		 * rightarmlower.rotateAngleX = 0f; rightarmlower.rotateAngleY = 0f;
		 * rightarmlower.rotateAngleZ = (float) Math.toRadians(pitch2);
		 * 
		 */
		// rightarm.render(0.0625f);

		// rightarm.isHidden = true;

		// master.render(0.0625f);

		// rightarm.isHidden = false;

		// master.render(0.0625f);
	}

	public void specialRender(RopeSimulation sim) {
		//master.render(0.065f);
		
		// debug code
		
		Vec3d rs1 = sim.points.get(1).pos.subtract(sim.points.get(12).pos).normalize();
		//Vec3d rs = sim.points.get(2).pos.subtract(sim.points.get(3).pos).normalize();
		Vec3d rs = sim.points.get(3).pos;
		Vector3d t1 = new Vector3d(rs1.x, 0.0, rs1.z);
		Vector3d t2 = new Vector3d(rs.x, 0.0, rs.z);
		float theX = (float) (angleBetweenVecs(t1, t2));
		rs1 = rs1.rotatePitch((float) Math.PI/2);
		//rs = rs.rotateYaw((float) Math.PI);
		//rs = rs.rotatePitch((float) Math.PI);
		
		
		GlStateManager.pushMatrix();
		//GL11.glRotated(180, 1, 0, 0);
		//GL11.glRotated(180, 0, 1, 0);
		GlStateManager.disableTexture2D();
//		/GlStateManager.disableDepth();
		//GlStateManager.disableCull();
		/*
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(0, 0, 0);
		
		
		GL11.glVertex3d(rs1.x*2, rs1.y*2, rs1.z*2);
		Vec3d m = sim.points.get(1).pos;
		
		
		
		GL11.glVertex3d(m.x, 0-1.5, m.z);
	
		GL11.glVertex3d(rs.x*2, 0-1.5, rs.z*2);
		GL11.glEnd();*/
		GlStateManager.popMatrix();
		GlStateManager.enableTexture2D();
		//GlStateManager.enableDepth();
		
		t1 = new Vector3d(rs1.x, 0.0, rs1.z);
		t2 = new Vector3d(rs.x, 0.0, rs.z);
		theX = (float) (angleBetweenVecs(t1, t2));
		
		
		theX = (float) (theX*Math.signum(rs1.crossProduct(rs).y)+Math.PI);
		
	//	System.out.println(Math.toDegrees(theX));
		//System.out.println(Math.toDegrees(theX*Math.signum(rs1.crossProduct(rs).y))+180);
		
	
		//System.out.println(Math.toDegrees(theX));
		
		
		applyPos(sim.getPointDifference(0).scale(-1.0), head);
		applyStandardBoneTransform(sim, head, 0, 1);

		Vec3d o = sim.getPointDifference(1).scale(1.0);
		
		
		applyPos(o, body);
		applyBodyTransform(sim, body, 1, 12, 9);

		body.rotateAngleY = (float) ((float) -theX-Math.PI/2);
		
	//	applyPos(sim.points.get(3).pos, rightarm);
		applyLigamentTransforms(sim, rightarm, 3, 5, 7);

		//applyPos(sim.points.get(2).pos, leftarm);
		applyLigamentTransforms(sim, leftarm, 2, 4, 6);

		// applyPos(sim.getPointDifference(10).scale(-1), leftleg);
		applyLigamentTransforms(sim, leftleg, 10, 13, 14);

		// applyPos(sim.getPointDifference(11).scale(-1), rightleg);
		applyLigamentTransforms(sim, rightleg, 11, 15, 16);

		// applyPos(Vec3d.ZERO, rightarm);
		// applyLigamentTransforms(sim, rightarm, 0);

		// applyPos(new Vec3d(0.0, 0, 0), body);
		// applyStandardBoneTransform(sim, body, 0, 1);
		// head.render(0.065f);
		// rightarm.render(0.065f);
		// body.rotateAngleX = (float) Math.PI/3;
		// leftarm.render(0.065f);
		//body.rotateAngleY = 1.0f;
		master.render(0.065f);
		
		
		// head.render(0.065f);

		// master.offsetY = 1;
		// master.rotateAngleX = (float) Math.PI;
		// master.rotateAngleY = (float) Math.PI;
		// master.rotateAngleZ = (float) Math.PI;
		// master.render(0.065f);

	}
	
	public static Matrix4f createMatrix(Vec3d up, Vec3d side, Vec3d forward, Vec3d translation) {
		Matrix4f mat = new Matrix4f();
		
		mat.translate(new Vector3f((float) translation.x, (float) translation.y, (float) translation.z));
		
		mat.m00 = (float) side.x;
		mat.m01 = (float)up.x;
		mat.m02 = (float)forward.x;
		
		mat.m10 = (float)side.y;
		mat.m11 = (float)up.y;
		mat.m12 = (float)forward.y;
		
		mat.m20 = (float)side.z;
		mat.m21 = (float)up.z;
		mat.m22 = (float)forward.z;
		
	
		mat.m03 = (float) translation.x;
		mat.m13 = (float) translation.y;
		mat.m23 = (float) translation.z;
		
		mat.m30 = 0f;
		mat.m31 = 0f;
		mat.m32 = 0f;
		mat.m33 = 1f;
		
		
		return mat;
		
	}
	/**
	 * https://stackoverflow.com/questions/53970962/load-an-matrix4f-into-a-floatbuffer-so-my-shader-can-use-it
	 * @param size
	 * @return
	 */
	public static FloatBuffer createFloatBuffer(int size) {
		return ByteBuffer.allocateDirect(size << 2)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
	}
	
	public void renderRagdollPart(RopeSimulation sim, ModelRenderer part, Vec3d bodySide, int pointA, int pointB, int pointC, Vec3d offset, double pitchOffset) {
		// get orthogonal vectors
		Vec3d up = sim.points.get(pointA).pos.subtract(sim.points.get(pointB).pos).normalize();
		Vec3d forward = up.crossProduct(bodySide).normalize();
		Vec3d side = up.crossProduct(forward).normalize();
		
		// create buffer
		FloatBuffer buf = createFloatBuffer(16);
		
		Matrix4f mat = createMatrix(up, side, forward, Vec3d.ZERO);
		mat.invert();
		mat.rotate((float) pitchOffset, new Vector3f(1f, 0f, 0f));
		
		mat.store(buf);
		buf.rewind();
		
		// get pos
		Vec3d pos = sim.points.get(pointC).pos;
		
		// render
		double zero = 0;
		if(part == rightarm_r1) {
			zero = 0.1;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(pos.x, pos.y, pos.z);
		GlStateManager.multMatrix(buf);
		GlStateManager.translate(offset.x, offset.y, offset.z);
		
		part.render(0.0625f);
		
		GlStateManager.popMatrix();
		
		
	}
	
	public void setupRagdoll() {
		
		body.childModels.remove(rightarm);
		body.childModels.remove(leftarm);
		body.childModels.remove(rightleg);
		body.childModels.remove(leftleg);
		
		rightarm.childModels.remove(rightarm_r1);
		rightleg.childModels.remove(rightleg_r1);
		leftarm.childModels.remove(leftarm_r1);
		leftleg.childModels.remove(leftleg_r1);
		//setupRagdoll(master);
		/*
		setupRagdoll(body);
		setupRagdoll(rightarm);
		setupRagdoll(leftarm);
		
		setupRagdoll(leftleg);
		setupRagdoll(rightleg);*/
	}
	
	public void setupRagdoll(ModelRenderer mr) {
		
		mr.childModels.clear();
	}
	
public void setupRagdoll(ModelRenderer mr, ModelRenderer exem) {
		
		
		mr.childModels.clear();
	}
	
	/**
	 * Arms (-1.5, -7.0)
	 * Legs (0, -3)
	 * @param top
	 * @param bottom
	 * @param xTop
	 * @param yBottom
	 */
	public void setupLigamentRotPoints(ModelRenderer top, ModelRenderer bottom, float xTop, float yBottom) {
		top.rotationPointX = xTop;
		top.rotationPointY = -5.0f;
		
		bottom.rotationPointX = 0.0f;
		bottom.rotationPointY = yBottom;
	}

	public void specialRender2(RopeSimulation sim) {
		
		Vec3d up = sim.points.get(12).pos.subtract(sim.points.get(1).pos).normalize();
		Vec3d side = sim.points.get(2).pos.subtract(sim.points.get(3).pos).normalize();
		Vec3d forw = up.crossProduct(side).scale(1);
		
		Vec3d bodyCenter = sim.points.get(12).pos.add(sim.points.get(1).pos).scale(0.5);
	
		
		
		
		FloatBuffer buf = createFloatBuffer(16);
		
		
		
		Matrix4f mat = createMatrix(up, side, forw, new Vec3d(0, 0, 0));
		mat.invert();
		
		mat.store(buf);
		buf.rewind();
		
		GlStateManager.enableDepth();
		GlStateManager.pushMatrix();
		
		body.rotationPointY = -5f;
		GlStateManager.translate(bodyCenter.x, bodyCenter.y, bodyCenter.z);
		
		GlStateManager.multMatrix(buf);
		GlStateManager.translate(0.0, 0.0, 0.0);
		body.render(0.065f);
		GlStateManager.popMatrix();


		
		renderRagdollPart(sim, head, side, 0, 1, 1, new Vec3d(0, 1.5, 0), Math.PI);
		//leftarm_r1.isHidden = true;
		
		/*
		leftarm.rotationPointX = -1.5f;
		leftarm.rotationPointY = -5.0f;
		
		leftarm_r1.rotationPointX = 0.0f;
		leftarm_r1.rotationPointY = -7.0f;*/
		setupLigamentRotPoints(leftarm, leftarm_r1, -1.5f, -7.0f);
		renderRagdollPart(sim, leftarm, side, 2, 4, 4, new Vec3d(0, 0, 0), Math.PI);
		renderRagdollPart(sim, leftarm_r1, side, 4, 6, 6, new Vec3d(0, 0, 0), Math.PI);
		
		setupLigamentRotPoints(rightarm, rightarm_r1, 1.5f, -7.0f);
		renderRagdollPart(sim, rightarm, side, 3, 5, 5, new Vec3d(0, 0, 0), Math.PI);
		renderRagdollPart(sim, rightarm_r1, side, 5, 7, 7, new Vec3d(0, 0, 0), Math.PI);
		
		//leftleg.offsetY = 0f;
		
		
		setupLigamentRotPoints(leftleg, leftleg_r1, 0.0f, -3.0f);
		renderRagdollPart(sim, leftleg, side, 10, 13, 13, new Vec3d(0, 0.0, 0.1), Math.PI);
		renderRagdollPart(sim, leftleg_r1, side, 13, 14, 14, new Vec3d(0.0, 0.0, 0.1), Math.PI);
		
		setupLigamentRotPoints(rightleg, rightleg_r1, 0.0f, -3.0f);
		renderRagdollPart(sim, rightleg, side, 11, 15, 15, new Vec3d(0, 0, 0.1), Math.PI);
		renderRagdollPart(sim, rightleg_r1, side, 15, 16, 16, new Vec3d(0, 0, 0.1), Math.PI);
		
		
	
		
	}
	

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		body.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}