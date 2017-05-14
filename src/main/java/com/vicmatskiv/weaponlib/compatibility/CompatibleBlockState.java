package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.block.state.IBlockState;

public class CompatibleBlockState {

    private IBlockState blockState;

    CompatibleBlockState(IBlockState blockState) {
        this.blockState = blockState;
    }

    public IBlockState getBlockState() {
        return blockState;
    }
}
