package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.state.StateContext;

public abstract class CommonWeaponAspectContext extends PlayerContext implements StateContext {
		
	public CommonWeaponAspectContext() {}
	
	public CommonWeaponAspectContext(WeaponClientStorage weaponClientStorage) {
		setManagedStateContainer(weaponClientStorage);
	}
	
	public abstract Weapon getWeapon();
}
