package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleEnumFacing;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class BlockHitMessage implements CompatibleMessage {
    private int posX;
    private int posY;
    private int posZ;
    private int enumFacing;

    public BlockHitMessage() {}

    public BlockHitMessage(int posX, int posY, int posZ, CompatibleEnumFacing enumFacing) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.enumFacing = enumFacing.ordinal();
    }

    public void fromBytes(ByteBuf buf) {
        posX = buf.readInt();
        posY = buf.readInt();
        posZ = buf.readInt();
        enumFacing = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeInt(posZ);
        buf.writeInt(enumFacing);
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
    
    public CompatibleEnumFacing getSideHit() {
        return CompatibleEnumFacing.values()[enumFacing];
    }
}
