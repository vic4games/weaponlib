package com.vicmatskiv.weaponlib.vehicle.network;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class VehicleClientPacketHandler implements CompatibleMessageHandler<VehicleClientPacket, CompatibleMessage> {
	
	public static ModContext context;

	public VehicleClientPacketHandler(ModContext context) {
		this.context = context;
	}

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(VehicleClientPacket m, CompatibleMessageContext ctx) {
		 if(!ctx.isServerSide()) {
	            compatibility.runInMainClientThread(() -> {
				
	            	
	            	
				EntityPlayer player = compatibility.clientPlayer();
				VehicleDataContainer cont = m.serializer;
				
				EntityVehicle vehicle = (EntityVehicle) player.world.getEntityByID(cont.entityID);
				
				VehiclePacketLatencyTracker.push(vehicle);
				//System.out.println("There we go! " + vehicle);
				if(vehicle != null) {
					
					vehicle.smoothShell.upload(cont);
					
					//cont.updateVehicle(vehicle);
				}
				
				
				
			});
		}
		
		return null;
	}

}
