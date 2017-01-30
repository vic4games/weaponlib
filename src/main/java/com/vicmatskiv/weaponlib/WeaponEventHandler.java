package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

public class WeaponEventHandler extends CompatibleWeaponEventHandler {
	
	private SafeGlobals safeGlobals;

	public WeaponEventHandler(SafeGlobals safeGlobals) {
		this.safeGlobals = safeGlobals;
	}
	
	@Override
	public void onCompatibleGuiOpenEvent(GuiOpenEvent event) {
		safeGlobals.guiOpen.set(compatibility.getGui(event) != null);
	}
	
	@Override
	public void compatibleZoom(FOVUpdateEvent event) {

		ItemStack stack = compatibility.getHeldItemMainHand(compatibility.getEntity(event));
		if (stack != null) {
			if (stack.getItem() instanceof Weapon) {
				if (compatibility.getTagCompound(stack) != null) {
					compatibility.setNewFov(event, Tags.getZoom(stack));
				}
			}
		}
	}
	
	@Override
	public void onCompatibleMouse(MouseEvent event) {
		if(compatibility.getButton(event) == 0) {
			ItemStack heldItem = compatibility.getHeldItemMainHand(Minecraft.getMinecraft().thePlayer);
			if(heldItem != null && heldItem.getItem() instanceof Weapon) {
				event.setCanceled(true);
			}
		} else if(compatibility.getButton(event) == 1) {
			ItemStack heldItem = compatibility.getHeldItemMainHand(Minecraft.getMinecraft().thePlayer);			if(heldItem != null && heldItem.getItem() instanceof Weapon 
					&& Weapon.isEjectedSpentRound(Minecraft.getMinecraft().thePlayer, heldItem)) {
				event.setCanceled(true);
			}
		}
	}

	@Override
	public void onCompatibleHandleRenderLivingEvent(RenderLivingEvent.Pre event) {

		if ((event.isCanceled()) || (!(compatibility.getEntity(event) instanceof EntityPlayer)))
			return;

		ItemStack itemStack = compatibility.getHeldItemMainHand(compatibility.getEntity(event));

		if (itemStack != null && itemStack.getItem() instanceof Weapon) {
			RenderPlayer rp = compatibility.getRenderer(event);

			if (itemStack != null && compatibility.getTagCompound(itemStack) != null) {
				compatibility.setAimed(rp, Weapon.isAimed(itemStack));
			}
		}
	}

}