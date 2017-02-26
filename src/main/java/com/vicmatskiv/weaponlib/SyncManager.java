package com.vicmatskiv.weaponlib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.PermitManager;

import net.minecraft.item.ItemStack;

public class SyncManager<S extends ManagedState<S>> {
	
	private PermitManager permitManager;
	
	private Map<PlayerItemInstance<?>, Long> watchables = new LinkedHashMap<>();
	
	private long syncTimeout = 10000;
	
	@SuppressWarnings("unchecked")
	public SyncManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		this.permitManager.registerEvaluator(Permit.class, PlayerItemInstance.class, this::syncOnServer);
	}
	
	private void syncOnServer(Permit<S> permit, PlayerItemInstance<S> instance) {
		System.out.println("Syncing " + instance + " in state " + instance.getState() + " on server");
		ItemStack itemStack = instance.getItemStack();
		if(itemStack != null) {
			System.out.println("Stored " + instance + " in stack " + itemStack);
			Tags.setInstance(itemStack, instance);
		}
	}

	public void watch(PlayerItemInstance<?> watchableInstance) {
		watchables.put(watchableInstance, watchableInstance.getUpdateId());
	}
	
	public void unwatch(PlayerItemInstance<S> watchableInstance) {
		watchables.remove(watchableInstance);
	}
	
	public void run() {
		List<PlayerItemInstance<?>> instancesToUpdate = watchables.entrySet().stream()
				.filter(e -> e.getKey().getUpdateId() != e.getValue() 
							&& !e.getKey().getState().isTransient()
							&& e.getKey().getSyncStartTimestamp() + syncTimeout < System.currentTimeMillis())
				.map(e -> e.getKey())
				.collect(Collectors.toList());
		instancesToUpdate.forEach(this::sync);
	}
	
	@SuppressWarnings("unchecked")
	private void sync(PlayerItemInstance<?> watchable) {
		System.out.println("Start syncing watchable " + watchable
				+ " with update id " + watchable.getUpdateId());
		long updateId = watchable.getUpdateId(); // capturing update id
		watchable.setSyncStartTimestamp(System.currentTimeMillis());
		permitManager.request(new Permit<S>((S) watchable.getState()), (PlayerItemInstance<S>)watchable, (p, e) -> {
			// During sync, the watchable.getUpdateId() can change, so using the original update id
			watchables.put(watchable, updateId); 
			watchable.setSyncStartTimestamp(0);
			System.out.println("Completed syncing watchable " + watchable
					+ " with update id " + updateId);
		});
	}
}
