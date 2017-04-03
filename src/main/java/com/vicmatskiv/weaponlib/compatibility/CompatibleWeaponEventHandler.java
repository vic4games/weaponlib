package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.ModContext;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.event.TextureStitchEvent;

public abstract class CompatibleWeaponEventHandler {

	@SubscribeEvent
	public final void onGuiOpenEvent(GuiOpenEvent event) {
		onCompatibleGuiOpenEvent(event);
	}

	protected abstract void onCompatibleGuiOpenEvent(GuiOpenEvent event);

	@SubscribeEvent
	public final void zoom(FOVUpdateEvent event) {
		compatibleZoom(event);
	}

	protected abstract void compatibleZoom(FOVUpdateEvent event);

	@SubscribeEvent
	public final void onMouse(MouseEvent event) {
		onCompatibleMouse(event);
	}

	protected abstract void onCompatibleMouse(MouseEvent event);

	@SubscribeEvent
	public final void handleRenderLivingEvent(RenderLivingEvent.Pre event) {
		onCompatibleHandleRenderLivingEvent(event);
	}

	protected abstract void onCompatibleHandleRenderLivingEvent(Pre event);

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
	    event.map.registerIcon(getModContext().getNamedResource(CompatibleParticle.CompatibleParticleBreaking.TEXTURE_BLOOD_PARTICLES).toString());
	}

    protected abstract ModContext getModContext();
}