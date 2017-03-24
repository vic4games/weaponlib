package com.vicmatskiv.weaponlib.perspective;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.PlayerItemInstance;

public class PerspectiveManager {

    private static final Logger logger = LogManager.getLogger(PerspectiveManager.class);

    private Perspective<?> currentPerspective;
    private ClientModContext clientModContext;

    public PerspectiveManager(ClientModContext clientModContext) {
        this.clientModContext = clientModContext;
    }

    public Perspective<?> getPerspective(PlayerItemInstance<?> currentInstance, boolean init) {
        
        if(currentInstance == null || (currentPerspective == null && !init)) {
            return null;
        }

        Class<? extends Perspective<?>> perspectiveClass = currentInstance.getRequiredPerspectiveType();

        if (perspectiveClass != null) {
            if(currentPerspective == null) {
                currentPerspective = createActivePerspective(perspectiveClass);
            } else if(!perspectiveClass.isInstance(currentPerspective)) {
                currentPerspective.deactivate(clientModContext);
                currentPerspective = createActivePerspective(perspectiveClass);
            }
        } else if (currentPerspective != null) {
            currentPerspective.deactivate(clientModContext);
        }

        return currentPerspective;
    }

    private Perspective<?> createActivePerspective(Class<? extends Perspective<?>> perspectiveClass) {
        Perspective<?> result = null;
        try {
            result = perspectiveClass.newInstance();
            result.activate(clientModContext);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Failed to create view of {} - {}", perspectiveClass, e, e);
        }
        return result;
    }

}