package com.vicmatskiv.weaponlib.network.packets;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.IOException;

import com.vicmatskiv.weaponlib.ClientEventHandler;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
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

public class BulletShellClient implements CompatibleMessage {

	public Vec3d position;
	public int shooter;
	public Vec3d velocity;
	public Shell.Type type;

	public BulletShellClient() {}
	
	public BulletShellClient(int shooterID, Shell.Type type, Vec3d pos, Vec3d velocity) {
		this.shooter = shooterID;
		this.type = type;
		this.position = pos;
		this.velocity = velocity;
	}
	

	public void fromBytes(ByteBuf buf) {
		this.shooter = buf.readInt();
		this.position = NetworkUtil.readVec3d(buf);
		this.velocity = NetworkUtil.readVec3d(buf);
		this.type = Shell.Type.valueOf(ByteBufUtils.readUTF8String(buf));
	}
	
	

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.shooter);
		NetworkUtil.writeVec3d(buf, position);
		NetworkUtil.writeVec3d(buf, velocity);
		ByteBufUtils.writeUTF8String(buf, type.toString());
	}

	public static class GunFXPacketHandler implements CompatibleMessageHandler<BulletShellClient, CompatibleMessage> {
		
		

		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(BulletShellClient m, CompatibleMessageContext ctx) {
			 if(!ctx.isServerSide()) {
		            compatibility.runInMainClientThread(() -> {
					
		            	if(Minecraft.getMinecraft().player.getEntityId() != m.shooter) {
		            		Shell shell = new Shell(m.type, m.position, new Vec3d(-90, 0, 90), m.velocity);
			            	CompatibleClientEventHandler.SHELL_MANAGER.enqueueShell(shell);
		            	}
		            	
					
				});
			}
			
			return null;
		}

	}

	
}
