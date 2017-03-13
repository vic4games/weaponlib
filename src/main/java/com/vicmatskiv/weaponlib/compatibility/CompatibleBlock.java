package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class CompatibleBlock {

    public static final CompatibleBlock PLANK = new CompatibleBlock(Blocks.PLANKS);
    
    private Block block;
    
    private CompatibleBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
