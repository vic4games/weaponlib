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

public class WorkshopClientPacket implements CompatibleMessage {

	public int opcode;
	
	public BlockPos pos;
	public int craftingTimer, craftingDuration;
	public String craftingTargetName;
	
	
	public boolean hasCrafting, hasDismantling;
	
	public int[] dismantleStatus;
	public int[] dismantleLengths;

	public WorkshopClientPacket() {}
	
	public WorkshopClientPacket(BlockPos pos, String targetName, int timer, int length) {
		this.opcode = WorkbenchPacket.CRAFT;
		this.pos = pos;
		this.craftingTargetName = targetName;
		this.craftingTimer = timer;
		this.craftingDuration = length;
	}
	
	public WorkshopClientPacket(BlockPos pos, int[] status, int[] lengths) {
		this.opcode = WorkbenchPacket.DISMANTLE;
		this.pos = pos;
		this.dismantleStatus = status;
		this.dismantleLengths = lengths;
	}
	
	public WorkshopClientPacket(BlockPos pos, TileEntityWorkbench workbench) {
		this.opcode = WorkbenchPacket.UPDATE;
		this.pos = pos;
		
		this.craftingTimer = workbench.craftingTimer;
		this.craftingDuration = workbench.craftingDuration;
	
		this.dismantleStatus = workbench.dismantleStatus;
		this.dismantleLengths = workbench.dismantleDuration;
		
		this.hasCrafting = craftingTimer != -1;
		this.hasDismantling = arrayFilledWithNull(workbench.dismantleStatus);
		
		this.craftingTargetName = workbench.craftingTarget != null ? workbench.craftingTarget.getItem().getUnlocalizedName() : "";
	}
	

	public void fromBytes(ByteBuf buf) {
		this.opcode = buf.readInt();
		
	
		
		if(this.opcode == WorkbenchPacket.UPDATE) {
			this.hasCrafting = buf.readBoolean();
			this.hasDismantling = buf.readBoolean();
		}
		
		this.pos = BlockPos.fromLong(buf.readLong());
		
		if(this.opcode == WorkbenchPacket.CRAFT || (this.opcode == WorkbenchPacket.UPDATE && hasCrafting)) {
			this.craftingTimer = buf.readInt();
			this.craftingDuration = buf.readInt();
			this.craftingTargetName = ByteBufUtils.readUTF8String(buf);

		} else if(this.opcode == WorkbenchPacket.DISMANTLE || (this.opcode == WorkbenchPacket.UPDATE && hasDismantling)) {
		
			this.dismantleStatus = new int[] {
					buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()
			};
			
			this.dismantleLengths = new int[] {
					buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()
			};
		}
		
	}
	
	
	public boolean arrayFilledWithNull(int[] objArray) {
		for(int i = 0; i < objArray.length; i++) {
			if(objArray[i] != -1) return false;
		}
		return true;
	}
	

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.opcode);
		
		if(this.opcode == WorkbenchPacket.UPDATE) {
			buf.writeBoolean(hasCrafting);
			buf.writeBoolean(hasDismantling);
		}
		
		buf.writeLong(this.pos.toLong());
		
		if(this.opcode == WorkbenchPacket.CRAFT || (this.opcode == WorkbenchPacket.UPDATE && hasCrafting)) {
			
			buf.writeInt(this.craftingTimer);
			buf.writeInt(this.craftingDuration);
			ByteBufUtils.writeUTF8String(buf, this.craftingTargetName);
		} else if(this.opcode == WorkbenchPacket.DISMANTLE || (this.opcode == WorkbenchPacket.UPDATE && hasDismantling)) {
			for(int i : this.dismantleStatus) buf.writeInt(i);
			for(int i : this.dismantleLengths) buf.writeInt(i);
		}
		
		
	}

	public static class WorkshopClientPacketHandler implements CompatibleMessageHandler<WorkshopClientPacket, CompatibleMessage> {
		
		private ModContext modContext;
		
		
		public WorkshopClientPacketHandler(ModContext context) {
			this.modContext = context;
		}
		

		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(WorkshopClientPacket m, CompatibleMessageContext ctx) {
			 if(!ctx.isServerSide()) {
		            compatibility.runInMainClientThread(() -> {
					
		            	
		            
		            	World world = Minecraft.getMinecraft().player.world;
		            	TileEntity tileEntity = world.getTileEntity(m.pos);
		            	if(tileEntity instanceof TileEntityWorkbench) {
		            		TileEntityWorkbench teWorkbench = (TileEntityWorkbench) tileEntity;
		            		
		            		if(m.opcode == WorkbenchPacket.CRAFT || (m.opcode == WorkbenchPacket.UPDATE && m.hasCrafting)) {
		            			teWorkbench.setTimer(m.craftingTimer, m.craftingDuration);
		            			teWorkbench.craftingTargetName = m.craftingTargetName;
		            		} else if(m.opcode == WorkbenchPacket.DISMANTLE || (m.opcode == WorkbenchPacket.UPDATE && m.hasDismantling )) {
		            			teWorkbench.setDismantling(m.dismantleStatus, m.dismantleLengths);
		            		}
		            		
		            		teWorkbench.pushInventoryRefresh = true;
		            		
		            	}
		            	
		            	
		            	
				});
			}
			
			return null;
		}

	}

	
}
