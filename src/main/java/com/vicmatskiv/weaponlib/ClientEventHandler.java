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
import com.vicmatskiv.weaponlib.compatibility.CompatibleRenderTickEvent;
import com.vicmatskiv.weaponlib.perspective.Perspective;
import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
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

	private FakePlayer fakePlayer; // TODO: remove

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

			PlayerEntityTracker tracker = PlayerEntityTracker.getTracker(compatibility.clientPlayer());
			if(tracker != null) {
			    tracker.update();
			}
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

	@Override
    protected void onCompatibleRenderTickEvent(CompatibleRenderTickEvent event) {
        if(event.getPhase() ==  CompatibleRenderTickEvent.Phase.START && compatibility.clientPlayer() != null) {

            PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry()
                    .getMainHandItemInstance(compatibility.clientPlayer());
            if(instance != null) {
                Perspective<?> view = modContext.getViewManager().getPerspective(instance, true);
                if(view != null) {
                    view.update(event);
                }
            }

        } else if(event.getPhase() ==  CompatibleRenderTickEvent.Phase.END) {
            safeGlobals.renderingPhase.set(null);
        }
    }

    @Override
    protected ModContext getModContext() {
        return modContext;
    }
}
