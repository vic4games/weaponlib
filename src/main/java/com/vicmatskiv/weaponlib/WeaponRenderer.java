package com.vicmatskiv.weaponlib;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.Weapon.WeaponInstanceStorage;
import com.vicmatskiv.weaponlib.animation.RenderStateManager;
import com.vicmatskiv.weaponlib.animation.Transition;
import com.vicmatskiv.weaponlib.animation.TransitionProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;


public class WeaponRenderer implements IItemRenderer, TransitionProvider<RenderableState> {
	
	private static final int DEFAULT_ANIMATION_DURATION = 250;
	private static final int DEFAULT_RECOIL_ANIMATION_DURATION = 5;

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
		
		private List<Transition> firstPersonPositioningReloading;
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
		
		@SafeVarargs
		public final Builder withFirstPersonPositioningReloading(Transition ...transitions) {
			this.firstPersonPositioningReloading = Arrays.asList(transitions);
			return this;
		}
		
		public Builder withFirstPersonPositioningModifying(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioningModifying) {
			this.firstPersonPositioningModifying = firstPersonPositioningModifying;
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
			
			if(firstPersonPositioningReloading == null) {
				firstPersonPositioningReloading = Collections.singletonList(new Transition(firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
			}
			
			if(firstPersonPositioningRecoiled == null) {
				firstPersonPositioningRecoiled = firstPersonPositioning;
			}
			
			if(thirdPersonPositioning == null) {
				thirdPersonPositioning = (player, itemStack) -> {
					GL11.glTranslatef(-0.4F, 0.2F, 0.4F);
					GL11.glRotatef(-45F, 0f, 1f, 0f);
					GL11.glRotatef(70F, 1f, 0f, 0f);
				};
			}
			
			return new WeaponRenderer(this);
		}
	}
	
	private Builder builder;
	
	private Map<EntityPlayer, RenderStateManager<RenderableState>> firstPersonStateManagers;
	
	private WeaponRenderer (Builder builder)
	{
		this.builder = builder;
		this.firstPersonStateManagers = new HashMap<>();
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
	
	private RenderStateManager<RenderableState> getStateManager(EntityPlayer player, ItemStack itemStack) {
		RenderableState currentState = null;
		Weapon weapon = (Weapon) itemStack.getItem();
		if(weapon.getState(itemStack) == Weapon.STATE_MODIFYING && builder.firstPersonPositioningModifying != null) {
			currentState = RenderableState.MODIFYING;
		} else if(player.isSprinting() && builder.firstPersonPositioningRunning != null) {
			currentState = RenderableState.RUNNING;
		} else if(Weapon.isReloadingConfirmed(player, itemStack)) {
			currentState = RenderableState.RELOADING;
		} else if(Weapon.isZoomed(itemStack)) {
			currentState = RenderableState.ZOOMING;
		} else {
			WeaponInstanceStorage storage = weapon.getWeaponInstanceStorage(player);

			if(storage != null) {
				currentState = storage.getNextDisposableRenderableState();
			}
			if(currentState == null) {
				currentState = RenderableState.NORMAL;
			} else {
				//System.out.println("Rendering state " + currentState);
			}
			
//			if(hasRecoiled) {
//				currentState = RenderableState.RECOILED;
//				System.out.println("Rendering recoiled state...");
//			} else if(storage != null && storage.getState() == WeaponInstanceState.SHOOTING) {
//				System.out.println("Rendering shooting state...");
//				storage.resetRecoiled();
//				currentState = RenderableState.SHOOTING;
//			} else {
//				System.out.println("Rendering normal state...");
//				currentState = RenderableState.NORMAL;
//			}
		}
		
		RenderableState effectiveCurrentState = currentState;
//		if(currentState == RenderableState.SHOOTING || currentState == RenderableState.RECOILED) {
//			effectiveCurrentState = RenderableState.NORMAL;
//		} else {
//			effectiveCurrentState = currentState;
//		}
		
		RenderStateManager<RenderableState> stateManager = firstPersonStateManagers.get(player);
		if(stateManager == null) {
			stateManager = new RenderStateManager<>(effectiveCurrentState, this);
			firstPersonStateManagers.put(player, stateManager);
		} else {
			stateManager.setState(effectiveCurrentState, true);
		}
		return stateManager;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		
		GL11.glPushMatrix();
		
		GL11.glScaled(-1F, -1F, 1F);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
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
//			if(((Weapon) item.getItem()).getState(item) == Weapon.STATE_MODIFYING && builder.firstPersonPositioningModifying != null) {
//				builder.firstPersonPositioningModifying.accept(player, item);
//			} else if(player.isSprinting() && builder.firstPersonPositioningRunning != null) {
//				builder.firstPersonPositioningRunning.accept(player, item);
//			} else{
//				builder.firstPersonPositioning.accept(player, item);
//			}
			RenderStateManager<RenderableState> stateManager = getStateManager(player, item);
			stateManager.getPosition().accept(player, item);
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
	

	@Override
	public List<Transition> getPositioning(RenderableState state) {
		switch(state) {
		case MODIFYING:
			return Collections.singletonList(new Transition(builder.firstPersonPositioningModifying, DEFAULT_ANIMATION_DURATION));
		case RUNNING:
			return Collections.singletonList(new Transition(builder.firstPersonPositioningRunning, DEFAULT_ANIMATION_DURATION));
		case RELOADING:
			return builder.firstPersonPositioningReloading;
		case RECOILED:
			return Collections.singletonList(new Transition(builder.firstPersonPositioningRecoiled, DEFAULT_RECOIL_ANIMATION_DURATION));
		case SHOOTING:
			return Collections.singletonList(new Transition(builder.firstPersonPositioning, DEFAULT_RECOIL_ANIMATION_DURATION));
		case NORMAL:
			return Collections.singletonList(new Transition(builder.firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
		case ZOOMING:
			return Collections.singletonList(new Transition(
					builder.firstPersonPositioningZooming != null ? builder.firstPersonPositioningZooming : builder.firstPersonPositioning, DEFAULT_ANIMATION_DURATION));
		default:
			break;
		}
		return null;
	}
}
