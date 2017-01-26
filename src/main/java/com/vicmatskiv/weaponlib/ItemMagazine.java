package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import com.vicmatskiv.weaponlib.Weapon.State;

public class ItemMagazine extends ItemAttachment<Weapon> implements Part {
	
	private static final long DEFAULT_RELOADING_TIMEOUT_TICKS = 25;
	
	public static final class Builder extends AttachmentBuilder<Weapon> {
		private int ammo;
		private long reloadingTimeout = DEFAULT_RELOADING_TIMEOUT_TICKS;
		private Set<ItemBullet> compatibleBullets = new HashSet<>();
		private String reloadSound;
		
		public Builder withAmmo(int ammo) {
			this.ammo = ammo;
			return this;
		}
		
		public Builder withReloadingTimeout(int reloadingTimeout) {
			this.reloadingTimeout = reloadingTimeout;
			return this;
		}
		
		public Builder withReloadSound(String reloadSound) {
			this.reloadSound = reloadSound;
			return this;
		}
		
		public Builder withCompatibleBullet(ItemBullet compatibleBullet) {
			this.compatibleBullets.add(compatibleBullet);
			return this;
		}
		
		@Override
		protected ItemAttachment<Weapon> createAttachment(ModContext modContext) {
			ItemMagazine magazine = new ItemMagazine(modId, model, textureName, ammo);
			magazine.reloadingTimeout = reloadingTimeout;
			magazine.compatibleBullets = new ArrayList<>(compatibleBullets);
			if(reloadSound != null) {
				magazine.reloadSound = modContext.registerSound(reloadSound);
			}
			return magazine;
		}
	}
	
	private final int DEFAULT_MAX_STACK_SIZE = 1;
	
	private int ammo;
	private long reloadingTimeout;
	private List<ItemBullet> compatibleBullets;
	private SoundEvent reloadSound;

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
	public void onUpdate(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
		ensureItemStack(stack);
		State state = Tags.getState(stack);
		// TODO: this needs to be moved to reload manager
		if(state == Weapon.State.RELOAD_CONFIRMED && Tags.getDefaultTimer(stack) <= world.getTotalWorldTime()) {
			Tags.setState(stack, Weapon.State.READY);
		}
		
		super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
	}

	List<ItemBullet> getCompatibleBullets() {
		return compatibleBullets;
	}

	int getAmmo() {
		return ammo;
	}

	public SoundEvent getReloadSound() {
		return reloadSound;
	}

	public long getReloadTimeout() {
		return reloadingTimeout;
	}
	
	@Override
	public Part getRenderablePart() {
		return this;
	}
	
}
