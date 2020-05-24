package com.vicmatskiv.weaponlib.animation;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.PlayerRawPitchAnimationManager.State;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class ScreenShakeAnimation implements PlayerAnimation {
    
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
    
    private float startRotateZ = 0f;
    
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
    
    private float zRotationCoefficient = 0.5f;
    
    private float rotationAttenuation = 0.5f;// = ATTENUATION_COEFFICIENT;

    private long transitionDuration = 2000;

    private long startTime;
    
    private PlayerRawPitchAnimationManager.State state;
    
    private CirclePointGenerator circlePointGenerator;

    private float totalAdjustment;
    
    private float cumulativeAttenuation = 1f;
    
    public static class Builder {
        
        private float rotationAttenuation = 0.5f;
        private float translationAttenuation = 0.5f;
        private long transitionDuration = 2000;
        private float xTranslateCoefficient = 0.05f;
        private float yTranslateCoefficient = 0.05f;
        private float zTranslateCoefficient = 0.1f;
        private float zRotationCoefficient = 0.5f;
        
        private PlayerRawPitchAnimationManager.State state;
        
        Builder withRotationAttenuation(float rotationAttenuation) {
            this.rotationAttenuation = rotationAttenuation;
            return this;
        }
        
        Builder withTranslationAttenuation(float translationAttenuation) {
            this.translationAttenuation = translationAttenuation;
            return this;
        }
        
        Builder withTransitionDuration(long transitionDuration) {
            this.transitionDuration = transitionDuration;
            return this;
        }
        
        Builder withXTranslateCoefficient(float xTranslateCoefficient) {
            this.xTranslateCoefficient = xTranslateCoefficient;
            return this;
        }
        
        Builder withYTranslateCoefficient(float yTranslateCoefficient) {
            this.yTranslateCoefficient = yTranslateCoefficient;
            return this;
        }
        
        Builder withZTranslateCoefficient(float zTranslateCoefficient) {
            this.zTranslateCoefficient = zTranslateCoefficient;
            return this;
        }
        
        Builder withZRotationCoefficient(float zRotationCoefficient) {
            this.zRotationCoefficient = zRotationCoefficient;
            return this;
        }
        
        Builder withState(PlayerRawPitchAnimationManager.State state) {
            this.state = state;
            return this;
        }
        
        PlayerAnimation build() {
            if(state == null) {
                throw new IllegalStateException("State is not set");
            }
            
            ScreenShakeAnimation animation = new ScreenShakeAnimation(state);
            animation.rotationAttenuation = rotationAttenuation;
            animation.xTranslateCoefficient = xTranslateCoefficient;
            animation.yTranslateCoefficient = yTranslateCoefficient;
            animation.zTranslateCoefficient = zTranslateCoefficient;
            animation.zRotationCoefficient = zRotationCoefficient;
            animation.transitionDuration = transitionDuration;
            animation.state = state;
            animation.circlePointGenerator = new CirclePointGenerator(1f, (float)Math.PI / 4f, (float)Math.PI / 5f, translationAttenuation);
            return animation;
        }
    }
    
    private ScreenShakeAnimation(PlayerRawPitchAnimationManager.State state) {
        this.state = state;
    }
    
    public void update(EntityPlayer player, boolean fadeOut) {
        float progress = (float)(System.currentTimeMillis() - startTime) / transitionDuration;
                
        if(progress >= 1f) {
            progress = 0f;
            startTime = System.currentTimeMillis();
        }
        
        if(progress == 0f) {
            float[] next = circlePointGenerator.next();
            startX = targetX;
            startY = targetY;
            startZ = targetZ;
            
            startRotateZ = targetRotateZ;
                    
            targetX = next[1] * xTranslateCoefficient;
            targetY = next[2] * yTranslateCoefficient;
            targetZ = targetZ * rotationAttenuation * zTranslateCoefficient;
            
            if(targetRotateZ == 0f) {
                targetRotateZ = 1f;
            } else {
                targetRotateZ = -targetRotateZ * rotationAttenuation ;
            }

            totalAdjustment += state.getStepAdjustement();       
        }
        
        float adjustedProgress = MathHelper.sin(progress * (float)Math.PI / 2f);
        float currentX = startX + (targetX - startX) * adjustedProgress;
        float currentY = startY + (targetY - startY) * adjustedProgress;
        float currentZ = startZ + (targetZ - startZ) * adjustedProgress;
        
        GL11.glTranslatef(currentX, currentY, currentZ);
        
        float currentRotateZ = startRotateZ + (targetRotateZ - startRotateZ) * adjustedProgress;
        GL11.glRotatef(currentRotateZ * zRotationCoefficient, 0f, 0f, 1f);
        
        cumulativeAttenuation *= rotationAttenuation;
    }

    public void reset(EntityPlayer player, boolean force) {
        if(force || totalAdjustment != 0f) {
            System.out.println("Force reset");
            totalAdjustment = 0f;
//            attenuation = ATTENUATION_COEFFICIENT;
            cumulativeAttenuation = 1f;
            circlePointGenerator.reset();
            targetZ = Math.signum(targetZ) * zTranslateCoefficient;
            targetRotateZ = -Math.signum(targetRotateZ) /** rand.nextFloat()*/ * rotationAttenuation;
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
