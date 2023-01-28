package com.jimholden.conomy.entity.rope;

import java.util.ArrayList;

import com.jimholden.conomy.entity.rope.RopeSimulation.Point;
import com.jimholden.conomy.entity.rope.RopeSimulation.Stick;

import net.minecraft.util.math.Vec3d;

public class FABRIK {
	
	public static class Point {
		public Vec3d pos = Vec3d.ZERO, prevPos = Vec3d.ZERO;
		public boolean locked = false;
		
		public Point(Vec3d pos) {
			this.pos = pos;
			this.prevPos = pos;
			
			
		}
		
		public Point(Vec3d pos, boolean locked) {
			this.pos = pos;
			this.prevPos = pos;
			this.locked = locked;
			
			
			
		}
	}
	
	public static class Stick {
		public Point pointA, pointB;
		public float length;
		
		public Stick(Point a, Point b) {
			this.pointA = a;
			this.pointB = b;
			
			this.length = (float) a.pos.distanceTo(b.pos);
		}
	}

	
	public ArrayList<Point> points = new ArrayList<>();
	public ArrayList<Stick> sticks = new ArrayList<>();
	
	public FABRIK() {
		
	}
	
	public void simulate(double dt, Vec3d target) {
		Point origin = points.get(0);
		
		
		for(int itr = 0; itr < 100; ++itr) {
			boolean startingFromTarget = itr%2 == 0;
			
			
			if(!startingFromTarget) {
				// FORWARD
				
				for(int i = 1; i < points.size(); ++i) {
					Vec3d dir = points.get(i).pos.subtract(points.get(i-1).pos);
					Point p = points.get(i);
					//p.pos = points.get(i-1).pos.add(dir)
				}
				
			} else {
				// BACKWARD
				
				
				
				
			}
			
			
		}
		
		
	}
	
	public Point newPoint(Vec3d pos) {
		Point p = new Point(pos, false);
		points.add(p);
		return p;
	}
	
	
	public void autoSticks() {
		for(int p = 0; p < points.size()-1; ++p) {
			Stick stick = new Stick(points.get(p), points.get(p+1));
			sticks.add(stick);
		}
	}
	

}
