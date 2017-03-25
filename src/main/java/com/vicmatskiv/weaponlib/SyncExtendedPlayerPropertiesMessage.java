package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class SyncExtendedPlayerPropertiesMessage implements CompatibleMessage {

	private ExtendedPlayerProperties properties;

	public SyncExtendedPlayerPropertiesMessage() {}
	
	public SyncExtendedPlayerPropertiesMessage(ExtendedPlayerProperties properties) {
		this.properties = properties;
	}

	public void fromBytes(ByteBuf buf) {
	    properties = ExtendedPlayerProperties.deserializeForClient(buf);
	}

	public void toBytes(ByteBuf buf) {
	    properties.serializeForClient(buf);
	}

    public ExtendedPlayerProperties getExtendedPlayerProperties() {
        return properties;
    }

	
}
