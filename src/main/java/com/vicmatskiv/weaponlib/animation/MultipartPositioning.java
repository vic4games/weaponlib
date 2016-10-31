package com.vicmatskiv.weaponlib.animation;

import java.util.Queue;

public interface MultipartPositioning<Part, Context> {
	
	public interface Positioner<Part, Context> {
		public void position(Part part, Context context);
	}

	public boolean isExpired(Queue<MultipartPositioning<Part, Context>> positioningQueue);
	
	public Positioner<Part, Context> getPositioner();

}
