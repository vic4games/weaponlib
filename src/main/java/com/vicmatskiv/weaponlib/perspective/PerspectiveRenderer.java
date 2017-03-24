package com.vicmatskiv.weaponlib.perspective;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.CustomRenderer;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.ViewfinderModel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PerspectiveRenderer implements CustomRenderer<RenderableState> {
	
	private ViewfinderModel model = new ViewfinderModel();
	private BiConsumer<EntityPlayer, ItemStack> positioning;

	public PerspectiveRenderer(BiConsumer<EntityPlayer, ItemStack> positioning) {
		this.positioning = positioning;
	}

	@Override
	public void render(RenderContext<RenderableState> renderContext) {
		
		if(renderContext.getCompatibleTransformType() != CompatibleTransformType.FIRST_PERSON_RIGHT_HAND
				&& renderContext.getCompatibleTransformType() != CompatibleTransformType.FIRST_PERSON_LEFT_HAND) {
			return;
		}
		
		if(renderContext.getModContext() == null) {
		    return;
		}
		
//		Framebuffer framebuffer = ((ClientModContext) renderContext.getModContext()).getFramebuffer();
//		if(framebuffer == null) {
//		    return;
//		}
		
		ClientModContext clientModContext = (ClientModContext) renderContext.getModContext();
		@SuppressWarnings("unchecked")
        Perspective<RenderableState> view = (Perspective<RenderableState>) clientModContext.getViewManager().getPerspective(renderContext.getPlayerItemInstance(), false);
		if(view == null) {
		    return;
		}
		
		float brightness = view.getBrightness(renderContext);
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);

		positioning.accept(renderContext.getPlayer(), renderContext.getWeapon());
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, view.getTexture(renderContext));
		compatibility.disableLightMap();
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
		
		compatibility.enableLightMap();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
