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
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderingRegistry implements ICustomModelLoader {

	private List<ModelSourceRenderer> renderers = new ArrayList<>();
	private Set<String> modelSourceLocations = new HashSet<>();
	
	private String modId;
	
	public RenderingRegistry(String modId) {
		this.modId = modId;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void bakeModel(ModelBakeEvent event) {
		for(ModelSourceRenderer model: renderers) {
			event.getModelRegistry().putObject(model.getResourceLocation(), model);
		}
	}

	public void register(Item item, String name, ModelSourceRenderer renderer) {
		renderers.add(renderer);
		modelSourceLocations.add(modId + ":models/item/" + name);
		ModelResourceLocation modelID = new ModelResourceLocation(modId + ":" + name, "inventory");
		renderer.setResourceLocation(modelID);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		ItemModelMesher itemModelMesher = renderItem.getItemModelMesher();
		itemModelMesher.register(item, 0, modelID);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		// Do not accept attachments
		return modId.equals(modelLocation.getResourceDomain()) && modelSourceLocations.contains(modelLocation.toString());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws IOException {
		return ModelLoaderRegistry.getMissingModel();
	}
}
