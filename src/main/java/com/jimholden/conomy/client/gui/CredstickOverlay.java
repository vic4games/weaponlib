package com.jimholden.conomy.client.gui;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.items.OpenDimeBase;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(Side.CLIENT)
public class CredstickOverlay {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {	
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc);
			ItemStack stack = player.getHeldItemMainhand();
			if(stack.getItem() instanceof OpenDimeBase) {
				OpenDimeBase odb = (OpenDimeBase) stack.getItem();
				String key = odb.getKey(stack);
				int keyLen = key.length();
				double credits = odb.getBalance(stack);
				int stringLen = ("MRC" + credits).length();
				mc.fontRenderer.drawString(ChatFormatting.GREEN + "MRC" + credits, (sr.getScaledWidth()-7-(stringLen)*6), (sr.getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-2), 0xFFFFFFFF);
				mc.fontRenderer.drawString(ChatFormatting.YELLOW + key, (sr.getScaledWidth()-7-(keyLen)*6), (sr.getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-10), 0xFFFFFFFF);
				Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);
			}
			
		}
	}

}
