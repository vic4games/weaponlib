package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.ReloadAspect.ReloadContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponKeyInputHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class WeaponKeyInputHandler extends CompatibleWeaponKeyInputHandler {
	
	private CompatibleChannel channel;
	private Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier;
	private AttachmentManager attachmentManager;
	private ReloadManager reloadManager;
	private ReloadAspect reloadAspect;
	
	public WeaponKeyInputHandler(Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier, 
			AttachmentManager attachmentManager,
			ReloadManager reloadManager,
			ReloadAspect reloadAspect,
			CompatibleChannel channel) {
		this.entityPlayerSupplier = entityPlayerSupplier;
		this.attachmentManager = attachmentManager;
		this.reloadManager = reloadManager;
		this.reloadAspect = reloadAspect;
		this.channel = channel;
	}

	@Override
    public void onCompatibleKeyInput() {
		
        if(KeyBindings.reloadKey.isPressed()) {
        	EntityPlayer player = entityPlayerSupplier.apply(null);
        	ItemStack itemStack = compatibility.getHeldItemMainHand(player);
    		if(itemStack != null) {
//    			reloadManager.toggleReload(itemStack, player);
    			ReloadContext reloadContext = reloadAspect.contextForPlayer(player);
    			if(reloadContext != null) {
    				reloadAspect.onReloadAction(reloadContext);
    			}
    			
    		}
        }
        
        else if(KeyBindings.laserSwitchKey.isPressed()) {
        	channel.getChannel().sendToServer(new LaserSwitchMessage()); 
        }
        
        else if(KeyBindings.attachmentKey.isPressed()) {
        	EntityPlayer player = entityPlayerSupplier.apply(null);
    		ItemStack itemStack = compatibility.getHeldItemMainHand(player);
    		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
    			attachmentManager.toggleClientAttachmentSelectionMode(itemStack, player);
    		}
        } 
        
        else if(KeyBindings.upArrowKey.isPressed()) {
        	channel.getChannel().sendToServer(new ChangeAttachmentMessage(AttachmentCategory.SCOPE)); 
        } 
        
        else if(KeyBindings.rightArrowKey.isPressed()) {
        	channel.getChannel().sendToServer(new ChangeTextureMessage()); 
        } 
        
        else if(KeyBindings.downArrowKey.isPressed()) {
        	channel.getChannel().sendToServer(new ChangeAttachmentMessage(AttachmentCategory.GRIP)); 
        } 
        
        else if(KeyBindings.leftArrowKey.isPressed()) {
        	channel.getChannel().sendToServer(new ChangeAttachmentMessage(AttachmentCategory.SILENCER)); 
        }
    }
}
