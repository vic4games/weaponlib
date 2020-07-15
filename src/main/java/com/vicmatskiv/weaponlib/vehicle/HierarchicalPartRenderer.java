package com.vicmatskiv.weaponlib.vehicle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

final class HierarchicalPartRenderer<Part, State> implements StatefulRenderer<State> {
    
    static enum SinglePart { MAIN }
       
    private StatefulRenderer<State> modelRenderer;
    private ResourceLocation textureResource;
    
    private Part part;
    
    protected Map<Part, HierarchicalPartRenderer<Part, State>> partRenderers;
    
    protected Supplier<MultipartRenderStateManager<State, SinglePart, PartRenderContext<State>>> stateManagerSupplier;
    
    private BiConsumer<MultipartRenderStateManager<State, SinglePart, PartRenderContext<State>>, PartRenderContext<State>> stateSetter;

    private Function<PartRenderContext<State>, Float> currentProgressProvider;
    
    private Map<Entity, MultipartRenderStateManager<State, SinglePart, PartRenderContext<State>>> stateManagers = new HashMap<>();
    
    protected HierarchicalPartRenderer(Part part, 
            StatefulRenderer<State> modelRenderer,
            ResourceLocation textureResource,
            Map<Part, HierarchicalPartRenderer<Part, State>> partRenderers,
            Supplier<MultipartRenderStateManager<State, SinglePart, PartRenderContext<State>>> stateManagerSupplier,
            BiConsumer<MultipartRenderStateManager<State, SinglePart, PartRenderContext<State>>, PartRenderContext<State>> stateSetter,
            Function<PartRenderContext<State>, Float> currentProgressProvider) {
        this.part = part;
        this.modelRenderer = modelRenderer;
        this.textureResource = textureResource;
        this.partRenderers = partRenderers;
        this.stateManagerSupplier = stateManagerSupplier;
        this.stateSetter = stateSetter;
        this.currentProgressProvider = currentProgressProvider;
    }
    
    @SuppressWarnings("unchecked")
    public void render(PartRenderContext<State> context) {
        
        MultipartRenderStateManager<State, SinglePart, PartRenderContext<State>> stateManager = stateManagers.computeIfAbsent(context.getEntity(), e -> stateManagerSupplier.get());
        
        stateSetter.accept(stateManager, context);
        MultipartPositioning<SinglePart, PartRenderContext<State>> multipartPositioning = stateManager.nextPositioning();
        Positioner<SinglePart, PartRenderContext<State>> positioner = multipartPositioning.getPositioner();
        
        context.setProgress(currentProgressProvider.apply(context));
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureResource);
        
        GL11.glPushMatrix();
        
        try {
            positioner.position(SinglePart.MAIN, context);
            
            if(DebugPositioner.isDebugModeEnabled()) {
                DebugPositioner.position(part, context);
            }
    
            modelRenderer.render(context);
            
            if(part instanceof PartContainer) {
                for(Part renderablePart: ((PartContainer<Part>)part).getChildParts()) {
                    HierarchicalPartRenderer<Part, State> partRenderer = partRenderers.get(renderablePart);
                    if(partRenderer != null) {
//                        System.out.println("Rendering part " + renderablePart);
                        partRenderer.render(context);
                    }
                }
            }
        } finally {
            GL11.glPopMatrix();
        }
        
    }
}