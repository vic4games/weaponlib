package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponEventHandler;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

public class WeaponEventHandler extends CompatibleWeaponEventHandler {
	
	private SafeGlobals safeGlobals;
	private ModContext modContext;

	public WeaponEventHandler(ModContext modContext, SafeGlobals safeGlobals) {
		this.modContext = modContext;
		this.safeGlobals = safeGlobals;
	}
	
	@Override
	public void onCompatibleGuiOpenEvent(GuiOpenEvent event) {
		safeGlobals.guiOpen.set(compatibility.getGui(event) != null);
	}
	
	@Override
	public void compatibleZoom(FOVUpdateEvent event) {
		/*
		 * TODO: if optical zoom is on then
		 * 			if rendering phase is "render viewfinder" then 
		 * 				setNewFov(getZoom());
		 *          else if rendering phase is normal then
		 *              setNewFov(1);
		 *       else if optical zoom is off
		 *       	setNewFov(getZoom())
		 */

		ItemStack stack = compatibility.getHeldItemMainHand(compatibility.getEntity(event));
		if (stack != null) {
			if (stack.getItem() instanceof Weapon) {
				if (compatibility.getTagCompound(stack) != null) {
					final float fov;
					if(Tags.isAttachmentOnlyZoom(stack)) {
						if(safeGlobals.renderingPhase.get() == RenderingPhase.RENDER_VIEWFINDER) {
							fov = Tags.getZoom(stack);
						} else {
							fov = 1f;
						}
					} else {
						fov = Tags.getZoom(stack);
					}
					
					compatibility.setNewFov(event, fov); //Tags.getZoom(stack));
				}
			}
		}
	}
	
	@Override
	public void onCompatibleMouse(MouseEvent event) {
		if(compatibility.getButton(event) == 0) {
			
			PlayerWeaponInstance mainHandHeldWeaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(
					compatibility.clientPlayer(), PlayerWeaponInstance.class);
			if(mainHandHeldWeaponInstance != null) {
				event.setCanceled(true);
			}
			
//			ItemStack heldItem = compatibility.getHeldItemMainHand(compatibility.clientPlayer());
//			if(heldItem != null && heldItem.getItem() instanceof Weapon) {
//				event.setCanceled(true);
//			}
		} else if(compatibility.getButton(event) == 1) {
//			ItemStack heldItem = compatibility.getHeldItemMainHand(compatibility.clientPlayer());			
//			if(heldItem != null && heldItem.getItem() instanceof Weapon 
//					&& Weapon.isEjectedSpentRound(compatibility.clientPlayer(), heldItem)) {
//				event.setCanceled(true);
//			}
			
			PlayerWeaponInstance mainHandHeldWeaponInstance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(
					compatibility.clientPlayer(), PlayerWeaponInstance.class);
			
			if(mainHandHeldWeaponInstance != null 
					&& mainHandHeldWeaponInstance.getState() == WeaponState.EJECT_REQUIRED) {
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