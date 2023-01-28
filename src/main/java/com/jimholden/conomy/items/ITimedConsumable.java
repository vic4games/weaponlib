package com.jimholden.conomy.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface ITimedConsumable {
	
	public int getDuration();
	public void onComplete(World worldIn, EntityPlayer playerIn, EnumHand handIn);

}
