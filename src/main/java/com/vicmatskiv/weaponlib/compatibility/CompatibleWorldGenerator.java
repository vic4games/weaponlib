package com.vicmatskiv.weaponlib.compatibility;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

public abstract class CompatibleWorldGenerator implements IWorldGenerator {

    public void generate(Block block, int maxVeinSize,
            CompatibleBlocks target, World world, Random random,
            int posX, int posY, int posZ) {
        new WorldGenMinable(block.getDefaultState(), maxVeinSize)
            .generate(world, random, new BlockPos(posX, posY, posZ));
    }
}
