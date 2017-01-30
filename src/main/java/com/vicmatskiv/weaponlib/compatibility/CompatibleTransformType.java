package com.vicmatskiv.weaponlib.compatibility;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public enum CompatibleTransformType {

	NONE,
    THIRD_PERSON_LEFT_HAND,
    THIRD_PERSON_RIGHT_HAND,
    FIRST_PERSON_LEFT_HAND,
    FIRST_PERSON_RIGHT_HAND,
    HEAD,
    GUI,
    GROUND,
    FIXED;
	
	public static CompatibleTransformType fromItemRenderType(ItemRenderType itemRenderType) {
		CompatibleTransformType result = null;
		switch(itemRenderType) {
		case ENTITY: result = GROUND; break;
		case EQUIPPED: result = THIRD_PERSON_RIGHT_HAND; break;
		case EQUIPPED_FIRST_PERSON: result = FIRST_PERSON_RIGHT_HAND; break;
		case INVENTORY: result = GUI;
		default: result = NONE; break;
		}
		return result;
		
	}
}
