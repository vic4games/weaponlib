package com.vicmatskiv.weaponlib.compatibility;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.melee.RenderableState;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.melee.MeleeRenderer.Builder;
import com.vicmatskiv.weaponlib.melee.PlayerMeleeInstance;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public abstract class CompatibleMeleeRenderer implements IItemRenderer {
	
	protected static class StateDescriptor {
		protected MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager;
		protected float rate;
		protected float amplitude = 0.04f;
		private PlayerMeleeInstance instance;
		public StateDescriptor(PlayerMeleeInstance instance, MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager,
				float rate, float amplitude) {
			this.instance = instance;
			this.stateManager = stateManager;
			this.rate = rate;
			this.amplitude = amplitude;
		}
	}
	
	private Builder builder;
	
	protected CompatibleMeleeRenderer(Builder builder){
		this.builder = builder;
	}
	
	protected abstract ClientModContext getClientModContext();
	
	protected abstract StateDescriptor getStateDescriptor(EntityPlayer player, ItemStack itemStack);
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderItem(ItemRenderType type, ItemStack weaponItemStack, Object... data)
	{
		
		GL11.glPushMatrix();
		
		GL11.glScaled(-1F, -1F, 1F);
		EntityPlayer player;
		
		if(data.length > 1 && data[1] instanceof EntityPlayer) {
		    player = (EntityPlayer) data[1];
		} else {
		    player = Minecraft.getMinecraft().thePlayer;
		}
		
		RenderContext<RenderableState> renderContext = new RenderContext<>(getClientModContext(), player, weaponItemStack);
		
		//float limbSwing, float flimbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale
		//0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		renderContext.setAgeInTicks(-0.4f);
		renderContext.setScale(0.08f);
		renderContext.setCompatibleTransformType(CompatibleTransformType.fromItemRenderType(type));
		
		Positioner<Part, RenderContext<RenderableState>> positioner = null;
		switch (type)
		{
		case ENTITY:
			builder.getEntityPositioning().accept(weaponItemStack);
			break;
			
		case INVENTORY:
			builder.getInventoryPositioning().accept(weaponItemStack);
			break;
			
		case EQUIPPED:
			builder.getThirdPersonPositioning().accept(renderContext);
			break;
			
		case EQUIPPED_FIRST_PERSON:
			
			StateDescriptor stateDescriptor = getStateDescriptor(player, weaponItemStack);
			
			renderContext.setPlayerItemInstance(stateDescriptor.instance);
						
			MultipartPositioning<Part, RenderContext<RenderableState>> multipartPositioning = stateDescriptor.stateManager.nextPositioning();
			
			renderContext.setTransitionProgress(multipartPositioning.getProgress());
			
			renderContext.setFromState(multipartPositioning.getFromState(RenderableState.class));
			
			renderContext.setToState(multipartPositioning.getToState(RenderableState.class));
			
			positioner = multipartPositioning.getPositioner();
		
			positioner.position(Part.MAIN_ITEM, renderContext);
			
//			BiConsumer<Part, RenderContext<RenderableState>> partDebugPositioning = getPartDebugPositioning();
//            if(partDebugPositioning != null) {
//                partDebugPositioning.accept(Part.MAIN_ITEM, renderContext);
//                if(DebugPositioner.isDebugModeEnabled()) {
//                    DebugPositioner.position();
//                }
//            }

			renderLeftArm(player, renderContext, positioner);
			renderRightArm(player, renderContext, positioner);
	        
			break;
		default:
		}
		
		renderItem(weaponItemStack, renderContext, positioner);
		
		GL11.glPopMatrix();
	}
	
	protected abstract BiConsumer<Part, RenderContext<RenderableState>> getPartDebugPositioning();

	protected abstract void renderItem(ItemStack weaponItemStack, RenderContext<RenderableState> renderContext,
			Positioner<Part, RenderContext<RenderableState>> positioner);
		

	private void renderRightArm(EntityPlayer player, RenderContext<RenderableState> renderContext,
			Positioner<Part, RenderContext<RenderableState>> positioner) {
		RenderPlayer render = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
		Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
		GL11.glPushMatrix();
		GL11.glScaled(1F, 1F, 1F);
		
		GL11.glScaled(1F, 1F, 1F);
		GL11.glTranslatef(-0.25f, 0f, 0.2f);
		
		GL11.glRotatef(5F, 1f, 0f, 0f);
		GL11.glRotatef(25F, 0f, 1f, 0f);
		GL11.glRotatef(0F, 0f, 0f, 1f);
		
		positioner.position(Part.RIGHT_HAND, renderContext);
		
		BiConsumer<Part, RenderContext<RenderableState>> partDebugPositioning = getPartDebugPositioning();
		if(partDebugPositioning != null) {
		    partDebugPositioning.accept(Part.RIGHT_HAND, renderContext);
		}
		GL11.glColor3f(1F, 1F, 1F);
		render.modelBipedMain.onGround = 0.0F;
		render.modelBipedMain.setRotationAngles(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		render.modelBipedMain.bipedRightArm.render(0.0625F);
		GL11.glPopMatrix();
	}

	private void renderLeftArm(EntityPlayer player, RenderContext<RenderableState> renderContext,
			Positioner<Part, RenderContext<RenderableState>> positioner) {
		RenderPlayer render = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
		Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
		
		GL11.glPushMatrix();
		
		GL11.glScaled(1F, 1F, 1F);
		
		GL11.glTranslatef(0f, -1f, 0f);
		
		GL11.glRotatef(-10F, 1f, 0f, 0f);
		GL11.glRotatef(0F, 0f, 1f, 0f);
		GL11.glRotatef(10F, 0f, 0f, 1f);
		
		positioner.position(Part.LEFT_HAND, renderContext);
		BiConsumer<Part, RenderContext<RenderableState>> partDebugPositioning = getPartDebugPositioning();
        if(partDebugPositioning != null) {
            partDebugPositioning.accept(Part.LEFT_HAND, renderContext);
        }
		GL11.glColor3f(1F, 1F, 1F);
		render.modelBipedMain.onGround = 0.0F;
		render.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		render.modelBipedMain.bipedLeftArm.render(0.0625F);
		
		GL11.glPopMatrix();
	}
}
