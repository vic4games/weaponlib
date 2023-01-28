package com.jimholden.conomy.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.client.gui.engine.GUItil;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.proxy.ClientProxy;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(Side.CLIENT)
public class LedgerOverlay {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {	
		boolean highQuality = true;
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc);
			ItemStack stack = player.getHeldItemMainhand();
			if(stack.getItem() instanceof LedgerBase) {
				LedgerBase ledger = (LedgerBase) stack.getItem();
				String key = stack.getTagCompound().getString("pkey");
				int keyLen = key.length();
				int credits = stack.getTagCompound().getInteger("balance");
				int stringLen = ("MRC" + credits).length();
				
				
				
				double height = 18;
				double width = 90;
				
				double x = sr.getScaledWidth_double()-width;
				double y = sr.getScaledHeight_double()-height-20;
				
				Color niceScreenGray = new Color(0x2f3640).darker();
				
				Color vert = new Color(0x4cd137);
				Color rouge = new Color(0xe84118);
				
				
				//GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
				GUItil.renderRoundedRectangle(niceScreenGray, 1.0, x, y, x+width, y+height, 2, 15);
			
				GUItil.drawScaledString(ClientProxy.newFontRenderer, key, x+10, y+4.5, 0xffffff, 0.75f);
				GUItil.drawScaledString(ClientProxy.newFontRenderer, "MRC" + credits, x+10, y+10, 0xfbc531, 0.65f);
				if(!ledger.getState(stack)) {
					GUItil.renderCircle(rouge, 1.0, x+5.5, y+7, 3);
				} else {
					GUItil.renderCircle(vert, 1.0, x+5.5, y+7, 3);
				}
				//GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
				
				
				if(!highQuality) {
					mc.fontRenderer.drawString(ChatFormatting.GREEN + "MRC" + credits, (sr.getScaledWidth()-7-(stringLen)*6), (sr.getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-10), 0xFFFFFFFF);
					mc.fontRenderer.drawString(ChatFormatting.YELLOW + key, (sr.getScaledWidth()-7-(keyLen)*6), (sr.getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-18), 0xFFFFFFFF);
					if(ledger.getState(stack)) mc.fontRenderer.drawString(ChatFormatting.GREEN + "ONLINE", (sr.getScaledWidth()-7-("ONLINE".length())*6), (sr.getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-2), 0xFFFFFFFF);
					if(!ledger.getState(stack)) mc.fontRenderer.drawString(ChatFormatting.RED + "OFFLINE", (sr.getScaledWidth()-7-("OFFLINE".length())*6), (sr.getScaledHeight()-mc.fontRenderer.FONT_HEIGHT-2), 0xFFFFFFFF);
					
				}
				Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);
			}
			
		}
	}

}
