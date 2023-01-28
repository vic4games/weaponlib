package com.jimholden.conomy.medical;

import com.jimholden.conomy.capabilities.ICredit;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants.NBT;

public class ConsciousStorage implements IStorage<IConscious>{


	@Override
	public NBTBase writeNBT(Capability<IConscious> capability, IConscious instance, EnumFacing side) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("isDowned", instance.isDowned());
		compound.setInteger("blood", instance.getBlood());
		compound.setBoolean("isBleeding", instance.isBleeding());
		compound.setInteger("downTimer", instance.getDownTimer());
		compound.setBoolean("markDirty", instance.isDirty());
		compound.setDouble("weight", instance.getWeight());
		compound.setInteger("waterLevel", instance.getWaterLevel());
		compound.setDouble("pain", instance.getPainLevel());
		compound.setDouble("applicator", instance.getApplicator());
		
		compound.setBoolean("hasSplint", instance.hasSplint());
		compound.setInteger("legHealth", instance.getLegHealth());
		
		NBTTagList modifiers = new NBTTagList();
		for(SystemDrug mod : instance.getSystemDrugs()) {
			modifiers.appendTag(mod.writeNBT());
		}
		compound.setTag("modifiers", modifiers);
		System.out.println("mods : " + modifiers + "(" + instance.getSystemDrugs().size() + ")");
		return compound;
		//return new NBTTagInt(instance.isDowned());
	}

	@Override
	public void readNBT(Capability<IConscious> capability, IConscious instance, EnumFacing side, NBTBase nbt) {
		System.out.println("Here's the NBT we got: " + nbt);
		NBTTagCompound compound = (NBTTagCompound) nbt;
		instance.setBlood(((NBTTagCompound) nbt).getInteger("blood"));
		instance.setDowned(((NBTTagCompound) nbt).getInteger("isDowned"));
		instance.setIsBleed(((NBTTagCompound) nbt).getBoolean("isBleeding"));
		instance.setDownTimer(((NBTTagCompound) nbt).getInteger("downTimer"));
		instance.markDirty(((NBTTagCompound) nbt).getBoolean("markDirty"));
		instance.setWeight(((NBTTagCompound) nbt).getDouble("weight"));
		instance.setWaterLevel(((NBTTagCompound) nbt).getInteger("waterLevel"));
		instance.setPain(((NBTTagCompound) nbt).getDouble("pain"));
		instance.setApplicator(((NBTTagCompound) nbt).getDouble("applicator"));
		
		instance.setLegHealth(compound.getInteger("legHealth"));
		instance.setHasSplint(compound.getBoolean("hasSplint"));
		//instance.setDowned(((NBTPrimitive) nbt).getInt());
		
		NBTTagList modifiers = compound.getTagList("modifiers", NBT.TAG_COMPOUND);
		for(int i = 0; i < modifiers.tagCount(); ++i) {
			instance.addDrug(SystemDrug.readNBT(modifiers.getCompoundTagAt(i)));
			SystemDrug droog = SystemDrug.readNBT(modifiers.getCompoundTagAt(i));
			if(!droog.getAbilities().isEmpty()) {
				System.out.println("adding " + droog.getAbilities().get(0).writeNBT());
			}
			
		}
		
		
	}

}
