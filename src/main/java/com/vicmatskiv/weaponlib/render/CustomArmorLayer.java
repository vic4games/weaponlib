package com.vicmatskiv.weaponlib.render;

import java.util.function.BiConsumer;

import com.vicmatskiv.weaponlib.ItemStorage;
import com.vicmatskiv.weaponlib.ItemVest;
import com.vicmatskiv.weaponlib.KeyBindings;
import com.vicmatskiv.weaponlib.compatibility.CompatibleCustomPlayerInventoryCapability;
import com.vicmatskiv.weaponlib.inventory.CustomPlayerInventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CustomArmorLayer implements LayerRenderer<EntityPlayer> {

	private final RenderLivingBase<?> renderer;

	public CustomArmorLayer(RenderLivingBase<?> rendererIn) {
		this.renderer = rendererIn;
	}

	@Override
	public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!(entitylivingbaseIn instanceof EntityPlayer))
			return;
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
				headPitch, scale, 1);
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
				headPitch, scale, 2);
	}

	private void renderEquipLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, float scale, int index) {
        // Construction
        CustomPlayerInventory capability = CompatibleCustomPlayerInventoryCapability.getInventory(player);
        
        if(capability == null) return;
        
        if(capability.getStackInSlot(0) != null && !capability.getStackInSlot(0).isEmpty()) {
        	ItemStack backpackStack = capability.getStackInSlot(0); 
        	
        	ItemStorage storage = (ItemStorage) backpackStack.getItem();
        	ModelBase model = (ModelBase) storage.getTexturedModels().get(0).getU();
			
        	ResourceLocation resource = new ResourceLocation("mw:textures/models/" + storage.getTexturedModels().get(0).getV());
        	
        	
        	doEquipmentRender(model, player, backpackStack, storage.getCustomEquippedPositioning(), resource, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
        
        if(capability.getStackInSlot(1) != null && !capability.getStackInSlot(1).isEmpty()) {   
        	ItemStack vestStack = capability.getStackInSlot(1); 
        	
        	
        	
        	ItemVest storage = (ItemVest) vestStack.getItem();
        	ModelBase model = (ModelBase) storage.getTexturedModels().get(0).getU();	
			
        	ResourceLocation resource = new ResourceLocation("mw:textures/models/" + storage.getTexturedModels().get(0).getV());
        	
        	doEquipmentRender(model, player, vestStack, storage.getCustomEquippedPositioning(), resource, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
	}

	
	public void doEquipmentRender(ModelBase model, EntityPlayer player, ItemStack itemStack, BiConsumer<EntityPlayer, ItemStack> positioning, ResourceLocation texture, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		
		// Load the correct texture
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    	
    	GlStateManager.pushMatrix();
    	
    	// Apply positioning
    	positioning.accept(player, itemStack);

    	// Set the model attributes & render.
    	model.setModelAttributes(this.renderer.getMainModel());
    	
    	model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
    	
    	model.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
    	model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    	
    	GlStateManager.popMatrix();
	}


	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}
