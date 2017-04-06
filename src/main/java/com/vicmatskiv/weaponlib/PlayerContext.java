package com.vicmatskiv.weaponlib;

import net.minecraft.entity.player.EntityPlayer;

public interface PlayerContext {

	public EntityPlayer getPlayer();
	
	public void setPlayer(EntityPlayer player);
}
