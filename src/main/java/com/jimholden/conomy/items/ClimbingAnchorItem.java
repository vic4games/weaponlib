package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.entity.EntityRope;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClimbingAnchorItem extends Item implements IHasModel {

	public ClimbingAnchorItem(String name) {
		super();
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
		ModItems.ITEMS.add(this);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand) {
		// TODO Auto-generated method stub
		//System.out.println(world.isRemote);
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}
	
	/**
     * Called when a Block is right-clicked with this Item
     */
	
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	//System.out.println(worldIn.isRemote);
    	
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        ItemStack itemstack = player.getHeldItem(hand);
        
        if (!block.isPassable(worldIn, pos))
        {
        	
            return EnumActionResult.PASS;
        }
        else
        {
        	
        	
            if (!worldIn.isRemote)
            {
            	
            	
            	itemstack.shrink(1);
                placeAnchor(player, worldIn, pos.up());
                
            }

            return EnumActionResult.SUCCESS;
        }
    }

    public static void placeAnchor(EntityPlayer player, World worldIn, BlockPos anchorSpot)
    {
        EntityRope rope = EntityRope.createKnot(worldIn, anchorSpot, player);
    }
    
    @Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}

}
