package com.vicmatskiv.weaponlib.vehicle.network;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class VehicleControlPacketHandler implements CompatibleMessageHandler<VehicleControlPacket, CompatibleMessage> {
	
	public static ModContext context;

	public VehicleControlPacketHandler(ModContext context) {
		this.context = context;
	}

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(VehicleControlPacket message, CompatibleMessageContext ctx) {
		if(ctx.isServerSide()) {
			ctx.runInMainThread(() -> {
				
				EntityPlayer player = ctx.getPlayer();
				VehicleDataContainer cont = message.serializer;
				
				
				
				//System.out.println("Ent ID: " + cont.entityID + " | " + player.world.getEntityByID(cont.entityID));
			
				/*
				for(Entity e : player.world.getLoadedEntityList()) {
					if(e instanceof EntityVehicle) {
						System.out.println("fuckin' vehicle bruh: " + e.getEntityId());
					}
				}*/
				EntityVehicle vehicle = (EntityVehicle) ctx.getPlayer().world.getEntityByID(cont.entityID);
				/*
				System.out.println(cont.synthAccelFor);
				System.out.println("what the fuck " + ctx.getPlayer().world.getEntityByID(cont.entityID));
				*/
				cont.vehicle = vehicle;
				
				if(vehicle == null) return;
				//System.out.println("fucking success");
				
				for(EntityPlayer p: ctx.getPlayer().world.playerEntities) {
					
					
					
					boolean b = vehicle.getPassengers().isEmpty() || vehicle.getPassengers().get(0) == p;
					if(!b) {
						
						context.getChannel().getChannel().sendTo(new VehicleClientPacket(cont), (EntityPlayerMP) p);
					}
				}
				
				
				
				
			});
		}
		
		return null;
	}

}
