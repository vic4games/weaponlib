package com.vicmatskiv.weaponlib;

import io.netty.buffer.ByteBuf;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

public class BlockHitMessage implements CompatibleMessage {
    private int posX;
    private int posY;
    private int posZ;
    private int sideHit;

    public BlockHitMessage() {}

    public BlockHitMessage(int posX, int posY, int posZ, int sideHit) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.sideHit = sideHit;
    }

    public void fromBytes(ByteBuf buf) {
        posX = buf.readInt();
        posY = buf.readInt();
        posZ = buf.readInt();
        sideHit = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeInt(posZ);
        buf.writeInt(sideHit);
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosZ() {
        return posZ;
    }
    
    public int getSideHit() {
        return sideHit;
    }
}
