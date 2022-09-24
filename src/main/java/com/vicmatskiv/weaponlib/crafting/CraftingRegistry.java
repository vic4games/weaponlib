package com.vicmatskiv.weaponlib.crafting;

import java.util.ArrayList;
import java.util.HashMap;

import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.Weapon;

import net.minecraft.item.ItemStack;


/**
 * CraftingRegistry that stores all of the craftable items, allows for an easy lookup of them,
 * and finally an easy registration
 * 
 * @author Homer Riva-Cambrin
 * @version September 23rd, 2022
 */
public class CraftingRegistry {
	
	// Stores a map of all the items registered in a certain category
	private static HashMap<CraftingGroup, ArrayList<IModernCrafting>> craftingMap = new HashMap<>(200, .7f);
	
	// Stores a map of a map of each group under their unlocalized names respectively
	private static HashMap<CraftingGroup, HashMap<String, IModernCrafting>> categoricalLookup = new HashMap<>(50, 0.7f);
	
	static {
		// Fills the maps with the groups (obviously important
		// or we will get a null pointer exception)
		for(CraftingGroup group : CraftingGroup.values()) {
			craftingMap.put(group, new ArrayList<>());
			categoricalLookup.put(group, new HashMap<>());
		}
	}
	
	/**
	 * Returns the IModernCrafting given the crafting group to search and the 
	 * unlocalized name of the item you are looking to craft.
	 * 
	 * @param group - Crafting group of the crafting you are looking for
	 * @param name - Unlocalized name of item you are looking to craft
	 * @return The modern crafting of that item
	 */
	public static IModernCrafting getModernCrafting(CraftingGroup group, String name) {
		return categoricalLookup.get(group).get(name);
	}
	
	/**
	 * Returns if there is an IModernCrafting for an item given 
	 * the crafting group to search and the unlocalized name of
	 * the item you are looking to check.
	 * 
	 * @param group - Crafting group of the crafting you are looking for
	 * @param name - Unlocalized name of item you are looking to craft
	 * @return The modern crafting of that item
	 */
	public static boolean hasModernCrafting(CraftingGroup group, String name) {
		return categoricalLookup.get(group).containsKey(name);
	}

	/**
	 * Returns the list of all the items registered to a category
	 * 
	 * @param group - Crafting group of the crafting you are looking for
	 * @return The list of all the items registered to that category
	 */
	public static ArrayList<IModernCrafting> getCraftingListForGroup(CraftingGroup group) {
		return craftingMap.get(group);
	}
	
	/**
	 * Registers an implementor of {@link IModernCrafting}
	 * 
	 * @param group - Crafting group of the crafting you are looking for
	 * @param crafting - IModernCrafting to register
	 */
	public static void register(IModernCrafting crafting) {
		craftingMap.get(crafting.getCraftingGroup()).add(crafting);
		categoricalLookup.get(crafting.getCraftingGroup()).put(crafting.getItem().getUnlocalizedName(), crafting);
	}

}
