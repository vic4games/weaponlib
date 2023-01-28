package com.jimholden.conomy.blocks;

public interface IMachine {
	
	public int timer = 0;
	
	public void produce();
	public void startMixing();
	public void setTimer(int val);
	public int getTimer();
	public void setAuto(boolean state);
	public boolean isAuto();

}
