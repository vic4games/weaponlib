package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.UniversalObject;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.ManagedStateContainer;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerContext extends UniversalObject {

	private EntityPlayer player;
	private ManagedStateContainer<ManagedState> stateContainer;

	public EntityPlayer getPlayer() {
		return player;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	
	public ManagedStateContainer<ManagedState> getStateContainer() {
		return stateContainer;
	}

	public void setManagedStateContainer(ManagedStateContainer<ManagedState> stateContainer) {
		this.stateContainer = stateContainer;
	}
}
