package com.vicmatskiv.weaponlib.network;

import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.state.Permit;

import io.netty.buffer.ByteBuf;

public class PermitMessage<Context extends UniversalObject> implements CompatibleMessage {

	private Permit permit;
	private Context context;

	public PermitMessage() {}
	
	public PermitMessage(Permit permit, Context context) {
		this.permit = permit;
		this.context = context;
	}

	public Permit getPermit() {
		return permit;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		context = UniversalObject.fromBytes(buf);
		permit = Permit.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		context.serialize(buf);
		permit.serialize(buf);
	}

}