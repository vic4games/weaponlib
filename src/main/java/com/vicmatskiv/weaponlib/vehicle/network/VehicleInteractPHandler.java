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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class VehicleInteractPHandler implements CompatibleMessageHandler<VehicleInteractPacket, CompatibleMessage> {
	
	public static ModContext context;

	public VehicleInteractPHandler(ModContext context) {
		this.context = context;
	}

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(VehicleInteractPacket m, CompatibleMessageContext ctx) {
		if(ctx.isServerSide()) {
			ctx.runInMainThread(() -> {
				EntityVehicle vehicle = (EntityVehicle) ctx.getPlayer().world.getEntityByID(m.vehicleID);
				EntityPlayer player = (EntityPlayer) ctx.getPlayer().world.getEntityByID(m.playerID);
				
				if(m.right) {
					if(vehicle.canFitPassenger(player)) {
						
						player.startRiding(vehicle);
					}
				} else {
					
					vehicle.setDead();
					
					
				}
				
				
				
				
			});
		}
		
		return null;
	}

}
