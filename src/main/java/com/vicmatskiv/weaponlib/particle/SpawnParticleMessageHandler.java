package com.vicmatskiv.weaponlib.particle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

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
                        modContext.getEffectManager().spawnExplosionSmoke(
                                message.getPosX(), message.getPosY(), message.getPosZ(),
                                message.getMotionX(), message.getMotionY(), message.getMotionZ(),
                                0.2f * compatibility.world(compatibility.clientPlayer()).rand.nextFloat(),
                                300, ExplosionSmokeFX.Behavior.SMOKE_GRENADE);
                        break;
                    default:
                        break;
                    }

                }
            });
        }
        return null;
    }
}
