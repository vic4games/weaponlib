package com.vicmatskiv.weaponlib.state;

import java.util.function.BiConsumer;

public interface PermitManager<Context> {
	
	public <T extends Permit> void request(T permit, Context context, BiConsumer<Permit, Context> callback);
	
	public <T extends Permit> void registerEvaluator(Class<T> permitClass, BiConsumer<T, Context> evaluator);
}