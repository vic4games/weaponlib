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

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;
import com.vicmatskiv.weaponlib.animation.Transition;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class WeaponRenderer extends CompatibleWeaponRenderer {
	
	private static final float DEFAULT_RANDOMIZING_RATE = 0.33f;
	private static final float DEFAULT_RANDOMIZING_FIRING_RATE = 20;
	private static final float DEFAULT_RANDOMIZING_ZOOM_RATE = 0.25f;
	
	private static final float DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE = 0.06f;
	private static final float DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE = 0.01f;
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
		private BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
		
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningZooming;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningRunning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningModifying;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningRecoiled;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningShooting;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningZoomingRecoiled;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningZoomingShooting;
		
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

		private List<Transition> firstPersonPositioningReloading;
		private List<Transition> firstPersonLeftHandPositioningReloading;
		private List<Transition> firstPersonRightHandPositioningReloading;
		
		private List<Transition> firstPersonPositioningUnloading;
		private List<Transition> firstPersonLeftHandPositioningUnloading;
		private List<Transition> firstPersonRightHandPositioningUnloading;
		
		private String modId;
		
		private int recoilAnimationDuration = DEFAULT_RECOIL_ANIMATION_DURATION;
		private int shootingAnimationDuration = DEFAULT_SHOOTING_ANIMATION_DURATION;
		
		private float normalRandomizingRate = DEFAULT_RANDOMIZING_RATE; // movements per second, e.g. 0.25 = 0.25 movements per second = 1 movement in 3 minutes
		private float firingRandomizingRate = DEFAULT_RANDOMIZING_FIRING_RATE; // movements per second, e.g. 20 = 20 movements per second = 1 movement in 50 ms
		private float zoomRandomizingRate = DEFAULT_RANDOMIZING_ZOOM_RATE;
		
		private float normalRandomizingAmplitude = DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE;
		private float zoomRandomizingAmplitude = DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE;
		private float firingRandomizingAmplitude = DEFAULT_FIRING_RANDOMIZING_AMPLITUDE;
		
		private LinkedHashMap<Part, BiConsumer<EntityPlayer, ItemStack>> firstPersonCustomPositioning = new LinkedHashMap<>();
		private LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningUnloading = new LinkedHashMap<>();
		private LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningReloading = new LinkedHashMap<>();
		private LinkedHashMap<Part, BiConsumer<EntityPlayer, ItemStack>> firstPersonCustomPositioningRecoiled = new LinkedHashMap<>();
		private LinkedHashMap<Part, BiConsumer<EntityPlayer, ItemStack>> firstPersonCustomPositioningZoomingRecoiled = new LinkedHashMap<>();
		private LinkedHashMap<Part, BiConsumer<EntityPlayer, ItemStack>> firstPersonCustomPositioningZoomingShooting = new LinkedHashMap<>();
		
		private List<Transition> firstPersonPositioningEjectSpentRound;
		private List<Transition> firstPersonLeftHandPositioningEjectSpentRound;
		private List<Transition> firstPersonRightHandPositioningEjectSpentRound;
		private LinkedHashMap<Part, List<Transition>> firstPersonCustomPositioningEjectSpentRound = new LinkedHashMap<>();

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
		
		public Builder withFirstPersonPositioningZoomingRecoiled(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningZoomingRecoiled) {
			this.firstPersonPositioningZoomingRecoiled = firstPersonPositioningZoomingRecoiled;
			return this;
		}
		
		public Builder withFirstPersonPositioningZoomingShooting(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningZoomingShooting) {
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
				BiConsumer<EntityPlayer, ItemStack> leftHand,
				BiConsumer<EntityPlayer, ItemStack> rightHand)
		{
			this.firstPersonLeftHandPositioningModifying = leftHand;
			this.firstPersonRightHandPositioningModifying = rightHand;
			return this;
		}
		
		public Builder withFirstPersonCustomPositioning(Part part, BiConsumer<EntityPlayer, ItemStack> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioning.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonPositioningCustomRecoiled(Part part, BiConsumer<EntityPlayer, ItemStack> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningRecoiled.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonPositioningCustomZoomingShooting(Part part, BiConsumer<EntityPlayer, ItemStack> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningZoomingShooting.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonPositioningCustomZoomingRecoiled(Part part, BiConsumer<EntityPlayer, ItemStack> positioning) {
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
			
			if(firstPersonPositioning == null) {
				firstPersonPositioning = (player, itemStack) -> {
					GL11.glRotatef(45F, 0f, 1f, 0f);
					if(compatibility.getTagCompound(itemStack) != null && Tags.getZoom(itemStack) != 1.0f) {
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
				firstPersonPositioningShooting = firstPersonPositioning;
			}
			
			if(firstPersonPositioningZoomingRecoiled == null) {
				firstPersonPositioningZoomingRecoiled = firstPersonPositioningZooming;
			}
			
			if(firstPersonPositioningZoomingShooting == null) {
				firstPersonPositioningZoomingShooting = firstPersonPositioningZooming;
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
			
			return new WeaponRenderer(this);
		}

		public Consumer<ItemStack> getEntityPositioning() {
			return entityPositioning;
		}

		public Consumer<ItemStack> getInventoryPositioning() {
			return inventoryPositioning;
		}

		public BiConsumer<EntityPlayer, ItemStack> getThirdPersonPositioning() {
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
		Weapon weapon = (Weapon) itemStack.getItem();
		if(Weapon.isModifying(itemStack)) {
			currentState = builder.firstPersonPositioningModifying != null ? RenderableState.MODIFYING : RenderableState.NORMAL;
		} else if(Weapon.isUnloadingStarted(player, itemStack)) {
			currentState = RenderableState.UNLOADING;
		} else if(Weapon.isReloadingConfirmed(player, itemStack)) {
			currentState = RenderableState.RELOADING;
		} else if(Weapon.isEjectedSpentRound(player, itemStack)) {
			currentState = RenderableState.EJECT_SPENT_ROUND;
		} else if(player.isSprinting() && builder.firstPersonPositioningRunning != null) {
			currentState = RenderableState.RUNNING;
		} else if(Weapon.isZoomed(player, itemStack)) {
			WeaponClientStorage storage = weapon.getWeaponClientStorage(player);

			if(storage != null) {
				currentState = storage.getNextDisposableRenderableState();
				if(currentState == RenderableState.AUTO_SHOOTING) {
					currentState = RenderableState.ZOOMING;
					rate = builder.firingRandomizingRate;
				} else if(currentState == RenderableState.SHOOTING) {
					currentState = RenderableState.ZOOMING_SHOOTING;
					rate = builder.firingRandomizingRate;
				} else if(currentState == RenderableState.RECOILED) {
					currentState = RenderableState.ZOOMING_RECOILED;
					rate = builder.zoomRandomizingRate;
				} else {
					currentState = RenderableState.ZOOMING;
					rate = builder.zoomRandomizingRate;
				}
				
			}
			amplitude = builder.zoomRandomizingAmplitude; // Zoom amplitude is enforced even when firing
			//System.out.println("Rendering state: " + currentState);
		} else {
			WeaponClientStorage storage = weapon.getWeaponClientStorage(player);

			if(storage != null) {
				currentState = storage.getNextDisposableRenderableState();
				if(currentState == RenderableState.AUTO_SHOOTING) {
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
			stateManager.setState(currentState, true, currentState == RenderableState.SHOOTING
					|| currentState == RenderableState.ZOOMING_SHOOTING);
		}
		
		return new StateDescriptor(stateManager, rate, amplitude);
	}
	
	private BiConsumer<Part, RenderContext> createWeaponPartPositionFunction(BiConsumer<EntityPlayer, ItemStack> weaponPositionFunction) {
		return (part, context) -> weaponPositionFunction.accept(context.getPlayer(), context.getWeapon());
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
}
