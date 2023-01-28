package com.jimholden.conomy.blocks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.graph.ElementOrder.Type;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityMiner;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.items.BindingTool;
import com.jimholden.conomy.util.BoxUtil;
import com.jimholden.conomy.util.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class NetworkNodeBlock extends Block3D implements ITileEntityProvider {
	//OLD : protected static final AxisAlignedBB BOUND = new AxisAlignedBB(0.175, 0, 0.375, 0.825, 0.71875, 0.625);
     // newest protected static final AxisAlignedBB BOUND = new AxisAlignedBB(0.14, 0.033, 0.31, 0.86, 0.1445, 0.82);

	public ArrayList<TileEntityMiner> nodes = new ArrayList<TileEntityMiner>();
	private AxisAlignedBB BOUND = null;
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	//protected List<EventSeat> modelData = new ArrayList<EventSeat>();
	
	public NetworkNodeBlock(String name, Material material) {
		super(name, material);
		//this.fullBlock = false;
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		// TODO Auto-generated constructor stub
	}
	
	
	/*  FACING STUFF
	 *     Yes, I'm well aware it's a bit much...
	 *  
	 */
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		// TODO Auto-generated method stub
		EnumFacing face = state.getValue(FACING);
		//AxisAlignedBB defaultBox = new AxisAlignedBB(0.14, 0.033, 0.31, 0.86, 0.1445, 0.82);
		//return defaultBox;
		//BOUND = BoxUtil.rotate(defaultBox, face);
		//return BOUND;
		
		
		if(face == EnumFacing.NORTH)
        {
        	BOUND = new AxisAlignedBB(0.14, 0.033, 0.31, 0.86, 0.1445, 0.82);
        }
		if(face == EnumFacing.SOUTH)
		{
			BOUND = new AxisAlignedBB(0.14, 0.033, 0.31, 0.86, 0.1445, 0.82);
		}
	

        if((face == EnumFacing.EAST) || (face == EnumFacing.WEST))
        {
        	BOUND = new AxisAlignedBB(0.14, 0.033, 0.31, 0.86, 0.1445, 0.82);
        }
		return BOUND;
		
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
		
		/*
		if(!worldIn.isRemote)
		{
			ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(Main.instance, worldIn, ForgeChunkManager.Type.NORMAL);
			ForgeChunkManager.forceChunk(ticket, new ChunkPos(pos.getX()/16, pos.getZ()/16));
			System.out.println("forcing this fucking idiotic fucking chunk");
		}
		*/
        
		// TODO Auto-generated method stub
		worldIn.setBlockState(pos, this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
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
	public boolean isFullBlock(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	// END FACING
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		/*
		for(int x = 0; x < nodes.size(); x++)
		{
			System.out.println(nodes.get(x));
		}
		*/
		if(stack.getItem() instanceof BindingTool)
		{
			BindingTool tool = ((BindingTool) stack.getItem());
			if(tool.isBound(stack))
			{
				BlockPos aimPos = new BlockPos(tool.getBoundX(stack), tool.getBoundY(stack), tool.getBoundZ(stack));
				Block block = worldIn.getBlockState(aimPos).getBlock();
				TileEntity tile = worldIn.getTileEntity(aimPos);
				//System.out.println("Tile: " + tile + " | Pos: " + aimPos);
				
				if(!(tile instanceof TileEntityMiner)) return false;
	
				TileEntityMiner miner = (TileEntityMiner) tile;
				//System.out.println("Is an instance of miner.. and here..." + miner);
				if(!worldIn.isRemote) {
					//System.out.println("who's our miner friend: " + miner);
					/*
					this.nodes.add(miner);
					if(!nodes.isEmpty())
					{
						for(int x = 0; x < nodes.size(); x++)
						{
							System.out.println(nodes.get(x));
						}
					}
					*/
					TileEntityNode tileentity = (TileEntityNode) worldIn.getTileEntity(pos);
					/*
					File file = new File("networks.json");
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					tileentity.nodes.add(miner);
					System.out.print(tileentity.nodes);
					*/
					if(!tileentity.checkConnection(miner.getPos().getX(), miner.getPos().getY(), miner.getPos().getZ()))
					{
						System.out.println("conn not in list");
						tileentity.newConnection(miner.getPos().getX(), miner.getPos().getY(), miner.getPos().getZ());
						playerIn.sendMessage(new TextComponentString(TextFormatting.GOLD + ">> " + TextFormatting.DARK_GRAY + "Router has bound " + TextFormatting.GRAY + tool.getBoundX(stack) + TextFormatting.DARK_GRAY + ", "+ TextFormatting.GRAY + tool.getBoundY(stack) + TextFormatting.DARK_GRAY + ", "+ TextFormatting.GRAY + tool.getBoundZ(stack) + TextFormatting.DARK_GRAY + " to it's network."));
						
					}
					else
					{
						System.out.println("conn not in list3");
						playerIn.sendMessage(new TextComponentString(TextFormatting.GOLD + ">> " + TextFormatting.DARK_GRAY + "Machine already bound to network."));
						
					}
					
					
					
					/*
					tileentity.connX.add(miner.getPos().getX());
					tileentity.connY.add(miner.getPos().getY());
					tileentity.connZ.add(miner.getPos().getZ());
					*/
					
				}
				
				
				
		     }
		}
		else
		{
			if(!worldIn.isRemote)
			{
				TileEntityNode tilenode = (TileEntityNode) worldIn.getTileEntity(pos);
				playerIn.openGui(Main.instance, Reference.GUI_NODE, worldIn, pos.getX(), pos.getY(), pos.getZ());
				/*
				playerIn.sendMessage(new TextComponentString(TextFormatting.GRAY + "-----------------------------------"));
				playerIn.sendMessage(new TextComponentString(TextFormatting.GREEN + "NETWORK NODE " + TextFormatting.GRAY + " | " + TextFormatting.DARK_GRAY + "Total Power: " + TextFormatting.GOLD + tilenode.getPower() + "TH/s"));
				//playerIn.sendMessage(new TextComponentString(TextFormatting.RED + "Total Power: " + tilenode.getPower()));
				playerIn.sendMessage(new TextComponentString(TextFormatting.GRAY + "-----------------------------------"));
				playerIn.sendMessage(new TextComponentString(TextFormatting.RED + "Connected Nodes:"));
				for(int x = 0; x < tilenode.connX.size(); x++)
				{
					int posX = tilenode.getConnectedBlock(x).getX();
					int posY = tilenode.getConnectedBlock(x).getY();
					int posZ = tilenode.getConnectedBlock(x).getZ();
					playerIn.sendMessage(new TextComponentString(TextFormatting.RED + "MINER | X: " + TextFormatting.GRAY + posX + TextFormatting.RED + " | Y: " + TextFormatting.GRAY + posY + TextFormatting.RED + " | Z: " + TextFormatting.GRAY + posZ));
				}
				playerIn.sendMessage(new TextComponentString(TextFormatting.GRAY + "-----------------------------------"));
				*/
			}
			
			
			
		}
		
		//playerIn.openGui(Main.instance, Reference.GUI_ATM, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		// TODO Auto-generated method stub
		return BOUND;
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		/* this method runs after destryed lmao
		System.out.println("Runs..?");
		if(!worldIn.isRemote)
        {
			System.out.println("me too :)");
            TileEntityNode tile = (TileEntityNode)worldIn.getTileEntity(pos);
            System.out.println("Here is tile: " + tile);
            if(tile != null)
            {
            	System.out.println("I was destroyed.");
            	((TileEntityNode) tile).unforceChunkLoad();
            }
        }
        */
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityNode();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		System.out.println("Runs..?");
		if(!worldIn.isRemote)
        {
			System.out.println("me too :)");
            TileEntityNode tile = (TileEntityNode)worldIn.getTileEntity(pos);
            System.out.println("Here is tile: " + tile);
            if(tile != null)
            {
            	System.out.println("I was destroyed.");
            	((TileEntityNode) tile).unforceChunkLoad();
            }
        }
		super.breakBlock(worldIn, pos, state);
	}
	
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
