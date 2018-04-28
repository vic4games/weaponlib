package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;

public class PlayerTransitionProvider implements MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>>  {

    public static class Builder {

        private List<MultipartTransition<Part, RenderContext<RenderableState>>> proningTransitions =
                new ArrayList<>();

        public Builder withProningTransition(MultipartTransition<Part, RenderContext<RenderableState>> transition) {
            proningTransitions.add(transition);
            return this;
        }

        public PlayerTransitionProvider build() {
            return new PlayerTransitionProvider(this);
        }
    }

    //    private List<MultipartTransition<Part, RenderContext<RenderableState>>> proningTransitions = 
    //            Arrays.asList(
    //
    //                    new MultipartTransition<Part, RenderContext<RenderableState>>(
    //                            Part.MAIN, renderContext -> {
    //                                GL11.glTranslatef(0f, 1.3f, -0.5f);
    //                                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0F);
    //                                GL11.glRotatef(-10.0F, 0.0F, 1.0F, 0F);
    //
    //                            }, 200, 0)
    //                    .withPartPositionFunction(Part.LEFT_HAND, rc -> {
    //                        GL11.glRotatef(20.0F, 1.0F, 1.0F, 0.0F);
    //                    })
    //                    .withPartPositionFunction(Part.RIGHT_HAND, rc -> {
    //                        GL11.glRotatef(20.0F, 1.0F, 0.0F, 1.0F);
    //                    })
    //                    .withPartPositionFunction(Part.LEFT_LEG, rc -> {
    //                        GL11.glRotatef(-10.0F, 0.0F, 1.0F, 1.0F);
    //                    })
    //                    .withPartPositionFunction(Part.RIGHT_LEG, rc -> {
    //                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
    //                    })
    //                    ,
    //
    //                    new MultipartTransition<Part, RenderContext<RenderableState>>(
    //                            Part.MAIN, renderContext -> {
    //
    //                                GL11.glTranslatef(0f, 1.3f, -0.5f);
    //                                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0F);
    //                                GL11.glRotatef(10.0F, 0.0F, 1.0F, 0F);
    //                            }, 200, 0)
    //                    .withPartPositionFunction(Part.LEFT_HAND, rc -> {
    //                        GL11.glRotatef(-20.0F, 1.0F, 1.0F, 0.0F);
    //                    })
    //                    .withPartPositionFunction(Part.RIGHT_HAND, rc -> {
    //                        GL11.glRotatef(-20.0F, 1.0F, 0.0F, 1.0F);
    //                    })
    //                    .withPartPositionFunction(Part.LEFT_LEG, rc -> {
    //                        GL11.glRotatef(10.0F, 0.0F, 1.0F, 1.0F);
    //                    })
    //                    .withPartPositionFunction(Part.RIGHT_LEG, rc -> {
    //                        GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
    //                    })
    //                    );
    
    
    private List<MultipartTransition<Part, RenderContext<RenderableState>>> normalTransitions = 
            Arrays.asList(
                    new MultipartTransition<Part, RenderContext<RenderableState>>(
                            Part.MAIN, renderContext -> {}, 200, 0)
                    .withPartPositionFunction(Part.LEFT_HAND, rc -> {})
                    .withPartPositionFunction(Part.RIGHT_HAND, rc -> {})
                    .withPartPositionFunction(Part.LEFT_LEG, rc -> {})
                    .withPartPositionFunction(Part.RIGHT_LEG, rc -> {})
                    );

    private Builder builder;

    public PlayerTransitionProvider(Builder builder) {
        this.builder = builder;
    }

    @Override
    public List<MultipartTransition<Part, RenderContext<RenderableState>>> getTransitions(RenderableState state) {
        if(state == RenderableState.PRONING) {
            return builder.proningTransitions;
        } else {
            return normalTransitions;
        }
    }
}