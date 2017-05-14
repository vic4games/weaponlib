package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class EntityControlMessage implements CompatibleMessage {

    public static enum EntityAction { STOP }

    private int action;

    public EntityControlMessage() {}

    public EntityControlMessage(EntityAction action) {
        this.action = action.ordinal();
    }

    public void fromBytes(ByteBuf buf) {
        action = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(action);
    }

    public EntityAction getAction() {
        return EntityAction.values()[action];
    }
}
