package com.jimholden.conomy.client.gui.button;

import com.jimholden.conomy.sound.LootingSound;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;

public class ChemButton extends GuiButtonImage {

	
	public ChemButton(int p_i47392_1_, int p_i47392_2_, int p_i47392_3_, int p_i47392_4_, int p_i47392_5_,
			int p_i47392_6_, int p_i47392_7_, int p_i47392_8_, ResourceLocation p_i47392_9_) {
		super(p_i47392_1_, p_i47392_2_, p_i47392_3_, p_i47392_4_, p_i47392_5_, p_i47392_6_, p_i47392_7_, p_i47392_8_,
				p_i47392_9_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
    {
		//System.out.println("sup");
		//soundHandlerIn.playSound(new LootingSound(Minecraft.getMinecraft().player));
         soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.FANCY_BEEP, 1.0F));
      // soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.COMPOUND_MIX, 1.0F));
    }
	

}
