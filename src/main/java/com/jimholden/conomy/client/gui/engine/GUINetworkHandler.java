package com.jimholden.conomy.client.gui.engine;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.util.packets.AdvGUIClientPacket;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GUINetworkHandler {
	
	

	public NBTTagCompound writePacket(int op) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("op", op);
		return nbt;
	}
	
	public NBTTagCompound writeRecursivePacket(int op, int playerID) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("op", op);
		nbt.setInteger("playerID", playerID);
		return nbt;
	}
	
	public void sendPacket(EntityPlayerMP p, NBTTagCompound nbt) {
		Main.NETWORK.sendTo(new AdvGUIClientPacket(nbt), p);
	}
	
	public EntityPlayerMP getEntityPlayer(NBTTagCompound nbt) {
		return (EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getEntityByID(nbt.getInteger("playerID"));
	}
	
	public int getOpCode(NBTTagCompound nbt) {
		return nbt.getInteger("op");
	}
	
	public void runServer(NBTTagCompound nbt) {
		
	}

}
