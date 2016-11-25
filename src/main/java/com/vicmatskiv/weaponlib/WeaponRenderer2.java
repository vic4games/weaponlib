package com.vicmatskiv.weaponlib;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;

public abstract class WeaponRenderer2 implements ISmartItemModel, IPerspectiveAwareModel, IFlexibleBakedModel {

	protected EntityPlayer owner;

	protected TextureManager textureManager;
	public ModelResourceLocation resourceLocation;

	private Pair<? extends IFlexibleBakedModel, Matrix4f> pair;
	protected ModelBiped playerBiped = new ModelBiped();
	protected Minecraft mc;
	
	@SuppressWarnings("deprecation")
	TransformType transformType;

	public WeaponRenderer2(ModelResourceLocation resourceLocation) {
		this.mc = Minecraft.getMinecraft();
		this.textureManager = mc.getTextureManager();
		this.resourceLocation = resourceLocation;
		this.pair = Pair.of((IFlexibleBakedModel) this, null);
		this.playerBiped = new ModelBiped();
		this.playerBiped.textureWidth = 64;
		this.playerBiped.textureHeight = 64;
	}

	public abstract void render();

	@Override
	public final List<BakedQuad> getGeneralQuads() {
		// Method that this get's called in, is using startDrawingQuads. We
		// finish
		// drawing it so we can move on to render our own thing.
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		tessellator.draw();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);

		
		if (owner != null) {
			if (transformType == TransformType.THIRD_PERSON) {
				if (owner.isSneaking()) GlStateManager.translate(0.0F, -0.2F, 0.0F);
			}
		}

		if (onGround()) {
			GlStateManager.scale(-3f, -3f, -3f);
		}

		render();
		GlStateManager.popMatrix();
		// Reset the dynamic values.
		this.owner = null;
		this.stack = null;
		this.transformType = null;
		
		// Method that this gets called is expecting that we are still using
		// startDrawingQuads.
		//TODO: this was commented out: worldrenderer.startDrawingQuads();
		
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

//	@Override
//	public final TextureAtlasSprite getTexture() {
//		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
//	}
	
	// TODO: getTexture above was commented and replaced by getParticleTexture() below. Is this the right method?
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}

	protected ItemStack stack;

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		this.stack = stack;
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

//	protected TransformType transformType;
//
//	@Override
//	public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
//		this.transformType = cameraTransformType;
//		switch (cameraTransformType) {
//		case FIRST_PERSON:
//			RenderItem.applyVanillaTransform(getItemCameraTransforms().firstPerson);
//			break;
//		case GUI:
//			RenderItem.applyVanillaTransform(getItemCameraTransforms().gui);
//			break;
//		case HEAD:
//			RenderItem.applyVanillaTransform(getItemCameraTransforms().head);
//			break;
//		case THIRD_PERSON:
//			RenderItem.applyVanillaTransform(getItemCameraTransforms().thirdPerson);
//			break;
//		default:
//			break;
//		}
//		return pair;
//	}
}