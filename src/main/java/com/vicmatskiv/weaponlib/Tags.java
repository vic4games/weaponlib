package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.state.ManagedState;

import net.minecraft.item.ItemStack;

public final class Tags {

	private static final String ZOOM_TAG = "Zoom";
	private static final String ZOOM_MODE_TAG = "ZoomMode";
	private static final String ALLOWED_ZOOM_TAG = "AllowedZoom";
	private static final String AIMED_TAG = "Aimed";
	private static final String RECOIL_TAG = "Recoil";
	private static final String ACTIVE_TEXTURE_INDEX_TAG = "ActiveTextureIndex";
	private static final String LASER_ON_TAG = "LaserOn";
	private static final String AMMO_TAG = "Ammo";
	private static final String STATE_TAG = "State";
	private static final String MANAGED_STATE_TAG = "ManagedState";
	private static final String DEFAULT_TIMER_TAG = "DefaultTimer";

	static boolean isLaserOn(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return false;
		return compatibility.getTagCompound(itemStack).getBoolean(LASER_ON_TAG);
	}

	static void setLaser(ItemStack itemStack, boolean enabled) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setBoolean(LASER_ON_TAG, enabled);
	}

	public static int getAmmo(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return 0;
		return compatibility.getTagCompound(itemStack).getInteger(AMMO_TAG);
	}

	static void setAmmo(ItemStack itemStack, int ammo) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setInteger(AMMO_TAG, ammo);
	}

	static void setAimed(ItemStack itemStack, boolean aimed) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setBoolean(AIMED_TAG, aimed);
	}
	
	static boolean isAimed(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return false;
		return compatibility.getTagCompound(itemStack).getBoolean(Tags.AIMED_TAG);
	}
	
	static float getAllowedZoom(ItemStack itemStack) {
		if (itemStack == null || itemStack.getTagCompound() == null) {
			return 0f;
		}
		return itemStack.getTagCompound().getFloat(ALLOWED_ZOOM_TAG);
	}

	static void setAllowedZoom(ItemStack itemStack, float zoom) {
		setAllowedZoom(itemStack, zoom, false);
	}
	
	static void setAllowedZoom(ItemStack itemStack, float zoom, boolean attachmentOnlyZoomMode) {
		if (itemStack == null || itemStack.getTagCompound() == null) {
			return;
		}
		itemStack.getTagCompound().setFloat(ALLOWED_ZOOM_TAG, zoom);
		compatibility.getTagCompound(itemStack).setBoolean(ZOOM_MODE_TAG, attachmentOnlyZoomMode);
	}

	static float getZoom(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return 0f;
		return compatibility.getTagCompound(itemStack).getFloat(ZOOM_TAG);
	}
	
	static boolean isAttachmentOnlyZoom(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return false;
		return compatibility.getTagCompound(itemStack).getBoolean(ZOOM_MODE_TAG);
	}

	static void setZoom(ItemStack itemStack, float zoom) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setFloat(ZOOM_TAG, zoom);
		
	}

	static void setActiveTexture(ItemStack itemStack, int currentIndex) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setInteger(ACTIVE_TEXTURE_INDEX_TAG, currentIndex);
	}
	
	static int getActiveTexture(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return 0;
		return compatibility.getTagCompound(itemStack).getInteger(ACTIVE_TEXTURE_INDEX_TAG);
	}

	static void setRecoil(ItemStack itemStack, float recoil) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setFloat(RECOIL_TAG, recoil);
	}

	static float getRecoil(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return 0f;
		return compatibility.getTagCompound(itemStack).getFloat(RECOIL_TAG);
	}

	static Weapon.State getState(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return null;
		return Weapon.State.values()[compatibility.getTagCompound(itemStack).getInteger(STATE_TAG)];
	}
	
	
	static void setState(ItemStack itemStack, Weapon.State state) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setInteger(STATE_TAG, state.ordinal());
	}
	
	static ManagedState getManagedState(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return null;
		return WeaponState.values()[compatibility.getTagCompound(itemStack).getInteger(MANAGED_STATE_TAG)];
	}
	
	public static long getDefaultTimer(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return 0;
		return compatibility.getTagCompound(itemStack).getLong(DEFAULT_TIMER_TAG);
	}

	static void setDefaultTimer(ItemStack itemStack, long ammo) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setLong(DEFAULT_TIMER_TAG, ammo);
	}
}
