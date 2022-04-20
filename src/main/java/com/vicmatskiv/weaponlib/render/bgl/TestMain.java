package com.vicmatskiv.weaponlib.render.bgl;

import org.lwjgl.util.vector.Quaternion;

import com.vicmatskiv.weaponlib.render.bgl.math.AngleKit;
import com.vicmatskiv.weaponlib.render.bgl.math.AngleKit.AxisAngle;
import com.vicmatskiv.weaponlib.render.bgl.math.AngleKit.EulerAngle;
import com.vicmatskiv.weaponlib.render.bgl.math.AngleKit.Format;

public class TestMain {
	
	public static void main(String[] args) {
		
		
		EulerAngle initial = new EulerAngle(Format.DEGREES, 0, 90, 0);
		
		Quaternion q0 = initial.asQuaternion();
		
		EulerAngle angles = AngleKit.eulerFromQuat(q0, Format.DEGREES);
		
		System.out.println(angles);
		
		
		
		// Second Test
		EulerAngle a0 = new EulerAngle(Format.DEGREES, 0, 0, 0);
		EulerAngle a1 = new EulerAngle(Format.DEGREES, 0, 90, 0);
		System.out.println(a0.slerp(a1, 0.5));
		
	}

}
