package com.vicmatskiv.weaponlib.animation.movement;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.animation.Interpolation;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.jim.BBLoader;
import com.vicmatskiv.weaponlib.animation.jim.KeyedAnimation;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer.StateDescriptor;
import com.vicmatskiv.weaponlib.numerical.LerpedValue;
import com.vicmatskiv.weaponlib.numerical.LissajousCurve;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;
import com.vicmatskiv.weaponlib.compatibility.RecoilParam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class WeaponRotationHandler {
	
	public double currentInVal;
	public double previousInVal;
	
	public LerpedValue runningDepth = new LerpedValue();
	
	public KeyedAnimation walkingAnimation = new KeyedAnimation(BBLoader.getAnimation("universal", "walkforward", "main"));
	public KeyedAnimation runningAnimation = new KeyedAnimation(BBLoader.getAnimation("universal", "running", "main"));
	public KeyedAnimation strafingAnimation = new KeyedAnimation(BBLoader.getAnimation("universal", "walk", "main"));
	public double springTransition;
	
	
	public static void applyRotationAtPoint(float xOffset, float yOffset, float zOffset, float xRotation,
			float yRotation, float zRotation) {
		GL11.glTranslatef(-xOffset, -yOffset, -zOffset);

		GL11.glRotatef(xRotation, 1f, 0f, 0f);
		GL11.glRotatef(yRotation, 0f, 1f, 0f);
		GL11.glRotatef(zRotation, 0f, 0f, 1f);

		GL11.glTranslatef(xOffset, yOffset, zOffset);
	}
	
	

	
	public void runze(RenderContext<RenderableState> renderContext, StateDescriptor stateDescriptor) {

		
	
		
		//System.out.println(keyAnim.max);
		
		float mag = ClientValueRepo.forward.getLerpedFloat();
		if(renderContext.getWeaponInstance().isAimed()) {
			mag /= 100;
		}
		
		
		if(Minecraft.getMinecraft().player.isSprinting()) {
			springTransition += 0.01;
		} else {
			springTransition *= 0.8;
		}
		
		springTransition = Math.max(Math.min(springTransition, 1), 0);
		
	
	//	System.out.println(runningAnimation.max);
		
		
		
	
		//GlStateManager.pushMatrix();
		//GlStateManager.translate(0, -1, 0);
		Vec3d rotationPoint = new Vec3d(-0.10, -1, 0);
		walkingAnimation.doPositioning((float) Math.max(mag-(springTransition), 0), rotationPoint);
		runningAnimation.doPositioning((float) Math.max(mag-(1.0-springTransition), 0), rotationPoint);
		
		//System.out.println(mag);
		//runningAnimation.doPositioning((float) mag, rotationPoint);
		//GlStateManager.popMatrix();
		if(true) return;
		
		// update previous
		runningDepth.updatePrevious();
		
		// Calculate current value
		currentInVal += 1;
		double ticks = previousInVal + (currentInVal - previousInVal)*Minecraft.getMinecraft().getRenderPartialTicks();
		previousInVal = currentInVal;
		
		
		runningDepth.currentValue *= 0.95;
		
		if(Math.round(currentInVal)%100 < 10) { 
			runningDepth.currentValue += 0.1;
			
		}
		
		double n = Math.sin(ticks*0.05)*0.5+0.5;
		
		double scale = 0.5;
		//Vec3d i = MatrixHelper.solveBeizer(Vec3d.ZERO, new Vec3d(1.0, -1, 0.0), new Vec3d(1.2, 0.2, 0), 1-runningDepth.getLerpedFloat());
		//GlStateManager.translate(i.x*scale, i.y*scale, i.z*scale);
		
		//double downRotation = LissajousCurve.getXOffsetOnCurve(-5*runningDepth.getLerpedFloat(), 0.2, Math.PI, 0, ticks);
		
		
		applyRotationAtPoint(0.0f, 0.8f, 0.1f, 0f, (float) Math.sin(ticks*0.5)*5, 0);
		GlStateManager.translate(0, runningDepth.getLerpedFloat(), 0);
	}
	
	public void run(RenderContext<RenderableState> renderContext, StateDescriptor stateDescriptor) {
		runze(renderContext, stateDescriptor);
		if(true) return;
		
		// Get parameters
		RecoilParam parameters = renderContext.getWeaponInstance().getWeapon().getRecoilParameters();

		boolean scopeFlag = true;
		
		
		boolean isPistol = parameters.getRecoilGroup() == 1;
		boolean isShotgun = parameters.getRecoilGroup() == 2;
		boolean isAssault = parameters.getRecoilGroup() == 0;
		
		
		double gunPower = ClientValueRepo.gunPow.getLerpedFloat();
		double recovery = ClientValueRepo.recovery.getLerpedFloat();
		
		float min = (isAssault && renderContext.getWeaponInstance().isAimed()) ? 0.2f : 1f;
		if (renderContext.getWeaponInstance().getScope() != null
				&& renderContext.getWeaponInstance().getScope().isOptical()
				&& renderContext.getWeaponInstance().isAimed()) {
			min *= 0.5;
			scopeFlag = true;
			// System.out.println("yo");
		}
		float maxAngle = (float) (2 * Math.PI);
		float time = (float) (35f - (gunPower / 400));
		if (min != 1.0)
			time = 35f;
		float tick = (float) ((float) maxAngle * ((Minecraft.getMinecraft().player.ticksExisted % time) / time))
				- (maxAngle / 2);

		double amp = 0.07 + (gunPower / 700);
		double a = 1;
		double b = 2;
		double c = Math.PI;

		EntityPlayer p = Minecraft.getMinecraft().player;

		float xRotation = (float) ((float) amp * Math.sin(a * tick + c));
		float yRotation = (float) ((float) amp * Math.sin(b * tick));
		float zRotation = (float) 0;

		RenderableState sus = stateDescriptor.getStateManager().getLastState();

		float shoting = (float) gunPower;
		if (scopeFlag)
			shoting *= 0.2f;

		float recoilStop = (float) ClientValueRepo.recoilStop / 1.5f;

		float zRot = (float) ((float) -gunPower / 25f + ((float) 0)) * min;

		float pistol = 25;
		float pR = isPistol ? (float) ClientValueRepo.randomRot.y : 0f;

		float muzzleRiser = (float) shoting / 60f;
		if (shoting > recoilStop) {
			muzzleRiser = recoilStop / 60f;
		}

		if (isPistol || isShotgun)
			muzzleRiser *= pistol;
		muzzleRiser *= (min);
		muzzleRiser *= parameters.getMuzzleClimbMultiplier();

		float wavyBoi = 0f;
		if (!isPistol) {
			wavyBoi = (float) Math.pow(Math.sin(recovery * 0.048 + shoting * 0.015), 3) * 2;
		} else {
			wavyBoi = (float) Math.pow(-Math.sin((recovery - gunPower) * 0.2), 1) * 2;

		}
		wavyBoi *= min;

		// System.out.println(wavyBoi);
		// System.out.println(System.currentTimeMillis());

		// float muzzleDown = gunPower > 30 ? (float)
		// (gunPower-30f)/5f : 0f;
		// System.out.println(shoting);

		float aimMultiplier = renderContext.getWeaponInstance().isAimed() ? 0.1f : 1.0f;

		float strafe = (float) ClientValueRepo.strafe.getLerpedPosition() * aimMultiplier * 0.7f;

		float forwardMov = (float) ClientValueRepo.forward.getLerpedFloat() * aimMultiplier * 0.7f;
		float rise = (float) (ClientValueRepo.jumpingSpring.getLerpedPosition());
		
		
		

		forwardMov = Math.max(0, forwardMov);

		if (!AnimationModeProcessor.getInstance().getFPSMode()) {

			// gun sway
			applyRotationAtPoint(0f, 0f, 3f, (float) (xRotation) - (wavyBoi) + forwardMov + (rise / 1f),
					yRotation + strafe, zRotation + zRot);

			// Gun inertia
			// applyRotationAtPoint(0.0f, 0.0f, 0.0f, wavyBoi, 0, 0);

			float fight = (float) Math.pow(Math.sin(shoting * 0.015), 3);
			fight *= min;
			// +-+

			// System.out.println(Minecraft.getMinecraft().player.motionY);
			// float prevWiggle = (float)
			// (2*Math.PI*((Minecraft.getMinecraft().player.ticksExisted%20)/20.0))*Minecraft.getMinecraft().getRenderPartialTicks();
			float prevTickWiggle = (float) (2 * Math.PI
					* (((Minecraft.getMinecraft().player.ticksExisted - 1) % 20) / 20.0));

			// System.out.println(Minecraft.getMinecraft().player.ticksExisted);
			float tickWiggle = (float) (2 * Math.PI * (((ClientValueRepo.ticker.getLerpedFloat()) % 36) / 36.0));

	
			// tickWiggle = MatrixHelper.solveLerp((float) ClientValueRepo.walkYWiggle,
			// tickWiggle, Minecraft.getMinecraft().getRenderPartialTicks());

			
			
			float xWiggle = (float) ((float) Math.sin(tickWiggle) * ClientValueRepo.walkingGun.getLerpedPosition());
			
			// xWiggle = MatrixHelper.solveLerp((float) ClientValueRepo.walkXWiggle,
			// xWiggle, Minecraft.getMinecraft().getRenderPartialTicks());

			// ClientValueRepo.walkXWiggle = xWiggle;

			float yWiggle = (float) ((float) Math.cos(tickWiggle) * ClientValueRepo.walkingGun.getLerpedPosition())
					* 0.02f;
			
			
		

			float sway = (float) ((float) ((float) Math.sin(tickWiggle * (2))) * ClientValueRepo.forward.getLerpedFloat()) * 0.2f;
			sway *= aimMultiplier;
			
			
			
			// xWiggle = (float) ClientValueRepo.walkingGun.getLerpedPosition();
			// xWiggle = 0f;
			// forwardMov = 0f;

			// Gun inertia

			applyRotationAtPoint(0.0f, 0.0f, 0.0f,
					(float) ClientValueRepo.yInertia.getLerpedPosition() + fight + (isPistol ? -muzzleRiser : 0f) + forwardMov
							+ (rise / 1f) + (yWiggle * 3),
					(float) -ClientValueRepo.xInertia.getLerpedPosition() - fight + pR + strafe - (forwardMov * 3) + (sway * 10),
					(float) ClientValueRepo.xInertia.getLerpedPosition() + fight + xWiggle + (forwardMov * 10));

			if (!isPistol)
				applyRotationAtPoint(0.0f, 0.0f, -1.0f, -muzzleRiser, 0.0f, 0.0f);

			float limitedShoting = Math.min(shoting, (float) ClientValueRepo.recoilStop / 1.5f);

			GlStateManager.translate(0.0 * parameters.getTranslationMultipliers().x + (-strafe / 10) + (sway / 3f),
					(isPistol ? -0.01 * limitedShoting : 0f) * parameters.getTranslationMultipliers().y
							+ (rise / 35f) + yWiggle + (forwardMov / 10f),
					0.01 * limitedShoting * min * parameters.getTranslationMultipliers().z);
			
		}
	}

}
