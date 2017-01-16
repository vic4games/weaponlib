package com.vicmatskiv.weaponlib;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class WeaponKeyInputHandler {
	
	private SimpleNetworkWrapper channel;
	private Function<MessageContext, EntityPlayer> entityPlayerSupplier;
	private AttachmentManager attachmentManager;
	private ReloadManager reloadManager;
	
	public WeaponKeyInputHandler(Function<MessageContext, EntityPlayer> entityPlayerSupplier, 
			AttachmentManager attachmentManager,
			ReloadManager reloadManager,
			SimpleNetworkWrapper channel) {
		this.entityPlayerSupplier = entityPlayerSupplier;
		this.attachmentManager = attachmentManager;
		this.reloadManager = reloadManager;
		this.channel = channel;
	}

	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
		
        if(KeyBindings.reloadKey.isPressed()) {
        	EntityPlayer player = entityPlayerSupplier.apply(null);
        	ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);   		
    		if(itemStack != null) {
    			reloadManager.toggleReload(itemStack, player);
    		}
        }
        
        else if(KeyBindings.laserSwitchKey.isPressed()) {
        	channel.sendToServer(new LaserSwitchMessage()); 
        }
        
        else if(KeyBindings.attachmentKey.isPressed()) {
        	EntityPlayer player = entityPlayerSupplier.apply(null);
    		ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
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
