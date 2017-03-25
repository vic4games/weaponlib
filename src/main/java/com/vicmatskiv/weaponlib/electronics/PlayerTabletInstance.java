package com.vicmatskiv.weaponlib.electronics;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.ExtendedPlayerProperties;
import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.perspective.Perspective;
import com.vicmatskiv.weaponlib.perspective.RemoteFirstPersonPerspective;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerTabletInstance extends PlayerItemInstance<TabletState> {
	
	private static final int SERIAL_VERSION = 1;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(PlayerTabletInstance.class);

	static {
		TypeRegistry.getInstance().register(PlayerTabletInstance.class);
	}
	
	private int activeWatchIndex;

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
	public void serialize(ByteBuf buf) {
	    super.serialize(buf);
	    buf.writeInt(activeWatchIndex);
	}
	
	public void setActiveWatchIndex(int activeWatchIndex) {
	    if(this.activeWatchIndex != activeWatchIndex) {
	        this.activeWatchIndex = activeWatchIndex;
	        updateId++;
	    }
    }
	
	public int getActiveWatchIndex() {
        return activeWatchIndex;
    }
	
	@Override
	public void init(ByteBuf buf) {
	    super.init(buf);
	    activeWatchIndex = buf.readInt();
	}
	
	@Override
	protected int getSerialVersion() {
		return SERIAL_VERSION;
	}

	@Override
	public String toString() {
		return "Tablet [" + getUuid() + "]";
	}

    public void nextActiveWatchIndex() {
        ExtendedPlayerProperties properties = ExtendedPlayerProperties.getProperties(player);
        if(properties != null) {
            if(activeWatchIndex >= properties.getTrackableEntitites().size() - 1) {
                setActiveWatchIndex(0);
            } else {
                setActiveWatchIndex(activeWatchIndex + 1);
            }
        }
    }

}
