package com.vicmatskiv.weaponlib.electronics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.perspective.Perspective;
import com.vicmatskiv.weaponlib.perspective.WirelessCameraPerspective;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class PlayerTabletInstance extends PlayerItemInstance<TabletState> {
	
	private static final int SERIAL_VERSION = 1;
	
	private static final Logger logger = LogManager.getLogger(PlayerTabletInstance.class);

	static {
		TypeRegistry.getInstance().register(PlayerTabletInstance.class);
	}
	
	private int activeWatchIndex;

	public PlayerTabletInstance() {
		super();
	}

	public PlayerTabletInstance(int itemInventoryIndex, EntityLivingBase player, ItemStack itemStack) {
		super(itemInventoryIndex, player, itemStack);
	}

	public PlayerTabletInstance(int itemInventoryIndex, EntityLivingBase player) {
		super(itemInventoryIndex, player);
	}
	
	@Override
	public Class<? extends Perspective<?>> getRequiredPerspectiveType() {
	    return WirelessCameraPerspective.class;
	}
	
	@Override
	public void serialize(ByteBuf buf) {
	    super.serialize(buf);
	    buf.writeInt(activeWatchIndex);
	}
	
	public void setActiveWatchIndex(int activeWatchIndex) {
	    if(this.activeWatchIndex != activeWatchIndex) {
	        logger.debug("Changing active watch index to {}", activeWatchIndex);
	        this.activeWatchIndex = activeWatchIndex;
	        markDirty();
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

    public void nextActiveWatchIndex() {
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker(player);
        if(tracker != null) {
            if(activeWatchIndex >= tracker.getTrackableEntitites().size() - 1) {
                setActiveWatchIndex(0);
            } else {
                setActiveWatchIndex(activeWatchIndex + 1);
            }
        }
    }
    

    public void previousActiveWatchIndex() {
        PlayerEntityTracker tracker = PlayerEntityTracker.getTracker(player);
        if(tracker != null) {
            if(activeWatchIndex == 0) {
                setActiveWatchIndex(tracker.getTrackableEntitites().size() - 1);
            } else {
                setActiveWatchIndex(activeWatchIndex - 1);
            }
        }
    }

    
    @Override
    public String toString() {
        return "Tablet [" + getUuid() + "]";
    }

}
