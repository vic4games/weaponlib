package com.vicmatskiv.weaponlib;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.perspective.PerspectiveRenderer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ItemScope extends ItemAttachment<Weapon> {

    private final int DEFAULT_MAX_STACK_SIZE = 1;

    public static final class Builder extends AttachmentBuilder<Weapon> {

        private float minZoom;
        private float maxZoom;
        private boolean isOpticalZoom;
        private boolean hasNightVision;
        private BiConsumer<EntityLivingBase, ItemStack> viewfinderPositioning;

        public Builder withZoomRange(float minZoom, float maxZoom) {
            this.minZoom = minZoom;
            this.maxZoom = maxZoom;
            return this;
        }

        public Builder withOpticalZoom() {
            this.isOpticalZoom = true;
            return this;
        }
        
        public Builder withNightVision() {
            this.hasNightVision = true;
            return this;
        }

        public Builder withViewfinderPositioning(BiConsumer<EntityLivingBase, ItemStack> viewfinderPositioning) {
            this.viewfinderPositioning = viewfinderPositioning;
            return this;
        }

        @Override
        protected ItemAttachment<Weapon> createAttachment(ModContext modContext) {
            if(isOpticalZoom) {
                if(viewfinderPositioning == null) {
                    viewfinderPositioning = (p, s) -> {
                        GL11.glScalef(1.1f, 1.1f, 1.1f);
                        GL11.glTranslatef(0.1f, 0.4f, 0.6f);
                    };
                }
                withPostRender(new PerspectiveRenderer(viewfinderPositioning));
            }

            ItemScope itemScope = new ItemScope(this);
            itemScope.modContext = modContext;

            return itemScope;
        }

        @Override
        public ItemAttachment<Weapon> build(ModContext modContext) {
            this.apply2 = (a, instance) -> {
                float zoom = minZoom + (maxZoom - minZoom) / 2f;
                instance.setZoom(zoom);
            };
            this.remove2 = (a, instance) -> {
                instance.setZoom(1);
            };
            return super.build(modContext);
        }
    }

    @SuppressWarnings("unused")
    private ModContext modContext;
    private Builder builder;

    private ItemScope(Builder builder) {
        super(builder.getModId(), AttachmentCategory.SCOPE, builder.getModel(), builder.getTextureName(), null, 
                null, null);
        this.builder = builder;

        setMaxStackSize(DEFAULT_MAX_STACK_SIZE);
    }

    public float getMinZoom() {
        return builder.minZoom;
    }

    public float getMaxZoom() {
        return builder.maxZoom;
    }

    public boolean isOptical() {
        return builder.isOpticalZoom;
    }

    public boolean hasNightVision() {
        return builder.hasNightVision;
    }
}
