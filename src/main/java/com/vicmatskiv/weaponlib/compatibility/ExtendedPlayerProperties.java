package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.tracking.PlayerEntityTracker;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayerProperties implements IExtendedEntityProperties {
    
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(ExtendedPlayerProperties.class);
    
    private static final String TAG_TRACKER = "tracker";
    
    private static final String EXTENDED_PROPERTY_NAME = ExtendedPlayerProperties.class.getName();
    
    public static final ExtendedPlayerProperties getProperties(EntityPlayer player) {
        if(player == null) {
            return null;
        }
        ExtendedPlayerProperties properties = (ExtendedPlayerProperties) player.getExtendedProperties(EXTENDED_PROPERTY_NAME);
        if(properties != null && !properties.initialized) {
            properties.init(player, compatibility.world(player));
        }
        return properties;
    }
    
    public static final void init(EntityPlayer player) {
        ExtendedPlayerProperties properties = new ExtendedPlayerProperties();
        properties.init(player, compatibility.world(player));
        player.registerExtendedProperties(EXTENDED_PROPERTY_NAME, properties);
    }
    
    public static final void set(EntityPlayer player, ExtendedPlayerProperties properties) {
        ExtendedPlayerProperties existingProperties = getProperties(player);
        if(existingProperties != null) {
            existingProperties.copyFrom(properties);
        } else {
            player.registerExtendedProperties(EXTENDED_PROPERTY_NAME, properties);
        }
    }
    
    public static final ExtendedPlayerProperties fromBuf(ByteBuf buf) {
        return new ExtendedPlayerProperties();
    }
    
    private void copyFrom(ExtendedPlayerProperties properties) {
        this.entityTrackerContainer = properties.entityTrackerContainer;
    }

    private static class Container<T, I> {
        Function<I, T> initializer;
        Container() {}
        Container(Function<I, T> initializer) {
            this.initializer = initializer;
        }

        T resolved;
        public T get(I i) {
            if(initializer == null) {
                return null;
            }
            if(resolved == null) {
                resolved = initializer.apply(i);
            }
            return resolved;
        }
    }
    
    private boolean initialized;
    
    private World world;

    private Container<PlayerEntityTracker, World> entityTrackerContainer = new Container<>();
    
    ExtendedPlayerProperties() {}
    
    @Override
    public void init(Entity entity, World world) {
        this.world = world;
        this.initialized = true;
    }

    @Override
    public void saveNBTData(NBTTagCompound playerTagCompound) {
        if(!initialized) {
            return;
        }
        NBTTagCompound tagCompound = new NBTTagCompound();
        PlayerEntityTracker playerEntityTracker = entityTrackerContainer.get(world);
        if(playerEntityTracker != null) {
            tagCompound.setByteArray(TAG_TRACKER, playerEntityTracker.toByteArray());
        }
        playerTagCompound.setTag(EXTENDED_PROPERTY_NAME, tagCompound);
    }

    @Override
    public void loadNBTData(NBTTagCompound playerTagCompound) {
        NBTTagCompound tagCompound = playerTagCompound.getCompoundTag(EXTENDED_PROPERTY_NAME);
        if(tagCompound != null) {
            byte[] bytes = tagCompound.getByteArray(TAG_TRACKER);
            if(bytes != null) {
                entityTrackerContainer.initializer = w -> PlayerEntityTracker.fromByteArray(bytes, w);
            }
        }
    }

    public PlayerEntityTracker getTracker() {
        if(!initialized) return null;
        return entityTrackerContainer.get(world);
    }

    public void serialize(ByteBuf buf) {
        if(!initialized) {
            return;
        }
        PlayerEntityTracker et = entityTrackerContainer.get(world);
        if(et != null) {
            et.serialize(buf);;
        }
    }

    public void setTracker(PlayerEntityTracker tracker) {
        this.entityTrackerContainer = new Container<>(w -> tracker);
    }
    
}
