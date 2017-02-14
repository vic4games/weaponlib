package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.network.UniversalObject;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.ManagedStateContainer;
import com.vicmatskiv.weaponlib.state.StateContext;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlayerItemContext extends UniversalObject implements StateContext {

	protected EntityPlayer player;
	protected Item item;
	protected ItemStack itemStack;
	protected int itemInventoryIndex;
	private ManagedStateContainer<ManagedState> stateContainer;
	
	public PlayerItemContext() {}
	
	public PlayerItemContext(ManagedStateContainer<ManagedState> stateContainer, EntityPlayer player) {
		setManagedStateContainer(stateContainer);
		this.player = player;
		this.itemStack = compatibility.getHeldItemMainHand(player);
		if(this.itemStack != null) {
			this.item = itemStack.getItem();
		}
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	
	public Item getItem() {
		return item;
	}
	
	public ItemStack getItemStack() {
		if(itemStack == null) {
			itemStack = compatibility.getInventoryItemStack(player, itemInventoryIndex);
		}
		return itemStack;
	}
	
	public ManagedStateContainer<ManagedState> getStateContainer() {
		return stateContainer;
	}

	public void setManagedStateContainer(ManagedStateContainer<ManagedState> stateContainer) {
		this.stateContainer = stateContainer;
	}
	
	@Override
	public boolean init(ByteBuf buf) {
		super.init(buf);
		item = Item.getItemById(buf.readInt());
		itemInventoryIndex = buf.readInt();
		return true;
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(Item.getIdFromItem(item));
		buf.writeInt(itemInventoryIndex);
	}

}
