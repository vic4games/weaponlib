package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.vicmatskiv.weaponlib.state.ManagedState;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlayerItemInstanceRegistry {
	
	private static final int CACHE_EXPIRATION_TIMEOUT_SECONDS = 5;

	private static final Logger logger = LogManager.getLogger(PlayerItemInstanceRegistry.class);

	private Map<UUID, Map<Integer, PlayerItemInstance<?>>> registry = new HashMap<>();
	
	private SyncManager<?> syncManager;
	
	private Cache<ItemStack, Optional<PlayerItemInstance<?>>> itemStackInstanceCache;
	
	public PlayerItemInstanceRegistry(SyncManager<?> syncManager) {
		this.syncManager = syncManager;
		this.itemStackInstanceCache = CacheBuilder
				.newBuilder()
				.weakKeys()
				.maximumSize(1000)
				.expireAfterAccess(CACHE_EXPIRATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.build();
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
				result.markDirty();
			}
		} else {
			ItemStack slotItemStack = compatibility.getInventoryItemStack(player, slot);
			if(slotItemStack != null && slotItemStack.getItem() != result.getItem()) {
				syncManager.unwatch(result);
				result = createItemInstance(player, slot);
				if(result != null) {
					slotInstances.put(slot, result);
					syncManager.watch(result);
					result.markDirty();
				}
			}
			if(result.getItemInventoryIndex() != slot) {
				logger.warn("Invalid instance slot id, correcting...");
				result.setItemInventoryIndex(slot);
			}
			if(result.getPlayer() != player) {
				logger.warn("Invalid player " + result.getPlayer()
						+ " associated with instance in slot, changing to {}", player);
				result.setPlayer(player);
			}
			
		}
		return result;
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
		
		PlayerItemInstance<?> result = null;
		if(itemStack != null && itemStack.getItem() instanceof PlayerItemInstanceFactory) {
			logger.debug("Creating instance for slot {} from stack {}", slot, itemStack);
			try {
				result = Tags.getInstance(itemStack);
			} catch(RuntimeException e) {
				logger.debug("Failed to deserialize instance from {}", itemStack);
			}
			if(result == null) {
				result = ((PlayerItemInstanceFactory<?, ?>) itemStack.getItem()).createItemInstance(player, itemStack, slot);
			}
			result.setItemInventoryIndex(slot);
			result.setPlayer(player);
		}
		
		return result;
	}
	
	public PlayerItemInstance<?> getItemInstance(EntityPlayer player, ItemStack itemStack) {
		Optional<PlayerItemInstance<?>> result = Optional.empty();
		try {
			result = itemStackInstanceCache.get(itemStack, () -> {
				logger.debug("ItemStack {} not found in cache, initializing...", itemStack);
				int slot = compatibility.getInventorySlot(player, itemStack);
				PlayerItemInstance<?> instance = null;
				if(slot >= 0) {
					instance = getItemInstance(player, slot);
					logger.debug("Resolved item stack instance {} in slot {}", instance, slot);
				} else {
					try {
						instance = Tags.getInstance(itemStack);
					} catch(RuntimeException e) {
						logger.error("Failed to deserialize instance from stack {}: {}", itemStack, e.toString());
					}
				}
				return Optional.ofNullable(instance);
			});
		} catch (UncheckedExecutionException | ExecutionException e) {
			logger.error("Failed to initialize cache instance from {}", itemStack, e.getCause());
		}
		return result.orElse(null);
	}

	@SuppressWarnings({ "rawtypes" })
	public void update(EntityPlayer player) {
		if(player == null) {
			return;
		}
		
		Map<Integer, PlayerItemInstance<?>> slotContexts = registry.get(player.getPersistentID());
		if(slotContexts != null) {
			for(Iterator<Entry<Integer, PlayerItemInstance<?>>> it = slotContexts.entrySet().iterator(); it.hasNext();) {
				Entry<Integer, PlayerItemInstance<?>> e = it.next();
				ItemStack slotStack = compatibility.getInventoryItemStack(player, e.getKey());
				if(slotStack == null || slotStack.getItem() != e.getValue().getItem()) {
					logger.debug("Removing {} from slot {}", e.getValue(), e.getKey());
					syncManager.unwatch((PlayerItemInstance) e.getValue());
					it.remove();
				}
			}
		}
	}
}
