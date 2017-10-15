package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.UUID;

import com.vicmatskiv.weaponlib.SpreadableExposure.Blackout;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.shader.DynamicShaderGroupSource;
import com.vicmatskiv.weaponlib.shader.DynamicShaderGroupSourceProvider;
import com.vicmatskiv.weaponlib.shader.DynamicShaderPhase;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

class PipelineShaderGroupSourceProvider implements DynamicShaderGroupSourceProvider {
    
    private boolean nightVisionEnabled;
    private boolean blurEnabled;
    private boolean vignetteEnabled;
    private float sepiaRatio;
    private float exposureProgress;
    private float vignetteRadius;
    private float brightness;
    private SpreadableExposure spreadableExposure;
    	    
    final DynamicShaderGroupSource source = new DynamicShaderGroupSource(UUID.randomUUID(),
            new ResourceLocation("weaponlib:/com/vicmatskiv/weaponlib/resources/post-processing-pipeline.json"))
                .withUniform("NightVisionEnabled", context -> nightVisionEnabled ? 1.0f : 0.0f)
                .withUniform("BlurEnabled", context -> blurEnabled ? 1.0f : 0.0f)
                .withUniform("BlurVignetteRadius", context -> 0.0f)
                .withUniform("Radius", context -> 10f)
                .withUniform("Progress", context -> exposureProgress)
                .withUniform("VignetteEnabled", context -> vignetteEnabled ? 1.0f : 0.0f)
                .withUniform("VignetteRadius", context -> vignetteRadius)
                .withUniform("Brightness", context -> brightness)
                .withUniform("SepiaRatio", context -> sepiaRatio)
                .withUniform("IntensityAdjust", context -> 40f - Minecraft.getMinecraft().gameSettings.gammaSetting * 38)
                .withUniform("NoiseAmplification", context ->  2f + 3f * Minecraft.getMinecraft().gameSettings.gammaSetting);
    
    @Override
    public DynamicShaderGroupSource getShaderSource(DynamicShaderPhase phase) {
        spreadableExposure = CompatibleExposureCapability.getExposure(compatibility.clientPlayer(), SpreadableExposure.class);
        exposureProgress = MiscUtils.smoothstep(0, 1, spreadableExposure != null ? spreadableExposure.getTotalDose() : 0f);
        updateNightVision();
        updateVignette();
        updateBlur();
        updateSepia();
        updateBrightness();
        spreadableExposure = null;
        return nightVisionEnabled || blurEnabled || vignetteEnabled || sepiaRatio > 0 ?
                source : null;
    }
    
    private void updateBrightness() {
        brightness = 1f;
        
        if(spreadableExposure != null && !compatibility.clientPlayer().isDead) {
            Blackout blackout = spreadableExposure.getBlackout();
            blackout.update();
            switch(blackout.getPhase()) {
            case ENTER:
                brightness = 1f - blackout.getEnterProgress();
                break;
            case EXIT:
                brightness = blackout.getExitProgress();
                break;
            case DARK:
                brightness = 0f;
                break;
            case NONE:
                brightness = 1f;
                break;
            }
        }
    }

    private void updateBlur() {
        blurEnabled = exposureProgress > 0.01f; // TODO: set min
    }

    private void updateVignette() {
        vignetteEnabled = nightVisionEnabled;
        if(!vignetteEnabled) {
            ItemStack helmetStack = compatibility.getHelmet();
            if(helmetStack != null && helmetStack.getItem() instanceof CustomArmor) {
                vignetteEnabled = ((CustomArmor)helmetStack.getItem()).hasNightVision();
            }
        }
        vignetteRadius = 0.55f;            
    }

    private void updateNightVision() {
        ItemStack helmetStack = compatibility.getHelmet();
        if(helmetStack != null) {
            NBTTagCompound tagCompound = compatibility.getTagCompound(helmetStack);
            if(tagCompound != null) {
                nightVisionEnabled = tagCompound.getBoolean("nv");
            } else {
                nightVisionEnabled = false;
            }
        } else {
            nightVisionEnabled = false;
        }
    }
    
    private void updateSepia() {
        sepiaRatio = exposureProgress;
    }

}