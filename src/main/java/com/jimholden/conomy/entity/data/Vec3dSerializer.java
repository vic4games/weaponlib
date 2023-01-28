package com.jimholden.conomy.entity.data;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Vec3dSerializer {
	
	public Vec3d vec;
	
	public Vec3dSerializer() {}
	
	public Vec3dSerializer(Vec3d vec) {
		this.vec = vec;
	}
	
	public void setVec(Vec3d vec) {
		this.vec = vec;
	}
	
	public Vec3d getVec() {
		return vec;
	}
	

	
	public static final DataSerializer<Vec3dSerializer> SERIALIZER = new DataSerializer<Vec3dSerializer>() {

		@Override
		public void write(PacketBuffer buf, Vec3dSerializer value) {
			if(value == null){
				buf.writeInt(-1);
				return;
			} else {
				buf.writeInt(0);
			}
			buf.writeDouble(value.getVec().x);
			buf.writeDouble(value.getVec().y);
			buf.writeDouble(value.getVec().z);
		}

		@Override
		public Vec3dSerializer read(PacketBuffer buf) throws IOException {
			int check = buf.readInt();
			if(check != 0) return new Vec3dSerializer(Vec3d.ZERO);
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			
			Vec3d newVec = new Vec3d(x, y, z);
			return new Vec3dSerializer(newVec);
		}

		@Override
		public DataParameter<Vec3dSerializer> createKey(int id) {
			return new DataParameter<Vec3dSerializer>(id, this);
		}

		@Override
		public Vec3dSerializer copyValue(Vec3dSerializer value) {
			return value;
		}
		

	};
}
