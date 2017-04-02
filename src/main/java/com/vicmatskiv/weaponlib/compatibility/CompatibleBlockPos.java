package com.vicmatskiv.weaponlib.compatibility;

public class CompatibleBlockPos {

    private int blockPosX;
    private int blockPosY;
    private int blockPosZ;

    public CompatibleBlockPos(int blockPosX, int blockPosY, int blockPosZ) {
        this.blockPosX = blockPosX;
        this.blockPosY = blockPosY;
        this.blockPosZ = blockPosZ;
    }

    public int getBlockPosX() {
        return blockPosX;
    }

    public int getBlockPosY() {
        return blockPosY;
    }

    public int getBlockPosZ() {
        return blockPosZ;
    }

}
