package com.vicmatskiv.weaponlib.compatibility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vicmatskiv.weaponlib.SpawnEntityRenderer;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CompatibleRenderingRegistry implements ICustomModelLoader {

	private List<ModelSourceRenderer> renderers = new ArrayList<>();
	private Set<String> modelSourceLocations = new HashSet<>();

	private String modId;

	public CompatibleRenderingRegistry(String modId) {
		this.modId = modId;
		ModelLoaderRegistry.registerLoader(this);
		{
		    System.setProperty("fml.reloadResourcesOnStart", "true");
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void bakeModel(ModelBakeEvent event) {
		for(ModelSourceRenderer model: renderers) {
			event.getModelRegistry().putObject(model.getResourceLocation(), model);
		}
	}

	public void register(Item item, String name, Object renderer) {
		renderers.add((ModelSourceRenderer) renderer);
		modelSourceLocations.add(modId + ":models/item/" + name);
		ModelResourceLocation modelID = new ModelResourceLocation(modId + ":" + name, "inventory");
		((ModelSourceRenderer) renderer).setResourceLocation(modelID);
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

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void registerEntityRenderingHandler(Class<? extends Entity> class1,
	        Object spawnEntityRenderer) {
		RenderingRegistry.registerEntityRenderingHandler(class1, (Render<? extends Entity>) spawnEntityRenderer);
	}
}
