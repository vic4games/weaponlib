package com.vicmatskiv.weaponlib;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeaponEventHandler {

	static final float DEFAULT_ZOOM = 0.75f;

	static int tmp = 0;
	
	private SafeGlobals safeGlobals;

	public WeaponEventHandler(SafeGlobals safeGlobals) {
		this.safeGlobals = safeGlobals;
	}
	
	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		//System.out.println("Gui event: " + event.gui);
		safeGlobals.guiOpen.set(event.gui != null);
	}
	

	@SubscribeEvent
	public void zoom(FOVUpdateEvent event) {
		// System.out.println("Using item " + event.entity.getHeldItem());

		ItemStack stack = event.entity.getHeldItem();
		if (stack != null) {
			if (stack.getItem() instanceof Weapon) {
				if (stack.stackTagCompound != null) {
					float zoom = stack.stackTagCompound
							.getFloat(Weapon.ZOOM_TAG);
					event.newfov = zoom;
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
				rp.modelBipedMain.aimedBow = itemStack.stackTagCompound
						.getBoolean("Aimed");
			}
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event) {
		//System.out.println("Interacting with " + event.entity);
	}
}