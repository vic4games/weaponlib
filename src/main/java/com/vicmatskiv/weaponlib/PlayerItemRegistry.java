package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.vicmatskiv.weaponlib.state.ManagedState;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlayerItemRegistry {

	private Map<UUID, Map<Integer, PlayerItemState<?>>> registry = new HashMap<>();
	
	public PlayerItemState<?> getMainHandItemContext(EntityPlayer player) {
		return getItemContext(player, compatibility.getCurrentInventoryItemIndex(player));
	}
	
	public PlayerItemState<?> getItemContext(EntityPlayer player, int slot) {
		Map<Integer, PlayerItemState<?>> slotContexts = registry.computeIfAbsent(player.getUniqueID(), p -> new HashMap<>());
		return slotContexts.computeIfAbsent(slot, s -> createItemContext(player, s));
	}
	
	@SuppressWarnings("unchecked")
	public <S extends ManagedState<S>, T extends PlayerItemState<S>> T update(T input) {
		Map<Integer, PlayerItemState<?>> slotContexts = registry.get(input.getPlayer().getUniqueID());
		return (T)(slotContexts != null ? slotContexts.merge(input.getItemInventoryIndex(), input, 
				// if input item does not match stored item, keep the old value, otherwise replace with new value
				(oldValue, newValue) -> Item.getIdFromItem(oldValue.getItem()) != Item.getIdFromItem(newValue.getItem()) ? oldValue : newValue
				) : null);
	}

	private PlayerItemState<?> createItemContext(EntityPlayer player, int slot) {
		ItemStack itemStack = compatibility.getInventoryItemStack(player, slot);
		if(itemStack.getItem() instanceof Weapon) {
			PlayerWeaponState state = new PlayerWeaponState(player, itemStack);
			state.setState(WeaponState.READY); // TODO: why is it ready by default? Shouldn't it be deserialized from item stack?
			return state;
		}
		return new PlayerItemState<>(player, itemStack);
	}
}
