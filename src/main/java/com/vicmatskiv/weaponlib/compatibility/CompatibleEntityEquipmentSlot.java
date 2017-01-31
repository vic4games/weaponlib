package com.vicmatskiv.weaponlib.compatibility;

public enum CompatibleEntityEquipmentSlot {
	HEAD(0), CHEST(1), FEET(3);

	private int slot;

	private CompatibleEntityEquipmentSlot(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}
}
