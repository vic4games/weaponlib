package com.vicmatskiv.weaponlib;

import org.junit.Test;

public class CirclePingPongTest {
    
    static class PointGenerator {
        private double r;
        private double alpha;
        private double nextAngle;
        private double attenuation;
        
        public PointGenerator(double r, double startAngle, double alpha, double attenuation) {
            this.r = r;
            this.alpha = alpha;
            this.nextAngle = startAngle;
            this.attenuation = attenuation;
        }
        
        public double[] next() {
            double x = r * Math.cos(nextAngle);
            double y = r * Math.sin(nextAngle);
            double rOrig = r;
            
            nextAngle += 2 * Math.PI - 2 * alpha;
            nextAngle %= 2 * Math.PI;
            
            r *= attenuation;
            return new double[] {rOrig, x, y};
        }
    }

    @Test
    public void test() {
        
        double r = 1;
        double startAngle = Math.PI / 5;
        double alpha = Math.PI / 4;
        PointGenerator generator = new PointGenerator(r, startAngle, alpha, 0.9);
        for(int i = 0; i < 100; i++) {
            double[] n = generator.next();
            System.out.println("r: " + n[0] + ", x: " + n[1] + ", y: " + n[2]);
        }
        
    }
}
