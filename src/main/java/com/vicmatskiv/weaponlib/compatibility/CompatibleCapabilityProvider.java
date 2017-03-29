package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public abstract class CompatibleCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<NBTTagCompound> {

    @Override
    public final boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return hasCapability(new CompatibleCapability<>(capability), CompatibleEnumFacing.valueOf(facing));
    }

    protected abstract boolean hasCapability(CompatibleCapability<?> compatibleCapability, CompatibleEnumFacing enumFacing);

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return getCapability(new CompatibleCapability<T>(capability), CompatibleEnumFacing.valueOf(facing));
    }

    protected abstract <T> T getCapability(CompatibleCapability<T> compatibleCapability, CompatibleEnumFacing facing);

    @Override
    public NBTTagCompound serializeNBT() {
        throw new UnsupportedOperationException("Implement me");
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        throw new UnsupportedOperationException("Implement me");
    }

}
