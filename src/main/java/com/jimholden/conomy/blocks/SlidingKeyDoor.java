package com.jimholden.conomy.blocks;

import com.jimholden.conomy.blocks.tileentity.TileEntityKeyDoor;
import com.jimholden.conomy.items.ItemAccessCard;
import com.jimholden.conomy.looting.keycards.IAccessCard;
import com.jimholden.conomy.util.VectorUtil;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SlidingKeyDoor extends KeyDoorBlock implements ITileEntityProvider, IAccessCard {

	public SlidingKeyDoor(String name, Material materialIn) {
		super(name, materialIn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		
		state = state.getActualState(source, pos);
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		boolean flag1 = ((Boolean)state.getValue(OPEN)).booleanValue();
		
		
			
			
			
			TileEntityKeyDoor tkd = null;
			if(state.getValue(HALF) == EnumDoorHalf.UPPER) {
				tkd = (TileEntityKeyDoor) source.getTileEntity(pos.down());
			} else {
				tkd = (TileEntityKeyDoor) source.getTileEntity(pos);
			}
			
			
			if(tkd == null || tkd.getTime() == 0.0) {
				return super.getBoundingBox(state, source, pos);
			}
			
			double d = tkd.getTime()/(double) 500.0;
			double g = 0.0;
			
			
			if(tkd.getDoorState()) {
				g = Math.max(-0.9, -1.0*d);
			} else {
				g = Math.min(0.0, 1.0*d-1);
			}
			
			switch (enumfacing)
	        {
	            case EAST:
	            default:
	                return EAST_AABB.offset(0.0, 0.0, g);
	            case SOUTH:
	                return SOUTH_AABB.offset(g, 0.0, 0.0);
	            case WEST:
	                return WEST_AABB.offset(0.0, 0.0, g);
	            case NORTH:
	                return NORTH_AABB.offset(g, 0.0, 0.0);
	        }
			/*
		} else {
			return super.getBoundingBox(state, source, pos);
		}*/
		
		
		
		
	
		
		
		
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		
		
		boolean flag = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		if(!flag) {
			
			return flag;
		}
		else {
			
			BlockPos adjPos = pos;
			if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
				adjPos = adjPos.down();
			}
			
			TileEntityKeyDoor tkd = (TileEntityKeyDoor) worldIn.getTileEntity(adjPos);
			
			tkd.setOpenTimer();
			
			return flag;
		}
	
				
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityKeyDoor();
	}

}
