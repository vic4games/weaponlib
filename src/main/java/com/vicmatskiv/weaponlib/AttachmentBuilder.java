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

import cpw.mods.fml.common.registry.GameRegistry;

public class AttachmentBuilder<T> {
	protected String name;
	protected String modId;
	protected ModelBase model;
	protected String textureName;
	protected Consumer<ItemStack> entityPositioning;
	protected Consumer<ItemStack> inventoryPositioning;
	protected BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
	protected BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
	protected BiConsumer<ModelBase, ItemStack> firstPersonModelPositioning;
	protected BiConsumer<ModelBase, ItemStack> thirdPersonModelPositioning;
	protected BiConsumer<ModelBase, ItemStack> inventoryModelPositioning;
	protected BiConsumer<ModelBase, ItemStack> entityModelPositioning;
	
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
	
	public AttachmentBuilder<T> withFirstPersonModelPositioning(BiConsumer<ModelBase, ItemStack> firstPersonModelPositioning) {
		this.firstPersonModelPositioning = firstPersonModelPositioning;
		return this;
	}
	
	public AttachmentBuilder<T> withEntityModelPositioning(BiConsumer<ModelBase, ItemStack> entityModelPositioning) {
		this.entityModelPositioning = entityModelPositioning;
		return this;
	}
	
	public AttachmentBuilder<T> withInventoryModelPositioning(BiConsumer<ModelBase, ItemStack> inventoryModelPositioning) {
		this.inventoryModelPositioning = inventoryModelPositioning;
		return this;
	}

	public AttachmentBuilder<T> withThirdPersonModelPositioning(BiConsumer<ModelBase, ItemStack> thirdPersonModelPositioning) {
		this.thirdPersonModelPositioning = thirdPersonModelPositioning;
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
	
	public AttachmentBuilder<T> withApply(ApplyHandler<T> apply) {
		this.apply = apply;
		return this;
	}
	
	public AttachmentBuilder<T> withRemove(ApplyHandler<T> remove) {
		this.remove = remove;
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
		attachment.setCreativeTab(tab);
		attachment.setPostRenderer(postRenderer);
		attachment.setName(name);
		if(textureName != null) {
			attachment.setTextureName(modId + ":" + stripFileExtension(textureName, ".png"));
		} 
		
		if(isRenderablePart) {
			attachment.setRenderablePart(new Part() {
				@Override
				public String toString() {
					return name != null ? "Part [" + name + "]" : super.toString();
				}
			});
		}
		
		if(model != null) {
			attachment.addModel(model, textureName);
		}
		
		texturedModels.forEach(tm -> attachment.addModel(tm.getU(), tm.getV()));
		
		if(model != null || !texturedModels.isEmpty()) {
			StaticModelSourceRenderer renderer = new StaticModelSourceRenderer.Builder()
					.withEntityPositioning(entityPositioning)
					.withFirstPersonPositioning(firstPersonPositioning)
					.withThirdPersonPositioning(thirdPersonPositioning)
					.withInventoryPositioning(inventoryPositioning)
					.withEntityModelPositioning(entityModelPositioning)
					.withFirstPersonModelPositioning(firstPersonModelPositioning)
					.withThirdPersonModelPositioning(thirdPersonModelPositioning)
					.withInventoryModelPositioning(inventoryModelPositioning)
					.withModId(modId)
					.build();
			
			modContext.registerRenderableItem(name, attachment, renderer);
		} else {
			GameRegistry.registerItem(attachment, name);
		}
		
		return attachment;
	}

	private static String stripFileExtension(String str, String extension) {
		return str.endsWith(extension) ? str.substring(0, str.length() - 4) : str;
	}
	
	public <V extends ItemAttachment<T>> V build(ModContext modContext, Class<V> target) {
		return target.cast(build(modContext));
	}

}
