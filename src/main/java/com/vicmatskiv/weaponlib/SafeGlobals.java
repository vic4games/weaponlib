package com.vicmatskiv.weaponlib;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.util.MovingObjectPosition;

public class SafeGlobals {

	public final AtomicBoolean guiOpen = new AtomicBoolean();
	
	public final AtomicInteger currentItemIndex = new AtomicInteger(-1);
	
	public final AtomicReference<MovingObjectPosition> objectMouseOver = new AtomicReference<>();
}
