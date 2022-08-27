package com.vicmatskiv.weaponlib.crafting.workbench;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.crafting.base.BlockStation;
import com.vicmatskiv.weaponlib.inventory.GuiHandler;
import com.vicmatskiv.weaponlib.network.packets.WorkshopClientPacket;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Block class for the workbench block
 * 
 * @author Homer Riva-Cambrin, 2022
 */
public class WorkbenchBlock extends BlockStation {

	
	// In ticks
	public static final int WORKBENCH_WEAPON_CRAFTING_TIME = 1200;
	public static final int WORKBENCH_ATTACHMENT_CRAFTING_TIME = 400;
	public static final int WORKBENCH_DISMANTLING_TIME = 200;
	
	
	
	private ModContext modContext;

	public WorkbenchBlock(ModContext context, String name, Material materialIn) {
		super(name, materialIn);
		this.modContext = context;
	}
	
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(worldIn.isRemote) {
			super.onBlockHarvested(worldIn, pos, state, player);
			return;
		}
		
		TileEntityWorkbench workbench = (TileEntityWorkbench) worldIn.getTileEntity(pos);
		for(int i = 0; i < workbench.mainInventory.getSlots(); ++i) {
			ItemStack stack = workbench.mainInventory.getStackInSlot(i);
			
			worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
		}
		
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		playerIn.openGui(modContext.getMod(), GuiHandler.WORKBENCH_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		
		if(!worldIn.isRemote && hand == EnumHand.MAIN_HAND) {
			modContext.getChannel().getChannel().sendTo(new WorkshopClientPacket(pos, (TileEntityWorkbench) worldIn.getTileEntity(pos)), (EntityPlayerMP) playerIn);
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityWorkbench();
	}



	


}
