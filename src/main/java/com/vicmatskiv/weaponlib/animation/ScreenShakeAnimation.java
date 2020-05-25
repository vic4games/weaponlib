package com.vicmatskiv.weaponlib.animation;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.animation.ScreenShakeAnimationManager.State;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class ScreenShakeAnimation implements ScreenShakingAnimation {
    
    static class CirclePointGenerator {
        
        private float startingRadius;
        private float nextRadius;
        private float alpha;
        private float nextAngle;
        private float attenuation;
        private float cumulativeAttenuation = 1f;
        
        public CirclePointGenerator(float radius, float startAngle, float alpha, float attenuation) {
            this.startingRadius = radius;
            this.nextRadius = radius;
            this.alpha = alpha;
            this.nextAngle = startAngle;
            this.attenuation = attenuation;
        }
        
        public float[] next() {
            float currentX = nextRadius * MathHelper.cos(nextAngle);
            float currentY = nextRadius * MathHelper.sin(nextAngle);
            float currentRadius = nextRadius;
            
            nextAngle += 2 * Math.PI - 2 * alpha;
            nextAngle %= 2 * Math.PI;
            
            nextRadius *= attenuation;
            cumulativeAttenuation *= attenuation;
            return new float[] {currentRadius, currentX, currentY};
        }
        
        public void reset() {
            nextRadius = startingRadius;
            cumulativeAttenuation = 1f;
        }
    }
    
    private float startRotateX = 0f;
    private float startRotateY = 0f;
    private float startRotateZ = 0f;
    
    private float targetRotateX = 0f;
    private float targetRotateY = 0f;
    private float targetRotateZ = 0f;
    
    private float startX = 0f;
    private float startY = 0f;
    private float startZ = 0f;
    
    private float targetX = 0f;
    private float targetY = 0f;
    private float targetZ = 0f;
    
    private float xTranslateCoefficient = 0.05f;
    private float yTranslateCoefficient = 0.05f;
    private float zTranslateCoefficient = 0.1f;
    
    private float xRotateCoefficient = 0.5f;
    private float yRotateCoefficient = 0.5f;
    private float zRotateCoefficient = 0.5f;
    
    private float rotationAttenuation = 0.5f;// = ATTENUATION_COEFFICIENT;

    private long transitionDuration = 2000;

    private long startTime;
    
    private ScreenShakeAnimationManager.State state;
    
    private CirclePointGenerator circlePointGenerator;

    private float totalAdjustment;
    
    private float cumulativeAttenuation = 1f;
    
    private boolean initialized;
    
    public static class Builder {
        
        private float rotationAttenuation = 0.5f;
        private float translationAttenuation = 0.5f;
        private long transitionDuration = 2000;
        private float xTranslateCoefficient = 0.05f;
        private float yTranslateCoefficient = 0.05f;
        private float zTranslateCoefficient = 0.1f;
        private float xRotateCoefficient = 0.5f;
        private float yRotateCoefficient = 0.5f;
        private float zRotateCoefficient = 0.5f;
        
        private ScreenShakeAnimationManager.State state;
        
        public Builder withRotationAttenuation(float rotationAttenuation) {
            this.rotationAttenuation = rotationAttenuation;
            return this;
        }
        
        public Builder withTranslationAttenuation(float translationAttenuation) {
            this.translationAttenuation = translationAttenuation;
            return this;
        }
        
        public Builder withTransitionDuration(long transitionDuration) {
            this.transitionDuration = transitionDuration;
            return this;
        }
        
        public Builder withXTranslateCoefficient(float xTranslateCoefficient) {
            this.xTranslateCoefficient = xTranslateCoefficient;
            return this;
        }
        
        public Builder withYTranslateCoefficient(float yTranslateCoefficient) {
            this.yTranslateCoefficient = yTranslateCoefficient;
            return this;
        }
        
        public Builder withZTranslateCoefficient(float zTranslateCoefficient) {
            this.zTranslateCoefficient = zTranslateCoefficient;
            return this;
        }
        
        public Builder withXRotateCoefficient(float xRotateCoefficient) {
            this.xRotateCoefficient = xRotateCoefficient;
            return this;
        }
        
        public Builder withYRotateCoefficient(float yRotateCoefficient) {
            this.yRotateCoefficient = yRotateCoefficient;
            return this;
        }
        
        public Builder withZRotateCoefficient(float zRotateCoefficient) {
            this.zRotateCoefficient = zRotateCoefficient;
            return this;
        }
        
        public Builder withState(ScreenShakeAnimationManager.State state) {
            this.state = state;
            return this;
        }
        
        public Builder withState(RenderableState state) {
            this.state = ScreenShakeAnimationManager.toManagedState(state);
            return this;
        }
        
        public ScreenShakingAnimation build() {
            if(state == null) {
                throw new IllegalStateException("State is not set");
            }
            
            ScreenShakeAnimation animation = new ScreenShakeAnimation(state);
            animation.rotationAttenuation = rotationAttenuation;
            animation.xTranslateCoefficient = xTranslateCoefficient;
            animation.yTranslateCoefficient = yTranslateCoefficient;
            animation.zTranslateCoefficient = zTranslateCoefficient;
            animation.xRotateCoefficient = xRotateCoefficient;
            animation.yRotateCoefficient = yRotateCoefficient;
            animation.zRotateCoefficient = zRotateCoefficient;
            animation.transitionDuration = transitionDuration;
            animation.state = state;
            Random rand = new Random();
            animation.circlePointGenerator = new CirclePointGenerator(1f, 
                    (float)Math.PI * rand.nextFloat(), (float)Math.PI / 5f, translationAttenuation);
            return animation;
        }
    }
    
    private ScreenShakeAnimation(ScreenShakeAnimationManager.State state) {
        this.state = state;
    }
    
    public void update(EntityPlayer player, boolean fadeOut) {
        
        float progress = (float)(System.currentTimeMillis() - startTime) / transitionDuration;
        float[] next = circlePointGenerator.next();
        
        if(!initialized) {
            targetRotateX = 1f;
            targetRotateY = 1f;
            targetRotateZ = 1f;
            
            targetX = next[1];
            targetY = next[2];
            targetZ = 1f;
            
            initialized = true;
            
        } else if(progress >= 1f) {
            progress = 0f;
            
            startX = targetX;
            startY = targetY;
            startZ = targetZ;
            
            startRotateX = targetRotateX;
            startRotateY = targetRotateY;
            startRotateZ = targetRotateZ;
                    
            targetX = next[1];
            targetY = next[2];
            targetZ = targetZ * circlePointGenerator.attenuation;
            
            targetRotateX = -targetRotateX * rotationAttenuation;
            targetRotateY = -targetRotateY * rotationAttenuation;
            targetRotateZ = -targetRotateZ * rotationAttenuation;

            totalAdjustment += state.getStepAdjustement();       
        }
        
        float adjustedProgress = MathHelper.sin(progress * (float)Math.PI / 2f);
        float currentX = startX + (targetX - startX) * adjustedProgress;
        float currentY = startY + (targetY - startY) * adjustedProgress;
        float currentZ = startZ + (targetZ - startZ) * adjustedProgress;
        
        GL11.glTranslatef(currentX * xTranslateCoefficient, currentY * yTranslateCoefficient, currentZ * zTranslateCoefficient);
        
        float currentRotateX = startRotateX + (targetRotateX - startRotateX) * adjustedProgress;
        float currentRotateY = startRotateY + (targetRotateY - startRotateY) * adjustedProgress;
        float currentRotateZ = startRotateZ + (targetRotateZ - startRotateZ) * adjustedProgress;
        
        GL11.glRotatef(currentRotateX * xRotateCoefficient, 1f, 0f, 0f);
        GL11.glRotatef(currentRotateY * yRotateCoefficient, 0f, 1f, 0f);
        GL11.glRotatef(currentRotateZ * zRotateCoefficient, 0f, 0f, 1f);
        
        cumulativeAttenuation *= rotationAttenuation;
    }

    public void reset(EntityPlayer player, boolean force) {
        if(force || totalAdjustment != 0f) {
            System.out.println("Resetting, targetRotateZ: " + targetRotateZ);
            totalAdjustment = 0f;
            cumulativeAttenuation = 1f;
            circlePointGenerator.reset();
            targetZ = Math.signum(targetZ); // * zTranslateCoefficient;
            
            targetRotateX = 1f; //-Math.signum(targetRotateX); // * rotationAttenuation;
            targetRotateY = 1f; //-Math.signum(targetRotateY); // * rotationAttenuation;
            targetRotateZ = 1f; //-Math.signum(targetRotateZ); // * rotationAttenuation;
        }
    }

    @Override
    public boolean isCompleted() {
        return cumulativeAttenuation < 0.001f && circlePointGenerator.cumulativeAttenuation < 0.001f;
    }

    @Override
    public State getState() {
        return state;
    }
}
