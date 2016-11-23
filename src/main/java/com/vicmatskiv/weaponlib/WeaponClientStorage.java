package com.vicmatskiv.weaponlib;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.util.concurrent.AtomicDouble;
import com.vicmatskiv.weaponlib.Weapon.WeaponInstanceState;

final class WeaponClientStorage {
	private AtomicInteger currentAmmo;
	private AtomicLong reloadingStopsAt;

	private long lastShotFiredAt;
	private int shotsInternal;
	private float zoom;
	private AtomicDouble recoil;
	private float fireRate;

	private AtomicReference<WeaponInstanceState> state;

	private int recoilableShotCount;
	private boolean recoiledForCurrentShot;

	private Queue<ExpirableRenderableState> disposableRenderableStates = new ArrayBlockingQueue<>(100);

	public WeaponClientStorage(WeaponInstanceState state, int currentAmmo, float zoom, float recoil, float fireRate) {
		this.currentAmmo = new AtomicInteger(currentAmmo);
		this.reloadingStopsAt = new AtomicLong();
		this.recoil = new AtomicDouble(recoil);
		this.state = new AtomicReference<>(state);
		this.zoom = zoom;
		this.fireRate = fireRate;
	}

	public void setLastShotFiredAt(long lastShotFiredAt) {
		this.lastShotFiredAt = lastShotFiredAt;
	}

	public long getLastShotFiredAt() {
		return lastShotFiredAt;
	}

	public WeaponInstanceState getState() {
		return state.get();
	}

	public void setState(WeaponInstanceState state) {
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

	public synchronized void addRecoilableShot() {
		if (recoilableShotCount < 0) {
			recoilableShotCount = 0;
		}
		if (recoilableShotCount == 0) {
			recoiledForCurrentShot = false;
		}
		recoilableShotCount++;
	}

	public synchronized boolean hasRecoiled() {
		return recoiledForCurrentShot;
	}

	public synchronized boolean checkIfNotRecoiledAndRecoil() {
		if (recoilableShotCount > 0) {
			if (!recoiledForCurrentShot) {
				recoilableShotCount--;
				recoiledForCurrentShot = true;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public synchronized void resetRecoiled() {
		recoiledForCurrentShot = false;
	}

	public void addShot() {
		if (shotsInternal++ == 0) {
			// disposableRenderableStates.add(RenderableState.RECOILED);
		}
		// disposableRenderableStates.add(RenderableState.SHOOTING);
		disposableRenderableStates.add(new ExpirableRenderableState(RenderableState.SHOOTING,
				System.currentTimeMillis() + (long) (50f / fireRate)));
	}

	/**
	 * Retrives next available valid state. Expired states are discarded
	 * internally. Single use states are discarded after the first use.
	 * 
	 * @return
	 */
	public RenderableState getNextDisposableRenderableState() {
		ExpirableRenderableState ers;
		while ((ers = disposableRenderableStates.peek()) != null) {
			if (System.currentTimeMillis() <= ers.expiresAt) {
				// if(ers.singleUse) disposableRenderableStates.poll();
				break;
			} else {
				disposableRenderableStates.poll();
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

	// public void resetDisposableRenderableQueue() {
	// disposableRenderableStates.clear();
	// }
}