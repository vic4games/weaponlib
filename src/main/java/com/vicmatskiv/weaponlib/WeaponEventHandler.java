package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.concurrent.atomic.AtomicReference;

import com.vicmatskiv.weaponlib.animation.OpenGLSelectionHelper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityJoinWorldEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponEventHandler;
import com.vicmatskiv.weaponlib.compatibility.Interceptors;
import com.vicmatskiv.weaponlib.grenade.PlayerGrenadeInstance;
import com.vicmatskiv.weaponlib.melee.PlayerMeleeInstance;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		//ClientModContext modContext = ClientModContext.getContext();
		PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
		EntityPlayer clientPlayer = compatibility.clientPlayer();
		if (instance != null) {
		   
		    float fov;
		    if(instance.isAttachmentZoomEnabled()) {
		    	fov = instance.getWeapon().getADSZoom();
		    	 if(safeGlobals.renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE) {
		           
		        	fov = instance.getZoom();
		        } else {
		        	
		            fov = compatibility.isFlying(clientPlayer) ? 1.1f : 1.0f;
		        }
		    } else {
		    	 fov = compatibility.isFlying(compatibility.clientPlayer()) ? 1.1f : 1.0f;
		    	//fov = instance.isAimed() ? instance.getZoom() : 1f;
		       // fov = compatibility.isFlying(compatibility.clientPlayer()) ? 1.1f : 1.0f; //instance.isAimed() ? instance.getZoom() : 1f;
		    }

		   RenderingPhase phase = (((ClientModContext) modContext).getSafeGlobals().renderingPhase).get();
		 
		     if(instance.isAimed() && phase == null) {
		    	fov = 0.7f;
		    }
		     
		    if(Minecraft.getMinecraft().player.isSprinting()) {
		    	fov *= 1.2;
		    }
		    
		    //fov = 0.3f;
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
	public void onCompatibleHandleRenderLivingEvent(@SuppressWarnings("rawtypes") RenderLivingEvent.Pre event) {

		
		
		if ((event.isCanceled()) || (!(compatibility.getEntity(event) instanceof EntityPlayer)))
			return;

		ItemStack itemStack = compatibility.getHeldItemMainHand(compatibility.getEntity(event));

		if (itemStack != null && itemStack.getItem() instanceof Weapon) {
			RenderPlayer rp = compatibility.getRenderer(event);

			if (itemStack != null ) {
			    EntityPlayer player = (EntityPlayer)compatibility.getEntity(event);
			    PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry()
			            .getItemInstance(player, itemStack);
			    if(instance instanceof PlayerWeaponInstance) {
			        PlayerWeaponInstance weaponInstance = (PlayerWeaponInstance) instance;
			        compatibility.setAimed(rp, !Interceptors.isProning(player) && 
			                (weaponInstance.isAimed()
			                || weaponInstance.getState() == WeaponState.FIRING
			                || weaponInstance.getState() == WeaponState.RECOILED
			                || weaponInstance.getState() == WeaponState.PAUSED
			                ));
			    }
			}
		}
	}
	
    @SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onRenderItemEvent(RenderHandEvent event) {
    	
	    if(compatibility.clientPlayer().getRidingEntity() instanceof EntityVehicle) {
	       
	    	event.setCanceled(true);
	    }
	    
	  
	    
	    //System.out.println("Render player");
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