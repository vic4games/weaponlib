package com.vicmatskiv.weaponlib.crafting;

public enum CraftingGroup {
	GUN,
	ATTACHMENT_NORMAL,
	ATTACHMENT_MODIFICATION;
	
	
	public static CraftingGroup getValue(int id) {
		switch(id) {
			default:
				return GUN;
			case 1:
				return GUN;
			case 2:
				return ATTACHMENT_NORMAL;
			case 3:
				return ATTACHMENT_MODIFICATION;
		}
	}
	
	public int getID() {
		switch(this) {
			default:
				return 1;
			case GUN:
				return 1;
			case ATTACHMENT_NORMAL:
				return 2;
			case ATTACHMENT_MODIFICATION:
				return 3;
		}
	}

}
