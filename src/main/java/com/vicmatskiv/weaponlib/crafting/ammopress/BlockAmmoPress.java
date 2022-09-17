package com.vicmatskiv.weaponlib.crafting.ammopress;

import java.util.List;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.crafting.base.BlockStation;
import com.vicmatskiv.weaponlib.crafting.workbench.TileEntityWorkbench;
import com.vicmatskiv.weaponlib.inventory.GuiHandler;
import com.vicmatskiv.weaponlib.network.packets.StationClientPacket;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockAmmoPress extends BlockStation {

	public BlockAmmoPress(ModContext context, String name, Material materialIn) {
		super(context, name, materialIn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
		// TODO Auto-generated method stub
		//super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.2D, 0.9D));
		addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.8D, 0.8D));
		
		
		
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		playerIn.openGui(modContext.getMod(), GuiHandler.AMMOPRESS_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		
		if(hand == EnumHand.MAIN_HAND) {
			
			playerIn.swingArm(hand);
			
			if(!worldIn.isRemote) {
				modContext.getChannel().getChannel().sendTo(new StationClientPacket(worldIn, pos), (EntityPlayerMP) playerIn);
			}
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		// TODO Auto-generated method stub
		return new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityAmmoPress();
	}


}
