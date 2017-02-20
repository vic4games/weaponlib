package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.network.UniversalObject;
import com.vicmatskiv.weaponlib.state.ExtendedState;
import com.vicmatskiv.weaponlib.state.ManagedState;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlayerItemState<S extends ManagedState<S>> extends UniversalObject implements ExtendedState<S>, PlayerContext {
	
	static {
		TypeRegistry.getInstance().register(PlayerItemState.class);
		TypeRegistry.getInstance().register(PlayerWeaponState.class);
	}
	
//	public static interface PlayerItemStateListener<S extends ManagedState<S>> {
//		void stateChanged(PlayerItemState<S> playerItemState);
//	}

	protected S state;
	protected long stateUpdateTimestamp;
	protected long updateId;
	protected EntityPlayer player;
	protected Item item;
	protected int itemInventoryIndex;
	private PlayerItemState<S> preparedState;
	private boolean dirty;
	
	
//	private Set<PlayerItemStateListener<S>> listeners = new HashSet<>();
	
	public PlayerItemState() {}
	
	public PlayerItemState(int itemInventoryIndex, EntityPlayer player) {
		this.itemInventoryIndex = itemInventoryIndex;
		this.player = player;
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		if(itemStack != null) {
			this.item = itemStack.getItem();
		}
	}
	
	public PlayerItemState(int itemInventoryIndex, EntityPlayer player, ItemStack itemStack) {
		this.itemInventoryIndex = itemInventoryIndex;
		this.player = player;
		//this.itemStack = itemStack;
		if(itemStack != null) {
			this.item = itemStack.getItem();
		}
	}

	@Override
	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	
	public Item getItem() {
		return item;
	}
	
	public ItemStack getItemStack() {
		return compatibility.getInventoryItemStack(player, itemInventoryIndex);
	}
	
	public int getItemInventoryIndex() {
		return itemInventoryIndex;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends PlayerItemState<S>> T getPreparedState() {
		return (T)preparedState;
	}

	@Override
	public void init(ByteBuf buf) {
		super.init(buf);
		item = Item.getItemById(buf.readInt());
		itemInventoryIndex = buf.readInt();
		updateId = buf.readLong();
		state = TypeRegistry.getInstance().fromBytes(buf);
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(Item.getIdFromItem(item));
		buf.writeInt(itemInventoryIndex);
		buf.writeLong(updateId);
		TypeRegistry.getInstance().toBytes(state, buf);
	}

	@Override
	public boolean setState(S state) {
		this.state = state;
		stateUpdateTimestamp = System.currentTimeMillis();
		updateId++;
		if(preparedState != null) { // TODO: use comparator or equals?
			if(preparedState.getState().commitPhase() == state) {
				System.out.println("Committing state " + preparedState.getState() 
					+ " to " + preparedState.getState().commitPhase());
				updateWith(preparedState, false);
			} else {
				rollback();
			}
			
			preparedState = null;
		}
//		notifyListeners();
		return false;
	}

	protected void rollback() {
	}

	/**
	 * Commits pending state
	 */
	protected void updateWith(PlayerItemState<S> otherState, boolean updateManagedState) {
		if(updateManagedState) {
			setState(otherState.getState());
		}
	}

	@Override
	public S getState() {
		return state;
	}

	@Override
	public long getStateUpdateTimestamp() {
		return stateUpdateTimestamp;
	}

	public long getUpdateId() {
		return updateId;
	}

	@Override
	public <E extends ExtendedState<S>> void prepareTransaction(E preparedExtendedState) {
		setState(preparedExtendedState.getState());
		this.preparedState = (PlayerItemState<S>) preparedExtendedState;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	protected void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
//	public void addListener(PlayerItemStateListener<S> listener) {
//		listeners.add(listener);
//	}
//	
//	public void removeListener(PlayerItemStateListener<S> listener) {
//		listeners.remove(listener);
//	}
//	
//	protected void notifyListeners() {
//		listeners.forEach(l -> l.stateChanged(this));
//	}

}
