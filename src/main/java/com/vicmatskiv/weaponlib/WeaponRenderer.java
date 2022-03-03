package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLSync;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Project;


import com.vicmatskiv.weaponlib.animation.AnimationModeProcessor;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.Interpolation;
import com.vicmatskiv.weaponlib.animation.DebugPositioner.TransitionConfiguration;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData;
import com.vicmatskiv.weaponlib.animation.jim.AnimationSet;
import com.vicmatskiv.weaponlib.animation.jim.BBLoader;
import com.vicmatskiv.weaponlib.animation.jim.SingleAnimation;
import com.vicmatskiv.weaponlib.animation.jim.AnimationData.BlockbenchTransition;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;
import com.vicmatskiv.weaponlib.animation.OpenGLSelectionHelper;
import com.vicmatskiv.weaponlib.animation.SpecialAttachments;
import com.vicmatskiv.weaponlib.animation.Transform;
import com.vicmatskiv.weaponlib.animation.Transition;
import com.vicmatskiv.weaponlib.animation.gui.AnimationGUI;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer;
import com.vicmatskiv.weaponlib.compatibility.Interceptors;
import com.vicmatskiv.weaponlib.compatibility.ModernWeaponRenderer;
import com.vicmatskiv.weaponlib.config.Projectiles;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.render.Dloom;
import com.vicmatskiv.weaponlib.render.Shaders;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

import akka.japi.Pair;
import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class WeaponRenderer extends CompatibleWeaponRenderer {

	private static final Logger logger = LogManager.getLogger(WeaponRenderer.class);

	private static final float DEFAULT_RANDOMIZING_RATE = 0.33f;
	private static final float DEFAULT_RANDOMIZING_FIRING_RATE = 20;
	private static final float DEFAULT_RANDOMIZING_ZOOM_RATE = 0.25f;

	private static final float DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE = 0.06f;
	private static final float DEFAULT_ZOOM_RANDOMIZING_AMPLITUDE = 0.005f;
	private static final float DEFAULT_FIRING_RANDOMIZING_AMPLITUDE = 0.03f;

	public static final int DEFAULT_ANIMATION_DURATION = 250;
	private static final int DEFAULT_RECOIL_ANIMATION_DURATION = 100;
	private static final int DEFAULT_SHOOTING_ANIMATION_DURATION = 100;
	private static final int DEFAULT_ITERATION_COMPLETED_ANIMATION_DURATION = 100;
	private static final int DEFAULT_PREPARE_FIRST_LOAD_ITERATION_ANIMATION_DURATION = 100;
	private static final int DEFAULT_ALL_LOAD_ITERATION_ANIMATIONS_COMPLETED_DURATION = 100;

	public static class Builder {
	    
        private Random random = new Random();
        
        public boolean threePointOh;
        
        private Vec3d beizer = new Vec3d(0, 3.5, -1);

		private ModelBase model;
		private String textureName;
		private float weaponProximity;
		private float yOffsetZoom;
		private float xOffsetZoom = 0.69F;

		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private Consumer<RenderContext<RenderableState>> thirdPersonPositioning;

		public Consumer<RenderContext<RenderableState>> firstPersonPositioning;
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
	    

	    private TransitionContainer compoundReloadContainer = new TransitionContainer();
	    private TransitionContainer compoundReloadEmptyContainer = new TransitionContainer();
	    private TransitionContainer loadEmptyContainer = new TransitionContainer();
	    private TransitionContainer unloadEmptyContainer = new TransitionContainer();
	    private TransitionContainer tacticalReloadContainer = new TransitionContainer();
	    
	    
	    // ADS animations
	    private TransitionContainer compoundReloadADSContainer = new TransitionContainer();
	    
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
		
		
		private long totalCompoundReloadingDuration;
		private long totalCompoundReloadEmptyDuration;
		private long totalTacticalReloadDuration;
		private long totalLoadEmptyDuration;
		private long totalLoadDuration;
		private long totalUnloadDuration;
		

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
		
		
		// VMW 3.0
		public Transform firstPersonTransform;
		public Transform firstPersonLeftHandTransform;
		public Transform firstPersonRightHandTransform;
		
		public Transform firstPersonZoomingTransform;
		public Transform firstPersonLeftHandZoomingTransform;
		public Transform firstPersonRightHandZoomingTransform;
		
		

		private boolean compoundReloadUsesTactical;
		private boolean compoundReloadEmptyUsesTactical;
		private boolean hasTacticalReload;
		public boolean isHasTacticalReload() {
			return hasTacticalReload;
		}

		public void setHasTacticalReload(boolean hasTacticalReload) {
			this.hasTacticalReload = hasTacticalReload;
		}

		public boolean isHasUnloadEmpty() {
			return hasUnloadEmpty;
		}

		public void setHasUnloadEmpty(boolean hasUnloadEmpty) {
			this.hasUnloadEmpty = hasUnloadEmpty;
		}

		public boolean isHasLoadEmpty() {
			return hasLoadEmpty;
		}

		public void setHasLoadEmpty(boolean hasLoadEmpty) {
			this.hasLoadEmpty = hasLoadEmpty;
		}

		public boolean isHasCompoundReloadEmpty() {
			return hasCompoundReloadEmpty;
		}

		public void setHasCompoundReloadEmpty(boolean hasCompoundReloadEmpty) {
			this.hasCompoundReloadEmpty = hasCompoundReloadEmpty;
		}

		public boolean isHasCompoundReload() {
			return hasCompoundReload;
		}

		public void setHasCompoundReload(boolean hasCompoundReload) {
			this.hasCompoundReload = hasCompoundReload;
		}

		public boolean isHasLoad() {
			return hasLoad;
		}

		public void setHasLoad(boolean hasLoad) {
			this.hasLoad = hasLoad;
		}

		public boolean isHasUnload() {
			return hasUnload;
		}

		public void setHasUnload(boolean hasUnload) {
			this.hasUnload = hasUnload;
		}

		public boolean isHasDraw() {
			return hasDraw;
		}

		public void setHasDraw(boolean hasDraw) {
			this.hasDraw = hasDraw;
		}

		public boolean isHasInspect() {
			return hasInspect;
		}

		public void setHasInspect(boolean hasInspect) {
			this.hasInspect = hasInspect;
		}


		private boolean hasUnloadEmpty;
		private boolean hasLoadEmpty;
		private boolean hasCompoundReloadEmpty;
		private boolean hasCompoundReload;
		private boolean hasLoad;
		private boolean hasUnload;
		private boolean hasDraw;
		private boolean hasInspect; 
		
		

		

		
		public long getCompoundReloadDuration() {
			return compoundReloadContainer.getDuration();
		}
		
		public long getCompoundReloadEmptyDuration() {
			return compoundReloadEmptyContainer.getDuration();
		}
		
		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}

		public Builder withModel(ModelBase model) {
			this.model = model;
			return this;
		}

		@Deprecated
		public Builder withShootingAnimationDuration(int shootingAnimationDuration) {
			this.shootingAnimationDuration = shootingAnimationDuration;
			return this;
		}
		@Deprecated
		public Builder withRecoilAnimationDuration(int recoilAnimationDuration) {
			this.recoilAnimationDuration = recoilAnimationDuration;
			return this;
		}
		@Deprecated
		public Builder withPrepareFirstLoadIterationAnimationDuration(int prepareFirstLoadIterationAnimationDuration) {
            this.prepareFirstLoadIterationAnimationDuration = prepareFirstLoadIterationAnimationDuration;
            return this;
        }
		@Deprecated
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
		
		public Builder withADSBeizer(Vec3d beizer) {
			this.beizer = beizer;
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

		@Deprecated
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
		
		// VMW 3.0
		
		
		
		public Builder withFirstPersonPositioningProning(Consumer<RenderContext<RenderableState>> firstPersonPositioningProning) {
		    this.firstPersonPositioningProning = firstPersonPositioningProning;
		    return this;
		}

		@Deprecated
		public Builder withFirstPersonPositioningRunning(Consumer<RenderContext<RenderableState>> firstPersonPositioningRunning) {
			//this.firstPersonPositioningRunning = firstPersonPositioningRunning;
			return this;
		}

		public Builder withFirstPersonPositioningZooming(Consumer<RenderContext<RenderableState>> firstPersonPositioningZooming) {
			this.firstPersonPositioningZooming = firstPersonPositioningZooming;
			return this;
		}

		@Deprecated
		public Builder withFirstPersonPositioningRecoiled(Consumer<RenderContext<RenderableState>> firstPersonPositioningRecoiled) {
			//this.hasRecoilPositioningDefined = true;
			//this.firstPersonPositioningRecoiled = firstPersonPositioningRecoiled;
			return this;
		}
		
		@Deprecated
		public Builder withFirstPersonPositioningProningRecoiled(Consumer<RenderContext<RenderableState>> firstPersonPositioningProningRecoiled) {
		   // this.firstPersonPositioningProningRecoiled = firstPersonPositioningProningRecoiled;
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

		@Deprecated
		public Builder withFirstPersonPositioningZoomingRecoiled(Consumer<RenderContext<RenderableState>> firstPersonPositioningZoomingRecoiled) {
			//this.firstPersonPositioningZoomingRecoiled = firstPersonPositioningZoomingRecoiled;
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
		
		public final Builder withFirstPersonPositioningReloadingNew(List<Transition<RenderContext<RenderableState>>> transitions) {
			
			this.firstPersonPositioningReloading = transitions;
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
		
		public final Builder withFirstPersonPositioningUnloading(List<Transition<RenderContext<RenderableState>>> transitions) {
			this.firstPersonPositioningUnloading = transitions;
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
		
		 public final Builder withFirstPersonPositioningInspecting(List<Transition<RenderContext<RenderableState>>> transitions) {
	            this.firstPersonPositioningInspecting = transitions;
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

		@Deprecated
		public Builder withFirstPersonHandPositioningRunning(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			//this.firstPersonLeftHandPositioningRunning = leftHand;
			//this.firstPersonRightHandPositioningRunning = rightHand;
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

		@Deprecated
		public Builder withFirstPersonHandPositioningRecoiled(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			//this.firstPersonLeftHandPositioningRecoiled = leftHand;
			//this.firstPersonRightHandPositioningRecoiled = rightHand;
			return this;
		}
		
		@Deprecated
		public Builder withFirstPersonHandPositioningProningRecoiled(
		        Consumer<RenderContext<RenderableState>> leftHand,
		        Consumer<RenderContext<RenderableState>> rightHand)
		{
		  //  this.firstPersonLeftHandPositioningProningRecoiled = leftHand;
		  //  this.firstPersonRightHandPositioningProningRecoiled = rightHand;
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
		
		public final Builder withFirstPersonLeftHandPositioningReloading(List<Transition<RenderContext<RenderableState>>> transitions) {
			this.firstPersonLeftHandPositioningReloading = transitions;
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
		
		public final Builder withFirstPersonLeftHandPositioningInspecting(List<Transition<RenderContext<RenderableState>>> transitions) {
            this.firstPersonLeftHandPositioningInspecting = transitions;
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
		
		public final Builder withFirstPersonLeftHandPositioningUnloading(List<Transition<RenderContext<RenderableState>>> transitions) {
			this.firstPersonLeftHandPositioningUnloading = transitions;
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
		
		public final Builder withFirstPersonRightHandPositioningUnloading(List<Transition<RenderContext<RenderableState>>> transitions) {
			this.firstPersonRightHandPositioningUnloading = transitions;
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
		
		   public final Builder withFirstPersonRightHandPositioningInspecting(List<Transition<RenderContext<RenderableState>>> transitions) {
	            this.firstPersonRightHandPositioningInspecting = transitions;
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

		@Deprecated
		public Builder withFirstPersonPositioningCustomRecoiled(Part part, Consumer<RenderContext<RenderableState>> positioning) {
		/*
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningRecoiled.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}*/
			return this;
		}
		
		@Deprecated
		public Builder withFirstPersonPositioningCustomProningRecoiled(Part part, Consumer<RenderContext<RenderableState>> positioning) {
		    /*
			if(part instanceof DefaultPart) {
		        throw new IllegalArgumentException("Part " + part + " is not custom");
		    }
		    if(this.firstPersonCustomPositioningProningRecoiled.put(part, positioning) != null) {
		        throw new IllegalArgumentException("Part " + part + " already added");
		    }*/
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
		
		
		@Deprecated
		public Builder withFirstPersonPositioningCustomZoomingRecoiled(Part part, Consumer<RenderContext<RenderableState>> positioning) {
			/*
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			if(this.firstPersonCustomPositioningZoomingRecoiled.put(part, positioning) != null) {
				throw new IllegalArgumentException("Part " + part + " already added");
			}*/
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
		
		public final Builder withFirstPersonCustomPositioningReloading(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}

			this.firstPersonCustomPositioningReloading.put(part, transitions);
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
		
		public final Builder withFirstPersonCustomPositioningUnloading(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			this.firstPersonCustomPositioningUnloading.put(part, transitions);
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
		
		/*
		 * VMW 3.0
		 */
		
		public boolean isUsingNewSystem() {
			return this.threePointOh;
		}
		
		public void setUsingThreePointOh() {
			this.threePointOh = true;
		}
		
		public Builder withFirstPersonPositioning(Transform firstPersonTransform) {
			this.firstPersonTransform = firstPersonTransform;
			this.firstPersonPositioning = firstPersonTransform.getAsPosition();
			return this;
		}
		
		public Builder withFPSZooming(Transform zooming) {
			this.firstPersonZoomingTransform = zooming;
			this.firstPersonPositioningZooming = zooming.getAsPosition();
			return this;
		}
		
		public Builder withFirstPersonHandPositioning(Transform leftHand, Transform rightHand)
		{
			
			this.firstPersonLeftHandTransform = leftHand;
			this.firstPersonRightHandTransform = rightHand;
			
			this.firstPersonLeftHandPositioning = leftHand.getAsPosition();
			this.firstPersonRightHandPositioning = rightHand.getAsPosition();
			return this;
		}
		
		public void checkDefaults() {
			if(firstPersonTransform == null) {
				firstPersonTransform = Transform.NULL.copy();
			} 
			if(firstPersonLeftHandTransform == null) {
				firstPersonLeftHandTransform = Transform.NULL.copy();
			}
			if(firstPersonRightHandTransform == null) {
				firstPersonRightHandTransform = Transform.NULL.copy();
			}
		}
		
		private void setupBBAnim(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName, List<Transition<RenderContext<RenderableState>>> mainT, List<Transition<RenderContext<RenderableState>>> leftT, List<Transition<RenderContext<RenderableState>>> rightT) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			mainT = main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR);
			mainT = left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR);
			mainT = right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR);

		}
		
		private void setupBBAnim(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName, TransitionContainer tc) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			tc.setFirstPerson(main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR));
			tc.setLeftHand(main.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			tc.setRightHand(right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));

		}
		
		
		private String animationFileName;
		
		public Builder setupModernMagazineAnimations(String animationFile, Part...parts) {
			// .withFirstPersonCustomPositioningReloading(Magazines.M38Mag,
			
			this.setAnimationFileName(animationFile);
			
			for(Part p : parts) {
				//if(!(p instanceof ItemMagazine)) continue;
				
				Vec3d r = ((ItemMagazine) p).getRotationPoint();
				//System.out.println("ROTMAMDFKFKJF FOR MAG: " + r);
				
				if(hasUnloadEmpty) {
					withUnloadEmptyCustom(p, BBLoader.getAnimation(animationFile, BBLoader.KEY_UNLOAD_EMPTY, BBLoader.KEY_MAGAZINE)
							.getTransitionList(Transform.NULL.copy().withRotationPoint(r.x, r.y, r.z), BBLoader.HANDDIVISOR));
				}
				
				if(hasLoadEmpty) {
					withLoadEmptyCustom(p, BBLoader.getAnimation(animationFile, BBLoader.KEY_LOAD_EMPTY, BBLoader.KEY_MAGAZINE)
							.getTransitionList(Transform.NULL.copy().withRotationPoint(r.x, r.y, r.z), BBLoader.HANDDIVISOR));
					
				}
				
				if(hasTacticalReload) {
					System.out.println("YYEEYEY DOIN TACTICAL");
					withTacticalReloadCustom(p, BBLoader.getAnimation(animationFile, BBLoader.KEY_TACTICAL_RELOAD, BBLoader.KEY_MAGAZINE)
							.getTransitionList(Transform.NULL.copy().withRotationPoint(r.x, r.y, r.z), BBLoader.HANDDIVISOR));
					
				}
				
				if(hasLoad) {
					withFirstPersonCustomPositioningReloading(p, BBLoader.getAnimation(animationFile, BBLoader.KEY_LOAD, BBLoader.KEY_MAGAZINE)
							.getTransitionList(Transform.NULL.copy().withRotationPoint(r.x, r.y, r.z), BBLoader.HANDDIVISOR));
					
				}
				if(hasUnload) {
					withFirstPersonCustomPositioningUnloading(p, BBLoader.getAnimation(animationFile, BBLoader.KEY_UNLOAD, BBLoader.KEY_MAGAZINE)
							.getTransitionList(Transform.NULL.copy().withRotationPoint(r.x, r.y, r.z), BBLoader.HANDDIVISOR));
					
				}
				
				if(hasCompoundReload) {
					withFirstPersonCustomPositioningCompoundReloading(p, BBLoader.getAnimation(animationFile, BBLoader.KEY_COMPOUND_RELOAD, BBLoader.KEY_MAGAZINE)
							.getTransitionList(Transform.NULL.copy().withRotationPoint(r.x, r.y, r.z), BBLoader.HANDDIVISOR));
					
				}
				
				if(hasCompoundReloadEmpty) {
					withFPSCustomCompoundReloadingEmpty(p, BBLoader.getAnimation(animationFile, BBLoader.KEY_COMPOUND_RELOAD_EMPTY, BBLoader.KEY_MAGAZINE)
							.getTransitionList(Transform.NULL.copy().withRotationPoint(r.x, r.y, r.z), BBLoader.HANDDIVISOR));
				
				}
				
				
				
				
		}
			
			
			// Now time to do magic magazine things
			if(hasCompoundReloadEmpty && compoundReloadEmptyUsesTactical) {
				withFPSCustomCompoundReloadingEmpty(SpecialAttachments.MagicMag.getRenderablePart(),
						BBLoader.getAnimation(animationFile, BBLoader.KEY_COMPOUND_RELOAD_EMPTY, BBLoader.KEY_MAGIC_MAGAZINE)
						.getTransitionList(Transform.NULL
								.copy(), BBLoader.HANDDIVISOR));
			}
			if(hasCompoundReload && compoundReloadUsesTactical) {
				withFirstPersonCustomPositioningCompoundReloading(SpecialAttachments.MagicMag.getRenderablePart(), 
						BBLoader.getAnimation(animationFile, BBLoader.KEY_COMPOUND_RELOAD, BBLoader.KEY_MAGIC_MAGAZINE)
						.getTransitionList(Transform.NULL
								.copy(), BBLoader.HANDDIVISOR));
			}
			
			
			return this;
		
		}
		
		
		
		public Builder setupModernAnimations(String animationFile, Part action) {
			final String mainBoneName = "main";
			final String leftBoneName = "lefthand";
			final String rightBoneName = "righthand";
			
			// Makes sure the file is loaded
			
			AnimationSet set = BBLoader.getAnimationSet(animationFile);
			
			
			
			/* ==============
			 * 
			 * Do category checks
			 * 
			 * ==============
			 */
			
			if(set.containsKey(BBLoader.KEY_LOAD_EMPTY)) {
				hasLoadEmpty = true;
			}
			if(set.containsKey(BBLoader.KEY_UNLOAD_EMPTY)) {
				hasUnloadEmpty = true;
			}
			if(set.containsKey(BBLoader.KEY_TACTICAL_RELOAD)) {
				hasTacticalReload = true;
			}
			if(set.containsKey(BBLoader.KEY_COMPOUND_RELOAD)) {
				hasCompoundReload = true;
			}
			if(set.containsKey(BBLoader.KEY_COMPOUND_RELOAD_EMPTY)) {
				hasCompoundReloadEmpty = true;
			}
			if(set.containsKey(BBLoader.KEY_INSPECT)) {
				hasInspect = true;
			}
			if(set.containsKey(BBLoader.KEY_DRAW)) {
				hasDraw = true;
			}
			if(set.containsKey(BBLoader.KEY_LOAD)) {
				hasLoad = true;
			}
			if(set.containsKey(BBLoader.KEY_UNLOAD)) {
				hasUnload = true;
			}
			
			// Check if compound & compound empty should use tactical functionality
			SingleAnimation compound = set.getSingleAnimation(BBLoader.KEY_COMPOUND_RELOAD);
			if(compound != null) {
				if(compound.hasBone(BBLoader.KEY_MAGIC_MAGAZINE)) {
					if(compound.getBone(BBLoader.KEY_MAGIC_MAGAZINE).bbTransition.size() > 1) {
						compoundReloadUsesTactical = true;
					}
				}
			}
			
			SingleAnimation compoundEmpty = set.getSingleAnimation(BBLoader.KEY_COMPOUND_RELOAD_EMPTY);
			if(compoundEmpty != null) {
				if(compoundEmpty.hasBone(BBLoader.KEY_MAGIC_MAGAZINE)) {
					if(compoundEmpty.getBone(BBLoader.KEY_MAGIC_MAGAZINE).bbTransition.size() > 1) {
						compoundReloadEmptyUsesTactical = true;
					}
				}
			}
			
			
			if(hasLoadEmpty) {
				setupLoadEmpty(animationFile, BBLoader.KEY_LOAD_EMPTY, mainBoneName, leftBoneName, rightBoneName);
	
				
			}
			if(hasUnloadEmpty) setupUnloadEmpty(animationFile, BBLoader.KEY_UNLOAD_EMPTY, mainBoneName, leftBoneName, rightBoneName);
			if(hasTacticalReload) setupTacticalReload(animationFile, BBLoader.KEY_TACTICAL_RELOAD, mainBoneName, leftBoneName, rightBoneName);
			
			if(hasInspect) setupInspectAnimations(animationFile, BBLoader.KEY_INSPECT, mainBoneName, leftBoneName, rightBoneName);
			if(hasCompoundReload) {
				if(firstPersonZoomingTransform != null) {
					setupModernContainerADS(animationFile, BBLoader.KEY_COMPOUND_RELOAD, this.compoundReloadADSContainer);
				}
				setupCompoundReload(animationFile, BBLoader.KEY_COMPOUND_RELOAD, mainBoneName, leftBoneName, rightBoneName);
			}
			if(hasLoad) setupReload(animationFile, BBLoader.KEY_LOAD, mainBoneName,  leftBoneName, rightBoneName);
			if(hasUnload) setupUnload(animationFile, BBLoader.KEY_UNLOAD, mainBoneName, leftBoneName, rightBoneName);
			if(hasDraw) setupDraw(animationFile, BBLoader.KEY_DRAW, mainBoneName, leftBoneName, rightBoneName);
			if(hasCompoundReloadEmpty) setupCompoundReloadEmpty(animationFile, BBLoader.KEY_COMPOUND_RELOAD_EMPTY, mainBoneName, leftBoneName, rightBoneName);
			
			setupCustomKeyedPart(action, animationFile, BBLoader.KEY_ACTION);
			
			return this;
		}
		
		public Builder setupCustomKeyedPart(Part action, String animationFile, String partKey) {
			AnimationSet set = BBLoader.getAnimationSet(animationFile);
			
			if(hasLoadEmpty && set.getSingleAnimation(BBLoader.KEY_LOAD_EMPTY).hasBone(partKey)) {
				withLoadEmptyCustom(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_LOAD_EMPTY, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasUnloadEmpty && set.getSingleAnimation(BBLoader.KEY_UNLOAD_EMPTY).hasBone(partKey)) {
				withUnloadEmptyCustom(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_UNLOAD_EMPTY, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasCompoundReload && set.getSingleAnimation(BBLoader.KEY_COMPOUND_RELOAD).hasBone(partKey)) {
				withFirstPersonCustomPositioningCompoundReloading(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_COMPOUND_RELOAD, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasCompoundReloadEmpty && set.getSingleAnimation(BBLoader.KEY_COMPOUND_RELOAD_EMPTY).hasBone(partKey)) {
				withFPSCustomCompoundReloadingEmpty(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_COMPOUND_RELOAD_EMPTY, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasTacticalReload && set.getSingleAnimation(BBLoader.KEY_TACTICAL_RELOAD).hasBone(partKey)) {
				withTacticalReloadCustom(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_TACTICAL_RELOAD, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasDraw && set.getSingleAnimation(BBLoader.KEY_DRAW).hasBone(partKey)) {
				withFirstPersonCustomPositioningDrawing(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_DRAW, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasInspect && set.getSingleAnimation(BBLoader.KEY_INSPECT).hasBone(partKey)) {
				withFirstPersonCustomPositioningInspecting(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_INSPECT, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasLoad && set.getSingleAnimation(BBLoader.KEY_LOAD).hasBone(partKey)) {
				withFirstPersonCustomPositioningReloading(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_LOAD, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			if(hasUnload && set.getSingleAnimation(BBLoader.KEY_UNLOAD).hasBone(partKey)) {
				withFirstPersonCustomPositioningUnloading(action, BBLoader.getAnimation(animationFile, BBLoader.KEY_UNLOAD, partKey)
						.getTransitionList(Transform.NULL.copy(), BBLoader.HANDDIVISOR));
			}
			
			return this;
			
			
			
		}
		
		public Builder setCompoundReloadTacticalFunctionality(boolean normal, boolean empty) {
			this.compoundReloadUsesTactical = normal;
			this.compoundReloadEmptyUsesTactical = empty;
			return this;
		}
		
		public Builder setupInspectAnimations(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.firstPersonPositioningInspecting = main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR);
			this.firstPersonLeftHandPositioningInspecting = left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR);
			this.firstPersonRightHandPositioningInspecting = right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR);
			return this;
		}
		
		public Builder setupCompoundReload(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			if(firstPersonZoomingTransform != null) {
				this.compoundReloadContainer.setFirstPerson(main.getTransitionListDual(firstPersonTransform, firstPersonZoomingTransform, BBLoader.GENDIVISOR));
				
			} else {
				this.compoundReloadContainer.setFirstPerson(main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR));
				
			}
			this.compoundReloadContainer.setLeftHand(left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			this.compoundReloadContainer.setRightHand(right == null ? null : right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));

			this.compoundReloadContainer.setDuration((long) Math.round((main.getAppointedDuration()*AnimationData.PACE)));
			return this;
			
		}
		
		public Builder setupLoadEmpty(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.loadEmptyContainer.setFirstPerson(main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR));
			this.loadEmptyContainer.setLeftHand(left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			this.loadEmptyContainer.setRightHand(right == null ? null : right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));

			this.loadEmptyContainer.setDuration((long) Math.round((main.getAppointedDuration()*AnimationData.PACE)));
			return this;
			
		}
		
		public Builder setupUnloadEmpty(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.unloadEmptyContainer.setFirstPerson(main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR));
			this.unloadEmptyContainer.setLeftHand(left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			this.unloadEmptyContainer.setRightHand(right == null ? null : right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));

			this.unloadEmptyContainer.setDuration((long) Math.round((main.getAppointedDuration()*AnimationData.PACE)));
			return this;
			
		}
		
		public Builder setupModernContainer(String animationFile, String anim, TransitionContainer container) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, "main");
			AnimationData left = BBLoader.getAnimation(animationFile, anim, "lefthand");
			AnimationData right = BBLoader.getAnimation(animationFile, anim, "righthand");
						
			checkDefaults();
			
			container.setFirstPerson(main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR));
			container.setLeftHand(left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			container.setRightHand(right == null ? null : right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));
			container.setDuration((long) Math.round((main.getAppointedDuration()*AnimationData.PACE)));
			return this;
		}
		
		public Builder setupModernContainerADS(String animationFile, String anim, TransitionContainer container) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, "main");
			AnimationData left = BBLoader.getAnimation(animationFile, anim, "lefthand");
			AnimationData right = BBLoader.getAnimation(animationFile, anim, "righthand");
						
			checkDefaults();
			
			container.setFirstPerson(main.getTransitionList(firstPersonZoomingTransform, BBLoader.GENDIVISOR));
			container.setLeftHand(left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			container.setRightHand(right == null ? null : right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));
			container.setDuration((long) Math.round((main.getAppointedDuration()*AnimationData.PACE)));
			return this;
		}
		
		public Builder setupTacticalReload(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.tacticalReloadContainer.setFirstPerson(main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR));
			this.tacticalReloadContainer.setLeftHand(left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			this.tacticalReloadContainer.setRightHand(right == null ? null : right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));

			this.tacticalReloadContainer.setDuration((long) Math.round((main.getAppointedDuration()*AnimationData.PACE)));
			return this;
			
		}
		
		
		public Builder setupCompoundReloadEmpty(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.compoundReloadEmptyContainer.setFirstPerson(main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR));
			this.compoundReloadEmptyContainer.setLeftHand(left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR));
			this.compoundReloadEmptyContainer.setRightHand(right == null ? null : right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR));
			
			this.compoundReloadEmptyContainer.setDuration((long) Math.round((main.getAppointedDuration()*AnimationData.PACE)));
			//setupBBAnim(animationFile, anim, mainBoneName, leftHandBoneName, rightHandBoneName, this.compoundReloadContainer);
			return this;
			
		}
		
		public Builder setupReload(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.firstPersonPositioningReloading = main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR);
			this.firstPersonLeftHandPositioningReloading = left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR);
			this.firstPersonRightHandPositioningReloading = right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR);
	
			return this;
			
		}
		
		public Builder setupUnload(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.firstPersonPositioningUnloading = main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR);
			this.firstPersonLeftHandPositioningUnloading = left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR);
			this.firstPersonRightHandPositioningUnloading = right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR);
	
			return this;
			
		}
		
		
		public Builder setupDraw(String animationFile, String anim, String mainBoneName, String leftHandBoneName, String rightHandBoneName) {
			AnimationData main = BBLoader.getAnimation(animationFile, anim, mainBoneName);
			AnimationData left = BBLoader.getAnimation(animationFile, anim, leftHandBoneName);
			AnimationData right = BBLoader.getAnimation(animationFile, anim, rightHandBoneName);
						
			checkDefaults();
			
			this.firstPersonPositioningDrawing = main.getTransitionList(firstPersonTransform, BBLoader.GENDIVISOR);
			this.firstPersonLeftHandPositioningDrawing = left.getTransitionList(firstPersonLeftHandTransform, BBLoader.HANDDIVISOR);
			this.firstPersonRightHandPositioningDrawing = right.getTransitionList(firstPersonRightHandTransform, BBLoader.HANDDIVISOR);
	
			return this;
			
		}
		
		public final Builder withLoadEmptyCustom(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            loadEmptyContainer.getCustom().put(part, transitions);
            return this;
        }
		
		public final Builder withUnloadEmptyCustom(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            unloadEmptyContainer.getCustom().put(part, transitions);
            return this;
        }
		
		
		public final Builder withTacticalReloadCustom(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            tacticalReloadContainer.getCustom().put(part, transitions);
            return this;
        }
		
		public final Builder withFirstPersonCustomPositioningCompoundReloading(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            compoundReloadContainer.getCustom().put(part, transitions);
            return this;
        }
		
		public final Builder withFPSCustomCompoundReloadingEmpty(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            compoundReloadEmptyContainer.getCustom().put(part, transitions);
            return this;
        }
		
		public final Builder withFPSCustomCompoundReloadingEmpty(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}
			
			
			
			compoundReloadEmptyContainer.getCustom().put(part, Arrays.asList(transitions));
			return this;
		}
        public final Builder withFirstPersonCustomPositioningInspecting(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            this.firstPersonCustomPositioningInspecting.put(part, transitions);
            return this;
        }

        public final Builder withFirstPersonCustomPositioningDrawing(Part part, List<Transition<RenderContext<RenderableState>>> transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            this.firstPersonCustomPositioningDrawing.put(part, transitions);
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

			/*
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
			*/
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
				
				
				firstPersonPositioningRunning = (rc) -> {
					new Transform()
					.withPosition(-1.875000f, 4.725000f, -4.600000f).withRotation(21.175000f, -63.189999f, 0.000000f).withRotationPoint(-0.100000f, 1.000000f, 0.000000f).withScale(3.000000f, 3.000000f, 3.000000f)
					.doGLDirect();
				};
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
            
            /*
             * Compound
             */
            
            this.compoundReloadContainer.build(this);
            this.compoundReloadEmptyContainer.build(this);
            this.loadEmptyContainer.build(this);
            this.unloadEmptyContainer.build(this);
            this.tacticalReloadContainer.build(this);

            

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
		
		public void buildNormalTransition(List<Transition<RenderContext<RenderableState>>> transitionList) {
			  if(transitionList == null) {
				  transitionList = firstPersonPositioningReloading.stream().map(
	                        t -> new Transition<RenderContext<RenderableState>>(c -> {}, 0)).collect(Collectors.toList());
	            }
		}
		
		
		@SuppressWarnings("unchecked")
		public void buildCustomTransitionList(LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> lhm) {
			lhm.forEach((p, t) -> {
                if(((List<Transition<RenderContext<RenderableState>>>) t).size() != lhm.size()) {
                    throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + lhm.size()
                    + ", actual: " + ((List<Transition<RenderContext<RenderableState>>>) t).size());
                }
            });
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

		public String getAnimationFileName() {
			return animationFileName;
		}

		public void setAnimationFileName(String animationFileName) {
			this.animationFileName = animationFileName;
		}
	}

	private Builder builder;

	private Map<EntityLivingBase, MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>>> firstPersonStateManagers;
    private Map<EntityLivingBase, MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>>> thirdPersonStateManagers;

	private MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> firstPersonTransitionProvider;
	private MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> thirdPersonTransitionProvider;

	// Deferred render list
	private ArrayList<Pair<FloatBuffer,  CustomRenderer<RenderableState>>> deferredPost = new ArrayList<>();
	
	
	// Magic Magazine Stuff
	private boolean magicMagPermit;
	private long magicAnimationTimer;
	private WeaponState magicState = WeaponState.READY;
	
	
	

	protected ClientModContext clientModContext;

	private WeaponRenderer(Builder builder) {
		super(builder);
		this.setBuilder(builder);
		this.firstPersonStateManagers = new HashMap<>();
		this.thirdPersonStateManagers = new HashMap<>();
		this.firstPersonTransitionProvider = new FirstPersonWeaponTransitionProvider();
		this.thirdPersonTransitionProvider = new ThirdPersonWeaponTransitionProvider();
	}
	
	
	public Builder getWeaponRendererBuilder() {
		return this.getBuilder();
	}

	protected long getTotalReloadingDuration() {
		return getBuilder().totalReloadingDuration;
	}

	protected long getTotalUnloadingDuration() {
		return getBuilder().totalUnloadingDuration;
	}
	
	protected long getTotalDrawingDuration() {
        return getBuilder().totalDrawingDuration;
    }

	protected ClientModContext getClientModContext() {
		return clientModContext;
	}

	protected void setClientModContext(ClientModContext clientModContext) {
		this.clientModContext = clientModContext;
	}
	
	public boolean isCompoundReloadTactical() {
		return getBuilder().compoundReloadUsesTactical;
	}
	
	public boolean isCompoundReloadEmptyTactical() {
		return getBuilder().compoundReloadEmptyUsesTactical;
	}
	
	public boolean compoundReload = false;
	public boolean compoundReloadEmpty = false;
	
	private boolean shouldDoEmptyVariant = false;
	
	public boolean shouldDoEmptyVariant() {
		return shouldDoEmptyVariant;
	}
	
	public void setShouldDoEmptyVariant(boolean state) {
		this.shouldDoEmptyVariant = state;
	}

	@Override
	protected StateDescriptor getFirstPersonStateDescriptor(EntityLivingBase player, ItemStack itemStack) {
		
		
		float amplitude = getBuilder().normalRandomizingAmplitude;
		float rate = getBuilder().normalRandomizingRate;
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
						rate = getBuilder().firingRandomizingRate;
						amplitude = getBuilder().zoomRandomizingAmplitude;
					} else {
						currentState = RenderableState.NORMAL;
						rate = getBuilder().firingRandomizingRate;
						amplitude = getBuilder().firingRandomizingAmplitude;
					}
				} else if(playerWeaponInstance.isAimed()) {
					currentState = RenderableState.ZOOMING_RECOILED;
					amplitude = getBuilder().zoomRandomizingAmplitude;
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
							rate = getBuilder().firingRandomizingRate;
						}
						amplitude = getBuilder().zoomRandomizingAmplitude;
					} else {
						currentState = RenderableState.NORMAL;
						if(!isLongPaused) {
							rate = getBuilder().firingRandomizingRate;
							amplitude = getBuilder().firingRandomizingAmplitude;
						}
					}
				} else if(playerWeaponInstance.isAimed()) {
					currentState = RenderableState.ZOOMING_SHOOTING;
					//rate = builder.firingRandomizingRate;
					amplitude = getBuilder().zoomRandomizingAmplitude;
				} else {
					currentState = RenderableState.SHOOTING;
				}

				break;
				
			case COMPOUND_RELOAD_EMPTY:
				currentState = RenderableState.COMPOUND_RELOAD_EMPTY;
				break;

			case TACTICAL_RELOAD:
				
				
				currentState = RenderableState.TACTICAL_RELOAD;
				break;
				
			case COMPOUND_RELOAD:
				
				currentState = RenderableState.COMPOUND_RELOAD;
				break;
				
			
				
			case UNLOAD_PREPARING: case UNLOAD_REQUESTED: case UNLOAD:
			
			
				/*
				if(compoundReload) {
					currentState = RenderableState.COMPOUND_RELOAD;
				} else if(compoundReloadEmpty){
					currentState = RenderableState.COMPOUND_RELOAD_EMPTY;
				} else {
					currentState = RenderableState.UNLOADING;
				}*/
				if(shouldDoEmptyVariant()) {
					currentState = RenderableState.UNLOAD_EMPTY;
				} else {
					currentState = RenderableState.UNLOADING;
				}
				
			
				
				break;

			case LOAD:
				
				
				/*
				if(!compoundReload && !compoundReloadEmpty) {
					currentState = RenderableState.RELOADING;
				} 
				*/
				if(shouldDoEmptyVariant()) {
					currentState = RenderableState.LOAD_EMPTY;
				} else {
					currentState = RenderableState.RELOADING;
				}
				
				
				
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
				if(player.isSprinting() && getBuilder().firstPersonPositioningRunning != null
				
				&& asyncWeaponState.getState() == WeaponState.READY
				&& !playerWeaponInstance.isAwaitingCompoundInstructions()
				// Prevents jumping dureing reloading
				&& System.currentTimeMillis()-playerWeaponInstance.getStateUpdateTimestamp()  > 50
						) {
					
					
					currentState = RenderableState.RUNNING;
				} else if(playerWeaponInstance.isAimed()) {
					currentState = RenderableState.ZOOMING;
					rate = getBuilder().zoomRandomizingRate;
					amplitude = getBuilder().zoomRandomizingAmplitude;
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
        float amplitude = getBuilder().normalRandomizingAmplitude;
        float rate = getBuilder().normalRandomizingRate;
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
                        rate = getBuilder().firingRandomizingRate;
                        amplitude = getBuilder().zoomRandomizingAmplitude;
                    } else {
                        currentState = RenderableState.NORMAL;
                        rate = getBuilder().firingRandomizingRate;
                        amplitude = getBuilder().firingRandomizingAmplitude;
                    }
                } else if(playerWeaponInstance.isAimed()) {
                    currentState = RenderableState.ZOOMING_RECOILED;
                    amplitude = getBuilder().zoomRandomizingAmplitude;
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
                            rate = getBuilder().firingRandomizingRate;
                        }
                        amplitude = getBuilder().zoomRandomizingAmplitude;
                    } else {
                        currentState = RenderableState.NORMAL;
                        if(!isLongPaused) {
                            rate = getBuilder().firingRandomizingRate;
                            amplitude = getBuilder().firingRandomizingAmplitude;
                        }
                    }
                } else if(playerWeaponInstance.isAimed()) {
                    currentState = RenderableState.ZOOMING_SHOOTING;
                    //rate = builder.firingRandomizingRate;
                    amplitude = getBuilder().zoomRandomizingAmplitude;
                } else {
                    currentState = RenderableState.SHOOTING;
                }

                break;

            case UNLOAD_PREPARING: case UNLOAD_REQUESTED: case UNLOAD:
                currentState = RenderableState.UNLOADING;
                break;

            case LOAD:
            	
            	
            	
            
            	
                //currentState = RenderableState.RELOADING;
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
                if(player.isSprinting() && getBuilder().firstPersonPositioningRunning != null) {
                    currentState = RenderableState.RUNNING;
                } else if(playerWeaponInstance.isAimed()) {
                    currentState = RenderableState.ZOOMING;
                    rate = getBuilder().zoomRandomizingRate;
                    amplitude = getBuilder().zoomRandomizingAmplitude;
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
	
	
	private List<MultipartTransition<Part, RenderContext<RenderableState>>> getComplexTransition(TransitionContainer tc) {
		return getComplexTransition(tc.getFirstPerson(),
				tc.getLeftHand(),
				tc.getRightHand(),
				tc.getCustom());
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
	
	private List<MultipartTransition<Part, RenderContext<RenderableState>>> getSimpleTransition(
			Consumer<RenderContext<RenderableState>> w,
			Consumer<RenderContext<RenderableState>> lh,
			Consumer<RenderContext<RenderableState>> rh,
			//Consumer<RenderContext<RenderableState>> m,
			LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> custom,
			int duration,
			Interpolation interpType
			) {
		MultipartTransition<Part, RenderContext<RenderableState>> mt = new MultipartTransition<Part, RenderContext<RenderableState>>(duration, 0, interpType)
				.withPartPositionFunction(Part.MAIN_ITEM, createWeaponPartPositionFunction(w))
				.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(lh))
				.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(rh));
		custom.forEach((part, position) -> {
			mt.withPartPositionFunction(part, createWeaponPartPositionFunction(position));
		});
		return Collections.singletonList(mt);
	}
	
	private List<MultipartTransition<Part, RenderContext<RenderableState>>> getSimpleTransitionBeizer(
			Consumer<RenderContext<RenderableState>> w,
			Consumer<RenderContext<RenderableState>> lh,
			Consumer<RenderContext<RenderableState>> rh,
			//Consumer<RenderContext<RenderableState>> m,
			LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> custom,
			int duration, Vec3d beizer) {
		MultipartTransition<Part, RenderContext<RenderableState>> mt = new MultipartTransition<Part, RenderContext<RenderableState>>(duration, 0, beizer)
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
				return getSimpleTransition(getBuilder().firstPersonPositioningModifying,
						getBuilder().firstPersonLeftHandPositioningModifying,
						getBuilder().firstPersonRightHandPositioningModifying,
						getBuilder().firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION);
			case MODIFYING_ALT:
                return getSimpleTransition(getBuilder().firstPersonPositioningModifyingAlt,
                        getBuilder().firstPersonLeftHandPositioningModifyingAlt,
                        getBuilder().firstPersonRightHandPositioningModifyingAlt,
                        getBuilder().firstPersonCustomPositioning,
                        DEFAULT_ANIMATION_DURATION);
			case RUNNING:
				return
						getSimpleTransition(getBuilder().firstPersonPositioningRunning,
						getBuilder().firstPersonLeftHandPositioningRunning,
						getBuilder().firstPersonRightHandPositioningRunning,
						getBuilder().firstPersonCustomPositioning,
						325, Interpolation.ACCELERATION);
			case UNLOAD_EMPTY:
				return getComplexTransition(getBuilder().unloadEmptyContainer);
			case LOAD_EMPTY:
				return getComplexTransition(getBuilder().loadEmptyContainer);
			case TACTICAL_RELOAD:
				
				
				return getComplexTransition(getBuilder().tacticalReloadContainer);
			case UNLOADING:
				return getComplexTransition(getBuilder().firstPersonPositioningUnloading,
						getBuilder().firstPersonLeftHandPositioningUnloading,
						getBuilder().firstPersonRightHandPositioningUnloading,
						getBuilder().firstPersonCustomPositioningUnloading
						);
			case RELOADING:
				return getComplexTransition(getBuilder().firstPersonPositioningReloading,
						getBuilder().firstPersonLeftHandPositioningReloading,
						getBuilder().firstPersonRightHandPositioningReloading,
						getBuilder().firstPersonCustomPositioningReloading
						);
			case COMPOUND_RELOAD:
				
				return getComplexTransition(getBuilder().compoundReloadContainer);
			case COMPOUND_RELOAD_EMPTY:
				
				
				
				return getComplexTransition(getBuilder().compoundReloadEmptyContainer);
			case LOAD_ITERATION:
                return getComplexTransition(getBuilder().firstPersonPositioningLoadIteration,
                        getBuilder().firstPersonLeftHandPositioningLoadIteration,
                        getBuilder().firstPersonRightHandPositioningLoadIteration,
                        getBuilder().firstPersonCustomPositioningLoadIteration
                        );
			case INSPECTING:
				
                return getComplexTransition(getBuilder().firstPersonPositioningInspecting,
                        getBuilder().firstPersonLeftHandPositioningInspecting,
                        getBuilder().firstPersonRightHandPositioningInspecting,
                        getBuilder().firstPersonCustomPositioningInspecting
                        );
            case DRAWING:
                return getComplexTransition(getBuilder().firstPersonPositioningDrawing,
                        getBuilder().firstPersonLeftHandPositioningDrawing,
                        getBuilder().firstPersonRightHandPositioningDrawing,
                        getBuilder().firstPersonCustomPositioningDrawing
                        );
			case LOAD_ITERATION_COMPLETED:
                return getSimpleTransition(getBuilder().firstPersonPositioningLoadIterationCompleted,
                        getBuilder().firstPersonLeftHandPositioningLoadIterationCompleted,
                        getBuilder().firstPersonRightHandPositioningLoadIterationCompleted,
                        getBuilder().firstPersonCustomPositioningLoadIterationCompleted,
                        getBuilder().loadIterationCompletedAnimationDuration);
			case ALL_LOAD_ITERATIONS_COMPLETED:
                return getComplexTransition(getBuilder().firstPersonPositioningAllLoadIterationsCompleted,
                        getBuilder().firstPersonLeftHandPositioningAllLoadIterationsCompleted,
                        getBuilder().firstPersonRightHandPositioningAllLoadIterationsCompleted,
                        getBuilder().firstPersonCustomPositioningLoadIterationsCompleted
                        );
			case RECOILED:
				return getSimpleTransition(getBuilder().firstPersonPositioningRecoiled,
						getBuilder().firstPersonLeftHandPositioningRecoiled,
						getBuilder().firstPersonRightHandPositioningRecoiled,
						getBuilder().firstPersonCustomPositioningRecoiled,
						getBuilder().recoilAnimationDuration);
            case PRONING_RECOILED:
                return getSimpleTransition(getBuilder().firstPersonPositioningProningRecoiled,
                        getBuilder().firstPersonLeftHandPositioningProningRecoiled,
                        getBuilder().firstPersonRightHandPositioningProningRecoiled,
                        getBuilder().firstPersonCustomPositioningProningRecoiled,
                        getBuilder().recoilAnimationDuration);
			case SHOOTING:
				return getSimpleTransition(getBuilder().firstPersonPositioningShooting,
						getBuilder().firstPersonLeftHandPositioningShooting,
						getBuilder().firstPersonRightHandPositioningShooting,
						getBuilder().firstPersonCustomPositioning,
						getBuilder().shootingAnimationDuration);
            case PRONING_SHOOTING:
                return getSimpleTransition(getBuilder().firstPersonPositioningProningShooting,
                        getBuilder().firstPersonLeftHandPositioningProningShooting,
                        getBuilder().firstPersonRightHandPositioningProningShooting,
                        getBuilder().firstPersonCustomPositioning,
                        getBuilder().shootingAnimationDuration);
			case EJECT_SPENT_ROUND:
				return getComplexTransition(getBuilder().firstPersonPositioningEjectSpentRound,
						getBuilder().firstPersonLeftHandPositioningEjectSpentRound,
						getBuilder().firstPersonRightHandPositioningEjectSpentRound,
						getBuilder().firstPersonCustomPositioningEjectSpentRound
						);
			case EJECT_SPENT_ROUND_AIMED:
                return getComplexTransition(getBuilder().firstPersonPositioningEjectSpentRoundAimed,
                        getBuilder().firstPersonLeftHandPositioningEjectSpentRoundAimed,
                        getBuilder().firstPersonRightHandPositioningEjectSpentRoundAimed,
                        getBuilder().firstPersonCustomPositioningEjectSpentRoundAimed
                        );
			case NORMAL:
				return getSimpleTransitionBeizer(getBuilder().firstPersonPositioning,
						getBuilder().firstPersonLeftHandPositioning,
						getBuilder().firstPersonRightHandPositioning,
						getBuilder().firstPersonCustomPositioning,
						DEFAULT_ANIMATION_DURATION, getBuilder().beizer);
            case PRONING:
                return getSimpleTransition(getBuilder().firstPersonPositioningProning,
                        getBuilder().firstPersonLeftHandPositioningProning,
                        getBuilder().firstPersonRightHandPositioningProning,
                        getBuilder().firstPersonCustomPositioningProning,
                        DEFAULT_ANIMATION_DURATION);
			case ZOOMING:
				
				
				return getSimpleTransitionBeizer(getBuilder().firstPersonPositioningZooming,
						getBuilder().firstPersonLeftHandPositioningZooming,
						getBuilder().firstPersonRightHandPositioningZooming,
						getBuilder().firstPersonCustomPositioningZooming,
						DEFAULT_ANIMATION_DURATION, getBuilder().beizer);
				
			case ZOOMING_SHOOTING:
				return getSimpleTransition(getBuilder().firstPersonPositioningZoomingShooting,
						getBuilder().firstPersonLeftHandPositioningZooming,
						getBuilder().firstPersonRightHandPositioningZooming,
						getBuilder().firstPersonCustomPositioningZoomingShooting,
						60);
			case ZOOMING_RECOILED:
				return getSimpleTransition(getBuilder().firstPersonPositioningZoomingRecoiled,
						getBuilder().firstPersonLeftHandPositioningZooming,
						getBuilder().firstPersonRightHandPositioningZooming,
						getBuilder().firstPersonCustomPositioningZoomingRecoiled,
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
                return getComplexTransition(getBuilder().thirdPersonPositioningUnloading,
                        getBuilder().thirdPersonLeftHandPositioningUnloading,
                        getBuilder().thirdPersonRightHandPositioningUnloading,
                        getBuilder().thirdPersonCustomPositioningUnloading
                        );
            case RELOADING:
                return getComplexTransition(getBuilder().thirdPersonPositioningReloading,
                        getBuilder().thirdPersonLeftHandPositioningReloading,
                        getBuilder().thirdPersonRightHandPositioningReloading,
                        getBuilder().thirdPersonCustomPositioningReloading
                        );
            case NORMAL: default:
                return getSimpleTransition(getBuilder().thirdPersonPositioning,
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
	
	public static ResourceLocation SPARKS_ONE = new ResourceLocation("mw" + ":" + "textures/flashes/sparks1.png");
	public static ResourceLocation FLASHF = new ResourceLocation("mw" + ":" + "textures/flashes/flashfront2.png");

	public static ItemAttachment<Weapon> magicMagReplacement;
	public static boolean updateMagicMagazine;


	public static void renderFlash(ItemStack weaponItemStack, boolean bloom) {
		Shaders.flash.use();
		
		Shaders.flash.uniform1i("bloom", bloom ? 1 : 0);
		
		GlStateManager.disableLighting();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		
		GlStateManager.disableCull();
		GlStateManager.color(1f, 1f, 1f, 0.99f);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		Vec3d mP = ((Weapon) weaponItemStack.getItem()).getMuzzlePosition();
		if(CompatibleClientEventHandler.muzzlePositioner) {
			mP = CompatibleClientEventHandler.debugmuzzlePosition;
		}
		GlStateManager.translate(mP.x, mP.y, mP.z);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ClientEventHandler.FLASH);
		EntityPlayer p = Minecraft.getMinecraft().player;

		
		
		GlStateManager.rotate(-180f, 0f, 1f, 0f);
		//GlStateManager.rotate(p.rotationPitch, 1f, 0f, 0f);
		// renderFlashPlane(0, 0, 0, 0.7, 0.4, 0);
		//double scale = 3.0-(1*Math.random());
		double scale = 3;
		
		
		//	GlStateManager.color(2.0f, 2.0f, 2.0f);
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
			ClientEventHandler.renderFlashPlane(0, 0, 0, 0, 0.6 * scale, 0.8 * scale, 0);
			ClientEventHandler.renderFlashPlane(0, 0, 0, 0, 0.6 * scale, 0.8 * scale, 90);
			
		}
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(SPARKS_ONE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    
    	Shaders.flash.uniform1i("natural", 1);
		ClientEventHandler.renderSparks(0, 0, 0, 0.6*scale, 0.6 * scale, 0, 0, 3);
		Shaders.flash.uniform1i("natural", 0);
		Minecraft.getMinecraft().getTextureManager().bindTexture(ClientEventHandler.FLASHF);

		
		
		ClientEventHandler.renderFlashPlane(0, 0, 0.1, 0.6*scale, 0.6 * scale, 0, 30);
		ClientEventHandler.renderFlashPlane(0, 0, 2.0/scale, 0.5 * scale, 0.5 * scale, 0, 30);
		ClientEventHandler.renderFlashPlane(0, 0, 3.5/scale, 0.4 * scale, 0.4 * scale, 0, 0);
		
		
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		Shaders.flash.release();
	}
	
	
	public static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
	public static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
	public static final FloatBuffer POSITION = GLAllocation.createDirectFloatBuffer(4);
    
	
	
	@Override
	public void renderItem(ItemStack weaponItemStack, RenderContext<RenderableState> renderContext,
			Positioner<Part, RenderContext<RenderableState>> positioner) {
	    
//	    if(player.getDistanceSqToEntity(compatibility.clientPlayer()) > 400) {
//	        return;
//	    }
		
		
		
		Weapon wea = (Weapon) weaponItemStack.getItem();
	
		
		if(CompatibleClientEventHandler.muzzlePositioner && !OpenGLSelectionHelper.isInSelectionPass) {
			//Bloom.bindBloomBuffer();
			//System.out.println("yo");
			
			GlStateManager.pushMatrix();
			Vec3d deb = CompatibleClientEventHandler.debugmuzzlePosition;
			GlStateManager.translate(deb.x, deb.y, deb.z);
			CompatibleWeaponRenderer.captureAtlasPosition();
			GlStateManager.popMatrix();
		}
		
		
		
		if(renderContext.getPlayer() != null && (ClientEventHandler.checkShot(renderContext.getPlayer().getEntityId()) || AnimationGUI.getInstance().forceFlash.isState())) {
			
			//flash = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/flash"));
			
			Bloom.bindBloomBuffer();
			
			
			//Dloom.bloomData.bindFramebuffer(false);
			renderFlash(weaponItemStack, true);
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
			renderFlash(weaponItemStack, false);
			
		}
		
		
		
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);
		Project.gluProject(-0.15f, -1.5f, 1f, MODELVIEW, PROJECTION, VIEWPORT, POSITION);
	      
		
		
		
		//Project.gluProject((float) (100f*Math.random()-100), (float) (100f*Math.random()-100), (float) (100f*Math.random()-100), buf, buf2, buf3, test);
        
		 
		
	//	System.out.println(POSITION.get(0) + " | " + POSITION.get(1) + " | " + POSITION.get(2) + " | " + POSITION.get(3));
		//GLU.gluUnProject(winx, winy, winz, modelMatrix, projMatrix, viewport, obj_pos)
		
		
		List<CompatibleAttachment<? extends AttachmentContainer>> attachments = null;
		if(getBuilder().getModel() instanceof ModelWithAttachments) {
			attachments = ((Weapon) weaponItemStack.getItem()).getActiveAttachments(renderContext.getPlayer(), weaponItemStack);
		}

		if(getBuilder().getTextureName() != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(getBuilder().getModId()
					+ ":textures/models/" + getBuilder().getTextureName()));
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

			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(getBuilder().getModId()
					+ ":textures/models/" + textureName));
		}
		//gunLightingShader = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/gunlight"));
	    
		if(!OpenGLSelectionHelper.isInSelectionPass && !AnimationGUI.getInstance().magEdit.isState()) {
			
			Shaders.gunLightingShader.use();
			
			
	    	GL20.glUniform1i(GL20.glGetUniformLocation(Shaders.gunLightingShader.getShaderId(), "lightmap"), 1);
	    	GL20.glUniform1f(GL20.glGetUniformLocation(Shaders.gunLightingShader.getShaderId(), "lightIntensity"), (ClientValueRepo.flash > 0) ? 5.0f : 0.0f);
	    	
		}
		
    	// Clears out the defferal list, so that a new set can be
		// populated in.
		deferredPost.clear();

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
		    getBuilder().getModel().render(this.player,
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
		
		
		
		if(!OpenGLSelectionHelper.isInSelectionPass) Shaders.gunLightingShader.release();
		
		if(!AnimationModeProcessor.getInstance().getFPSMode()) renderPostRenderers(renderContext);
		
		
		
		/*
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			@SuppressWarnings("unchecked")
	        CustomRenderer<RenderableState> postRenderer = (CustomRenderer<RenderableState>) compatibleAttachment.getAttachment().getPostRenderer();
			if(postRenderer != null) {
				
				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
				postRenderer.render(renderContext);
				GL11.glPopAttrib();
				GL11.glPopMatrix();
				
			}
		}
		*/
		
	}

	public void renderAttachments(Positioner<Part, RenderContext<RenderableState>> positioner, RenderContext<RenderableState> renderContext,List<CompatibleAttachment<? extends AttachmentContainer>> attachments) {
		
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null && !(compatibleAttachment.getAttachment() instanceof ItemSkin) && !(compatibleAttachment.getAttachment() instanceof ItemScope)) {
				renderCompatibleAttachment(compatibleAttachment, positioner, renderContext);
			}
		}
		
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null && !(compatibleAttachment.getAttachment() instanceof ItemSkin) && (compatibleAttachment.getAttachment() instanceof ItemScope)) {
				renderCompatibleAttachment(compatibleAttachment, positioner, renderContext);
			}
		}
	}

	
	
	
	public void setMagicMagPermit(boolean state) {
		magicMagPermit = state;
	}
	
	
	public void setMagicMag(PlayerWeaponInstance instance, ItemAttachment<Weapon> weapon, WeaponState nextState) {
		
		magicMagReplacement = weapon;
		updateMagicMagazine = true;
		magicState = nextState;

		magicAnimationTimer = System.currentTimeMillis();
		
		Vec3d magRotationPoint = ((ItemMagazine) weapon).getRotationPoint();
		
		if(getBuilder().isHasTacticalReload() && nextState == WeaponState.TACTICAL_RELOAD) {
			getWeaponRendererBuilder().tacticalReloadContainer.getCustom().put(SpecialAttachments.MagicMag.getRenderablePart(),
					BBLoader.getAnimation(getBuilder().getAnimationFileName(), BBLoader.KEY_TACTICAL_RELOAD, BBLoader.KEY_MAGIC_MAGAZINE)
					.getTransitionList(Transform.NULL
							
							.withRotationPoint(magRotationPoint.x, magRotationPoint.y, magRotationPoint.z)
							.copy(), BBLoader.HANDDIVISOR));
			
			
		} else if(isCompoundReloadEmptyTactical() && nextState == WeaponState.COMPOUND_RELOAD_EMPTY) {
			
			// Log
			logger.debug("Creating a compound empty animation using the magic magazine system");
			
			getWeaponRendererBuilder().compoundReloadEmptyContainer.getCustom().put(SpecialAttachments.MagicMag.getRenderablePart(),
					BBLoader.getAnimation(getBuilder().getAnimationFileName(), BBLoader.KEY_COMPOUND_RELOAD_EMPTY, BBLoader.KEY_MAGIC_MAGAZINE)
					.getTransitionList(Transform.NULL
							
							.withRotationPoint(magRotationPoint.x, magRotationPoint.y, magRotationPoint.z)
							.copy(), BBLoader.HANDDIVISOR));
		} else if(isCompoundReloadTactical() && nextState == WeaponState.COMPOUND_RELOAD) {
			
			// Log
			logger.debug("Creating a standard compound animation using the magic magazine system");
			
			getWeaponRendererBuilder().compoundReloadContainer.getCustom().put(SpecialAttachments.MagicMag.getRenderablePart(),
					BBLoader.getAnimation(getBuilder().getAnimationFileName(), BBLoader.KEY_COMPOUND_RELOAD, BBLoader.KEY_MAGIC_MAGAZINE)
					.getTransitionList(Transform.NULL
							.withRotationPoint(magRotationPoint.x, magRotationPoint.y, magRotationPoint.z)
							.copy(), BBLoader.HANDDIVISOR));
		} 
	}
	
	private CompatibleAttachment<?> currentMagazine;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private void renderCompatibleAttachment(CompatibleAttachment<?> compatibleAttachment,
			Positioner<Part, RenderContext<RenderableState>> positioner, RenderContext<RenderableState> renderContext) {

		if(compatibleAttachment.getAttachment().getCategory() == AttachmentCategory.MAGAZINE) {
			currentMagazine = compatibleAttachment;
		}
		
		
		// Do magic mag stuff
		if(compatibleAttachment.getAttachment().getCategory() == AttachmentCategory.MAGICMAG) {
			
			
			
			WeaponState state = renderContext.getWeaponInstance().getState();
			if(!renderContext.getWeaponInstance().getWeapon().builder.isUsingNewSystem()) return;
			
			
			
			
			if(magicMagReplacement != null && updateMagicMagazine) {
				// New magazine? No problem-- just swap the models.
				updateMagicMagazine = false;
				compatibleAttachment.getAttachment().setFirstModel(magicMagReplacement);
			}
			
			
			
			
			// The *1.2 is important so that the magazine is there until the animation is fully completed
			boolean time = System.currentTimeMillis() - this.magicAnimationTimer >=
					 renderContext.getWeaponInstance().getAnimationDuration(magicState) * 1.2;
		
			
			
			// If the times up, remove our permit
			if(time) setMagicMagPermit(false);
					
			// If we don't have a permit cancel
						//if(!magicMagPermit) return;
			
			boolean isFinishing = state != WeaponState.COMPOUND_RELOAD_FINISHED && state != WeaponState.COMPOUND_RELOAD_FINISH;
			
			
			if(magicState == WeaponState.COMPOUND_RELOAD) {
				if((state == WeaponState.COMPOUND_REQUESTED || state == WeaponState.COMPOUND_RELOAD || state == WeaponState.COMPOUND_RELOAD_FINISHED) && !isCompoundReloadTactical()) {
					
					return;
				}
			}
			
			if(magicState == WeaponState.COMPOUND_RELOAD_EMPTY) {
				if((state == WeaponState.COMPOUND_RELOAD_EMPTY) && !isCompoundReloadEmptyTactical()) {
					return;
				}
			}
			
			
		
			
			
			// Run checks
			if(time) {
				if(!isCompoundReloadEmptyTactical() && !isCompoundReloadTactical() && !getBuilder().isHasTacticalReload())
					return;
					
					
				
				
				
					
				if(state != WeaponState.COMPOUND_RELOAD_EMPTY && state != WeaponState.COMPOUND_RELOAD && state != WeaponState.TACTICAL_RELOAD && isFinishing) {
					return;
				}
				
				
				
				
			
			
			} else if(!isCompoundReloadEmptyTactical() && !isCompoundReloadTactical() && !getBuilder().isHasTacticalReload()){
				return;
			}
			
			
			
			//ItemAttachment<Weapon> exp = renderContext.getModContext().getAttachmentAspect().getActiveAttachment(renderContext.getWeaponInstance(), AttachmentCategory.MAGAZINE);
			if(currentMagazine != null) {
				//compatibleAttachment.getModelPositioning().accept(texturedModel.getU());
				
				//currentMagazine.getModelPositioning().accept(arg0);
			}
			
			
		
		}
		
		
		
		
		
		if(compatibleAttachment.getAttachment() instanceof ItemMagazine && AnimationGUI.getInstance().magEdit.isState() && !OpenGLSelectionHelper.isInSelectionPass) {
	    	ItemMagazine mag = (ItemMagazine) compatibleAttachment.getAttachment();
	    	
	    	
	    	GlStateManager.pushMatrix();
	    	
	    	//GlStateManager.translate(mag.getRotationPoint().x, mag.getRotationPoint().y, mag.getRotationPoint().z);
	    	GlStateManager.translate(CompatibleClientEventHandler.magRotPositioner.x, CompatibleClientEventHandler.magRotPositioner.y, CompatibleClientEventHandler.magRotPositioner.z);
	    	
	    	
	    	CompatibleWeaponRenderer.captureAtlasPosition();
	    	//AnimationModeProcessor.getInstance().captureDeferral();
	    	
	    	GlStateManager.popMatrix();
	    }
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

	   // GlStateManager.rotate(45, 1, 0, 0);
	    
	    
	    
	    
	   
	    
		for(Tuple<ModelBase, String> texturedModel: compatibleAttachment.getAttachment().getTexturedModels()) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(getBuilder().getModId()
					+ ":textures/models/" + texturedModel.getV()));
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
			if(compatibleAttachment.getModelPositioning() != null) {
			
				/*
				if(!(compatibleAttachment.getAttachment() instanceof ItemMagazine)) {
					compatibleAttachment.getModelPositioning().accept(texturedModel.getU());
				} else {
					new Transform().withScale(1, 1, 1).withRotationPoint(CompatibleClientEventHandler.magRotPositioner.x, CompatibleClientEventHandler.magRotPositioner.y, CompatibleClientEventHandler.magRotPositioner.z)
							.withRotation(45, 0, 0).doGLDirect();
				}*/
				
				
				if(compatibleAttachment.getAttachment().getCategory() == AttachmentCategory.MAGICMAG) {
					if(currentMagazine != null) {
						currentMagazine.getModelPositioning().accept(texturedModel.getU());
					}
				} else {
					compatibleAttachment.getModelPositioning().accept(texturedModel.getU());
					
				}
				
				
				/*
				if((compatibleAttachment.getAttachment() instanceof ItemMagazine)) {
					new Transform().withScale(1, 1, 1).withRotationPoint(CompatibleClientEventHandler.magRotPositioner.x, CompatibleClientEventHandler.magRotPositioner.y, CompatibleClientEventHandler.magRotPositioner.z)
					.withRotation(0, 0, 0).doGLDirect();
				}
				*/
				
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
			// Stuff like lasers goes in here
			deferredPost.add(new Pair<>(captureCurrentModelViewMatrix(), postRenderer));
			
		}
		

		for(CompatibleAttachment<?> childAttachment: itemAttachment.getAttachments()) {
			renderCompatibleAttachment(childAttachment, positioner, renderContext);
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	public FloatBuffer captureCurrentModelViewMatrix() {
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
		buf.rewind();
		return buf;
	}
	
	public void renderPostRenderers(RenderContext<RenderableState> renderContext) {
		
		for(Pair<FloatBuffer, CustomRenderer<RenderableState>> pair : this.deferredPost) {
			GL11.glPushMatrix();
			
			GL11.glLoadMatrix(pair.first());
			
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
			pair.second().render(renderContext);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		
	}
	
	public boolean hasRecoilPositioning() {
        return getBuilder().hasRecoilPositioningDefined;
    }
	
	public Vec3d getADSBeizer() {
		return getBuilder().beizer;
	}

    public long getTotalLoadIterationDuration() {
        return getBuilder().totalLoadIterationDuration;
    }

    public long getPrepareFirstLoadIterationAnimationDuration() {
        return getBuilder().prepareFirstLoadIterationAnimationDuration;
    }
    
    public long getAllLoadIterationAnimationsCompletedDuration() {
        return getBuilder().allLoadIterationAnimationsCompletedDuration;
    }

    public MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> getStateManager(EntityPlayer player) {
        return firstPersonStateManagers.get(player);
    }


	public Builder getBuilder() {
		return builder;
	}


	public void setBuilder(Builder builder) {
		this.builder = builder;
	}
}
