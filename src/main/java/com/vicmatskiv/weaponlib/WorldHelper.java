package com.vicmatskiv.weaponlib;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class WorldHelper {

	public static Block getBlockAtPosition(World world, MovingObjectPosition position) {
		Block block = world.getBlockState(position.getBlockPos()).getBlock();
		return block;
	}

	public static void destroyBlock(World world, MovingObjectPosition position) {
		world.destroyBlock(position.getBlockPos(), true);
	}

	public static boolean isGlassBlock(Block block) {
		return block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass || block == Blocks.stained_glass_pane;
	}
}
