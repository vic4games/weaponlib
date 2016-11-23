package com.vicmatskiv.weaponlib;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

public class WeaponEventHandler {
	
	private SafeGlobals safeGlobals;

	public WeaponEventHandler(SafeGlobals safeGlobals) {
		this.safeGlobals = safeGlobals;
	}
	
	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		safeGlobals.guiOpen.set(event.gui != null);
	}
	
	@SubscribeEvent
	public void zoom(FOVUpdateEvent event) {

		ItemStack stack = event.entity.getHeldItem();
		if (stack != null) {
			if (stack.getItem() instanceof Weapon) {
				if (stack.stackTagCompound != null) {
					event.newfov = Weapon.getZoom(stack);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleRenderLivingEvent(RenderLivingEvent.Pre event) {

		if ((event.isCanceled()) || (!(event.entity instanceof EntityPlayer)))
			return;

		ItemStack itemStack = event.entity.getHeldItem();

		if (itemStack != null && itemStack.getItem() instanceof Weapon) {
			RenderPlayer rp = (RenderPlayer) event.renderer;

			if (itemStack.stackTagCompound != null) {
				rp.modelBipedMain.aimedBow = Weapon.isAimed(itemStack); //itemStack.stackTagCompound.getBoolean("Aimed");
			}
		}
	}

}