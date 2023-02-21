package com.vicmatskiv.weaponlib.network.advanced;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.network.BalancePackClient;
import com.vicmatskiv.weaponlib.network.advanced.data.DataType;
import com.vicmatskiv.weaponlib.network.advanced.data.DataTypes;
import com.vicmatskiv.weaponlib.network.advanced.data.PacketSerializer;

import io.netty.buffer.ByteBuf;

public class SimplePacket implements CompatibleMessage {
	
	private ArrayList<PacketSerializer<?>> serializers = new ArrayList<>();
	
	public SimplePacket() {
	
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	
		
		for(Field f : getClass().getFields()) {
			if(f.getType() == PacketSerializer.class) {
				try {
					PacketSerializer<?> serializer = (PacketSerializer<?>) f.get(this);
					serializer.read(buf);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/*
		for(PacketSerializer<?> s : serializers) {
			s.read(buf);
		}	*/
	}

	@Override
	public void toBytes(ByteBuf buf) {
		for(Field f : getClass().getFields()) {
			if(f.getType() == PacketSerializer.class) {
				try {
					PacketSerializer<?> serializer = (PacketSerializer<?>) f.get(this);
					serializer.write(buf);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/*
		for(PacketSerializer<?> s : serializers) {
			s.write(buf);
		}	*/
	}
	
	
	
	

	
	

	

}
