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
import java.util.function.BiFunction;

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
			if(result.getItemInventoryIndex() != slot) {
				logger.warn("Invalid instance slot id, correcting...");
				result.setItemInventoryIndex(slot);
			}
			if(result.getPlayer() != player) {
				logger.warn("Invalid player " + result.getPlayer()
						+ " associated with instance in slot, changing to " + player);
				result.setPlayer(player);
			}
			
		}
		return result;
	}
	
//	private boolean matches(ItemStack slotStack, PlayerItemInstance<?> result) {
//		byte instanceBytes[] = Tags.getInstanceBytes(slotStack);
//		if(instanceBytes == null || instanceBytes.length < 36) {
//			return false;
//		}
//		
//		ByteBuf buf = Unpooled.wrappedBuffer(instanceBytes);
//		buf.skipBytes(20); // skip serial version id (4 bytes) and type uuid (16 bytes)
//		UUID stackUuid = new UUID(buf.readLong(), buf.readLong()); // 16 more bytes
//		return stackUuid.equals(result);
//	}

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
			logger.debug("Creating instance for slot " + slot +  " from item stack "+ itemStack);
			try {
				result = Tags.getInstance(itemStack);
			} catch(RuntimeException e) {
				logger.debug("Failed to deserialize instance from " + itemStack);
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
				logger.debug("ItemStack " + itemStack + " not found in cache, initializing...");
				int slot = compatibility.getInventorySlot(player, itemStack);
				PlayerItemInstance<?> instance = null;
				if(slot >= 0) {
					instance = getItemInstance(player, slot);
					logger.debug("Resolved item stack instance " + instance + " from slot " + slot);
				} else {
					try {
						instance = Tags.getInstance(itemStack);
					} catch(RuntimeException e) {
						logger.error("Failed to deserialize instance from stack " + itemStack + ". " + e);
					}
				}
				return Optional.ofNullable(instance);
			});
		} catch (UncheckedExecutionException | ExecutionException e) {
			logger.error("Failed to initialize cache instance from " + itemStack, e.getCause());
		}
		return result.orElse(null);
	}

//	public PlayerItemInstance<?> getItemInstance2(EntityPlayer player, ItemStack itemStack) {
//		int slot = compatibility.getInventorySlot(player, itemStack);
//		PlayerItemInstance<?> result = null;
//		if(slot >= 0) {
//			result = getItemInstance(player, slot);
//		} else {
//			// For everything else use cache
//			result = getItemInstance(itemStack);
//		}
//		return result;
//	}

//	private PlayerItemInstance<?> getItemInstance(ItemStack itemStack) {
//		PlayerItemInstance<?> result = null;
//		try {
//			result = itemStackInstanceCache.get(itemStack, () -> {
//				logger.debug("Initializing instance from stack " + itemStack);
//				PlayerItemInstance<?> instance = null;
//				try {
//					instance = Tags.getInstance(itemStack);
//				} catch(RuntimeException e) {
//					logger.error("Failed to initialize instance from " + itemStack, e.getCause());
//				}
//				if(instance == null && itemStack.getItem() instanceof PlayerItemInstanceFactory) {
//					instance = ((PlayerItemInstanceFactory<?, ?>) itemStack.getItem()).createItemInstance(null, itemStack, -1);
//				}
//				return instance;
//			});
//		} catch (UncheckedExecutionException | ExecutionException e) {
//			logger.error("Failed to initialize cache instance from " + itemStack, e.getCause());
//		}
//		return result;
//	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(EntityPlayer player) {
		if(player == null) {
			return;
		}
		
		Map<Integer, PlayerItemInstance<?>> slotContexts = registry.get(player.getPersistentID());
		if(slotContexts != null) {
			for(Iterator<Entry<Integer, PlayerItemInstance<?>>> it = slotContexts.entrySet().iterator(); it.hasNext();) {
				Entry<Integer, PlayerItemInstance<?>> e = it.next();
				ItemStack slotStack = compatibility.getInventoryItemStack(player, e.getKey());
				if(slotStack == null) {
					logger.debug("Removing instance in slot " + e.getKey());
					syncManager.unwatch((PlayerItemInstance) e.getValue());
					it.remove();
				}
			}
		}
	}
}