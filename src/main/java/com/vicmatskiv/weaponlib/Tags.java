package com.vicmatskiv.weaponlib;

import net.minecraft.item.ItemStack;

final class Tags {

	private static final String ZOOM_TAG = "Zoomed";
	private static final String AIMED_TAG = "Aimed";
	private static final String RECOIL_TAG = "Recoil";
	private static final String ACTIVE_TEXTURE_INDEX_TAG = "ActiveTextureIndex";
	private static final String LASER_ON_TAG = "LaserOn";
	private static final String AMMO_TAG = "Ammo";
	private static final String STATE_TAG = "State";

	static boolean isLaserOn(ItemStack itemStack) {
		if (itemStack.getTagCompound() == null) {
			return false;
		}
		return itemStack.getTagCompound().getBoolean(LASER_ON_TAG);
	}

	static void setLaser(ItemStack itemStack, boolean enabled) {
		itemStack.getTagCompound().setBoolean(LASER_ON_TAG, enabled);
	}

	static int getAmmo(ItemStack itemStack) {
		return itemStack.getTagCompound().getInteger(AMMO_TAG);
	}

	static void setAmmo(ItemStack itemStack, int ammo) {
		itemStack.getTagCompound().setInteger(AMMO_TAG, ammo);
	}

	static void setAimed(ItemStack itemStack, boolean aimed) {
		itemStack.getTagCompound().setBoolean(AIMED_TAG, aimed);
	}
	
	static boolean isAimed(ItemStack itemStack) {
		return itemStack.getTagCompound().getBoolean(Tags.AIMED_TAG);
	}

	static float getZoom(ItemStack itemStack) {
		return itemStack.getTagCompound().getFloat(ZOOM_TAG);
	}

	static void setZoom(ItemStack itemStack, float zoom) {
		itemStack.getTagCompound().setFloat(ZOOM_TAG, zoom);
	}

	static void setActiveTexture(ItemStack itemStack, int currentIndex) {
		itemStack.getTagCompound().setInteger(ACTIVE_TEXTURE_INDEX_TAG, currentIndex);
	}
	
	static int getActiveTexture(ItemStack itemStack) {
		return itemStack.getTagCompound().getInteger(ACTIVE_TEXTURE_INDEX_TAG);
	}

	static void setRecoil(ItemStack itemStack, float recoil) {
		itemStack.getTagCompound().setFloat(RECOIL_TAG, recoil);
	}

	static float getRecoil(ItemStack itemStack) {
		return itemStack.getTagCompound().getFloat(RECOIL_TAG);
	}

	static Weapon.State getState(ItemStack itemStack) {
		return Weapon.State.values()[itemStack.getTagCompound().getInteger(STATE_TAG)];
	}
	
	static void setState(ItemStack itemStack, Weapon.State state) {
		itemStack.getTagCompound().setInteger(STATE_TAG, state.ordinal());
	}
}
