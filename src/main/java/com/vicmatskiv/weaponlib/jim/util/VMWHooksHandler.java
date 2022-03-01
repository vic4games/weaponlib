package com.vicmatskiv.weaponlib.jim.util;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.Weapon;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class VMWHooksHandler {
	
	private static Logger logger = LogManager.getLogger("VMW Hooks Handler");
	private static ItemAttachment<Weapon> magicMagazine;
	
	
	private static Field getFastField(Class<?> clazz, String name) {
		Field f = ReflectionHelper.findField(clazz, name);
		return f;
	}
	 
	

}
