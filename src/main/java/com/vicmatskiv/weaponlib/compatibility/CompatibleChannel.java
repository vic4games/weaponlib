package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.LaserSwitchMessage;
import com.vicmatskiv.weaponlib.LaserSwitchMessageHandler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

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
    
    public void registerMessage(Class<LaserSwitchMessageHandler> messageHandlerClass, Class<LaserSwitchMessage> messageType,
			int discriminator, CompatibleSide side) {
		channel.registerMessage(messageHandlerClass, messageType, discriminator, side.getSide());
	}
}
