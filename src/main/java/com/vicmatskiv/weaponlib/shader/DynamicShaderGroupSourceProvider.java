package com.vicmatskiv.weaponlib.shader;

public interface DynamicShaderGroupSourceProvider {

    public DynamicShaderGroupSource getShaderSource(DynamicShaderPhase phase);
}
