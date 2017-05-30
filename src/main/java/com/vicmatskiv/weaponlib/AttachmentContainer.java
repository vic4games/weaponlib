package com.vicmatskiv.weaponlib;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface AttachmentContainer {
    List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(EntityPlayer entityPlayer,
            ItemStack itemStack);

    Collection<CompatibleAttachment<? extends AttachmentContainer>> getCompatibleAttachments(AttachmentCategory...category);
}
