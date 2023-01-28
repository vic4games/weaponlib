package com.jimholden.conomy.blocks;

import java.time.Duration;
import java.util.Random;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityLootingBlock;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.items.MemoryItem;
import com.jimholden.conomy.util.BoxUtil;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.LootItemPacket;
import com.jimholden.conomy.util.packets.PowerSurveyPacket;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.common.property.PropertyFloat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



public class LootingBlockBase extends Block3D implements ITileEntityProvider {
	
	// the bounding box
	private AxisAlignedBB BOUND = null;
	
	// how long it takes to loot the box
	public float lootingTime;
	
	// creates the looting bounding box
	public LootBlockBoundType boundType;
	
	// cooldown duration
	public Duration cooldownDuration;
	
	// is a single slotter?
	public boolean isSingle = false;
	
	// CLID
	public int clid;
	

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	
	
	public LootingBlockBase(String name, Material material, float lootingTime, LootBlockBoundType boundType, Duration dur, boolean isSingle, int cid) {
		super(name, material);
		setSoundType(SoundType.METAL);
		setHardness(1.0F);
		fullBlock = false;
		
		this.lootingTime = lootingTime;

		this.cooldownDuration = dur;
		this.boundType = boundType;
		this.isSingle = isSingle;
		
		this.clid = cid;
		setCreativeTab(Main.LOOTABLEBLOCKSTAB);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		// TODO Auto-generated constructor stub
	}
	
	
	/*
	 * GETTERS
	 */
	
	public Duration getCooldownDuration() {
		return cooldownDuration;
	}
	
	public LootBlockBoundType getBoundType() {
		return this.boundType;
	}
	
	public float getLootingTime() {
		return this.lootingTime;
	}
	
	public boolean hasOnlySingleSlot() {
		return this.isSingle;
	}
	
	public int getCLID() {
		return this.clid;
	}
	

	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		// TODO Auto-generated method stub
		return Item.getItemFromBlock(ModBlocks.ATM);
	}
	
	@Override
	public boolean causesSuffocation(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		return super.getCollisionBoundingBox(blockState, worldIn, pos);
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing face = state.getValue(FACING);
		
		if(this.boundType == LootBlockBoundType.WALL) {
			return BoxUtil.rotate(new AxisAlignedBB(0.15, 0.35, 0.195, 0.875, 0.8, 0.0), face);
		}
		if(this.boundType == LootBlockBoundType.FLATTENEDRECT) {
			return BoxUtil.rotate(new AxisAlignedBB(0.0, 0.0, 0, 1, 0.5, 1), face);
		}
		if(this.boundType == LootBlockBoundType.BOX) {
			return BoxUtil.rotate(new AxisAlignedBB(0.02, 0.0, 0.07, 0.98, 0.6, 0.9), face);
		}
		if(this.boundType == LootBlockBoundType.DOUBLEHIGH) {
			return BoxUtil.rotate(new AxisAlignedBB(0.02, 0.0, 0.07, 0.98, 2.0, 0.9), face);
		}
		if(this.boundType == LootBlockBoundType.FLAT) {
			return BoxUtil.rotate(new AxisAlignedBB(0.02, 0.0, 0.07, 0.98, 0.1, 0.9), face);
		}
		if(this.boundType == LootBlockBoundType.TALLRECT) {
			return BoxUtil.rotate(new AxisAlignedBB(0.02, 0.0, 0.07, 0.98, 0.1, 0.9), face);
		}
		if(this.boundType == LootBlockBoundType.DOUBLEWIDEHIGH) {
			return BoxUtil.rotate(new AxisAlignedBB(-0.5, 0.0, 0.0, 1.5, 2.0, 1), face);
		}
		if(this.boundType == LootBlockBoundType.DOUBLEWIDE) {
			return BoxUtil.rotate(new AxisAlignedBB(-0.5, 0.0, 0, 1.5, 1, 1), face);
		}
		return BOUND;
		
		
		
		
		/*
		if((face == EnumFacing.SOUTH) || (face == EnumFacing.NORTH))
        {
        	BOUND = new AxisAlignedBB(0.375, 0, 0.575, 0.625, 0.5, 0.825);
        }
        if((face == EnumFacing.EAST) || (face == EnumFacing.WEST))
        {
        	BOUND = new AxisAlignedBB(0.575, 0, 0.375, 0.825, 0.51875, 0.625);
        }*/
	}
	
	@SideOnly(Side.CLIENT)
	public void playOpenSound(World worldIn) {
		if(worldIn.isRemote) {
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.OPEN_LOOTBOX, 1.0F));
		}
	}
	
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		if(playerIn.getHeldItem(hand).getItem() instanceof MemoryItem)
		{
			playerIn.openGui(Main.instance, Reference.GUI_LOOTINGBLOCK, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		else {
			if(hasOnlySingleSlot()) {
				playerIn.openGui(Main.instance, Reference.GUI_LOOTSS, worldIn, pos.getX(), pos.getY(), pos.getZ());
			} else {
				playerIn.openGui(Main.instance, Reference.GUI_LOOTINGBLOCKPLAYER, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
			
			playOpenSound(worldIn);

		}
		
		return true;
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}
	
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) 
	{
		if (!worldIn.isRemote) 
        {
            IBlockState north = worldIn.getBlockState(pos.north());
            IBlockState south = worldIn.getBlockState(pos.south());
            IBlockState west = worldIn.getBlockState(pos.west());
            IBlockState east = worldIn.getBlockState(pos.east());
            EnumFacing face = (EnumFacing)state.getValue(FACING);

            if (face == EnumFacing.NORTH && north.isFullBlock() && !south.isFullBlock()) face = EnumFacing.SOUTH;
            else if (face == EnumFacing.SOUTH && south.isFullBlock() && !north.isFullBlock()) face = EnumFacing.NORTH;
            else if (face == EnumFacing.WEST && west.isFullBlock() && !east.isFullBlock()) face = EnumFacing.EAST;
            else if (face == EnumFacing.EAST && east.isFullBlock() && !west.isFullBlock()) face = EnumFacing.WEST;
            worldIn.setBlockState(pos, state.withProperty(FACING, face), 2);
            /*
            if((face == EnumFacing.SOUTH) || (face == EnumFacing.NORTH))
            {
            	BOUND = new AxisAlignedBB(0.375, 0, 0.175, 0.625, 0.71875, 0.825);
            }
            if((face == EnumFacing.EAST) || (face == EnumFacing.WEST))
            {
            	BOUND = new AxisAlignedBB(0.175, 0, 0.375, 0.825, 0.71875, 0.625);
            }
            */
            	
            	
        } 
	}
	
	public static void setState(boolean active, World worldIn, BlockPos pos) 
	{
		IBlockState state = worldIn.getBlockState(pos);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity != null) 
		{
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityLootingBlock();
	}
	
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		// TODO Auto-generated method stub
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		// TODO Auto-generated method stub
		worldIn.setBlockState(pos, this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		/*
		if(!worldIn.isRemote) {
			TileEntityLootingBlock tile = (TileEntityLootingBlock) worldIn.getTileEntity(pos);
			System.out.println("yo " + this.lootingTime);
			tile.lootingTime = this.lootingTime;
		}
		*/	
	}
	
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		// TODO Auto-generated method stub
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}
	
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		// TODO Auto-generated method stub
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		TileEntityLootingBlock tileentity = (TileEntityLootingBlock) worldIn.getTileEntity(pos);
		//InventoryHelper.dropInventoryItems(worldIn, pos, tileentity);
		super.breakBlock(worldIn, pos, state);
	}

	
	@Override
	public Block setLightLevel(float value) {
		// TODO Auto-generated method stub
		return super.setLightLevel(value);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing facing = EnumFacing.getFront(meta);
		if(facing.getAxis() == EnumFacing.Axis.Y) facing = EnumFacing.NORTH;
		return this.getDefaultState().withProperty(FACING, facing);
		//return this.getDefaultState().withProperty(BURNING_SIDES_COUNT, Integer.valueOf(meta));
	}
	
	
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!worldIn.isRemote) {
			TileEntityLootingBlock tile = (TileEntityLootingBlock) worldIn.getTileEntity(pos);
			tile.lootingTime = this.lootingTime;
		}
		super.updateTick(worldIn, pos, state, rand);
	}
	*/

}
