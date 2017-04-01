package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;

public class CompatibleParticleManager {
   
    private EffectRenderer effectRenderer;

    public CompatibleParticleManager(WorldClient world) {
        this.effectRenderer = new EffectRenderer(world, Minecraft.getMinecraft().getTextureManager());
    }
    
    public CompatibleParticleManager(EffectRenderer effectRenderer) {
        this.effectRenderer = effectRenderer;
    }

    public EffectRenderer getParticleManager() {
        return effectRenderer;
    }

}
