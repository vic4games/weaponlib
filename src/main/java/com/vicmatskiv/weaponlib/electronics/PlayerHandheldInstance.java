package com.vicmatskiv.weaponlib.electronics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.PlayerItemInstance;
import com.vicmatskiv.weaponlib.network.TypeRegistry;
import com.vicmatskiv.weaponlib.perspective.Perspective;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class PlayerHandheldInstance extends PlayerItemInstance<HandheldState> {
	
	private static final int SERIAL_VERSION = 1;
	
	@SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(PlayerHandheldInstance.class);

	static {
		TypeRegistry.getInstance().register(PlayerHandheldInstance.class);
	}

	public PlayerHandheldInstance() {
		super();
	}

	public PlayerHandheldInstance(int itemInventoryIndex, EntityLivingBase player, ItemStack itemStack) {
		super(itemInventoryIndex, player, itemStack);
	}

	public PlayerHandheldInstance(int itemInventoryIndex, EntityLivingBase player) {
		super(itemInventoryIndex, player);
	}
	
	@Override
	public Class<? extends Perspective<?>> getRequiredPerspectiveType() {
	    ItemHandheld<?> item = (ItemHandheld<?>)getItem();
	    return item != null ? item.getRequiredPespectiveType() : null;
	}
	
	@Override
	public void serialize(ByteBuf buf) {
	    super.serialize(buf);
	}
	
	@Override
	public void init(ByteBuf buf) {
	    super.init(buf);
	}
	
	@Override
	protected int getSerialVersion() {
		return SERIAL_VERSION;
	}
    
    @Override
    public String toString() {
        return "Handheld [" + getUuid() + "]";
    }

}
