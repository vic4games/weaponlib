package com.vicmatskiv.weaponlib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderingRegistry implements ICustomModelLoader {

	private List<WeaponRenderer> renderers = new ArrayList<WeaponRenderer>();
	private Set<String> weaponLocations = new HashSet<>();
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void bakeModel(ModelBakeEvent event) {
		for(WeaponRenderer model: renderers) {
			event.getModelRegistry().putObject(model.resourceLocation, model);
		}
	}

	public void register(Weapon weapon, WeaponRenderer renderer) {
		renderers.add(renderer);
		weaponLocations.add("mw" + ":models/item/" + weapon.getName());
		ModelResourceLocation modelID = new ModelResourceLocation("mw" + ":" + weapon.getName(), "inventory");
		renderer.resourceLocation = modelID;
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		ItemModelMesher itemModelMesher = renderItem.getItemModelMesher();
		itemModelMesher.register(weapon, 0, modelID);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		// Do not accept attachments
		return "mw".equals(modelLocation.getResourceDomain()) && weaponLocations.contains(modelLocation.toString());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws IOException {
		return ModelLoaderRegistry.getMissingModel();
	}
}
