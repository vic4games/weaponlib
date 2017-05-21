package com.vicmatskiv.weaponlib.particle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class SpawnParticleMessageHandler implements CompatibleMessageHandler<SpawnParticleMessage, CompatibleMessage>  {

    private ModContext modContext;

    @SuppressWarnings("unused")
    private double yOffset = 1;

    public SpawnParticleMessageHandler(ModContext modContext) {
        this.modContext = modContext;
    }

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(SpawnParticleMessage message, CompatibleMessageContext ctx) {
        if(!ctx.isServerSide()) {
            compatibility.runInMainClientThread(() -> {
                for (int i = 0; i < message.getCount(); ++i) {
                    switch(message.getParticleType()) {
                    case BLOOD:
                        compatibility.addBreakingParticle(modContext, message.getPosX(), message.getPosY(), message.getPosZ());
                        break;
                    case SMOKE_GRENADE_SMOKE:

                        {
                            World world = compatibility.world(compatibility.clientPlayer());
                            ExplosionSmokeFX smokeParticle = new ExplosionSmokeFX(
                                    world,
                                    message.getPosX(),
                                    message.getPosY(),
                                    message.getPosZ(),
                                    0.2f * world.rand.nextFloat(),
                                    (float)message.getMotionX(),
                                    (float)message.getMotionY(),
                                    (float)message.getMotionZ(),
                                    300,
                                    ExplosionSmokeFX.Behavior.SMOKE_GRENADE);

                            Minecraft.getMinecraft().effectRenderer.addEffect(smokeParticle);
                        }
                        break;
                    }

                }
            });
        }
        return null;
    }
}
