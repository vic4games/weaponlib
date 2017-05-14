package com.vicmatskiv.weaponlib.particle;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;

public class SpawnParticleMessage implements CompatibleMessage {

    public enum ParticleType { BLOOD, SHELL }

    private double posX;
    private double posY;
    private double posZ;
    private int count;
    private ParticleType particleType;

    public SpawnParticleMessage() {}

    public SpawnParticleMessage(ParticleType particleType, int count, double posX, double posY, double posZ) {
        this.particleType = particleType;
        this.count = count;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void fromBytes(ByteBuf buf) {
        particleType = ParticleType.values()[buf.readInt()];
        count = buf.readInt();
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(particleType.ordinal());
        buf.writeInt(count);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
    }

    public ParticleType getParticleType() {
        return particleType;
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

    public int getCount() {
        return count;
    }
}
