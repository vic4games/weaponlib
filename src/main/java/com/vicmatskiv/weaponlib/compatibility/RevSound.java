package com.vicmatskiv.weaponlib.compatibility;

import java.util.function.Supplier;

import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RevSound extends PositionedSound {
    private Supplier<CompatibleVec3> positionProvider;
    private EntityVehicle vehicle;

    public RevSound(CompatibleSound sound, Supplier<CompatibleVec3> positionProvider, EntityVehicle vehicle) {
        super(sound.getSound(), SoundCategory.BLOCKS);
        this.repeat = true;
        this.volume = 1.5F;
        this.vehicle = vehicle;
        this.positionProvider = positionProvider;
    }

    public void update() {
        CompatibleVec3 position = positionProvider.get();
        this.xPosF = (float) position.getXCoord();
        this.yPosF = (float) position.getYCoord();
        this.zPosF = (float) position.getZCoord();
    }
}
