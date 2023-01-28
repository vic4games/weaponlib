package com.jimholden.conomy.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;

import ibxm.Player;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
public class OpenDimeBase extends Item implements IHasModel {
	
	public OpenDimeBase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
		ModItems.ITEMS.add(this);
	}
	
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		for(int x = 0; x < items.size(); x++) {
			if(items.get(x).getItem() instanceof OpenDimeBase) {
				NBTTagCompound compound = items.get(x).getTagCompound();
				compound = new NBTTagCompound();
				compound.setInteger("balance", 0);
				compound.setString("pkey", ("0x" + randomAlphaNumeric(15)));
				items.get(x).setTagCompound(compound);
			}
		}
		// TODO Auto-generated method stub
		super.getSubItems(tab, items);
	}
	
	public void updateCompound(ItemStack stack, NBTTagCompound comp) {
		if(comp == null) {
			comp = new NBTTagCompound();
			comp.setDouble("balance", 0.0);
			comp.setString("pkey", ("0x" + randomAlphaNumeric(15)));
			stack.setTagCompound(comp);
		}
		
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		NBTTagCompound compound = stack.getTagCompound();
		updateCompound(stack, compound);
		
		super.onCreated(stack, worldIn, playerIn);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(worldIn.isRemote) {
			
			BlockPos pT = new BlockPos(1619, 4, 26716);
			
			IBlockState iBS2 = worldIn.getBlockState(pT);
			
			List<AxisAlignedBB> t = worldIn.getCollisionBoxes(playerIn, playerIn.getEntityBoundingBox());
			System.out.println(t);
			
			 /*
			int radius = 1;
            for(double y = 0; y <= 20; y+=0.5) {
                double x = radius * Math.cos(y);
                double z = radius * Math.sin(y);
                worldIn.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, playerIn.posX + x,playerIn.posY + y*0.2F, playerIn.posZ+z, 0.0D, 0.0D, 0.0D);
        		
            }

            for(double y = 0; y <= 20; y+=0.5) {
                double x = radius * Math.cos(y);
                double z = radius * Math.sin(y);
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, playerIn.posX - x,playerIn.posY + y*0.2F, playerIn.posZ - z, 0.0D, 0.0D, 0.0D);
        		
            }
           
            playerIn.motionY += 1.0F; */
           
			/*
			float yaw = playerIn.rotationYaw;
			float pitch = playerIn.rotationPitch;
			float f = 1.0F;
			double motionX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
			double motionZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI) * f);
			double motionY = (double)(-MathHelper.sin((pitch) / 180.0F * (float)Math.PI) * f);
			playerIn.addVelocity(motionX, motionY, motionZ);
			//return par1ItemStack;
			playerIn.addVelocity(0.1F, 0.5F, 0.1F);*/
			
			Vec3d newCast = playerIn.getLookVec();
			Vec3d endCast = newCast.scale(10);
			
			Vec3d height = playerIn.getPositionVector().addVector(0.0, playerIn.eyeHeight, 0.0);
			System.out.println(playerIn.eyeHeight);
			newCast = newCast.add(height);
			endCast = endCast.add(height);
			
			RayTraceResult ray = worldIn.rayTraceBlocks(newCast, endCast);
			if(ray != null) {
				Vec3d v = ray.hitVec;
				
				double newX = Math.floor(v.x);
				double newY = Math.floor(v.y);
				double newZ = Math.floor(v.z);
				
				BlockPos bp = new BlockPos((int) newX, (int) newY, (int) newZ);
				
				int sFull = 0;
				
				ArrayList<IBlockState> surroundState = new ArrayList<>();
				surroundState.add(worldIn.getBlockState(bp.east()).getActualState(worldIn, bp.east()));
				surroundState.add(worldIn.getBlockState(bp.west()).getActualState(worldIn, bp.west()));
				surroundState.add(worldIn.getBlockState(bp.north()).getActualState(worldIn, bp.north()));
				surroundState.add(worldIn.getBlockState(bp.south()).getActualState(worldIn, bp.south()));
				
				int lHeight = 8;
				
				for(IBlockState iBS : surroundState) {
					if(iBS.getBlock() != Blocks.AIR && iBS.getBlock() != Blocks.SNOW_LAYER) {
						++sFull;
					}
					
					if(iBS.getBlock() == Blocks.SNOW_LAYER) {
						System.out.println("fucking hell " + iBS.getValue(BlockSnow.LAYERS));
						if(iBS.getValue(BlockSnow.LAYERS) < lHeight) {
							lHeight = iBS.getValue(BlockSnow.LAYERS);
						}
					}
					
					
				}
				
				
				int h = 0;
				
				if(lHeight == 8 & sFull >= 2) {
					h = 7;
				} else {
					h = Math.max(1, lHeight-1);
				}
				
				
				
				IBlockState bs = Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, h);
				
				
				worldIn.setBlockState(bp, bs, 2);
			}
			
			System.out.println("cast");
			
			
			
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	/*
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
	  {
	    ItemStack itemStackIn = playerIn.getHeldItem(hand);
	    NBTTagCompound nbtTagCompound = itemStackIn.getTagCompound();

	    if (playerIn.isSneaking()) { // shift pressed; save (or overwrite) current location
	      if (nbtTagCompound == null) {
	        nbtTagCompound = new NBTTagCompound();
	        itemStackIn.setTagCompound(nbtTagCompound);
	      }
	      nbtTagCompound.setBoolean("Bound", true);
	      nbtTagCompound.setDouble("X", (int) playerIn.posX);
	      nbtTagCompound.setDouble("Y", (int)playerIn.posY);
	      nbtTagCompound.setDouble("Z", (int)playerIn.posZ);
	    }
	      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	  }
	*/

	public String getKey(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		updateCompound(stack, compound);
		return compound.getString("pkey");
	}
	
	public void setBalance(double balance, ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		updateCompound(stack, compound);
		compound.setDouble("balance", balance);
	}
	
	public double getBalance(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		updateCompound(stack, compound);
		return compound.getDouble("balance");
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound compound = stack.getTagCompound();
		updateCompound(stack, compound);
		try {
			tooltip.add("Credits: " + getBalance(stack));
			tooltip.add("Key: " + getKey(stack));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	  
	


	

	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	// help
}
