package com.vicmatskiv.weaponlib.compatibility;

import com.vicmatskiv.weaponlib.SpawnEntityRenderer;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class CompatibleRenderingRegistry {

	public CompatibleRenderingRegistry(String modId) {
		// TODO Auto-generated constructor stub
	}

	public void register(Item item, String name, Object renderer) {
		MinecraftForgeClient.registerItemRenderer(item, (IItemRenderer) renderer);

	}

	public void registerEntityRenderingHandler(Class<WeaponSpawnEntity> class1,
			SpawnEntityRenderer spawnEntityRenderer) {
		RenderingRegistry.registerEntityRenderingHandler(WeaponSpawnEntity.class, new SpawnEntityRenderer());
	}

}
