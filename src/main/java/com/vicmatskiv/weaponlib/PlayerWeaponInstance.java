package com.vicmatskiv.weaponlib;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import com.vicmatskiv.weaponlib.network.TypeRegistry;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
		
	/*
	 * Upon adding an element to the head of the queue, all existing elements with lower priority are removed 
	 * from the queue. Elements with the same priority are not removed.
	 * This ensures the queue is always sorted by priority, lowest (head) to highest (tail).
	 */
	private Deque<Tuple<WeaponState, Long>> filteredStateQueue = new LinkedBlockingDeque<>();
	private int[] activeAttachmentsIds;

	public PlayerWeaponInstance() {
		super();
	}

	public PlayerWeaponInstance(int itemInventoryIndex, EntityPlayer player, ItemStack itemStack) {
		super(itemInventoryIndex, player, itemStack);
	}

	public PlayerWeaponInstance(int itemInventoryIndex, EntityPlayer player) {
		super(itemInventoryIndex, player);
	}
	
	private void addStateToHistory(WeaponState state) {
		Tuple<WeaponState, Long> t;
		
		// Remove existing items from lower priorities from the top of the stack; stop when same or higher priority item is found
		while((t = filteredStateQueue.peekFirst()) != null) {
			if(t.getU().getPriority() < state.getPriority()) {
				filteredStateQueue.pollFirst();
			} else {
				break;
			}
		}
		
		filteredStateQueue.addFirst(new Tuple<>(state, System.currentTimeMillis()));
	}
	
	@Override
	public boolean setState(WeaponState state) {
		boolean result = super.setState(state);
		addStateToHistory(state);
		return result;
	}
	
	public Tuple<WeaponState, Long> nextHistoryState() {
		Tuple<WeaponState, Long> result;
		if(filteredStateQueue.size() > 1) {
			result = filteredStateQueue.pollLast();
		} else {
			result = new Tuple<>(getState(), System.currentTimeMillis());
		}
		return result;
	}

	public int getAmmo() {
		return ammo;
	}
	
	protected void setAmmo(int ammo) {
		if(ammo != this.ammo) {
			System.out.println("Updating instance with ammo " + ammo);
			this.ammo = ammo;
			//updateId++; //TODO: what's going on with this update id?
		}
		
	}
	
	@Override
	public void init(ByteBuf buf) {
		super.init(buf);
		ammo = buf.readInt();
		aimed = buf.readBoolean();
		recoil = buf.readFloat();
	}
	
	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(ammo);
		buf.writeBoolean(aimed);
		buf.writeFloat(recoil);
	}
	
	@Override
	protected void updateWith(PlayerItemInstance<WeaponState> otherItemInstance, boolean updateManagedState) {
		super.updateWith(otherItemInstance, updateManagedState);
		PlayerWeaponInstance otherWeaponInstance = (PlayerWeaponInstance) otherItemInstance;
		setAmmo(otherWeaponInstance.ammo);
	}

	public Weapon getWeapon() {
		return (Weapon)item;
	}

	public float getRecoil() {
		return recoil;
	}

	public void setRecoil(float recoil) {
		if(recoil != this.recoil) {
			this.recoil = recoil;
			updateId++;
		}
		
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
		if(aimed != this.aimed) {
			this.aimed = aimed;
			updateId++;
		}
	}

	public int[] getActiveAttachmentIds() {
		if(activeAttachmentsIds == null || activeAttachmentsIds.length != AttachmentCategory.values.length) {
			activeAttachmentsIds = new int[AttachmentCategory.values.length];
			for(CompatibleAttachment<Weapon> attachment: getWeapon().getCompatibleAttachments().values()) {
				if(attachment.isDefault()) {
					activeAttachmentsIds[attachment.getAttachment().getCategory().ordinal()] = Item.getIdFromItem(attachment.getAttachment());
				}
			}
		}
		return activeAttachmentsIds;
	}

	public void setActiveAttachmentIds(int[] activeAttachmentIds) {
		this.activeAttachmentsIds = activeAttachmentIds;
	}
}
