package com.vicmatskiv.weaponlib.animation;

import javax.print.StreamPrintService;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.animation.gui.AnimationGUI;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.RecoilParam;
import com.vicmatskiv.weaponlib.numerical.LerpedValue;
import com.vicmatskiv.weaponlib.numerical.LissajousCurve;
import com.vicmatskiv.weaponlib.numerical.RandomVector;
import com.vicmatskiv.weaponlib.numerical.SpringValue;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class ClientValueRepo {

	
	
	public static double oldGunPow = 0;
	
	public static LerpedValue ticker = new LerpedValue();
	
	public static double walkXWiggle = 0;
	public static double walkYWiggle = 0;

	public static LerpedValue strafe = new LerpedValue();
	public static LerpedValue forward = new LerpedValue();
	public static LerpedValue running = new LerpedValue();
	
	public static int prevTickTick = 0;


	public static SpringValue jumpingSpring = new SpringValue(400, 40, 90);
	public static RandomVector stressVec = new RandomVector();

	
	public static SpringValue walkingGun = new SpringValue(50, 10, 80);
	
	
	// Recoil
	public static LerpedValue gunPow = new LerpedValue();
	public static SpringValue weaponRecovery = new SpringValue(1, 1, 1);
	
	//public static SpringValue walkingGun = new SpringValue(400, 60, 50);

	public static boolean shouldContinueRunning = false;

	// scope
	public static LerpedValue scopeX = new LerpedValue();
	public static LerpedValue scopeY = new LerpedValue();


	
	
	
	public static LerpedValue recovery = new LerpedValue();
	
	
	
	//public static double recovery = 0.0;
	public static Vec3d randomRot = Vec3d.ZERO;

	public static SpringValue xInertia = new SpringValue(0, 0 ,0);
	public static SpringValue yInertia = new SpringValue(0, 0 ,0);
	/*
	public static double xInertia = 0.0;
	public static double yInertia = 0.0;
	*/
	public static int flash = 0;

	public static double recoilStop = 35f;

	public static int gunTick = 0;
	
	
	public static void fireWeapon(PlayerWeaponInstance pwi) {
		
		RecoilParam params = pwi.getRecoilParameters();
		
		double power = params.getWeaponPower();
		
		if(gunPow.currentValue < 10) {
		
			power *= 2;
		} else if(gunPow.currentValue > params.getStockLength()	) {
			power /= 2;
			
		} else {
			
			
		}
		
		weaponRecovery.velocity += power/4;
		
		gunPow.currentValue += power;

		stressVec.getVector();
		
		
	}

	public static void update(ModContext context) {
		PlayerWeaponInstance pwi = context.getMainHeldWeapon();
		
		
		// Recoil constants
		RecoilParam recoilParameters = pwi.getRecoilParameters();
		
		
		//if(Math.abs(gunPow.position) > 1000) {
			//gunPow = new SpringValue(500, 50, 100);
		//}
		
		//double stockLength = AnimationGUI.getInstance().stockLength.getValue();
		
		//System.out.println(gunPow.getPosition());
		//gunPow.setPosition(1);
		/*
		gunPow.setDamping(2000);
		gunPow.setMass(50);
		gunPow.setSpringConstant(10000);
		*/
		running.updatePrevious();
		strafe.updatePrevious();
		scopeX.updatePrevious();
		scopeY.updatePrevious();
		gunPow.updatePrevious();
		forward.updatePrevious();
		recovery.updatePrevious();
		ticker.updatePrevious();
		//weaponRecovery.updatePrevious();
		
		
		ticker.currentValue += 0.01;
		
		boolean reload = false;
		if(reload) {
			xInertia = null;
			yInertia = null;
		}
		
		
		
		if(xInertia == null) {
			xInertia = new SpringValue(500, 50, 100);
		}
		if(yInertia == null) {
			yInertia = new SpringValue(500, 50, 100);
		}
		xInertia.setSpringConstant(4000);
		yInertia.setSpringConstant(4000);
		xInertia.setMass(15);
		yInertia.setMass(15);
	xInertia.setDamping(400);
	yInertia.setDamping(400);
	
	
	
	
	
	
		// System.out.println("Called: " + System.currentTimeMillis());
		

		
		
		if(Minecraft.getMinecraft().player.moveForward < 0) {
			strafe.currentValue += Minecraft.getMinecraft().player.moveForward/3f;
			
		}
		strafe.currentValue += Minecraft.getMinecraft().player.moveStrafing/2;
		
		xInertia.velocity += strafe.currentValue;
		
		strafe.currentValue *= 0.7;
	
		
		if(Minecraft.getMinecraft().player.isSprinting()) {
			running.currentValue += 0.5;
		}
		running.currentValue *= 0.6;
		//System.out.println(running.currentValue);
		

		/*
		if (gunTick == 1) {
			gunTick++;

		} else if (gunTick >= 1) {
		*/
		if(gunTick == 1) {
			/*
			float power = 50;
			if (param != null)
				power = (float) param.getGunPower();

			gunPow.velocity += power;
			//ClientValueRepo.gunPow += power;

			if (recovery.getValue() > 1) {
				recovery.add(power/(recovery.getValue()/2));
				//ClientValueRepo.recovery += power / (ClientValueRepo.recovery / 2);
			} else {
				recovery.add(power);
				//ClientValueRepo.recovery += power;
			}

			randomRot = randomRot.addVector(30 * Math.random(), 5 * Math.random(), 30 * Math.random());

			gunTick = 0;
			*/
		}
		
		
		
		if(gunPow.currentValue > recoilParameters.getStockLength()) {
			gunPow.currentValue *= recoilParameters.getPowerRecoveryStockRate();
		} else {
			gunPow.currentValue *= recoilParameters.getPowerRecoveryNormalRate();
		}


		weaponRecovery.update(0.05);
		
		//gunPow.update(0.05);

		// xInertia = 0;
		Minecraft mc = Minecraft.getMinecraft();
		int ticks = mc.player.ticksExisted;

		float i = (float) ((float) 2 * Math.PI * ((ticks % 45) / 45.0));

		if (flash > 4)
			flash = 0;
		if (flash > 0)
			flash++;

		if (recoilStop > 35f) {
			recoilStop -= 2f;
		}
		
		
		if(Minecraft.getMinecraft().player.moveForward > 0) {
			forward.add(Minecraft.getMinecraft().player.moveForward/4);
		}
		forward.currentValue *= 0.7;
		
	      
	stressVec.update(1, 0.05);
		/*
		strafe *= 0.95;
		forward *= 0.93;
		*/
		
		randomRot = randomRot.scale(0.88);

		/*
		 * assault rifle gunPow *= 0.92; recovery *= 0.96;
		 */
		/*
		if (!isPistol) {
			
			
			gunPow.currentValue *= 0.5 - recoveryMod;
			recovery.currentValue *= 0.7 - (recoveryMod / 2f);
			
			//gunPow *= 0.90 - recoveryMod;
			//recovery *= 0.95 - (recoveryMod / 2);
		} else {
			
			gunPow.currentValue *= 0.80 - recoveryMod;
			recovery.currentValue *= 0.90 - (recoveryMod / 2f);
			
			//gunPow *= 0.80 - recoveryMod;
			//recovery *= 0.90 - (recoveryMod / 2);
		}
		*/
		
		

		//gunPow.update(0.05);
		
		//recovery.currentValue = 0;
		/* GOOD
		double add = Math.sin(xInertia * 0.02);

		xInertia += 2 * -Math.abs(add) * Math.signum(xInertia);
		if (pwi != null && pwi.isAimed()) {
			xInertia *= 0.90;
		} else {
			xInertia *= 0.95;
		}

		add = Math.sin(yInertia * 0.02);

		yInertia += 2 * -Math.abs(add) * Math.signum(yInertia);
		if (pwi != null && pwi.isAimed()) {
			yInertia *= 0.90;
		} else {
			yInertia *= 0.95;
		}
		*/
		xInertia.update(0.05);
		yInertia.update(0.05);
		//gunPow.update(0.05);
		
		if (pwi != null) {
			if (pwi.isAimed()) {
				// Handle scope values
				scopeX.currentValue *= 0.2;
				scopeY.currentValue *= 0.2;
				/*
				scopeX *= 0.7;
				scopeY *= 0.7;
				*/
			} else {
				scopeX.add(-0.5);
				scopeY.add(0.5);
				
				/*
				scopeX -= 0.5;
				scopeY += 0.5;
				*/
			}
		}
		// rise = Minecraft.getMinecraft().player.moveVertical*10;
		// rise = 0;

		// System.out.println(rise);

		/*
		 * 
		 * float ticker = (float)
		 * (2*Math.PI*(Minecraft.getMinecraft().player.ticksExisted%10)/10f);
		 * if(!Minecraft.getMinecraft().player.onGround) { rise +=
		 * Minecraft.getMinecraft().player.motionY*3; coRise +=
		 * Minecraft.getMinecraft().player.motionY*3; } else {
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * rise *= 0.8;
		 */
		
		jumpingSpring.setSpringConstant(2000);
		jumpingSpring.setDamping(400);
		
		if (!Minecraft.getMinecraft().player.onGround) {
			jumpingSpring.velocity += Minecraft.getMinecraft().player.motionY*10;
		}

		

		EntityPlayer entityplayer = Minecraft.getMinecraft().player;

		float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
		float f1 = -(entityplayer.distanceWalkedModified + f * Minecraft.getMinecraft().getRenderPartialTicks());

		if (f != 0.0 && (pwi != null && !pwi.isAimed())) {

			walkingGun.position += f / 2;

			if(!entityplayer.isSprinting()) {
				walkingGun.setSpringConstant(50);
				if (Math.random() < 0.25) {
					double val = 17;
					walkingGun.velocity += (val * Math.random()) - (val/2);
				}
			} else {
				walkingGun.setSpringConstant(500);
				if (Math.random() < 0.25) {
					double val = 200;
					walkingGun.velocity += (val * Math.random()) - (val/2);
				}
			}
			
		}
		
		//strafe = new SpringValue(0, 0, 0);
		//forward = new SpringValue(0, 0, 0);
	
		



		//forward.update(0.05);
		walkingGun.update(0.05);
		jumpingSpring.update(0.05);
		
		 
		//walkYWiggle = (2*Math.PI*((Minecraft.getMinecraft().player.ticksExisted%36)/36.0));
      
		//walkXWiggle *= 0.9;
		//walkYWiggle *= 0.9;

		/*
		 * walkXWiggle *= 0.9; walkYWiggle *= 0.9; //rise = 0;
		 * 
		 * 
		 * //System.out.println(shock); /* if(shock > 0.1) { rise = 0; }
		 */

		// rise *= 0.9;
		// shock *= 0.9;

		// xInertia *= 0.92;
		// yInertia *= 0.92;

		

	}
	
	public static boolean isGunPowDecreasing() {
		return gunPow.previousValue-gunPow.currentValue > 0.1;
	}

}
