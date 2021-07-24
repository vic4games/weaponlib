package com.vicmatskiv.weaponlib.vehicle.network;

import java.io.IOException;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import scala.reflect.internal.Trees.This;

public class VehicleControlPacket implements CompatibleMessage {

	public VehicleDataContainer serializer;

	public VehicleControlPacket() {}
	
	public VehicleControlPacket(VehicleDataContainer serializer) {
		this.serializer = serializer;
	}
	

	public void fromBytes(ByteBuf buf) {
		try {
			this.serializer = this.serializer.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void toBytes(ByteBuf buf) {
		
		
		this.serializer.write(buf, this.serializer);
	}

	
}
