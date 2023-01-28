package com.jimholden.conomy.entity.models;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.jimholden.conomy.client.gui.player.DeathGui;
import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.render.tesr.PistolStandTESR;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.animations.Animation;
import com.jimholden.conomy.util.animations.AnimationJSONTool;
import com.jimholden.conomy.util.animations.AnimationPlayTool;
import com.jimholden.conomy.util.animations.AnimationPlayer;
import com.jimholden.conomy.util.animations.AnimationState;
import com.jimholden.conomy.util.animations.Bone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class DeerModel extends ModelBase {
	private AnimationState state = null;
	private AnimationPlayer player = null;
	
	private final ModelRenderer bone5;
	private final ModelRenderer tail_r1;
	private final ModelRenderer backbody_r1;
	private final ModelRenderer frontbody_r1;
	private final ModelRenderer neck;
	private final ModelRenderer cube_r1;
	private final ModelRenderer head;
	private final ModelRenderer cube_r2;
	private final ModelRenderer nose;
	private final ModelRenderer cube_r3;
	private final ModelRenderer ears;
	private final ModelRenderer leftear;
	private final ModelRenderer cube_r4;
	private final ModelRenderer rightear;
	private final ModelRenderer cube_r5;
	private final ModelRenderer bone;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer frontrightleg;
	private final ModelRenderer cube_r8;
	private final ModelRenderer bone22;
	private final ModelRenderer cube_r9;
	private final ModelRenderer bone23;
	private final ModelRenderer cube_r10;
	private final ModelRenderer bone24;
	private final ModelRenderer cube_r11;
	private final ModelRenderer bone25;
	private final ModelRenderer frontleftleg;
	private final ModelRenderer cube_r12;
	private final ModelRenderer bone18;
	private final ModelRenderer cube_r13;
	private final ModelRenderer bone19;
	private final ModelRenderer cube_r14;
	private final ModelRenderer bone20;
	private final ModelRenderer cube_r15;
	private final ModelRenderer bone21;
	private final ModelRenderer backrightleg;
	private final ModelRenderer cube_r16;
	private final ModelRenderer bone15;
	private final ModelRenderer cube_r17;
	private final ModelRenderer bone16;
	private final ModelRenderer cube_r18;
	private final ModelRenderer bone17;
	private final ModelRenderer backleftleg;
	private final ModelRenderer cube_r19;
	private final ModelRenderer bone12;
	private final ModelRenderer cube_r20;
	private final ModelRenderer bone13;
	private final ModelRenderer cube_r21;
	private final ModelRenderer bone14;
	private final ModelRenderer body;
	private final ModelRenderer tail;

	public DeerModel() {
		textureWidth = 128;
		textureHeight = 128;

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 4.0F, 5.0F);
		

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone5.addChild(body);
		

		backbody_r1 = new ModelRenderer(this);
		backbody_r1.setRotationPoint(-1.0F, 0.25F, 6.0F);
		body.addChild(backbody_r1);
		setRotationAngle(backbody_r1, 0.0524F, 0.0F, 0.0F);
		backbody_r1.cubeList.add(new ModelBox(backbody_r1, 0, 0, -4.5F, -5.0F, -7.0F, 11, 10, 14, 0.0F, false));

		frontbody_r1 = new ModelRenderer(this);
		frontbody_r1.setRotationPoint(-1.0F, -0.25F, -6.0F);
		body.addChild(frontbody_r1);
		setRotationAngle(frontbody_r1, -0.1309F, 0.0F, 0.0F);
		frontbody_r1.cubeList.add(new ModelBox(frontbody_r1, 0, 24, -4.5F, -5.0F, -7.0F, 11, 10, 14, 0.0F, false));

		tail = new ModelRenderer(this);
		tail.setRotationPoint(-0.25F, -1.4829F, 12.6078F);
		bone5.addChild(tail);
		

		tail_r1 = new ModelRenderer(this);
		tail_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tail.addChild(tail_r1);
		setRotationAngle(tail_r1, -0.6545F, 0.0F, 0.0F);
		tail_r1.cubeList.add(new ModelBox(tail_r1, 50, 36, -2.0F, -1.8044F, -1.1033F, 4, 3, 3, 0.0F, false));

		neck = new ModelRenderer(this);
		neck.setRotationPoint(0.0F, 1.189F, -12.3195F);
		bone5.addChild(neck);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, -3.0F, 0.0F);
		neck.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.2182F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 44, 18, -3.5F, -6.5F, -2.5F, 7, 12, 6, -0.1F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -8.0552F, -0.6158F);
		neck.addChild(head);
		

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, -3.6F, -1.0F);
		head.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0436F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 42, 42, -3.5F, -3.5F, -4.5F, 7, 7, 8, 0.1F, false));

		nose = new ModelRenderer(this);
		nose.setRotationPoint(0.0F, -2.6F, -5.0F);
		head.addChild(nose);
		

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
		nose.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.1745F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 64, 43, -2.5F, -0.4118F, -3.4583F, 5, 3, 3, 0.1F, false));

		ears = new ModelRenderer(this);
		ears.setRotationPoint(0.0F, -7.728F, 0.8663F);
		head.addChild(ears);
		

		leftear = new ModelRenderer(this);
		leftear.setRotationPoint(3.5F, 1.0F, 0.0F);
		ears.addChild(leftear);
		

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, -1.0F, 0.0F);
		leftear.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.6109F, -0.2618F, -1.0908F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 36, 28, -2.0F, -1.5F, -0.5F, 3, 3, 1, 0.0F, false));

		rightear = new ModelRenderer(this);
		rightear.setRotationPoint(-3.5F, 1.0F, 0.0F);
		ears.addChild(rightear);
		

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, -1.0F, 0.0F);
		rightear.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.6109F, 0.2618F, 1.0908F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 36, 24, -1.0F, -1.5F, -0.5F, 3, 3, 1, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-0.4014F, -6.6771F, -2.6935F);
		head.addChild(bone);
		

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(3.4014F, -6.3229F, 0.6935F);
		bone.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, -0.3491F, 0.1745F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 83, 0, 0.0F, -6.0F, -5.0F, 0, 12, 10, 0.0F, true));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-2.5986F, -6.3229F, 0.6935F);
		bone.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.3491F, -0.1745F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 83, 0, 0.0F, -6.0F, -5.0F, 0, 12, 10, 0.0F, false));

		frontrightleg = new ModelRenderer(this);
		frontrightleg.setRotationPoint(-4.0F, 2.1147F, -9.4497F);
		bone5.addChild(frontrightleg);
		

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, 2.3853F, -0.8003F);
		frontrightleg.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.2182F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 36, 0, -2.0F, -4.5F, -2.0F, 4, 8, 5, 0.0F, false));

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(0.25F, 5.8683F, -0.6138F);
		frontrightleg.addChild(bone22);
		

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, 0.1168F, -0.1492F);
		bone22.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.3491F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 64, 13, -2.0F, -1.5F, -2.0F, 4, 3, 4, 0.0F, false));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone22.addChild(bone23);
		

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.0F, 9.7512F, -1.4764F);
		bone23.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0873F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 64, -2.0F, -11.0F, 1.0F, 4, 9, 3, -0.1F, false));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(0.0F, 7.25F, 1.0F);
		bone23.addChild(bone24);
		

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, 4.8817F, -0.8862F);
		bone24.addChild(cube_r11);
		setRotationAngle(cube_r11, -0.0436F, 0.0F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 28, 66, -2.0F, -5.0F, -1.0F, 4, 4, 3, -0.1F, false));

		bone25 = new ModelRenderer(this);
		bone25.setRotationPoint(0.0F, 3.25F, -0.5F);
		bone24.addChild(bone25);
		bone25.cubeList.add(new ModelBox(bone25, 50, 13, -2.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F, false));

		frontleftleg = new ModelRenderer(this);
		frontleftleg.setRotationPoint(4.0F, 2.1147F, -9.4497F);
		bone5.addChild(frontleftleg);
		

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(0.0F, 2.3853F, -0.8003F);
		frontleftleg.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.2182F, 0.0F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 54, 0, -2.0F, -4.5F, -2.0F, 4, 8, 5, 0.0F, false));

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(-0.25F, 5.8683F, -0.6138F);
		frontleftleg.addChild(bone18);
		

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(0.0F, 0.1168F, -0.1492F);
		bone18.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.3491F, 0.0F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 64, 36, -2.0F, -1.5F, -2.0F, 4, 3, 4, 0.0F, false));

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone18.addChild(bone19);
		

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(0.0F, 9.7512F, -1.4764F);
		bone19.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0873F, 0.0F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 14, 64, -2.0F, -11.0F, 1.0F, 4, 9, 3, -0.1F, false));

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(0.0F, 7.25F, 1.0F);
		bone19.addChild(bone20);
		

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(0.0F, 4.8817F, -0.8862F);
		bone20.addChild(cube_r15);
		setRotationAngle(cube_r15, -0.0436F, 0.0F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 42, 69, -2.0F, -5.0F, -1.0F, 4, 4, 3, -0.1F, false));

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(0.0F, 3.25F, -0.5F);
		bone20.addChild(bone21);
		bone21.cubeList.add(new ModelBox(bone21, 67, 0, -2.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F, false));

		backrightleg = new ModelRenderer(this);
		backrightleg.setRotationPoint(-4.0F, 1.1495F, 10.5521F);
		bone5.addChild(backrightleg);
		

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-0.25F, 1.0654F, -0.5084F);
		backrightleg.addChild(cube_r16);
		setRotationAngle(cube_r16, -0.0436F, 0.0F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 48, -2.0F, -4.0F, -2.5F, 4, 10, 6, 0.0F, false));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(0.0F, 5.8335F, 0.3845F);
		backrightleg.addChild(bone15);
		

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(0.0F, -1.983F, -2.4366F);
		bone15.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.3491F, 0.0F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 40, 57, -2.0F, 0.0F, -1.0F, 4, 7, 5, 0.0F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.0F, 3.0F, 1.75F);
		bone15.addChild(bone16);
		

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(0.0F, 1.1665F, -0.8845F);
		bone16.addChild(cube_r18);
		setRotationAngle(cube_r18, -0.3491F, 0.0F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 0, -2.0F, -2.0F, -1.0F, 4, 11, 3, -0.1F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(0.0F, 9.5F, -3.25F);
		bone16.addChild(bone17);
		bone17.cubeList.add(new ModelBox(bone17, 64, 20, -2.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F, false));

		backleftleg = new ModelRenderer(this);
		backleftleg.setRotationPoint(4.0F, 1.1495F, 10.5521F);
		bone5.addChild(backleftleg);
		

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(0.25F, 1.0654F, -0.5084F);
		backleftleg.addChild(cube_r19);
		setRotationAngle(cube_r19, -0.0436F, 0.0F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 20, 48, -2.0F, -4.0F, -2.5F, 4, 10, 6, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(0.0F, 5.8335F, 0.3845F);
		backleftleg.addChild(bone12);
		

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(0.0F, -1.983F, -2.4366F);
		bone12.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.3491F, 0.0F, 0.0F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 58, 58, -2.0F, 0.0F, -1.0F, 4, 7, 5, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(0.0F, 3.0F, 1.75F);
		bone12.addChild(bone13);
		

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(0.0F, 1.1665F, -0.8845F);
		bone13.addChild(cube_r21);
		setRotationAngle(cube_r21, -0.3491F, 0.0F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 24, -2.0F, -2.0F, -1.0F, 4, 11, 3, -0.1F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.0F, 9.5F, -3.25F);
		bone13.addChild(bone14);
		bone14.cubeList.add(new ModelBox(bone14, 69, 54, -2.0F, -0.5F, -1.5F, 4, 1, 3, 0.0F, false));
		
		
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		
		
		bone5.render(f5);
		
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	float testTicker = 0;
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		
		
		this.head.rotateAngleX = headPitch * 0.017453292F;
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        //System.out.println(AnimationJSONTool.DEER_TEST);
        if(state == null) {
        	state = AnimationJSONTool.loadAnimationFile("deer", new ResourceLocation(Reference.MOD_ID + ":animations/deer/deeranimations.json"));
        	state.addBoneMapping("bone5", bone5);
        	state.addBoneMapping("frontrightleg", frontrightleg);
        	state.addBoneMapping("frontleftleg", frontleftleg);
        	state.addBoneMapping("backrightleg", backrightleg);
        	state.addBoneMapping("backleftleg", backleftleg);
        	state.addBoneMapping("head", head);
        	state.addBoneMapping("neck", neck);
        	state.addBoneMapping("ears", ears);
        	state.addBoneMapping("tail", tail_r1);
        	state.addBoneMapping("body", backbody_r1);
        	//state.addBoneMapping("frontleftleg", frontleftleg);
        	
        }
        //player = null;
        if(this.player == null) {
        	this.player = new AnimationPlayer(state, "walk");
        }


        boolean check = entityIn.motionX > 0.0001 || entityIn.motionX < -0.0001;
        if(check) {
        	this.player.play(entityIn, 0.15F, false, true, true);
        }
        
        
	}
}