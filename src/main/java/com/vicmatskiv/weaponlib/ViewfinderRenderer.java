package com.vicmatskiv.weaponlib;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ViewfinderRenderer implements CustomRenderer {
	
	private ViewfinderModel model = new ViewfinderModel();
	private BiConsumer<EntityPlayer, ItemStack> positioning;
	private ModContext modContext;

	public ViewfinderRenderer(ModContext modContext, BiConsumer<EntityPlayer, ItemStack> positioning) {
		this.modContext = modContext;
		this.positioning = positioning;
	}

	@Override
	public void render(RenderContext renderContext) {
		
		if(renderContext.getCompatibleTransformType() != CompatibleTransformType.FIRST_PERSON_RIGHT_HAND
				&& renderContext.getCompatibleTransformType() != CompatibleTransformType.FIRST_PERSON_LEFT_HAND) {
			return;
		}
		
		float brightness = 0f;
		PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
		boolean aimed = instance != null && instance.isAimed();
		float progress = Math.min(1f, renderContext.getTransitionProgress());
		if(progress > 0f && aimed) {
			brightness = progress;
		} else if(isAimingState(renderContext.getFromState()) && progress > 0f && !aimed) {
			brightness = Math.max(1 - progress, 0f);
		} else if(isAimingState(renderContext.getFromState()) && isAimingState(renderContext.getToState())) {
			brightness = 1f;
		}
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);

		positioning.accept(renderContext.getPlayer(), renderContext.getWeapon());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderContext.getClientModContext().getFramebuffer().framebufferTexture);
		Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glColor4f(brightness, brightness, brightness, 1f);
		model.render(renderContext.getPlayer(), 
				renderContext.getLimbSwing(), 
				renderContext.getFlimbSwingAmount(), 
				renderContext.getAgeInTicks(), 
				renderContext.getNetHeadYaw(), 
				renderContext.getHeadPitch(), 
				renderContext.getScale());
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	private static boolean isAimingState(RenderableState renderableState) {
		return renderableState == RenderableState.ZOOMING
				|| renderableState ==RenderableState.ZOOMING_RECOILED
				|| renderableState ==RenderableState.ZOOMING_SHOOTING;
	}
}
