package com.vicmatskiv.weaponlib.animation;

import java.util.Queue;

public interface MultipartPositioning<Part, Context> {
	
	public interface Positioner<Part, Context> {
		public void position(Part part, Context context);
		public default void randomize(float rate, float amplitude) {};
	}
	
	//, float randomizationRate, float randomizationAmplitude

	public boolean isExpired(Queue<MultipartPositioning<Part, Context>> positioningQueue);
	
	public Positioner<Part, Context> getPositioner();

}
