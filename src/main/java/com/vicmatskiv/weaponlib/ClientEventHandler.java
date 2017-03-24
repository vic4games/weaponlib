package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent.Phase;
import com.vicmatskiv.weaponlib.perspective.Perspective;
import com.vicmatskiv.weaponlib.compatibility.CompatiblePlayerCreatureWrapper;
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWorldWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class ClientEventHandler extends CompatibleClientEventHandler {
	
	private static final UUID SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID = UUID.fromString("8efa8469-0256-4f8e-bdd9-3e7b23970663");
	private static final AttributeModifier SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER = (new AttributeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID, "Slow Down While Zooming", -0.5, 2)).setSaved(false);

	private static final Logger logger = LogManager.getLogger(ClientEventHandler.class);

	private Lock mainLoopLock = new ReentrantLock();
	private SafeGlobals safeGlobals;
	private Queue<Runnable> runInClientThreadQueue;
	private long renderEndNanoTime;
	
	
	private ClientModContext modContext;
	
	private FakePlayer fakePlayer;
	
	//private ReloadAspect reloadAspect;

	public ClientEventHandler(ClientModContext modContext, Lock mainLoopLock, SafeGlobals safeGlobals, 
			Queue<Runnable> runInClientThreadQueue /*, ReloadAspect reloadAspect*/) {
		this.modContext = modContext;
		this.mainLoopLock = mainLoopLock;
		this.safeGlobals = safeGlobals;
		this.runInClientThreadQueue = runInClientThreadQueue;
        this.renderEndNanoTime = System.nanoTime();
        //this.reloadAspect = reloadAspect;
	}

	public void onCompatibleClientTick(CompatibleClientTickEvent event) {		
		if(event.getPhase() == Phase.START) {
			mainLoopLock.lock();
		} else if(event.getPhase() == Phase.END) {
			update();
			modContext.getSyncManager().run();
			mainLoopLock.unlock();
			processRunInClientThreadQueue();
			safeGlobals.objectMouseOver.set(compatibility.getObjectMouseOver());
			if(compatibility.clientPlayer() != null) {
				safeGlobals.currentItemIndex.set(compatibility.clientPlayer().inventory.currentItem);
				
				//reloadAspect.updateMainHeldItem(compatibility.clientPlayer());
			}
		}
	}

	private void update() {
		EntityPlayer player = compatibility.clientPlayer();
		modContext.getPlayerItemInstanceRegistry().update(player);
		PlayerWeaponInstance mainHandHeldWeaponInstance = modContext.getMainHeldWeapon();
		if(mainHandHeldWeaponInstance != null) {
			if(player.isSprinting()) {
				mainHandHeldWeaponInstance.setAimed(false);
			}
			if(mainHandHeldWeaponInstance.isAimed()) {
				slowPlayerDown(player);
			} else {
				restorePlayerSpeed(player);
			}
		} else if(player != null){
			restorePlayerSpeed(player);
		}
	}
	
	// TODO: create player utils, move this method
	private void restorePlayerSpeed(EntityPlayer entityPlayer) {
		if(entityPlayer.getEntityAttribute(compatibility.getMovementSpeedAttribute())
				.getModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER.getID()) != null) {
			entityPlayer.getEntityAttribute(compatibility.getMovementSpeedAttribute())
				.removeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		}
	}

	// TODO: create player utils, move this method
	private void slowPlayerDown(EntityPlayer entityPlayer) {
		if(entityPlayer.getEntityAttribute(compatibility.getMovementSpeedAttribute())
				.getModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER.getID()) == null) {
			entityPlayer.getEntityAttribute(compatibility.getMovementSpeedAttribute())
				.applyModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER);
		}
	}

	private void processRunInClientThreadQueue() {
		Runnable r;
		while((r = runInClientThreadQueue.poll()) != null) {
			r.run();
		}
	}
	
	private static class RenderInfo {
	    private RenderGlobal renderGlobal;
        private EffectRenderer effectRenderer;
        private CompatiblePlayerCreatureWrapper watchablePlayer;
        private WorldClient world;

        public RenderInfo(WorldClient world) {
            this.world = world;
	        this.renderGlobal = new RenderGlobal(Minecraft.getMinecraft());
	        this.effectRenderer = new EffectRenderer(world, Minecraft.getMinecraft().getTextureManager());
	        this.watchablePlayer = new CompatiblePlayerCreatureWrapper(Minecraft.getMinecraft(), world);
	        this.renderGlobal.setWorldAndLoadRenderers(world);
        }
	}
	
	private RenderInfo renderInfo;

	private int tickCounter = 0;
	
	@Override
    protected void onCompatibleRenerTickEvent(CompatibleRenderTickEvent event) {
        if(event.getPhase() ==  CompatibleRenderTickEvent.Phase.START && compatibility.clientPlayer() != null) {
            
            PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry()
                    .getMainHandItemInstance(compatibility.clientPlayer());
            if(instance != null) {
                Perspective<?> view = modContext.getViewManager().getPerspective(instance, true);
                if(view != null) {
                    view.update(event);
                }
            }
            
            
//            safeGlobals.renderingPhase.set(RenderingPhase.RENDER_VIEWFINDER);
//            long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
//            
//            PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
//            if(instance != null && instance.isAimed()) {
//                modContext.getFramebuffer().bindFramebuffer(true);
//                modContext.getSecondWorldRenderer().updateRenderer();
//                modContext.getSecondWorldRenderer().renderWorld(event.getRenderTickTime(), p_78471_2_);
//                Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
//            } else {
//                //logger.debug("Either instance is null or not aimed");
//            }
//                
//            this.renderEndNanoTime = System.nanoTime();
            
            //safeGlobals.renderingPhase.set(RenderingPhase.NORMAL);
        } else if(event.getPhase() ==  CompatibleRenderTickEvent.Phase.END) {
            safeGlobals.renderingPhase.set(null);
        }
    }
	
//	@Override
//	protected void onCompatibleRenerTickEvent(CompatibleRenderTickEvent event) {
//		if(event.getPhase() ==  CompatibleRenderTickEvent.Phase.START && compatibility.clientPlayer() != null) {
//		    Entity watchableEntity = DebugPositioner.getWatchableEntity();
//		    if(watchableEntity != null && tickCounter++ %10 == 0) {
//		        logger.debug("Watching {}, distance: {}  ", 
//		                watchableEntity, 
//		                    Math.sqrt(Math.pow(watchableEntity.posX - Minecraft.getMinecraft().thePlayer.posX, 2)
//		                    + Math.pow(watchableEntity.posX - Minecraft.getMinecraft().thePlayer.posZ, 2))
//		                    );
//		    }
//		    
//			safeGlobals.renderingPhase.set(RenderingPhase.RENDER_VIEWFINDER);
//			long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
//			
//			PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
//			
//			if(/*instance != null && instance.isAimed() &&*/ watchableEntity != null) {
//				modContext.getFramebuffer().bindFramebuffer(true);
//				
//				WorldClient origWorld = Minecraft.getMinecraft().theWorld;
//				if(renderInfo == null) {
//				    WorldClient world = Minecraft.getMinecraft().theWorld;// new CompatibleWorldWrapper(Minecraft.getMinecraft().theWorld.provider.dimensionId);
//				    renderInfo = new RenderInfo(world);
//				}
//				Entity realEntity = renderInfo.world.getEntityByID(watchableEntity.getEntityId());
//                if(realEntity != null && realEntity != watchableEntity) {
//                    watchableEntity = (EntityLivingBase) realEntity;
//                }
//                if(watchableEntity == null) {
//                    modContext.getFramebuffer().framebufferClear();
//                }
//                
//                if(watchableEntity != null) {
//                    renderInfo.world.tick();
//				    EntityLivingBase origRenderViewEntity = Minecraft.getMinecraft().renderViewEntity;
//				    EntityClientPlayerMP origPlayer = Minecraft.getMinecraft().thePlayer;
//
//				    //((CompatiblePlayerCreatureWrapper) watchableEntity).updateCoordinates();
//				    RenderGlobal origRenderGlobal = Minecraft.getMinecraft().renderGlobal;
//				    EffectRenderer origEffectRenderer = Minecraft.getMinecraft().effectRenderer;
//				    renderInfo.watchablePlayer.setEntityLiving((EntityLivingBase) watchableEntity);
//				    Minecraft.getMinecraft().renderGlobal = renderInfo.renderGlobal;
//				    Minecraft.getMinecraft().effectRenderer = renderInfo.effectRenderer;
//				    Minecraft.getMinecraft().renderViewEntity = (EntityLivingBase) watchableEntity;
//				    Minecraft.getMinecraft().thePlayer = renderInfo.watchablePlayer;
//				    Minecraft.getMinecraft().theWorld = renderInfo.world;
//				    RenderManager.instance.set(renderInfo.world);
//                    
//				    //Minecraft.getMinecraft().theWorld.updateEntities();
//				    //Minecraft.getMinecraft().thePlayer = renderInfo.watchablePlayer;
//				    //Minecraft.getMinecraft().renderViewEntity = (EntityLivingBase) watchableEntity; //Minecraft.getMinecraft().thePlayer;
//				    
//				    modContext.getSecondWorldRenderer().updateRenderer((EntityLivingBase) watchableEntity); //Minecraft.getMinecraft().thePlayer);
//	                modContext.getSecondWorldRenderer().renderWorld((EntityLivingBase) watchableEntity, event.getRenderTickTime(), p_78471_2_);
//	                
//	                Minecraft.getMinecraft().renderViewEntity = origRenderViewEntity;
//	                Minecraft.getMinecraft().thePlayer = origPlayer;
//	                Minecraft.getMinecraft().theWorld = origWorld;
//                    Minecraft.getMinecraft().renderGlobal = origRenderGlobal;
//                    Minecraft.getMinecraft().effectRenderer = origEffectRenderer;
//                    
//                    RenderManager.instance.set(origWorld);
//                    
//	                //Minecraft.getMinecraft().gameSettings.renderDistanceChunks = currentRenderDistanceChunks;
//				}
//				
//				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
//			} else {
//				//logger.debug("Either instance is null or not aimed");
//			}
//				
//			this.renderEndNanoTime = System.nanoTime();
//			
//			safeGlobals.renderingPhase.set(RenderingPhase.NORMAL);
//		} else if(event.getPhase() ==  CompatibleRenderTickEvent.Phase.END) {
//			safeGlobals.renderingPhase.set(null);
//		}
//	}
}
