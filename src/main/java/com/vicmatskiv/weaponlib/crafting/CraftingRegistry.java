package com.vicmatskiv.weaponlib.crafting;

import java.util.ArrayList;
import java.util.HashMap;

import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.Weapon;

import net.minecraft.item.ItemStack;

public class CraftingRegistry {
	
	private static HashMap<String, Weapon> weaponRecipeLookup = new HashMap<>(50, 0.7f);
	private static HashMap<String, ItemAttachment<?>> attachmentRecipeLookup = new HashMap<>(50, 0.7f);
	private static ArrayList<Weapon> weaponCraftingRegistry = new ArrayList<>();
	private static ArrayList<ItemAttachment<?>> attachmentCraftingRegistry = new ArrayList<>();
	
	private static HashMap<CraftingGroup, HashMap<String, IModernCrafting>> categoricalLookup = new HashMap<>(50, 0.7f);
	
	static {
		for(CraftingGroup group : CraftingGroup.values()) {
			categoricalLookup.put(group, new HashMap<>());
		}
	}
	
	
	public static IModernCrafting getModernCrafting(CraftingGroup group, String name) {
		return categoricalLookup.get(group).get(name);
	}
	
	public static ArrayList<Weapon> getWeaponCraftingRegistry() {
		return weaponCraftingRegistry;
	}
	
	public static ArrayList<ItemAttachment<?>> getAttachmentCraftingRegistry() {
		return attachmentCraftingRegistry;
	}
	
	public static Weapon getWeapon(String name) {
		return weaponRecipeLookup.get(name);
	}
	
	public static ItemStack[] getWeaponRecipe(String name) {
		return getWeapon(name).getModernRecipe();
	}
	
	public static void register(Weapon weapon) {
		weaponCraftingRegistry.add(weapon);
		weaponRecipeLookup.put(weapon.getName(), weapon);
		categoricalLookup.get(CraftingGroup.GUN).put(weapon.getUnlocalizedName(), weapon);
		
	}
	
	public static void register(CraftingGroup group, ItemAttachment<?> attachment) {
		attachmentCraftingRegistry.add(attachment);
		attachmentRecipeLookup.put(attachment.getUnlocalizedName(), attachment);
		
		if(group == null)
			group = CraftingGroup.ATTACHMENT_NORMAL;
		
		categoricalLookup.get(group).put(attachment.getUnlocalizedName(), attachment);
	}

}
