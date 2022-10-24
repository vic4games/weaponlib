package com.vicmatskiv.weaponlib.animation;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.compatibility.Interceptors;
import com.vicmatskiv.weaponlib.compatibility.RecoilParam;
import com.vicmatskiv.weaponlib.numerical.LerpedValue;
import com.vicmatskiv.weaponlib.numerical.RandomVector;
import com.vicmatskiv.weaponlib.numerical.SpringValue;

import akka.japi.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Stores a bunch of values that need to update on an interval, and typically
 * have to be smooth. This class was such a tremendous mess that I would say it
 * actually in fact used negative software engineering principles.
 * 
 * 
 * @author Homer Riva-Cambrin
 * @since October 23rd, 2022
 */
public class ClientValueRepo {

	// 20 ticks/s -> 1/20 = 0.05
	private static final double DELTA_T = 0.05;

	// This value is added to the "TICKER" every tick.
	// Don't touch this as it will mess with how things
	// look.
	private static final double TICKER_INCREMENT = 0.01;
	
	
	private static final Minecraft MC = Minecraft.getMinecraft();
	
	
	private static final double RUNNING_SPEED_VALUE = 0.5;
	private static final double RUNNING_DAMPEN_VALUE = 0.6;
	private static final double FORWARD_MOVEMENT_DAMPEN_VALUE = 0.7;
	private static final double STRAFE_MOVEMENT_DAMPEN_VALUE = 0.7;

	/*
	 * LERPED VALUES Values that are only updated every tick. Do not require high
	 * precision. Typically for slower movements.
	 */

	public static final LerpedValue TICKER = new LerpedValue();

	public static LerpedValue strafe = new LerpedValue();
	public static LerpedValue forward = new LerpedValue();
	public static LerpedValue running = new LerpedValue();

	// Recoil
	public static LerpedValue gunPow = new LerpedValue();

	// scope
	public static LerpedValue scopeX = new LerpedValue();
	public static LerpedValue scopeY = new LerpedValue();
	
	public static LerpedValue slidePumpValue = new LerpedValue();

	

	/**
	 * SPRING VALUES Simulated spring movements. Again, these are updated on tick so
	 * actually lack in high-precision movements.
	 */
	public static SpringValue xInertia = new SpringValue(4000, 20, 350);
	public static SpringValue yInertia = new SpringValue(4000, 20, 350);
	public static SpringValue jumpingSpring = new SpringValue(2000, 40, 400);
	public static SpringValue weaponRecovery = new SpringValue(1, 1, 1);

	
	
	/**
	 * RANDOM VECTORS ?? tf are these
	 */
	public static RandomVector stressVec = new RandomVector();
	public static RandomVector recoilRotationVector = new RandomVector();

	// public static SpringValue walkingGun = new SpringValue(400, 60, 50);

	public static boolean shouldContinueRunning = false;

	// Slide pumping Wtf is a frame value
	
	public static int flash = 0;

	public static double recoilStop = 35f;

	public static double recoilWoundY;

	public static void fireWeapon(PlayerWeaponInstance pwi) {

		RecoilParam params = pwi.getRecoilParameters();

		Pair<Double, Double> screenShakeParam = pwi.getScreenShakeParameters();

		// System.out.println(pwi.getWeapon().getScreenShakeAnimationBuilder(RenderableState.SHOOTING).build());

		double power = params.getWeaponPower();

		if (gunPow.currentValue < 10) {

			Interceptors.nsm.impulse(screenShakeParam.first());
			power *= 2;
		} else if (gunPow.currentValue > params.getStockLength()) {
			power /= 2;
			Interceptors.nsm.impulse(screenShakeParam.first() / 3);
		} else {
			Interceptors.nsm.impulse(screenShakeParam.first());

		}

		weaponRecovery.velocity += power / 4;

		gunPow.currentValue += power;

		stressVec.callRandom(pwi.isAimed() ? 0.05 : 0.2);
		recoilRotationVector.callRandom(15);

		slidePumpValue.currentValue += 1.0;

	}



	public static void update(ModContext context) {
		// Update all of our lerped values' previous
		// values before we assign new values.
		running.updatePrevious();
		strafe.updatePrevious();
		scopeX.updatePrevious();
		scopeY.updatePrevious();
		gunPow.updatePrevious();
		forward.updatePrevious();
		TICKER.updatePrevious();
		slidePumpValue.updatePrevious();
		
		EntityPlayer player = MC.player;

		PlayerWeaponInstance pwi = context.getMainHeldWeapon();

		

		// Add the ticker increment to the ticker every tick (say that
		// ten times fast!)
		TICKER.currentValue += TICKER_INCREMENT;

		// Recoil constants
		RecoilParam recoilParameters = pwi.getRecoilParameters();

		
		
		// Update movement values
		if (!MC.player.onGround) jumpingSpring.velocity += MC.player.motionY * 10;		
		if (player.moveForward < 0) {
			strafe.add(player.moveForward / 3);
		} else {
			forward.add(player.moveForward / 4);
		}
		strafe.add(player.moveStrafing / 2);
		
		
		xInertia.velocity += strafe.currentValue;
		
		
		

		
		// Update running value. Adds the running speed to
		// it if we are sprinting.
		if (player.isSprinting()) running.add(RUNNING_SPEED_VALUE);
		
		
		strafe.dampen(STRAFE_MOVEMENT_DAMPEN_VALUE);
		forward.dampen(FORWARD_MOVEMENT_DAMPEN_VALUE);
		running.dampen(RUNNING_DAMPEN_VALUE);
		
		if (gunPow.currentValue > recoilParameters.getStockLength()) {
			gunPow.dampen(recoilParameters.getPowerRecoveryStockRate());
		} else {
			gunPow.dampen(recoilParameters.getPowerRecoveryNormalRate());
		}

		if (flash > 4)
			flash = 0;
		if (flash > 0)
			flash++;

		if (recoilStop > 35f) {
			recoilStop -= 2f;
		}

	
	
		recoilRotationVector.update(0.2, 0.6);
		stressVec.update(0.2, 0.6);

		if (pwi != null) {
			if (pwi.isAimed()) {
				// Handle scope values
				scopeX.currentValue *= 0.2;
				scopeY.currentValue *= 0.2;
			} else {
				scopeX.add(-0.5);
				scopeY.add(0.5);
			}
		}

		// Finally, we update our springs.
		weaponRecovery.update(DELTA_T);
		jumpingSpring.update(DELTA_T);
		xInertia.update(DELTA_T);
		yInertia.update(DELTA_T);

	}

}
