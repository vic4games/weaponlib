package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.util.math.BlockPos;

public class CompatibleBlockPos {

    private BlockPos blockPos;

    public CompatibleBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

}
