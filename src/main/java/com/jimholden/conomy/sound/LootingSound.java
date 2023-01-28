package com.jimholden.conomy.sound;

import com.jimholden.conomy.blocks.tileentity.TileEntityLootingBlock;
import com.jimholden.conomy.client.gui.player.GuiLootingBlockPlayer;
import com.jimholden.conomy.util.handlers.SoundsHandler;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LootingSound extends MovingSound
{
    private final GuiLootingBlockPlayer lootingBlock;
    private int time;

    public LootingSound(GuiLootingBlockPlayer lootingBlock)
    {
    	
        super(SoundsHandler.LOOTSEARCH, SoundCategory.BLOCKS);
       // System.out.println("hi!");
        this.lootingBlock = lootingBlock;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.0F;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
    	System.out.println("Fuck: " + lootingBlock.tick + " / " + lootingBlock.maxTicking);
        ++this.time;
        this.xPosF = this.lootingBlock.tileentity.getPos().getX();
        this.yPosF = this.lootingBlock.tileentity.getPos().getY();
        this.zPosF = this.lootingBlock.tileentity.getPos().getZ();
        
        if(lootingBlock.tick >= lootingBlock.maxTicking) {
        	System.out.println("done");
        	this.donePlaying = true;
        	//System.out.println("yo");
        }

    }
}
