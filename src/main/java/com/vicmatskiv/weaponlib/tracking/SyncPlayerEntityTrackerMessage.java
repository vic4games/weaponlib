package com.vicmatskiv.weaponlib.tracking;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class SyncPlayerEntityTrackerMessage implements CompatibleMessage {

	private Function<World, PlayerEntityTracker> playerEntityTracker;;

	public SyncPlayerEntityTrackerMessage() {}
	
	public SyncPlayerEntityTrackerMessage(PlayerEntityTracker playerEntityTracker) {
		this.playerEntityTracker = a -> playerEntityTracker;
	}

	public void fromBytes(ByteBuf buf) {
	    playerEntityTracker = w -> PlayerEntityTracker.fromBuf(buf, w);
	}

	public void toBytes(ByteBuf buf) {
	    playerEntityTracker.apply(null).serialize(buf); // TODO: refactor
	}

    public Function<World, PlayerEntityTracker> getTracker() {
        return playerEntityTracker;
    }

	
}
