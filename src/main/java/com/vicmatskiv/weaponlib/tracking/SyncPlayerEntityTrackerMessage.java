package com.vicmatskiv.weaponlib.tracking;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.electronics.EntityWirelessCamera;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class SyncPlayerEntityTrackerMessage implements CompatibleMessage {

    private static final Logger logger = LogManager.getLogger(SyncPlayerEntityTrackerMessage.class);


	private Function<World, PlayerEntityTracker> playerEntityTracker;
	private String statusMessage;

	public SyncPlayerEntityTrackerMessage() {}

	public SyncPlayerEntityTrackerMessage(PlayerEntityTracker playerEntityTracker) {
	    this(playerEntityTracker, null);
	}

	public SyncPlayerEntityTrackerMessage(PlayerEntityTracker playerEntityTracker, String statusMessage) {
		this.playerEntityTracker = a -> playerEntityTracker;
		this.statusMessage = statusMessage;
	}

	public void fromBytes(ByteBuf buf) {
	    try {

	        int statusMessageBytesLength = buf.readInt();
	        if(statusMessageBytesLength > 0) {
	            byte bytes[] = new byte[statusMessageBytesLength];
	            buf.readBytes(bytes);
	            this.statusMessage = new String(bytes);
	        }
	        playerEntityTracker = w -> PlayerEntityTracker.fromBuf(buf, w);
	    } catch(Exception e) {
	        logger.error("Failed to deserialize tracker {}", e);
	    }
	}

	public void toBytes(ByteBuf buf) {

	    byte[] statusMessageBytes = statusMessage != null ? statusMessage.getBytes() : new byte[0];
	    buf.writeInt(statusMessageBytes.length);
	    if(statusMessageBytes.length > 0) {
	        buf.writeBytes(statusMessageBytes);
	    }
	    playerEntityTracker.apply(null).serialize(buf); // TODO: refactor

	}

    public Function<World, PlayerEntityTracker> getTracker() {
        return playerEntityTracker;
    }

    public String getStatusMessage() {
        return statusMessage;
    }


}
