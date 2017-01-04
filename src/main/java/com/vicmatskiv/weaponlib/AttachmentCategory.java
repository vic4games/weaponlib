package com.vicmatskiv.weaponlib;

public enum AttachmentCategory {
	SCOPE, GRIP, SILENCER, MAGAZINE;
	
	public static final AttachmentCategory values[] = values();
	
	public static AttachmentCategory valueOf(int ordinal) {
		return values[ordinal];
		
	}
}
