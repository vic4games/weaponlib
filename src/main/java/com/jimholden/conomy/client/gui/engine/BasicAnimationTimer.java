package com.jimholden.conomy.client.gui.engine;

import net.minecraft.client.Minecraft;

public class BasicAnimationTimer {
	
	public int max;
	public int min = 0;
	public int time = 0;
	
	public BasicAnimationTimer(int max, int min) {
		this.max = max;
		this.min = min;
	}
	
	public BasicAnimationTimer(int max) {
		this.max = max;
	}
	
	
	
	public void reset() {
		time = min;
	}
	
	public void tick() {
		if(time < max) {
			time += 1;
		}
	}
	
	public void tick(int i) {
		if(time+i < max) {
			time += i;
		} else {
			time = max;
		}
	}
	
	public void reverseTick() {
		if(time > min) time -= 1;
	}
	
	public void reverseTick(int i) {
		if(time > min) time -= i;
	}
	
	
	public double lastPosition() {
		if(time == min || time == max || time == -1) return calculatePos(time);
		
		return calculatePos(time-1);
	}
	
	public double calculatePos(double t) {
		return t/(double) max;
		//return (t-min)/(double)(max-min);
	}
	
	public double advancePosition() {
		if(time == max) return calculatePos(time);
		return calculatePos(time + 1);
	}
	
	public double position() {
		return calculatePos(time);
		
	}
	
	public double mcReverseInterp() {
		return advancePosition() + (position() - advancePosition())*Minecraft.getMinecraft().getRenderPartialTicks();
	}
	
	public double mcInterp() {
		return lastPosition() + (position() - lastPosition())*Minecraft.getMinecraft().getRenderPartialTicks();
	}
	
	public double smooth() {
		return (1 - Math.cos(mcInterp() * Math.PI)) / 2;
	}
	
	public boolean atMax() {
		return time == max;
	}
	
	public boolean atMin() {
		return time == min;
	}

}
