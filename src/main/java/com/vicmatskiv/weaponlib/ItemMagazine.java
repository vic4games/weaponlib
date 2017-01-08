package com.vicmatskiv.weaponlib;

import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemMagazine extends ItemAttachment<Weapon> implements Part {
	
	public static final class Builder extends AttachmentBuilder<Weapon> {
		private int ammo;
		
		public Builder withAmmo(int ammo) {
			this.ammo = ammo;
			return this;
		}
		
		@Override
		protected ItemAttachment<Weapon> createAttachment() {
			return new ItemMagazine(modId, model, textureName, ammo);
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
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
			Tags.setAmmo(itemStack, ammo);
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World p_77622_2_, EntityPlayer p_77622_3_) {
		ensureItemStack(stack);
		super.onCreated(stack, p_77622_2_, p_77622_3_);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		ensureItemStack(stack);
		return super.onItemUseFinish(stack, worldIn, entityLiving);
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
