package com.vicmatskiv.weaponlib;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface AttachmentContainer {
	
	List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(ItemStack itemStack);
}
