package com.vicmatskiv.weaponlib;

import net.minecraft.item.Item;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ReloadMessage implements IMessage {

	private int ammo;
	private int weaponItemId;

	public ReloadMessage() {
	}
	
	public ReloadMessage(Weapon weapon) {
		this.weaponItemId = Item.getIdFromItem(weapon);
	}
	
	public ReloadMessage(Weapon weapon, int ammo) {
		this.weaponItemId = Item.getIdFromItem(weapon);
		this.ammo = ammo;
	}

	public int getAmmo() {
		return ammo;
	}
	
	public Weapon getWeapon() {
		Item item = Item.getItemById(weaponItemId);
		return item != null ? (Weapon) item : null;
	}

	public void fromBytes(ByteBuf buf) {
		this.weaponItemId = buf.readInt();
		this.ammo = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.weaponItemId);
		buf.writeInt(this.ammo);
	}

	
}
