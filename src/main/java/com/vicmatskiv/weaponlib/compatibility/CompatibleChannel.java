package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.particle.SpawnParticleMessage;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class CompatibleChannel {
	
	private SimpleNetworkWrapper channel;
	
	public CompatibleChannel(SimpleNetworkWrapper channel) {
		this.channel = channel;
	}

	public SimpleNetworkWrapper getChannel() {
		return channel;
	}
	
    public <Request extends CompatibleMessage, Response extends CompatibleMessage> void registerMessage(
    		CompatibleMessageHandler<? super Request, ? extends Response> messageHandler, 
    		Class<Request> requestMessageType, int discriminator, CompatibleSide side) {
    	channel.registerMessage(messageHandler, requestMessageType, discriminator, side.getSide());
    }

    public void sendToAllAround(SpawnParticleMessage spawnParticleMessage, CompatibleTargetPoint point) {
        channel.sendToAllAround(spawnParticleMessage, point.getTargetPoint());
    }
}
