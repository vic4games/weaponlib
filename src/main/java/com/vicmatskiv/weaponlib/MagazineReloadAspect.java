package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.Permit.Status;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MagazineReloadAspect implements Aspect<MagazineState, PlayerMagazineInstance> {

	static {
		TypeRegistry.getInstance().register(LoadPermit.class);
	}

	private static final Set<MagazineState> allowedUpdateFromStates = new HashSet<>(
			Arrays.asList(
					MagazineState.LOAD_REQUESTED,
					MagazineState.LOAD));

	public static class LoadPermit extends Permit<MagazineState> {

		public LoadPermit() {}

		public LoadPermit(MagazineState state) {
			super(state);
		}
	}

	private static long reloadAnimationDuration = 1000;


	private static Predicate<PlayerMagazineInstance> reloadAnimationCompleted = es ->
		System.currentTimeMillis() >= es.getStateUpdateTimestamp() + reloadAnimationDuration; // TODO: read reload animation duration from the state itself

	private ModContext modContext;

	private PermitManager permitManager;

	private StateManager<MagazineState, ? super PlayerMagazineInstance> stateManager;

	private Predicate<PlayerMagazineInstance> notFull = instance -> {
		boolean result = Tags.getAmmo(instance.getItemStack()) < instance.getMagazine().getAmmo();
		return result;
	};

	public MagazineReloadAspect(ModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	public void setStateManager(StateManager<MagazineState, ? super PlayerMagazineInstance> stateManager) {

		if(permitManager == null) {
			throw new IllegalStateException("Permit manager not initialized");
		}

		this.stateManager = stateManager

		.in(this)
			.change(MagazineState.READY).to(MagazineState.LOAD)
			.when(notFull)
			.withPermit((s, es) -> new LoadPermit(s),
					modContext.getPlayerItemInstanceRegistry()::update,
					permitManager)
			.withAction((c, f, t, p) -> doPermittedLoad(c, (LoadPermit)p))
			.manual()

		.in(this)
			.change(MagazineState.LOAD).to(MagazineState.READY)
			.when(reloadAnimationCompleted)
			.automatic()
		;
	}

	@Override
	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		permitManager.registerEvaluator(LoadPermit.class, PlayerMagazineInstance.class, (p, c) -> { evaluateLoad(p, c); });
	}

	public void reloadMainHeldItem(EntityPlayer player) {
		PlayerMagazineInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMagazineInstance.class);
		stateManager.changeState(this, instance, MagazineState.LOAD);
	}

	void updateMainHeldItem(EntityPlayer player) {
		PlayerMagazineInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerMagazineInstance.class);
		if(instance != null) {
			stateManager.changeStateFromAnyOf(this, instance, allowedUpdateFromStates); // no target state specified, will trigger auto-transitions
		}
	}

	private void evaluateLoad(LoadPermit p, PlayerMagazineInstance magazineInstance) {

		ItemStack magazineStack = magazineInstance.getItemStack();

		Status status = Status.DENIED;
		if(magazineStack.getItem() instanceof ItemMagazine) {
			ItemStack magazineItemStack = magazineStack;
			ItemMagazine magazine = (ItemMagazine) magazineItemStack.getItem();
			List<ItemBullet> compatibleBullets = magazine.getCompatibleBullets();
			int currentAmmo = Tags.getAmmo(magazineStack);
			ItemStack consumedStack;
			if((consumedStack = compatibility.tryConsumingCompatibleItem(compatibleBullets, magazine.getAmmo() - currentAmmo, magazineInstance.getPlayer())) != null) {
				Tags.setAmmo(magazineStack, Tags.getAmmo(magazineStack) + compatibility.getStackSize(consumedStack));
				if(magazine.getReloadSound() != null) {
					compatibility.playSound(magazineInstance.getPlayer(), magazine.getReloadSound(), 1.0F, 1.0F);
				}
				status = Status.GRANTED;
			}
		}

		p.setStatus(status);
	}

	private void doPermittedLoad(PlayerMagazineInstance weaponInstance, LoadPermit permit) {
		if(permit == null) {
			System.err.println("Permit is null, something went wrong");
			return;
		}
//		if(permit.getStatus() == Status.GRANTED) {
//			compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getReloadSound(), 1.0F, 1.0F);
//		}
	}
}
