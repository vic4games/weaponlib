package com.vicmatskiv.weaponlib.network;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.state.ExtendedState;
import com.vicmatskiv.weaponlib.state.ManagedState;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.PermitManager;

import net.minecraft.entity.player.EntityPlayerMP;

public class NetworkPermitManager
implements PermitManager, CompatibleMessageHandler<PermitMessage, CompatibleMessage>  {
	
	private static final Logger logger = LogManager.getLogger(NetworkPermitManager.class);

	private ModContext modContext;
	private Map<UUID, Object /*BiConsumer<Permit<?>, ?>*/> permitCallbacks = new HashMap<>();
	private Map<Class<?>, BiConsumer<Permit<?>, ?>> evaluators = new HashMap<>();
	
	public NetworkPermitManager(ModContext modContext) {
		this.modContext = modContext;
	}
	
	@Override
	public <S extends ManagedState<S>, P extends Permit<S>, E extends ExtendedState<S>> void request(
			P permit, E extendedState, BiConsumer<P, E> callback) {
		
		permitCallbacks.put(permit.getUuid(), callback);
		modContext.getChannel().getChannel().sendToServer(new PermitMessage(permit, extendedState));
	}

	@Override
	public <S extends ManagedState<S>, P extends Permit<S>, E extends ExtendedState<S>> void registerEvaluator(
			Class<? extends P> permitClass,
			Class<? extends E> esClass,
			BiConsumer<P, E> evaluator) {
		evaluators.put(permitClass,  (p, c) -> { 
			logger.debug("Processing permit {} for instance {}", p, c);
			evaluator.accept(permitClass.cast(p), esClass.cast(c)); 
		});
	}
	
	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(PermitMessage permitMessage,
			CompatibleMessageContext ctx) {
		
		Permit<?> permit = permitMessage.getPermit();
		Object extendedState = permitMessage.getContext();
		
		if(ctx.isServerSide()) {
			if(extendedState instanceof PlayerContext) { // TODO: think of something better than upcasting
				((PlayerContext) extendedState).setPlayer(ctx.getPlayer());
			}
			ctx.runInMainThread(() -> {
				//serverAction.accept(permit, context);
				@SuppressWarnings("unchecked")
				BiConsumer<Permit<?>, Object> evaluator = (BiConsumer<Permit<?>, Object>) evaluators.get(permit.getClass());
				if(evaluator != null) {
					evaluator.accept(permit, extendedState);
				}
				PermitMessage message = new PermitMessage(permit, extendedState);
				//System.out.println("Sending out permit");
				modContext.getChannel().getChannel().sendTo(message, (EntityPlayerMP) ctx.getPlayer());
			});
		} else {
			compatibility.runInMainClientThread(() -> {
				if(extendedState instanceof PlayerContext) {
					((PlayerContext) extendedState).setPlayer(compatibility.clientPlayer());
				}
				@SuppressWarnings("unchecked")
				BiConsumer<Permit<?>, Object> callback = (BiConsumer<Permit<?>, Object>) permitCallbacks.remove(permit.getUuid());
				if(callback != null) {
					callback.accept(permit, extendedState);
				}
			});
		}
		
		return null;
	}



	
}
