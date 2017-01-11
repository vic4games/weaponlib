package com.vicmatskiv.weaponlib;

import net.minecraft.item.Item;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ReloadMessage implements IMessage {
	
	public static enum Type { LOAD, UNLOAD, CANCEL };
	
	private int ammo;
	private int weaponItemId;
	private int magazineItemId;
	private Type type = Type.LOAD;

	public ReloadMessage() {
	}
	
	public ReloadMessage(Weapon weapon) {
		this(weapon, Type.LOAD, null, 0);
	}
	
	public ReloadMessage(Weapon weapon, int ammo) {
		this(weapon, Type.LOAD, null, ammo);
	}
	
	public ReloadMessage(Weapon weapon, Type type, ItemMagazine magazine, int ammo) {
		this.weaponItemId = Item.getIdFromItem(weapon);
		this.magazineItemId = magazine != null ? Item.getIdFromItem(magazine) : 0;
		this.type = type;
		this.ammo = ammo;
	}

	public int getAmmo() {
		return ammo;
	}
	
	public Type getType() {
		return type;
	}
	
	public Weapon getWeapon() {
		Item item = Item.getItemById(weaponItemId);
		return item != null ? (Weapon) item : null;
	}
	
	public ItemMagazine getMagazine() {
		Item item = Item.getItemById(magazineItemId);
		return item != null ? (ItemMagazine) item : null;
	}

	public void fromBytes(ByteBuf buf) {
		this.weaponItemId = buf.readInt();
		this.magazineItemId = buf.readInt();
		this.type = Type.values()[buf.readInt()];
		this.ammo = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.weaponItemId);
		buf.writeInt(this.magazineItemId);
		buf.writeInt(this.type.ordinal());
		buf.writeInt(this.ammo);
	}

	
}
