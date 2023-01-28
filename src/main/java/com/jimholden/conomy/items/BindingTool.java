package com.jimholden.conomy.items;

import java.awt.Event;
import java.util.List;

import javax.annotation.Nullable;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.Block3D;
import com.jimholden.conomy.blocks.MinerBlock;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;
import com.jimholden.conomy.util.ModelGeometryTool;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BindingTool extends Item implements IHasModel {
	
	public BindingTool(String name)
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
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand) {

		if(!player.isSneaking()) return EnumActionResult.PASS;
		if(hand == EnumHand.OFF_HAND) return EnumActionResult.PASS;
		//if(world.isRemote) return EnumActionResult.PASS;
		ItemStack stack = player.getHeldItemMainhand();
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		
		if(player.isSneaking())
		{
			Block block = world.getBlockState(pos).getBlock();
			if(!(block instanceof MinerBlock)) return null;
			if (nbtTagCompound == null) {
		        nbtTagCompound = new NBTTagCompound();
		        stack.setTagCompound(nbtTagCompound);
		    }
			nbtTagCompound.setBoolean("Bound", true);
	        nbtTagCompound.setDouble("X", pos.getX());
	        nbtTagCompound.setDouble("Y", pos.getY());
	        nbtTagCompound.setDouble("Z", pos.getZ());
	        if(world.isRemote)
	        {
	        	player.sendMessage(new TextComponentString(TextFormatting.GOLD + ">> " + TextFormatting.DARK_GRAY + "Miner block bound to tool at " + TextFormatting.GRAY + pos.getX() + TextFormatting.DARK_GRAY + ", " + TextFormatting.GRAY + pos.getY() + TextFormatting.DARK_GRAY + ", " + TextFormatting.GRAY + pos.getZ() + TextFormatting.DARK_GRAY + ". Right click node to pair."));
	        }
	        
	        return EnumActionResult.SUCCESS;
			
		}
		
		
		
		// TODO Auto-generated method stub
		return EnumActionResult.SUCCESS;
	}
	
	public boolean isBound(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			return false;
		}
		if(compound.getBoolean("Bound"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int getBoundX(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			return 0;
		}
		if(!this.isBound(stack))
		{
			return 0;
		}
		else {
			return compound.getInteger("X");
		}
		
	}
	
	public int getBoundY(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			return 0;
		}
		if(!this.isBound(stack))
		{
			return 0;
		}
		else {
			return compound.getInteger("Y");
		}
		
	}
	
	public int getBoundZ(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) {
			return 0;
		}
		if(!this.isBound(stack))
		{
			return 0;
		}
		else {
			return compound.getInteger("Z");
		}
		
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
	      Vec3d look = playerIn.getLookVec();
	      IBlockState block = worldIn.getBlockState(new BlockPos(look.x, look.y, look.z));
	      playerIn.sendMessage(new TextComponentString(TextFormatting.GREEN + "br block bound at " + block));
	      
	      int BlockX = 0;
	      nbtTagCompound.setBoolean("Bound", true);
	      nbtTagCompound.setDouble("X", (int) playerIn.posX);
	      nbtTagCompound.setDouble("Y", (int)playerIn.posY);
	      nbtTagCompound.setDouble("Z", (int)playerIn.posZ);
	      playerIn.sendMessage(new TextComponentString(TextFormatting.GREEN + "Miner block bound at "));
	    }
	      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	  }
	  */
	

	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	// help
}