package com.vicmatskiv.weaponlib.animation.jim;

import java.util.HashMap;

public class AnimationSet {

	private HashMap<String, SingleAnimation> animations = new HashMap<>();
	
	
	
	
	
	public SingleAnimation getSingleAnimation(String subName) {
		if(!animations.containsKey(subName)) return null;
		return animations.get(subName);
	}
	
	public void addSingleAnimation(SingleAnimation sa) {
		animations.put(sa.getAnimationName(), sa);
	}

}
