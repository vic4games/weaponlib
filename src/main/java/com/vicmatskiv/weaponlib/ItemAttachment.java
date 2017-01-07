package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAttachment<T> extends Item implements ModelSource {

	private AttachmentCategory category;
	private String crosshair;
	private ApplyHandler<T> apply;
	private ApplyHandler<T> remove;
	private List<Tuple<ModelBase, String>> texturedModels = new ArrayList<>();
	private CustomRenderer postRenderer;
	private CustomRenderer preRenderer;
	
	private List<Weapon> compatibleWeapons = new ArrayList<>();
	
	public static interface ApplyHandler<T> {
		public void apply(ItemAttachment<T> itemAttachment, T target, EntityPlayer player);
	}


	public ItemAttachment(String modId, AttachmentCategory category, ModelBase attachment, String textureName, String crosshair, 
			ApplyHandler<T> apply, ApplyHandler<T> remove) {
		//this.modId = modId;
		this.category = category;
		this.texturedModels.add(new Tuple<ModelBase, String>(attachment, textureName));
		this.crosshair = crosshair != null ? modId + ":" + "textures/crosshairs/" + crosshair + ".png" : null;
		this.apply = apply;
		this.remove = remove;
	}
	
	public ItemAttachment(String modId, AttachmentCategory category, String crosshair, 
			ApplyHandler<T> apply, ApplyHandler<T> remove) {
		//this.modId = modId;
		this.category = category;
		this.crosshair = crosshair != null ? modId + ":" + "textures/crosshairs/" + crosshair + ".png" : null;
		this.apply = apply;
		this.remove = remove;
	}
	
	@Deprecated
	public ItemAttachment<T> addModel(ModelBase model, String textureName) {
		texturedModels.add(new Tuple<>(model, textureName));
		return this;
	}
	
	public ItemAttachment(String modId, AttachmentCategory category, String crosshair) {
		this(modId, category, crosshair, (a, w, p) -> {}, (a, w, p) -> {});
	}
	
	public ItemAttachment(String modId, AttachmentCategory category, ModelBase attachment, String textureName, String crosshair) {
		this(modId, category, attachment, textureName, crosshair, (a, w, p) -> {}, (a, w ,p) -> {});
	}

	public AttachmentCategory getCategory() {
		return category;
	}
	
	public List<Tuple<ModelBase, String>> getTexturedModels() {
		return texturedModels;
	}

	public String getCrosshair() {
		return crosshair;
	}

	public ApplyHandler<T> getApply() {
		return apply;
	}

	public ApplyHandler<T> getRemove() {
		return remove;
	}
	
	public void addCompatibleWeapon(Weapon weapon) {
		compatibleWeapons.add(weapon);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List info, boolean p_77624_4_) {
		info.add("Compatible guns:");
		compatibleWeapons.forEach((weapon) -> info.add(weapon.getName()));
	}
	
	public void setPostRenderer(CustomRenderer postRenderer) {
		this.postRenderer = postRenderer;
	}

	public CustomRenderer getPostRenderer() {
		return postRenderer;
	}

	public CustomRenderer getPreRenderer() {
		return preRenderer;
	}

	public void setPreRenderer(CustomRenderer preRenderer) {
		this.preRenderer = preRenderer;
	}
}