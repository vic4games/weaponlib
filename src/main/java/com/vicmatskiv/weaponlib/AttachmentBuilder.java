package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler;

public class AttachmentBuilder<T> {
	protected String name;
	protected String modId;
	protected ModelBase model;
	protected String textureName;
	protected Consumer<ItemStack> entityPositioning;
	protected Consumer<ItemStack> inventoryPositioning;
	protected BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
	protected BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
	protected CreativeTabs tab;
	protected AttachmentCategory attachmentCategory;
	protected ApplyHandler<T> apply;
	protected ApplyHandler<T> remove;
	private String crosshair;
	private CustomRenderer postRenderer;
	private List<Tuple<ModelBase, String>> texturedModels = new ArrayList<>();
	private boolean isRenderablePart;
	
	public AttachmentBuilder<T> withCategory(AttachmentCategory attachmentCategory) {
		this.attachmentCategory = attachmentCategory;
		return this;
	}
	
	public AttachmentBuilder<T> withName(String name) {
		this.name = name;
		return this;
	}
	
	public AttachmentBuilder<T> withCreativeTab(CreativeTabs tab) {
		this.tab = tab;
		return this;
	}

	public AttachmentBuilder<T> withModId(String modId) {
		this.modId = modId;
		return this;
	}
	
	public AttachmentBuilder<T> withModel(ModelBase model) {
		this.model = model;
		return this;
	}
	
	public AttachmentBuilder<T> withTextureName(String textureName) {
		this.textureName = textureName;
		return this;
	}
	
	public AttachmentBuilder<T> withEntityPositioning(Consumer<ItemStack> entityPositioning) {
		this.entityPositioning = entityPositioning;
		return this;
	}
	
	public AttachmentBuilder<T> withInventoryPositioning(Consumer<ItemStack> inventoryPositioning) {
		this.inventoryPositioning = inventoryPositioning;
		return this;
	}

	public  AttachmentBuilder<T> withThirdPersonPositioning(BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning) {
		this.thirdPersonPositioning = thirdPersonPositioning;
		return this;
	}

	public AttachmentBuilder<T> withFirstPersonPositioning(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning) {
		this.firstPersonPositioning = firstPersonPositioning;
		return this;
	}
	
	public AttachmentBuilder<T> withCrosshair(String crosshair) {
		this.crosshair = crosshair;
		return this;
	}
	

	public AttachmentBuilder<T> withPostRender(CustomRenderer postRenderer) {
		this.postRenderer = postRenderer;
		return this;
	}
	
	public AttachmentBuilder<T> withModel(ModelBase model, String textureName) {
		this.texturedModels.add(new Tuple<>(model, textureName));
		return this;
	}
	
	public AttachmentBuilder<T> withRenderablePart() {
		this.isRenderablePart = true;
		return this;
	}
	
	protected ItemAttachment<T> createAttachment() {
		return new ItemAttachment<T>(
				modId, attachmentCategory, /*model, textureName, */ crosshair, 
				apply, remove);
	}
	
	@SuppressWarnings("deprecation")
	public ItemAttachment<T> build(ModContext modContext) {
		ItemAttachment<T> attachment = createAttachment();
		attachment.setUnlocalizedName(modId + "_" + name); 
		attachment.setRegistryName(modId, name);
		attachment.setCreativeTab(tab);
		attachment.setPostRenderer(postRenderer);
		if(isRenderablePart) {
			attachment.setRenderablePart(new Part() {});
		}
		
		if(model != null) {
			attachment.addModel(model, textureName);
		}
		texturedModels.forEach(tm -> attachment.addModel(tm.getU(), tm.getV()));
		StaticModelSourceRenderer renderer = new StaticModelSourceRenderer.Builder()
				.withEntityPositioning(entityPositioning)
				.withFirstPersonPositioning(firstPersonPositioning)
				.withThirdPersonPositioning(thirdPersonPositioning)
				.withInventoryPositioning(inventoryPositioning)
				.withModId(modId)
				.build();
		
		modContext.registerRenderableItem(name, attachment, renderer);
		return attachment;
	}
	
	public <V extends ItemAttachment<T>> V build(ModContext modContext, Class<V> target) {
		return target.cast(build(modContext));
	}

}
