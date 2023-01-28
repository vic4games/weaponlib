package com.jimholden.conomy.entity.data;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VecListSerializer {
	
	public ArrayList<Vec3d> vecList = new ArrayList<>();
	public ArrayList<BlockPos> blockPosList = new ArrayList<>();
	
	public VecListSerializer() {}
	
	public VecListSerializer(ArrayList<Vec3d> vecList, ArrayList<BlockPos> blockPosList) {
		this.vecList = vecList;
		this.blockPosList = blockPosList;
	}
	
	public void setVecList(ArrayList<Vec3d> vecList) {
		this.vecList = vecList;
	}
	
	public void setBlockPosList(ArrayList<BlockPos> blockPosList) {
		this.blockPosList = blockPosList;
	}
	

	
	public static final DataSerializer<VecListSerializer> SERIALIZER = new DataSerializer<VecListSerializer>() {

		@Override
		public void write(PacketBuffer buf, VecListSerializer value) {
			if(value == null){
				buf.writeInt(-1);
				return;
			}
			
			
			buf.writeInt(value.vecList.size());
			
			for(Vec3d vecTW : value.vecList) {
				buf.writeDouble(vecTW.x);
				buf.writeDouble(vecTW.y);
				buf.writeDouble(vecTW.z);
			}
			
			buf.writeInt(value.blockPosList.size());
			for(BlockPos bpTW : value.blockPosList) {
				buf.writeLong(bpTW.toLong());
			}
		}

		@Override
		public VecListSerializer read(PacketBuffer buf) throws IOException {
			
			int size = buf.readInt();
			ArrayList<Vec3d> vecListS = new ArrayList<>();
			for(int x = 0; x < size; ++x) {
				vecListS.add(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
			}
			
			int bpSize = buf.readInt();
			ArrayList<BlockPos> bpLS = new ArrayList<>();
			for(int r = 0; r < bpSize; ++r) {
				bpLS.add(BlockPos.fromLong(buf.readLong()));
			}
			
			
			return new VecListSerializer(vecListS, bpLS);
		}

		@Override
		public DataParameter<VecListSerializer> createKey(int id) {
			return new DataParameter<VecListSerializer>(id, this);
		}

		@Override
		public VecListSerializer copyValue(VecListSerializer value) {
			return value;
		}
		

	};
}
