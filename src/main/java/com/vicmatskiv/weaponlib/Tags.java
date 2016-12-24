package com.vicmatskiv.weaponlib;

import net.minecraft.item.ItemStack;

final class Tags {

	private static final String ZOOM_TAG = "Zoom";
	private static final String ALLOWED_ZOOM_TAG = "AllowedZoom";
	private static final String AIMED_TAG = "Aimed";
	private static final String RECOIL_TAG = "Recoil";
	private static final String ACTIVE_TEXTURE_INDEX_TAG = "ActiveTextureIndex";
	private static final String LASER_ON_TAG = "LaserOn";
	private static final String AMMO_TAG = "Ammo";
	private static final String STATE_TAG = "State";

	static boolean isLaserOn(ItemStack itemStack) {
		if(itemStack == null || itemStack.stackTagCompound == null) return false;
		return itemStack.stackTagCompound.getBoolean(LASER_ON_TAG);
	}

	static void setLaser(ItemStack itemStack, boolean enabled) {
		if(itemStack == null || itemStack.stackTagCompound == null) return;
		itemStack.stackTagCompound.setBoolean(LASER_ON_TAG, enabled);
	}

	static int getAmmo(ItemStack itemStack) {
		if(itemStack == null || itemStack.stackTagCompound == null) return 0;
		return itemStack.stackTagCompound.getInteger(AMMO_TAG);
	}

	static void setAmmo(ItemStack itemStack, int ammo) {
		if(itemStack == null || itemStack.stackTagCompound == null) return;
		itemStack.stackTagCompound.setInteger(AMMO_TAG, ammo);
	}

	static void setAimed(ItemStack itemStack, boolean aimed) {
		if(itemStack == null || itemStack.stackTagCompound == null) return;
		itemStack.stackTagCompound.setBoolean(AIMED_TAG, aimed);
	}
	
	static boolean isAimed(ItemStack itemStack) {
		if(itemStack == null || itemStack.stackTagCompound == null) return false;
		return itemStack.stackTagCompound.getBoolean(Tags.AIMED_TAG);
	}
	
	static float getAllowedZoom(ItemStack itemStack) {
		if (itemStack == null || itemStack.getTagCompound() == null) {
			return 0f;
		}
		return itemStack.getTagCompound().getFloat(ALLOWED_ZOOM_TAG);
	}

	static void setAllowedZoom(ItemStack itemStack, float zoom) {
		if (itemStack == null || itemStack.getTagCompound() == null) {
			return;
		}
		itemStack.getTagCompound().setFloat(ALLOWED_ZOOM_TAG, zoom);
	}

	static float getZoom(ItemStack itemStack) {
		if(itemStack == null || itemStack.stackTagCompound == null) return 0f;
		return itemStack.stackTagCompound.getFloat(ZOOM_TAG);
	}

	static void setZoom(ItemStack itemStack, float zoom) {
		if(itemStack == null || itemStack.stackTagCompound == null) return;
		itemStack.stackTagCompound.setFloat(ZOOM_TAG, zoom);
	}

	static void setActiveTexture(ItemStack itemStack, int currentIndex) {
		if(itemStack == null || itemStack.stackTagCompound == null) return;
		itemStack.stackTagCompound.setInteger(ACTIVE_TEXTURE_INDEX_TAG, currentIndex);
	}
	
	static int getActiveTexture(ItemStack itemStack) {
		if(itemStack == null || itemStack.stackTagCompound == null) return 0;
		return itemStack.stackTagCompound.getInteger(ACTIVE_TEXTURE_INDEX_TAG);
	}

	static void setRecoil(ItemStack itemStack, float recoil) {
		if(itemStack == null || itemStack.stackTagCompound == null) return;
		itemStack.stackTagCompound.setFloat(RECOIL_TAG, recoil);
	}

	static float getRecoil(ItemStack itemStack) {
		if(itemStack == null || itemStack.stackTagCompound == null) return 0f;
		return itemStack.stackTagCompound.getFloat(RECOIL_TAG);
	}

	static Weapon.State getState(ItemStack itemStack) {
		if(itemStack == null || itemStack.stackTagCompound == null) return null;
		return Weapon.State.values()[itemStack.stackTagCompound.getInteger(STATE_TAG)];
	}
	
	static void setState(ItemStack itemStack, Weapon.State state) {
		if(itemStack == null || itemStack.stackTagCompound == null) return;
		itemStack.stackTagCompound.setInteger(STATE_TAG, state.ordinal());
	}
}
