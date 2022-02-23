package com.vicmatskiv.weaponlib.animation.movement;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer.StateDescriptor;
import com.vicmatskiv.weaponlib.compatibility.RecoilParam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class WeaponRotationHandler {
	
	
	public static void applyRotationAtPoint(float xOffset, float yOffset, float zOffset, float xRotation,
			float yRotation, float zRotation) {
		GL11.glTranslatef(-xOffset, -yOffset, -zOffset);

		GL11.glRotatef(xRotation, 1f, 0f, 0f);
		GL11.glRotatef(yRotation, 0f, 1f, 0f);
		GL11.glRotatef(zRotation, 0f, 0f, 1f);

		GL11.glTranslatef(xOffset, yOffset, zOffset);
	}
	
	public void run(RenderContext<RenderableState> renderContext, StateDescriptor stateDescriptor) {
		
		// Get parameters
		RecoilParam parameters = renderContext.getWeaponInstance().getWeapon().getRecoilParameters();

		boolean scopeFlag = true;
		
		
		boolean isPistol = parameters.getRecoilGroup() == 1;
		boolean isShotgun = parameters.getRecoilGroup() == 2;
		boolean isAssault = parameters.getRecoilGroup() == 0;
		
		
		
		float min = (isAssault && renderContext.getWeaponInstance().isAimed()) ? 0.2f : 1f;
		if (renderContext.getWeaponInstance().getScope() != null
				&& renderContext.getWeaponInstance().getScope().isOptical()
				&& renderContext.getWeaponInstance().isAimed()) {
			min *= 0.5;
			scopeFlag = true;
			// System.out.println("yo");
		}
		float maxAngle = (float) (2 * Math.PI);
		float time = (float) (35f - (ClientValueRepo.gunPow / 400));
		if (min != 1.0)
			time = 35f;
		float tick = (float) ((float) maxAngle * ((Minecraft.getMinecraft().player.ticksExisted % time) / time))
				- (maxAngle / 2);

		double amp = 0.07 + (ClientValueRepo.gunPow / 700);
		double a = 1;
		double b = 2;
		double c = Math.PI;

		EntityPlayer p = Minecraft.getMinecraft().player;

		float xRotation = (float) ((float) amp * Math.sin(a * tick + c));
		float yRotation = (float) ((float) amp * Math.sin(b * tick));
		float zRotation = (float) 0;

		RenderableState sus = stateDescriptor.getStateManager().getLastState();

		float shoting = (float) ClientValueRepo.gunPow;
		if (scopeFlag)
			shoting *= 0.2f;

		float recoilStop = (float) ClientValueRepo.recoilStop / 1.5f;

		float zRot = (float) ((float) -ClientValueRepo.gunPow / 25f + ((float) 0)) * min;

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
			wavyBoi = (float) Math.pow(Math.sin(ClientValueRepo.recovery * 0.048 + shoting * 0.015), 3) * 2;
		} else {
			wavyBoi = (float) Math.pow(-Math.sin((ClientValueRepo.recovery - ClientValueRepo.gunPow) * 0.2), 1) * 2;

		}
		wavyBoi *= min;

		// System.out.println(wavyBoi);
		// System.out.println(System.currentTimeMillis());

		// float muzzleDown = ClientValueRepo.gunPow > 30 ? (float)
		// (ClientValueRepo.gunPow-30f)/5f : 0f;
		// System.out.println(shoting);

		float aimMultiplier = renderContext.getWeaponInstance().isAimed() ? 0.1f : 1.0f;

		float strafe = (float) ClientValueRepo.strafe * aimMultiplier * 0.7f;

		float forwardMov = (float) ClientValueRepo.forward * aimMultiplier * 0.7f;
		float rise = (float) (ClientValueRepo.rise / 1f);
		
		
		

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
			
			
		

			float sway = (float) ((float) ((float) Math.sin(tickWiggle * (2))) * ClientValueRepo.forward) * 0.2f;
			sway *= aimMultiplier;
			
			
			
			// xWiggle = (float) ClientValueRepo.walkingGun.getLerpedPosition();
			// xWiggle = 0f;
			// forwardMov = 0f;

			// Gun inertia

			applyRotationAtPoint(0.0f, 0.0f, 0.0f,
					(float) ClientValueRepo.yInertia + fight + (isPistol ? -muzzleRiser : 0f) + forwardMov
							+ (rise / 1f) + (yWiggle * 3),
					(float) -ClientValueRepo.xInertia - fight + pR + strafe - (forwardMov * 3) + (sway * 10),
					(float) ClientValueRepo.xInertia + fight + xWiggle + (forwardMov * 10));

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
