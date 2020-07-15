package com.vicmatskiv.weaponlib;

import org.junit.Test;

public class ATest {

    @Test
    public void testArray() {
        float offset = 0.5f;
        float[][] offsets = new float [][] {
            {0, 0, 0},
            {offset, 0, 0},
            {-offset, 0, 0},
            {0, offset, 0},
            {0, -offset, 0},
            {0, 0, offset},
            {0, 0, -offset},
        };
        for(int i = 0; i < offsets.length; i++) {
            System.out.println(offsets[i][0] + " " + offsets[i][1] + " " + offsets[i][2]);
            float coefficient = (offset * 3f - Math.abs(offsets[i][0]) - Math.abs(offsets[i][1]) - Math.abs(offsets[i][2])) / (offset * 3f);
            System.out.println(coefficient);
        }
    }
}
