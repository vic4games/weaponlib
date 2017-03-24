package com.vicmatskiv.weaponlib.perspective;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;

public interface Perspective<S> {

    public void activate(ClientModContext modContext);

    public void deactivate(ClientModContext modContext);

    public float getBrightness(RenderContext<S> context);

    public int getTexture(RenderContext<S> context);

    public void update(CompatibleRenderTickEvent event);
}
