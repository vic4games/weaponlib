package com.vicmatskiv.weaponlib;

import net.minecraft.item.Item;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ChangeSettingsMessage implements IMessage {

	private float recoil;
	private int weaponItemId;

	public ChangeSettingsMessage() {
	}
	
	public ChangeSettingsMessage(Weapon weapon) {
		this.weaponItemId = Item.getIdFromItem(weapon);
	}
	
	public ChangeSettingsMessage(Weapon weapon, float recoil) {
		this.weaponItemId = Item.getIdFromItem(weapon);
		this.recoil = recoil;
	}
	
	public Weapon getWeapon() {
		Item item = Item.getItemById(weaponItemId);
		return item != null ? (Weapon) item : null;
	}

	public void fromBytes(ByteBuf buf) {
		this.weaponItemId = buf.readInt();
		this.recoil = buf.readFloat();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.weaponItemId);
		buf.writeFloat(this.recoil);
	}


	public float getRecoil() {
		return recoil;
	}

}