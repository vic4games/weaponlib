package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;
import com.vicmatskiv.weaponlib.animation.Transition;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class WeaponRenderer extends CompatibleWeaponRenderer {
	
	private static final Logger logger = LogManager.getLogger(WeaponRenderer.class);

	
	private static final float DEFAULT_RANDOMIZING_RATE = 0.33f;
	private static final float DEFAULT_RANDOMIZING_FIRING_RATE = 20;
	private static final float DEFAULT_RANDOMIZING_ZOOM_RATE = 0.25f;
	
	private static final float DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE = 0.06f;
	private static final float DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE = 0.005f;
	private static final float DEFAULT_FIRING_RANDOMIZING_AMPLITUDE = 0.03f;
	
	private static final int DEFAULT_ANIMATION_DURATION = 250;
	private static final int DEFAULT_RECOIL_ANIMATION_DURATION = 100;
	private static final int DEFAULT_SHOOTING_ANIMATION_DURATION = 100;


	public static class Builder {
		
		private ModelBase model;
		private String textureName;
		private float weaponProximity;
		private float yOffsetZoom;
		private float xOffsetZoom = 0.69F;
		
		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private Consumer<RenderContext> thirdPersonPositioning;
		
		private Consumer<RenderContext> firstPersonPositioning;
		private Consumer<RenderContext> firstPersonPositioningZooming;
		private Consumer<RenderContext> firstPersonPositioningRunning;
		private Consumer<RenderContext> firstPersonPositioningModifying;
		private Consumer<RenderContext> firstPersonPositioningRecoiled;
		private Consumer<RenderContext> firstPersonPositioningShooting;
		private Consumer<RenderContext> firstPersonPositioningZoomingRecoiled;
		private Consumer<RenderContext> firstPersonPositioningZoomingShooting;
		
		private Consumer<RenderContext> firstPersonLeftHandPositioning;
		private Consumer<RenderContext> firstPersonLeftHandPositioningZooming;
		private Consumer<RenderContext> firstPersonLeftHandPositioningRunning;
		private Consumer<RenderContext> firstPersonLeftHandPositioningModifying;
		private Consumer<RenderContext> firstPersonLeftHandPositioningRecoiled;
		private Consumer<RenderContext> firstPersonLeftHandPositioningShooting;
		
		private Consumer<RenderContext> firstPersonRightHandPositioning;
		private Consumer<RenderContext> firstPersonRightHandPositioningZooming;
		private Consumer<RenderContext> firstPersonRightHandPositioningRunning;
		private Consumer<RenderContext> firstPersonRightHandPositioningModifying;
		private Consumer<RenderContext> firstPersonRightHandPositioningRecoiled;
		private Consumer<RenderContext> firstPersonRightHandPositioningShooting;

		private List<Transition> firstPersonPositioningReloading;
		private List<Transition> firstPersonLeftHandPositioningReloading;
		private List<Transition> firstPersonRightHandPositioningReloading;
		
		private List<Transition> firstPersonPositioningUnloading;
		private List<Transition> firstPersonLeftHandPositioningUnloading;
		private List<Transition> firstPersonRightHandPositioningUnloading;
		
		private long totalReloadingDuration;
		private long totalUnloadingDuration;
		
		private String modId;
		
		private int recoilAnimationDuration = DEFAULT_RECOIL_ANIMATION_DURATION;
		private int shootingAnimationDuration = DEFAULT_SHOOTING_ANIMATION_DURATION;
		
		private float normalRandomizingRate = DEFAULT_RANDOMIZING_RATE; // movements per second, e.g. 0.25 = 0.25 movements per second = 1 movement in 3 minutes
		private float firingRandomizingRate = DEFAULT_RANDOMIZING_FIRING_RATE; // movements per second, e.g. 20 = 20 movements per second = 1 movement in 50 ms
		private float zoomRandomizingRate = DEFAULT_RANDOMIZING_ZOOM_RATE;
		
		private float normalRandomizingAmplitude = DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE;
		private float zoomRandomizingAmplitude = DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE;
		private float firingRandomizingAmplitude = DEFAULT_FIRING_RANDOMIZING_AMPLITUDE;
		
		private LinkedHashMap<Part, Consumer<RenderContext>> firstPersonCustomPositioning = new LinkedHashMap<>();
		private LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningUnloading = new LinkedHashMap<>();
		private LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningReloading = new LinkedHashMap<>();
		private LinkedHashMap<Part, Consumer<RenderContext>> firstPersonCustomPositioningRecoiled = new LinkedHashMap<>();
		private LinkedHashMap<Part, Consumer<RenderContext>> firstPersonCustomPositioningZoomingRecoiled = new LinkedHashMap<>();
		private LinkedHashMap<Part, Consumer<RenderContext>> firstPersonCustomPositioningZoomingShooting = new LinkedHashMap<>();
		
		private List<Transition> firstPersonPositioningEjectSpentRound;
		private List<Transition> firstPersonLeftHandPositioningEjectSpentRound;
		private List<Transition> firstPersonRightHandPositioningEjectSpentRound;
		private LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningEjectSpentRound = new LinkedHashMap<>();
		private boolean hasRecoilPositioningDefined;

		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withModel(ModelBase model) {
			this.model = model;
			return this;
		}
		
		public Builder withShootingAnimationDuration(int shootingAnimationDuration) {
			this.shootingAnimationDuration = shootingAnimationDuration;
			return this;
		}
		
		public Builder withRecoilAnimationDuration(int recoilAnimationDuration) {
			this.recoilAnimationDuration = recoilAnimationDuration;
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

		public Builder withThirdPersonPositioning(Consumer<RenderContext> thirdPersonPositioning) {
			this.thirdPersonPositioning = thirdPersonPositioning;
			return this;
		}

		public Builder withFirstPersonPositioning(Consumer<RenderContext> firstPersonPositioning) {
			this.firstPersonPositioning = firstPersonPositioning;
			return this;
		}
		
		public Builder withFirstPersonPositioningRunning(Consumer<RenderContext> firstPersonPositioningRunning) {
			this.firstPersonPositioningRunning = firstPersonPositioningRunning;
			return this;
		}
		
		public Builder withFirstPersonPositioningZooming(Consumer<RenderContext> firstPersonPositioningZooming) {
			this.firstPersonPositioningZooming = firstPersonPositioningZooming;
			return this;
		}
		
		public Builder withFirstPersonPositioningRecoiled(Consumer<RenderContext> firstPersonPositioningRecoiled) {
			this.hasRecoilPositioningDefined = true;
			this.firstPersonPositioningRecoiled = firstPersonPositioningRecoiled;
			return this;
		}
		
		public Builder withFirstPersonPositioningShooting(Consumer<RenderContext> firstPersonPositioningShooting) {
			this.firstPersonPositioningShooting = firstPersonPositioningShooting;
			return this;
		}
		
		public Builder withFirstPersonPositioningZoomingRecoiled(Consumer<RenderContext> firstPersonPositioningZoomingRecoiled) {
			this.firstPersonPositioningZoomingRecoiled = firstPersonPositioningZoomingRecoiled;
			return this;
		}
		
		public Builder withFirstPersonPositioningZoomingShooting(Consumer<RenderContext> firstPersonPositioningZoomingShooting) {
			this.firstPersonPositioningZoomingShooting = firstPersonPositioningZoomingShooting;
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonPositioningReloading(Transition ...transitions) {
			this.firstPersonPositioningReloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonPositioningUnloading(Transition ...transitions) {
			this.firstPersonPositioningUnloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonPositioningEjectSpentRound(Transition ...transitions) {
			this.firstPersonPositioningEjectSpentRound = Arrays.asList(transitions);
			return this;
		}
		
		public Builder withFirstPersonPositioningModifying(Consumer<RenderContext> firstPersonPositioningModifying) {
			this.firstPersonPositioningModifying = firstPersonPositioningModifying;
			return this;
		}
		
		
		public Builder withFirstPersonHandPositioning(
				Consumer<RenderContext> leftHand,
				Consumer<RenderContext> rightHand) 
		{
			this.firstPersonLeftHandPositioning = leftHand;
			this.firstPersonRightHandPositioning = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningRunning(
				Consumer<RenderContext> leftHand,
				Consumer<RenderContext> rightHand) 
		{
			this.firstPersonLeftHandPositioningRunning = leftHand;
			this.firstPersonRightHandPositioningRunning = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningZooming(
				Consumer<RenderContext> leftHand,
				Consumer<RenderContext> rightHand)
		{
			this.firstPersonLeftHandPositioningZooming = leftHand;
			this.firstPersonRightHandPositioningZooming = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningRecoiled(
				Consumer<RenderContext> leftHand,
				Consumer<RenderContext> rightHand)
		{
			this.firstPersonLeftHandPositioningRecoiled = leftHand;
			this.firstPersonRightHandPositioningRecoiled = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningShooting(
				Consumer<RenderContext> leftHand,
				Consumer<RenderContext> rightHand)
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

		public final Builder withFirstPersonLeftHandPositioningEjectSpentRound(Transition ...transitions) {
			this.firstPersonLeftHandPositioningEjectSpentRound = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonLeftHandPositioningUnloading(Transition ...transitions) {
			this.firstPersonLeftHandPositioningUnloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningReloading(Transition ...transitions) {
			this.firstPersonRightHandPositioningReloading = Arrays.asList(transitions);
			return this;
		}

		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningUnloading(Transition ...transitions) {
			this.firstPersonRightHandPositioningUnloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningEjectSpentRound(Transition ...transitions) {
			this.firstPersonRightHandPositioningEjectSpentRound = Arrays.asList(transitions);
			return this;
		}
		
		public Builder withFirstPersonHandPositioningModifying(
				Consumer<RenderContext> leftHand,
				Consumer<RenderContext> rightHand)
		{
			this.firstPersonLeftHandPositioningModifying = leftHand;
			this.firstPersonRightHandPositioningModifying = rightHand;
			return this;
		}
		
		public Builder withFirstPersonCustomPositioning(Part part, Consumer<RenderContext> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioning.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonPositioningCustomRecoiled(Part part, Consumer<RenderContext> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningRecoiled.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonPositioningCustomZoomingShooting(Part part, Consumer<RenderContext> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningZoomingShooting.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonPositioningCustomZoomingRecoiled(Part part, Consumer<RenderContext> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningZoomingRecoiled.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningReloading(Part part, Transition ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			
			this.firstPersonCustomPositioningReloading.put(part, Arrays.asList(transitions));
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningUnloading(Part part, Transition ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			this.firstPersonCustomPositioningUnloading.put(part, Arrays.asList(transitions));
			return this;
		}
		
		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningEjectSpentRound(Part part, Transition ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			
			this.firstPersonCustomPositioningEjectSpentRound.put(part, Arrays.asList(transitions));
			return this;
		}
		
		public WeaponRenderer build() {
			if(!compatibility.isClientSide()) {
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
			
			WeaponRenderer renderer = new WeaponRenderer(this);
			
			if(firstPersonPositioning == null) {
				firstPersonPositioning = (renderContext) -> {
					GL11.glRotatef(45F, 0f, 1f, 0f);

					if(renderer.getClientModContext() != null) {
						PlayerWeaponInstance instance = renderer.getClientModContext().getMainHeldWeapon();
						if(instance != null && instance.isAimed()) {
							GL11.glTranslatef(xOffsetZoom, yOffsetZoom, weaponProximity);
						} else {
							GL11.glTranslatef(0F, -1.2F, 0F);
						}
					}
					
				};
			}
			
			if(firstPersonPositioningZooming == null) {
				firstPersonPositioningZooming = firstPersonPositioning;
			}
			
			if(firstPersonPositioningReloading == null) {
				firstPersonPositioningReloading = Collections.singletonList(new Transition(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
			for(Transition t: firstPersonPositioningReloading) {
				totalReloadingDuration += t.getDuration();
				totalReloadingDuration += t.getPause();
			}
			
			if(firstPersonPositioningUnloading == null) {
				firstPersonPositioningUnloading = Collections.singletonList(new Transition(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
			for(Transition t: firstPersonPositioningUnloading) {
				totalUnloadingDuration += t.getDuration();
				totalUnloadingDuration += t.getPause();
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
				firstPersonPositioningShooting = firstPersonPositioning;
			}
			
			if(firstPersonPositioningZoomingRecoiled == null) {
				firstPersonPositioningZoomingRecoiled = firstPersonPositioningZooming;
			}
			
			if(firstPersonPositioningZoomingShooting == null) {
				firstPersonPositioningZoomingShooting = firstPersonPositioningZooming;
			}
			
			if(thirdPersonPositioning == null) {
				thirdPersonPositioning = (context) -> {
					GL11.glTranslatef(-0.4F, 0.2F, 0.4F);
					GL11.glRotatef(-45F, 0f, 1f, 0f);
					GL11.glRotatef(70F, 1f, 0f, 0f);
				};
			}
			
			// Left hand positioning
			
			if(firstPersonLeftHandPositioning == null) {
				firstPersonLeftHandPositioning = (context) -> {};
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
				firstPersonRightHandPositioning = (context) -> {};
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
			
			/*
			 * If custom positioning for recoil is not set, default it to normal custom positioning
			 */
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningRecoiled.isEmpty()) {
				firstPersonCustomPositioning.forEach((part, pos) -> {
					firstPersonCustomPositioningRecoiled.put(part, pos);
				});
			}
			
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningZoomingRecoiled.isEmpty()) {
				firstPersonCustomPositioning.forEach((part, pos) -> {
					firstPersonCustomPositioningZoomingRecoiled.put(part, pos);
				});
			}
			
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningZoomingShooting.isEmpty()) {
				firstPersonCustomPositioning.forEach((part, pos) -> {
					firstPersonCustomPositioningZoomingShooting.put(part, pos);
				});
			}
						
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
			
			
			return renderer;
		}

		public Consumer<ItemStack> getEntityPositioning() {
			return entityPositioning;
		}

		public Consumer<ItemStack> getInventoryPositioning() {
			return inventoryPositioning;
		}

		public Consumer<RenderContext> getThirdPersonPositioning() {
			return thirdPersonPositioning;
		}

		public String getTextureName() {
			return textureName;
		}

		public ModelBase getModel() {
			return model;
		}

		public String getModId() {
			return modId;
		}


	}
	
	private Builder builder;
	
	private Map<EntityPlayer, MultipartRenderStateManager<RenderableState, Part, RenderContext>> firstPersonStateManagers;
		
	private MultipartTransitionProvider<RenderableState, Part, RenderContext> weaponTransitionProvider;
	
	protected ClientModContext clientModContext;
			
	private WeaponRenderer(Builder builder) {
		super(builder);
		this.builder = builder;
		this.firstPersonStateManagers = new HashMap<>();
		this.weaponTransitionProvider = new WeaponPositionProvider();
	}
	
	protected long getTotalReloadingDuration() {
		return builder.totalReloadingDuration;
	}
	
	protected long getTotalUnloadingDuration() {
		return builder.totalUnloadingDuration;
	}

	protected ClientModContext getClientModContext() {
		return clientModContext;
	}
	
	protected void setClientModContext(ClientModContext clientModContext) {
		this.clientModContext = clientModContext;
	}

	@Override
	protected StateDescriptor getStateDescriptor(EntityPlayer player, ItemStack itemStack) {
		float amplitude = builder.normalRandomizingAmplitude;
		float rate = builder.normalRandomizingRate;
		RenderableState currentState = null;
		
		PlayerWeaponInstance playerWeaponInstance = clientModContext.getPlayerItemInstanceRegistry()
				.getMainHandItemInstance(player, PlayerWeaponInstance.class); // TODO: cannot be always main hand, need to which hand from context
		
		if(playerWeaponInstance != null) {
			AsyncWeaponState asyncWeaponState = getNextNonExpiredState(playerWeaponInstance);
			
			switch(asyncWeaponState.getState()) {
				
			case RECOILED: 
				if(playerWeaponInstance.isAutomaticModeEnabled() && !hasRecoilPositioning()) {
					if(playerWeaponInstance.isAimed()) {
						currentState = RenderableState.ZOOMING;
						rate = builder.firingRandomizingRate;
						amplitude = builder.zoomRandomizingAmplitude;
					} else {
						currentState = RenderableState.NORMAL; 
						rate = builder.firingRandomizingRate;
						amplitude = builder.firingRandomizingAmplitude;
					}
				} else if(playerWeaponInstance.isAimed()) {
					currentState = RenderableState.ZOOMING_RECOILED;
					amplitude = builder.zoomRandomizingAmplitude;
				} else {
					currentState = RenderableState.RECOILED; 
				}
				
				break;
				
			case PAUSED: 
				if(playerWeaponInstance.isAutomaticModeEnabled() && !hasRecoilPositioning()) {
					
					boolean isLongPaused = System.currentTimeMillis() - asyncWeaponState.getTimestamp() > (50f / playerWeaponInstance.getFireRate())
							&& asyncWeaponState.isInfinite();
					
					if(playerWeaponInstance.isAimed()) {
						currentState = RenderableState.ZOOMING;
						if(!isLongPaused) {
							rate = builder.firingRandomizingRate;
						}
						amplitude = builder.zoomRandomizingAmplitude;
					} else {
						currentState = RenderableState.NORMAL; 
						if(!isLongPaused) {
							rate = builder.firingRandomizingRate;
							amplitude = builder.firingRandomizingAmplitude;
						}
					}
				} else if(playerWeaponInstance.isAimed()) {
					currentState = RenderableState.ZOOMING_SHOOTING;
					//rate = builder.firingRandomizingRate;
					amplitude = builder.zoomRandomizingAmplitude;
				} else {
					currentState = RenderableState.SHOOTING;
				}
				
				break;
				
			case UNLOAD_PREPARING: case UNLOAD_REQUESTED: case UNLOAD:
				currentState = RenderableState.UNLOADING;
				break;
				
			case LOAD:
				currentState = RenderableState.RELOADING;
				break;
				
			case EJECTING:
				currentState = RenderableState.EJECT_SPENT_ROUND;
				break;
				
			case MODIFYING: case MODIFYING_REQUESTED: case NEXT_ATTACHMENT: case NEXT_ATTACHMENT_REQUESTED:
				currentState = RenderableState.MODIFYING;
				break;	
				
			default:
				if(player.isSprinting() && builder.firstPersonPositioningRunning != null) {
					currentState = RenderableState.RUNNING;
				} else if(playerWeaponInstance.isAimed()) {
					currentState = RenderableState.ZOOMING;
					rate = builder.zoomRandomizingRate;
					amplitude = builder.zoomRandomizingAmplitude;
				}
			}
			

			logger.trace("Rendering state {} created from {}", currentState, asyncWeaponState.getState());
		}
		
		if(currentState == null) {
			currentState = RenderableState.NORMAL;
		}
		
		MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager = firstPersonStateManagers.get(player);
		if(stateManager == null) {
			stateManager = new MultipartRenderStateManager<>(currentState, weaponTransitionProvider, Part.WEAPON);
			firstPersonStateManagers.put(player, stateManager);
		} else {
			stateManager.setState(currentState, true, currentState == RenderableState.SHOOTING
					|| currentState == RenderableState.ZOOMING_SHOOTING);
		}
		
		
		return new StateDescriptor(playerWeaponInstance, stateManager, rate, amplitude);
	}

	private AsyncWeaponState getNextNonExpiredState(PlayerWeaponInstance playerWeaponState) {
		AsyncWeaponState asyncWeaponState = null;
		while((asyncWeaponState = playerWeaponState.nextHistoryState()) != null) {
			
			if(System.currentTimeMillis() < asyncWeaponState.getTimestamp() + asyncWeaponState.getDuration()) {
				if(asyncWeaponState.getState() == WeaponState.FIRING 
						&& (hasRecoilPositioning() || !playerWeaponState.isAutomaticModeEnabled())) { // allow recoil for non-automatic weapons
					continue;
				} else {
					break; // found non-expired-state
				}
			}
		}	
		
		return asyncWeaponState;
	}
	
	private BiConsumer<Part, RenderContext> createWeaponPartPositionFunction(Transition t) {
		Consumer<RenderContext> weaponPositionFunction = t.getItemPositioning();
		if(weaponPositionFunction != null) {
			return (part, context) -> weaponPositionFunction.accept(context);
		} 
		
		return (part, context) -> {};
		
	}
	
	private BiConsumer<Part, RenderContext> createWeaponPartPositionFunction(Consumer<RenderContext> weaponPositionFunction) {
		if(weaponPositionFunction != null) {
			return (part, context) -> weaponPositionFunction.accept(context);
		} 
		return (part, context) -> {};
		
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
					.withPartPositionFunction(Part.WEAPON, createWeaponPartPositionFunction(p))
					.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(l))
					.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(r));
			
			for(Entry<Part, List<Transition>> e: custom.entrySet()){
				Transition partTransition = e.getValue().get(i);
				t.withPartPositionFunction(e.getKey(), createWeaponPartPositionFunction(partTransition));
			}
	
			result.add(t);
		}
		return result;
	}
	
	private List<MultipartTransition<Part, RenderContext>> getSimpleTransition(
			Consumer<RenderContext> w,
			Consumer<RenderContext> lh,
			Consumer<RenderContext> rh,
			//Consumer<RenderContext> m,
			LinkedHashMap<Part, Consumer<RenderContext>> custom,
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
						builder.firstPersonCustomPositioningRecoiled,
						builder.recoilAnimationDuration);
			case SHOOTING:
				return getSimpleTransition(builder.firstPersonPositioningShooting, 
						builder.firstPersonLeftHandPositioningShooting,
						builder.firstPersonRightHandPositioningShooting,
						builder.firstPersonCustomPositioning,
						builder.shootingAnimationDuration);
			case EJECT_SPENT_ROUND:
				return getComplexTransition(builder.firstPersonPositioningEjectSpentRound, 
						builder.firstPersonLeftHandPositioningEjectSpentRound,
						builder.firstPersonRightHandPositioningEjectSpentRound,
						builder.firstPersonCustomPositioningEjectSpentRound
						);
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
			case ZOOMING_SHOOTING:
				return getSimpleTransition(builder.firstPersonPositioningZoomingShooting, 
						builder.firstPersonLeftHandPositioningZooming,
						builder.firstPersonRightHandPositioningZooming,
						builder.firstPersonCustomPositioningZoomingShooting,
						60);
			case ZOOMING_RECOILED:
				return getSimpleTransition(builder.firstPersonPositioningZoomingRecoiled, 
						builder.firstPersonLeftHandPositioningZooming,
						builder.firstPersonRightHandPositioningZooming,
						builder.firstPersonCustomPositioningZoomingRecoiled,
						60);
			default:
				break;
			}
			return null;
		}
	}
	
	@Override
	public void renderAttachments(Positioner<Part, RenderContext> positioner, RenderContext renderContext,List<CompatibleAttachment<? extends AttachmentContainer>> attachments) {
		
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null) {
				renderCompatibleAttachment(compatibleAttachment, positioner, renderContext);
			}
		}
	}

	private void renderCompatibleAttachment(CompatibleAttachment<?> compatibleAttachment,
			Positioner<Part, RenderContext> positioner, RenderContext renderContext) {
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
		
		if(compatibleAttachment.getPositioning() != null) {
			compatibleAttachment.getPositioning().accept(renderContext.getPlayer(), renderContext.getWeapon());
		}
		
		ItemAttachment<?> itemAttachment = compatibleAttachment.getAttachment();
		
		
		if(positioner != null) {
			if(itemAttachment instanceof Part) {
				positioner.position((Part) itemAttachment, renderContext);
			} else if(itemAttachment.getRenderablePart() != null) {
				positioner.position(itemAttachment.getRenderablePart(), renderContext);
			}
		}

		for(Tuple<ModelBase, String> texturedModel: compatibleAttachment.getAttachment().getTexturedModels()) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId() 
					+ ":textures/models/" + texturedModel.getV()));
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
			if(compatibleAttachment.getModelPositioning() != null) {
				compatibleAttachment.getModelPositioning().accept(texturedModel.getU());
			}
			texturedModel.getU().render(renderContext.getPlayer(), 
					renderContext.getLimbSwing(), 
					renderContext.getFlimbSwingAmount(), 
					renderContext.getAgeInTicks(), 
					renderContext.getNetHeadYaw(), 
					renderContext.getHeadPitch(), 
					renderContext.getScale());

			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		
		CustomRenderer postRenderer = compatibleAttachment.getAttachment().getPostRenderer();
		if(postRenderer != null) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
			postRenderer.render(renderContext);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		
		for(CompatibleAttachment<?> childAttachment: itemAttachment.getAttachments()) {
			renderCompatibleAttachment(childAttachment, positioner, renderContext);
		}
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	public boolean hasRecoilPositioning() {
		return builder.hasRecoilPositioningDefined;
	}
}
