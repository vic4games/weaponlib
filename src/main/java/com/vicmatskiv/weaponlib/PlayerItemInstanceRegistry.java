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

public class PlayerItemInstanceRegistry {

	private Map<UUID, Map<Integer, PlayerItemInstance<?>>> registry = new HashMap<>();
	
	private SyncManager<?> syncManager;
	
	public PlayerItemInstanceRegistry(SyncManager<?> syncManager) {
		this.syncManager = syncManager;
	}

	/**
	 * Returns instance of the target class, or null if there is no instance or instance class does not match.
	 * 
	 * @param player
	 * @param targetClass
	 * @return
	 */
	public <T extends PlayerItemInstance<S>, S extends ManagedState<S>> T getMainHandItemInstance(EntityPlayer player, Class<T> targetClass) {
		if(player == null) {
			return null;
		}
		PlayerItemInstance<?> instance = getItemInstance(player, compatibility.getCurrentInventoryItemIndex(player));
		return targetClass.isInstance(instance) ? targetClass.cast(instance) : null;
	}
	
	public PlayerItemInstance<?> getItemInstance(EntityPlayer player, int slot) {
		Map<Integer, PlayerItemInstance<?>> slotContexts = registry.computeIfAbsent(player.getPersistentID(), p -> new HashMap<>());
		PlayerItemInstance<?> result = slotContexts.get(slot);
		if (result == null) {
			System.out.println("State not found, creating...");
			result = createItemInstance(player, slot);
			if(result != null) {
				slotContexts.put(slot, result);
				syncManager.watch(result);
			}
		} else {
			
		}
		return result;
	}
	
	// if input item does not match stored item, keep the old value, otherwise replace with new value
	BiFunction<? super PlayerItemInstance<?>, ? super PlayerItemInstance<?>, ? extends PlayerItemInstance<?>> merge = (currentState, newState) -> 
		isSameItem(currentState, newState) && isSameUpdateId(currentState, newState) ? currentState : newState;

	private boolean isSameUpdateId(PlayerItemInstance<?> currentState, PlayerItemInstance<?> newState) {
		return currentState.getUpdateId() == newState.getUpdateId();
	}

	private boolean isSameItem(PlayerItemInstance<?> instance1, PlayerItemInstance<?> instance2) {
		return Item.getIdFromItem(instance1.getItem()) == Item.getIdFromItem(instance2.getItem());
	}
	
	@SuppressWarnings("unchecked")
	public <S extends ManagedState<S>, T extends PlayerItemInstance<S>> boolean update(S newManagedState, T extendedStateToMerge) {
		
		Map<Integer, PlayerItemInstance<?>> slotContexts = registry.get(extendedStateToMerge.getPlayer().getUniqueID());
		
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

	private PlayerItemInstance<?> createItemInstance(EntityPlayer player, int slot) {
		ItemStack itemStack = compatibility.getInventoryItemStack(player, slot);
		
		PlayerItemInstance<?> result;
		if(itemStack != null) {
			try {
				result = Tags.getInstance(itemStack);
				if(result != null) {
					
					//
					result.setPlayer(player);
					return result;
				}
			} catch(RuntimeException e) {
				System.err.println("Opps, looks like serialization format has been changed for this item");
			}
		} else {
			return null;
		}
		
		if(itemStack.getItem() instanceof PlayerItemInstanceFactory) {
			return ((PlayerItemInstanceFactory<?, ?>) itemStack.getItem()).createItemInstance(player, itemStack, slot);	
		} else {
			result = new PlayerItemInstance<>(slot, player, itemStack);
		}

		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void removeItemInstance(EntityPlayer player, int slot) {
		System.out.println("Removing instance from slot " + slot);
		Map<Integer, PlayerItemInstance<?>> slotContexts = registry.get(player.getPersistentID());
		if(slotContexts != null) {
			PlayerItemInstance<?> instance = slotContexts.remove(slot);
			if(instance != null) {
				syncManager.unwatch((PlayerItemInstance) instance);
			}
//			if(instance instanceof PlayerWeaponInstance) {
//				instance.setState(WeaponState.READY); //TODO make it ready
//			}
		}

	}
}
