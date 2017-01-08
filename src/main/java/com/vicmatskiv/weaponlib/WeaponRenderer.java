package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;
import com.vicmatskiv.weaponlib.animation.Transition;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
//import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBase;
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
import net.minecraft.entity.Entity;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponRenderer extends ModelSourceRenderer implements IPerspectiveAwareModel, IBakedModel {
	
	private static final float DEFAULT_RANDOMIZING_RATE = 0.33f;
	private static final float DEFAULT_RANDOMIZING_FIRING_RATE = 20;
	private static final float DEFAULT_RANDOMIZING_ZOOM_RATE = 0.25f;
	
	private static final float DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE = 0.06f;
	private static final float DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE = 0.01f;
	private static final float DEFAULT_FIRING_RANDOMIZING_AMPLITUDE = 0.03f;
	
	private static final int DEFAULT_ANIMATION_DURATION = 250;
	private static final int DEFAULT_RECOIL_ANIMATION_DURATION = 100;

	public static class Builder {
		
		private ModelBase model;
		private String textureName;
		private float weaponProximity;
		private float yOffsetZoom;
		private float xOffsetZoom = 0.69F;
		
		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
		
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningZooming;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningRunning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningModifying;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningRecoiled;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningShooting;
		
		private BiConsumer<EntityPlayer, ItemStack> firstPersonLeftHandPositioning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonLeftHandPositioningZooming;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonLeftHandPositioningRunning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonLeftHandPositioningModifying;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonLeftHandPositioningRecoiled;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonLeftHandPositioningShooting;
		
		private BiConsumer<EntityPlayer, ItemStack> firstPersonRightHandPositioning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonRightHandPositioningZooming;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonRightHandPositioningRunning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonRightHandPositioningModifying;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonRightHandPositioningRecoiled;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonRightHandPositioningShooting;
		
		//private BiConsumer<EntityPlayer, ItemStack> firstPersonMagazinePositioning;
		
		private Random random = new Random();
		
		private List<Transition> firstPersonPositioningReloading;
		private List<Transition> firstPersonLeftHandPositioningReloading;
		private List<Transition> firstPersonRightHandPositioningReloading;
		
		private List<Transition> firstPersonPositioningUnloading;
		private List<Transition> firstPersonLeftHandPositioningUnloading;
		private List<Transition> firstPersonRightHandPositioningUnloading;
		
		private String modId;
		
		private float normalRandomizingRate = DEFAULT_RANDOMIZING_RATE; // movements per second, e.g. 0.25 = 0.25 movements per second = 1 movement in 3 minutes
		private float firingRandomizingRate = DEFAULT_RANDOMIZING_FIRING_RATE; // movements per second, e.g. 20 = 20 movements per second = 1 movement in 50 ms
		private float zoomRandomizingRate = DEFAULT_RANDOMIZING_ZOOM_RATE;
		
		private float normalRandomizingAmplitude = DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE;
		private float zoomRandomizingAmplitude = DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE;
		private float firingRandomizingAmplitude = DEFAULT_FIRING_RANDOMIZING_AMPLITUDE;
		
		public LinkedHashMap<Part, BiConsumer<EntityPlayer, ItemStack>> firstPersonCustomPositioning = new LinkedHashMap<>();
		public LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningUnloading = new LinkedHashMap<>();
		public LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningReloading = new LinkedHashMap<>();

		
		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withModel(ModelBase model) {
			this.model = model;
			return this;
		}
		
		public Builder withNormalRandomizingRate(float normalRandomizingRate) {
			this.normalRandomizingRate = normalRandomizingRate;
			return this;
		}
		
		public Builder withZoomRandomizingRate(float zoomRandomizingRate) {
			this.zoomRandomizingRate = zoomRandomizingRate;
			return this;
		}
		
		public Builder withFiringRandomizingRate(float firingRandomizingRate) {
			this.firingRandomizingRate = firingRandomizingRate;
			return this;
		}
		
		public Builder withFiringRandomizingAmplitude(float firingRandomizingAmplitude) {
			this.firingRandomizingAmplitude = firingRandomizingAmplitude;
			return this;
		}
		
		public Builder withNormalRandomizingAmplitude(float firingRandomizingRate) {
			this.firingRandomizingRate = firingRandomizingRate;
			return this;
		}
		
		public Builder withZoomRandomizingAmplitude(float zoomRandomizingAmplitude) {
			this.zoomRandomizingAmplitude = zoomRandomizingAmplitude;
			return this;
		}
		
		public Builder withTextureName(String textureName) {
			this.textureName = textureName + ".png";
			return this;
		}
		
		public Builder withWeaponProximity(float weaponProximity) {
			this.weaponProximity = weaponProximity;
			return this;
		}
		
		public Builder withYOffsetZoom(float yOffsetZoom) {
			this.yOffsetZoom = yOffsetZoom;
			return this;
		}
		
		public Builder withXOffsetZoom(float xOffsetZoom) {
			this.xOffsetZoom = xOffsetZoom;
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
		
		public Builder withFirstPersonPositioningRunning(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningRunning) {
			this.firstPersonPositioningRunning = firstPersonPositioningRunning;
			return this;
		}
		
		public Builder withFirstPersonPositioningZooming(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningZooming) {
			this.firstPersonPositioningZooming = firstPersonPositioningZooming;
			return this;
		}
		
		public Builder withFirstPersonPositioningRecoiled(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningRecoiled) {
			this.firstPersonPositioningRecoiled = firstPersonPositioningRecoiled;
			return this;
		}
		
		public Builder withFirstPersonPositioningShooting(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningShooting) {
			this.firstPersonPositioningShooting = firstPersonPositioningShooting;
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonPositioningReloading(Transition ...transitions) {
			this.firstPersonPositioningReloading = Arrays.asList(transitions);
			return this;
		}
		
		public Builder withFirstPersonPositioningModifying(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningModifying) {
			this.firstPersonPositioningModifying = firstPersonPositioningModifying;
			return this;
		}
		
		
		public Builder withFirstPersonHandPositioning(
				BiConsumer<EntityPlayer, ItemStack> leftHand,
				BiConsumer<EntityPlayer, ItemStack> rightHand) 
		{
			this.firstPersonLeftHandPositioning = leftHand;
			this.firstPersonRightHandPositioning = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningRunning(
				BiConsumer<EntityPlayer, ItemStack> leftHand,
				BiConsumer<EntityPlayer, ItemStack> rightHand) 
		{
			this.firstPersonLeftHandPositioningRunning = leftHand;
			this.firstPersonRightHandPositioningRunning = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningZooming(
				BiConsumer<EntityPlayer, ItemStack> leftHand,
				BiConsumer<EntityPlayer, ItemStack> rightHand)
		{
			this.firstPersonLeftHandPositioningZooming = leftHand;
			this.firstPersonRightHandPositioningZooming = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningRecoiled(
				BiConsumer<EntityPlayer, ItemStack> leftHand,
				BiConsumer<EntityPlayer, ItemStack> rightHand)
		{
			this.firstPersonLeftHandPositioningRecoiled = leftHand;
			this.firstPersonRightHandPositioningRecoiled = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningShooting(
				BiConsumer<EntityPlayer, ItemStack> leftHand,
				BiConsumer<EntityPlayer, ItemStack> rightHand)
		{
			this.firstPersonLeftHandPositioningShooting = leftHand;
			this.firstPersonRightHandPositioningShooting = rightHand;
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonLeftHandPositioningReloading(Transition ...transitions) {
			this.firstPersonLeftHandPositioningReloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningReloading(Transition ...transitions) {
			this.firstPersonRightHandPositioningReloading = Arrays.asList(transitions);
			return this;
		}
		
		public Builder withFirstPersonHandPositioningModifying(
				BiConsumer<EntityPlayer, ItemStack> leftHand,
				BiConsumer<EntityPlayer, ItemStack> rightHand)
		{
			this.firstPersonLeftHandPositioningModifying = leftHand;
			this.firstPersonRightHandPositioningModifying = rightHand;
			return this;
		}

		public WeaponRenderer build() {
			if(FMLCommonHandler.instance().getSide() != Side.CLIENT) {
				return null;
			}
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
					GL11.glRotatef(45F, 0f, 1f, 0f);
					if(itemStack.getTagCompound() != null && Tags.getZoom(itemStack) /*itemStack.stackTagCompound.getFloat(Weapon.ZOOM_TAG)*/ != 1.0f) {
						GL11.glTranslatef(xOffsetZoom, yOffsetZoom, weaponProximity);
					} else {
						GL11.glTranslatef(0F, -1.2F, 0F);
					}
				};
			}
			
			if(firstPersonPositioningZooming == null) {
				firstPersonPositioningZooming = firstPersonPositioning;
			}
			
			if(firstPersonPositioningReloading == null) {
				firstPersonPositioningReloading = Collections.singletonList(new Transition(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
			if(firstPersonPositioningUnloading == null) {
				firstPersonPositioningUnloading = Collections.singletonList(new Transition(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
			if(firstPersonPositioningRecoiled == null) {
				firstPersonPositioningRecoiled = firstPersonPositioning;
			}
			
			if(firstPersonPositioningRunning == null) {
				firstPersonPositioningRunning = firstPersonPositioning;
			}
			
			if(firstPersonPositioningModifying == null) {
				firstPersonPositioningModifying = firstPersonPositioning;
			}
			
			if(firstPersonPositioningShooting == null) {
				//firstPersonPositioningShooting = firstPersonPositioning;
				
				firstPersonPositioningShooting = (player, itemStack) -> {
					//firstPersonPositioning.accept(player, itemStack);

					float xRandomOffset = 0.05f * (random.nextFloat() - 0.5f) * 2;
					float yRandomOffset = 0.05f * (random.nextFloat() - 0.5f) * 2;
					float zRandomOffset = 0.05f * (random.nextFloat() - 0.5f) * 2;
					GL11.glTranslatef(xRandomOffset, yRandomOffset, zRandomOffset);
					//System.out.println("Rendering randomized shooting position...");
				};
			}
			
			if(thirdPersonPositioning == null) {
				thirdPersonPositioning = (player, itemStack) -> {
					GL11.glTranslatef(-0.4F, 0.2F, 0.4F);
					GL11.glRotatef(-45F, 0f, 1f, 0f);
					GL11.glRotatef(70F, 1f, 0f, 0f);
				};
			}
			
			// Left hand positioning
			
			if(firstPersonLeftHandPositioning == null) {
				firstPersonLeftHandPositioning = (player, itemStack) -> {};
			}
			
			if(firstPersonLeftHandPositioningReloading == null) {
				firstPersonLeftHandPositioningReloading = firstPersonPositioningReloading.stream().map(t -> new Transition((p, i) -> {}, 0)).collect(Collectors.toList());
			}
			
			if(firstPersonLeftHandPositioningUnloading == null) {
				firstPersonLeftHandPositioningUnloading = firstPersonPositioningUnloading.stream().map(t -> new Transition((p, i) -> {}, 0)).collect(Collectors.toList());
			}
			
			if(firstPersonLeftHandPositioningRecoiled == null) {
				firstPersonLeftHandPositioningRecoiled = firstPersonLeftHandPositioning;
			}
			
			if(firstPersonLeftHandPositioningShooting == null) {
				firstPersonLeftHandPositioningShooting = firstPersonLeftHandPositioning;
			}
			
			if(firstPersonLeftHandPositioningZooming == null) {
				firstPersonLeftHandPositioningZooming = firstPersonLeftHandPositioning;
			}
			
			if(firstPersonLeftHandPositioningRunning == null) {
				firstPersonLeftHandPositioningRunning = firstPersonLeftHandPositioning;
			}
			
			if(firstPersonLeftHandPositioningModifying == null) {
				firstPersonLeftHandPositioningModifying = firstPersonLeftHandPositioning;
			}
			
			// Right hand positioning
			
			if(firstPersonRightHandPositioning == null) {
				firstPersonRightHandPositioning = (player, itemStack) -> {};
			}
			
			if(firstPersonRightHandPositioningReloading == null) {
				//firstPersonRightHandPositioningReloading = Collections.singletonList(new Transition(firstPersonRightHandPositioning, DEFAULT_ANIMATION_DURATION));
				firstPersonRightHandPositioningReloading = firstPersonPositioningReloading.stream().map(t -> new Transition((p, i) -> {}, 0)).collect(Collectors.toList());
			}

			if(firstPersonRightHandPositioningUnloading == null) {
				firstPersonRightHandPositioningUnloading = firstPersonPositioningUnloading.stream().map(t -> new Transition((p, i) -> {}, 0)).collect(Collectors.toList());
			}

			if(firstPersonRightHandPositioningRecoiled == null) {
				firstPersonRightHandPositioningRecoiled = firstPersonRightHandPositioning;
			}

			if(firstPersonRightHandPositioningShooting == null) {
				firstPersonRightHandPositioningShooting = firstPersonRightHandPositioning;
			}
			
			if(firstPersonRightHandPositioningZooming == null) {
				firstPersonRightHandPositioningZooming = firstPersonRightHandPositioning;
			}
			
			if(firstPersonRightHandPositioningRunning == null) {
				firstPersonRightHandPositioningRunning = firstPersonRightHandPositioning;
			}
			
			if(firstPersonRightHandPositioningModifying == null) {
				firstPersonRightHandPositioningModifying = firstPersonRightHandPositioning;
			}
			
			
			// Custom positioning
			
			firstPersonCustomPositioningReloading.forEach((p, t) -> {
				if(t.size() != firstPersonPositioningReloading.size()) {
					throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + firstPersonPositioningReloading.size()
					+ ", actual: " + t.size());
				}
			});
			
			firstPersonCustomPositioningUnloading.forEach((p, t) -> {
				if(t.size() != firstPersonPositioningUnloading.size()) {
					throw new IllegalStateException("Custom unloading transition number mismatch. Expected " + firstPersonPositioningUnloading.size()
					+ ", actual: " + t.size());
				}
			});
			
			return new WeaponRenderer(this);
		}
	}
	
	private Builder builder;
	
	private Map<EntityPlayer, MultipartRenderStateManager<RenderableState, Part, RenderContext>> firstPersonStateManagers;
		
	private MultipartTransitionProvider<RenderableState, Part, RenderContext> weaponTransitionProvider;
	
	protected EntityPlayer owner;

	protected TextureManager textureManager;

	private Pair<? extends IBakedModel, Matrix4f> pair;
	protected ModelBiped playerBiped = new ModelBiped();
	
	protected ItemStack itemStack;

	protected ModelResourceLocation resourceLocation;
	
	TransformType transformType;
	
	private class WeaponItemOverrideList extends ItemOverrideList {

		public WeaponItemOverrideList(List<ItemOverride> overridesIn) {
			super(overridesIn);
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world,
				EntityLivingBase entity) {
			WeaponRenderer.this.itemStack = stack;
			WeaponRenderer.this.owner = (EntityPlayer) entity;
			return super.handleItemState(originalModel, stack, world, entity);
		}
	}
	
	private ItemOverrideList itemOverrideList = new WeaponItemOverrideList(Collections.emptyList());
	
	private WeaponRenderer (Builder builder)
	{
		this.builder = builder;
		this.firstPersonStateManagers = new HashMap<>();
		this.weaponTransitionProvider = new WeaponPositionProvider();
		
		this.textureManager = Minecraft.getMinecraft().getTextureManager();
		//this.resourceLocation = resourceLocation;
		this.pair = Pair.of((IBakedModel) this, null);
		this.playerBiped = new ModelBiped();
		this.playerBiped.textureWidth = 64;
		this.playerBiped.textureHeight = 64;
		
	}

	private static class StateDescriptor {
		MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager;
		float rate;
		float amplitude = 0.04f;
		public StateDescriptor(MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager,
				float rate, float amplitude) {
			this.stateManager = stateManager;
			this.rate = rate;
			this.amplitude = amplitude;
		}
		
	}
	
	private StateDescriptor getStateDescriptor(EntityPlayer player, ItemStack itemStack) {
		float amplitude = builder.normalRandomizingAmplitude;
		float rate = builder.normalRandomizingRate;
		RenderableState currentState = null;
		Weapon weapon = (Weapon) itemStack.getItem();
		if(Weapon.isModifying(itemStack)) {
			currentState = builder.firstPersonPositioningModifying != null ? RenderableState.MODIFYING : RenderableState.NORMAL;
		} else if(Weapon.isUnloadingStarted(player, itemStack)) {
			currentState = RenderableState.UNLOADING;
		} else if(Weapon.isReloadingConfirmed(player, itemStack)) {
			currentState = RenderableState.RELOADING;
		} else if(player.isSprinting() && builder.firstPersonPositioningRunning != null) {
			currentState = RenderableState.RUNNING;
		} else if(Weapon.isZoomed(itemStack)) {
			WeaponClientStorage storage = weapon.getWeaponClientStorage(player);

			if(storage != null) {
				currentState = storage.getNextDisposableRenderableState();
				if(currentState == RenderableState.SHOOTING) {
					rate = builder.firingRandomizingRate;
				} else {
					rate = builder.zoomRandomizingRate;
				}
			}
			amplitude = builder.zoomRandomizingAmplitude; // Zoom amplitude is enforced even when firing
			currentState = RenderableState.ZOOMING;
		} else {
			WeaponClientStorage storage = weapon.getWeaponClientStorage(player);

			if(storage != null) {
				currentState = storage.getNextDisposableRenderableState();
				if(currentState == RenderableState.SHOOTING) {
					currentState = RenderableState.NORMAL;
					rate = builder.firingRandomizingRate;
					amplitude = builder.firingRandomizingAmplitude;
				}
			}
		}
		
		if(currentState == null) {
			currentState = RenderableState.NORMAL;
		}
		
		MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager = firstPersonStateManagers.get(player);
		if(stateManager == null) {
			stateManager = new MultipartRenderStateManager<>(currentState, weaponTransitionProvider, Part.WEAPON);
			firstPersonStateManagers.put(player, stateManager);
		} else {
			stateManager.setState(currentState, true, currentState == RenderableState.SHOOTING);
		}
		
		return new StateDescriptor(stateManager, rate, amplitude);
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if(transformType == TransformType.GROUND 
				|| transformType == TransformType.GUI
				|| transformType == TransformType.FIRST_PERSON_RIGHT_HAND 
				|| transformType == TransformType.THIRD_PERSON_RIGHT_HAND 
				) {
			
//			if(transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
//				System.out.println("Renderer " + WeaponRenderer.this.builder.model.getClass() + " handing item " + this.itemStack);
//			}
		
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer worldrenderer = tessellator.getBuffer();
			tessellator.draw();
			GlStateManager.pushMatrix();

			if (owner != null) {
				if (transformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
					if (owner.isSneaking()) GlStateManager.translate(0.0F, -0.2F, 0.0F);
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


//	@Override
//	public IBakedModel handleItemState(ItemStack stack) {
//		this.itemStack = stack;
//		return this;
//	}

	public void setOwner(EntityPlayer player) {
		this.owner = player;
	}
	
	
//	@Override
//	public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
//		this.transformType = cameraTransformType;
//		return pair;
//	}
	

	@SideOnly(Side.CLIENT)
	public void renderItem()
	{
		GL11.glPushMatrix();
		
		
		AbstractClientPlayer player = Minecraft.getMinecraft().thePlayer;
		RenderContext renderContext = new RenderContext(player, itemStack);
		Positioner<Part, RenderContext> positioner = null;
		switch (transformType)
		{
		case GROUND:
			GL11.glScaled(-1F, -1F, 1F);
			builder.entityPositioning.accept(itemStack);
			break;
		case GUI:
			GL11.glScaled(-1F, -1F, 1F);
			GL11.glScaled(0.5F, 0.5F, 0.5F);
			GL11.glTranslatef(-1.1f, -0.9f, 0f);
			GL11.glRotatef(0F, 1f, 0f, 0f);
			GL11.glRotatef(0F, 0f, 1f, 0f);
			GL11.glRotatef(-10F, 0f, 0f, 1f);
			builder.inventoryPositioning.accept(itemStack);
			break;
		case THIRD_PERSON_RIGHT_HAND: case THIRD_PERSON_LEFT_HAND:
			GL11.glScaled(-1F, -1F, 1F);
			GL11.glScaled(0.4F, 0.4F, 0.4F);
			GL11.glTranslatef(-1.2f, -2.5f, -0.05f);
			GL11.glRotatef(-20F, 1f, 0f, 0f);
			GL11.glRotatef(-45F, 0f, 1f, 0f);
			GL11.glRotatef(-180F, 0f, 0f, 1f);
			
			builder.thirdPersonPositioning.accept(player, itemStack);
			break;
		case FIRST_PERSON_RIGHT_HAND: case FIRST_PERSON_LEFT_HAND:
			
			GL11.glTranslatef(0.5f, 0.5f  + 0.6f, 0.5f); // untranslate
			GL11.glRotatef(45f, 0f, 1f, 0f); // rotate as per 1.8.9 transformFirstPersonItem
			
			GL11.glScalef(0.4F, 0.4F, 0.4F); // scale as per 1.8.9 transformFirstPersonItem
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f); 
			
			GL11.glScaled(-1F, -1F, 1F);
			
			StateDescriptor stateDescriptor = getStateDescriptor(player, itemStack);
			MultipartPositioning<Part, RenderContext> multipartPositioning = stateDescriptor.stateManager.nextPositioning();
			
			positioner = multipartPositioning.getPositioner();
						
			positioner.randomize(stateDescriptor.rate, stateDescriptor.amplitude);
			
			positioner.position(Part.WEAPON, renderContext);
			
			Render<AbstractClientPlayer> entityRenderObject = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(player);
			RenderPlayer render = (RenderPlayer) entityRenderObject;
			Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
			
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
			//render.renderRightArm(player);
			renderRightArm(render, player);
			GL11.glPopMatrix();
	        
			break;
		default:
		}
		
		if(builder.textureName != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.modId 
					+ ":textures/models/" + builder.textureName));
		} else {
			Weapon weapon = ((Weapon) itemStack.getItem());
			String textureName = weapon.getActiveTextureName(itemStack);
			if(textureName != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.modId 
						+ ":textures/models/" + textureName));
			}
		}
		
		builder.model.render(null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		if(builder.model instanceof ModelWithAttachments) {
			List<CompatibleAttachment<? extends AttachmentContainer>> attachments = ((Weapon) itemStack.getItem()).getActiveAttachments(itemStack);
			renderAttachments(positioner, builder.modId, renderContext, itemStack, transformType, attachments , null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		}
		
		GL11.glPopMatrix();
	   
	}

	private void renderAttachments(Positioner<Part, RenderContext> positioner, String modId, RenderContext renderContext,
			ItemStack itemStack, TransformType type, List<CompatibleAttachment<? extends AttachmentContainer>> attachments, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		GL11.glPushMatrix();
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null) {
				ItemAttachment<?> itemAttachment = compatibleAttachment.getAttachment();
				
				if(itemAttachment instanceof Part && positioner != null) {
					positioner.position((Part) itemAttachment, renderContext);
				}

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
		
		GL11.glPopMatrix();
	   
	}
	
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

            if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
            {
                modelplayer.rightArmPose = modelbiped$armpose;
                modelplayer.leftArmPose = modelbiped$armpose1;
            }
            else
            {
                modelplayer.rightArmPose = modelbiped$armpose1;
                modelplayer.leftArmPose = modelbiped$armpose;
            }
        }
    }
	
	private BiConsumer<Part, RenderContext> createWeaponPartPositionFunction(BiConsumer<EntityPlayer, ItemStack> weaponPositionFunction) {
		return (part, context) -> weaponPositionFunction.accept(context.player, context.weapon);
	}
	
	private class RenderContext {
		EntityPlayer player;
		ItemStack weapon;
		public RenderContext(EntityPlayer player, ItemStack weapon) {
			this.player = player;
			this.weapon = weapon;
		}
	}
	
	private List<MultipartTransition<Part, RenderContext>> getComplexTransition(
			List<Transition> wt, 
			List<Transition> lht, 
			List<Transition> rht, 
			LinkedHashMap<Part, List<Transition>> custom) 
	{
		List<MultipartTransition<Part, RenderContext>> result = new ArrayList<>();
		for(int i = 0; i < wt.size(); i++) {
			Transition p = wt.get(i);
			Transition l = lht.get(i);
			Transition r = rht.get(i);
			
			MultipartTransition<Part, RenderContext> t = new MultipartTransition<Part, RenderContext>(p.getDuration(), p.getPause())
					.withPartPositionFunction(Part.WEAPON, createWeaponPartPositionFunction(p.getPositioning()))
					.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(l.getPositioning()))
					.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(r.getPositioning()));
			
			for(Entry<Part, List<Transition>> e: custom.entrySet()){
				Transition partTransition = e.getValue().get(i);
				t.withPartPositionFunction(e.getKey(), createWeaponPartPositionFunction(partTransition.getPositioning()));
			}
	
			result.add(t);
		}
		return result;
	}
	
	private List<MultipartTransition<Part, RenderContext>> getSimpleTransition(
			BiConsumer<EntityPlayer, ItemStack> w,
			BiConsumer<EntityPlayer, ItemStack> lh,
			BiConsumer<EntityPlayer, ItemStack> rh,
			//BiConsumer<EntityPlayer, ItemStack> m,
			LinkedHashMap<Part, BiConsumer<EntityPlayer, ItemStack>> custom,
			int duration) {
		MultipartTransition<Part, RenderContext> mt = new MultipartTransition<Part, RenderContext>(duration, 0)
				.withPartPositionFunction(Part.WEAPON, createWeaponPartPositionFunction(w))
				.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(lh))
				.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(rh));
		custom.forEach((part, position) -> {
			mt.withPartPositionFunction(part, createWeaponPartPositionFunction(position));
		});
		return Collections.singletonList(mt);
	}
	
	private class WeaponPositionProvider implements MultipartTransitionProvider<RenderableState, Part, RenderContext> {

		@Override
		public List<MultipartTransition<Part, RenderContext>> getPositioning(RenderableState state) {
			switch(state) {
			case MODIFYING:
				return getSimpleTransition(builder.firstPersonPositioningModifying,
						builder.firstPersonLeftHandPositioningModifying,
						builder.firstPersonRightHandPositioningModifying,
						builder.firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION);
			case RUNNING:
				return getSimpleTransition(builder.firstPersonPositioningRunning,
						builder.firstPersonLeftHandPositioningRunning,
						builder.firstPersonRightHandPositioningRunning,
						builder.firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION);
			case UNLOADING:
				return getComplexTransition(builder.firstPersonPositioningUnloading, 
						builder.firstPersonLeftHandPositioningUnloading,
						builder.firstPersonRightHandPositioningUnloading,
						builder.firstPersonCustomPositioningUnloading
						);
			case RELOADING:
				return getComplexTransition(builder.firstPersonPositioningReloading, 
						builder.firstPersonLeftHandPositioningReloading,
						builder.firstPersonRightHandPositioningReloading,
						builder.firstPersonCustomPositioningReloading
						);
			case RECOILED:
				return getSimpleTransition(builder.firstPersonPositioningRecoiled, 
						builder.firstPersonLeftHandPositioningRecoiled,
						builder.firstPersonRightHandPositioningRecoiled,
						builder.firstPersonCustomPositioning,
						DEFAULT_RECOIL_ANIMATION_DURATION);
			case SHOOTING:
				return getSimpleTransition(builder.firstPersonPositioningShooting, 
						builder.firstPersonLeftHandPositioningShooting,
						builder.firstPersonRightHandPositioningShooting,
						builder.firstPersonCustomPositioning,
						DEFAULT_RECOIL_ANIMATION_DURATION); // TODO: is it really recoil duration
			case NORMAL:
				return getSimpleTransition(builder.firstPersonPositioning, 
						builder.firstPersonLeftHandPositioning,
						builder.firstPersonRightHandPositioning,
						builder.firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION);
			case ZOOMING:
				return getSimpleTransition(builder.firstPersonPositioningZooming, 
						builder.firstPersonLeftHandPositioningZooming,
						builder.firstPersonRightHandPositioningZooming,
						builder.firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION);
			default:
				break;
			}
			return null;
		}
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

//	@Override
//	public VertexFormat getFormat() {
//		return DefaultVertexFormats.ITEM;
//	}

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
