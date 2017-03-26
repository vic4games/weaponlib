package com.vicmatskiv.weaponlib.perspective;

import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;

public class OpticalScopePerspective extends FirstPersonPerspective<RenderableState> {
    
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 200;
    
    public OpticalScopePerspective() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    @Override
    public float getBrightness(RenderContext<RenderableState> renderContext) {
        float brightness = 0f;
        PlayerWeaponInstance instance = renderContext.getWeaponInstance();
        if(instance == null) {
            return 0f;
        }
        boolean aimed = instance != null && instance.isAimed();
        float progress = Math.min(1f, renderContext.getTransitionProgress());

        if(isAimingState(renderContext.getFromState()) && isAimingState(renderContext.getToState())) {
            brightness = 1f;
        } else if(progress > 0f && aimed) {
            brightness = progress;
        } else if(isAimingState(renderContext.getFromState()) && progress > 0f && !aimed) {
            brightness = Math.max(1 - progress, 0f);
        }
        return brightness;
    }
    
    private static boolean isAimingState(RenderableState renderableState) {
        return renderableState == RenderableState.ZOOMING
                || renderableState == RenderableState.ZOOMING_RECOILED
                || renderableState == RenderableState.ZOOMING_SHOOTING;
    }
    
    @Override
    public void update(CompatibleRenderTickEvent event) {
        PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
        if(instance != null && instance.isAimed()) {
            super.update(event);
        }
    }
}
