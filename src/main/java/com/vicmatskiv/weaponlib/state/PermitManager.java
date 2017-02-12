package com.vicmatskiv.weaponlib.state;

import java.util.function.BiConsumer;

public interface PermitManager<Context> {
	
	public void request(Permit permit, Context context, BiConsumer<Permit, Context> callback);
}