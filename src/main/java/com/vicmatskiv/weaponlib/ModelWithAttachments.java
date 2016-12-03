package com.vicmatskiv.weaponlib;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("deprecation")
public class ModelWithAttachments extends ModelBase {
	

	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	public void renderAttachments(String modId, ItemStack itemStack, TransformType type, List<CompatibleAttachment<Weapon>> attachments, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null) {
				for(Tuple<ModelBase, String> texturedModel: compatibleAttachment.getAttachment().getTexturedModels()) {
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(modId 
							+ ":textures/models/" + texturedModel.getV()));
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					if(compatibleAttachment.getPositioning() != null) {
						compatibleAttachment.getPositioning().accept(texturedModel.getU());
					}
					texturedModel.getU().render(entity, f, f1, f2, f3, f4, f5);
					
					CustomRenderer postRenderer = compatibleAttachment.getAttachment().getPostRenderer();
					if(postRenderer != null) {
						postRenderer.render(type, itemStack);
					}
					GL11.glPopAttrib();
					GL11.glPopMatrix();

				}
			}
		}
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}
}
