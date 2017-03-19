package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler;
import com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler2;
import com.vicmatskiv.weaponlib.crafting.CraftingComplexity;
import com.vicmatskiv.weaponlib.crafting.OptionsMetadata;

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
	protected ApplyHandler2<T> apply2;
	protected ApplyHandler2<T> remove2;
	private String crosshair;
	private CustomRenderer postRenderer;
	private List<Tuple<ModelBase, String>> texturedModels = new ArrayList<>();
	private boolean isRenderablePart;
    private int maxStackSize = 1;
    private Function<ItemStack, String> informationProvider;
	
	private CraftingComplexity craftingComplexity;

	private Object[] craftingMaterials;
	
	Map<ItemAttachment<T>, CompatibleAttachment<T>> compatibleAttachments = new HashMap<>();
    private int craftingCount = 1;

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
	
	public AttachmentBuilder<T> withCompatibleAttachment(ItemAttachment<T> attachment, Consumer<ModelBase> positioner) {
		compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner));
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
	
	public AttachmentBuilder<T> withMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
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
	
	public AttachmentBuilder<T> withApply(ApplyHandler2<T> apply) {
		this.apply2 = apply;
		return this;
	}
	
	public AttachmentBuilder<T> withRemove(ApplyHandler2<T> remove) {
		this.remove2 = remove;
		return this;
	}

	public AttachmentBuilder<T> withCrafting(CraftingComplexity craftingComplexity, Object...craftingMaterials) {
	    return withCrafting(1, craftingComplexity, craftingMaterials);
	}
	
	public AttachmentBuilder<T> withInformationProvider(Function<ItemStack, String> informationProvider) {
        this.informationProvider = informationProvider;
        return this;
    } 
	
	public AttachmentBuilder<T> withCrafting(int craftingCount, CraftingComplexity craftingComplexity, Object...craftingMaterials) {
		if(craftingComplexity == null) {
			throw new IllegalArgumentException("Crafting complexity not set");
		}
		if(craftingMaterials.length < 2) {
			throw new IllegalArgumentException("2 or more materials required for crafting");
		}
		if(craftingCount == 0) {
		    throw new IllegalArgumentException("Invalid item count");
		}
		this.craftingComplexity = craftingComplexity;
		this.craftingMaterials = craftingMaterials;
		this.craftingCount = craftingCount;
		return this;
	}
	
	protected ItemAttachment<T> createAttachment(ModContext modContext) {
		return new ItemAttachment<T>(
				modId, attachmentCategory, crosshair, 
				apply, remove);
	}
	
	@SuppressWarnings("deprecation")
	public ItemAttachment<T> build(ModContext modContext) {
		ItemAttachment<T> attachment = createAttachment(modContext);
		attachment.setUnlocalizedName(modId + "_" + name); 
		attachment.setCreativeTab(tab);
		attachment.setPostRenderer(postRenderer);
		attachment.setName(name);
		attachment.apply2 = apply2;
		attachment.remove2 = remove2;
		attachment.maxStackSize = maxStackSize;
		if(attachment.getInformationProvider() == null) {
		    attachment.setInformationProvider(informationProvider);
		}
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
			attachment.addModel(model, addFileExtension(textureName, ".png"));
		}
		
		texturedModels.forEach(tm -> attachment.addModel(tm.getU(), addFileExtension(tm.getV(), ".png") ));
		
		compatibleAttachments.values().forEach(a -> attachment.addCompatibleAttachment(a));
		
		if((model != null || !texturedModels.isEmpty())) {
			modContext.registerRenderableItem(name, attachment, compatibility.isClientSide() ? registerRenderer(attachment) : null);
		}
		
		if(craftingComplexity != null) {
			OptionsMetadata optionsMetadata = new OptionsMetadata.OptionMetadataBuilder()
				.withSlotCount(9)
            	.build(craftingComplexity, Arrays.copyOf(craftingMaterials, craftingMaterials.length));
			
			List<Object> shape = modContext.getRecipeGenerator().createShapedRecipe(name, optionsMetadata);
			
			ItemStack itemStack = new ItemStack(attachment);
			itemStack.stackSize = craftingCount;
            if(optionsMetadata.hasOres()) {
			    compatibility.addShapedOreRecipe(itemStack, shape.toArray());
			} else {
			    compatibility.addShapedRecipe(itemStack, shape.toArray());
			}
		}
		
		return attachment;
	}

	
	private Object registerRenderer(ItemAttachment<T> attachment) {
		return new StaticModelSourceRenderer.Builder()
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
	}
	

	static String addFileExtension(String s, String ext) {
		return s != null && !s.endsWith(ext) ? s + ext : s;
	}

	protected static String stripFileExtension(String str, String extension) {
		return str.endsWith(extension) ? str.substring(0, str.length() - extension.length()) : str;
	}
	
	public <V extends ItemAttachment<T>> V build(ModContext modContext, Class<V> target) {
		return target.cast(build(modContext));
	}

}
