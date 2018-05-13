package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEnumHandSide;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExtraEntityFlags;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerRenderer {
    
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(PlayerRenderer.class);

    protected static class StateDescriptor {
        protected MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager;
        protected float rate;
        protected float amplitude = 0.04f;
        public StateDescriptor(MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager,
                float rate, float amplitude) {
            this.stateManager = stateManager;
            this.rate = rate;
            this.amplitude = amplitude;
        }
    }
     
    private PlayerTransitionProvider transitionProvider;// = new PlayerTransitionProvider();
    
    private ThreadLocal<Positioner<Part, RenderContext<RenderableState>>> currentPositioner = new ThreadLocal<>();
    
    private int currentFlags;
    private int newFlags;
    private long renderingStartTimestamp;
    private long playerStopMovingTimestamp;
    private ClientModContext clientModContext;
    
    private MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager;
    
    public PlayerRenderer(ClientModContext clientModContext) {
        this.clientModContext = clientModContext;
        this.transitionProvider = clientModContext.getPlayerTransitionProvider();
    }
    
    private StateDescriptor getStateDescriptor(EntityPlayer player) {
        
        if(currentFlags != newFlags) {
            stateManager = null;
        }
                
        if(stateManager == null) {
            //logger.trace("Creating state manager");
            stateManager = new MultipartRenderStateManager<>(RenderableState.NORMAL, transitionProvider,
                  () -> currentTime(player));
        } else if(player.distanceWalkedModified == player.prevDistanceWalkedModified
                /*|| player.motionX == 0 || player.motionZ == 0*/) {
            //logger.trace("Setting aiming state");
            stateManager.setState(RenderableState.PRONING_AIMING, true, true, true);
        } else {
            //logger.trace("Setting proning state");
            stateManager.setCycleState(RenderableState.PRONING, false);
        }

        return new StateDescriptor(stateManager, 0f, 0f);
    }
    
    private long currentTime(EntityPlayer player) {
        long elapseRenderingStart = System.currentTimeMillis() - renderingStartTimestamp;
        int renderingStartThreshold = 400;
        if(elapseRenderingStart < renderingStartThreshold) {
            return elapseRenderingStart;
        }
        long afterStopMovingTimeout = 0;
        if(player.distanceWalkedModified == player.prevDistanceWalkedModified) {
            //logger.trace("Player stopped moving");
            if(playerStopMovingTimestamp == 0) {
                playerStopMovingTimestamp = System.currentTimeMillis();
            } else if(afterStopMovingTimeout < 1000){
                afterStopMovingTimeout = System.currentTimeMillis() - playerStopMovingTimestamp;
            }
        } else {
            playerStopMovingTimestamp = 0;
        }
        return (long)(renderingStartThreshold + player.distanceWalkedModified * 600 + (afterStopMovingTimeout));
    }

    public void renderModel(ModelBiped modelPlayer, EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        newFlags = CompatibleExtraEntityFlags.getFlags(player);
        if(newFlags != currentFlags) {
            renderingStartTimestamp = System.currentTimeMillis();
        }
        if((newFlags & CompatibleExtraEntityFlags.PRONING) != 0) {
            renderProningModel2(modelPlayer, player, ageInTicks, netHeadYaw, headPitch, scale);
        } else {
            //currentPositioner.remove();
            modelPlayer.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        currentFlags = newFlags;
    }

    private void renderProningModel2(ModelBiped modelPlayer, EntityPlayer player, float ageInTicks, float netHeadYaw,
            float headPitch, float scale) {
        StateDescriptor stateDescriptor = getStateDescriptor(player);
        MultipartPositioning<Part, RenderContext<RenderableState>> multipartPositioning = stateDescriptor.stateManager.nextPositioning();
        Positioner<Part, RenderContext<RenderableState>> positioner = multipartPositioning.getPositioner();
        currentPositioner.set(positioner);
        
        GL11.glPushMatrix();
        
        RenderContext<RenderableState> renderContext = new RenderContext<>(clientModContext, player, null);

        renderContext.setAgeInTicks(ageInTicks);
        renderContext.setScale(scale);
        renderContext.setLimbSwing(0); //limbSwing);
        renderContext.setNetHeadYaw(netHeadYaw);
        renderContext.setHeadPitch(headPitch);
        renderContext.setCompatibleTransformType(CompatibleTransformType.NONE);
        
        modelPlayer.setRotationAngles(0, 0, renderContext.getAgeInTicks(), renderContext.getNetHeadYaw(), 
                renderContext.getHeadPitch(), renderContext.getScale(), renderContext.getPlayer());

        positioner.position(Part.MAIN, renderContext);
        
        renderBody(positioner, modelPlayer, renderContext);
        renderHead(positioner, modelPlayer, renderContext);
        renderLeftArm(positioner, modelPlayer, renderContext);
        renderRightArm(positioner, modelPlayer, renderContext);
        renderLeftLeg(positioner, modelPlayer, renderContext);
        renderRightLeg(positioner, modelPlayer, renderContext);
        
        GL11.glPopMatrix();
    }
    
    private void renderBody(Positioner<Part, RenderContext<RenderableState>> positioner, 
            ModelBiped modelPlayer, RenderContext<RenderableState> renderContext) {
        GL11.glPushMatrix();
        modelPlayer.bipedBody.render(renderContext.getScale());
        compatibility.renderBodywear(modelPlayer, renderContext.getScale());
        GL11.glPopMatrix();
    }
    
    private void renderHead(Positioner<Part, RenderContext<RenderableState>> positioner,
            ModelBiped modelPlayer, RenderContext<RenderableState> renderContext) {
        GL11.glPushMatrix();
        positioner.position(Part.HEAD, renderContext);
        modelPlayer.bipedHead.render(renderContext.getScale());
        compatibility.renderHeadwear(modelPlayer, renderContext.getScale());
        GL11.glPopMatrix();
    }

    private void renderRightArm(Positioner<Part, RenderContext<RenderableState>> positioner,
            ModelBiped modelPlayer, RenderContext<RenderableState> renderContext) {
        GL11.glPushMatrix();
        positioner.position(Part.RIGHT_HAND, renderContext);
        modelPlayer.bipedRightArm.render(renderContext.getScale());
        compatibility.renderRightArmwear(modelPlayer, renderContext.getScale());
        GL11.glPopMatrix();
    }

    private void renderLeftArm(Positioner<Part, RenderContext<RenderableState>> positioner,
            ModelBiped modelPlayer, RenderContext<RenderableState> renderContext) {
        GL11.glPushMatrix();
        positioner.position(Part.LEFT_HAND, renderContext);
        modelPlayer.bipedLeftArm.render(renderContext.getScale());
        compatibility.renderLeftArmwear(modelPlayer, renderContext.getScale());
        GL11.glPopMatrix();
    }
    
    private void renderRightLeg(Positioner<Part, RenderContext<RenderableState>> positioner,
            ModelBiped modelPlayer, RenderContext<RenderableState> renderContext) {
        GL11.glPushMatrix();
        positioner.position(Part.RIGHT_LEG, renderContext);
        modelPlayer.bipedRightLeg.render(renderContext.getScale());
        compatibility.renderRightLegwear(modelPlayer, renderContext.getScale());
        GL11.glPopMatrix();
    }

    private void renderLeftLeg(Positioner<Part, RenderContext<RenderableState>> positioner,
            ModelBiped modelPlayer, RenderContext<RenderableState> renderContext) {
        GL11.glPushMatrix();
        positioner.position(Part.LEFT_LEG, renderContext);
        modelPlayer.bipedLeftLeg.render(renderContext.getScale());
        compatibility.renderLeftLegwear(modelPlayer, renderContext.getScale());
        GL11.glPopMatrix();
    }

    public boolean renderArmor(ModelBiped modelPlayer, EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if((currentFlags & CompatibleExtraEntityFlags.PRONING) != 0) {
            return renderArmor2(modelPlayer, player, ageInTicks, netHeadYaw, headPitch, scale);
        } else {
            return false;
        }
    }

    private boolean renderArmor2(ModelBiped modelPlayer, EntityPlayer player, float ageInTicks, float netHeadYaw,
            float headPitch, float scale) {
        
        Positioner<Part, RenderContext<RenderableState>> positioner = currentPositioner.get();

        if(positioner != null) {
            GL11.glPushMatrix();
            
            RenderContext<RenderableState> renderContext = new RenderContext<>(clientModContext, player, null);

            renderContext.setAgeInTicks(ageInTicks);
            renderContext.setScale(scale);
            renderContext.setLimbSwing(0); //limbSwing);
            renderContext.setNetHeadYaw(netHeadYaw);
            renderContext.setHeadPitch(headPitch);
            renderContext.setCompatibleTransformType(CompatibleTransformType.NONE);
            
            modelPlayer.setRotationAngles(0, 0, renderContext.getAgeInTicks(), renderContext.getNetHeadYaw(), 
                    renderContext.getHeadPitch(), renderContext.getScale(), renderContext.getPlayer());

            positioner.position(Part.MAIN, renderContext);
            
            renderBody(positioner, modelPlayer, renderContext);
            renderHead(positioner, modelPlayer, renderContext);
            renderLeftArm(positioner, modelPlayer, renderContext);
            renderRightArm(positioner, modelPlayer, renderContext);
            renderLeftLeg(positioner, modelPlayer, renderContext);
            renderRightLeg(positioner, modelPlayer, renderContext);
            
            GL11.glPopMatrix();
        }
        return positioner != null;
    }

    public boolean positionItemSide(EntityPlayer player, ItemStack itemStack, CompatibleTransformType transformType, CompatibleEnumHandSide handSide) {
        Positioner<Part, RenderContext<RenderableState>> positioner = currentPositioner.get();
        if(positioner != null) {
            
            RenderContext<RenderableState> renderContext = new RenderContext<>(clientModContext, player, null);

            positioner.position(Part.MAIN, renderContext);
            
            if(handSide == CompatibleEnumHandSide.LEFT) {
                positioner.position(Part.LEFT_HAND, renderContext);
            } else if(handSide == null || handSide == CompatibleEnumHandSide.RIGHT) {
                positioner.position(Part.RIGHT_HAND, renderContext);
            }
            
            GL11.glTranslatef(-0.35f, 0.1f, -0f);
            GL11.glRotatef(-378f, 1f, 0f, 0f);
            GL11.glRotatef(360f, 0f, 1f, 0f);
            GL11.glRotatef(0f, 0f, 0f, 1f);
        }
        return positioner != null;
    }
}
