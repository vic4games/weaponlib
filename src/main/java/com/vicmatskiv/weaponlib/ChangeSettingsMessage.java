package com.vicmatskiv.weaponlib;

import net.minecraft.item.Item;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ChangeSettingsMessage implements IMessage {

	private float recoil;
	private float zoom;
	private int weaponItemId;
	private int flags;
	
	private static int RECOIL_FLAG = 0x01;
	private static int ZOOM_FLAG = 0x02;
	private static int TOGGLE_AIMING_FLAG = 0x04;

	public ChangeSettingsMessage() {
	}
	
	public ChangeSettingsMessage(Weapon weapon) {
		this.weaponItemId = Item.getIdFromItem(weapon);
	}
	
	public static ChangeSettingsMessage createToggleAimingMessage(Weapon weapon) {
		ChangeSettingsMessage message = new ChangeSettingsMessage();
		message.weaponItemId = Item.getIdFromItem(weapon);
		message.flags = TOGGLE_AIMING_FLAG;
		return message;
	}
	
	public static ChangeSettingsMessage createChangeRecoilMessage(Weapon weapon, float recoil) {
		ChangeSettingsMessage message = new ChangeSettingsMessage();
		message.weaponItemId = Item.getIdFromItem(weapon);
		message.recoil = recoil;
		message.flags = RECOIL_FLAG;
		return message;
	}
	
	public static ChangeSettingsMessage createChangeZoomMessage(Weapon weapon, float zoom) {
		ChangeSettingsMessage message = new ChangeSettingsMessage();
		message.weaponItemId = Item.getIdFromItem(weapon);
		message.zoom = zoom;
		message.flags = ZOOM_FLAG;
		return message;
	}
	
	public ChangeSettingsMessage(Weapon weapon, float recoil) {
		this.weaponItemId = Item.getIdFromItem(weapon);
		this.recoil = recoil;
		this.flags = RECOIL_FLAG;
	}
	
	public Weapon getWeapon() {
		Item item = Item.getItemById(weaponItemId);
		return item != null ? (Weapon) item : null;
	}

	public void fromBytes(ByteBuf buf) {
		this.weaponItemId = buf.readInt();
		this.flags = buf.readInt();
		this.recoil = buf.readFloat();
		this.zoom = buf.readFloat();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.weaponItemId);
		buf.writeInt(this.flags);
		buf.writeFloat(this.recoil);
		buf.writeFloat(this.zoom);
	}
	
	public boolean aimingChanged() {
		return (flags & TOGGLE_AIMING_FLAG) == TOGGLE_AIMING_FLAG;
	}

	public boolean zoomChanged() {
		return (flags & ZOOM_FLAG) == ZOOM_FLAG;
	}
	
	public boolean recoilChanged() {
		return (flags & RECOIL_FLAG) == RECOIL_FLAG;
	}

	public float getRecoil() {
		return recoil;
	}

	public float getZoom() {
		return zoom;
	}

}