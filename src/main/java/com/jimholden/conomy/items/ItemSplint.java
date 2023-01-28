package com.jimholden.conomy.items;

import com.jimholden.conomy.main.ModEventHandler;
import com.jimholden.conomy.medical.ConsciousCapability;
import com.jimholden.conomy.medical.ConsciousProvider;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSplint extends ItemBase {

	public ItemSplint(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		System.out.println(playerIn.getCapability(ConsciousProvider.CONSCIOUS, null).getLegHealth());
		if(playerIn.getCapability(ConsciousProvider.CONSCIOUS, null).hasSplint() || playerIn.getCapability(ConsciousProvider.CONSCIOUS, null).getLegHealth() == ConsciousCapability.MAX_LEG_HEALTH) return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
		
		
		if(!worldIn.isRemote) {
			
			playerIn.getCapability(ConsciousProvider.CONSCIOUS, null).setHasSplint(true);
			IAttributeInstance atty = playerIn.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			atty.removeModifier(ModEventHandler.BUSTED_LEG_MODIFIER);
			
			playerIn.getHeldItem(handIn).shrink(1);
			
		} else {
			
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

}
