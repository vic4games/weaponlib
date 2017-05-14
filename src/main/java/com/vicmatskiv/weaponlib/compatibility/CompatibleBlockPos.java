package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.math.BlockPos;

public class CompatibleBlockPos {

    private BlockPos blockPos;

    public CompatibleBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public CompatibleBlockPos(int xCoord, int yCoord, int zCoord) {
        this.blockPos = new BlockPos(xCoord, yCoord, zCoord);
    }

    public CompatibleBlockPos(CompatibleVec3 projectedPos) {
        this.blockPos = new BlockPos(projectedPos.getVec());
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

}
