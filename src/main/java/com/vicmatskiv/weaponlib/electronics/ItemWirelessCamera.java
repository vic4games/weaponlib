package com.vicmatskiv.weaponlib.electronics;

import com.vicmatskiv.weaponlib.AttachmentBuilder;
import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ModContext;

import net.minecraft.client.model.ModelBase;

public class ItemWirelessCamera<T> extends ItemAttachment<T> {
        
        public static final class Builder<T> extends AttachmentBuilder<T> {

            @Override
            protected ItemAttachment<T> createAttachment(ModContext modContext) {
                ItemWirelessCamera<T> skin = new ItemWirelessCamera<>(getModId(), AttachmentCategory.EXTRA, getModel(), getTextureName(), null, null, null);
                return skin;
            }
            
            @Override
            public <V extends ItemAttachment<T>> V build(ModContext modContext, Class<V> target) {
                return super.build(modContext, target);
            }
        }
        
        public ItemWirelessCamera(String modId, AttachmentCategory category, ModelBase model, String textureName, String crosshair,
                com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<T> apply,
                com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<T> remove) {
            super(modId, category, model, textureName, crosshair, apply, remove);
        }

        public String getTextureName() {
            return textureName;
        }
    }