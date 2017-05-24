package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.network.TypeRegistry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;

public final class Tags {

	private static final String AMMO_TAG = "Ammo";

	private static final String DEFAULT_TIMER_TAG = "DefaultTimer";

	private static final String INSTANCE_TAG = "Instance";

	static int getAmmo(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return 0;
		return compatibility.getTagCompound(itemStack).getInteger(AMMO_TAG);
	}

	static void setAmmo(ItemStack itemStack, int ammo) {
		if(itemStack == null) return;
		compatibility.ensureTagCompound(itemStack);
		compatibility.getTagCompound(itemStack).setInteger(AMMO_TAG, ammo);
	}

	public static long getDefaultTimer(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return 0;
		return compatibility.getTagCompound(itemStack).getLong(DEFAULT_TIMER_TAG);
	}

	static void setDefaultTimer(ItemStack itemStack, long ammo) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return;
		compatibility.getTagCompound(itemStack).setLong(DEFAULT_TIMER_TAG, ammo);
	}

	public static PlayerItemInstance<?> getInstance(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return null;

		byte[] bytes = compatibility.getTagCompound(itemStack).getByteArray(INSTANCE_TAG);
		if(bytes != null && bytes.length > 0) {
			return TypeRegistry.getInstance().fromBytes(Unpooled.wrappedBuffer(bytes));
		}
		return null;
	}

	public static <T extends PlayerItemInstance<?>> T getInstance(ItemStack itemStack, Class<T> targetClass) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return null;

		byte[] bytes = compatibility.getTagCompound(itemStack).getByteArray(INSTANCE_TAG);
		if(bytes != null && bytes.length > 0) {
			try {
				return targetClass.cast(TypeRegistry.getInstance().fromBytes(Unpooled.wrappedBuffer(bytes)));
			} catch(RuntimeException e) {
				return null;
			}
		}
		return null;
	}

	static void setInstance(ItemStack itemStack, PlayerItemInstance<?> instance) {
		if(itemStack == null) return;
		compatibility.ensureTagCompound(itemStack);
		ByteBuf buf = Unpooled.buffer();
		if(instance != null) {
			TypeRegistry.getInstance().toBytes(instance, buf);
			compatibility.getTagCompound(itemStack).setByteArray(INSTANCE_TAG, buf.array());
		} else {
			compatibility.getTagCompound(itemStack).removeTag(INSTANCE_TAG);
		}

	}

	public static byte[] getInstanceBytes(ItemStack itemStack) {
		if(itemStack == null || compatibility.getTagCompound(itemStack) == null) return null;
		return compatibility.getTagCompound(itemStack).getByteArray(INSTANCE_TAG);
	}
}
