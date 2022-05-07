package com.vicmatskiv.weaponlib.animation.movement;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.animation.Interpolation;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.gui.AnimationGUI;
import com.vicmatskiv.weaponlib.animation.jim.BBLoader;
import com.vicmatskiv.weaponlib.animation.jim.KeyedAnimation;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer.StateDescriptor;
import com.vicmatskiv.weaponlib.numerical.LerpedValue;
import com.vicmatskiv.weaponlib.numerical.LissajousCurve;
import com.vicmatskiv.weaponlib.numerical.RandomVector;
import com.vicmatskiv.weaponlib.numerical.SpringValue;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;
import com.vicmatskiv.weaponlib.compatibility.RecoilParam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import scala.actors.threadpool.helpers.FIFOWaitQueue;

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
	
	

	
	public void run(RenderContext<RenderableState> renderContext, StateDescriptor stateDescriptor) {
		
		
		
		Vec3d rotationPoint = new Vec3d(-0.10, -1, 0);
		
		
		PlayerWeaponInstance pwi = renderContext.getWeaponInstance();
		RecoilParam params = pwi.getRecoilParameters();
		
		// Handle the basic animations
		float forwardMagnitude = ClientValueRepo.forward.getLerpedFloat();
		float strafeMagnitude = ClientValueRepo.strafe.getLerpedFloat();
		float runningMagnitude = ClientValueRepo.running.getLerpedFloat();
		
	
		
		
		// Sway & walk
		float swayAmplitude = 0.1f;
		float walkingSwayAmplitude = strafeMagnitude/30f + forwardMagnitude/30f;
		
		
		
		
		// System.out.println(BBLoader.getTotalAnimations());
		
		/*
		if(gunPow.currentValue > recoilParameters.getStockLength()) {
			gunPow.currentValue *= recoilParameters.getPowerRecoveryStockRate();
		} else {
			gunPow.currentValue *= recoilParameters.getPowerRecoveryNormalRate();
		}
		*/
		
		// Recoil
		float recoilAmplitude = ClientValueRepo.gunPow.getLerpedFloat();
		
		
		if(ClientModContext.getContext() != null && ClientModContext.getContext().getMainHeldWeapon() != null) {
			LerpedValue gunPow = ClientValueRepo.gunPow;
			RecoilParam recoilParameters = new RecoilParam();
			if(ClientModContext.getContext() != null) {
				recoilParameters = ClientModContext.getContext().getMainHeldWeapon().getRecoilParameters();
			}
			/*
			if(gunPow.currentValue > recoilParameters.getStockLength()) {
				gunPow.currentValue *= recoilParameters.getPowerRecoveryStockRate();
			} else {
				gunPow.currentValue *= recoilParameters.getPowerRecoveryNormalRate();
			}
			*/
		}
		/*
		float delta = (System.currentTimeMillis()-ClientValueRepo.lastShotStamp)/50f;
		delta = Math.min(delta, 1);
		//System.out.println(delta);
		recoilAmplitude = 50*(1-delta);
		*/
		
		
		float weaponRecoveryAmplitude = ClientValueRepo.weaponRecovery.getLerpedFloat()/15f;
		float muzzleClimbDivisor = (float) params.getMuzzleClimbDivisor();
		float rotationYDivisor = (float) params.getWeaponRotationX();
		float rotationZDivisor = (float) params.getWeaponRotationY();
		
		SpringValue recoverySpring = ClientValueRepo.weaponRecovery;
		recoverySpring.setDamping(3);
		recoverySpring.setSpringConstant(50);
		recoverySpring.setMass(1);
		
		
		if(renderContext.getWeaponInstance().isAimed()) {
			float divisorMultiplier = 1f;
			swayAmplitude /= 3;
			if(renderContext.getWeaponInstance().getScope() != null && renderContext.getWeaponInstance().getScope().isOptical()) {
				divisorMultiplier = 3f;
				swayAmplitude /= 3;
				walkingSwayAmplitude /= 3f;
			}
			
			divisorMultiplier /= params.getADSSimilarity();
			
			// The spring is more obvious in first person
			// so, by increasing the damping we can make
			// it look better.
			recoverySpring.setDamping(5);
			
			forwardMagnitude /= 5*divisorMultiplier;
			strafeMagnitude /= 3*divisorMultiplier;
			recoilAmplitude /= 3*divisorMultiplier;
			weaponRecoveryAmplitude /= 2*divisorMultiplier;
			walkingSwayAmplitude /= 4*divisorMultiplier;
			
		}
		

		
		
		strafingAnimation.doPositioning((float) strafeMagnitude, rotationPoint);	
		walkingAnimation.doPositioning((float) Math.max(forwardMagnitude-(springTransition+runningMagnitude), 0), rotationPoint);
		runningAnimation.doPositioning((float) Math.max(runningMagnitude, 0), rotationPoint);
		
		
		// Add sway
		
		
		
		double gunSwayX = LissajousCurve.getXOffsetOnCurve(swayAmplitude, 0.25, Math.PI, 0, ClientValueRepo.ticker.getLerpedFloat());
		double gunSwayY = LissajousCurve.getXOffsetOnCurve(swayAmplitude, 0.5,Math.PI, Math.PI/2, ClientValueRepo.ticker.getLerpedFloat());
		
		
		double walkSwayX = LissajousCurve.getXOffsetOnCurve(walkingSwayAmplitude, 0.5, Math.PI, 0, ClientValueRepo.ticker.getLerpedFloat());
		double walkSwayY = LissajousCurve.getXOffsetOnCurve(walkingSwayAmplitude, 0.25,Math.PI, Math.PI/2, ClientValueRepo.ticker.getLerpedFloat());
		
		
		GlStateManager.translate(walkSwayY, walkSwayX, 0);
		applyRotationAtPoint(0f, 0f, 1.5f, (float)gunSwayX, (float) gunSwayY, 0f);
		
				GlStateManager.rotate((float) ClientValueRepo.xInertia.getLerpedPosition(), 0, 1, 0);
				GlStateManager.rotate((float) ClientValueRepo.yInertia.getLerpedPosition(), 1, 0, 0);
				

		
		
		
		//weaponRecoveryAmplitude = 1;
		double recoveryX = LissajousCurve.getXOffsetOnCurve(weaponRecoveryAmplitude, 1, Math.PI, 0, ClientValueRepo.ticker.getLerpedFloat());
		double recoveryY = LissajousCurve.getXOffsetOnCurve(weaponRecoveryAmplitude, 0.5, Math.PI, Math.PI/2, ClientValueRepo.ticker.getLerpedFloat());
		double recoveryZ = LissajousCurve.getXOffsetOnCurve(weaponRecoveryAmplitude, 2, Math.PI, Math.PI/2, ClientValueRepo.ticker.getLerpedFloat());
		
		//System.out.println(weaponRecoveryAmplitude);
	
		GlStateManager.translate(0, weaponRecoveryAmplitude/5f, weaponRecoveryAmplitude);
		GlStateManager.rotate(weaponRecoveryAmplitude*5f, 1, 0, 0);
		/*
		GlStateManager.rotate((float) recoil/15f, 0, 0, 1);
		GlStateManager.rotate((float)-recoil/25, 1, 0, 0);
		*/
		Vec3d strezz = ClientValueRepo.stressVec.getInterpolatedVector(2.0);
		Vec3d recoilRotation = ClientValueRepo.recoilRotationVector.getInterpolatedVector(1.0);
		
		//System.out.println(recoilRotation);
		
		GlStateManager.translate(strezz.x, strezz.y, strezz.z);
		GlStateManager.translate(0, 0, recoilAmplitude*0.008);
		
		applyRotationAtPoint(0.0f, 1.0f, 0, (float) recoilRotation.x,  (float) recoilRotation.y,  (float) recoilRotation.z);
		
		applyRotationAtPoint(0.0f, 1.0f, 0, -recoilAmplitude/muzzleClimbDivisor, recoilAmplitude*rotationYDivisor, recoilAmplitude*rotationZDivisor);
		//applyRotationAtPoint(0.0f, 1.0f, 0, -recoilAmplitude*1.5f, 0, recoilAmplitude/25);
		
		
	}
	/*
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

		float strafe = (float) ClientValueRepo.strafe.getLerpedFloat() * aimMultiplier * 0.7f;

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
	}*/

}
