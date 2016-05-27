package com.vicmatskiv.weaponlib;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class ModelWithAttachments extends ModelBase {
	
	/*
	public static class Attachment {
		private ModelBase model;
		private String textureName;
		public Attachment(ModelBase model, String textureName) {
			this.model = model;
			this.textureName = textureName;
		}
	}
	
	private Map<Class<? extends ModelBase>, ItemAttachment> compatibleAttachments = new HashMap<>();
	*/


	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	public void renderAttachments(String modId, List<CompatibleAttachment<Weapon>> attachments, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null) {
				for(Tuple<ModelBase, String> texturedModel: compatibleAttachment.getAttachment().getTexturedModels()) {
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(modId 
							+ ":textures/models/" + texturedModel.getV()));
					GL11.glPushMatrix();
					if(compatibleAttachment.getPositioning() != null) {
						compatibleAttachment.getPositioning().accept(texturedModel.getU());
					}
					texturedModel.getU().render(entity, f, f1, f2, f3, f4, f5);
					GL11.glPopMatrix();
				}
			}
		}
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

		/*
		for(ModelBase attachmentModel: attachments) {
			attachmentModel.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		}*/
	}
}
