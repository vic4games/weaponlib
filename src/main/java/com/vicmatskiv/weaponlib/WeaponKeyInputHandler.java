package com.vicmatskiv.weaponlib;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class WeaponKeyInputHandler {
	
	private SimpleNetworkWrapper channel;
	private Function<MessageContext, EntityPlayer> entityPlayerSupplier;
	private AttachmentManager attachmentManager;
	
	public WeaponKeyInputHandler(Function<MessageContext, EntityPlayer> entityPlayerSupplier, 
			AttachmentManager attachmentManager,
			SimpleNetworkWrapper channel) {
		this.entityPlayerSupplier = entityPlayerSupplier;
		this.attachmentManager = attachmentManager;
		this.channel = channel;
	}

	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
		
        if(KeyBindings.reloadKey.isPressed()) {
        	//channel.sendToServer(new ReloadMessage());
        	EntityPlayer player = entityPlayerSupplier.apply(null);
        	ItemStack itemStack = player.getHeldItem();    		
    		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
    			((Weapon) itemStack.getItem()).initiateReload(itemStack, player);
    		}
        }
        
        else if(KeyBindings.laserSwitchKey.isPressed()) {
        	channel.sendToServer(new LaserSwitchMessage()); 
        }
        
        else if(KeyBindings.attachmentKey.isPressed()) {
        	EntityPlayer player = entityPlayerSupplier.apply(null);
    		ItemStack itemStack = player.getHeldItem();
    		if(itemStack != null && itemStack.getItem() instanceof Weapon) {
    			attachmentManager.toggleClientAttachmentSelectionMode(itemStack, player);
    		}
        } 
        
        else if(KeyBindings.upArrowKey.isPressed()) {
        	channel.sendToServer(new ChangeAttachmentMessage(AttachmentCategory.SCOPE)); 
        } 
        
        else if(KeyBindings.rightArrowKey.isPressed()) {
        	channel.sendToServer(new ChangeTextureMessage()); 
        } 
        
        else if(KeyBindings.downArrowKey.isPressed()) {
        	channel.sendToServer(new ChangeAttachmentMessage(AttachmentCategory.GRIP)); 
        } 
        
        else if(KeyBindings.leftArrowKey.isPressed()) {
        	channel.sendToServer(new ChangeAttachmentMessage(AttachmentCategory.SILENCER)); 
        }
    }
}
