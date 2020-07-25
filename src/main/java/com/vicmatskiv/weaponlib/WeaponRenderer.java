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
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.DebugPositioner.TransitionConfiguration;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;
import com.vicmatskiv.weaponlib.animation.Transition;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;
import com.vicmatskiv.weaponlib.compatibility.Interceptors;
import com.vicmatskiv.weaponlib.config.Projectiles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

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
	private static final int DEFAULT_ITERATION_COMPLETED_ANIMATION_DURATION = 100;
	private static final int DEFAULT_PREPARE_FIRST_LOAD_ITERATION_ANIMATION_DURATION = 100;
	private static final int DEFAULT_ALL_LOAD_ITERATION_ANIMATIONS_COMPLETED_DURATION = 100;

	public static class Builder {
	    
        private Random random = new Random();

		private ModelBase model;
		private String textureName;
		private float weaponProximity;
		private float yOffsetZoom;
		private float xOffsetZoom = 0.69F;

		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private Consumer<RenderContext<RenderableState>> thirdPersonPositioning;

		private Consumer<RenderContext<RenderableState>> firstPersonPositioning;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningProning;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningZooming;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningRunning;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningModifying;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningModifyingAlt;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningRecoiled;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningProningRecoiled;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningShooting;
	    private Consumer<RenderContext<RenderableState>> firstPersonPositioningProningShooting;

		private Consumer<RenderContext<RenderableState>> firstPersonPositioningZoomingRecoiled;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningZoomingShooting;
		private Consumer<RenderContext<RenderableState>> firstPersonPositioningLoadIterationCompleted;

		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioning;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningProning;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningZooming;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningRunning;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningModifying;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningModifyingAlt;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningRecoiled;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningProningRecoiled;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningShooting;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningProningShooting;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningLoadIterationCompleted;

		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioning;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningProning;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningZooming;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningRunning;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningModifying;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningModifyingAlt;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningRecoiled;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningProningRecoiled;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningShooting;
	    private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningProningShooting;

	    private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningLoadIterationCompleted;

		private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningReloading;
		private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningReloading;
		private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningReloading;
		
	    private List<Transition<RenderContext<RenderableState>>> thirdPersonPositioningReloading;
	    private List<Transition<RenderContext<RenderableState>>> thirdPersonLeftHandPositioningReloading;
	    private List<Transition<RenderContext<RenderableState>>> thirdPersonRightHandPositioningReloading;
	        
	    private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningInspecting;
	    private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningInspecting;
	    private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningInspecting;
	    
	    private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningDrawing;
        private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningDrawing;
        private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningDrawing;

		private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningUnloading;
		private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningUnloading;
		private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningUnloading;
		
	    private List<Transition<RenderContext<RenderableState>>> thirdPersonPositioningUnloading;
	    private List<Transition<RenderContext<RenderableState>>> thirdPersonLeftHandPositioningUnloading;
	    private List<Transition<RenderContext<RenderableState>>> thirdPersonRightHandPositioningUnloading;
	        
		private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningLoadIteration;
        private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningLoadIteration;
        private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningLoadIteration;
        
        private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningAllLoadIterationsCompleted;
        private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningAllLoadIterationsCompleted;
        private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningAllLoadIterationsCompleted;

		private long totalReloadingDuration;
		private long totalUnloadingDuration;
		private long totalDrawingDuration;
		private long totalLoadIterationDuration;

		private String modId;

		private int recoilAnimationDuration = DEFAULT_RECOIL_ANIMATION_DURATION;
		private int shootingAnimationDuration = DEFAULT_SHOOTING_ANIMATION_DURATION;
		private int loadIterationCompletedAnimationDuration = DEFAULT_ITERATION_COMPLETED_ANIMATION_DURATION;
	    private int prepareFirstLoadIterationAnimationDuration = DEFAULT_PREPARE_FIRST_LOAD_ITERATION_ANIMATION_DURATION;
	    private int allLoadIterationAnimationsCompletedDuration = DEFAULT_ALL_LOAD_ITERATION_ANIMATIONS_COMPLETED_DURATION;

		private float normalRandomizingRate = DEFAULT_RANDOMIZING_RATE; // movements per second, e.g. 0.25 = 0.25 movements per second = 1 movement in 3 minutes
		private float firingRandomizingRate = DEFAULT_RANDOMIZING_FIRING_RATE; // movements per second, e.g. 20 = 20 movements per second = 1 movement in 50 ms
		private float zoomRandomizingRate = DEFAULT_RANDOMIZING_ZOOM_RATE;

		private float normalRandomizingAmplitude = DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE;
		private float zoomRandomizingAmplitude = DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE;
		private float firingRandomizingAmplitude = DEFAULT_FIRING_RANDOMIZING_AMPLITUDE;

		private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioning = new LinkedHashMap<>();
	    private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningProning = new LinkedHashMap<>();

		private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningUnloading = new LinkedHashMap<>();
		private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningReloading = new LinkedHashMap<>();

	    private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> thirdPersonCustomPositioningUnloading = new LinkedHashMap<>();
	    private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> thirdPersonCustomPositioningReloading = new LinkedHashMap<>();
		
	    private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningLoadIteration = new LinkedHashMap<>();
        private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningLoadIterationsCompleted = new LinkedHashMap<>();
        private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningInspecting = new LinkedHashMap<>();
        private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningDrawing = new LinkedHashMap<>();

		private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningRecoiled = new LinkedHashMap<>();
		private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningProningRecoiled = new LinkedHashMap<>();
        private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningZoomingRecoiled = new LinkedHashMap<>();
		private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningZoomingShooting = new LinkedHashMap<>();
	    private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningLoadIterationCompleted = new LinkedHashMap<>();

	    private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningZooming = new LinkedHashMap<>();

		private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningEjectSpentRound;
		private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningEjectSpentRound;
		private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningEjectSpentRound;
		private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningEjectSpentRound = new LinkedHashMap<>();

		private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningEjectSpentRoundAimed;
        private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningEjectSpentRoundAimed;
        private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningEjectSpentRoundAimed;
        private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningEjectSpentRoundAimed = new LinkedHashMap<>();

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
		
		public Builder withPrepareFirstLoadIterationAnimationDuration(int prepareFirstLoadIterationAnimationDuration) {
            this.prepareFirstLoadIterationAnimationDuration = prepareFirstLoadIterationAnimationDuration;
            return this;
        }

        public Builder withAllLoadIterationAnimationsCompletedDuration(int allLoadIterationAnimationsCompletedDuration) {
            this.allLoadIterationAnimationsCompletedDuration = allLoadIterationAnimationsCompletedDuration;
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

		public Builder withThirdPersonPositioning(Consumer<RenderContext<RenderableState>> thirdPersonPositioning) {
			this.thirdPersonPositioning = thirdPersonPositioning;
			return this;
		}

		public Builder withFirstPersonPositioning(Consumer<RenderContext<RenderableState>> firstPersonPositioning) {
			this.firstPersonPositioning = firstPersonPositioning;
			return this;
		}
		
		public Builder withFirstPersonPositioningProning(Consumer<RenderContext<RenderableState>> firstPersonPositioningProning) {
		    this.firstPersonPositioningProning = firstPersonPositioningProning;
		    return this;
		}

		public Builder withFirstPersonPositioningRunning(Consumer<RenderContext<RenderableState>> firstPersonPositioningRunning) {
			this.firstPersonPositioningRunning = firstPersonPositioningRunning;
			return this;
		}

		public Builder withFirstPersonPositioningZooming(Consumer<RenderContext<RenderableState>> firstPersonPositioningZooming) {
			this.firstPersonPositioningZooming = firstPersonPositioningZooming;
			return this;
		}

		public Builder withFirstPersonPositioningRecoiled(Consumer<RenderContext<RenderableState>> firstPersonPositioningRecoiled) {
			this.hasRecoilPositioningDefined = true;
			this.firstPersonPositioningRecoiled = firstPersonPositioningRecoiled;
			return this;
		}
		
		public Builder withFirstPersonPositioningProningRecoiled(Consumer<RenderContext<RenderableState>> firstPersonPositioningProningRecoiled) {
		    this.firstPersonPositioningProningRecoiled = firstPersonPositioningProningRecoiled;
		    return this;
		}

		public Builder withFirstPersonPositioningShooting(Consumer<RenderContext<RenderableState>> firstPersonPositioningShooting) {
			this.firstPersonPositioningShooting = firstPersonPositioningShooting;
			return this;
		}

		public Builder withFirstPersonPositioningProningShooting(Consumer<RenderContext<RenderableState>> firstPersonPositioningProningShooting) {
		    this.firstPersonPositioningProningShooting = firstPersonPositioningProningShooting;
		    return this;
		}

		public Builder withFirstPersonPositioningZoomingRecoiled(Consumer<RenderContext<RenderableState>> firstPersonPositioningZoomingRecoiled) {
			this.firstPersonPositioningZoomingRecoiled = firstPersonPositioningZoomingRecoiled;
			return this;
		}

		public Builder withFirstPersonPositioningZoomingShooting(Consumer<RenderContext<RenderableState>> firstPersonPositioningZoomingShooting) {
			this.firstPersonPositioningZoomingShooting = firstPersonPositioningZoomingShooting;
			return this;
		}
		
		public Builder withFirstPersonPositioningLoadIterationCompleted(Consumer<RenderContext<RenderableState>> firstPersonPositioningLoadIterationCompleted) {
            this.firstPersonPositioningLoadIterationCompleted = firstPersonPositioningLoadIterationCompleted;
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonPositioningReloading(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonPositioningReloading = Arrays.asList(transitions);
			return this;
		}

		@SafeVarargs
		public final Builder withFirstPersonPositioningUnloading(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonPositioningUnloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
        public final Builder withThirdPersonPositioningReloading(Transition<RenderContext<RenderableState>> ...transitions) {
            this.thirdPersonPositioningReloading = Arrays.asList(transitions);
            return this;
        }

        @SafeVarargs
        public final Builder withThirdPersonPositioningUnloading(Transition<RenderContext<RenderableState>> ...transitions) {
            this.thirdPersonPositioningUnloading = Arrays.asList(transitions);
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonPositioningInspecting(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonPositioningInspecting = Arrays.asList(transitions);
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonPositioningDrawing(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonPositioningDrawing = Arrays.asList(transitions);
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonPositioningLoadIteration(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonPositioningLoadIteration = Arrays.asList(transitions);
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonPositioningAllLoadIterationsCompleted(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonPositioningAllLoadIterationsCompleted = Arrays.asList(transitions);
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonPositioningEjectSpentRound(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonPositioningEjectSpentRound = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
        public final Builder withFirstPersonPositioningEjectSpentRoundAimed(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonPositioningEjectSpentRoundAimed = Arrays.asList(transitions);
            return this;
        }

		public Builder withFirstPersonPositioningModifying(Consumer<RenderContext<RenderableState>> firstPersonPositioningModifying) {
			this.firstPersonPositioningModifying = firstPersonPositioningModifying;
			return this;
		}
		
		public Builder withFirstPersonPositioningModifyingAlt(Consumer<RenderContext<RenderableState>> firstPersonPositioningModifyingAlt) {
            this.firstPersonPositioningModifyingAlt = firstPersonPositioningModifyingAlt;
            return this;
        }


		public Builder withFirstPersonHandPositioning(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			this.firstPersonLeftHandPositioning = leftHand;
			this.firstPersonRightHandPositioning = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningProning(
		        Consumer<RenderContext<RenderableState>> leftHand,
		        Consumer<RenderContext<RenderableState>> rightHand)
		{
		    this.firstPersonLeftHandPositioningProning = leftHand;
		    this.firstPersonRightHandPositioningProning = rightHand;
		    return this;
		}

		public Builder withFirstPersonHandPositioningRunning(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			this.firstPersonLeftHandPositioningRunning = leftHand;
			this.firstPersonRightHandPositioningRunning = rightHand;
			return this;
		}

		public Builder withFirstPersonHandPositioningZooming(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			this.firstPersonLeftHandPositioningZooming = leftHand;
			this.firstPersonRightHandPositioningZooming = rightHand;
			return this;
		}

		public Builder withFirstPersonHandPositioningRecoiled(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			this.firstPersonLeftHandPositioningRecoiled = leftHand;
			this.firstPersonRightHandPositioningRecoiled = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningProningRecoiled(
		        Consumer<RenderContext<RenderableState>> leftHand,
		        Consumer<RenderContext<RenderableState>> rightHand)
		{
		    this.firstPersonLeftHandPositioningProningRecoiled = leftHand;
		    this.firstPersonRightHandPositioningProningRecoiled = rightHand;
		    return this;
		}

		public Builder withFirstPersonHandPositioningShooting(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			this.firstPersonLeftHandPositioningShooting = leftHand;
			this.firstPersonRightHandPositioningShooting = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningProningShooting(
		        Consumer<RenderContext<RenderableState>> leftHand,
		        Consumer<RenderContext<RenderableState>> rightHand)
		{
		    this.firstPersonLeftHandPositioningProningShooting = leftHand;
		    this.firstPersonRightHandPositioningProningShooting = rightHand;
		    return this;
		}
		
		public Builder withFirstPersonHandPositioningLoadIterationCompleted(
                Consumer<RenderContext<RenderableState>> leftHand,
                Consumer<RenderContext<RenderableState>> rightHand)
        {
            this.firstPersonLeftHandPositioningLoadIterationCompleted = leftHand;
            this.firstPersonRightHandPositioningLoadIterationCompleted = rightHand;
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonLeftHandPositioningReloading(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonLeftHandPositioningReloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withThirdPersonLeftHandPositioningReloading(Transition<RenderContext<RenderableState>> ...transitions) {
		    this.thirdPersonLeftHandPositioningReloading = Arrays.asList(transitions);
		    return this;
		}
		
		@SafeVarargs
        public final Builder withFirstPersonLeftHandPositioningInspecting(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonLeftHandPositioningInspecting = Arrays.asList(transitions);
            return this;
        }
		
	      @SafeVarargs
	        public final Builder withFirstPersonLeftHandPositioningDrawing(Transition<RenderContext<RenderableState>> ...transitions) {
	            this.firstPersonLeftHandPositioningDrawing = Arrays.asList(transitions);
	            return this;
	        }

		@SafeVarargs
		public final Builder withFirstPersonLeftHandPositioningEjectSpentRound(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonLeftHandPositioningEjectSpentRound = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
        public final Builder withFirstPersonLeftHandPositioningEjectSpentRoundAimed(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonLeftHandPositioningEjectSpentRoundAimed = Arrays.asList(transitions);
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonLeftHandPositioningUnloading(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonLeftHandPositioningUnloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withThirdPersonLeftHandPositioningUnloading(Transition<RenderContext<RenderableState>> ...transitions) {
		    this.thirdPersonLeftHandPositioningUnloading = Arrays.asList(transitions);
		    return this;
		}
		
		@SafeVarargs
        public final Builder withFirstPersonLeftHandPositioningLoadIteration(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonLeftHandPositioningLoadIteration = Arrays.asList(transitions);
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonLeftHandPositioningAllLoadIterationsCompleted(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonLeftHandPositioningAllLoadIterationsCompleted = Arrays.asList(transitions);
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningReloading(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonRightHandPositioningReloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withThirdPersonRightHandPositioningReloading(Transition<RenderContext<RenderableState>> ...transitions) {
		    this.thirdPersonRightHandPositioningReloading = Arrays.asList(transitions);
		    return this;
		}

		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningUnloading(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonRightHandPositioningUnloading = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
		public final Builder withThirdPersonRightHandPositioningUnloading(Transition<RenderContext<RenderableState>> ...transitions) {
		    this.thirdPersonRightHandPositioningUnloading = Arrays.asList(transitions);
		    return this;
		}
		
		@SafeVarargs
        public final Builder withFirstPersonRightHandPositioningInspecting(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonRightHandPositioningInspecting = Arrays.asList(transitions);
            return this;
        }
		
		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningDrawing(Transition<RenderContext<RenderableState>> ...transitions) {
		    this.firstPersonRightHandPositioningDrawing = Arrays.asList(transitions);
		    return this;
		}

		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningEjectSpentRound(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonRightHandPositioningEjectSpentRound = Arrays.asList(transitions);
			return this;
		}
		
		@SafeVarargs
        public final Builder withFirstPersonRightHandPositioningEjectSpentRoundAimed(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonRightHandPositioningEjectSpentRoundAimed = Arrays.asList(transitions);
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonRightHandPositioningLoadIteration(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonRightHandPositioningLoadIteration = Arrays.asList(transitions);
            return this;
        }
        
        @SafeVarargs
        public final Builder withFirstPersonRightHandPositioningAllLoadIterationsCompleted(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonRightHandPositioningAllLoadIterationsCompleted = Arrays.asList(transitions);
            return this;
        }

		public Builder withFirstPersonHandPositioningModifying(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			this.firstPersonLeftHandPositioningModifying = leftHand;
			this.firstPersonRightHandPositioningModifying = rightHand;
			return this;
		}
		
		public Builder withFirstPersonHandPositioningModifyingAlt(
                Consumer<RenderContext<RenderableState>> leftHand,
                Consumer<RenderContext<RenderableState>> rightHand)
        {
            this.firstPersonLeftHandPositioningModifyingAlt = leftHand;
            this.firstPersonRightHandPositioningModifyingAlt = rightHand;
            return this;
        }

		public Builder withFirstPersonCustomPositioning(Part part, Consumer<RenderContext<RenderableState>> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioning.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonCustomPositioningProning(Part part, Consumer<RenderContext<RenderableState>> positioning) {
		    if(part instanceof DefaultPart) {
		        throw new IllegalArgumentException("Part " + part + " is not custom");
		    }
		    if(this.firstPersonCustomPositioningProning.put(part, positioning) != null) {
		        throw new IllegalArgumentException("Part " + part + " already added");
		    }
		    return this;
		}
		
		public Builder withFirstPersonCustomPositioningZooming(Part part, Consumer<RenderContext<RenderableState>> positioning) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            if(this.firstPersonCustomPositioningZooming.put(part, positioning) != null) {
                throw new IllegalArgumentException("Part " + part + " already added");
            }
            return this;
        }

		public Builder withFirstPersonPositioningCustomRecoiled(Part part, Consumer<RenderContext<RenderableState>> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningRecoiled.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}
		
		public Builder withFirstPersonPositioningCustomProningRecoiled(Part part, Consumer<RenderContext<RenderableState>> positioning) {
		    if(part instanceof DefaultPart) {
		        throw new IllegalArgumentException("Part " + part + " is not custom");
		    }
		    if(this.firstPersonCustomPositioningProningRecoiled.put(part, positioning) != null) {
		        throw new IllegalArgumentException("Part " + part + " already added");
		    }
		    return this;
		}

		public Builder withFirstPersonPositioningCustomZoomingShooting(Part part, Consumer<RenderContext<RenderableState>> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningZoomingShooting.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}

		public Builder withFirstPersonPositioningCustomZoomingRecoiled(Part part, Consumer<RenderContext<RenderableState>> positioning) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningZoomingRecoiled.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}
			return this;
		}

		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningReloading(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}

			this.firstPersonCustomPositioningReloading.put(part, Arrays.asList(transitions));
			return this;
		}
		
	      @SafeVarargs
	      public final Builder withThirdPersonCustomPositioningReloading(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
	          if(part instanceof DefaultPart) {
	              throw new IllegalArgumentException("Part " + part + " is not custom");
	          }

	          this.thirdPersonCustomPositioningReloading.put(part, Arrays.asList(transitions));
	          return this;
	      }
	      
		@SafeVarargs
        public final Builder withFirstPersonCustomPositioningInspecting(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            this.firstPersonCustomPositioningInspecting.put(part, Arrays.asList(transitions));
            return this;
        }
		
		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningDrawing(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
		    if(part instanceof DefaultPart) {
		        throw new IllegalArgumentException("Part " + part + " is not custom");
		    }

		    this.firstPersonCustomPositioningDrawing.put(part, Arrays.asList(transitions));
		    return this;
		}
		
		public Builder withFirstPersonCustomPositioningLoadIterationCompleted(Part part, Consumer<RenderContext<RenderableState>> positioning) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            if(this.firstPersonCustomPositioningLoadIterationCompleted.put(part, positioning) != null) {
                throw new IllegalArgumentException("Part " + part + " already added");
            }
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningUnloading(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			this.firstPersonCustomPositioningUnloading.put(part, Arrays.asList(transitions));
			return this;
		}
		
		@SafeVarargs
        public final Builder withThirdPersonCustomPositioningUnloading(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            this.thirdPersonCustomPositioningUnloading.put(part, Arrays.asList(transitions));
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningEjectSpentRound(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}

			this.firstPersonCustomPositioningEjectSpentRound.put(part, Arrays.asList(transitions));
			return this;
		}
		
		@SafeVarargs
        public final Builder withFirstPersonCustomPositioningEjectSpentRoundAimed(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            this.firstPersonCustomPositioningEjectSpentRoundAimed.put(part, Arrays.asList(transitions));
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonCustomPositioningLoadIteration(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            this.firstPersonCustomPositioningLoadIteration.put(part, Arrays.asList(transitions));
            return this;
        }
		
		@SafeVarargs
        public final Builder withFirstPersonCustomPositioningAllLoadIterationsCompleted(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            this.firstPersonCustomPositioningLoadIterationsCompleted.put(part, Arrays.asList(transitions));
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
			
			if(firstPersonPositioningProning == null) {
			    firstPersonPositioningProning = firstPersonPositioning;
			}

			if(firstPersonPositioningZooming == null) {
				firstPersonPositioningZooming = firstPersonPositioning;
			}

			if(firstPersonPositioningReloading == null) {
				firstPersonPositioningReloading = Collections.singletonList(new Transition<>(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
	        if(thirdPersonPositioningReloading == null) {
	            // TODO: verify
	            thirdPersonPositioningReloading = Collections.singletonList(new Transition<>(thirdPersonPositioning, DEFAULT_ANIMATION_DURATION));
	        }
			
			if(firstPersonPositioningInspecting == null) {
                firstPersonPositioningInspecting = Collections.singletonList(new Transition<>(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
            }
			
			if(firstPersonPositioningDrawing == null) {
			    firstPersonPositioningDrawing = Collections.singletonList(new Transition<>(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
			if(firstPersonPositioningLoadIteration == null) {
			    firstPersonPositioningLoadIteration = Collections.singletonList(new Transition<>(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
            }

			for(Transition<RenderContext<RenderableState>> t: firstPersonPositioningReloading) {
				totalReloadingDuration += t.getDuration();
				totalReloadingDuration += t.getPause();
			}
			
			//totalLoadIterationDuration
			for(Transition<RenderContext<RenderableState>> t: firstPersonPositioningLoadIteration) {
			    totalLoadIterationDuration += t.getDuration();
			    totalLoadIterationDuration += t.getPause();
            }

			if(firstPersonPositioningUnloading == null) {
				firstPersonPositioningUnloading = Collections.singletonList(new Transition<>(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
	        if(thirdPersonPositioningUnloading == null) {
	            thirdPersonPositioningUnloading = Collections.singletonList(new Transition<>(thirdPersonPositioning, DEFAULT_ANIMATION_DURATION));
	        }

			for(Transition<RenderContext<RenderableState>> t: firstPersonPositioningUnloading) {
				totalUnloadingDuration += t.getDuration();
				totalUnloadingDuration += t.getPause();
			}
			
			for(Transition<RenderContext<RenderableState>> t: firstPersonPositioningDrawing) {
			    totalDrawingDuration += t.getDuration();
			    totalDrawingDuration += t.getPause();
			}
			
			if(firstPersonPositioningLoadIteration == null) {
			    firstPersonPositioningLoadIteration = Collections.singletonList(new Transition<>(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
            }
			
			if(firstPersonPositioningAllLoadIterationsCompleted == null) {
			    firstPersonPositioningAllLoadIterationsCompleted = Collections.singletonList(new Transition<>(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
            }

			if(firstPersonPositioningRecoiled == null) {
				firstPersonPositioningRecoiled = firstPersonPositioning;
			} else {
			    Consumer<RenderContext<RenderableState>> firstPersonPositioningRecoiledOrig = firstPersonPositioningRecoiled;
			    
			    firstPersonPositioningRecoiled = renderContext -> {
			        
			        float maxAngle = 1.5f;
		            float xRotation = random.nextFloat() * maxAngle;
		            float yRotation = random.nextFloat() * maxAngle;
		            float zRotation = random.nextFloat() * maxAngle;

		            GL11.glRotatef(xRotation, 1f, 0f, 0f);
		            GL11.glRotatef(yRotation, 0f, 1f, 0f);
		            GL11.glRotatef(zRotation, 0f, 0f, 1f);

		            float amplitude = 0f;
                    float xRandomOffset = random.nextFloat() * amplitude;
		            float yRandomOffset = random.nextFloat() * amplitude;
		            float zRandomOffset = random.nextFloat() * amplitude;
		            GL11.glTranslatef(xRandomOffset, yRandomOffset, zRandomOffset);
		            
		            firstPersonPositioningRecoiledOrig.accept(renderContext);
			    };
			}
			
			if(firstPersonPositioningProningRecoiled == null) {
			    firstPersonPositioningProningRecoiled = firstPersonPositioningRecoiled;
			}

			if(firstPersonPositioningRunning == null) {
				firstPersonPositioningRunning = firstPersonPositioning;
			}

			if(firstPersonPositioningModifying == null) {
				firstPersonPositioningModifying = firstPersonPositioning;
			}

			if(firstPersonPositioningModifyingAlt == null) {
                firstPersonPositioningModifyingAlt = firstPersonPositioning;
            }

			if(firstPersonPositioningShooting == null) {
				firstPersonPositioningShooting = firstPersonPositioning;
			}
			
			if(firstPersonPositioningProningShooting == null) {
			    firstPersonPositioningProningShooting = firstPersonPositioningProning;
			}

			if(firstPersonPositioningZoomingRecoiled == null) {
				firstPersonPositioningZoomingRecoiled = firstPersonPositioningZooming;
			}

			if(firstPersonPositioningZoomingShooting == null) {
				firstPersonPositioningZoomingShooting = firstPersonPositioningZooming;
			}
			
			if(firstPersonPositioningLoadIterationCompleted == null) {
			    firstPersonPositioningLoadIterationCompleted = firstPersonPositioning;
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
			
			if(firstPersonLeftHandPositioningProning == null) {
                firstPersonLeftHandPositioningProning = firstPersonLeftHandPositioning;
            }

			if(firstPersonLeftHandPositioningReloading == null) {
				firstPersonLeftHandPositioningReloading = firstPersonPositioningReloading.stream().map(
				        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}

			if(firstPersonLeftHandPositioningUnloading == null) {
				firstPersonLeftHandPositioningUnloading = firstPersonPositioningUnloading.stream().map(
				        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}
			
			if(thirdPersonLeftHandPositioningReloading == null) {
			    thirdPersonLeftHandPositioningReloading = thirdPersonPositioningReloading.stream().map(
			            t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}

			if(thirdPersonLeftHandPositioningUnloading == null) {
			    thirdPersonLeftHandPositioningUnloading = thirdPersonPositioningUnloading.stream().map(
			            t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}
			
			if(firstPersonLeftHandPositioningInspecting == null) {
                firstPersonLeftHandPositioningInspecting = firstPersonPositioningInspecting.stream().map(
                        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
            }
			
			if(firstPersonLeftHandPositioningDrawing == null) {
			    firstPersonLeftHandPositioningDrawing = firstPersonPositioningDrawing.stream().map(
			            t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}
			
			if(firstPersonLeftHandPositioningLoadIteration == null) {
			    firstPersonLeftHandPositioningLoadIteration = firstPersonPositioningReloading.stream().map(
                        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
            }
			
			if(firstPersonLeftHandPositioningAllLoadIterationsCompleted == null) {
			    firstPersonLeftHandPositioningAllLoadIterationsCompleted = firstPersonPositioningReloading.stream().map(
                        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
            }

			if(firstPersonLeftHandPositioningRecoiled == null) {
				firstPersonLeftHandPositioningRecoiled = firstPersonLeftHandPositioning;
			}
			
			if(firstPersonLeftHandPositioningProningRecoiled == null) {
			    firstPersonLeftHandPositioningProningRecoiled = firstPersonLeftHandPositioningProning;
            }

			if(firstPersonLeftHandPositioningShooting == null) {
				firstPersonLeftHandPositioningShooting = firstPersonLeftHandPositioning;
			}
			
			if(firstPersonLeftHandPositioningProningShooting == null) {
			    firstPersonLeftHandPositioningProningShooting = firstPersonLeftHandPositioningProning;
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
			
			if(firstPersonLeftHandPositioningModifyingAlt == null) {
                firstPersonLeftHandPositioningModifyingAlt = firstPersonLeftHandPositioning;
            }
			
			if(firstPersonLeftHandPositioningLoadIterationCompleted == null) {
			    firstPersonLeftHandPositioningLoadIterationCompleted = firstPersonLeftHandPositioning;
            }

			// Right hand positioning

			if(firstPersonRightHandPositioning == null) {
				firstPersonRightHandPositioning = (context) -> {};
			}
			
			if(firstPersonRightHandPositioningProning == null) {
			    firstPersonRightHandPositioningProning = firstPersonRightHandPositioning;
			}

			if(firstPersonRightHandPositioningReloading == null) {
				//firstPersonRightHandPositioningReloading = Collections.singletonList(new Transition(firstPersonRightHandPositioning, DEFAULT_ANIMATION_DURATION));
				firstPersonRightHandPositioningReloading = firstPersonPositioningReloading.stream().map(
				        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}

			if(firstPersonRightHandPositioningUnloading == null) {
				firstPersonRightHandPositioningUnloading = firstPersonPositioningUnloading.stream().map(
				        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}
			
			if(thirdPersonRightHandPositioningReloading == null) {
			    //thirdPersonRightHandPositioningReloading = Collections.singletonList(new Transition(thirdPersonRightHandPositioning, DEFAULT_ANIMATION_DURATION));
			    thirdPersonRightHandPositioningReloading = thirdPersonPositioningReloading.stream().map(
			            t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}

			if(thirdPersonRightHandPositioningUnloading == null) {
			    thirdPersonRightHandPositioningUnloading = thirdPersonPositioningUnloading.stream().map(
			            t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}
			
			if(firstPersonRightHandPositioningInspecting == null) {
                firstPersonRightHandPositioningInspecting = firstPersonPositioningInspecting.stream().map(
                        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
            }
			
			if(firstPersonRightHandPositioningDrawing == null) {
			    firstPersonRightHandPositioningDrawing = firstPersonPositioningDrawing.stream().map(
			            t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
			}
			
			if(firstPersonRightHandPositioningLoadIteration == null) {
                firstPersonRightHandPositioningLoadIteration = firstPersonPositioningReloading.stream().map(
                        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
            }
            
            if(firstPersonRightHandPositioningAllLoadIterationsCompleted == null) {
                firstPersonRightHandPositioningAllLoadIterationsCompleted = firstPersonPositioningReloading.stream().map(
                        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
            }

			if(firstPersonRightHandPositioningRecoiled == null) {
				firstPersonRightHandPositioningRecoiled = firstPersonRightHandPositioning;
			}
			
			if(firstPersonRightHandPositioningProningRecoiled == null) {
			    firstPersonRightHandPositioningProningRecoiled = firstPersonRightHandPositioningProning;
            }

			if(firstPersonRightHandPositioningShooting == null) {
				firstPersonRightHandPositioningShooting = firstPersonRightHandPositioning;
			}
			
			if(firstPersonRightHandPositioningProningShooting == null) {
			    firstPersonRightHandPositioningProningShooting = firstPersonRightHandPositioningProning;
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
			
			if(firstPersonRightHandPositioningModifyingAlt == null) {
                firstPersonRightHandPositioningModifyingAlt = firstPersonRightHandPositioning;
            }
			
			if(firstPersonRightHandPositioningLoadIterationCompleted == null) {
			    firstPersonRightHandPositioningLoadIterationCompleted = firstPersonLeftHandPositioning;
            }

			/*
			 * If custom positioning for recoil is not set, default it to normal custom positioning
			 */
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningRecoiled.isEmpty()) {
				firstPersonCustomPositioning.forEach((part, pos) -> {
					firstPersonCustomPositioningRecoiled.put(part, pos);
				});
			}
			
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningProning.isEmpty()) {
                firstPersonCustomPositioning.forEach((part, pos) -> {
                    firstPersonCustomPositioningProning.put(part, pos);
                });
             }
			
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningZooming.isEmpty()) {
			    firstPersonCustomPositioning.forEach((part, pos) -> {
			        firstPersonCustomPositioningZooming.put(part, pos);
			    });
			}

			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningZoomingRecoiled.isEmpty()) {
				firstPersonCustomPositioning.forEach((part, pos) -> {
					firstPersonCustomPositioningZoomingRecoiled.put(part, pos);
				});
			}
			
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningProningRecoiled.isEmpty()) {
			    firstPersonCustomPositioningRecoiled.forEach((part, pos) -> {
                    firstPersonCustomPositioningProningRecoiled.put(part, pos);
                });
            }

			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningZoomingShooting.isEmpty()) {
				firstPersonCustomPositioning.forEach((part, pos) -> {
					firstPersonCustomPositioningZoomingShooting.put(part, pos);
				});
			}
			
			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningLoadIterationCompleted.isEmpty()) {
                firstPersonCustomPositioning.forEach((part, pos) -> {
                    firstPersonCustomPositioningLoadIterationCompleted.put(part, pos);
                });
            }

			firstPersonCustomPositioningReloading.forEach((p, t) -> {
				if(t.size() != firstPersonPositioningReloading.size()) {
					throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + firstPersonPositioningReloading.size()
					+ ", actual: " + t.size());
				}
			});
			
			thirdPersonCustomPositioningReloading.forEach((p, t) -> {
			    if(t.size() != thirdPersonPositioningReloading.size()) {
			        throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + thirdPersonPositioningReloading.size()
			        + ", actual: " + t.size());
			    }
			});

			firstPersonCustomPositioningUnloading.forEach((p, t) -> {
				if(t.size() != firstPersonPositioningUnloading.size()) {
					throw new IllegalStateException("Custom unloading transition number mismatch. Expected " + firstPersonPositioningUnloading.size()
					+ ", actual: " + t.size());
				}
			});
			
			thirdPersonCustomPositioningUnloading.forEach((p, t) -> {
                if(t.size() != thirdPersonPositioningUnloading.size()) {
                    throw new IllegalStateException("Custom unloading transition number mismatch. Expected " + thirdPersonPositioningUnloading.size()
                    + ", actual: " + t.size());
                }
            });
			
			firstPersonCustomPositioningInspecting.forEach((p, t) -> {
			    if(t.size() != firstPersonPositioningInspecting.size()) {
			        throw new IllegalStateException("Custom inspecting transition number mismatch. Expected " + firstPersonPositioningInspecting.size()
			        + ", actual: " + t.size());
			    }
			});
			
			firstPersonCustomPositioningDrawing.forEach((p, t) -> {
			    if(t.size() != firstPersonPositioningDrawing.size()) {
			        throw new IllegalStateException("Custom Drawing transition number mismatch. Expected " + firstPersonPositioningDrawing.size()
			        + ", actual: " + t.size());
			    }
			});

			firstPersonCustomPositioningLoadIteration.forEach((p, t) -> {
                if(t.size() != firstPersonPositioningLoadIteration.size()) {
                    throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + firstPersonPositioningLoadIteration.size()
                    + ", actual: " + t.size());
                }
            });
			
			firstPersonCustomPositioningLoadIterationsCompleted.forEach((p, t) -> {
                if(t.size() != firstPersonPositioningAllLoadIterationsCompleted.size()) {
                    throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + firstPersonPositioningAllLoadIterationsCompleted.size()
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

		public Consumer<RenderContext<RenderableState>> getThirdPersonPositioning() {
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

	private Map<EntityLivingBase, MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>>> firstPersonStateManagers;
    private Map<EntityLivingBase, MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>>> thirdPersonStateManagers;

	private MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> firstPersonTransitionProvider;
	private MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> thirdPersonTransitionProvider;


	protected ClientModContext clientModContext;

	private WeaponRenderer(Builder builder) {
		super(builder);
		this.builder = builder;
		this.firstPersonStateManagers = new HashMap<>();
		this.thirdPersonStateManagers = new HashMap<>();
		this.firstPersonTransitionProvider = new FirstPersonWeaponTransitionProvider();
		this.thirdPersonTransitionProvider = new ThirdPersonWeaponTransitionProvider();
	}

	protected long getTotalReloadingDuration() {
		return builder.totalReloadingDuration;
	}

	protected long getTotalUnloadingDuration() {
		return builder.totalUnloadingDuration;
	}
	
	protected long getTotalDrawingDuration() {
        return builder.totalDrawingDuration;
    }

	protected ClientModContext getClientModContext() {
		return clientModContext;
	}

	protected void setClientModContext(ClientModContext clientModContext) {
		this.clientModContext = clientModContext;
	}

	@Override
	protected StateDescriptor getFirstPersonStateDescriptor(EntityLivingBase player, ItemStack itemStack) {
		float amplitude = builder.normalRandomizingAmplitude;
		float rate = builder.normalRandomizingRate;
		RenderableState currentState = null;

		PlayerItemInstance<?> playerItemInstance = clientModContext.getPlayerItemInstanceRegistry().getItemInstance(player, itemStack);
				//.getMainHandItemInstance(player, PlayerWeaponInstance.class); // TODO: cannot be always main hand, need to which hand from context

		PlayerWeaponInstance playerWeaponInstance = null;
		if(playerItemInstance == null || !(playerItemInstance instanceof PlayerWeaponInstance)
		        || playerItemInstance.getItem() != itemStack.getItem()) {
		    logger.error("Invalid or mismatching item. Player item instance: {}. Item stack: {}", playerItemInstance, itemStack);
		} else {
		    playerWeaponInstance = (PlayerWeaponInstance) playerItemInstance;
		}

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

			case LOAD_ITERATION:
			    currentState = RenderableState.LOAD_ITERATION;
			    break;
			    
			case LOAD_ITERATION_COMPLETED:
                currentState = RenderableState.LOAD_ITERATION_COMPLETED;
                break;
			    
			case ALL_LOAD_ITERATIONS_COMPLETED:
			    currentState = RenderableState.ALL_LOAD_ITERATIONS_COMPLETED;
			    break;
			    
			case EJECTING:
			    if(playerWeaponInstance.isAimed()) {
			        currentState = RenderableState.EJECT_SPENT_ROUND_AIMED;
			    } else {
			        currentState = RenderableState.EJECT_SPENT_ROUND;
			    }
				
				break;

			case MODIFYING: case MODIFYING_REQUESTED: case NEXT_ATTACHMENT: case NEXT_ATTACHMENT_REQUESTED:
			    if(playerWeaponInstance.isAltMofificationModeEnabled()) {
			        currentState = RenderableState.MODIFYING_ALT;
			    } else {
			        currentState = RenderableState.MODIFYING;
			    }
				
				break;
				
			case INSPECTING:
                currentState = RenderableState.INSPECTING;
                break;
                
			case DRAWING:
                currentState = RenderableState.DRAWING;
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


			//logger.trace("Rendering state {} created from {}", currentState, asyncWeaponState.getState());
		}

		if(currentState == null) {
			currentState = RenderableState.NORMAL;
		}
		
        
        if(player instanceof EntityPlayer && Interceptors.isProning((EntityPlayer) player)) {
            switch(currentState) {
            case NORMAL:
                currentState = RenderableState.PRONING;
                break;
            case RECOILED:
                currentState = RenderableState.PRONING_RECOILED;
                break;
            case SHOOTING:
                currentState = RenderableState.PRONING_SHOOTING;
                break;
            default:
                break;
            }
        }


		MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager = firstPersonStateManagers.get(player);
		if(stateManager == null) {
			stateManager = new MultipartRenderStateManager<>(currentState, firstPersonTransitionProvider);
			firstPersonStateManagers.put(player, stateManager);
		} else {
			stateManager.setState(currentState, true, currentState == RenderableState.SHOOTING
			        || currentState == RenderableState.PRONING_SHOOTING
					|| currentState == RenderableState.ZOOMING_SHOOTING
					|| currentState == RenderableState.RUNNING
					|| currentState == RenderableState.ZOOMING
					|| currentState == RenderableState.DRAWING);
		}

		return new StateDescriptor(playerWeaponInstance, stateManager, rate, amplitude);
	}
	
    protected StateDescriptor getThirdPersonStateDescriptor(EntityLivingBase player, ItemStack itemStack) {
        float amplitude = builder.normalRandomizingAmplitude;
        float rate = builder.normalRandomizingRate;
        RenderableState currentState = null;

        PlayerItemInstance<?> playerItemInstance = clientModContext.getPlayerItemInstanceRegistry().getItemInstance(player, itemStack);
                //.getMainHandItemInstance(player, PlayerWeaponInstance.class); // TODO: cannot be always main hand, need to which hand from context

        PlayerWeaponInstance playerWeaponInstance = null;
        if(playerItemInstance == null || !(playerItemInstance instanceof PlayerWeaponInstance)
                || playerItemInstance.getItem() != itemStack.getItem()) {
            logger.error("Invalid or mismatching item. Player item instance: {}. Item stack: {}", playerItemInstance, itemStack);
        } else {
            playerWeaponInstance = (PlayerWeaponInstance) playerItemInstance;
        }

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

            case LOAD_ITERATION:
                currentState = RenderableState.LOAD_ITERATION;
                break;
                
            case LOAD_ITERATION_COMPLETED:
                currentState = RenderableState.LOAD_ITERATION_COMPLETED;
                break;
                
            case ALL_LOAD_ITERATIONS_COMPLETED:
                currentState = RenderableState.ALL_LOAD_ITERATIONS_COMPLETED;
                break;
                
            case EJECTING:
                if(playerWeaponInstance.isAimed()) {
                    currentState = RenderableState.EJECT_SPENT_ROUND_AIMED;
                } else {
                    currentState = RenderableState.EJECT_SPENT_ROUND;
                }
                
                break;

            case MODIFYING: case MODIFYING_REQUESTED: case NEXT_ATTACHMENT: case NEXT_ATTACHMENT_REQUESTED:
                currentState = RenderableState.MODIFYING;
                break;
                
            case INSPECTING:
                currentState = RenderableState.INSPECTING;
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


            //logger.trace("Rendering state {} created from {}", currentState, asyncWeaponState.getState());
        }

        if(currentState == null) {
            currentState = RenderableState.NORMAL;
        }

        MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager = thirdPersonStateManagers.get(player);
        if(stateManager == null) {
            stateManager = new MultipartRenderStateManager<>(currentState, thirdPersonTransitionProvider);
            thirdPersonStateManagers.put(player, stateManager);
        } else {
            stateManager.setState(currentState, true, currentState == RenderableState.SHOOTING
                    || currentState == RenderableState.ZOOMING_SHOOTING
                    || currentState == RenderableState.RUNNING
                    || currentState == RenderableState.ZOOMING);
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

	private Consumer<RenderContext<RenderableState>> createWeaponPartPositionFunction(Transition<RenderContext<RenderableState>> t) {
		if(t == null) {
			return context -> {};
		}
		Consumer<RenderContext<RenderableState>> weaponPositionFunction = t.getItemPositioning();
		if(weaponPositionFunction != null) {
			return context -> weaponPositionFunction.accept(context);
		}

		return context -> {};

	}

	private Consumer<RenderContext<RenderableState>> createWeaponPartPositionFunction(Consumer<RenderContext<RenderableState>> weaponPositionFunction) {
		if(weaponPositionFunction != null) {
			return context -> weaponPositionFunction.accept(context);
		}
		return context -> {};

	}

	private List<MultipartTransition<Part, RenderContext<RenderableState>>> getComplexTransition(
			List<Transition<RenderContext<RenderableState>>> wt,
			List<Transition<RenderContext<RenderableState>>> lht,
			List<Transition<RenderContext<RenderableState>>> rht,
			LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> custom)
	{
		List<MultipartTransition<Part, RenderContext<RenderableState>>> result = new ArrayList<>();
		for(int i = 0; i < wt.size(); i++) {
			Transition<RenderContext<RenderableState>> p = wt.get(i);
			Transition<RenderContext<RenderableState>> l = lht.get(i);
			Transition<RenderContext<RenderableState>> r = rht.get(i);

			long pause = p.getPause();
            if(DebugPositioner.isDebugModeEnabled()) {
                TransitionConfiguration transitionConfiguration = DebugPositioner.getTransitionConfiguration(i, false);
                if(transitionConfiguration != null) {
                    pause = transitionConfiguration.getPause();
                }
            }
			MultipartTransition<Part, RenderContext<RenderableState>> t = new MultipartTransition<Part, RenderContext<RenderableState>>(p.getDuration(), pause)
					.withPartPositionFunction(Part.MAIN_ITEM, createWeaponPartPositionFunction(p))
					.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(l))
					.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(r));

			for(Entry<Part, List<Transition<RenderContext<RenderableState>>>> e: custom.entrySet()){
				List<Transition<RenderContext<RenderableState>>> partTransitions = e.getValue();
				Transition<RenderContext<RenderableState>> partTransition = null;
				if(partTransitions != null && partTransitions.size() > i) {
					partTransition = partTransitions.get(i);
				} else {
					logger.warn("Transition not defined for part {}", custom);
				}
				t.withPartPositionFunction(e.getKey(), createWeaponPartPositionFunction(partTransition));
			}

			result.add(t);
		}
		return result;
	}

	private List<MultipartTransition<Part, RenderContext<RenderableState>>> getSimpleTransition(
			Consumer<RenderContext<RenderableState>> w,
			Consumer<RenderContext<RenderableState>> lh,
			Consumer<RenderContext<RenderableState>> rh,
			//Consumer<RenderContext<RenderableState>> m,
			LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> custom,
			int duration) {
		MultipartTransition<Part, RenderContext<RenderableState>> mt = new MultipartTransition<Part, RenderContext<RenderableState>>(duration, 0)
				.withPartPositionFunction(Part.MAIN_ITEM, createWeaponPartPositionFunction(w))
				.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(lh))
				.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(rh));
		custom.forEach((part, position) -> {
			mt.withPartPositionFunction(part, createWeaponPartPositionFunction(position));
		});
		return Collections.singletonList(mt);
	}

	private class FirstPersonWeaponTransitionProvider implements MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> {

		@Override
		public List<MultipartTransition<Part, RenderContext<RenderableState>>> getTransitions(RenderableState state) {
			switch(state) {
			case MODIFYING:
				return getSimpleTransition(builder.firstPersonPositioningModifying,
						builder.firstPersonLeftHandPositioningModifying,
						builder.firstPersonRightHandPositioningModifying,
						builder.firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION);
			case MODIFYING_ALT:
                return getSimpleTransition(builder.firstPersonPositioningModifyingAlt,
                        builder.firstPersonLeftHandPositioningModifyingAlt,
                        builder.firstPersonRightHandPositioningModifyingAlt,
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
			case LOAD_ITERATION:
                return getComplexTransition(builder.firstPersonPositioningLoadIteration,
                        builder.firstPersonLeftHandPositioningLoadIteration,
                        builder.firstPersonRightHandPositioningLoadIteration,
                        builder.firstPersonCustomPositioningLoadIteration
                        );
			case INSPECTING:
                return getComplexTransition(builder.firstPersonPositioningInspecting,
                        builder.firstPersonLeftHandPositioningInspecting,
                        builder.firstPersonRightHandPositioningInspecting,
                        builder.firstPersonCustomPositioningInspecting
                        );
            case DRAWING:
                return getComplexTransition(builder.firstPersonPositioningDrawing,
                        builder.firstPersonLeftHandPositioningDrawing,
                        builder.firstPersonRightHandPositioningDrawing,
                        builder.firstPersonCustomPositioningDrawing
                        );
			case LOAD_ITERATION_COMPLETED:
                return getSimpleTransition(builder.firstPersonPositioningLoadIterationCompleted,
                        builder.firstPersonLeftHandPositioningLoadIterationCompleted,
                        builder.firstPersonRightHandPositioningLoadIterationCompleted,
                        builder.firstPersonCustomPositioningLoadIterationCompleted,
                        builder.loadIterationCompletedAnimationDuration);
			case ALL_LOAD_ITERATIONS_COMPLETED:
                return getComplexTransition(builder.firstPersonPositioningAllLoadIterationsCompleted,
                        builder.firstPersonLeftHandPositioningAllLoadIterationsCompleted,
                        builder.firstPersonRightHandPositioningAllLoadIterationsCompleted,
                        builder.firstPersonCustomPositioningLoadIterationsCompleted
                        );
			case RECOILED:
				return getSimpleTransition(builder.firstPersonPositioningRecoiled,
						builder.firstPersonLeftHandPositioningRecoiled,
						builder.firstPersonRightHandPositioningRecoiled,
						builder.firstPersonCustomPositioningRecoiled,
						builder.recoilAnimationDuration);
            case PRONING_RECOILED:
                return getSimpleTransition(builder.firstPersonPositioningProningRecoiled,
                        builder.firstPersonLeftHandPositioningProningRecoiled,
                        builder.firstPersonRightHandPositioningProningRecoiled,
                        builder.firstPersonCustomPositioningProningRecoiled,
                        builder.recoilAnimationDuration);
			case SHOOTING:
				return getSimpleTransition(builder.firstPersonPositioningShooting,
						builder.firstPersonLeftHandPositioningShooting,
						builder.firstPersonRightHandPositioningShooting,
						builder.firstPersonCustomPositioning,
						builder.shootingAnimationDuration);
            case PRONING_SHOOTING:
                return getSimpleTransition(builder.firstPersonPositioningProningShooting,
                        builder.firstPersonLeftHandPositioningProningShooting,
                        builder.firstPersonRightHandPositioningProningShooting,
                        builder.firstPersonCustomPositioning,
                        builder.shootingAnimationDuration);
			case EJECT_SPENT_ROUND:
				return getComplexTransition(builder.firstPersonPositioningEjectSpentRound,
						builder.firstPersonLeftHandPositioningEjectSpentRound,
						builder.firstPersonRightHandPositioningEjectSpentRound,
						builder.firstPersonCustomPositioningEjectSpentRound
						);
			case EJECT_SPENT_ROUND_AIMED:
                return getComplexTransition(builder.firstPersonPositioningEjectSpentRoundAimed,
                        builder.firstPersonLeftHandPositioningEjectSpentRoundAimed,
                        builder.firstPersonRightHandPositioningEjectSpentRoundAimed,
                        builder.firstPersonCustomPositioningEjectSpentRoundAimed
                        );
			case NORMAL:
				return getSimpleTransition(builder.firstPersonPositioning,
						builder.firstPersonLeftHandPositioning,
						builder.firstPersonRightHandPositioning,
						builder.firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION);
            case PRONING:
                return getSimpleTransition(builder.firstPersonPositioningProning,
                        builder.firstPersonLeftHandPositioningProning,
                        builder.firstPersonRightHandPositioningProning,
                        builder.firstPersonCustomPositioningProning,
                        DEFAULT_ANIMATION_DURATION);
			case ZOOMING:
				return getSimpleTransition(builder.firstPersonPositioningZooming,
						builder.firstPersonLeftHandPositioningZooming,
						builder.firstPersonRightHandPositioningZooming,
						builder.firstPersonCustomPositioningZooming,
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
	
	private class ThirdPersonWeaponTransitionProvider implements MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> {

        @Override
        public List<MultipartTransition<Part, RenderContext<RenderableState>>> getTransitions(RenderableState state) {
            switch(state) {
            case UNLOADING:
                return getComplexTransition(builder.thirdPersonPositioningUnloading,
                        builder.thirdPersonLeftHandPositioningUnloading,
                        builder.thirdPersonRightHandPositioningUnloading,
                        builder.thirdPersonCustomPositioningUnloading
                        );
            case RELOADING:
                return getComplexTransition(builder.thirdPersonPositioningReloading,
                        builder.thirdPersonLeftHandPositioningReloading,
                        builder.thirdPersonRightHandPositioningReloading,
                        builder.thirdPersonCustomPositioningReloading
                        );
            case NORMAL: default:
                return getSimpleTransition(builder.thirdPersonPositioning,
                        context -> {},
                        context -> {},
                        new LinkedHashMap<>(),
                        DEFAULT_ANIMATION_DURATION);
//            default:
//                return getSimpleTransition(context -> {},
//                        context -> {
//                            //
//                        },
//                        context -> {
////                            GL11.glTranslatef(0f, 0.5f, 0f);
////                            GL11.glRotatef(30f, 0f, 0f, 1f);
//                        },
//                        new LinkedHashMap<>(),
//                        DEFAULT_ANIMATION_DURATION);
            }
            //return null;
        }
    }

	@Override
	public void renderItem(ItemStack weaponItemStack, RenderContext<RenderableState> renderContext,
			Positioner<Part, RenderContext<RenderableState>> positioner) {
	    
//	    if(player.getDistanceSqToEntity(compatibility.clientPlayer()) > 400) {
//	        return;
//	    }
	    
		List<CompatibleAttachment<? extends AttachmentContainer>> attachments = null;
		if(builder.getModel() instanceof ModelWithAttachments) {
			attachments = ((Weapon) weaponItemStack.getItem()).getActiveAttachments(renderContext.getPlayer(), weaponItemStack);
		}

		if(builder.getTextureName() != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId()
					+ ":textures/models/" + builder.getTextureName()));
		} else {
			String textureName = null;
			CompatibleAttachment<?> compatibleSkin = attachments.stream()
					.filter(ca -> ca.getAttachment() instanceof ItemSkin).findAny().orElse(null);
			if(compatibleSkin != null) {
				PlayerItemInstance<?> itemInstance = getClientModContext().getPlayerItemInstanceRegistry()
						.getItemInstance(renderContext.getPlayer(), weaponItemStack);
				if(itemInstance instanceof PlayerWeaponInstance) {
					int textureIndex = ((PlayerWeaponInstance) itemInstance).getActiveTextureIndex();
					if(textureIndex >= 0) {
						textureName = ((ItemSkin) compatibleSkin.getAttachment()).getTextureVariant(textureIndex)
								+ ".png";
					}
				}
			}

			if(textureName == null) {
				Weapon weapon = ((Weapon) weaponItemStack.getItem());
				textureName = weapon.getTextureName();
			}

			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId()
					+ ":textures/models/" + textureName));
		}


		double sqDistance = 0.0;
		
		if(player != null && player != Minecraft.getMinecraft().player) {
		    Vec3d projectView = net.minecraft.client.renderer.ActiveRenderInfo.projectViewFromEntity(
		            Minecraft.getMinecraft().player, 
		            renderContext.getAgeInTicks());
		    sqDistance = projectView.squareDistanceTo(player.posX, player.posY, player.posZ);
		}
		
		Float renderOptimization = null;
		Projectiles projectilesConfig = clientModContext.configurationManager.getProjectiles();
		if(projectilesConfig != null) {
		      renderOptimization = projectilesConfig.getRenderOptimization();
		}
		if(renderOptimization == null) {
		    renderOptimization = 0.25f;
		}
		double volumeThreshold = sqDistance * renderOptimization;
		
		Interceptors.setRenderVolumeThreshold(volumeThreshold);
		try {
		    builder.getModel().render(this.player,
	                renderContext.getLimbSwing(),
	                renderContext.getFlimbSwingAmount(),
	                renderContext.getAgeInTicks(),
	                renderContext.getNetHeadYaw(),
	                renderContext.getHeadPitch(),
	                renderContext.getScale());

		    if(sqDistance < 900) {
		    	    Interceptors.setRenderVolumeThreshold(volumeThreshold);
		        if(attachments != null) {
		            renderAttachments(positioner, renderContext, attachments);
		        }
		    }
		} finally {
		    Interceptors.setRenderVolumeThreshold(0.0);
		}
		
	}

	public void renderAttachments(Positioner<Part, RenderContext<RenderableState>> positioner, RenderContext<RenderableState> renderContext,List<CompatibleAttachment<? extends AttachmentContainer>> attachments) {
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null && !(compatibleAttachment.getAttachment() instanceof ItemSkin)) {
				renderCompatibleAttachment(compatibleAttachment, positioner, renderContext);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private void renderCompatibleAttachment(CompatibleAttachment<?> compatibleAttachment,
			Positioner<Part, RenderContext<RenderableState>> positioner, RenderContext<RenderableState> renderContext) {

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);

		if(compatibleAttachment.getPositioning() instanceof BiConsumer) {
			((BiConsumer) compatibleAttachment.getPositioning()).accept(renderContext.getPlayer(), renderContext.getWeapon());
		} else if(compatibleAttachment.getPositioning() instanceof Consumer) {
            ((Consumer) compatibleAttachment.getPositioning()).accept(renderContext);
        }

		ItemAttachment<?> itemAttachment = compatibleAttachment.getAttachment();


		if(positioner != null) {
			if(itemAttachment instanceof Part) {
				positioner.position((Part) itemAttachment, renderContext);
			} else if(itemAttachment.getRenderablePart() != null) {
				positioner.position(itemAttachment.getRenderablePart(), renderContext);
			}
		}
		
		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
	    if(renderViewEntity == null) {
	        renderViewEntity = Minecraft.getMinecraft().player;
	    }
//	    double distanceSq = this.player != null ? renderViewEntity.getDistanceSqToEntity(this.player) : 0;

		for(Tuple<ModelBase, String> texturedModel: compatibleAttachment.getAttachment().getTexturedModels()) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId()
					+ ":textures/models/" + texturedModel.getV()));
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
			if(compatibleAttachment.getModelPositioning() != null) {
				compatibleAttachment.getModelPositioning().accept(texturedModel.getU());
			}
			//if(distanceSq < 49) {
		         texturedModel.getU().render(renderContext.getPlayer(),
		                    renderContext.getLimbSwing(),
		                    renderContext.getFlimbSwingAmount(),
		                    renderContext.getAgeInTicks(),
		                    renderContext.getNetHeadYaw(),
		                    renderContext.getHeadPitch(),
		                    renderContext.getScale());
			//}


			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}

		@SuppressWarnings("unchecked")
        CustomRenderer<RenderableState> postRenderer = (CustomRenderer<RenderableState>) compatibleAttachment.getAttachment().getPostRenderer();
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

    public long getTotalLoadIterationDuration() {
        return builder.totalLoadIterationDuration;
    }

    public long getPrepareFirstLoadIterationAnimationDuration() {
        return builder.prepareFirstLoadIterationAnimationDuration;
    }
    
    public long getAllLoadIterationAnimationsCompletedDuration() {
        return builder.allLoadIterationAnimationsCompletedDuration;
    }

    public MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> getStateManager(EntityPlayer player) {
        return firstPersonStateManagers.get(player);
    }
}
