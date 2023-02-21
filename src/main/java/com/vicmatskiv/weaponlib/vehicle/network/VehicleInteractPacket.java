package com.vicmatskiv.weaponlib.vehicle.network;

import java.io.IOException;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

public class VehicleInteractPacket implements CompatibleMessage {

	public boolean right;
	public int vehicleID;
	public int playerID;
	
	public VehicleInteractPacket() {}
	
	public VehicleInteractPacket(boolean right, int entityID, int playerID) {
		this.right = right;
		this.vehicleID = entityID;
		this.playerID = playerID;
	}
	

	public void fromBytes(ByteBuf buf) {
		this.right = buf.readBoolean();
		this.vehicleID = buf.readInt();
		this.playerID = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(right);
		buf.writeInt(this.vehicleID);
		buf.writeInt(this.playerID);
	}

	
}
