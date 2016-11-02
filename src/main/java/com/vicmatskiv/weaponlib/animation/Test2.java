package com.vicmatskiv.weaponlib.animation;

import org.junit.Test;

public class Test2 {

	@Test
	public void test() {
		int segmentCount = 3;
		long totalDuration = 99;
		
		float segmentLength = (float)totalDuration / segmentCount;
		int currentIndex = 0;
		for(int currentOffset = 0; currentOffset < totalDuration; currentOffset++) {
			int index = (int) Math.floorDiv(segmentCount * currentOffset, totalDuration);
			if(index != currentIndex) {
				System.out.println("Switch!");
				currentIndex = index;
			}
			
			float segmentDuration = totalDuration / segmentCount;
			float segmentOffset = currentOffset - index * segmentLength;
			float segmentProgress = (float)segmentOffset / (segmentDuration);
			System.out.println("Offset: " + currentOffset + ", index: " + index 
					+ ", offset: " + segmentOffset
					+ ", progress: " + segmentProgress);
		}
		
	}
}
