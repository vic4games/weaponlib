package com.vicmatskiv.weaponlib.particle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleParticle;
import com.vicmatskiv.weaponlib.compatibility.CompatibleParticle.CompatibleParticleBreaking;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class SpawnParticleMessageHandler implements CompatibleMessageHandler<SpawnParticleMessage, CompatibleMessage>  {
    
    private ModContext modContext;
    
    private double yOffset = 1;

    public SpawnParticleMessageHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(SpawnParticleMessage message, CompatibleMessageContext ctx) {
        if(!ctx.isServerSide()) {
            compatibility.runInMainClientThread(() -> {
                World world = compatibility.world(compatibility.clientPlayer());
                for (int i = 0; i < message.getCount(); ++i) {
                    CompatibleParticleBreaking particle = CompatibleParticle.createParticleBreaking(
                            modContext, world, message.getPosX(), message.getPosY() + yOffset, message.getPosZ());
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                }
            });
        }
        return null;
    }
}
