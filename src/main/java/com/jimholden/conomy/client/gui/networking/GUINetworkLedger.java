package com.jimholden.conomy.client.gui.networking;

import java.util.ArrayList;
import java.util.List;

import com.jimholden.conomy.client.gui.engine.GUINetworkHandler;
import com.jimholden.conomy.items.LedgerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.AxisAlignedBB;

public class GUINetworkLedger extends GUINetworkHandler {

	public static final int UPDATE_INFO = 0x01B;
	public static final int TOGGLE_STATE = 0x02B;
	public static final int SEND_TRANSACTION = 0x03B;

	@Override
	public void runServer(NBTTagCompound nbt) {
		int op = getOpCode(nbt);
		EntityPlayerMP player = getEntityPlayer(nbt);
		ItemStack ledger = player.getHeldItemMainhand();
		LedgerBase playerLedger = (LedgerBase) ledger.getItem();
		switch (op) {
		case UPDATE_INFO:

			double credits = playerLedger.getBalance(ledger);
			NBTTagCompound response = writePacket(UPDATE_INFO);
			response.setString("key", playerLedger.getKey(ledger));
			response.setDouble("credits", credits);
			response.setBoolean("state", playerLedger.getState(ledger));
			
			
			NBTTagList nearby = new NBTTagList();
			List<EntityPlayer> playerList = player.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(player.posX-10, player.posY-10, player.posZ-10, player.posX+10, player.posY+10, player.posZ+10));
			if(!playerList.isEmpty()) {
				for(EntityPlayer p : playerList) {
					for(int x = 0; x < p.inventory.getSizeInventory(); ++x) {
						ItemStack stack = p.inventory.getStackInSlot(x);
						if(stack.getItem() instanceof LedgerBase) {
							
							LedgerBase base = (LedgerBase) stack.getItem();
							String key = base.getKey(stack);
							//System.out.println(key + " | " + playerLedger.getKey(ledger) + " | " + (!playerLedger.getKey(ledger).equals(key)) + " | " + base.getState(stack));
							if(!playerLedger.getKey(ledger).equals(key) && base.getState(stack)) {
								nearby.appendTag(new NBTTagString(base.getKey(stack)));
							}
							
						}
					}
				}
			}
			
			
			response.setTag("nearby", nearby);
			sendPacket(player, response);
			
			
			break;
		case TOGGLE_STATE: 
			playerLedger.setState(ledger, !playerLedger.getState(ledger));
			runServer(writeRecursivePacket(UPDATE_INFO, nbt.getInteger("playerID")));
			
			
			
			
			break;
		case SEND_TRANSACTION:
			String otherLedger = nbt.getString("recv");
			double transferAmt = nbt.getDouble("amount");
			List<EntityPlayer> check = player.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(player.posX-10, player.posY-10, player.posZ-10, player.posX+10, player.posY+10, player.posZ+10));
			if(!check.isEmpty()) {
				for(EntityPlayer p : check) {
					for(int x = 0; x < p.inventory.getSizeInventory(); ++x) {
						ItemStack stack = p.inventory.getStackInSlot(x);
						if(stack.getItem() instanceof LedgerBase) {
							
							LedgerBase base = (LedgerBase) stack.getItem();
							String key = base.getKey(stack);
							if(key.equals(otherLedger)) {
								
								base.addBalance(transferAmt, stack);
								playerLedger.removeBalance(transferAmt, ledger);
								
								runServer(writeRecursivePacket(UPDATE_INFO, nbt.getInteger("playerID")));
								
								NBTTagCompound tSuccess = writePacket(SEND_TRANSACTION);
								tSuccess.setBoolean("result", true);
								sendPacket(player, tSuccess);
							}
							
						}
					}
				}
			}
			

			break;
		}

	}

}
