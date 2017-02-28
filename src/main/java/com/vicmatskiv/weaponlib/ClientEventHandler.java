package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientTickEvent.Phase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;

public class ClientEventHandler extends CompatibleClientEventHandler {
	
	private static final UUID SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID = UUID.fromString("8efa8469-0256-4f8e-bdd9-3e7b23970663");
	private static final AttributeModifier SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER = (new AttributeModifier(SLOW_DOWN_WHILE_ZOOMING_ATTRIBUTE_MODIFIER_UUID, "Slow Down While Zooming", -0.5, 2)).setSaved(false);

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ClientEventHandler.class);

	private Lock mainLoopLock = new ReentrantLock();
	private SafeGlobals safeGlobals;
	private Queue<Runnable> runInClientThreadQueue;
	private long renderEndNanoTime;
	
	
	private ClientModContext modContext;
	
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
	
	@SubscribeEvent
	public final void onRenderTickEvent(TickEvent.RenderTickEvent event) {
		
		if(event.phase == TickEvent.Phase.START && compatibility.clientPlayer() != null) {
			safeGlobals.renderingPhase.set(RenderingPhase.RENDER_VIEWFINDER);
			long p_78471_2_ = this.renderEndNanoTime + (long)(1000000000 / 60);
			
			PlayerWeaponInstance instance = modContext.getMainHeldWeapon();
			if(instance != null && instance.isAimed()) {
				modContext.getFramebuffer().bindFramebuffer(true);
				modContext.getSecondWorldRenderer().updateRenderer();
				modContext.getSecondWorldRenderer().renderWorld(event.renderTickTime, p_78471_2_);
				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
			} else {
				//logger.debug("Either instance is null or not aimed");
			}
				
			this.renderEndNanoTime = System.nanoTime();
			
			safeGlobals.renderingPhase.set(RenderingPhase.NORMAL);
		} else if(event.phase == TickEvent.Phase.END) {
			safeGlobals.renderingPhase.set(null);
		}
	}
}
