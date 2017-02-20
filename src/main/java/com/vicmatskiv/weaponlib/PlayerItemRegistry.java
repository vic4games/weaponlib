package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.vicmatskiv.weaponlib.state.ManagedState;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlayerItemRegistry {

	private Map<UUID, Map<Integer, PlayerItemState<?>>> registry = new HashMap<>();
	
	private SyncManager<?> syncManager;
	
	public PlayerItemRegistry(SyncManager<?> syncManager) {
		this.syncManager = syncManager;
	}

	public PlayerItemState<?> getMainHandItemState(EntityPlayer player) {
		return getItemState(player, compatibility.getCurrentInventoryItemIndex(player));
	}
	
	public PlayerItemState<?> getItemState(EntityPlayer player, int slot) {
		Map<Integer, PlayerItemState<?>> slotContexts = registry.computeIfAbsent(player.getUniqueID(), p -> new HashMap<>());
		PlayerItemState<?> result = slotContexts.get(slot);
		if (result == null) {
			System.out.println("State not found, creating...");
			result = createItemState(player, slot);
			if(result != null) {
				slotContexts.put(slot, result);
				syncManager.watch(result);
			}
		}
		return result;
	}
	
	// if input item does not match stored item, keep the old value, otherwise replace with new value
	BiFunction<? super PlayerItemState<?>, ? super PlayerItemState<?>, ? extends PlayerItemState<?>> merge = (currentState, newState) -> 
		isSameItem(currentState, newState) && isSameUpdateId(currentState, newState) ? currentState : newState;

	private boolean isSameUpdateId(PlayerItemState<?> currentState, PlayerItemState<?> newState) {
		return currentState.getUpdateId() == newState.getUpdateId();
	}

	private boolean isSameItem(PlayerItemState<?> currentState, PlayerItemState<?> newState) {
		return Item.getIdFromItem(currentState.getItem()) == Item.getIdFromItem(newState.getItem());
	}
	
	@SuppressWarnings("unchecked")
	public <S extends ManagedState<S>, T extends PlayerItemState<S>> boolean update(S newManagedState, T extendedStateToMerge) {
		
		Map<Integer, PlayerItemState<?>> slotContexts = registry.get(extendedStateToMerge.getPlayer().getUniqueID());
		
		boolean result = false;
		
		if(slotContexts != null) {
			T currentState = (T) slotContexts.get(extendedStateToMerge.getItemInventoryIndex());
			if(currentState != null && isSameItem(currentState, extendedStateToMerge) && isSameUpdateId(currentState, extendedStateToMerge)) {
				/*
				 * If input.managedState has a transactional component, set current.managedState = input.managedState only,
				 * do not update the entire state
				 */
				extendedStateToMerge.setState(newManagedState); // why do we set it here?
				if(newManagedState.commitPhase() != null) {
					System.out.println("Preparing transaction for state " + newManagedState.commitPhase());
					currentState.prepareTransaction(extendedStateToMerge);
				} else {
					//slotContexts.put(extendedStateToMerge.getItemInventoryIndex(), extendedStateToMerge);
					currentState.updateWith(extendedStateToMerge, true);
				}
				result = true;
			}
		}
		
		return result;
	}

	private PlayerItemState<?> createItemState(EntityPlayer player, int slot) {
		ItemStack itemStack = compatibility.getInventoryItemStack(player, slot);
		if(itemStack == null) {
			return null;
		}
		PlayerItemState<?> result;
		if(itemStack.getItem() instanceof Weapon) {
			PlayerWeaponState state = new PlayerWeaponState(slot, player, itemStack);
			state.setAmmo(Tags.getAmmo(itemStack));
			state.setState(WeaponState.READY); // TODO: why is it ready by default? Shouldn't it be deserialized from item stack?
			result = state;
		} else {
			result = new PlayerItemState<>(slot, player, itemStack);
		}

		return result;
	}
}
