package com.vicmatskiv.weaponlib.vehicle.jimphysics.stability.numerical.vehicle;

import com.vicmatskiv.weaponlib.vehicle.jimphysics.solver.WheelSolver;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.stability.numerical.SolutionVector;

public class RK4Wheel {
	
	public static void calculateDerivatives(WheelSolutionVector x0, WheelSolutionVector dxdt, double wheelRadius) {
		double B = 0.75;
		dxdt.slipRatio = ((x0.velocity * wheelRadius - x0.longSpeed)
		          - Math.abs(x0.longSpeed) * x0.slipRatio)/B;
		
	}
	
	public static void integrateRK4(WheelSolver solver, WheelSolutionVector state, float dt) {
		
		WheelSolutionVector k1 = new WheelSolutionVector(), k2 = new WheelSolutionVector(), k3 = new WheelSolutionVector(), k4 = new WheelSolutionVector();
		WheelSolutionVector x;
		
		calculateDerivatives(state, k1, solver.radius);
		x = state;
		x.add(0.5f*dt, k1);
		
		calculateDerivatives(state, k2, solver.radius);
		x = state;
		x.add(0.5f*dt, k2);
		
		calculateDerivatives(state, k3, solver.radius);
		x = state;
		x.add(dt, k3);
		
		calculateDerivatives(x, k4, solver.radius);
		
		state.add( dt/6.0f, k1 );     
		 state.add( dt/3.0f, k2 );     
		 state.add( dt/3.0f, k3 );     
		 state.add( dt/6.0f, k4 );    
	}

}
