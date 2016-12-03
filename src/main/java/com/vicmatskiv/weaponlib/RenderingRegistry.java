package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderingRegistry {

	private List<WeaponRenderer> renderers = new ArrayList<WeaponRenderer>();
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void bakeModel(ModelBakeEvent event) {
		for(WeaponRenderer model: renderers) {
			event.modelRegistry.putObject(model.resourceLocation, model);
		}
	}

	public void register(Weapon weapon, WeaponRenderer renderer) {
		renderers.add(renderer);
		ModelResourceLocation modelID = new ModelResourceLocation("mw", weapon.getName());
		renderer.resourceLocation = modelID;
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		ItemModelMesher itemModelMesher = renderItem.getItemModelMesher();
		itemModelMesher.register(weapon, 0, modelID);
	}
}
