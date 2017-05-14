package com.vicmatskiv.weaponlib.grenade;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.AttachmentContainer;
import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.CompatibleAttachment;
import com.vicmatskiv.weaponlib.CustomRenderer;
import com.vicmatskiv.weaponlib.DefaultPart;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ItemSkin;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.Tuple;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.DebugPositioner.TransitionConfiguration;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;
import com.vicmatskiv.weaponlib.animation.Transition;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGrenadeRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GrenadeRenderer extends CompatibleGrenadeRenderer {

	private static final Logger logger = LogManager.getLogger(GrenadeRenderer.class);

	private static final float DEFAULT_RANDOMIZING_RATE = 0.33f;

	private static final float DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE = 0.06f;

	private static final int DEFAULT_ANIMATION_DURATION = 70;


	private static class SimplePositioning {
	    Part attachedTo;
	    Consumer<RenderContext<RenderableState>> positioning;
        SimplePositioning(Part attachedTo, Consumer<RenderContext<RenderableState>> positioning) {
            this.attachedTo = attachedTo;
            this.positioning = positioning;
        }
	}

	public static class Builder {

		private ModelBase model;
		private String textureName;

		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private Consumer<RenderContext<RenderableState>> thirdPersonPositioning;

		private Consumer<RenderContext<RenderableState>> firstPersonPositioning;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioning;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioning;
        private LinkedHashMap<Part, SimplePositioning> firstPersonCustomPositioning = new LinkedHashMap<>();

		private Consumer<RenderContext<RenderableState>> firstPersonPositioningRunning;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningRunning;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningRunning;
        private LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> firstPersonCustomPositioningRunning = new LinkedHashMap<>();

		private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningTakingSafetyPinOff;
		private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningTakingSafetyPinOff;
		private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningTakingSafetyPinOff;
		private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningTakingSafetyPinOff = new LinkedHashMap<>();

        private Consumer<RenderContext<RenderableState>> firstPersonPositioningSafetyPinOff;
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningSafetyPinOff;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningSafetyPinOff;
        private LinkedHashMap<Part, SimplePositioning> firstPersonCustomPositioningSafetyPinOff = new LinkedHashMap<>();

		private List<Transition<RenderContext<RenderableState>>> firstPersonPositioningThrowing;
        private List<Transition<RenderContext<RenderableState>>> firstPersonLeftHandPositioningThrowing;
        private List<Transition<RenderContext<RenderableState>>> firstPersonRightHandPositioningThrowing;
        private LinkedHashMap<Part, List<Transition<RenderContext<RenderableState>>>> firstPersonCustomPositioningThrowing = new LinkedHashMap<>();

        private Consumer<RenderContext<RenderableState>> firstPersonPositioningThrown;
        private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioningThrown;
        private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioningThrown;
        private LinkedHashMap<Part, SimplePositioning> firstPersonCustomPositioningThrown = new LinkedHashMap<>();

		private long totalTakingPinOffDuration;
		private long totalThrowingDuration;

		private String modId;

		private float normalRandomizingRate = DEFAULT_RANDOMIZING_RATE; // movements per second, e.g. 0.25 = 0.25 movements per second = 1 movement in 3 minutes
		private float normalRandomizingAmplitude = DEFAULT_NORMAL_RANDOMIZING_AMPLITUDE;
        public int animationDuration = DEFAULT_ANIMATION_DURATION;

		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}

		public Builder withModel(ModelBase model) {
			this.model = model;
			return this;
		}

		public Builder withAnimationDuration(int animationDuration) {
		    this.animationDuration = animationDuration;
		    return this;
		}

		public Builder withNormalRandomizingRate(float normalRandomizingRate) {
			this.normalRandomizingRate = normalRandomizingRate;
			return this;
		}

		public Builder withTextureName(String textureName) {
			this.textureName = textureName + ".png";
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

		public Builder withFirstPersonHandPositioning(
                Consumer<RenderContext<RenderableState>> leftHand,
                Consumer<RenderContext<RenderableState>> rightHand)
        {
            this.firstPersonLeftHandPositioning = leftHand;
            this.firstPersonRightHandPositioning = rightHand;
            return this;
        }

		public Builder withFirstPersonCustomPositioning(Part part, Part attachedTo, Consumer<RenderContext<RenderableState>> positioning) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            if(this.firstPersonCustomPositioning.put(part, new SimplePositioning(attachedTo, positioning)) != null) {
                throw new IllegalArgumentException("Part " + part + " already added");
            }
            return this;
        }

		public Builder withFirstPersonPositioningRunning(Consumer<RenderContext<RenderableState>> firstPersonPositioningRunning) {
			this.firstPersonPositioningRunning = firstPersonPositioningRunning;
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

		public Builder withFirstPersonCustomPositioningRunning(Part part, Consumer<RenderContext<RenderableState>> positioning) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            if(this.firstPersonCustomPositioningRunning.put(part, positioning) != null) {
                throw new IllegalArgumentException("Part " + part + " already added");
            }
            return this;
        }


		public Builder withFirstPersonPositioningThrown(Consumer<RenderContext<RenderableState>> firstPersonPositioningThrown) {
            this.firstPersonPositioningThrown = firstPersonPositioningThrown;
            return this;
        }

        public Builder withFirstPersonHandPositioningThrown(
                Consumer<RenderContext<RenderableState>> leftHand,
                Consumer<RenderContext<RenderableState>> rightHand)
        {
            this.firstPersonLeftHandPositioningThrown = leftHand;
            this.firstPersonRightHandPositioningThrown = rightHand;
            return this;
        }

        public Builder withFirstPersonCustomPositioningThrown(Part part, Part attachedTo, Consumer<RenderContext<RenderableState>> positioning) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            if(this.firstPersonCustomPositioningThrown.put(part,
                    new SimplePositioning(attachedTo, positioning)) != null) {
                throw new IllegalArgumentException("Part " + part + " already added");
            }
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonPositioningTakingSafetyPinOff(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonPositioningTakingSafetyPinOff = Arrays.asList(transitions);
			return this;
		}

		@SafeVarargs
        public final Builder withFirstPersonPositioningThrowing(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonPositioningThrowing = Arrays.asList(transitions);
            return this;
        }

		public Builder withFirstPersonPositioningSafetyPinOff(Consumer<RenderContext<RenderableState>> firstPersonPositioningSafetyPinOff) {
			this.firstPersonPositioningSafetyPinOff = firstPersonPositioningSafetyPinOff;
			return this;
		}


		@SafeVarargs
		public final Builder withFirstPersonLeftHandPositioningTakingSafetyPinOff(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonLeftHandPositioningTakingSafetyPinOff = Arrays.asList(transitions);
			return this;
		}

		@SafeVarargs
        public final Builder withFirstPersonLeftHandPositioningThrowing(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonLeftHandPositioningThrowing = Arrays.asList(transitions);
            return this;
        }

		@SafeVarargs
        public final Builder withFirstPersonRightHandPositioningThrowing(Transition<RenderContext<RenderableState>> ...transitions) {
            this.firstPersonRightHandPositioningThrowing = Arrays.asList(transitions);
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonRightHandPositioningTakingSafetyPinOff(Transition<RenderContext<RenderableState>> ...transitions) {
			this.firstPersonRightHandPositioningTakingSafetyPinOff = Arrays.asList(transitions);
			return this;
		}

		public Builder withFirstPersonHandPositioningSafetyPinOff(
				Consumer<RenderContext<RenderableState>> leftHand,
				Consumer<RenderContext<RenderableState>> rightHand)
		{
			this.firstPersonLeftHandPositioningSafetyPinOff = leftHand;
			this.firstPersonRightHandPositioningSafetyPinOff = rightHand;
			return this;
		}

		public Builder withFirstPersonCustomPositioningSafetyPinOff(Part part, Part attachedTo, Consumer<RenderContext<RenderableState>> positioning) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }
            if(this.firstPersonCustomPositioningSafetyPinOff.put(part,
                    new SimplePositioning(attachedTo, positioning)) != null) {
                throw new IllegalArgumentException("Part " + part + " already added");
            }
            return this;
        }

		@SafeVarargs
		public final Builder withFirstPersonCustomPositioningTakingSafetyPinOff(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
			if(part instanceof DefaultPart) {
				throw new IllegalArgumentException("Part " + part + " is not custom");
			}

			this.firstPersonCustomPositioningTakingSafetyPinOff.put(part, Arrays.asList(transitions));
			return this;
		}

		@SafeVarargs
        public final Builder withFirstPersonCustomPositioningThrowing(Part part, Transition<RenderContext<RenderableState>> ...transitions) {
            if(part instanceof DefaultPart) {
                throw new IllegalArgumentException("Part " + part + " is not custom");
            }

            this.firstPersonCustomPositioningThrowing.put(part, Arrays.asList(transitions));
            return this;
        }

		public GrenadeRenderer build() {
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

			GrenadeRenderer renderer = new GrenadeRenderer(this);

			if(firstPersonPositioning == null) {
				firstPersonPositioning = (renderContext) -> {};
			}

			if(firstPersonPositioningTakingSafetyPinOff == null) {
				firstPersonPositioningTakingSafetyPinOff = Collections.singletonList(new Transition<>(firstPersonPositioning, animationDuration));
			}

			if(firstPersonPositioningThrowing == null) {
                firstPersonPositioningThrowing = Collections.singletonList(new Transition<>(firstPersonPositioning, animationDuration));
            }

			for(Transition<RenderContext<RenderableState>> t: firstPersonPositioningTakingSafetyPinOff) {
				totalTakingPinOffDuration += t.getDuration();
				totalTakingPinOffDuration += t.getPause();
			}

			for(Transition<RenderContext<RenderableState>> t: firstPersonPositioningThrowing) {
                totalThrowingDuration += t.getDuration();
                totalThrowingDuration += t.getPause();
            }

			if(firstPersonPositioningRunning == null) {
				firstPersonPositioningRunning = firstPersonPositioning;
			}

			if(firstPersonPositioningSafetyPinOff == null) {
			    if(firstPersonPositioningTakingSafetyPinOff != null && !firstPersonPositioningTakingSafetyPinOff.isEmpty()) {
			        // Use last transition
			        firstPersonPositioningSafetyPinOff = firstPersonPositioningTakingSafetyPinOff
			                .get(firstPersonPositioningTakingSafetyPinOff.size() - 1).getItemPositioning();
			    }

			    if(firstPersonPositioningSafetyPinOff == null) {
			        firstPersonPositioningSafetyPinOff = firstPersonPositioning;
			    }
			}

			if(firstPersonPositioningThrown == null) {
                if(firstPersonPositioningThrowing != null && !firstPersonPositioningThrowing.isEmpty()) {
                    // Use last transition
                    firstPersonPositioningThrown = firstPersonPositioningThrowing
                            .get(firstPersonPositioningThrowing.size() - 1).getItemPositioning();
                }

                if(firstPersonPositioningThrown == null) {
                    firstPersonPositioningThrown = firstPersonPositioning;
                }
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

			if(firstPersonLeftHandPositioningTakingSafetyPinOff == null) {
				firstPersonLeftHandPositioningTakingSafetyPinOff = firstPersonPositioningTakingSafetyPinOff.stream().map(t -> new Transition<RenderContext<RenderableState>>(
				        c -> {}, 0)).collect(Collectors.toList());
			}

			if(firstPersonLeftHandPositioningThrowing == null) {
                firstPersonLeftHandPositioningThrowing = firstPersonPositioningThrowing.stream().map(t -> new Transition<RenderContext<RenderableState>>(
                        c -> {}, 0)).collect(Collectors.toList());
            }

			if(firstPersonRightHandPositioningThrowing == null) {
                firstPersonRightHandPositioningThrowing = firstPersonPositioningThrowing.stream().map(t -> new Transition<RenderContext<RenderableState>>(
                        c-> {}, 0)).collect(Collectors.toList());
            }

			if(firstPersonLeftHandPositioningRunning == null) {
				firstPersonLeftHandPositioningRunning = firstPersonLeftHandPositioning;
			}

			if(firstPersonLeftHandPositioningSafetyPinOff == null) {

			    if(firstPersonLeftHandPositioningTakingSafetyPinOff != null && !firstPersonLeftHandPositioningTakingSafetyPinOff.isEmpty()) {
                    // Use last transition
			        firstPersonLeftHandPositioningSafetyPinOff = firstPersonLeftHandPositioningTakingSafetyPinOff
                            .get(firstPersonLeftHandPositioningTakingSafetyPinOff.size() - 1).getItemPositioning();
                }

			    if(firstPersonLeftHandPositioningSafetyPinOff == null) {
			        firstPersonLeftHandPositioningSafetyPinOff = firstPersonLeftHandPositioning;
			    }

			}

			if(firstPersonLeftHandPositioningThrown == null) {

                if(firstPersonLeftHandPositioningThrowing != null && !firstPersonLeftHandPositioningThrowing.isEmpty()) {
                    // Use last transition
                    firstPersonLeftHandPositioningThrown = firstPersonLeftHandPositioningThrowing
                            .get(firstPersonLeftHandPositioningThrowing.size() - 1).getItemPositioning();
                }

                if(firstPersonLeftHandPositioningThrown == null) {
                    firstPersonLeftHandPositioningThrown = firstPersonLeftHandPositioning;
                }

            }

			// Right hand positioning

			if(firstPersonRightHandPositioning == null) {
				firstPersonRightHandPositioning = (context) -> {};
			}

			if(firstPersonRightHandPositioningTakingSafetyPinOff == null) {
				//firstPersonRightHandPositioningTakingSafetyPinOff = Collections.singletonList(new Transition(firstPersonRightHandPositioning, DEFAULT_ANIMATION_DURATION));
				firstPersonRightHandPositioningTakingSafetyPinOff = firstPersonPositioningTakingSafetyPinOff.stream().map(t -> new Transition<RenderContext<RenderableState>>(
				        c -> {}, 0)).collect(Collectors.toList());
			}

			if(firstPersonRightHandPositioningThrowing == null) {
                //firstPersonRightHandPositioningTakingSafetyPinOff = Collections.singletonList(new Transition(firstPersonRightHandPositioning, DEFAULT_ANIMATION_DURATION));
                firstPersonRightHandPositioningThrowing = firstPersonPositioningThrowing.stream().map(t -> new Transition<RenderContext<RenderableState>>(
                        c -> {}, 0)).collect(Collectors.toList());
            }

			if(firstPersonRightHandPositioningRunning == null) {
				firstPersonRightHandPositioningRunning = firstPersonRightHandPositioning;
			}

			if(firstPersonRightHandPositioningSafetyPinOff == null) {

                if(firstPersonRightHandPositioningTakingSafetyPinOff != null && !firstPersonRightHandPositioningTakingSafetyPinOff.isEmpty()) {
                    // Use last transition
                    firstPersonRightHandPositioningSafetyPinOff = firstPersonRightHandPositioningTakingSafetyPinOff
                            .get(firstPersonRightHandPositioningTakingSafetyPinOff.size() - 1).getItemPositioning();
                }

                if(firstPersonRightHandPositioningSafetyPinOff == null) {
                    firstPersonRightHandPositioningSafetyPinOff = firstPersonRightHandPositioning;
                }

            }

			if(firstPersonRightHandPositioningThrown == null) {

                if(firstPersonRightHandPositioningThrowing != null && !firstPersonRightHandPositioningThrowing.isEmpty()) {
                    // Use last transition
                    firstPersonRightHandPositioningThrown = firstPersonRightHandPositioningThrowing
                            .get(firstPersonRightHandPositioningThrowing.size() - 1).getItemPositioning();
                }

                if(firstPersonRightHandPositioningThrown == null) {
                    firstPersonRightHandPositioningThrown = firstPersonRightHandPositioning;
                }

            }

			firstPersonCustomPositioningTakingSafetyPinOff.forEach((p, t) -> {
				if(t.size() != firstPersonPositioningTakingSafetyPinOff.size()) {
					throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + firstPersonPositioningTakingSafetyPinOff.size()
					+ ", actual: " + t.size());
				}
			});

			firstPersonCustomPositioningThrowing.forEach((p, t) -> {
                if(t.size() != firstPersonPositioningThrowing.size()) {
                    throw new IllegalStateException("Custom reloading transition number mismatch. Expected " + firstPersonPositioningThrowing.size()
                    + ", actual: " + t.size());
                }
            });

			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningSafetyPinOff.isEmpty()) {
                firstPersonCustomPositioning.forEach((part, pos) -> {
                    firstPersonCustomPositioningSafetyPinOff.put(part, new SimplePositioning(null, pos.positioning));
                });
            }

			if(!firstPersonCustomPositioning.isEmpty() && firstPersonCustomPositioningThrown.isEmpty()) {
                firstPersonCustomPositioning.forEach((part, pos) -> {
                    firstPersonCustomPositioningThrown.put(part, new SimplePositioning(null, pos.positioning));
                });
            }

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

	private Map<EntityPlayer, MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>>> firstPersonStateManagers;

	private MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> weaponTransitionProvider;

	protected ClientModContext clientModContext;

	private GrenadeRenderer(Builder builder) {
		super(builder);
		this.builder = builder;
		this.firstPersonStateManagers = new HashMap<>();
		this.weaponTransitionProvider = new WeaponPositionProvider();
	}

	protected long getTotalAttackDuration() {
		return builder.totalTakingPinOffDuration;
	}

	protected long getTotalHeavyAttackDuration() {
        return builder.totalThrowingDuration;
    }

	protected ClientModContext getClientModContext() {
		return clientModContext;
	}

	public void setClientModContext(ClientModContext clientModContext) {
		this.clientModContext = clientModContext;
	}



	@Override
	protected StateDescriptor getStateDescriptor(EntityPlayer player, ItemStack itemStack) {
		float amplitude = builder.normalRandomizingAmplitude;
		float rate = builder.normalRandomizingRate;
		RenderableState currentState = null;

		PlayerItemInstance<?> playerItemInstance = clientModContext.getPlayerItemInstanceRegistry().getItemInstance(player, itemStack);
				//.getMainHandItemInstance(player, PlayerWeaponInstance.class); // TODO: cannot be always main hand, need to which hand from context

		PlayerGrenadeInstance playerGrenadeInstance = null;
		if(playerItemInstance == null || !(playerItemInstance instanceof PlayerGrenadeInstance)
		        || playerItemInstance.getItem() != itemStack.getItem()) {
		    logger.error("Invalid or mismatching item. Player item instance: {}. Item stack: {}", playerItemInstance, itemStack);
		} else {
		    playerGrenadeInstance = (PlayerGrenadeInstance) playerItemInstance;
		}

		if(playerGrenadeInstance != null) {
			AsyncGrenadeState asyncWeaponState = getNextNonExpiredState(playerGrenadeInstance);

			switch(asyncWeaponState.getState()) {

			case TAKING_SAFETY_PING_OFF:
                currentState = RenderableState.TAKING_SAFETY_PIN_OFF;
                break;

			case SAFETY_PIN_OFF:
			    currentState = RenderableState.SAFETY_PIN_OFF;
			    break;

			case THROWING:
                currentState = RenderableState.THROWING;
                break;

			case THROWN:
                currentState = RenderableState.THROWN;
                break;

			default:
				if(player.isSprinting() && builder.firstPersonPositioningRunning != null) {
					currentState = RenderableState.RUNNING;
				}
			}

			logger.trace("Rendering state {} created from {}", currentState, asyncWeaponState.getState());
		}

		if(currentState == null) {
			currentState = RenderableState.NORMAL;
		}

		// TODO: what if there are multiple items of the same type? They all share the same state manager.
		MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager = firstPersonStateManagers.get(player);
		if(stateManager == null) {
			stateManager = new MultipartRenderStateManager<>(currentState, weaponTransitionProvider, Part.MAIN_ITEM);
			firstPersonStateManagers.put(player, stateManager);
		} else {
			stateManager.setState(currentState, true, currentState == RenderableState.THROWING);
		}


		return new StateDescriptor(playerGrenadeInstance, stateManager, rate, amplitude);
	}

	private AsyncGrenadeState getNextNonExpiredState(PlayerGrenadeInstance playerWeaponState) {
	    AsyncGrenadeState asyncWeaponState = null;
		while((asyncWeaponState = playerWeaponState.nextHistoryState()) != null) {
			if(System.currentTimeMillis() > asyncWeaponState.getTimestamp() + asyncWeaponState.getDuration()) {
			    continue;
			} else {
			    break;
			}
		}

		return asyncWeaponState;
	}

	private Consumer<RenderContext<RenderableState>> createWeaponPartPositionFunction(Transition<RenderContext<RenderableState>> t) {
		if(t == null) {
			return context -> {};
		}
		Consumer<RenderContext<RenderableState>> weaponPositionFunction = t.getItemPositioning();
		if((Consumer<?>)weaponPositionFunction == Transition.anchoredPosition()) {
		    return MultipartTransition.anchoredPosition();
		} else if(weaponPositionFunction != null) {
			return context -> weaponPositionFunction.accept(context);
		}

		return context -> {};

	}

	private Consumer<RenderContext<RenderableState>> createWeaponPartPositionFunction(Consumer<RenderContext<RenderableState>> weaponPositionFunction) {

	    if((Consumer<?>)weaponPositionFunction == Transition.anchoredPosition()) {
            return MultipartTransition.anchoredPosition();
        } else if(weaponPositionFunction != null) {
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
			MultipartTransition<Part, RenderContext<RenderableState>> t = new MultipartTransition<Part, RenderContext<RenderableState>>(
			        p.getDuration(), pause)
					.withPartPositionFunction(Part.MAIN_ITEM, p.getAttachedTo(), createWeaponPartPositionFunction(p))
					.withPartPositionFunction(Part.LEFT_HAND, l.getAttachedTo(), createWeaponPartPositionFunction(l))
					.withPartPositionFunction(Part.RIGHT_HAND, r.getAttachedTo(), createWeaponPartPositionFunction(r));

			for(Entry<Part, List<Transition<RenderContext<RenderableState>>>> e: custom.entrySet()){
				List<Transition<RenderContext<RenderableState>>> partTransitions = e.getValue();
				Transition<RenderContext<RenderableState>> partTransition = null;
				if(partTransitions != null && partTransitions.size() > i) {
					partTransition = partTransitions.get(i);
				} else {
					logger.warn("Transition not defined for part {}", custom);
				}
				t.withPartPositionFunction(e.getKey(), partTransition.getAttachedTo(), createWeaponPartPositionFunction(partTransition));
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
			LinkedHashMap<Part, SimplePositioning> custom, ///LinkedHashMap<Part, Consumer<RenderContext<RenderableState>>> custom,
			int duration) {

	    long pause = 0;
        if(DebugPositioner.isDebugModeEnabled()) {
            TransitionConfiguration transitionConfiguration = DebugPositioner.getTransitionConfiguration(0, false);
            if(transitionConfiguration != null) {
                pause = transitionConfiguration.getPause();
            }
        }
		MultipartTransition<Part, RenderContext<RenderableState>> mt = new MultipartTransition<Part, RenderContext<RenderableState>>(duration, pause)
				.withPartPositionFunction(Part.MAIN_ITEM, null, createWeaponPartPositionFunction(w))
				.withPartPositionFunction(Part.LEFT_HAND, null, createWeaponPartPositionFunction(lh))
				.withPartPositionFunction(Part.RIGHT_HAND, null, createWeaponPartPositionFunction(rh));
		custom.forEach((part, position) -> {
			mt.withPartPositionFunction(part, position.attachedTo, createWeaponPartPositionFunction(position.positioning));
		});
		return Collections.singletonList(mt);
	}

	private class WeaponPositionProvider implements MultipartTransitionProvider<RenderableState, Part, RenderContext<RenderableState>> {

		@Override
		public List<MultipartTransition<Part, RenderContext<RenderableState>>> getPositioning(RenderableState state) {
			switch(state) {
			case TAKING_SAFETY_PIN_OFF:
				return getComplexTransition(builder.firstPersonPositioningTakingSafetyPinOff,
						builder.firstPersonLeftHandPositioningTakingSafetyPinOff,
						builder.firstPersonRightHandPositioningTakingSafetyPinOff,
						builder.firstPersonCustomPositioningTakingSafetyPinOff
						);

			case SAFETY_PIN_OFF:
                return getSimpleTransition(builder.firstPersonPositioningSafetyPinOff,
                        builder.firstPersonLeftHandPositioningSafetyPinOff,
                        builder.firstPersonRightHandPositioningSafetyPinOff,
                        builder.firstPersonCustomPositioningSafetyPinOff,
                        builder.animationDuration);

			case THROWING:
                return getComplexTransition(builder.firstPersonPositioningThrowing,
                        builder.firstPersonLeftHandPositioningThrowing,
                        builder.firstPersonRightHandPositioningThrowing,
                        builder.firstPersonCustomPositioningThrowing
                        );

			case THROWN:
                return getSimpleTransition(builder.firstPersonPositioningThrown,
                        builder.firstPersonLeftHandPositioningThrown,
                        builder.firstPersonRightHandPositioningThrown,
                        builder.firstPersonCustomPositioningThrown,
                        builder.animationDuration);

			case NORMAL: case RUNNING: // TODO: configure running position
				return getSimpleTransition(builder.firstPersonPositioning,
						builder.firstPersonLeftHandPositioning,
						builder.firstPersonRightHandPositioning,
						builder.firstPersonCustomPositioning,
						builder.animationDuration);
			default:
				break;
			}
			return null;
		}
	}

	@Override
	public void renderItem(ItemStack weaponItemStack, RenderContext<RenderableState> renderContext,
			Positioner<Part, RenderContext<RenderableState>> positioner) {

		if(builder.getTextureName() != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId()
					+ ":textures/models/" + builder.getTextureName()));
		} else {
			String textureName = null;

			if(textureName == null) {
				ItemGrenade weapon = ((ItemGrenade) weaponItemStack.getItem());
				textureName = weapon.getTextureName();
			}

			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId()
					+ ":textures/models/" + textureName));
		}

		//limbSwing, float flimbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale
		builder.getModel().render(null,
				renderContext.getLimbSwing(),
				renderContext.getFlimbSwingAmount(),
				renderContext.getAgeInTicks(),
				renderContext.getNetHeadYaw(),
				renderContext.getHeadPitch(),
				renderContext.getScale());

		PlayerItemInstance<?> itemInstance = renderContext.getPlayerItemInstance();
        if(!(itemInstance instanceof PlayerGrenadeInstance)) {
            //logger.error("Instance is not a grenade!");
            return;
        }

        PlayerGrenadeInstance grenadeInstance = (PlayerGrenadeInstance) itemInstance;

        List<CompatibleAttachment<? extends AttachmentContainer>> attachments = grenadeInstance.getActiveAttachments(renderContext, true);
        renderAttachments(positioner, renderContext, attachments);
	}

	@Override
	public void renderAttachments(Positioner<Part, RenderContext<RenderableState>> positioner, RenderContext<RenderableState> renderContext,List<CompatibleAttachment<? extends AttachmentContainer>> attachments) {
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null && !(compatibleAttachment.getAttachment() instanceof ItemSkin)) {
				renderCompatibleAttachment(compatibleAttachment, positioner, renderContext);
			}
		}
	}

	private void renderCompatibleAttachment(CompatibleAttachment<?> compatibleAttachment,
			Positioner<Part, RenderContext<RenderableState>> positioner, RenderContext<RenderableState> renderContext) {


	    GL11.glPushMatrix();
	    GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);

	    if(compatibleAttachment.getPositioning() != null) {
	        //compatibleAttachment.getPositioning().accept(renderContext.getPlayer(), renderContext.getWeapon());
	    }

	    ItemAttachment<?> itemAttachment = compatibleAttachment.getAttachment();


	    if(positioner != null) {
	        if(itemAttachment instanceof Part) {
	            positioner.position((Part) itemAttachment, renderContext);
	            if(DebugPositioner.isDebugModeEnabled()) {
	                DebugPositioner.position((Part)itemAttachment, renderContext);
	            }
	        } else if(itemAttachment.getRenderablePart() != null) {
	            positioner.position(itemAttachment.getRenderablePart(), renderContext);
	            if(DebugPositioner.isDebugModeEnabled()) {
	                DebugPositioner.position(itemAttachment.getRenderablePart(), renderContext);
	            }
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

}
