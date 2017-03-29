package com.vicmatskiv.weaponlib.particle;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class SpawnParticleMessage implements CompatibleMessage {
    private double posX;
    private double posY;
    private double posZ;

    public SpawnParticleMessage() {}

    public SpawnParticleMessage(double posX, double posY, double posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void fromBytes(ByteBuf buf) {
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }
}
