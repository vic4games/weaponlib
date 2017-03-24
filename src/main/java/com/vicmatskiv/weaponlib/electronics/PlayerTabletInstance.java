package com.vicmatskiv.weaponlib.electronics;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.perspective.Perspective;
import com.vicmatskiv.weaponlib.perspective.RemoteFirstPersonPerspective;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerTabletInstance extends PlayerItemInstance<TabletState> {
	
	private static final int SERIAL_VERSION = 1;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(PlayerTabletInstance.class);

	static {
		TypeRegistry.getInstance().register(PlayerTabletInstance.class);
	}

	public PlayerTabletInstance() {
		super();
	}

	public PlayerTabletInstance(int itemInventoryIndex, EntityPlayer player, ItemStack itemStack) {
		super(itemInventoryIndex, player, itemStack);
	}

	public PlayerTabletInstance(int itemInventoryIndex, EntityPlayer player) {
		super(itemInventoryIndex, player);
	}
	
	@Override
	public Class<? extends Perspective<?>> getRequiredPerspectiveType() {
	    return RemoteFirstPersonPerspective.class;
	}
	
	@Override
	protected int getSerialVersion() {
		return SERIAL_VERSION;
	}

	@Override
	public String toString() {
		return "Tablet [" + getUuid() + "]";
	}

}
