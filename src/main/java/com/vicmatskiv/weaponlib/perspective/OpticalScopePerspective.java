package com.vicmatskiv.weaponlib.perspective;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.ItemScope;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;
import com.vicmatskiv.weaponlib.shader.DynamicShaderContext;
import com.vicmatskiv.weaponlib.shader.DynamicShaderPhase;

public class OpticalScopePerspective extends FirstPersonPerspective<RenderableState> {

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    public OpticalScopePerspective() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }
    
    @Override
    public void activate(ClientModContext modContext, PerspectiveManager manager) {
        PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
        if(instance != null) {
            ItemScope scope = instance.getScope();
            if(scope.isOptical()) {
                setSize(scope.getWidth(), scope.getHeight());
            }
        }
        super.activate(modContext, manager);
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
        } else if(progress > 0f && aimed && isAimingState(renderContext.getToState())) {
            brightness = progress;
        } else if(isAimingState(renderContext.getFromState()) && progress > 0f && !aimed) {
            brightness = Math.max(1 - progress, 0f);
        }
        return brightness;
    }

    private static boolean isAimingState(RenderableState renderableState) {
        return renderableState == RenderableState.ZOOMING
                || renderableState == RenderableState.ZOOMING_RECOILED
                || renderableState == RenderableState.ZOOMING_SHOOTING
                ;
    }

    @Override
    public void update(CompatibleRenderTickEvent event) {
        PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
        if(instance != null && instance.isAimed()) {
            ItemScope scope = instance.getScope();
            if(scope.isOptical()) {
                setSize(scope.getWidth(), scope.getHeight());
            }
            super.update(event);
        }
    }

    @Override
    protected void prepareRenderWorld(CompatibleRenderTickEvent event) {
        DynamicShaderContext shaderContext = new DynamicShaderContext(
                DynamicShaderPhase.POST_WORLD_OPTICAL_SCOPE_RENDER,
                this.entityRenderer,
                framebuffer,
                event.getRenderTickTime());
        PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
        shaderGroupManager.applyShader(shaderContext, instance);
    }

    @Override
    protected void postRenderWorld(CompatibleRenderTickEvent event) {
        DynamicShaderContext shaderContext = new DynamicShaderContext(
                DynamicShaderPhase.POST_WORLD_OPTICAL_SCOPE_RENDER,
                this.entityRenderer,
                framebuffer,
                event.getRenderTickTime());
        shaderGroupManager.removeStaleShaders(shaderContext); // this is probably not the right place
    }
}
