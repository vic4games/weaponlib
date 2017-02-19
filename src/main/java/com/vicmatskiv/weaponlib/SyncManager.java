package com.vicmatskiv.weaponlib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.PermitManager;

public class SyncManager<S extends ManagedState<S>> {
	
	private PermitManager permitManager;
	
	private Map<PlayerItemState<?>, Long> watchables = new LinkedHashMap<>();
	
	@SuppressWarnings("unchecked")
	public SyncManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		this.permitManager.registerEvaluator(Permit.class, PlayerItemState.class, this::syncOnServer);
	}
	
	private void syncOnServer(Permit<S> permit, PlayerItemState<S> state) {
		System.out.println("Syncing state " + state + " on server");
	}

	public void watch(PlayerItemState<?> watchable) {
		watchables.put(watchable, watchable.getUpdateId());
	}
	
	public void unwatch(PlayerItemState<S> watchable) {
		watchables.remove(watchable);
	}
	
	public void run() {
		List<PlayerItemState<?>> updates = watchables.entrySet().stream()
				.filter(e -> e.getKey().getUpdateId() != e.getValue() && !e.getKey().getState().isTransient())
				.map(e -> e.getKey())
				.collect(Collectors.toList());
		updates.forEach(this::sync);
	}
	
	@SuppressWarnings("unchecked")
	private void sync(PlayerItemState<?> watchable) {
		permitManager.request(new Permit<S>((S) watchable.getState()), (PlayerItemState<S>)watchable, (p, e) -> {
			watchables.put(watchable, watchable.getUpdateId());
		});
	}
}
