package com.vicmatskiv.weaponlib;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.vicmatskiv.weaponlib.network.TypeRegistry;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;


public class PlayerWeaponInstance extends PlayerItemInstance<WeaponState> {
	
	static {
		TypeRegistry.getInstance().register(PlayerWeaponInstance.class);
	}
	
	private int ammo;
	private float recoil; // TODO: serialize, initialize etc
	private int seriesShotCount; // TODO: serialize?
	private long lastFireTimestamp;
	private boolean aimed;
	
	private Queue<Tuple<WeaponState, Long>> stateHistory = new ArrayBlockingQueue<>(100);

	public PlayerWeaponInstance() {
		super();
	}

	public PlayerWeaponInstance(int itemInventoryIndex, EntityPlayer player, ItemStack itemStack) {
		super(itemInventoryIndex, player, itemStack);
	}

	public PlayerWeaponInstance(int itemInventoryIndex, EntityPlayer player) {
		super(itemInventoryIndex, player);
	}
	
	@Override
	public boolean setState(WeaponState state) {
		boolean result = super.setState(state);
		stateHistory.add(new Tuple<>(state, System.currentTimeMillis()));
		return result;
	}
	
	public Tuple<WeaponState, Long> nextHistoryState() {
		Tuple<WeaponState, Long> result;
		if(stateHistory.size() > 1) {
			result = stateHistory.poll();
		} else {
			result = new Tuple<>(getState(), System.currentTimeMillis());
		}
		return result;
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
	protected void updateWith(PlayerItemInstance<WeaponState> otherItemInstance, boolean updateManagedState) {
		super.updateWith(otherItemInstance, updateManagedState);
		PlayerWeaponInstance otherWeaponInstance = (PlayerWeaponInstance) otherItemInstance;
		System.out.println("Updating instance with ammo " + otherWeaponInstance.ammo);
		setAmmo(otherWeaponInstance.ammo);
	}

	public Weapon getWeapon() {
		return (Weapon)item;
	}

	public float getRecoil() {
		return recoil;
	}

	public int getSeriesShotCount() {
		return seriesShotCount;
	}

	public void setSeriesShotCount(int seriesShotCount) {
		this.seriesShotCount = seriesShotCount;
	}

	public long getLastFireTimestamp() {
		return lastFireTimestamp;
	}

	public void setLastFireTimestamp(long lastFireTimestamp) {
		this.lastFireTimestamp = lastFireTimestamp;
	}

	public void resetCurrentSeries() {
		this.seriesShotCount = 0;
	}

	public float getFireRate() {
		return getWeapon().builder.fireRate;
	}
	
	public boolean isAutomaticModeEnabled() {
		return getWeapon().builder.maxShots > 1;
	}
	
	public boolean isAimed() {
		return aimed;
	}

	public void setAimed(boolean aimed) {
		this.aimed = aimed;
	}
}
