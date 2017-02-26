package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.vicmatskiv.weaponlib.state.ManagedState;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
		Map<Integer, PlayerItemInstance<?>> slotInstances = registry.computeIfAbsent(player.getPersistentID(), p -> new HashMap<>());
		PlayerItemInstance<?> result = slotInstances.get(slot);
		if (result == null) {
			
			result = createItemInstance(player, slot);
			if(result != null) {
				slotInstances.put(slot, result);
				syncManager.watch(result);
			}
		} else {
			// TODO: compare result with the actual slot content somehow
			// if no match, unwatch, re-create and watch
			ItemStack slotStack = compatibility.getInventoryItemStack(player, slot);
			if(matches(slotStack, result)) {
				System.err.println("Stored item instance does not match instance in slot");
			}
			if(result.getItemInventoryIndex() != slot) {
				System.err.println("Invalid item slot id, correcting...");
				result.setItemInventoryIndex(slot);
			}
			
		}
		return result;
	}
	
	private boolean matches(ItemStack slotStack, PlayerItemInstance<?> result) {
		byte instanceBytes[] = Tags.getInstanceBytes(slotStack);
		if(instanceBytes == null || instanceBytes.length < 36) {
			return false;
		}
		
		ByteBuf buf = Unpooled.wrappedBuffer(instanceBytes);
		buf.skipBytes(20); // skip serial version id (4 bytes) and type uuid (16 bytes)
		UUID stackUuid = new UUID(buf.readLong(), buf.readLong()); // 16 more bytes
		return stackUuid.equals(result);
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
			if(currentState != null && isSameItem(currentState, extendedStateToMerge)
					/*&& isSameUpdateId(currentState, extendedStateToMerge)*/) {
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
			System.out.println("State for slot " + slot +  " not found, creating...");
			try {
				result = Tags.getInstance(itemStack);
				if(result != null) {
					result.setItemInventoryIndex(slot);
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

	public PlayerItemInstance<?> getItemInstance(EntityPlayer player, ItemStack itemStack) {
		int slot = compatibility.getInventorySlot(player, itemStack);
		PlayerItemInstance<?> result = null;
		if(slot >= 0) {
			result = getItemInstance(player, slot);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(EntityPlayer player) {
		if(player == null) {
			return;
		}
		Map<Integer, PlayerItemInstance<?>> slotContexts = registry.get(player.getPersistentID());
		if(slotContexts != null) {
			compatibility.forEachInventorySlot((slot, stack) -> {
				if(stack == null) {
					PlayerItemInstance<?> instance = slotContexts.remove(slot);
					if(instance != null) {
						System.out.println("Removing instance " + instance);
						syncManager.unwatch((PlayerItemInstance) instance);
					}
				}
			});
		}
	}
}
