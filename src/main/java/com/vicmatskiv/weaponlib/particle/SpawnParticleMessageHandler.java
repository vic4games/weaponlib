package com.vicmatskiv.weaponlib.particle;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleParticle;
import com.vicmatskiv.weaponlib.compatibility.CompatibleParticle.CompatibleParticleBreaking;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class SpawnParticleMessageHandler implements CompatibleMessageHandler<SpawnParticleMessage, CompatibleMessage>  {

    @Override
    public <T extends CompatibleMessage> T onCompatibleMessage(SpawnParticleMessage message, CompatibleMessageContext ctx) {
        if(!ctx.isServerSide()) {
            ctx.runInMainThread(() -> {
                World world = compatibility.world(compatibility.clientPlayer());
                System.out.println("Player: " + compatibility.clientPlayer());
                for (int i = 0; i < 15; ++i) {
                    System.out.printf("Spawning at %.2f %.2f %.2f\n", 
                            message.getPosX(), message.getPosY(), message.getPosZ());
                    CompatibleParticleBreaking particle = CompatibleParticle.createParticleBreaking(
                            world, message.getPosX(), message.getPosY() + 1, message.getPosZ(), null);
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
//                    world.spawnParticle("snowballpoof", message.getPosX(), 
//                            message.getPosY() + 1, message.getPosZ(), 0.0D, 0.0D, 0.0D);
                }
                
                //FMLClientHandler.instance().getClient().theWorld.spawnParticle("hugeexplosion", message.posX, message.posY+1, message.posZ, 0.0D, 0.0D, 0.0D);
            });
        }
        return null;
    }
}
