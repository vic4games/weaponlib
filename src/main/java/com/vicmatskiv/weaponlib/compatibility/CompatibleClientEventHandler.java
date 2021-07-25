package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.particle.DriftCloudFX;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CompatibleClientEventHandler {

    private Entity origRenderVeiwEntity;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public final void updateFOV(FOVUpdateEvent e) {
    	
    	EntityPlayer player = Minecraft.getMinecraft().player;
    	if(player == null || !player.isRiding() || !(player.getRidingEntity() instanceof EntityVehicle)) return;
    	EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
    	
    	double fA = (vehicle.getSolver().getSyntheticAcceleration()/45 + (vehicle.getRealSpeed()/120))*0.2;
    	
    	e.setNewfov((float) (e.getFov()+fA));
    	
    }
    
    @SubscribeEvent
    public final void properCameraSetup(EntityViewRenderEvent.CameraSetup e) {
    	EntityPlayer player = compatibility.getClientPlayer();
    	
    	
        
        if(player.isRiding() && player.getRidingEntity() instanceof EntityVehicle && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
        	EntityVehicle vehicle = (EntityVehicle) player.getRidingEntity();
        	//vehicle.rotationPitch = 30f;
        	
        	if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
        			
        		//GL11.glRotated(-45, 1.0, 0.0, 0.0);
        		//GL11.glTranslated(player.posX, player.posY, player.posZ);

        		
        		
        		//GL11.glTranslated(-player.posX, -player.posY, -player.posZ);
        		
        		
        		//e.setRoll(-(vehicle.rotationRoll + vehicle.rotationRollH));
        		//e.setPitch(-vehicle.rotationPitch);
        		//GL11.glTranslated(0.0, -0.9, -.8);
        	}

        }
    }
    
    
    
    
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onClientTick(TickEvent.ClientTickEvent event) {
		onCompatibleClientTick(new CompatibleClientTickEvent(event));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public final void onRenderTickEvent(TickEvent.RenderTickEvent event) {
		onCompatibleRenderTickEvent(new CompatibleRenderTickEvent(event));
	}

	@SideOnly(Side.CLIENT)
    @SubscribeEvent
	public final void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
	    ClientModContext modContext = (ClientModContext) getModContext();
	    if(modContext.getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE
	            && event.getEntityPlayer() instanceof EntityPlayerSP) {
	        /*
	         *  This is a hack to allow player to view him/herself in remote perspective.
	         *  By default EntityPlayerSP ("user" playing the game) cannot see himself unless player == renderViewEntity.
	         *  So, before rendering EntityPlayerSP, setting renderViewEntity to player temporarily.
	         */
	        origRenderVeiwEntity = event.getRenderer().getRenderManager().renderViewEntity;
	        event.getRenderer().getRenderManager().renderViewEntity = event.getEntityPlayer();
	    }
	    
	    onCompatibleRenderPlayerPreEvent(new CompatibleRenderPlayerPreEvent(event));
	}

	protected abstract ModContext getModContext();

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public final void onPostRenderPlayer(RenderPlayerEvent.Post event) {
        ClientModContext modContext = (ClientModContext) getModContext();
        if(modContext.getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE
                && event.getEntityPlayer() instanceof EntityPlayerSP) {
            /*
             *  This is a hack to allow player to view him/herself in remote perspective.
             *  By default EntityPlayerSP ("user" playing the game) cannot see himself unless player == renderViewEntity.
             *  So, before rendering EntityPlayerSP, setting renderViewEntity to player temporarily.
             *  After rendering EntityPlayerSP, restoring the original renderViewEntity.
             */
            event.getRenderer().getRenderManager().renderViewEntity = origRenderVeiwEntity;
        }
    }
    
    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        onCompatibleRenderHand(new CompatibleRenderHandEvent(event));
    }

    public static TextureAtlasSprite carParticles;
    
    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(getModContext().getNamedResource(
                CompatibleParticle.CompatibleParticleBreaking.TEXTURE_BLOOD_PARTICLES));
        carParticles = event.getMap().registerSprite(new ResourceLocation("mw" + ":" + "particle/carparticle"));
    }

	protected abstract void onCompatibleRenderTickEvent(CompatibleRenderTickEvent compatibleRenderTickEvent);

	protected abstract void onCompatibleClientTick(CompatibleClientTickEvent compatibleClientTickEvent);

	protected abstract void onCompatibleRenderHand(CompatibleRenderHandEvent event);

    protected abstract void onCompatibleRenderPlayerPreEvent(CompatibleRenderPlayerPreEvent event);

}
