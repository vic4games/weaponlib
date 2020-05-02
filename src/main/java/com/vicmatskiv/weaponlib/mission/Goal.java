package com.vicmatskiv.weaponlib.mission;

import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.network.UniversalObject;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class Goal extends UniversalObject {

    private Action requiredAction;
    private int quantity;
    
    public Goal() {}

    public Goal(Action requiredAction, int quantity) {
        this.requiredAction = requiredAction;
        this.quantity = quantity;
    }
    
    public Action getRequiredAction() {
        return requiredAction;
    }
    
    public int getQuantity() {
        return quantity;
    }

    public boolean update(Action action, EntityPlayer player) {
        int matchedCount = requiredAction.matches(action, player);
        if(matchedCount > 0) {
            if(requiredAction.isTransient()) {
                if(quantity > 0) {
                    quantity -= Math.min(matchedCount, quantity);
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCompleted(EntityPlayer player) {
        return requiredAction.isTransient() ? quantity == 0 : 
            requiredAction.quantityMatches(player, quantity);
    }
    
    @Override
    public void init(ByteBuf buf) {
        super.init(buf);
        requiredAction = TypeRegistry.getInstance().fromBytes(buf);
        quantity = buf.readInt();
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        TypeRegistry.getInstance().toBytes(requiredAction, buf);
        buf.writeInt(quantity);
    }
}
