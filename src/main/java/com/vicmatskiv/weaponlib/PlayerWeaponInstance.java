package com.vicmatskiv.weaponlib;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.network.TypeRegistry;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;


public class PlayerWeaponInstance extends PlayerItemInstance<WeaponState> {
	
	private static final int SERIAL_VERSION = 5;
	
	private static final Logger logger = LogManager.getLogger(PlayerWeaponInstance.class);

	static {
		TypeRegistry.getInstance().register(PlayerWeaponInstance.class);
	}
	
	private int ammo;
	private float recoil; // TODO: serialize, initialize etc
	private int seriesShotCount; // TODO: serialize?
	private long lastFireTimestamp;
	private boolean aimed;
	private int maxShots;
	private float zoom = 1f;
		
	/*
	 * Upon adding an element to the head of the queue, all existing elements with lower priority are removed 
	 * from the queue. Elements with the same priority are not removed.
	 * This ensures the queue is always sorted by priority, lowest (head) to highest (tail).
	 */
	private Deque<AsyncWeaponState> filteredStateQueue = new LinkedBlockingDeque<>();
	private int[] activeAttachmentIds = new int[0];
	private byte[] selectedAttachmentIndexes = new byte[0];

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
	protected int getSerialVersion() {
		return SERIAL_VERSION;
	}
	
	private void addStateToHistory(WeaponState state) {
		AsyncWeaponState t;
		// Remove existing items from lower priorities from the top of the stack; stop when same or higher priority item is found
		while((t = filteredStateQueue.peekFirst()) != null) {
			if(t.getState().getPriority() < state.getPriority()) {
				filteredStateQueue.pollFirst();
			} else {
				break;
			}
		}
		
		long expirationTimeout;
		
		if(state == WeaponState.FIRING || state == WeaponState.RECOILED || state == WeaponState.PAUSED) {
			if(isAutomaticModeEnabled()) {
				expirationTimeout = (long) (50f / getFireRate());
			} else {
				expirationTimeout = 500;
			}
		} else {
			expirationTimeout = Integer.MAX_VALUE;
		}
		filteredStateQueue.addFirst(new AsyncWeaponState(state, this.stateUpdateTimestamp, expirationTimeout));
	}
	
	@Override
	public boolean setState(WeaponState state) {
		boolean result = super.setState(state);
		addStateToHistory(state);
		return result;
	}
	
	public AsyncWeaponState nextHistoryState() {
//		logger.debug("State queue: " + filteredStateQueue.stream().map(t -> t.getState() + ":" + t.getDuration())
//				.collect(Collectors.toList()));
		AsyncWeaponState result;
		if(filteredStateQueue.size() > 0) { // was > 1 earlier, why?
			result = filteredStateQueue.pollLast();
		} else {
			result = new AsyncWeaponState(getState(), stateUpdateTimestamp);
		}
		return result;
	}

	public int getAmmo() {
		return ammo;
	}
	
	protected void setAmmo(int ammo) {
		if(ammo != this.ammo) {
			this.ammo = ammo;
			updateId++; //TODO: what's going on with this update id?
		}
	}
	
	@Override
	public void init(ByteBuf buf) {
		super.init(buf);
		activeAttachmentIds = initIntArray(buf);
		selectedAttachmentIndexes = initByteArray(buf);
		ammo = buf.readInt();
		aimed = buf.readBoolean();
		recoil = buf.readFloat();
		maxShots = buf.readShort();
		zoom = buf.readFloat();
	}
	
	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		serializeIntArray(buf, activeAttachmentIds);
		serializeByteArray(buf, selectedAttachmentIndexes);
		buf.writeInt(ammo);
		buf.writeBoolean(aimed);
		buf.writeFloat(recoil);
		buf.writeShort(maxShots);
		buf.writeFloat(zoom);
	}
	
	private static void serializeIntArray(ByteBuf buf, int a[]) {
		buf.writeByte(a.length);
		for(int i = 0; i < a.length; i++) {
			buf.writeInt(a[i]);
		}
	}
	
	private static void serializeByteArray(ByteBuf buf, byte a[]) {
		buf.writeByte(a.length);
		for(int i = 0; i < a.length; i++) {
			buf.writeByte(a[i]);
		}
	}
	
	private static int[] initIntArray(ByteBuf buf) {
		int length = buf.readByte();
		int a[] = new int[length];
		for(int i = 0; i < length; i++) {
			a[i] = buf.readInt();
		}
		return a;
	}
	
	private static byte[] initByteArray(ByteBuf buf) {
		int length = buf.readByte();
		byte a[] = new byte[length];
		for(int i = 0; i < length; i++) {
			a[i] = buf.readByte();
		}
		return a;
	}
	
	@Override
	protected void updateWith(PlayerItemInstance<WeaponState> otherItemInstance, boolean updateManagedState) {
		super.updateWith(otherItemInstance, updateManagedState);
		PlayerWeaponInstance otherWeaponInstance = (PlayerWeaponInstance) otherItemInstance;
		
		setAmmo(otherWeaponInstance.ammo);
		setZoom(otherWeaponInstance.zoom);
		setRecoil(otherWeaponInstance.recoil);
		setSelectedAttachmentIndexes(otherWeaponInstance.selectedAttachmentIndexes);
		setActiveAttachmentIds(otherWeaponInstance.activeAttachmentIds);
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

	public int getMaxShots() {
		return maxShots;
	}

	void setMaxShots(int maxShots) {
		if(this.maxShots != maxShots) {
			this.maxShots = maxShots;
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
		return maxShots > 1;
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
		if(activeAttachmentIds == null || activeAttachmentIds.length != AttachmentCategory.values.length) {
			activeAttachmentIds = new int[AttachmentCategory.values.length];
			for(CompatibleAttachment<Weapon> attachment: getWeapon().getCompatibleAttachments().values()) {
				if(attachment.isDefault()) {
					activeAttachmentIds[attachment.getAttachment().getCategory().ordinal()] = Item.getIdFromItem(attachment.getAttachment());
				}
			}
		}
		return activeAttachmentIds;
	}

	void setActiveAttachmentIds(int[] activeAttachmentIds) {
		if(!Arrays.equals(this.activeAttachmentIds, activeAttachmentIds)) {
			this.activeAttachmentIds = activeAttachmentIds;
			updateId++;
		}
	}

	public byte[] getSelectedAttachmentIds() {
		return selectedAttachmentIndexes;
	}

	void setSelectedAttachmentIndexes(byte[] selectedAttachmentIndexes) {
		if(!Arrays.equals(this.selectedAttachmentIndexes, selectedAttachmentIndexes)) {
			this.selectedAttachmentIndexes = selectedAttachmentIndexes;
			updateId++;
		}
	}

	public boolean isAttachmentZoomEnabled() {
		Item scopeItem = getAttachmentItemWithCategory(AttachmentCategory.SCOPE);
		return scopeItem instanceof ItemScope;
	}

	Item getAttachmentItemWithCategory(AttachmentCategory category) {
		if(activeAttachmentIds == null || activeAttachmentIds.length <= category.ordinal()) {
			return null;
		}
		Item scopeItem = Item.getItemById(activeAttachmentIds[category.ordinal()]);
		return scopeItem;
	}

	public float getZoom() {
		return zoom;
	}
	
	public void setZoom(float zoom) {
		if(this.zoom != zoom) {
			this.zoom = zoom;
			updateId++;
		}
		
	}
	
	@Override
	public String toString() {
		return getWeapon().builder.name + "[" + getUuid() + "]";
	}

}