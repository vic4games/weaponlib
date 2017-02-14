package com.vicmatskiv.weaponlib.network;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerItemContext;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponClientStorage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.PermitManager;

import net.minecraft.entity.player.EntityPlayerMP;

public class NetworkPermitManager<Context extends PlayerItemContext> implements PermitManager<Context>, CompatibleMessageHandler<PermitMessage<Context>, CompatibleMessage>  {
	
	private ModContext modContext;
	private Map<UUID, BiConsumer<Permit, Context>> permitCallbacks = new HashMap<>();
	private Map<Class<?>, BiConsumer<Permit, Context>> evaluators = new HashMap<>();

	public NetworkPermitManager(ModContext modContext) {
		this.modContext = modContext;
	}
	
	@Override
	public <T extends Permit> void request(T permit, Context context, BiConsumer<Permit, Context> callback) {
		permitCallbacks.put(permit.getUuid(), callback);
		modContext.getChannel().getChannel().sendToServer(new PermitMessage<>(permit, context));
	}

	@Override
	public <T extends Permit> void registerEvaluator(Class<T> permitClass, BiConsumer<T, Context> evaluator) {
		evaluators.put(permitClass,  (p, c) -> { evaluator.accept(permitClass.cast(p), c); });
	}
	
	@Override
	public <T extends CompatibleMessage> T onCompatibleMessage(PermitMessage<Context> permitMessage,
			CompatibleMessageContext ctx) {
		
		Permit permit = permitMessage.getPermit();
		Context context = permitMessage.getContext();
		
		if(ctx.isServerSide()) {
			context.setPlayer(ctx.getPlayer());
			//((Object) context).setPermit(permit); TODO: set permit in context somehow
			ctx.runInMainThread(() -> {
				//serverAction.accept(permit, context);
				BiConsumer<Permit, Context> evaluator = evaluators.get(permit.getClass());
				if(evaluator != null) {
					evaluator.accept(permit, context);
				}
				modContext.getChannel().getChannel().sendTo(new PermitMessage<>(permit, context), (EntityPlayerMP) ctx.getPlayer());
			});
		} else {
			compatibility.runInMainClientThread(() -> {
				context.setPlayer(compatibility.clientPlayer());
				
				// This needs to be redesigned, because this class should not have any knowledge about CommonWeaponAspectContext
				WeaponClientStorage container = modContext.getWeaponClientStorageManager().getWeaponClientStorage(context.getPlayer(), 
						(Weapon) ((PlayerItemContext)context).getItem());
				((PlayerItemContext)context).setManagedStateContainer(container);
				
				
				BiConsumer<Permit, Context> callback = permitCallbacks.remove(permit.getUuid());
				if(callback != null) {
					callback.accept(permit, context);
				}
			});
		}
		
		return null;
	}




}
