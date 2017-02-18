package com.vicmatskiv.weaponlib;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.util.concurrent.AtomicDouble;
import com.vicmatskiv.weaponlib.Weapon.State;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.ExtendedState;

public final class WeaponClientStorage {
	private AtomicInteger currentAmmo;
	private AtomicLong reloadingStopsAt;
	
	private long lastManagedStateUpdateTimestamp;

	private long lastShotFiredAt;
	private long ejectSpentRoundStartedAt;
	private int shotsInternal;
	private float zoom;
	private AtomicDouble recoil;
	private float fireRate;

	private AtomicReference<State> state;
	
	private AtomicReference<ManagedState> managedState;
	
	private boolean automatic;

	private Queue<ExpirableRenderableState> expirableRenderableStates = new ArrayBlockingQueue<>(100);

	public WeaponClientStorage(ManagedState managedState, State state, int currentAmmo, float zoom, float recoil, float fireRate, boolean automatic) {
		this.currentAmmo = new AtomicInteger(currentAmmo);
		this.reloadingStopsAt = new AtomicLong();
		this.recoil = new AtomicDouble(recoil);
		this.state = new AtomicReference<>(state);
		this.managedState = new AtomicReference<>(managedState);
		this.zoom = zoom;
		this.fireRate = fireRate;
		this.automatic = automatic;
	}

	void setLastShotFiredAt(long lastShotFiredAt) {
		this.lastShotFiredAt = lastShotFiredAt;
	}

	long getLastShotFiredAt() {
		return lastShotFiredAt;
	}

	long getEjectSpentRoundStartedAt() {
		return ejectSpentRoundStartedAt;
	}

	void setEjectSpentRoundStartedAt(long ejectSpentRoundStartedAt) {
		this.ejectSpentRoundStartedAt = ejectSpentRoundStartedAt;
	}

	public State getState() {
		return state.get();
	}

	public void setState(State state) {
		this.state.set(state);
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public AtomicInteger getCurrentAmmo() {
		return currentAmmo;
	}

	public AtomicLong getReloadingStopsAt() {
		return reloadingStopsAt;
	}

	public float getRecoil() {
		return (float) recoil.get();
	}

	public void setRecoil(float recoil) {
		this.recoil.set(recoil);
	}

	public void addShot() {
		setLastShotFiredAt(System.currentTimeMillis());
		
		if (shotsInternal++ == 0) {
			// disposableRenderableStates.add(RenderableState.RECOILED);
		}
		// disposableRenderableStates.add(RenderableState.SHOOTING);
		if(automatic) {
			expirableRenderableStates.add(new ExpirableRenderableState(RenderableState.AUTO_SHOOTING,
					System.currentTimeMillis() + (long) (50f / fireRate), false));
		}
		
		if(!automatic) {
			expirableRenderableStates.add(new ExpirableRenderableState(RenderableState.RECOILED,
					System.currentTimeMillis() + (long) (500f), true));
			expirableRenderableStates.add(new ExpirableRenderableState(RenderableState.SHOOTING,
					System.currentTimeMillis() + (long) (500f), true));
		}
	}

	/**
	 * Retrives next available valid state. Expired states are discarded
	 * internally. Single use states are discarded after the first use.
	 * 
	 * @return
	 */
	public RenderableState getNextDisposableRenderableState() {
		ExpirableRenderableState ers;
		while ((ers = expirableRenderableStates.peek()) != null) {
			if (System.currentTimeMillis() <= ers.expiresAt) {
				if(ers.singleUse) expirableRenderableStates.poll();
				break;
			} else {
				//System.out.println("Discarding expired renderable state " + ers.state);
				expirableRenderableStates.poll();
			}
		}
		return ers != null ? ers.state : RenderableState.NORMAL;
	}

	public int getShots() {
		return shotsInternal;
	}

	public void resetShots() {
		shotsInternal = 0;
	}

}