package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldSettings;

public class CompatibleWorldWrapper extends WorldClient {

    public CompatibleWorldWrapper(int dimentionId) {
        super(Minecraft.getMinecraft().getNetHandler(), 
                new WorldSettings(0L, 
                        Minecraft.getMinecraft().theWorld.getWorldInfo().getGameType(), 
                        Minecraft.getMinecraft().theWorld.getWorldInfo().isMapFeaturesEnabled(), 
                        Minecraft.getMinecraft().theWorld.getWorldInfo().isHardcoreModeEnabled(), 
                        Minecraft.getMinecraft().theWorld.getWorldInfo().getTerrainType()),
                dimentionId, 
                Minecraft.getMinecraft().gameSettings.difficulty, 
                Minecraft.getMinecraft().theWorld.theProfiler);
    }

}
