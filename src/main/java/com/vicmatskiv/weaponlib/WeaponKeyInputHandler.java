package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponKeyInputHandler;
import com.vicmatskiv.weaponlib.melee.MeleeState;
import com.vicmatskiv.weaponlib.melee.PlayerMeleeInstance;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WeaponKeyInputHandler extends CompatibleWeaponKeyInputHandler {
	
	@SuppressWarnings("unused")
	private CompatibleChannel channel;
	private Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier;
	private ModContext modContext;
	
	public WeaponKeyInputHandler(
			ModContext modContext,
			Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier, 
			WeaponAttachmentAspect attachmentAspect,
			CompatibleChannel channel) {
		this.modContext = modContext;
		this.entityPlayerSupplier = entityPlayerSupplier;
		this.channel = channel;
	}

	@Override
    public void onCompatibleKeyInput() {
		
		EntityPlayer player = entityPlayerSupplier.apply(null);
    	ItemStack itemStack = compatibility.getHeldItemMainHand(player);
    	
    	if(DebugPositioner.isDebugModeEnabled() && KeyBindings.upArrowKey.isPressed()) {
    	    DebugPositioner.incrementXRotation(5);
    	} else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.downArrowKey.isPressed()) {
    	    DebugPositioner.incrementXRotation(-5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.leftArrowKey.isPressed()) {
            DebugPositioner.incrementYRotation(5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.rightArrowKey.isPressed()) {
            DebugPositioner.incrementYRotation(-5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.jDebugKey.isPressed()) {
            DebugPositioner.incrementZRotation(5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.kDebugKey.isPressed()) {
            DebugPositioner.incrementZRotation(-5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.minusDebugKey.isPressed()) {
            DebugPositioner.incrementXPosition(-0.025f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.equalsDebugKey.isPressed()) {
            DebugPositioner.incrementXPosition(0.025f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.lBracketDebugKey.isPressed()) {
            DebugPositioner.incrementYPosition(-0.025f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.rBracketDebugKey.isPressed()) {
            DebugPositioner.incrementYPosition(0.025f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.semicolonDebugKey.isPressed()) {
            DebugPositioner.incrementZPosition(-0.025f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.apostropheDebugKey.isPressed()) {
            DebugPositioner.incrementZPosition(0.025f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.deleteDebugKey.isPressed()) {
            DebugPositioner.reset();
        }
        
        else if(KeyBindings.reloadKey.isPressed()) {
    		if(itemStack != null) {
    			Item item = itemStack.getItem();
    			if(item instanceof Reloadable) {
    				((Reloadable) item).reloadMainHeldItemForPlayer(player);
    			}
    		}
        }
        
        else if(KeyBindings.laserSwitchKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && (instance.getState() == WeaponState.READY || instance.getState() == WeaponState.MODIFYING)) {
    			instance.setLaserOn(!instance.isLaserOn());
    		}
        }
        
        else if(KeyBindings.attachmentKey.isPressed()) {
    		if(itemStack != null && itemStack.getItem() instanceof Modifiable /* && itemStack.getItem() instanceof Weapon*/) {
    		    ((Modifiable) itemStack.getItem()).toggleClientAttachmentSelectionMode(player);
    		}
        } 
        
        else if(KeyBindings.upArrowKey.isPressed()) {
    		PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.MODIFYING) {
    			modContext.getAttachmentAspect().changeAttachment(AttachmentCategory.SCOPE, instance);
    		}
        } 
        
        else if(KeyBindings.rightArrowKey.isPressed()) {
    		PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player);
    		if(instance instanceof PlayerWeaponInstance && instance.getState() == WeaponState.MODIFYING) {
    			modContext.getAttachmentAspect().changeAttachment(AttachmentCategory.SKIN, (PlayerWeaponInstance) instance);
    		} else if(instance instanceof PlayerMeleeInstance && instance.getState() == MeleeState.MODIFYING) {
                modContext.getMeleeAttachmentAspect().changeAttachment(AttachmentCategory.SKIN, (PlayerMeleeInstance) instance);
            }
        } 
        
        else if(KeyBindings.downArrowKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.MODIFYING) {
    			modContext.getAttachmentAspect().changeAttachment(AttachmentCategory.GRIP, instance);
    		}
        } 
        
        else if(KeyBindings.leftArrowKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.MODIFYING) {
    			modContext.getAttachmentAspect().changeAttachment(AttachmentCategory.SILENCER, instance);
    		}
        }
        
        else if(KeyBindings.fireModeKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.READY) {
    			instance.getWeapon().changeFireMode(instance);
    		}
        }
        
        else if(KeyBindings.addKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.READY) {
    			instance.getWeapon().incrementZoom(instance);
    		}
        }
        
        else if(KeyBindings.subtractKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.READY) {
    			instance.getWeapon().decrementZoom(instance);
    		}
        }
    }
}
