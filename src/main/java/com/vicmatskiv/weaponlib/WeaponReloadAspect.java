package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.state.Aspect;
import com.vicmatskiv.weaponlib.state.Permit;
import com.vicmatskiv.weaponlib.state.Permit.Status;
import com.vicmatskiv.weaponlib.state.PermitManager;
import com.vicmatskiv.weaponlib.state.StateManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class WeaponReloadAspect implements Aspect<WeaponState, PlayerWeaponInstance> {

	private static final Logger logger = LogManager.getLogger(WeaponReloadAspect.class);

	private static final long ALERT_TIMEOUT = 500;
	private static final long INSPECT_TIMEOUT = 500;
	
	private static final long UNLOAD_TIMEOUT = 1000;
	private static final long AWAIT_FURTHER_LOAD_INSTRUCTIONS_TIMEOUT = 400;

	static {
		TypeRegistry.getInstance().register(UnloadPermit.class);
		TypeRegistry.getInstance().register(LoadPermit.class);
		TypeRegistry.getInstance().register(PlayerWeaponInstance.class); // TODO: move it out
	}

	private static final Set<WeaponState> allowedUpdateFromStates = new HashSet<>(
			Arrays.asList(
			        WeaponState.AWAIT_FURTHER_LOAD_INSTRUCTIONS,
					WeaponState.LOAD_REQUESTED,
					WeaponState.LOAD,
					WeaponState.LOAD_ITERATION,
					WeaponState.LOAD_ITERATION_COMPLETED,
					WeaponState.ALL_LOAD_ITERATIONS_COMPLETED,
					WeaponState.UNLOAD_PREPARING,
					WeaponState.UNLOAD_REQUESTED,
					WeaponState.UNLOAD,
					WeaponState.ALERT,
					WeaponState.INSPECTING,
					WeaponState.DRAWING));

	public static class UnloadPermit extends Permit<WeaponState> {

		public UnloadPermit() {}

		public UnloadPermit(WeaponState state) {
			super(state);
		}
	}

	public static class LoadPermit extends Permit<WeaponState> {

		public LoadPermit() {}

		public LoadPermit(WeaponState state) {
			super(state);
		}
	}

	@SuppressWarnings("unused")
    private static Predicate<PlayerWeaponInstance> sprinting = instance -> instance.getPlayer().isSprinting();
    
    private static Predicate<PlayerWeaponInstance> hasNextLoadIteration = 
            weaponInstance -> weaponInstance.getWeapon().hasIteratedLoad() 
            && weaponInstance.getLoadIterationCount() > 0;

	private static Predicate<PlayerWeaponInstance> supportsDirectBulletLoad =
			weaponInstance -> weaponInstance.getWeapon().getAmmoCapacity() > 0;

	private static Predicate<PlayerWeaponInstance> magazineAttached =
			weaponInstance -> WeaponAttachmentAspect.getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance) != null;

	private static Predicate<PlayerWeaponInstance> loadIterationCompleted = weaponInstance ->
	        System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp()
	            + Math.max(weaponInstance.getWeapon().builder.loadIterationTimeout,
	                    weaponInstance.getWeapon().getTotalLoadIterationDuration() + 250);
	        
    private static Predicate<PlayerWeaponInstance> allLoadIterationsCompleted = weaponInstance ->
        System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp()
            + weaponInstance.getWeapon().getAllLoadIterationAnimationsCompletedDuration(); 
	        
	private static Predicate<PlayerWeaponInstance> reloadAnimationCompleted = weaponInstance ->
		System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp()
			+ Math.max(weaponInstance.getWeapon().builder.reloadingTimeout,
					weaponInstance.getWeapon().getTotalReloadingDuration() * 1.1);
	
	private static Predicate<PlayerWeaponInstance> unloadTimeoutExpired = weaponInstance ->
	    System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp() + UNLOAD_TIMEOUT;
	    
	private static Predicate<PlayerWeaponInstance> awaitFurtherLoadInstructionCompleted = weaponInstance ->
        System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp() + AWAIT_FURTHER_LOAD_INSTRUCTIONS_TIMEOUT;
		
    private static Predicate<PlayerWeaponInstance> loadAfterUnloadEnabled = PlayerWeaponInstance::isLoadAfterUnloadEnabled;
		
	private static Predicate<PlayerWeaponInstance> unloadAnimationCompleted = weaponInstance ->
		System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp()
			+ weaponInstance.getWeapon().getTotalUnloadingDuration() * 1.1;
		
	private static Predicate<PlayerWeaponInstance> prepareFirstLoadIterationAnimationCompleted = weaponInstance ->
        System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp()
            + weaponInstance.getWeapon().getPrepareFirstLoadIterationAnimationDuration() * 1.1;	
		

	private Predicate<PlayerWeaponInstance> inventoryHasFreeSlots = weaponInstance ->
	    weaponInstance.getPlayer() instanceof EntityPlayer
	    && compatibility.inventoryHasFreeSlots((EntityPlayer)weaponInstance.getPlayer());

	private static Predicate<PlayerWeaponInstance> alertTimeoutExpired = instance ->
		System.currentTimeMillis() >= ALERT_TIMEOUT + instance.getStateUpdateTimestamp();
		
	private static Predicate<PlayerWeaponInstance> inspectTimeoutExpired = instance ->
	    System.currentTimeMillis() >= INSPECT_TIMEOUT + instance.getStateUpdateTimestamp();
        
    private static Predicate<PlayerWeaponInstance> drawingAnimationCompleted = weaponInstance ->
        System.currentTimeMillis() >= weaponInstance.getStateUpdateTimestamp()
            + weaponInstance.getWeapon().getTotalDrawingDuration() * 1.1;

	private ModContext modContext;

	private PermitManager permitManager;

	private StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager;
	
	public WeaponReloadAspect(ModContext modContext) {
		this.modContext = modContext;
	}

	@Override
	public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager) {

		if(permitManager == null) {
			throw new IllegalStateException("Permit manager not initialized");
		}

		this.stateManager = stateManager
		        
		.in(this)
	         .change(WeaponState.READY).to(WeaponState.AWAIT_FURTHER_LOAD_INSTRUCTIONS)
	         .manual()
	         
	    .in(this)
             .change(WeaponState.AWAIT_FURTHER_LOAD_INSTRUCTIONS).to(WeaponState.READY)
             .when(awaitFurtherLoadInstructionCompleted)
             .withAction(this::noFurtherLoadInstructionsReceived)
             .automatic()
             
        .in(this)
             .change(WeaponState.AWAIT_FURTHER_LOAD_INSTRUCTIONS).to(WeaponState.READY)
             .withAction(this::furtherLoadInstructionsReceived)
             .manual()
            
		.in(this)
			.change(WeaponState.READY).to(WeaponState.LOAD)
			.when(supportsDirectBulletLoad.or(magazineAttached.negate()))
			.withPermit((s, es) -> new LoadPermit(s),
					modContext.getPlayerItemInstanceRegistry()::update,
					permitManager)
			.withAction((c, f, t, p) -> completeClientLoad(c, (LoadPermit)p))
			.manual()
			
	     .in(this)
            .change(WeaponState.UNLOAD).to(WeaponState.LOAD)
            .when(loadAfterUnloadEnabled.and(supportsDirectBulletLoad.or(magazineAttached.negate())))
            .withPermit((s, es) -> new LoadPermit(s),
                    modContext.getPlayerItemInstanceRegistry()::update,
                    permitManager)
            .withAction((c, f, t, p) -> completeClientLoad(c, (LoadPermit)p))
            .manual()
			
	    .in(this)
            .change(WeaponState.LOAD).to(WeaponState.READY)
            .when(reloadAnimationCompleted.and(hasNextLoadIteration.negate()))
            .automatic()
            
		.in(this)
            .change(WeaponState.LOAD).to(WeaponState.LOAD_ITERATION)
            .when(hasNextLoadIteration.and(prepareFirstLoadIterationAnimationCompleted))
            .withAction(this::startLoadIteration)
            .automatic()
            
        .in(this)
            .change(WeaponState.LOAD_ITERATION).to(WeaponState.LOAD_ITERATION_COMPLETED)
            .when(loadIterationCompleted)
            .withAction(this::completeLoadIteration)
            .automatic()
            
        .in(this)
            .change(WeaponState.LOAD_ITERATION_COMPLETED).to(WeaponState.LOAD_ITERATION)
            .when(hasNextLoadIteration)
            .withAction(this::startLoadIteration)
            .automatic()
            
        .in(this)
            .change(WeaponState.LOAD_ITERATION_COMPLETED).to(WeaponState.ALL_LOAD_ITERATIONS_COMPLETED)
            .when(hasNextLoadIteration.negate())
            .automatic()
            
        .in(this)
            .change(WeaponState.ALL_LOAD_ITERATIONS_COMPLETED).to(WeaponState.READY)
            .when(allLoadIterationsCompleted)
            .withAction(this::completeAllLoadIterations)
            .automatic()

		.in(this)
			.prepare((c, f, t) -> { prepareUnload(c); }, unloadAnimationCompleted)
			.change(WeaponState.READY).to(WeaponState.UNLOAD)
			.when(magazineAttached.and(inventoryHasFreeSlots))
			.withPermit((s, c) -> new UnloadPermit(s),
					modContext.getPlayerItemInstanceRegistry()::update,
					permitManager)
			.withAction((c, f, t, p) -> completeClientUnload(c, (UnloadPermit)p))
			.manual()

		.in(this)
			.change(WeaponState.UNLOAD).to(WeaponState.READY)
		    .when(loadAfterUnloadEnabled.negate().or(unloadTimeoutExpired))
			.automatic()

		.in(this)
			.change(WeaponState.READY).to(WeaponState.ALERT)
			.when(inventoryHasFreeSlots.negate())
			.withAction(this::inventoryFullAlert)
			.manual()

		.in(this).change(WeaponState.ALERT).to(WeaponState.READY)
			.when(alertTimeoutExpired)
			.automatic() 
			
	    .in(this)
            .change(WeaponState.READY).to(WeaponState.INSPECTING)
            .withAction(this::inspect)
            .manual()
		
		.in(this)
		    .change(WeaponState.INSPECTING).to(WeaponState.READY)
            .when(inspectTimeoutExpired)
            .automatic() 
            
        .in(this)
            .change(WeaponState.READY).to(WeaponState.DRAWING)
            .withAction(this::draw)
            .manual()
            
        .in(this)
            .change(WeaponState.DRAWING).to(WeaponState.READY)
            .when(drawingAnimationCompleted)
            .automatic()
        ;
	}

	@Override
	public void setPermitManager(PermitManager permitManager) {
		this.permitManager = permitManager;
		permitManager.registerEvaluator(LoadPermit.class, PlayerWeaponInstance.class, (p, c) -> { processLoadPermit(p, c); });
		permitManager.registerEvaluator(UnloadPermit.class, PlayerWeaponInstance.class, (p, c) -> { processUnloadPermit(p, c); });
	}

	public void reloadMainHeldItem(EntityPlayer player) {
		PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
		if(instance != null) {
//			stateManager.changeState(this, instance, WeaponState.LOAD, WeaponState.ALERT);
		    stateManager.changeState(this, instance, WeaponState.AWAIT_FURTHER_LOAD_INSTRUCTIONS, WeaponState.READY);
		}
	}
	
	public void unloadMainHeldItem(EntityPlayer player) {
        PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if(instance != null) {
            instance.setLoadAfterUnloadEnabled(false);
            stateManager.changeState(this, instance, WeaponState.UNLOAD, WeaponState.ALERT);
        }
    }

	void updateMainHeldItem(EntityPlayer player) {
		PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
		if(instance != null) {
			stateManager.changeStateFromAnyOf(this, instance, allowedUpdateFromStates); // no target state specified, will trigger auto-transitions
		}
	}
	
	public void inspectMainHeldItem(EntityPlayer player) {
        PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if(instance != null) {
            stateManager.changeState(this, instance, WeaponState.INSPECTING);
        }
    }
	
	public void drawMainHeldItem(EntityPlayer player) {
        PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if(instance != null) {
            stateManager.changeState(this, instance, WeaponState.DRAWING);
        }
    }

	@SuppressWarnings("unchecked")
	private void processLoadPermit(LoadPermit p, PlayerWeaponInstance weaponInstance) {
		logger.debug("Processing load permit on server for {}", weaponInstance);

		ItemStack weaponItemStack = weaponInstance.getItemStack();

		if(weaponItemStack == null || !(weaponInstance.getPlayer() instanceof EntityPlayer)) {
			// Since reload request was sent for an item, the item was removed from the original slot
		    // Also if instance is not owner by entity player , do not allow load
			return;
		}
		EntityPlayer player = (EntityPlayer) weaponInstance.getPlayer();
		Status status = Status.GRANTED;
		weaponInstance.setLoadIterationCount(0); // TODO: review if this is really necessary
		Weapon weapon = (Weapon) weaponInstance.getItem();
		if(compatibility.getTagCompound(weaponItemStack) == null) {
		    compatibility.setTagCompound(weaponItemStack, new NBTTagCompound());
		}
		//if (!player.isSprinting()) {
		List<ItemMagazine> compatibleMagazines = weapon.getCompatibleMagazines();
		List<ItemAttachment<Weapon>> compatibleBullets = weapon.getCompatibleAttachments(ItemBullet.class);
		ItemStack consumedStack;
		if(!compatibleMagazines.isEmpty()) {
		    ItemAttachment<Weapon> existingMagazine = WeaponAttachmentAspect.getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance);
		    int ammo = Tags.getAmmo(weaponItemStack);
		    if(existingMagazine == null) {
		        ammo = 0;
		        ItemStack magazineItemStack = compatibility.tryConsumingCompatibleItem(compatibleMagazines,
		                (stack1, stack2) -> Integer.compare(Tags.getAmmo(stack1), Tags.getAmmo(stack2)), player);
		        if(magazineItemStack != null) {
		            ammo = Tags.getAmmo(magazineItemStack);
		            Tags.setAmmo(weaponItemStack, ammo);
		            logger.debug("Setting server side ammo for {} to {}", weaponInstance, ammo);
		            WeaponAttachmentAspect.addAttachment((ItemAttachment<Weapon>) magazineItemStack.getItem(), weaponInstance);
		            compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
		        } else {
		            status = Status.DENIED;
		        }
		    }
		    // Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ReloadMessage.Type.LOAD, newMagazine, ammo), (EntityPlayerMP) player);
		    weaponInstance.setAmmo(ammo);
		} else if(!compatibleBullets.isEmpty() && (consumedStack = compatibility.tryConsumingCompatibleItem(compatibleBullets,
		        Math.min(weapon.getMaxBulletsPerReload(), weapon.getAmmoCapacity() - weaponInstance.getAmmo()), player, i -> true)) != null) {
		    int ammo = weaponInstance.getAmmo() + compatibility.getStackSize(consumedStack);
		    Tags.setAmmo(weaponItemStack, ammo);
		    // Update permit instead modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, ammo), (EntityPlayerMP) player);
		    weaponInstance.setAmmo(ammo);
		    if(weapon.hasIteratedLoad()) {
		        weaponInstance.setLoadIterationCount(compatibility.getStackSize(consumedStack));
		    }
		    compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
		} else if (compatibility.consumeInventoryItem(player.inventory, weapon.builder.ammo)) {
		    Tags.setAmmo(weaponItemStack, weapon.builder.ammoCapacity);
		    // Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, weapon.builder.ammoCapacity), (EntityPlayerMP) player);
		    weaponInstance.setAmmo(weapon.builder.ammoCapacity);
		    compatibility.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
		} else {
		    logger.debug("No suitable ammo found for {}. Permit denied.", weaponInstance);
		    //				Tags.setAmmo(weaponItemStack, 0);
		    //				weaponInstance.setAmmo(0);
		    status = Status.DENIED;
		    // Update permit instead: modContext.getChannel().getChannel().sendTo(new ReloadMessage(weapon, 0), (EntityPlayerMP) player);
		}
		/*} else {
		    status = Status.DENIED;
		}*/

//		Tags.setInstance(weaponItemStack, weaponInstance);

		p.setStatus(status);
	}

	private void prepareUnload(PlayerWeaponInstance weaponInstance) {
		compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getUnloadSound(), 1.0F, 1.0F);
	}

	private void processUnloadPermit(UnloadPermit p, PlayerWeaponInstance weaponInstance) {
		logger.debug("Processing unload permit on server for {}", weaponInstance);
		if(!(weaponInstance.getPlayer() instanceof EntityPlayer)) {
		    logger.warn("Not a player");
            return;
        }
		ItemStack weaponItemStack = weaponInstance.getItemStack();
		
		EntityPlayer player = (EntityPlayer) weaponInstance.getPlayer();

		Weapon weapon = (Weapon) weaponItemStack.getItem();
		if (compatibility.getTagCompound(weaponItemStack) != null /* && !player.isSprinting()*/) {
			ItemAttachment<Weapon> attachment = modContext.getAttachmentAspect().removeAttachment(AttachmentCategory.MAGAZINE, weaponInstance);
			
			if(attachment == null) {
			    // Attachment can be null if it's in use and cannot be removed
			    p.setStatus(Status.DENIED);
			    return;
			} 
			
			if(attachment instanceof ItemMagazine) {
				ItemStack attachmentItemStack = ((ItemMagazine) attachment).createItemStack();
				Tags.setAmmo(attachmentItemStack, weaponInstance.getAmmo());

				if(!player.inventory.addItemStackToInventory(attachmentItemStack)) {
					logger.error("Cannot add attachment " + attachment + " for " + weaponInstance + "back to the inventory");
				}
			} else {
				//throw new IllegalStateException();
			}

			Tags.setAmmo(weaponItemStack, 0);
			weaponInstance.setAmmo(0);
			compatibility.playSoundToNearExcept(player, weapon.getUnloadSound(), 1.0F, 1.0F);

			p.setStatus(Status.GRANTED);
		} else {
			p.setStatus(Status.DENIED);
		}

//		Tags.setInstance(weaponItemStack, weaponInstance); // to sync immediately without waiting for tick sync
		p.setStatus(Status.GRANTED);
	}

	private void completeClientLoad(PlayerWeaponInstance weaponInstance, LoadPermit permit) {
	    weaponInstance.setLoadAfterUnloadEnabled(false);
		if(permit == null) {
			logger.error("Permit is null, something went wrong");
			return;
		}
		
		if(permit.getStatus() == Status.GRANTED) {
			compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getReloadSound(), 1.0F, 1.0F);
		}
	}

	private void completeClientUnload(PlayerWeaponInstance weaponInstance, UnloadPermit p) {
	    if(weaponInstance.isLoadAfterUnloadEnabled()) {
	        stateManager.changeState(this, weaponInstance, WeaponState.LOAD, WeaponState.ALERT);
	        weaponInstance.setLoadAfterUnloadEnabled(false);
	    }
	}

	public void inventoryFullAlert(PlayerWeaponInstance weaponInstance) {
		modContext.getStatusMessageCenter().addAlertMessage(compatibility.getLocalizedString("gui.inventoryFull"), 3, 250, 200);
	}
	
	public void inspect(PlayerWeaponInstance weaponInstance) {
        compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getInspectSound(), 1.0F, 1.0F);
    }
	
	public void draw(PlayerWeaponInstance weaponInstance) {
	    compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getDrawSound(), 1.0F, 1.0F);
	}
	
	public void startLoadIteration(PlayerWeaponInstance weaponInstance) {
        compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getReloadIterationSound(), 1.0F, 1.0F);
    }
	
	public void completeLoadIteration(PlayerWeaponInstance weaponInstance) {
        weaponInstance.setLoadIterationCount(weaponInstance.getLoadIterationCount() - 1);
    }
	
	public void completeAllLoadIterations(PlayerWeaponInstance weaponInstance) {
        compatibility.playSound(weaponInstance.getPlayer(), weaponInstance.getWeapon().getAllReloadIterationsCompletedSound(), 1.0F, 1.0F);
    }
	
	public void noFurtherLoadInstructionsReceived(PlayerWeaponInstance weaponInstance) {
	    //System.out.println("-------No further load instructions received");
	    stateManager.changeState(this, weaponInstance, WeaponState.LOAD, WeaponState.ALERT);
	}
	
	public void furtherLoadInstructionsReceived(PlayerWeaponInstance weaponInstance) {
        //System.out.println("\nFurther load instructions received!\n");
        weaponInstance.setLoadAfterUnloadEnabled(true);
        stateManager.changeState(this, weaponInstance, WeaponState.UNLOAD, WeaponState.LOAD, WeaponState.ALERT);
    }
	
}