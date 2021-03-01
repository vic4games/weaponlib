package com.vicmatskiv.weaponlib.vehicle.jimphysics.solverb;

import javax.vecmath.Vector2d;


import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.VehiclePhysUtil;

import net.minecraft.entity.MoverType;

public class MarcTest {
	
	private EntityVehicle car;
	private double sn, cs, yawspeed, rot_angle, sideslip, slipanglefront, slipanglerear, torque, weight;
	public int front_slip = 0;
	public int rear_slip = 0;
	
	static final double DRAG        = 5.0;     /* factor for air resistance (drag)         */
	 static final double RESISTANCE  = 30.0;    /* factor for rolling resistance */
	 static final double CA_R        = -5.20;   /* cornering stiffness */
	 static final double CA_F        = -5.0;    /* cornering stiffness */
	 static final double MAX_GRIP    = 2.0;     /* maximum (normalised) friction force, =diameter of friction circle */

	
	public MarcTest(EntityVehicle car) {
		this.car = car;
	}
	
	
	public void do_physics(double delta_t )
	 {
	double wheelbase = 3.0;
	double c = 1.5;
	double b = 1.5;
	double inertia = 750;
		
	  Vector2d velocity = new Vector2d();
	  sn = Math.sin(car.angle);
	  cs = Math.cos(car.angle);
	  
	  // SAE convention: x is to the front of the car, y is to the right, z is down
	  // transform velocity in world reference frame to velocity in car reference frame
	  velocity.x =  cs * car.velocity_wc.y + sn * car.velocity_wc.x;
	  velocity.y = -sn * car.velocity_wc.y + cs * car.velocity_wc.x;
	  
	   // Lateral force on wheels
	   // Resulting velocity of the wheels as result of the yaw rate of the car body
	   // v = yawrate * r where r is distance of wheel to CG (approx. half wheel base)
	   // yawrate (ang.velocity) must be in rad/s
	   yawspeed = wheelbase * b * car.angularvelocity;
	   if( velocity.x == 0 )
	        rot_angle = 0;
	   else
	    rot_angle = Math.atan( yawspeed / velocity.x);
	   // Calculate the side slip angle of the car (a.k.a. beta)
	   if( velocity.x == 0 )
	        sideslip = 0;
	   else
	    sideslip = Math.atan( velocity.y / velocity.x);

	   // Calculate slip angles for front and rear wheels (a.k.a. alpha)
	   slipanglefront = sideslip + rot_angle - car.steerangle;
	   slipanglerear  = sideslip - rot_angle;
	  

	   // weight per axle = half car mass times 1G (=9.8m/s^2)
	   weight = car.mass * 9.8 * 0.5;

	   // lateral force on front wheels = (Ca * slip angle) capped to friction circle * load
	   Vector2d flatf = new Vector2d();
	   Vector2d flatr = new Vector2d();
	   Vector2d ftraction = new Vector2d();
	   Vector2d resistance = new Vector2d();
	   Vector2d force = new Vector2d();
	   Vector2d acceleration = new Vector2d();
	   Vector2d acceleration_wc = new Vector2d();
	   
	   double m_GRIP = 5.0;
	   double CA_F = -4.6;
	   double CA_R = -5.0;
	   
	   flatf.x = 0;
	   flatf.y = CA_F * slipanglefront;
	   flatf.y = Math.min(m_GRIP, flatf.y);
	   flatf.y = Math.max(-m_GRIP, flatf.y);
	   flatf.y *= weight;
	   if(front_slip==1)
	       flatf.y *= 0.5;

	   // lateral force on rear wheels
	   flatr.x = 0;
	   flatr.y = CA_R * slipanglerear;
	   flatr.y = Math.min(m_GRIP, flatr.y);
	   flatr.y = Math.max(-m_GRIP, flatr.y);
	   flatr.y *= weight;
	   if(rear_slip==1)
	     flatr.y *= 0.6;
	  

	   // longtitudinal force on rear wheels - very simple traction model
	   
	   
	    //double torque = car.engine.getTorqueAtRPM(car.throttle);
	   double torque = 50;
	   	//System.out.println(car.throttle);
		double tF = VehiclePhysUtil.getDrF(torque, 8.761, 3.312, 0.33);
		//System.out.println(tF);
		ftraction.x = tF - car.brake*Math.signum(velocity.x);
	   //ftraction.x = 300*(car.throttle - car.brake*Math.signum(velocity.x));
	   ftraction.y = 0;
	   //System.out.println(sideslip);
	   if(rear_slip==1)
		  
	     ftraction.x *= 0.5;

	   // Forces and torque on body
	   // drag and rolling resistance
	   resistance.x = -( RESISTANCE*velocity.x + DRAG*velocity.x*Math.abs(velocity.x) );
	   resistance.y = -( RESISTANCE*velocity.y + DRAG*velocity.y*Math.abs(velocity.y) );

	   // sum forces
	   force.x = ftraction.x + Math.sin(car.steerangle) * flatf.x + flatr.x + resistance.x;
	   force.y = ftraction.y + Math.cos(car.steerangle) * flatf.y + flatr.y + resistance.y;

	   // torque on body from lateral forces
	   torque = b * flatf.y - c * flatr.y;
	   
	   // Acceleration
	   // Newton F = m.a, therefore a = F/m
	   acceleration.x = force.x/car.mass;
	   acceleration.y = force.y/car.mass;
	  double angular_acceleration = torque / inertia;

	// Velocity and position

	   // transform acceleration from car reference frame to world reference frame
	   acceleration_wc.x =  cs * acceleration.y + sn * acceleration.x;
	   acceleration_wc.y = -sn * acceleration.y + cs * acceleration.x;

	   // velocity is integrated acceleration
	   //
	   car.velocity_wc.x += delta_t * acceleration_wc.x;
	   car.velocity_wc.y += delta_t * acceleration_wc.y;

	   // position is integrated velocity
	   //
	 
	   this.car.move(MoverType.SELF, delta_t * car.velocity_wc.x, 0, delta_t * car.velocity_wc.y);
	   //car.position_wc.x += delta_t * car.velocity_wc.x;
	   //car.position_wc.y += delta_t * car.velocity_wc.y;

	// Angular velocity and heading

	   // integrate angular acceleration to get angular velocity
	   //
	   car.angularvelocity += delta_t * angular_acceleration;

	   // integrate angular velocity to get angular orientation
	   //
	   car.angle += delta_t * car.angularvelocity ;
	 }

}
