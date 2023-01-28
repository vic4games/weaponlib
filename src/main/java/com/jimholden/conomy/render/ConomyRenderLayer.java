package com.jimholden.conomy.render;

import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.FacemaskItem;
import com.jimholden.conomy.items.GlassesItem;
import com.jimholden.conomy.items.HeadsetItem;
import com.jimholden.conomy.items.ItemBodyArmor;
import com.jimholden.conomy.items.ItemJacket;
import com.jimholden.conomy.items.RigItem;
import com.jimholden.conomy.items.models.FastModelLoader;
import com.jimholden.conomy.items.models.headset.HeadsetModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;




public class ConomyRenderLayer implements LayerRenderer<EntityPlayer> {

	private final RenderLivingBase<?> renderer;
	
	 public ConomyRenderLayer(RenderLivingBase<?> rendererIn)
	    {
	        this.renderer = rendererIn;
	    }
	
	@Override
	public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if(!(entitylivingbaseIn instanceof EntityPlayer)) return; 
		if(!entitylivingbaseIn.inventory.getStackInSlot(0).isEmpty()) {
		     ItemStack itemstack2 = entitylivingbaseIn.inventory.getStackInSlot(0);
	         renderItem(itemstack2);
		}
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 1);
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 2);
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 3);
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 4);
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 5);
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 6);
		this.renderEquipLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 7);
		
	}
	
	private void renderEquipLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, int index)
    {
		
		ItemStack itemstack = player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(index);
        //ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);
		//System.out.println("It is tick ID: " + Minecraft.getMinecraft().getRenderPartialTicks() + " | It is rendered!");
		

		
		if(itemstack.getItem() instanceof ItemJacket) {
			ItemJacket rig = (ItemJacket) itemstack.getItem();
			
			ModelBiped biped = rig.getModel();
            Minecraft.getMinecraft().renderEngine.bindTexture(rig.getTex());
            biped.setModelAttributes(this.renderer.getMainModel());

            biped.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            biped.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            return;
		}
		
		
		if(itemstack.getItem() instanceof ItemBodyArmor) {
			ItemBodyArmor rig = (ItemBodyArmor) itemstack.getItem();
			
			ModelBiped biped = rig.getModel();
            Minecraft.getMinecraft().renderEngine.bindTexture(rig.getTex());
            biped.setModelAttributes(this.renderer.getMainModel());

            biped.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            biped.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            
       
            
            
            
            return;
		}
		

		
		
		if(itemstack.getItem() instanceof RigItem) {
			//long fl1 = System.nanoTime();
			RigItem rig = (RigItem) itemstack.getItem();
			//long fl2 = System.nanoTime();
			//System.out.println("Itemstack Fetch: " + (fl2-fl1) + "ns");
			
			long fl3 = System.nanoTime();
			
			//ModelBiped biped = rig.getArmorModel(player, itemstack, EntityEquipmentSlot.CHEST, (ModelBiped) this.renderer.getMainModel());
			
			
			ModelBiped biped; 
			if(itemstack != FastModelLoader.oldChestStack) {
				biped = rig.getModel();
				FastModelLoader.chestModel = biped;
				FastModelLoader.oldChestStack = itemstack;
			} else {
				biped = FastModelLoader.chestModel;
			}
			
			
			long fl4 = System.nanoTime();
			
			//System.out.println("Get Model: " + (fl4-fl3) + "ns");
			
			//long fl5 = System.nanoTime();
            Minecraft.getMinecraft().renderEngine.bindTexture(rig.getTex());
            biped.setModelAttributes(this.renderer.getMainModel());
            //long fl6 = System.nanoTime();
            
           // System.out.println("bind & get main model: " + (fl6-fl5) + "ns");

            //long fl7 = System.nanoTime();
            
            
            //long fl1 = System.nanoTime();
            biped.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            //long fl2 = System.nanoTime();
            //System.out.println("Living Anims: " + (fl2-fl1) + "ns");
            
            
            //long fl3 = System.nanoTime();
            biped.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            //long fl4 = System.nanoTime();
           // System.out.println("Render: " + (fl4-fl3) + "ns");
            
            
           // long fl8 = System.nanoTime();
          //  System.out.println("living anim + render: " + (fl8-fl7) + "ns");
            return;
		}

		
		if(itemstack.getItem() instanceof BackpackItem) {
			BackpackItem itemarmor = (BackpackItem)itemstack.getItem();
            
            ModelBiped biped = itemarmor.getModel();
            Minecraft.getMinecraft().renderEngine.bindTexture(itemarmor.getTex());
            biped.setModelAttributes(this.renderer.getMainModel());

            biped.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            biped.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            return;
			
		}
		
	
		

		if(itemstack.getItem() instanceof FacemaskItem) {
			FacemaskItem itemarmor = (FacemaskItem)itemstack.getItem();
            
            ModelBiped biped = itemarmor.getModel();
            Minecraft.getMinecraft().renderEngine.bindTexture(itemarmor.getTex());
            biped.setModelAttributes(this.renderer.getMainModel());

            biped.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            biped.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            return;
			
		}
		
		
		
		if(itemstack.getItem() instanceof GlassesItem) {
			GlassesItem itemarmor = (GlassesItem)itemstack.getItem();
            
            ModelBiped biped = itemarmor.getArmorModel(player, itemstack, EntityEquipmentSlot.HEAD, (ModelBiped) this.renderer.getMainModel());
            Minecraft.getMinecraft().renderEngine.bindTexture(itemarmor.getTex());
            biped.setModelAttributes(this.renderer.getMainModel());

            biped.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            biped.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            return;
			
		}
		
		
        if (itemstack.getItem() instanceof HeadsetItem)
        {
            HeadsetItem itemarmor = (HeadsetItem)itemstack.getItem();
            
            ModelBiped biped = itemarmor.getModel();
            Minecraft.getMinecraft().renderEngine.bindTexture(itemarmor.getTex());
            biped.setModelAttributes(this.renderer.getMainModel());

            biped.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            biped.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            return;
            /*
            if (itemarmor.getEquipmentSlot() == slotIn)
            {
                T t = this.getModelFromSlot(slotIn);
                t = getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, t);
                t.setModelAttributes(this.renderer.getMainModel());
                t.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
                this.setModelSlotVisible(t, slotIn);
                boolean flag = this.isLegSlot(slotIn);
                this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, null));

                {
                    if (itemarmor.hasOverlay(itemstack)) // Allow this for anything, not only cloth
                    {
                        int i = itemarmor.getColor(itemstack);
                        float f = (float)(i >> 16 & 255) / 255.0F;
                        float f1 = (float)(i >> 8 & 255) / 255.0F;
                        float f2 = (float)(i & 255) / 255.0F;
                        GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
                        t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                        this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, "overlay"));
                    }
                    { // Non-colored
                        GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                        t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    } // Default
                }
            }*/
        }
    }

	public void renderItem(ItemStack stack) {
		RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.pushMatrix();
        // Translate to the center of the block and .9 points higher
        GlStateManager.translate(-0.15, 0.4, .15);
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        //GlStateManager.rotate(-8, 1, 0, 0);
        GlStateManager.rotate(35, 0, 0, 1);
       GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(90, 0, 1, 0);
        //atm.getBlockType().getBlockState().getBaseState().getValue(BlockHorizontal.FACING);
      //  atm.getBlockType().getStateFromMeta(atm.getBlockMetadata()).getValue(BlockHorizontal.FACING);
        //
        
        //GlStateManager.rotate(90, x, y, z);
        

        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);

        GlStateManager.popMatrix();
		
	}
	
	@Override
	public boolean shouldCombineTextures() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
