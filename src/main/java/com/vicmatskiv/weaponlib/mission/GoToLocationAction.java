package com.vicmatskiv.weaponlib.mission;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class GoToLocationAction extends Action {
    
    private double x;
    private double y;
    private double z;
    private double sqDistance;
    
    public GoToLocationAction(double x, double y, double z, double distance) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sqDistance = distance * distance;
    }
    
    public GoToLocationAction() {}
    
    @Override
    public int matches(Action anotherAction, EntityPlayer player) {
        return anotherAction instanceof GoToLocationAction 
                && (    (x - ((GoToLocationAction)anotherAction).x) * (x - ((GoToLocationAction)anotherAction).x)
                      + (y - ((GoToLocationAction)anotherAction).y) * (y - ((GoToLocationAction)anotherAction).y)
                      + (z - ((GoToLocationAction)anotherAction).z) * (z - ((GoToLocationAction)anotherAction).z))
                <= sqDistance ? 1 : 0;
    }

    @Override
    public void init(ByteBuf buf) {
        super.init(buf);
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        sqDistance = buf.readDouble();
    }
    
    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(sqDistance);
    }
}
