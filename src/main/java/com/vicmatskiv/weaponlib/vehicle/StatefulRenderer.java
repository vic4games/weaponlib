package com.vicmatskiv.weaponlib.vehicle;

public interface StatefulRenderer<State> {
    
    public void render(PartRenderContext<State> context);
}
