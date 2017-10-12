package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityJoinWorldEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponEventHandler;
import com.vicmatskiv.weaponlib.grenade.PlayerGrenadeInstance;
import com.vicmatskiv.weaponlib.melee.PlayerMeleeInstance;

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

		//ItemStack stack = compatibility.getHeldItemMainHand(compatibility.getEntity(event));
		PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
		EntityPlayer clientPlayer = compatibility.clientPlayer();
		if (instance != null) {

		    final float fov;
		    if(instance.isAttachmentZoomEnabled()) {
		        if(safeGlobals.renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE) {
		            fov = instance.getZoom();
		        } else {
		            fov = compatibility.isFlying(clientPlayer) ? 1.1f : 1.0f;
		        }
		    } else {
		        fov = compatibility.isFlying(compatibility.clientPlayer()) ? 1.1f : 1.0f; //instance.isAimed() ? instance.getZoom() : 1f;
		    }

		    compatibility.setNewFov(event, fov); //Tags.getZoom(stack));
		} else {
		    SpreadableExposure spreadableExposure = CompatibleExposureCapability.getExposure(compatibility.clientPlayer(), SpreadableExposure.class);
            if(spreadableExposure != null && spreadableExposure.getTotalDose() > 0f) {
                float fov = compatibility.isFlying(compatibility.clientPlayer()) ? 1.1f : 1.0f; 
                compatibility.setNewFov(event, fov);
            }
		}
		
	}

	@Override
	public void onCompatibleMouse(MouseEvent event) {
		if(compatibility.getButton(event) == 0 || compatibility.getButton(event) == 1) {
			// If the current player holds the weapon in their main hand, cancel default minecraft mouse processing
		    PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(compatibility.clientPlayer());
		    //PlayerWeaponInstance mainHandHeldWeaponInstance = modContext.getMainHeldWeapon();
			if(instance instanceof PlayerWeaponInstance || instance instanceof PlayerMeleeInstance
			        || instance instanceof PlayerGrenadeInstance) { // TODO: introduce common action handler interface and check instanceof ActionHandler instead
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

			if (itemStack != null ) {
				PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry()
						.getItemInstance((EntityPlayer)compatibility.getEntity(event), itemStack);
				if(instance instanceof PlayerWeaponInstance) {
					PlayerWeaponInstance weaponInstance = (PlayerWeaponInstance) instance;
						compatibility.setAimed(rp, weaponInstance.isAimed()
							|| weaponInstance.getState() == WeaponState.FIRING
							|| weaponInstance.getState() == WeaponState.RECOILED
							|| weaponInstance.getState() == WeaponState.PAUSED
							);
				}

			}
		}
	}

	@Override
	protected void onCompatibleEntityJoinedWorldEvent(CompatibleEntityJoinWorldEvent compatibleEntityJoinWorldEvent) {
	    if(compatibleEntityJoinWorldEvent.getEntity() instanceof Contextual) {
	        ((Contextual)compatibleEntityJoinWorldEvent.getEntity()).setContext(modContext);
	    }
	}

    @Override
    protected ModContext getModContext() {
        return modContext;
    }
}