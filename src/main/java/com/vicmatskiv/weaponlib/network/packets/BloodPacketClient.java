package com.vicmatskiv.weaponlib.network.packets;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.lang3.RandomUtils;

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
import com.vicmatskiv.weaponlib.jim.util.RandomUtil;
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

public class BloodPacketClient implements CompatibleMessage {

	double x, y, z, velx, vely, velz;

	public BloodPacketClient() {}
	
	public BloodPacketClient(double x, double y, double z, double velX, double velY, double velZ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.velx = velX;
		this.vely = velY;
		this.velz = velZ;
	}
	

	public void fromBytes(ByteBuf buf) {
	
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.velx = buf.readDouble();
		this.vely = buf.readDouble();
		this.velz = buf.readDouble();

	
	}
	
	

	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeDouble(this.velx);
		buf.writeDouble(this.vely);
		buf.writeDouble(this.velz);
	}

	public static class BalancePacketHandler implements CompatibleMessageHandler<BloodPacketClient, CompatibleMessage> {
		
		private ModContext modContext;
		
		
		public BalancePacketHandler(ModContext context) {
			this.modContext = context;
		}
		

		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(BloodPacketClient m, CompatibleMessageContext ctx) {
			 if(!ctx.isServerSide()) {
		            compatibility.runInMainClientThread(() -> {
					
		            	
		            	double velX = m.velx;
		            	double velY = m.vely;
		            	double velZ = m.velz;
		            	
		            	double length = Math.sqrt(velX*velX + velY*velY + velZ*velZ);
		            	velX /= -length;
		            	velY /= -length;
		            	velZ /= -length;
		            	
		            	
		            	double scale = 0.2;
		            	double spreader = 0.05;
		            	
		            	RandomUtil util = new RandomUtil();
		            	
		            	for(int i = 0; i < 15; ++i) {
		            	///	System.out.println(m.x + " | " + m.y  + " | " + m.z);
		            		compatibility.addBloodParticle(modContext, m.x, m.y, m.z, velX*scale + util.getRandomWithNegatives(spreader), velY*scale + util.getRandomWithNegatives(spreader), velZ*scale + util.getRandomWithNegatives(spreader));
			            	
							
		            	}
		            	
				});
			}
			
			return null;
		}

	}

	
}
