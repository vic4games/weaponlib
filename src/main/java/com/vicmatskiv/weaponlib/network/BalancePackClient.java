package com.vicmatskiv.weaponlib.network;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.gzip.GzipUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vicmatskiv.weaponlib.ClientEventHandler;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.config.BalancePackManager;
import com.vicmatskiv.weaponlib.config.BalancePackManager.BalancePack;
import com.vicmatskiv.weaponlib.network.CompressionUtil;
import com.vicmatskiv.weaponlib.network.NetworkUtil;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleClientPacket;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleDataContainer;
import com.vicmatskiv.weaponlib.vehicle.network.VehiclePacketLatencyTracker;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class BalancePackClient implements CompatibleMessage {

	BalancePack pack;
	String test;

	public BalancePackClient() {}
	
	public BalancePackClient(BalancePack pack) {
		this.pack = pack;
	}
	

	public void fromBytes(ByteBuf buf) {
	
		if(!buf.readBoolean()) return;
		
		int length = buf.readInt();
		
		byte[] bytes = new byte[length];
		for(int i = 0; i < bytes.length; ++i) {
			bytes[i] = buf.readByte();
		}
		String decompressed = CompressionUtil.decompressString(bytes);
		JsonObject obj = new GsonBuilder().create().fromJson(decompressed, JsonObject.class);
		this.pack = BalancePack.fromJSONObject(obj);

	
	}
	
	

	public void toBytes(ByteBuf buf) {
		if(pack == null) {
			buf.writeBoolean(false);
			return;
		}
		buf.writeBoolean(true);
		byte[] bytes = CompressionUtil.compressString(this.pack.toJSONObject().toString());
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}

	public static class BalancePacketHandler implements CompatibleMessageHandler<BalancePackClient, CompatibleMessage> {
		
		

		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(BalancePackClient m, CompatibleMessageContext ctx) {
			 if(!ctx.isServerSide()) {
		            compatibility.runInMainClientThread(() -> {
					
		            	BalancePackManager.setCurrentBalancePack(m.pack);
		            	
					
				});
			}
			
			return null;
		}

	}

	
}
