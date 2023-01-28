package com.jimholden.conomy.items.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICAModel {
	
	public ResourceLocation getTex();
	
	@SideOnly(Side.CLIENT)
	public ModelBiped getModel();
	
	@SideOnly(Side.CLIENT)
	public void clientInit();
	
	

}
