package com.vicmatskiv.weaponlib.network.packets;

import com.vicmatskiv.weaponlib.HighIQSpawnEgg;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.SecondaryEntityRegistry;
import com.vicmatskiv.weaponlib.ai.EntityCustomMob;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.crafting.CraftingFileManager;
import com.vicmatskiv.weaponlib.network.advanced.SimplePacket;
import com.vicmatskiv.weaponlib.network.advanced.data.DataTypes;
import com.vicmatskiv.weaponlib.network.advanced.data.PacketSerializer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class HighIQPickupPacket extends SimplePacket {
	
	

	
	public PacketSerializer<Integer> playerID = new PacketSerializer<>(DataTypes.INTEGER);
	public PacketSerializer<Integer> entityID = new PacketSerializer<>(DataTypes.INTEGER);
	
	public HighIQPickupPacket() {
		super();
	}
	
	public HighIQPickupPacket(int playerID, int entityID) {
		super();
		
		this.playerID.setValue(playerID);
		this.entityID.setValue(entityID);
	}

	
	
	public static class SimplePacketHandler implements CompatibleMessageHandler<HighIQPickupPacket, CompatibleMessage> {

		private ModContext context;
		
		public SimplePacketHandler(ModContext context) {
			this.context = context;
		}
		
		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(HighIQPickupPacket compatibleMessage,
				CompatibleMessageContext ctx) {
			ctx.getPlayer().getServer().addScheduledTask(() -> {
				// Find the player we should send to
				EntityPlayerMP target = (EntityPlayerMP) ctx.getPlayer().getEntityWorld().getEntityByID(compatibleMessage.playerID.getValue());
				
				Entity e = target.world.getEntityByID(compatibleMessage.entityID.getValue());
				
				
		
				if(e == null || !(e instanceof EntityCustomMob)) return;
				
				
				EntityCustomMob ecm = (EntityCustomMob) e;
				HighIQSpawnEgg egg = (HighIQSpawnEgg) SecondaryEntityRegistry.pickupMap.get(ecm.getConfiguration().getPickupItemID());
				
				
				//System.out.println(((HighIQSpawnEgg) ((EntityCustomMob) e).getConfiguration().getPickupItem()).getEntitySpawnName());
			
			
				target.addItemStackToInventory(new ItemStack(egg));
				e.setDead();
				
			});
			return null;
		}
		
	}
	

}
