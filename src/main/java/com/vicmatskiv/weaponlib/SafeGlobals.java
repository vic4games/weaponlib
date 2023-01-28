package com.vicmatskiv.weaponlib;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;

public class SafeGlobals {

	public final AtomicBoolean guiOpen = new AtomicBoolean();
	
	public final AtomicInteger currentItemIndex = new AtomicInteger(-1);
	
	public final AtomicReference<CompatibleRayTraceResult> objectMouseOver = new AtomicReference<>();
	
	public final AtomicReference<RenderingPhase> renderingPhase = new AtomicReference<>();
}
