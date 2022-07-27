package com.vicmatskiv.weaponlib.crafting.workbench;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.crafting.base.BlockStation;
import com.vicmatskiv.weaponlib.inventory.GuiHandler;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.Instance;

public class WorkbenchBlock extends BlockStation {

	
	private ModContext modContext;

	public WorkbenchBlock(ModContext context, String name, Material materialIn) {
		super(name, materialIn);
		this.modContext = context;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	
		
		playerIn.openGui(modContext.getMod(), GuiHandler.WORKBENCH_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		
		
		
	
		//playerIn.openGui(CommonModContext.getContext().getMod(), GuiHandler.WORKBENCH_GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityWorkbench();
	}
	


}
