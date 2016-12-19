package com.vicmatskiv.weaponlib;

import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponEventHandler {
	
	private SafeGlobals safeGlobals;

	public WeaponEventHandler(SafeGlobals safeGlobals) {
		this.safeGlobals = safeGlobals;
	}
	
	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		safeGlobals.guiOpen.set(event.getGui() != null);
	}
	
	@SubscribeEvent
	public void zoom(FOVUpdateEvent event) {

		ItemStack stack = event.getEntity().getHeldItem(EnumHand.MAIN_HAND);
		if (stack != null) {
			if (stack.getItem() instanceof Weapon) {
				if (stack.getTagCompound() != null) {
					event.setNewfov(Tags.getZoom(stack));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleRenderLivingEvent(RenderLivingEvent.Pre<? extends EntityLivingBase> event) {

		if ((event.isCanceled()) || (!(event.getEntity() instanceof EntityPlayer)))
			return;

		ItemStack itemStack = event.getEntity().getHeldItem(EnumHand.MAIN_HAND);

		if (itemStack != null && itemStack.getItem() instanceof Weapon) {
			RenderPlayer rp = (RenderPlayer) event.getRenderer();

			if (itemStack.getTagCompound() != null) {
				//throw new UnsupportedOperationException("Fix aiming!");
				//rp.getMainModel().aimedBow = Weapon.isAimed(itemStack);
				rp.getMainModel().leftArmPose = ArmPose.BOW_AND_ARROW;
				rp.getMainModel().rightArmPose = ArmPose.BOW_AND_ARROW;
			}
		}
	}

}