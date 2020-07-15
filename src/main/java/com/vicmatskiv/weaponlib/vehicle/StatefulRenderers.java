package com.vicmatskiv.weaponlib.vehicle;

import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;

public class StatefulRenderers {
    
    public static <State> StatefulRenderer<State> createLeftHandRenderer(ModelBiped model, Function<PartRenderContext<State>, Entity> entitySupplier) {
        return new LeftHandRenderer<>(model, entitySupplier);
    }
    
    public static <State> StatefulRenderer<State> createRightHandRenderer(ModelBiped model, Function<PartRenderContext<State>, Entity> entitySupplier) {
        return new RightHandRenderer<>(model, entitySupplier);
    }
    
    private static class LeftHandRenderer<State> implements StatefulRenderer<State> {
        
        //private ModelBiped model;
        private Function<PartRenderContext<State>, Entity> entitySupplier;
        
        public LeftHandRenderer(ModelBiped model, Function<PartRenderContext<State>, Entity> entitySupplier) {
            //this.model = model;
            this.entitySupplier = entitySupplier;
        }

        @Override
        public void render(PartRenderContext<State> context) {
            Entity entity = entitySupplier.apply(context);
            if(entity != null) {
                Minecraft minecraft = Minecraft.getMinecraft();
                if(minecraft.gameSettings.thirdPersonView == 0) {
                    minecraft.getTextureManager().bindTexture(((AbstractClientPlayer) entity).getLocationSkin());

                    Render<AbstractClientPlayer> entityRenderObject = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((AbstractClientPlayer)entity);
                    RenderPlayer render = (RenderPlayer) entityRenderObject;

                    ModelBiped model = render.getMainModel();

                    GL11.glColor3f(1F, 1F, 1F);
                    model.isSneak = false;
//                    model.onGround = 0.0F;
                    model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entity);
                    model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleY = model.bipedLeftArm.rotateAngleZ = 0f;
                    model.bipedLeftArm.render(0.0625F);
                }
            }
        }
    }
    
    private static class RightHandRenderer<State> implements StatefulRenderer<State> {
        
        //private ModelBiped model;
        private Function<PartRenderContext<State>, Entity> entitySupplier;
        
        public RightHandRenderer(ModelBiped model, Function<PartRenderContext<State>, Entity> entitySupplier) {
            //this.model = model;
            this.entitySupplier = entitySupplier;
        }

        @Override
        public void render(PartRenderContext<State> context) {
            Entity entity = entitySupplier.apply(context);
            if(entity != null) {
                Minecraft minecraft = Minecraft.getMinecraft();
                if(minecraft.gameSettings.thirdPersonView == 0) {
                    minecraft.getTextureManager().bindTexture(((AbstractClientPlayer) entity).getLocationSkin());

                    Render<AbstractClientPlayer> entityRenderObject = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((AbstractClientPlayer)entity);
                    RenderPlayer render = (RenderPlayer) entityRenderObject;

                    ModelBiped model = render.getMainModel();
                    
                    GL11.glColor3f(1F, 1F, 1F);
                    model.isRiding = false;
                    model.isSneak = false;
//                    model.onGround = 0.0F;
//                    model.heldItemLeft = 0;
//                    model.aimedBow = false;
                    model.setRotationAngles(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 0.0625F, entity);
                    model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleY = model.bipedRightArm.rotateAngleZ = 0f;
                    model.bipedRightArm.render(0.0625F);
                }
            }
        }
    }
}
