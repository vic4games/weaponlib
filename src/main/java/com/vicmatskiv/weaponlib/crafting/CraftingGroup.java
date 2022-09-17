package com.vicmatskiv.weaponlib.crafting;

public enum CraftingGroup {
	GUN,
	ATTACHMENT_NORMAL,
	ATTACHMENT_MODIFICATION,
	BULLET,
	MAGAZINE;
	
	
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
			case 4:
				return BULLET;
			case 5:
				return MAGAZINE;
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
			case BULLET:
				return 4;
			case MAGAZINE:
				return 5;
		}
	}

}
