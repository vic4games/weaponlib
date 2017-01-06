package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBullet extends Item {
	
	private List<ItemMagazine> compatibleMagazines = new ArrayList<>();

	public void addCompatibleMagazine(ItemMagazine magazine) {
		compatibleMagazines.add(magazine);
	}
	
	@SuppressWarnings({ "rawtypes"})
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List info, boolean p_77624_4_) {
		//info.add("Compatible guns:");

		//compatibleMagazines.forEach((m) -> info.add(GameRegistry.));
	}
}