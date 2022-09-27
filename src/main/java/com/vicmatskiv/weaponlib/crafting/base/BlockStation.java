package com.vicmatskiv.weaponlib.crafting.base;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockPos;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;
import com.vicmatskiv.weaponlib.crafting.workbench.TileEntityWorkbench;
import com.vicmatskiv.weaponlib.tile.CustomTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockStation extends Block {
	
	
	protected ModContext modContext;
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	
	public BlockStation(ModContext context, String name, Material materialIn) {
		super(materialIn);
		this.modContext = context;
		setHardness(2.0f);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	public abstract TileEntity createTileEntity(World world, IBlockState state);
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(worldIn.isRemote) {
			super.onBlockHarvested(worldIn, pos, state, player);
			return;
		}
		
		TileEntityStation station = (TileEntityStation) worldIn.getTileEntity(pos);
		for(int i = 0; i < station.mainInventory.getSlots(); ++i) {
			ItemStack stack = station.mainInventory.getStackInSlot(i);
			worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
		}
		
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	
	
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileEntityStation entity = (TileEntityStation) compatibility.getTileEntity(worldIn, new CompatibleBlockPos(pos));
		if(entity != null) {
			int side = CompatibleMathHelper.floor_double(placer.rotationYaw/90f + 0.5) & 3;
			entity.setSide(side);
		}
	}
	

	
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        return i;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		EnumFacing enumfacing = placer.getHorizontalFacing();

        try
        {
            return super.getStateForPlacement(world, pos, enumfacing, hitX, hitY, hitZ, meta, placer, hand);
        }
        catch (IllegalArgumentException var11)
        {
            if (!world.isRemote)
            {
                
                if (placer instanceof EntityPlayer)
                {
                    placer.sendMessage(new TextComponentTranslation("Invalid damage property. Please pick in [0, 1, 2]", new Object[0]));
                }
            }

            return super.getStateForPlacement(world, pos, enumfacing, hitX, hitY, hitZ, meta, placer, hand);
        }
	}
	
	
	
	
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	

	

}
