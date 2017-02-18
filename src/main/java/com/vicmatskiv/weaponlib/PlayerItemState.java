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
	}

	protected S state;
	protected long stateUpdateTimestamp;
	protected EntityPlayer player;
	protected Item item;
	protected int itemInventoryIndex;
	
	public PlayerItemState() {}
	
	public PlayerItemState(EntityPlayer player) {
		this.player = player;
		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
		if(itemStack != null) {
			this.item = itemStack.getItem();
		}
	}
	
	public PlayerItemState(EntityPlayer player, ItemStack itemStack) {
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

	@Override
	public void init(ByteBuf buf) {
		super.init(buf);
		item = Item.getItemById(buf.readInt());
		itemInventoryIndex = buf.readInt();
		state = TypeRegistry.getInstance().fromBytes(buf);
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(Item.getIdFromItem(item));
		buf.writeInt(itemInventoryIndex);
		TypeRegistry.getInstance().toBytes(state, buf);
	}

	@Override
	public boolean setState(S state) {
		this.state = state;
		stateUpdateTimestamp = System.currentTimeMillis();
		return false;
	}

	@Override
	public S getState() {
		return state;
	}

	@Override
	public long getStateUpdateTimestamp() {
		return stateUpdateTimestamp;
	}
}
