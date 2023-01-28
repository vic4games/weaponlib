package com.jimholden.conomy.entity.models;

import java.util.HashMap;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import org.apache.logging.log4j.core.lookup.Interpolator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Quaternion;

import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.entity.render.RenderModZombie;
import com.jimholden.conomy.entity.rope.ChainedRopeSimulation;
import com.jimholden.conomy.entity.rope.RopeSimulation;
import com.jimholden.conomy.entity.rope.RopeSimulation.Point;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.RopeUtil;
import com.jimholden.conomy.util.animations.AnimationJSONTool;
import com.jimholden.conomy.util.animations.AnimationPlayer;
import com.jimholden.conomy.util.animations.AnimationState;
import com.jimholden.conomy.util.animations.EntityAnimationState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

public class GhoulModel extends ModelBase {

	private AnimationPlayer defaultState = null;

	private AnimationState state = null;
	private AnimationPlayer player = null;
	private AnimationPlayer attackAnimationPlayer = null;
	private AnimationPlayer chaseAnimationPlayer = null;
	private AnimationPlayer sleepOne = null;
	private AnimationPlayer sleepTwo = null;
	private AnimationPlayer sleepThree = null;
	private AnimationPlayer deathOne = null;
	private AnimationPlayer deathTwo = null;
	private AnimationPlayer deathF1 = null;
	private AnimationPlayer deathF2 = null;
	private AnimationPlayer deathF3 = null;
	private AnimationPlayer deathL1 = null;
	private AnimationPlayer deathL2 = null;
	private AnimationPlayer deathL3 = null;
	private AnimationPlayer deathR1 = null;
	private AnimationPlayer deathR2 = null;
	private AnimationPlayer deathR3 = null;

	private final ModelRenderer master;
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer body_r1;
	private final ModelRenderer rightarm;
	private final ModelRenderer rightarmlower;
	private final ModelRenderer rightarm_r1;
	private final ModelRenderer leftarm;
	private final ModelRenderer leftarmlower;
	private final ModelRenderer leftarm_r1;
	private final ModelRenderer rightleg;
	private final ModelRenderer rightleglower;
	private final ModelRenderer rightleg_r1;
	private final ModelRenderer leftleg;
	private final ModelRenderer leftleglower;
	private final ModelRenderer leftleg_r1;

	public GhoulModel() {
		textureWidth = 64;
		textureHeight = 64;

		master = new ModelRenderer(this);
		master.setRotationPoint(0.0F, 24.0F, 0.0F);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -23.5F, -0.3F);
		master.addChild(head);
		setRotationAngle(head, 0.0873F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 5, 8, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 13, -3.999F, -3.0F, -2.0F, 8, 3, 6, 0.0F, false));
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
		rightarm.setRotationPoint(-5.0F, -21.5F, -0.3F);
		master.addChild(rightarm);
		setRotationAngle(rightarm, 0.0436F, 0.0F, 0.0873F);
		rightarm.cubeList.add(new ModelBox(rightarm, 32, 44, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		rightarmlower = new ModelRenderer(this);
		rightarmlower.setRotationPoint(-1.0F, 4.0F, 0.0F);
		rightarm.addChild(rightarmlower);

		rightarm_r1 = new ModelRenderer(this);
		rightarm_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightarmlower.addChild(rightarm_r1);
		setRotationAngle(rightarm_r1, -0.0873F, 0.0F, -0.0436F);
		rightarm_r1.cubeList.add(new ModelBox(rightarm_r1, 16, 34, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));

		leftarm = new ModelRenderer(this);
		leftarm.setRotationPoint(5.0F, -21.5F, -0.35F);
		master.addChild(leftarm);
		setRotationAngle(leftarm, 0.0436F, 0.0F, -0.0873F);
		leftarm.cubeList.add(new ModelBox(leftarm, 16, 44, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		leftarmlower = new ModelRenderer(this);
		leftarmlower.setRotationPoint(1.0F, 4.0F, 0.0F);
		leftarm.addChild(leftarmlower);

		leftarm_r1 = new ModelRenderer(this);
		leftarm_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftarmlower.addChild(leftarm_r1);
		setRotationAngle(leftarm_r1, -0.1309F, 0.0F, 0.0436F);
		leftarm_r1.cubeList.add(new ModelBox(leftarm_r1, 32, 34, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));

		rightleg = new ModelRenderer(this);
		rightleg.setRotationPoint(-2.0F, -12.3F, 0.3F);
		master.addChild(rightleg);
		setRotationAngle(rightleg, -0.1309F, 0.0F, 0.0436F);
		rightleg.cubeList.add(new ModelBox(rightleg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F, false));

		rightleglower = new ModelRenderer(this);
		rightleglower.setRotationPoint(0.0F, 7.0F, 0.0F);
		rightleg.addChild(rightleglower);

		rightleg_r1 = new ModelRenderer(this);
		rightleg_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightleglower.addChild(rightleg_r1);
		setRotationAngle(rightleg_r1, 0.2618F, 0.0F, -0.0436F);
		rightleg_r1.cubeList.add(new ModelBox(rightleg_r1, 40, 0, -2.0001F, -0.2479F, -1.9673F, 4, 6, 4, 0.0F, false));

		/*
		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(2.0F, -12.3F, 0.3F);
		
		master.addChild(leftleg);
		
		setRotationAngle(leftleg, -0.1309F, 0.0F, -0.0873F);
		leftleg.cubeList.add(new ModelBox(leftleg, 28, 9, -2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F, false));
		*/
		
		leftleg = new ModelRenderer(this);
		leftleg.setRotationPoint(-2.0F, 12.3F, 0.3F);
		
		body.addChild(leftleg);
		
		setRotationAngle(leftleg, 0.1309F, 0.0F, 0.0873F);
		leftleg.cubeList.add(new ModelBox(leftleg, 28, 9, -2.0F, 0.0F, -2.0F, 4, 7, 4, 0.0F, false));
		
		
		leftleglower = new ModelRenderer(this);
		leftleglower.setRotationPoint(0.0F, 7.0F, 0.0F);
		leftleg.addChild(leftleglower);

		leftleg_r1 = new ModelRenderer(this);
		leftleg_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftleglower.addChild(leftleg_r1);
		setRotationAngle(leftleg_r1, 0.2618F, 0.0F, 0.0436F);
		leftleg_r1.cubeList.add(new ModelBox(leftleg_r1, 0, 43, -1.9997F, -0.249F, -1.9779F, 4, 6, 4, 0.0F, false));
	
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

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		EntityBaseZombie zombie = (EntityBaseZombie) entity;
		GL11.glPushMatrix();
		double translate = 0;

		if (zombie.getDecomposeTime() < 40) {
			// System.out.println(zombie.getDecomposeTime());
			double counter = 40 - zombie.getDecomposeTime();
			translate = (1.0F * (counter / 40.0F));
			// System.out.println(translate);
		}
		GL11.glTranslated(0, translate, 0);

		if (((EntityBaseZombie) entity).isHeadBlownOff()) {
			head.isHidden = true;
		} else {
			head.isHidden = false;
		}

		if (zombie.isCorpse()) {
			RenderModZombie.ZOMBIE_DECAY.use();
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + 3);
			Minecraft.getMinecraft().getTextureManager()
					.bindTexture(new ResourceLocation(Reference.MOD_ID + ":" + "textures/shaders/noise_1.png"));
			GL20.glUniform1i(GL20.glGetUniformLocation(RenderModZombie.ZOMBIE_DECAY.getShaderId(), "noise"), 3);
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

			int time = GL20.glGetUniformLocation(RenderModZombie.ZOMBIE_DECAY.getShaderId(), "time");
			int ticks = Minecraft.getMinecraft().player.ticksExisted;
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			// System.out.println(zombie.getDecomposeTime());
			GL20.glUniform1f(time, (float) (240 - zombie.getDecomposeTime()) / 240.0f);
			GlStateManager.enableAlpha();
			// GlStateManager.enableBlend();

			master.render(f5);

			RenderModZombie.ZOMBIE_DECAY.release();

		} else {
			master.render(f5);
		}

		GL11.glPopMatrix();

	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void doDeathAnimations(EntityBaseZombie zombie) {
		int selector = zombie.getDeathAnimationSelector();
		EnumFacing facing = zombie.getDeathDirection();

		switch (facing) {
		default:
			if (selector == 0) {
				this.deathOne.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}
			if (selector == 1) {
				this.deathTwo.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}

		case NORTH:
			if (selector == 0) {
				this.deathF1.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}
			if (selector == 1) {
				this.deathF2.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}

		case WEST:
			if (selector == 0) {
				this.deathL1.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}
			if (selector == 1) {
				this.deathL2.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}
			if (selector == 2) {
				this.deathL3.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}

		case EAST:
			if (selector == 0) {
				this.deathR1.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}
			if (selector == 1) {
				// System.out.println(this.deathR2 + " | " + selector + " | " + facing);
				this.deathR2.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}
			if (selector == 2) {
				this.deathR3.playNoLoop(zombie, 0.1F, false, true, true);
				break;
			}

		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {

		// state = null;
		if (state == null) {

			state = AnimationJSONTool.loadAnimationFile("ghoul",
					new ResourceLocation(Reference.MOD_ID + ":animations/ghoul/proghoulanim.json"));
			state.addBoneMapping("master", master);
			state.addBoneMapping("head", head);
			state.addBoneMapping("body", body);
			state.addBoneMapping("leftarm", leftarm);
			state.addBoneMapping("leftarmlower", leftarmlower);
			state.addBoneMapping("rightarm", rightarm);
			state.addBoneMapping("rightarmlower", rightarmlower);
			state.addBoneMapping("leftleg", leftleg);
			state.addBoneMapping("rightleg", rightleg);
			state.addBoneMapping("rightleglower", rightleglower);
			state.addBoneMapping("leftleglower", leftleglower);

		}

		if (!this.state.animStatesEnt.containsKey(entityIn)) {
			this.state.animStatesEnt.put(entityIn, new EntityAnimationState(entityIn, this.state));
		}

		this.state.getState(entityIn).load();
		if (this.player == null) {
			this.player = new AnimationPlayer(state, "walkcycle");
			this.attackAnimationPlayer = new AnimationPlayer(state, "attack");
			this.chaseAnimationPlayer = new AnimationPlayer(state, "runcycle");
			this.sleepOne = new AnimationPlayer(state, "sleepingpose1");
			this.sleepTwo = new AnimationPlayer(state, "sleepingpose2");
			this.sleepThree = new AnimationPlayer(state, "sleepingpose3");
			this.deathOne = new AnimationPlayer(state, "death1");
			this.deathTwo = new AnimationPlayer(state, "death2");
			this.deathF1 = new AnimationPlayer(state, "deathfront1");
			this.deathF2 = new AnimationPlayer(state, "deathfront2");
			this.deathL1 = new AnimationPlayer(state, "deathleft1");
			this.deathL2 = new AnimationPlayer(state, "deathleft2");
			this.deathL3 = new AnimationPlayer(state, "deathleft3");
			this.deathR1 = new AnimationPlayer(state, "deathright1");
			this.deathR2 = new AnimationPlayer(state, "deathright2");
			this.deathR3 = new AnimationPlayer(state, "deathright3");
			this.defaultState = state.getDefaultAnimationPlayer();
		}

		if (entityIn.ticksExisted < 20)
			this.player.play(entityIn, 0.0F, false, true, false);
		EntityBaseZombie zombie = ((EntityBaseZombie) entityIn);

		int state = ((EntityBaseZombie) entityIn).getMovementState();

		if (zombie.isCorpse()) {
			zombie.hurtTime = 0;
			doDeathAnimations(zombie);
			// this.deathOne.playNoLoop(entityIn, 0.1F, false, true, true);
		}

		if (zombie.isCorpse())
			return;

		boolean check = entityIn.motionX > 0.0005 || entityIn.motionX < -0.0005;
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

		if (((EntityBaseZombie) entityIn).isWalking()) {
			this.player.play(entityIn, 0.7F, false, true, true);
		} else {
			// System.out.println("load " + this.defaultState);
			this.defaultState.play(entityIn, 1.0f, false, false, false);
			// this.defaultState.getState(entityIn).load();
		}
		if (state == 2) {
			this.chaseAnimationPlayer.play(entityIn, 0.3F, false, true, true);
		}
		if (state == 3) {
			// System.out.println("yo");
			this.sleepOne.play(entityIn, 0.0F, false, true, true);
		}
		if (state == 4) {
			this.sleepTwo.play(entityIn, 0.0F, false, true, true);
		}
		if (state == 5) {
			this.sleepThree.play(entityIn, 0.0F, false, true, true);
		}

		if (swingProgress > 0.0F) {
			this.attackAnimationPlayer.play(entityIn, 0.7F, true, true, true);
		}

		this.head.rotateAngleX = headPitch * 0.017453292F;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;

		if (zombie.isAngry()) {
			this.head.rotateAngleX += Math.random() * 0.2;
			this.head.rotateAngleY += Math.random() * 0.7;

		}

		this.state.getState(entityIn).save();
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

	public Quat4d rotationLol(Vec3d a, Vec3d b) {

		Quat4d quat = new Quat4d();
		Vec3d cross = a.crossProduct(b);

		quat.x = a.x;
		quat.y = a.y;
		quat.z = a.z;
		quat.w = Math.sqrt((a.lengthSquared()) * (b.lengthSquared())) + a.dotProduct(b);

		quat.normalize();
		return quat;
	}

	public Vec3d eulerAngle(Quat4d q1) {
		double sqw = q1.w * q1.w;
		double sqx = q1.x * q1.x;
		double sqy = q1.y * q1.y;
		double sqz = q1.z * q1.z;
		double heading = Math.atan2(2.0 * (q1.x * q1.y + q1.z * q1.w), (sqx - sqy - sqz + sqw));
		double bank = Math.atan2(2.0 * (q1.y * q1.z + q1.x * q1.w), (-sqx - sqy + sqz + sqw));
		double attitude = Math.asin(-2.0 * (q1.x * q1.z - q1.y * q1.w) / (sqx + sqy + sqz + sqw));
		return new Vec3d(Math.toDegrees(heading), Math.toDegrees(attitude), Math.toDegrees(bank));
	}

	public void magiTransform(Vec3d a, Vec3d b, ModelRenderer r) {
		Quaternion q = new Quaternion();

		GlStateManager.disableTexture2D();
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex3d(a.x, a.y, a.z);
		GL11.glVertex3d(b.x, b.y, b.z);
		GL11.glEnd();
		GlStateManager.enableTexture2D();

		applyPos(a.add(b).scale(0.5), r);

		Quat4d quat = rotationLol(a.subtract(b), new Vec3d(1, 0, 0));
		Vec3d euler = eulerAngle(quat);

		Vec3d vec = Vec3d.fromPitchYaw((float) euler.x, (float) euler.y);
		GlStateManager.disableTexture2D();
		GL11.glBegin(GL11.GL_LINE_STRIP);
		Vec3d c = a.add(b).scale(0.5);
		GL11.glVertex3d(c.x, c.y, c.z);
		GL11.glVertex3d(vec.x, vec.y, vec.z);
		GL11.glEnd();
		GlStateManager.enableTexture2D();
		
		
		applyRot(euler, r);
		// Vec3d p = a.pos;
		/*
		 * r.offsetX = (float) p.x; r.offsetY = (float) p.y; r.offsetZ = (float) p.z;
		 */

	//	r.render(0.065f);
	}

	public HashMap<ModelRenderer, Vec3d> defaults;
	
	public void addDefault(ModelRenderer m) {
		Vec3d p = new Vec3d(m.offsetX, m.offsetY, m.offsetZ);
		defaults.put(m, p);
	}
	
	public double angleBetweenVecs(Vector3d v1, Vector3d v2) {
	
		double p1 = v1.dot(v2);
	
		//System.out.println(v2);
		//System.out.println(v1.z*v2.z);
	
		double p2 = (v1.length()*v2.length());
		
		//System.out.println(p1 + " | " + p2);
		return Math.acos(p1/p2);
	}
	
	
	public void applyStandardBoneTransform(RopeSimulation sim, ModelRenderer lig, int p1, int p2) {
		Vec3d a = sim.points.get(p1).pos;
		Vec3d b = sim.points.get(p2).pos;
		
	
		
		Vec3d rs = b.subtract(a);
		Vector3d t1 = new Vector3d(1.0, 0.0, 0.0);
		Vector3d t2 = new Vector3d(rs.x, rs.y, 0.0);
		float theX = (float) (angleBetweenVecs(t1, t2)+Math.PI/2);

		Vector3d t3 = new Vector3d(0.0, 0.0, 1.0);
		Vector3d t4 = new Vector3d(0.0, rs.y, rs.z);
		float theZ = (float) (angleBetweenVecs(t3, t4)+Math.PI/2);
	
		lig.rotateAngleX = (float) -theZ;
		lig.rotateAngleZ = (float) theX;
	}
	
	public void applyLigamentTransforms(RopeSimulation sim, ModelRenderer lig, int top, int mid, int bottom) {
		
		
		Vec3d a = sim.points.get(top).pos;
		Vec3d b = sim.points.get(mid).pos;
		
	
		
		Vec3d rs = b.subtract(a);
		Vector3d t1 = new Vector3d(1.0, 0.0, 0.0);
		Vector3d t2 = new Vector3d(rs.x, rs.y, 0.0);
		float theX = (float) (angleBetweenVecs(t1, t2)+Math.PI/2);

		Vector3d t3 = new Vector3d(0.0, 0.0, 1.0);
		Vector3d t4 = new Vector3d(0.0, rs.y, rs.z);
		float theZ = (float) (angleBetweenVecs(t3, t4)+Math.PI/2);
	
		lig.rotateAngleX = (float) -theZ;
		lig.rotateAngleZ = (float) theX;
		
		ModelRenderer joint = lig.childModels.get(0);
		
		// lower
		a = sim.points.get(mid).pos;
		b = sim.points.get(bottom).pos;
		
	
		
		rs = b.subtract(a);
		t1 = new Vector3d(1.0, 0.0, 0.0);
		t2 = new Vector3d(rs.x, rs.y, 0.0);
		theX = (float) (angleBetweenVecs(t1, t2)-Math.PI/2);

		t3 = new Vector3d(0.0, 0.0, 1.0);
		t4 = new Vector3d(0.0, rs.y, rs.z);
		theZ = (float) (angleBetweenVecs(t3, t4)-Math.PI/2);
	
		joint.rotateAngleX = (float) -theZ/2;
		joint.rotateAngleZ = (float) theX/2;
		
		
		
	}
	
	public void srender2(ChainedRopeSimulation crs) {
		
		/*
		GlStateManager.disableTexture2D();
		GL11.glBegin(GL11.GL_LINES);
	
		GlStateManager.color(1.0f, 0.0f, 0.0f);
		GL11.glVertex3d(0.0, 0.0, 0.0);
		GL11.glVertex3d(1.0, 0.0, 0.0);
		
		GlStateManager.color(0.0f, 1.0f, 0.0f);
		GL11.glVertex3d(0.0, 0.0, 0.0);
		GL11.glVertex3d(0.0, 1.0, 0.0);
		
		GlStateManager.color(0.0f, 0.0f, 1.0f);
		GL11.glVertex3d(0.0, 0.0, 0.0);
		GL11.glVertex3d(0.0, 0.0, 1.0);
		GL11.glEnd();
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		GlStateManager.enableTexture2D();*/
		
		RopeSimulation one = crs.sims.get(0);
		
		applyPos(new Vec3d(-0.1, 0, 0), rightarm);
		//applyLigamentTransforms(one, rightarm, 0);
		//applyLigamentTransforms(one, rightarmlower, 1);
		
		RopeSimulation two = crs.sims.get(1);
		
		applyPos(new Vec3d(0.1, 0.0, 0), leftarm);
		//applyPos(two.getPointDifference(0).scale(-1), leftarm);
		//applyLigamentTransforms(one, leftarm, 0);
	//	applyLigamentTransforms(one, leftarmlower, 1);
		
		/*
		float pitch = (float) RopeUtil.getPitch(one.points.get(0).pos, one.points.get(1).pos);
		float yaw = (float) RopeUtil.getPitch(new Vec3d(0.0, 1.0, 0), one.points.get(1).pos.normalize());
		float roll = (float) RopeUtil.getPitch(new Vec3d(1.0, 0.0, 0.0), one.points.get(1).pos.normalize());
		
		
		
		Vec3d a  = one.points.get(0).pos;
		Vec3d b =  one.points.get(1).pos;
		*/
		//System.out.println(one.getPointDifference(2));
		
		//System.out.println("D: " + Math.abs(b.subtract(a).x)/one.sticks.get(0).length);
		
		
		//System.out.println("G: " + Math.acos((0.3*0.3)/0.2));
		//Math.sin
		
		/*
		Vec3d rs = b.subtract(a);
		Vector3d t1 = new Vector3d(1.0, 0.0, 0.0);
		Vector3d t2 = new Vector3d(rs.x, rs.y, 0.0);
		float theX = (float) (angleBetweenVecs(t1, t2)+Math.PI/2);

		Vector3d t3 = new Vector3d(0.0, 0.0, 1.0);
		Vector3d t4 = new Vector3d(0.0, rs.y, rs.z);
		float theZ = (float) (angleBetweenVecs(t3, t4)+Math.PI/2);
	
		
		rightarm.rotateAngleY = (float) 0.0f;
		rightarm.rotateAngleX = (float) -theZ;
		rightarm.rotateAngleZ = (float) theX;
		*/
		/*
		float pitch2 = (float) RopeUtil.getPitch(one.points.get(1).pos, one.points.get(2).pos);
		
		
		rightarmlower.rotateAngleX = 0f;
		rightarmlower.rotateAngleY = 0f;
		rightarmlower.rotateAngleZ = (float) Math.toRadians(pitch2);
		
		*/
		//rightarm.render(0.0625f);
		
		
		//rightarm.isHidden = true;
		
		
		master.render(0.0625f);
		
		//rightarm.isHidden = false;
		
		//master.render(0.0625f);
	}
	
	public void specialRender(RopeSimulation sim) {
		
		applyPos(sim.getPointDifference(0).scale(1.0), head);
		applyStandardBoneTransform(sim, head, 0, 1);
		
		applyPos(sim.getPointDifference(1).scale(-1.0), body);
		applyStandardBoneTransform(sim, body, 1, 12);
		
		applyPos(sim.points.get(3).pos, rightarm);
		applyLigamentTransforms(sim, rightarm, 3, 5, 7);
		
		applyPos(sim.points.get(2).pos, leftarm);
		applyLigamentTransforms(sim, leftarm, 2, 4, 6);
		
		//applyPos(sim.getPointDifference(10).scale(-1), leftleg);
		applyLigamentTransforms(sim, leftleg, 10, 13, 14);
		
		//applyPos(sim.getPointDifference(11).scale(-1), rightleg);
		applyLigamentTransforms(sim, rightleg,11, 15, 16);
		
		//applyPos(Vec3d.ZERO, rightarm);
		//applyLigamentTransforms(sim, rightarm, 0);
		
		
	//	applyPos(new Vec3d(0.0, 0, 0), body);
	//	applyStandardBoneTransform(sim, body, 0, 1);
	//	head.render(0.065f);
	//	rightarm.render(0.065f);
		//body.rotateAngleX = (float) Math.PI/3;
	//	leftarm.render(0.065f);
		master.render(0.065f);
		//head.render(0.065f);
		
		//master.offsetY = 1;
		//master.rotateAngleX = (float) Math.PI;
		//master.rotateAngleY = (float) Math.PI;
		//master.rotateAngleZ = (float) Math.PI;
	//	master.render(0.065f);

	}
}