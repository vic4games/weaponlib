package com.vicmatskiv.weaponlib.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MultipartTransition<Part, Context> {
	
	private Map<Part, BiConsumer<Part, Context>> multipartPositionFunctions = new HashMap<>();
	private long duration;
	private long pause;
	
	public MultipartTransition(Part part, BiConsumer<Part, Context> positionFunction, long duration, long pause) {
		this.duration = duration;
		this.pause = pause;
		multipartPositionFunctions.put(part, positionFunction);
	}
	
	public MultipartTransition(Part part, BiConsumer<Part, Context> positionFunction, long duration) {
		this(part, positionFunction, duration, 0);
	}
	
	public MultipartTransition(long duration, long pause) {
		this.duration = duration;
		this.pause = pause;
	}
	
	public MultipartTransition(long duration) {
		this(duration, 0);
	}
	
	public MultipartTransition<Part, Context> withPartPositionFunction(Part part, BiConsumer<Part, Context> positionFunction) {
		this.multipartPositionFunctions.put(part, positionFunction);
		return this;
	}
	
	public void position(Part part, Context context) {
		BiConsumer<Part, Context> positionFunction = multipartPositionFunctions.get(part);
		if(positionFunction == null) {
			throw new IllegalArgumentException("Don't know anything about part " + part);
		}
		positionFunction.accept(part, context);
	}

//	public MultipartPositioning getPositioning() {
//		return positionableSystem;
//	}

	public long getDuration() {
		return duration;
	}

	public long getPause() {
		return pause;
	}
}
