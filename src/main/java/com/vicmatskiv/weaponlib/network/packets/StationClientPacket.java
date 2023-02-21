package com.vicmatskiv.weaponlib.network.packets;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
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
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;
import com.vicmatskiv.weaponlib.crafting.workbench.TileEntityWorkbench;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class StationClientPacket implements CompatibleMessage {

	// THIS ONLY EXISTS ON THE SERVER END, THIS
	// VARIABLE WILL BE NULL ON THE CLIENT END.
	// USE MINECRAFT.GETMINECRAFT() INSTEAD.
	public World world;

	public BlockPos pos;

	
	// THIS ONLY EXISTS ON THE CLIENT.
	public ByteBuf copiedBuf;

	public StationClientPacket() {}
	
	public StationClientPacket(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}


	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
		
		
		
		
		this.copiedBuf = buf.copy();
	
		
		
	}


	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		
		TileEntity te = world.getTileEntity(pos);
		if(te != null && te instanceof TileEntityStation) {
			TileEntityStation station = (TileEntityStation) te;
			station.writeBytesForClientSync(buf);
		}

	}

	public static class WorkshopClientPacketHandler implements CompatibleMessageHandler<StationClientPacket, CompatibleMessage> {
		
		private ModContext modContext;
		
		
		public WorkshopClientPacketHandler(ModContext context) {
			this.modContext = context;
		}
		

		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(StationClientPacket m, CompatibleMessageContext ctx) {
			 if(!ctx.isServerSide()) {
		            compatibility.runInMainClientThread(() -> {
					
		            	TileEntity te = Minecraft.getMinecraft().world.getTileEntity(m.pos);
		        		if(te != null && te instanceof TileEntityStation) {
		        			TileEntityStation station = (TileEntityStation) te;
		        			station.readBytesFromClientSync(m.copiedBuf);
		        		}
	
		            	
				});
			}
			
			return null;
		}

	}

	
}
