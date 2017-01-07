package com.vicmatskiv.weaponlib;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class StaticModelSourceRenderer extends ModelSourceRenderer 
implements ISmartItemModel, IPerspectiveAwareModel, IFlexibleBakedModel {

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
	
	protected EntityPlayer owner;

	protected TextureManager textureManager;

	private Pair<? extends IFlexibleBakedModel, Matrix4f> pair;
	protected ModelBiped playerBiped = new ModelBiped();
	
	protected ItemStack itemStack;

	protected ModelResourceLocation resourceLocation;
	
	protected TransformType transformType;
	
	private StaticModelSourceRenderer(Builder builder)
	{
		this.builder = builder;
	}
	
	@Override
	public final List<BakedQuad> getGeneralQuads() {
		
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		tessellator.draw();
		GlStateManager.pushMatrix();

		
		if (owner != null) {
			if (transformType == TransformType.THIRD_PERSON) {
				if (owner.isSneaking()) GlStateManager.translate(0.0F, -0.2F, 0.0F);
			}
		}

		if (onGround()) {
			GlStateManager.scale(-3f, -3f, -3f);
		}

		renderItem();
		GlStateManager.popMatrix();
		this.owner = null;
		this.itemStack = null;
		this.transformType = null;
		
		worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		
		return Collections.emptyList();
	}
	
	protected boolean onGround() {
		return transformType == null;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
		return Collections.emptyList();
	}

	@Override
	public final boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public final boolean isGui3d() {
		return true;
	}

	@Override
	public final boolean isBuiltInRenderer() {
		return false;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}


	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		this.itemStack = stack;
		return this;
	}

	public void setOwner(EntityPlayer player) {
		this.owner = player;
	}
	
	@Override
	public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		this.transformType = cameraTransformType;
		return pair;
	}
	


	@SideOnly(Side.CLIENT)
	public void renderItem()
	{
		GL11.glPushMatrix();
		
		GL11.glScaled(-1F, -1F, 1F);
		AbstractClientPlayer player = Minecraft.getMinecraft().thePlayer;
		switch (transformType)
		{
		case GROUND:
			builder.entityPositioning.accept(itemStack);
			break;
		case GUI:
			builder.inventoryPositioning.accept(itemStack);
			break;
		case THIRD_PERSON:
			builder.thirdPersonPositioning.accept(player, itemStack);
			break;
		case FIRST_PERSON:
			builder.firstPersonPositioning.accept(player, itemStack);
	        
			break;
		default:
		}
		
		renderModelSource(itemStack, transformType, null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		
		GL11.glPopMatrix();
	}
	
	private void renderModelSource(
			ItemStack itemStack, TransformType type, Entity entity, 
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

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public VertexFormat getFormat() {
		return DefaultVertexFormats.ITEM;
	}

}
