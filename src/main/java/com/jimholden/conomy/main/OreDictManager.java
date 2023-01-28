package com.jimholden.conomy.main;

import com.jimholden.conomy.init.ModItems;

import net.minecraftforge.oredict.OreDictionary;

public class OreDictManager {
	public static void registerOres() {
		OreDictionary.registerOre("ingotOpiumGum", ModItems.OPIUMGUM);
		OreDictionary.registerOre("powderPoppyStraw", ModItems.POPPYSTRAW);
	}

}
