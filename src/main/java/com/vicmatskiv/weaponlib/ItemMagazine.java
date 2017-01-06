package com.vicmatskiv.weaponlib;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemMagazine extends ItemAttachment<Weapon> implements Part {
	
	public static final class Builder {
		private String name;
		private String modId;
		private ModelBase model;
		private String textureName;
		private int ammo;
		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
		
		private CreativeTabs tab;
		
		public Builder withName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder withCreativeTab(CreativeTabs tab) {
			this.tab = tab;
			return this;
		}

		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withModel(ModelBase model) {
			this.model = model;
			return this;
		}
		
		public Builder withTextureName(String textureName) {
			this.textureName = textureName;
			return this;
		}
		
		public Builder withAmmo(int ammo) {
			this.ammo = ammo;
			return this;
		}
		
		public Builder withEntityPositioning(Consumer<ItemStack> entityPositioning) {
			this.entityPositioning = entityPositioning;
			return this;
		}
		
		public Builder withInventoryPositioning(Consumer<ItemStack> inventoryPositioning) {
			this.inventoryPositioning = inventoryPositioning;
			return this;
		}

		public Builder withThirdPersonPositioning(BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning) {
			this.thirdPersonPositioning = thirdPersonPositioning;
			return this;
		}

		public Builder withFirstPersonPositioning(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning) {
			this.firstPersonPositioning = firstPersonPositioning;
			return this;
		}
		
		public ItemMagazine build(ModContext modContext) {
			ItemMagazine itemMagazine = new ItemMagazine(modId, model, textureName, ammo);
			itemMagazine.setUnlocalizedName(modId + "_" + name); 
			itemMagazine.setCreativeTab(tab);
			
			StaticModelSourceRenderer renderer = new StaticModelSourceRenderer.Builder()
					.withEntityPositioning(entityPositioning)
					.withFirstPersonPositioning(firstPersonPositioning)
					.withThirdPersonPositioning(thirdPersonPositioning)
					.withInventoryPositioning(inventoryPositioning)
					.withModId(modId)
					.build();
			
			modContext.registerRenderableItem(name, itemMagazine, renderer);
			return itemMagazine;
		}
	}
	
	private final int DEFAULT_MAX_STACK_SIZE = 1;
	
	private int ammo;

	public ItemMagazine(String modId, ModelBase model, String textureName, int ammo) {
		this(modId, model, textureName, ammo, null, null);
	}

	public ItemMagazine(String modId, ModelBase model, String textureName, int ammo,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> apply,
			com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler<Weapon> remove) {
		super(modId, AttachmentCategory.MAGAZINE, model, textureName, null, apply, remove);
		this.ammo = ammo;
		setMaxStackSize(DEFAULT_MAX_STACK_SIZE);
	}
	
	ItemStack createItemStack() {
		ItemStack attachmentItemStack = new ItemStack(this);
		ensureItemStack(attachmentItemStack);
		return attachmentItemStack;
	}
	
	private void ensureItemStack(ItemStack itemStack) {
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
			Tags.setAmmo(itemStack, ammo);
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World p_77622_2_, EntityPlayer p_77622_3_) {
		ensureItemStack(stack);
		super.onCreated(stack, p_77622_2_, p_77622_3_);
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		ensureItemStack(stack);
		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_,
			boolean p_77663_5_) {
		ensureItemStack(stack);
		super.onUpdate(stack, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
	}

	public void load(ItemStack itemStack, EntityPlayer player) {
		int currentAmmo = Tags.getAmmo(itemStack);
		ItemMagazine magazine = (ItemMagazine) itemStack.getItem();
		if(currentAmmo < ammo) {
			List<ItemBullet> compatibleBullets = magazine.getCompatibleBullets();
			ItemStack bulletStack = tryConsumingBullet(magazine, compatibleBullets, player);
			if(bulletStack != null) {
				Tags.setAmmo(itemStack, currentAmmo + 1);
			}
		}
	}

	private ItemStack tryConsumingBullet(ItemMagazine magazine, List<ItemBullet> compatibleBullets,
			EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ItemBullet> getCompatibleBullets() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
