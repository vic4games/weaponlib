package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.network.TypeRegistry;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerWeaponState extends PlayerItemState<WeaponState> {
	
	static {
		TypeRegistry.getInstance().register(PlayerWeaponState.class);
	}
	
	private int ammo;

	public PlayerWeaponState() {
		super();
	}

	public PlayerWeaponState(EntityPlayer player, ItemStack itemStack) {
		super(player, itemStack);
	}

	public PlayerWeaponState(EntityPlayer player) {
		super(player);
	}

	public int getAmmo() {
		return ammo;
	}
	
	protected void setAmmo(int ammo) {
		this.ammo = ammo;
	}
	
	@Override
	public void init(ByteBuf buf) {
		super.init(buf);
		ammo = buf.readInt();
	}
	
	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(ammo);
	}
	
	@Override
	protected void updateWith(PlayerItemState<WeaponState> otherItemState, boolean updateManagedState) {
		super.updateWith(otherItemState, updateManagedState);
		PlayerWeaponState other = (PlayerWeaponState) otherItemState;
		setAmmo(other.ammo);
	}
}
