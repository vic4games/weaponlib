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
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class OpenDoorPacket implements CompatibleMessage {

	BlockPos pos;

	public OpenDoorPacket() {}
	
	public OpenDoorPacket(BlockPos pos) {
		this.pos = pos;
	}
	

	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
	}
	
	

	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.pos.toLong());
	}

	public static class OpenDoorPacketHandler implements CompatibleMessageHandler<OpenDoorPacket, CompatibleMessage> {
		
		private ModContext modContext;
		
		
		public OpenDoorPacketHandler(ModContext context) {
			this.modContext = context;
		}
		

		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(OpenDoorPacket m, CompatibleMessageContext ctx) {
			if(ctx.isServerSide()) {
	            ctx.runInMainThread(() -> {
					
	            	
	            	
	            	EntityPlayer player = ctx.getPlayer();
	            	
	            	IBlockState state = player.world.getBlockState(m.pos);
	            	BlockDoor door = (BlockDoor) state.getBlock();
	 				door.onBlockActivated(player.world, m.pos, state, player, EnumHand.MAIN_HAND, EnumFacing.NORTH, (float) m.pos.getX(), (float) m.pos.getY(), (float) m.pos.getZ());
	 				
		            
		            	
				});
			}
			
			return null;
		}

	}

	
}
