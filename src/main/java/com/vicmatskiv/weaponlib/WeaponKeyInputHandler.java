package com.vicmatskiv.weaponlib;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class WeaponKeyInputHandler {
	
	private CompatibleChannel channel;
	private Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier;
	private AttachmentManager attachmentManager;
	private ReloadManager reloadManager;
	
	public WeaponKeyInputHandler(Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier, 
			AttachmentManager attachmentManager,
			ReloadManager reloadManager,
			CompatibleChannel channel) {
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
        	channel.getChannel().sendToServer(new LaserSwitchMessage()); 
        }
        
        else if(KeyBindings.attachmentKey.isPressed()) {
        	EntityPlayer player = entityPlayerSupplier.apply(null);
    		ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
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
