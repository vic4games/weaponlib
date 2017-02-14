package com.vicmatskiv.weaponlib.state;

import com.vicmatskiv.weaponlib.network.UniversalObject;

import io.netty.buffer.ByteBuf;

public abstract class Permit extends UniversalObject {
	
	public enum Status { REQUESTED, GRANTED, DENIED, UNKNOWN };
	
	protected ManagedState state;
	protected Status status;
	
	public Permit() {
		this.status = Status.UNKNOWN;
	}
	
	protected Permit(ManagedState state) {
		this.state = state;
		this.status = Status.REQUESTED;
	}

	public ManagedState getState() {
		return state;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
	
	@Override
	public boolean init(ByteBuf buf) {
		super.init(buf);
		status = Status.values()[buf.readInt()];
		return true;
	}
	
	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeInt(status.ordinal());
	}
	
}