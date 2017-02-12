package com.vicmatskiv.weaponlib.state;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;

import net.minecraft.entity.player.EntityPlayerMP;

public class NetworkPermitManager<Context extends UniversalObject> implements PermitManager<Context>, CompatibleMessageHandler<PermitMessage<Context>, CompatibleMessage>  {
	
	private ModContext modContext;
	private Map<UUID, BiConsumer<Permit, Context>> permitCallbacks = new HashMap<>();
	private BiConsumer<Permit, Context> serverAction;

	public NetworkPermitManager(ModContext modContext, BiConsumer<Permit, Context> serverAction) {
		this.modContext = modContext;
		this.serverAction = serverAction;
	}
	
	@Override
	public void request(Permit permit, Context context, BiConsumer<Permit, Context> callback) {
		permitCallbacks.put(permit.getUuid(), callback);
		modContext.getChannel().getChannel().sendToServer(new PermitMessage<>(permit));
	}

	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(PermitMessage<Context> permitMessage,
			CompatibleMessageContext ctx) {
		
		Permit permit = permitMessage.getPermit();
		Context context = permitMessage.getContext();
		if(ctx.isServerSide()) {
			ctx.runInMainThread(() -> {
				serverAction.accept(permit, context);
				modContext.getChannel().getChannel().sendTo(new PermitMessage<>(permit), (EntityPlayerMP) ctx.getPlayer());
			});
		} else {
			compatibility.runInMainClientThread(() -> {
				BiConsumer<Permit, Context> callback = permitCallbacks.remove(permit.getUuid());
				if(callback != null) {
					callback.accept(permit, context);
				}
			});
		}
		
		return null;
	}


}
