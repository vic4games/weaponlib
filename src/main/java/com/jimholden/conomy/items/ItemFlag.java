package com.jimholden.conomy.items;

import com.jimholden.conomy.entity.EntityRope;
import com.jimholden.conomy.entity.EntityTestVes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFlag extends ItemBase {

	public ItemFlag(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	/**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	
    	if(!worldIn.isRemote) {
    		worldIn.spawnEntity(new EntityTestVes(worldIn, pos.getX(), pos.getY()+1, pos.getZ()));
    	}
    	return EnumActionResult.SUCCESS;
    	/*
        Block block = worldIn.getBlockState(pos).getBlock();

        if (!(block instanceof BlockFence))
        {
            return EnumActionResult.PASS;
        }
        else
        {
            if (!worldIn.isRemote)
            {
                attachToFence(player, worldIn, pos);
            }

            return EnumActionResult.SUCCESS;
        }
        */
    }

    public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence)
    {
        EntityRope rope = EntityRope.createKnot(worldIn, fence, player);
        return true;
    }

}
