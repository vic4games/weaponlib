package com.vicmatskiv.weaponlib.vehicle;

import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class StatefulRenderers {

	@SideOnly(Side.CLIENT)
    public static <State> StatefulRenderer<State> createLeftHandRenderer(ModelBiped model, Function<PartRenderContext<State>, Entity> entitySupplier) {
        return new LeftHandRenderer<>(model, entitySupplier);
    }
    
	@SideOnly(Side.CLIENT)
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
                    
                 // start armor render
                    GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                    EntityPlayer player = (EntityPlayer) entity;
                    ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                    if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
                        render.bindTexture(CompatibleWeaponRenderer.getArmorResource(player, itemstack, EntityEquipmentSlot.CHEST, null));
                        
                        ModelBiped armorModel = CompatibleWeaponRenderer.getArmorModelHook(player, itemstack, EntityEquipmentSlot.CHEST, null);
                        if(armorModel != null) {
                        	
                        	armorModel.bipedLeftArm.rotateAngleX = armorModel.bipedLeftArm.rotateAngleY = armorModel.bipedLeftArm.rotateAngleZ = 0f;
                            CompatibleWeaponRenderer.renderLeftVehicleArm(armorModel,(AbstractClientPlayer) player);
                        }
                    }
                    
                    
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
                    
                   
                    
                    
                    
                    // start armor renderer
                    GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                    EntityPlayer player = (EntityPlayer) entity;
                    ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                    if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
                        render.bindTexture(CompatibleWeaponRenderer.getArmorResource(player, itemstack, EntityEquipmentSlot.CHEST, null));                       
                        ModelBiped armorModel = CompatibleWeaponRenderer.getArmorModelHook(player, itemstack, EntityEquipmentSlot.CHEST, null);
                        if(armorModel != null) {
                        	armorModel.bipedRightArm.rotateAngleX = armorModel.bipedRightArm.rotateAngleY = armorModel.bipedRightArm.rotateAngleZ = 0f;
                            CompatibleWeaponRenderer.renderVehicleRightArm(armorModel,(AbstractClientPlayer) player);
                        }
                    }
                    // finish armor rendering
                    
                    
                }
            }
        }
    }
}
