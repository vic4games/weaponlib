package com.vicmatskiv.weaponlib;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class StaticModelSourceRenderer implements IItemRenderer {

	public static class Builder {
		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
		
		private String modId;
		
		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withEntityPositioning(Consumer<ItemStack> entityPositioning) {
			this.entityPositioning = entityPositioning;
			return this;
		}
		
		public Builder withInventoryPositioning(Consumer<ItemStack> inventoryPositioning) {
			this.inventoryPositioning = inventoryPositioning;
			return this;
		}

		public Builder withThirdPersonPositioning(BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning) {
			this.thirdPersonPositioning = thirdPersonPositioning;
			return this;
		}

		public Builder withFirstPersonPositioning(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning) {
			this.firstPersonPositioning = firstPersonPositioning;
			return this;
		}

		public StaticModelSourceRenderer build() {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			
			if(inventoryPositioning == null) {
				inventoryPositioning = itemStack -> {GL11.glTranslatef(0,  0.12f, 0);};
			}
			
			if(entityPositioning == null) {
				entityPositioning = itemStack -> {
				};
			}
			
			if(firstPersonPositioning == null) {
				firstPersonPositioning = (player, itemStack) -> {
				};
			}
			
			if(thirdPersonPositioning == null) {
				thirdPersonPositioning = (player, itemStack) -> {
					GL11.glTranslatef(-0.4F, 0.2F, 0.4F);
					GL11.glRotatef(-45F, 0f, 1f, 0f);
					GL11.glRotatef(70F, 1f, 0f, 0f);
				};
			}
			
			return new StaticModelSourceRenderer(this);
		}
	}
	
	private Builder builder;
	
	private StaticModelSourceRenderer(Builder builder)
	{
		this.builder = builder;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data)
	{
		GL11.glPushMatrix();
		
		GL11.glScaled(-1F, -1F, 1F);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		switch (type)
		{
		case ENTITY:
			builder.entityPositioning.accept(itemStack);
			break;
		case INVENTORY:
			builder.inventoryPositioning.accept(itemStack);
			break;
		case EQUIPPED:
			builder.thirdPersonPositioning.accept(player, itemStack);
			break;
		case EQUIPPED_FIRST_PERSON:
			builder.firstPersonPositioning.accept(player, itemStack);
			break;
		default:
		}
		
		renderModelSource(itemStack, type, null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		
		GL11.glPopMatrix();
	}
	
	private void renderModelSource(
			ItemStack itemStack, ItemRenderType type, Entity entity, 
			float f, float f1, float f2, float f3, float f4, float f5) {
		
		if(!(itemStack.getItem() instanceof ModelSource)) {
			throw new IllegalArgumentException();
		}
		
		GL11.glPushMatrix();

		for(Tuple<ModelBase, String> texturedModel: ((ModelSource)itemStack.getItem()).getTexturedModels()) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.modId 
					+ ":textures/models/" + texturedModel.getV()));
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			texturedModel.getU().render(entity, f, f1, f2, f3, f4, f5);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

}
