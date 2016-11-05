package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.Weapon.WeaponInstanceStorage;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.MultipartTransition;
import com.vicmatskiv.weaponlib.animation.MultipartTransitionProvider;
import com.vicmatskiv.weaponlib.animation.Randomizer;
import com.vicmatskiv.weaponlib.animation.Transition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
//import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;


public class WeaponRenderer implements IItemRenderer {
	
	private static final int DEFAULT_RANDOMIZING_INTERVAL = 3000;
	private static final int DEFAULT_RANDOMIZING_FIRING_INTERVAL = 50;
	private static final float DEFAULT_ZOOM_AMPLITUDE = 0.01f;
	private static final float DEFAULT_RANDOMIZING_AMPLITUDE = 0.06f;
	
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
		
		private Random random = new Random();
		
		private List<Transition> firstPersonPositioningReloading;
		private List<Transition> firstPersonLeftHandPositioningReloading;
		private List<Transition> firstPersonRightHandPositioningReloading;
		private String modId;
		
		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withModel(ModelBase model) {
			this.model = model;
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
					if(itemStack.stackTagCompound != null && itemStack.stackTagCompound.getFloat(Weapon.ZOOM_TAG) != 1.0f) {
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
			
			return new WeaponRenderer(this);
		}
	}
	
	private Builder builder;
	
	private Map<EntityPlayer, MultipartRenderStateManager<RenderableState, Part, RenderContext>> firstPersonStateManagers;
		
	private MultipartTransitionProvider<RenderableState, Part, RenderContext> weaponTransitionProvider;
	
	private Randomizer randomizer = new Randomizer();
	
	private WeaponRenderer (Builder builder)
	{
		this.builder = builder;
		this.firstPersonStateManagers = new HashMap<>();
		this.weaponTransitionProvider = new WeaponPositionProvider();
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}
	
	private static class StateManagerTuple {
		MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager;
		int randomizeInterval;
		float amplitude = 0.04f;
		public StateManagerTuple(MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager,
				int randomizeInterval, float amplitude) {
			this.stateManager = stateManager;
			this.randomizeInterval = randomizeInterval;
			this.amplitude = amplitude;
		}
		
	}
	
	private StateManagerTuple getStateManager(EntityPlayer player, ItemStack itemStack) {
		int randomizeInterval = DEFAULT_RANDOMIZING_INTERVAL;
		float amplitude = DEFAULT_RANDOMIZING_AMPLITUDE;
		RenderableState currentState = null;
		Weapon weapon = (Weapon) itemStack.getItem();
		if(weapon.getState(itemStack) == Weapon.STATE_MODIFYING && builder.firstPersonPositioningModifying != null) {
			currentState = RenderableState.MODIFYING;
		} else if(player.isSprinting() && builder.firstPersonPositioningRunning != null) {
			currentState = RenderableState.RUNNING;
		} else if(Weapon.isReloadingConfirmed(player, itemStack)) {
			currentState = RenderableState.RELOADING;
		} else if(Weapon.isZoomed(itemStack)) {
			WeaponInstanceStorage storage = weapon.getWeaponInstanceStorage(player);

			if(storage != null) {
				currentState = storage.getNextDisposableRenderableState();
				if(currentState == RenderableState.SHOOTING) {
					currentState = RenderableState.ZOOMING;
					randomizeInterval = DEFAULT_RANDOMIZING_FIRING_INTERVAL;
				}
				
				amplitude = DEFAULT_ZOOM_AMPLITUDE;
			}
			currentState = RenderableState.ZOOMING;
		} else if(weapon.getState(itemStack) == Weapon.STATE_READY) {
			currentState = RenderableState.NORMAL;
		} else {
			WeaponInstanceStorage storage = weapon.getWeaponInstanceStorage(player);

			if(storage != null) {
				currentState = storage.getNextDisposableRenderableState();
				if(currentState == RenderableState.SHOOTING) {
					currentState = RenderableState.NORMAL;
					randomizeInterval = DEFAULT_RANDOMIZING_FIRING_INTERVAL;
				}

			}
			if(currentState == null) {
				currentState = RenderableState.NORMAL;
			}
		}
		
		
		MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager = firstPersonStateManagers.get(player);
		if(stateManager == null) {
			stateManager = new MultipartRenderStateManager<>(currentState, weaponTransitionProvider, Part.WEAPON);
			firstPersonStateManagers.put(player, stateManager);
		} else {
			stateManager.setState(currentState, true, currentState == RenderableState.SHOOTING);
		}
		
		return new StateManagerTuple(stateManager, randomizeInterval, amplitude);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
	    
		
		GL11.glPushMatrix();
		
		GL11.glScaled(-1F, -1F, 1F);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		RenderContext renderContext = new RenderContext(player, item);
		switch (type)
		{
		case ENTITY:
			builder.entityPositioning.accept(item);
			break;
		case INVENTORY:
			builder.inventoryPositioning.accept(item);
			break;
		case EQUIPPED:
			
			builder.thirdPersonPositioning.accept(player, item);
			break;
		case EQUIPPED_FIRST_PERSON:

			
			StateManagerTuple tuple = getStateManager(player, item);
			MultipartPositioning<Part, RenderContext> multipartPositioning = tuple.stateManager.getPositioning();
			
			Positioner<Part, RenderContext> positioner = multipartPositioning.getPositioner();
			
			randomizer.setIntervalAndAmplitude(tuple.randomizeInterval, tuple.amplitude);
			
			randomizer.update();
			
//			if(tuple.randomized) {
//				builder.firstPersonPositioningShooting.accept(player, item);
//			}
			
			positioner.position(Part.WEAPON, renderContext);
			
			RenderPlayer render = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
			Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
			
			GL11.glPushMatrix();
			positioner.position(Part.LEFT_HAND, renderContext);
			render.renderFirstPersonArm(player);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			positioner.position(Part.RIGHT_HAND, renderContext);
			render.renderFirstPersonArm(player);
			GL11.glPopMatrix();
	        
			break;
		default:
		}
		
		if(builder.textureName != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.modId 
					+ ":textures/models/" + builder.textureName));
		} else {
			Weapon weapon = ((Weapon) item.getItem());
			String textureName = weapon.getActiveTextureName(item);
			if(textureName != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.modId 
						+ ":textures/models/" + textureName));
			}
		}
		
		builder.model.render(null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		if(builder.model instanceof ModelWithAttachments) {
			List<CompatibleAttachment<Weapon>> attachments = ((Weapon) item.getItem()).getActiveAttachments(item);
			((ModelWithAttachments)builder.model).renderAttachments(builder.modId, item, 
					type, attachments , null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		}
		
		GL11.glPopMatrix();
	   
	}
	
	private enum Part {
		WEAPON, RIGHT_HAND, LEFT_HAND
	};
	
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
	
	private List<MultipartTransition<Part, RenderContext>> getComplexTransition(List<Transition> wt, List<Transition> lht, List<Transition> rht) {
		List<MultipartTransition<Part, RenderContext>> result = new ArrayList<>();
		for(int i = 0; i < wt.size(); i++) {
			Transition p = wt.get(i);
			Transition l = lht.get(i);
			Transition r = rht.get(i);
			
			MultipartTransition<Part, RenderContext> t = new MultipartTransition<Part, RenderContext>(p.getDuration(), p.getPause())
					.withPartPositionFunction(Part.WEAPON, createWeaponPartPositionFunction(p.getPositioning()))
					.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(l.getPositioning()))
					.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(r.getPositioning()));
			
			result.add(t);
		}
		return result;
	}
	
	private List<MultipartTransition<Part, RenderContext>> getSimpleTransition(
			BiConsumer<EntityPlayer, ItemStack> w,
			BiConsumer<EntityPlayer, ItemStack> lh,
			BiConsumer<EntityPlayer, ItemStack> rh,
			int duration) {
		MultipartTransition<Part, RenderContext> mt = new MultipartTransition<Part, RenderContext>(duration, 0)
				.withPartPositionFunction(Part.WEAPON, createWeaponPartPositionFunction(w))
				.withPartPositionFunction(Part.LEFT_HAND, createWeaponPartPositionFunction(lh))
				.withPartPositionFunction(Part.RIGHT_HAND, createWeaponPartPositionFunction(rh));
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
						DEFAULT_ANIMATION_DURATION);
			case RUNNING:
				return getSimpleTransition(builder.firstPersonPositioningRunning,
						builder.firstPersonLeftHandPositioningRunning,
						builder.firstPersonRightHandPositioningRunning,
						DEFAULT_ANIMATION_DURATION);
			case RELOADING:
				return getComplexTransition(builder.firstPersonPositioningReloading, 
						builder.firstPersonLeftHandPositioningReloading,
						builder.firstPersonRightHandPositioningReloading);
			case RECOILED:
				return getSimpleTransition(builder.firstPersonPositioningRecoiled, 
						builder.firstPersonLeftHandPositioningRecoiled,
						builder.firstPersonRightHandPositioningRecoiled,
						DEFAULT_RECOIL_ANIMATION_DURATION);
			case SHOOTING:
				return getSimpleTransition(builder.firstPersonPositioningShooting, 
						builder.firstPersonLeftHandPositioningShooting,
						builder.firstPersonRightHandPositioningShooting,
						DEFAULT_RECOIL_ANIMATION_DURATION); // TODO: is it really recoil duration
			case NORMAL:
				return getSimpleTransition(builder.firstPersonPositioning, 
						builder.firstPersonLeftHandPositioning,
						builder.firstPersonRightHandPositioning,
						DEFAULT_ANIMATION_DURATION);
			case ZOOMING:
				return getSimpleTransition(builder.firstPersonPositioningZooming, 
						builder.firstPersonLeftHandPositioningZooming,
						builder.firstPersonRightHandPositioningZooming,
						DEFAULT_ANIMATION_DURATION);
			default:
				break;
			}
			return null;
		}

		@Override
		public List<Part> getParts() {
			return Arrays.asList(Part.values());
		}
	}

	
}
