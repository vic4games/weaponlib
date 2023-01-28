package com.jimholden.conomy.init;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.entity.EntityDeer;
import com.jimholden.conomy.entity.EntityHog;
import com.jimholden.conomy.entity.EntityRemountRope;
import com.jimholden.conomy.entity.EntityRock;
import com.jimholden.conomy.entity.EntityRope;
import com.jimholden.conomy.entity.EntityTestVes;
import com.jimholden.conomy.entity.EntityTrader;
import com.jimholden.conomy.util.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
	
	public static void registerEntities() {
		registerEntity("basiczombie", EntityBaseZombie.class, Reference.ENTITY_BASEZOMBIE, 30, 8642207, 13341273);
		registerEntity("hog", EntityHog.class, Reference.ENTITY_HOG, 30, 13341273, 13341273);
		registerEntity("deer", EntityDeer.class, Reference.ENTITY_DEER, 30, 13341273, 13341273);
		registerEntity("trader", EntityTrader.class, Reference.ENTITY_TRADER, 64, 13341273, 13341273);
		registerEntityNoEgg("throwablerock", EntityRock.class, Reference.ENTITY_ROCK, 30);
		registerEntityNoEgg("rope", EntityRope.class, Reference.ENTITY_ROPE, 30);
		registerEntityNoEgg("remountpoint", EntityRemountRope.class, Reference.REMOUNT_DEVICE, 30);
		registerEntityNoEgg("testves", EntityTestVes.class, 128, 30);
	}
	
	private static void registerEntity(String name, Class<? extends Entity> entityClass, int id, int range, int color1, int color2) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":" + name), entityClass, name, id, Main.instance, range, 1, true, color1, color2);
		
	}
	
	private static void registerEntityNoEgg(String name, Class<? extends Entity> entityClass, int id, int range) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":" + name), entityClass, name, id, Main.instance, range, 1, true);
		
	}

}
