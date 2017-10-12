package com.vicmatskiv.weaponlib.compatibility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.SpreadableExposure;
import com.vicmatskiv.weaponlib.network.TypeRegistry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CompatibleExposureCapability implements ICapabilitySerializable<NBTBase> {

    public static void register(ModContext modContext) {
        CapabilityManager.INSTANCE.register(ExposureContainer.class, new ExposureContainerStorage(), 
                ExposureContainerImpl.class);
    }

    public static interface ExposureContainer {
//        public <T extends SpreadableExposure> T getExposure(Class<T> targetClass);
//        public Collection<? extends SpreadableExposure> getExposures();
//        public void addExposure(SpreadableExposure exposure);
//        public <T extends SpreadableExposure> T removeExposure(Class<T> targetClass);
        
        public Map<Class<?>, SpreadableExposure> getExposures();
    }
    
    public static class ExposureContainerImpl implements ExposureContainer {
        
        Map<Class<?>, SpreadableExposure> exposures = new HashMap<>();

        @Override
        public Map<Class<?>, SpreadableExposure> getExposures() {
            return exposures;
        }
    }
    
    public static class ExposureContainerStorage implements IStorage<ExposureContainer> {

        @Override
        public NBTBase writeNBT(Capability<ExposureContainer> capability, ExposureContainer instance, EnumFacing side) {
            NBTTagList tagList = new NBTTagList();
            for(SpreadableExposure exposure: instance.getExposures().values()) {
                ByteBuf buf = Unpooled.buffer();
                TypeRegistry.getInstance().toBytes(exposure, buf);
                tagList.appendTag(new NBTTagByteArray(buf.array()));
            }
            return tagList;
        }

        @Override
        public void readNBT(Capability<ExposureContainer> capability, ExposureContainer instance, EnumFacing side, NBTBase nbt) {
            NBTTagList tagList = (NBTTagList) nbt;
            for(int i = 0; i < tagList.tagCount(); i++) {
                NBTTagByteArray byteArray = (NBTTagByteArray) tagList.get(i);
                ByteBuf buf = Unpooled.wrappedBuffer(byteArray.getByteArray());
                SpreadableExposure exposure = TypeRegistry.getInstance().fromBytes(buf);
                instance.getExposures().put(exposure.getClass(), exposure);
            }
        }
    }
    
    @CapabilityInject(ExposureContainer.class)
    static Capability<ExposureContainer> capabilityContainer = null;
    
    private ExposureContainer instance = capabilityContainer.getDefaultInstance(); // doesn't this trigger null pointer exception if capability is not registered?

    public static <T extends SpreadableExposure> T getExposure(Entity entity, Class<T> targetClass) {
        if(entity == null) return null;
        ExposureContainer container = entity.getCapability(capabilityContainer, null);
        return container != null ? targetClass.cast(container.getExposures().get(targetClass)) : null;
    }
    
    public static Collection<? extends SpreadableExposure> getExposures(Entity entity) {
        if(entity == null) return null;
        ExposureContainer container = entity.getCapability(capabilityContainer, null);
        return container != null ? container.getExposures().values() : null;
    }

    public static <T extends SpreadableExposure> T removeExposure(Entity entity, Class<T> targetClass) {
        if(entity == null) return null;
        ExposureContainer container = entity.getCapability(capabilityContainer, null);
        return container != null ? targetClass.cast(container.getExposures().remove(targetClass)): null;
    }
    
    public static void updateExposure(Entity entity, SpreadableExposure exposure) {
        if(entity == null) return ;
        ExposureContainer container = entity.getCapability(capabilityContainer, null);
        if(container != null) {
            container.getExposures().put(exposure.getClass(), exposure);
        }
    }
    
    @Override
    public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == capabilityContainer; //hasCapability(new CompatibleCapability<>(capability), CompatibleEnumFacing.valueOf(facing));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == capabilityContainer ? capabilityContainer.cast(instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return capabilityContainer.getStorage().writeNBT(capabilityContainer, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        capabilityContainer.getStorage().readNBT(capabilityContainer, instance, null, nbt);
    }

   


}
