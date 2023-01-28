package com.jimholden.conomy.stocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.tutorial.ITutorialStep;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StockNotification
{
	public static void newStockNotification(Minecraft mc, float iPrice, double nPrice, int shares, String symbol) {
		String arrow = "\u2192";
		ITextComponent TITLE = new TextComponentString(TextFormatting.YELLOW + "Closed " + TextFormatting.BOLD + symbol);
	    ITextComponent DESCRIPTION = new TextComponentString(TextFormatting.WHITE + "" + (iPrice*shares) + " " + TextFormatting.BOLD + arrow + TextFormatting.WHITE + " " + (nPrice*shares));
	    StockToast toast = new StockToast(TutorialToast.Icons.WOODEN_PLANKS, TITLE, DESCRIPTION, true, 0.001F);
		mc.getToastGui().add(toast);
		
	}
	
}
