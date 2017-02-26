package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponKeyInputHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WeaponKeyInputHandler extends CompatibleWeaponKeyInputHandler {
	
	private CompatibleChannel channel;
	private Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier;
	private WeaponAttachmentAspect attachmentAspect;
	//private WeaponReloadAspect reloadAspect;
	private ModContext modContext;
	
	public WeaponKeyInputHandler(
			ModContext modContext,
			Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier, 
			WeaponAttachmentAspect attachmentAspect,
			//WeaponReloadAspect reloadAspect,
			CompatibleChannel channel) {
		this.modContext = modContext;
		this.entityPlayerSupplier = entityPlayerSupplier;
		this.attachmentAspect = attachmentAspect;
		//this.reloadAspect = reloadAspect;
		this.channel = channel;
	}

	@Override
    public void onCompatibleKeyInput() {
		
		EntityPlayer player = entityPlayerSupplier.apply(null);
    	ItemStack itemStack = compatibility.getHeldItemMainHand(player);
    	
        if(KeyBindings.reloadKey.isPressed()) {
    		if(itemStack != null) {
//    			reloadManager.toggleReload(itemStack, player);
    			Item item = itemStack.getItem();
    			if(item instanceof Reloadable) {
    				((Reloadable) item).reloadMainHeldItemForPlayer(player);
    			}
    		}
        }
        
        else if(KeyBindings.laserSwitchKey.isPressed()) {
        	channel.getChannel().sendToServer(new LaserSwitchMessage()); 
        }
        
        else if(KeyBindings.attachmentKey.isPressed()) {
    		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
//    			attachmentManager.toggleClientAttachmentSelectionMode(itemStack, player);
    			Item item = itemStack.getItem();
    			if(item instanceof Modifiable) {
    				System.out.println("Attachment mode toggled at " + System.currentTimeMillis());
    				((Modifiable) item).toggleClientAttachmentSelectionMode(player);
    			}
    		}
        } 
        
        else if(KeyBindings.upArrowKey.isPressed()) {
        	// TODO: this needs to be handled entirely by attachment aspect
    		PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.MODIFYING) {
    			//channel.getChannel().sendToServer(new ChangeAttachmentMessage(AttachmentCategory.SCOPE));
    			modContext.getAttachmentAspect().changeAttachment(AttachmentCategory.SCOPE, instance);
    		}
        	
        } 
        
        else if(KeyBindings.rightArrowKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.MODIFYING) {
    			channel.getChannel().sendToServer(new ChangeTextureMessage()); 
    		}
        } 
        
        else if(KeyBindings.downArrowKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.MODIFYING) {
    			channel.getChannel().sendToServer(new ChangeAttachmentMessage(AttachmentCategory.GRIP)); 
    		}
        } 
        
        else if(KeyBindings.leftArrowKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.MODIFYING) {
    			channel.getChannel().sendToServer(new ChangeAttachmentMessage(AttachmentCategory.SILENCER)); 
    		}
        }
        
        else if(KeyBindings.fireModeKey.isPressed()) {
        	PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
    		if(instance != null && instance.getState() == WeaponState.READY) {
    			instance.getWeapon().changeFireMode(instance);
    		}
        }
    }
}
