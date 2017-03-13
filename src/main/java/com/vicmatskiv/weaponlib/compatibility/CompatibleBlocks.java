package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class CompatibleBlocks {

    public static final CompatibleBlocks PLANK = new CompatibleBlocks(Blocks.planks);
    
    private Block block;
    
    private CompatibleBlocks(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
