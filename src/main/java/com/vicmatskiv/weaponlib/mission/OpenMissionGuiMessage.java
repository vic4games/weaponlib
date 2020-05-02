package com.vicmatskiv.weaponlib.mission;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class OpenMissionGuiMessage implements CompatibleMessage {

    private int guiInventoryId;

    public OpenMissionGuiMessage() {}
    
    public OpenMissionGuiMessage(int inventoryId) {
        this.guiInventoryId = inventoryId;
    }

    public void fromBytes(ByteBuf buf) {
        guiInventoryId = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(guiInventoryId);
    }
    
    public int getGuiInventoryId() {
        return guiInventoryId;
    }
}
