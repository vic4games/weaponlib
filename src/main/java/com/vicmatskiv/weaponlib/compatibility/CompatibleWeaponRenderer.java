package com.vicmatskiv.weaponlib.compatibility;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.AttachmentContainer;
import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.CompatibleAttachment;
import com.vicmatskiv.weaponlib.ModelWithAttachments;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponRenderer;
import com.vicmatskiv.weaponlib.WeaponRenderer.Builder;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CompatibleWeaponRenderer extends ModelSourceRenderer implements IPerspectiveAwareModel, IBakedModel {
	
	protected static class StateDescriptor {
		protected MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager;
		protected float rate;
		protected float amplitude = 0.04f;
		private PlayerWeaponInstance instance;
		public StateDescriptor(PlayerWeaponInstance instance, MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager,
				float rate, float amplitude) {
			this.stateManager = stateManager;
			this.rate = rate;
			this.amplitude = amplitude;
		}
		
		
		
	}
	
	protected EntityPlayer owner;

	protected TextureManager textureManager;

	private Pair<? extends IBakedModel, Matrix4f> pair;
	protected ModelBiped playerBiped = new ModelBiped();
	
	protected ItemStack itemStack;

	protected ModelResourceLocation resourceLocation;
	
	private class WeaponItemOverrideList extends ItemOverrideList {

		public WeaponItemOverrideList(List<ItemOverride> overridesIn) {
			super(overridesIn);
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world,
				EntityLivingBase entity) {
			CompatibleWeaponRenderer.this.itemStack = stack;
			CompatibleWeaponRenderer.this.owner = (EntityPlayer) entity;
			return super.handleItemState(originalModel, stack, world, entity);
		}
	}
	
	private ItemOverrideList itemOverrideList = new WeaponItemOverrideList(Collections.emptyList());
	
	TransformType transformType;

	private Builder builder;
	
	protected CompatibleWeaponRenderer (WeaponRenderer.Builder builder) {
		this.builder = builder;
		
		this.textureManager = Minecraft.getMinecraft().getTextureManager();
		this.pair = Pair.of((IBakedModel) this, null);
		this.playerBiped = new ModelBiped();
		this.playerBiped.textureWidth = 64;
		this.playerBiped.textureHeight = 64;
		
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if(transformType == TransformType.GROUND 
				|| transformType == TransformType.GUI
				|| transformType == TransformType.FIRST_PERSON_RIGHT_HAND 
				|| transformType == TransformType.THIRD_PERSON_RIGHT_HAND 
				|| transformType == TransformType.FIRST_PERSON_LEFT_HAND 
				|| transformType == TransformType.THIRD_PERSON_LEFT_HAND 
				) {
		
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer worldrenderer = tessellator.getBuffer();
			tessellator.draw();
			GlStateManager.pushMatrix();

			if (owner != null) {
				if (transformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
					if (owner.isSneaking()) GlStateManager.translate(0.0F, -0.2F, 0.0F);
				} else if (transformType == TransformType.FIRST_PERSON_LEFT_HAND || transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
					//
				}
			}

			if (onGround()) {
				GlStateManager.scale(-3f, -3f, -3f);
			}

			renderItem();
			GlStateManager.popMatrix();
			worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		}
		
		// Reset the dynamic values.
		this.owner = null;
		this.itemStack = null;
		this.transformType = null;
		
		return Collections.emptyList();
	}
	
	protected boolean onGround() {
		return transformType == null;
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

	public void setOwner(EntityPlayer player) {
		this.owner = player;
	}
	
	protected abstract ClientModContext getClientModContext();
	
	@SideOnly(Side.CLIENT)
	public void renderItem()
	{
		GL11.glPushMatrix();
		
		AbstractClientPlayer player = Minecraft.getMinecraft().thePlayer;
		RenderContext renderContext = new RenderContext(getClientModContext(), player, itemStack);
		
		renderContext.setAgeInTicks(-0.4f);
		renderContext.setScale(0.08f);
		renderContext.setCompatibleTransformType(CompatibleTransformType.fromItemRenderType(transformType));
		
		Positioner<Part, RenderContext> positioner = null;
		switch (transformType)
		{
		case GROUND:
			GL11.glScaled(-1F, -1F, 1F);
			GL11.glScaled(0.45F, 0.45F, 0.45F);
			GL11.glTranslatef(-1.1f, -0.9f, 0f);
			GL11.glRotatef(0F, 1f, 0f, 0f);
			GL11.glRotatef(0F, 0f, 1f, 0f);
			GL11.glRotatef(0F, 0f, 0f, 1f);
			builder.getEntityPositioning().accept(itemStack);
			break;
		case GUI:
			GL11.glScaled(-1F, -1F, 1F);
			GL11.glScaled(0.5F, 0.5F, 0.5F);
			GL11.glTranslatef(-1.1f, -0.9f, 0f);
			GL11.glRotatef(0F, 1f, 0f, 0f);
			GL11.glRotatef(0F, 0f, 1f, 0f);
			GL11.glRotatef(-10F, 0f, 0f, 1f);
			builder.getInventoryPositioning().accept(itemStack);
			break;
		case THIRD_PERSON_RIGHT_HAND: case THIRD_PERSON_LEFT_HAND:
			GL11.glScaled(-1F, -1F, 1F);
			GL11.glScaled(0.4F, 0.4F, 0.4F);
			GL11.glTranslatef(-1.25f, -2.1f, 0.6f);
			GL11.glRotatef(110F, 1f, 0f, 0f);
			GL11.glRotatef(135F, 0f, 1f, 0f);
			GL11.glRotatef(-180F, 0f, 0f, 1f);
			
			builder.getThirdPersonPositioning().accept(renderContext);
			break;
		case FIRST_PERSON_RIGHT_HAND: case FIRST_PERSON_LEFT_HAND:
			
			int i = transformType == TransformType.FIRST_PERSON_RIGHT_HAND ? 1 : -1;
			
			GL11.glTranslatef(0.5f, 0.5f, 0.5f); // untranslate 1.9.4
			
			i = -i;
			GL11.glTranslatef((float)i * 0.56F, 0.52F + /*p_187459_2_ * */ +0.6F, 0.72F); // untranslate 1.9.4

			if(transformType == TransformType.FIRST_PERSON_LEFT_HAND) {
				// mirror everything if left hand
				GL11.glScalef(-1f, 1f, 1f);
			}
			
			i = 1; // Draw everything as if for the right hand, assuming mirroring is already in place
			GL11.glTranslatef((float)i * 0.56F, -0.52F + /*p_187459_2_ * */ -0.6F, -0.72F); // re-translate 1.9.4
			
			GL11.glTranslatef(0f, 0.6f, 0f); // -0.6 y-offset is set somewhere upstream in 1.9.4, so adjusting it
						
			GL11.glRotatef(45f, 0f, 1f, 0f); // rotate as per 1.8.9 transformFirstPersonItem
			
			GL11.glScalef(0.4F, 0.4F, 0.4F); // scale as per 1.8.9 transformFirstPersonItem
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f); 
			
			GL11.glScaled(-1F, -1F, 1F);
			
			StateDescriptor stateDescriptor = getStateDescriptor(player, itemStack);
			renderContext.setPlayerItemInstance(stateDescriptor.instance);
			MultipartPositioning<Part, RenderContext> multipartPositioning = stateDescriptor.stateManager.nextPositioning();
			
			renderContext.setTransitionProgress(multipartPositioning.getProgress());
			
			renderContext.setFromState(multipartPositioning.getFromState(RenderableState.class));
			
			renderContext.setToState(multipartPositioning.getToState(RenderableState.class));
			
			positioner = multipartPositioning.getPositioner();
						
			positioner.randomize(stateDescriptor.rate, stateDescriptor.amplitude);
			
			positioner.position(Part.WEAPON, renderContext);
			
			Render<AbstractClientPlayer> entityRenderObject = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(player);
			RenderPlayer render = (RenderPlayer) entityRenderObject;
			Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
			
			if(player != null && player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof Weapon) {
				// Draw hands only if weapon is held in the main hand
				GL11.glPushMatrix();
				GL11.glTranslatef(0f, -1f, 0f);
				GL11.glRotatef(-10F, 1f, 0f, 0f);
				GL11.glRotatef(0F, 0f, 1f, 0f);
				GL11.glRotatef(10F, 0f, 0f, 1f);
				positioner.position(Part.LEFT_HAND, renderContext);
				render.renderLeftArm(player);
				GL11.glPopMatrix();
				
				GL11.glPushMatrix();
				GL11.glScaled(1F, 1F, 1F);
				GL11.glTranslatef(-0.25f, 0f, 0.2f);
				GL11.glRotatef(5F, 1f, 0f, 0f);
				GL11.glRotatef(25F, 0f, 1f, 0f);
				GL11.glRotatef(0F, 0f, 0f, 1f);	
				positioner.position(Part.RIGHT_HAND, renderContext);
				renderRightArm(render, player);
				GL11.glPopMatrix();
			}
			
	        
			break;
		default:
		}
		
		if(builder.getTextureName() != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId() 
					+ ":textures/models/" + builder.getTextureName()));
		} else {
			Weapon weapon = ((Weapon) itemStack.getItem());
			String textureName = weapon.getActiveTextureName(itemStack);
			if(textureName != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId() 
						+ ":textures/models/" + textureName));
			}
		}
		
//		builder.getModel().render(null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		
		builder.getModel().render(null,  
				renderContext.getLimbSwing(), 
				renderContext.getFlimbSwingAmount(), 
				renderContext.getAgeInTicks(), 
				renderContext.getNetHeadYaw(), 
				renderContext.getHeadPitch(), 
				renderContext.getScale());
		
		if(builder.getModel() instanceof ModelWithAttachments) {
			List<CompatibleAttachment<? extends AttachmentContainer>> attachments = ((Weapon) itemStack.getItem()).getActiveAttachments(itemStack);
			renderAttachments(positioner, renderContext, attachments);
		}
		
		GL11.glPopMatrix();
	}
	
	public abstract void renderAttachments(Positioner<Part, RenderContext> positioner, 
			RenderContext renderContext,
			List<CompatibleAttachment<? extends AttachmentContainer>> attachments);
	
	protected abstract StateDescriptor getStateDescriptor(EntityPlayer player, ItemStack itemStack);

//	private void renderAttachments(Positioner<Part, RenderContext> positioner, String modId, RenderContext renderContext,
//			ItemStack itemStack, TransformType type, List<CompatibleAttachment<? extends AttachmentContainer>> attachments, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
//		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
//			if(compatibleAttachment != null) {
//				GL11.glPushMatrix();
//				
//				ItemAttachment<?> itemAttachment = compatibleAttachment.getAttachment();
//				
//				if(positioner != null) {
//					if(itemAttachment instanceof Part) {
//						positioner.position((Part) itemAttachment, renderContext);
//					} else if(itemAttachment.getRenderablePart() != null) {
//						positioner.position(itemAttachment.getRenderablePart(), renderContext);
//					}
//				}
//				
//
//				for(Tuple<ModelBase, String> texturedModel: compatibleAttachment.getAttachment().getTexturedModels()) {
//					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(modId 
//							+ ":textures/models/" + texturedModel.getV()));
//					GL11.glPushMatrix();
//					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//					if(compatibleAttachment.getPositioning() != null) {
//						compatibleAttachment.getPositioning().accept(texturedModel.getU());
//					}
//					texturedModel.getU().render(entity, f, f1, f2, f3, f4, f5);
//					
//					CustomRenderer postRenderer = compatibleAttachment.getAttachment().getPostRenderer();
//					if(postRenderer != null) {
//						postRenderer.render(CompatibleTransformType.fromItemRenderType(type), itemStack);
//					}
//					GL11.glPopAttrib();
//					GL11.glPopMatrix();
//				}
//				GL11.glPopMatrix();
//			}
//		}
//	}
	
	public void renderRightArm(RenderPlayer renderPlayer, AbstractClientPlayer clientPlayer)
    {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        ModelPlayer modelplayer = renderPlayer.getMainModel();
        // Can ignore private method setModelVisibilities since it was already called earlier for left hand
        setModelVisibilities(renderPlayer, clientPlayer);
        
        GlStateManager.enableBlend();
        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedRightArm.rotateAngleX = -0.3F;
        modelplayer.bipedRightArm.rotateAngleY = 0.0F;
        modelplayer.bipedRightArm.render(0.0625F);
        modelplayer.bipedRightArmwear.rotateAngleX = 0.0F;
        modelplayer.bipedRightArmwear.render(0.0625F);
        GlStateManager.disableBlend();
    }
	
	private void setModelVisibilities(RenderPlayer renderPlayer, AbstractClientPlayer clientPlayer)
    {
        ModelPlayer modelplayer = renderPlayer.getMainModel();

        if (clientPlayer.isSpectator())
        {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        }
        else
        {
            ItemStack itemstack = clientPlayer.getHeldItemMainhand();
            ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.isSneak = clientPlayer.isSneaking();
            ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

            if (itemstack != null)
            {
                modelbiped$armpose = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                    }
                    else if (enumaction == EnumAction.BOW)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (itemstack1 != null)
            {
                modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction1 = itemstack1.getItemUseAction();

                    if (enumaction1 == EnumAction.BLOCK)
                    {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                    }
                }
            }

            if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT) {
                modelplayer.rightArmPose = modelbiped$armpose;
                modelplayer.leftArmPose = modelbiped$armpose1;
            } else {
                modelplayer.rightArmPose = modelbiped$armpose1;
                modelplayer.leftArmPose = modelbiped$armpose;
            }
        }
    }

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		
		return itemOverrideList;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		this.transformType = cameraTransformType;
		return pair;
	}
}
