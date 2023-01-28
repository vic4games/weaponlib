package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleManager;

public class CompatibleParticleManager {
   
    private ParticleManager effectRenderer;

    public CompatibleParticleManager(WorldClient world) {
        this.effectRenderer = new ParticleManager(world, Minecraft.getMinecraft().getTextureManager());
    }
    
    public CompatibleParticleManager(ParticleManager effectRenderer) {
        this.effectRenderer = effectRenderer;
    }

    public ParticleManager getParticleManager() {
        return effectRenderer;
    }

}
