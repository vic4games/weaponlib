package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.RenderingPhase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CompatibleClientEventHandler {
    
    private Entity origRenderVeiwEntity;
	
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
	        // This is a hack to allow player to view him/herself in remote perspective
	        origRenderVeiwEntity = event.getRenderer().getRenderManager().renderViewEntity;
	        event.getRenderer().getRenderManager().renderViewEntity = event.getEntityPlayer();
	    }
	}
	
	protected abstract ModContext getModContext();

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public final void onPostRenderPlayer(RenderPlayerEvent.Post event) {
        ClientModContext modContext = (ClientModContext) getModContext();
        if(modContext.getSafeGlobals().renderingPhase.get() == RenderingPhase.RENDER_PERSPECTIVE
                && event.getEntityPlayer() instanceof EntityPlayerSP) {
            event.getRenderer().getRenderManager().renderViewEntity = origRenderVeiwEntity;
        }
    }

	protected abstract void onCompatibleRenderTickEvent(CompatibleRenderTickEvent compatibleRenderTickEvent);

	protected abstract void onCompatibleClientTick(CompatibleClientTickEvent compatibleClientTickEvent);
}
