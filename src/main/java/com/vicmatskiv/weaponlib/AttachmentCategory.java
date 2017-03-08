package com.vicmatskiv.weaponlib;

public enum AttachmentCategory {
	SCOPE, GRIP, SILENCER, MAGAZINE, BULLET, SKIN, EXTRA, EXTRA2, EXTRA3, EXTRA4;
	
	public static final AttachmentCategory values[] = values();
	
	public static AttachmentCategory valueOf(int ordinal) {
		return values[ordinal];
		
	}
}
