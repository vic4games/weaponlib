package com.vicmatskiv.weaponlib;

class ExpirableRenderableState {
	RenderableState state;
	long expiresAt;

	public ExpirableRenderableState(RenderableState state, long expiresAt) {
		this.state = state;
		this.expiresAt = expiresAt;
	}
}