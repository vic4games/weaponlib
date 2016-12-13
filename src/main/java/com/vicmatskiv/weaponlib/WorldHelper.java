package com.vicmatskiv.weaponlib;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class WorldHelper {

	public static Block getBlockAtPosition(World world, MovingObjectPosition position) {
		return world.getBlock(position.blockX, position.blockY, position.blockZ);
	}

	public static void destroyBlock(World world, MovingObjectPosition position) {
		world.func_147480_a(position.blockX, position.blockY, position.blockZ, true);
	}
	
	public static boolean isGlassBlock(Block block) {
		return block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass || block == Blocks.stained_glass_pane;
	}
}
