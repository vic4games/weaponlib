package com.jimholden.conomy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.economy.Interchange;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.util.IHasModel;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LedgerBase extends Item implements IHasModel {
	
	public LedgerBase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
		
		this.addPropertyOverride(new ResourceLocation("powered"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
	        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
	        {
	            if (entityIn == null) {
	                return 0.0F;
	            }
	            
	            NBTTagCompound nbt = stack.getTagCompound();
	            if(nbt == null) {
	            	nbt = new NBTTagCompound();
	            	nbt.setInteger("balance", 0);
	            	nbt.setString("pkey", ("0x" + randomAlphaNumeric(15)));
	            	nbt.setBoolean("powered", false);
					stack.setTagCompound(nbt);
				}
	            float j = nbt.getBoolean("powered") ? 1.0F : 0.0F;
	            return j; 
	        }
		});
		

		
		
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
	
	public void updateNBT(ItemStack stack, NBTTagCompound compound) {
		
		if(compound == null) {
			compound = new NBTTagCompound();
			compound.setDouble("balance", 0.0);
			compound.setString("pkey", ("0x" + randomAlphaNumeric(15)));
			compound.setBoolean("powered", false);
			stack.setTagCompound(compound);
		}
	}
	
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItemMainhand();
		
		
		if(playerIn.isSneaking()) {
			NBTTagCompound compound = stack.getTagCompound();
			updateNBT(stack, compound);
			if(compound.getBoolean("powered")) {
				compound.setBoolean("powered", false);
			}
			else {
				compound.setBoolean("powered", true);
			}
			

		}
		else {
			Main.proxy.showLedgerGUI(playerIn);
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
	
	public void setBalance(double balance, ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		updateNBT(stack, compound);
		compound.setDouble("balance", Interchange.currencyRound(balance));
	}
	public void removeBalance(double balance, ItemStack stack) {
		setBalance(getBalance(stack)-balance, stack);
	}
	
	public void addBalance(double balance, ItemStack stack) {
		setBalance(getBalance(stack)+balance, stack);
	}
	
	public double getBalance(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		updateNBT(stack, compound);
		return compound.getDouble("balance");
	}
	
	public void setState(ItemStack stack, boolean b) {
		NBTTagCompound compound = stack.getTagCompound();
		updateNBT(stack, compound);
		compound.setBoolean("powered", b);
	}
	
	public void toggleState(ItemStack stack) {
		setState(stack, !getState(stack));
	}
	
	
	public boolean getState(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		updateNBT(stack, compound);
		return compound.getBoolean("powered");
	}
	
	public String getKey(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		updateNBT(stack, compound);
		return compound.getString("pkey");
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound compound = stack.getTagCompound();
		updateNBT(stack, compound);
		tooltip.add(ChatFormatting.ITALIC  + "White Flora Ledger Gen.4");
	///	tooltip.add("Credits: " + compound.getDouble("balance"));
	//	tooltip.add("Key: " + compound.getString("pkey"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	  
	


	

	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
		
	}
	
	// help
}