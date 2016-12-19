package com.vicmatskiv.weaponlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomArmor extends ItemArmor {
	
	private static final String ACTIVE_ATTACHMENT_TAG = "ActiveAttachments";
	
	public static class Builder {
		
		private String modId;
		private String textureName;
		@SuppressWarnings("unused")
		private String iconName;
		private ArmorMaterial material;
		private String unlocalizedName;
		private ModelBiped bootsModel;
		private ModelBiped chestModel;
		private String modelClass;
		private String hudTextureName;
		//private Function<Integer, ModelBiped> modelFactory;
		private Map<ItemAttachment<CustomArmor>, CompatibleAttachment<CustomArmor>> compatibleAttachments = new HashMap<>();
		
		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withTextureName(String textureName) {
			this.textureName = textureName;
			return this;
		}

		public Builder withMaterial(ArmorMaterial material) {
			this.material = material;
			return this;
		}
		
		public Builder withUnlocalizedName(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
			return this;
		}
		
		public Builder withModelClass(String modelClass) {
			this.modelClass = modelClass;
			return this;
		}
		
		public Builder withHudTextureName(String hudTextureName) {
			this.hudTextureName = hudTextureName;
			return this;
		}
		
		/*
		public Builder withModelSupplier(Function<Integer, ModelBiped> modelFactory) {
			this.modelFactory = modelFactory;
			return this;
		}*/
		
		public Builder withCompatibleAttachment(AttachmentCategory category, ModelBase attachmentModel, String textureName,
				Consumer<ModelBase> positioner) {
			ItemAttachment<CustomArmor> item = new ItemAttachment<CustomArmor>(modId, category, attachmentModel, textureName, null);
			compatibleAttachments.put(item, new CompatibleAttachment<CustomArmor>(item, positioner));
			return this;
		}

		public void build(boolean isClient) {
			
			if(isClient) {
				try {
					chestModel = (ModelBiped) Class.forName(modelClass).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new IllegalStateException("Missing chest model", e);
				}
				
				try {
					bootsModel = (ModelBiped) Class.forName(modelClass).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new IllegalStateException("Missing boots model", e);
				}
			}
			
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			
			String unlocalizedHelmetName = unlocalizedName + "_helmet";
			CustomArmor armorHelmet = new CustomArmor(modId, material, 4, EntityEquipmentSlot.HEAD, 
					unlocalizedHelmetName, textureName, chestModel, hudTextureName);
			armorHelmet.setUnlocalizedName(unlocalizedHelmetName);
			GameRegistry.registerItem(armorHelmet, unlocalizedHelmetName);
			
			String unlocalizedChestName = unlocalizedName + "_chest";
			CustomArmor armorChest = new CustomArmor(modId, material, 4, EntityEquipmentSlot.CHEST, 
					unlocalizedChestName, textureName, chestModel, hudTextureName);
			armorChest.setUnlocalizedName(unlocalizedChestName);
			GameRegistry.registerItem(armorChest, unlocalizedChestName);
			
			String unlocalizedBootsName = unlocalizedName + "_boots";
			CustomArmor armorBoots = new CustomArmor(modId, material, 4, EntityEquipmentSlot.LEGS, 
					unlocalizedBootsName, textureName, bootsModel, hudTextureName);
			armorBoots.setUnlocalizedName(unlocalizedBootsName);
			GameRegistry.registerItem(armorBoots, unlocalizedBootsName);
		}
		
		
		public CustomArmor buildHelmet(boolean isClient) {
			
			if(isClient) {
				if(chestModel == null) {
					try {
						chestModel = (ModelBiped) Class.forName(modelClass).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						throw new IllegalStateException("Missing chest model", e);
					}
				}
				
				if(bootsModel == null) {
					try {
						bootsModel = (ModelBiped) Class.forName(modelClass).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						throw new IllegalStateException("Missing boots model", e);
					}
				}
			}
			
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			
			String unlocalizedHelmetName = unlocalizedName + "_helmet";
			CustomArmor armorHelmet = new CustomArmor(modId, material, 4, EntityEquipmentSlot.HEAD, 
					unlocalizedHelmetName, textureName, chestModel, hudTextureName);
			armorHelmet.setUnlocalizedName(unlocalizedHelmetName);
			GameRegistry.registerItem(armorHelmet, unlocalizedHelmetName);
			
			return armorHelmet;
		}
		
		public CustomArmor buildChest(boolean isClient) {
			
			if(isClient) {
				if(chestModel == null) {
					try {
						chestModel = (ModelBiped) Class.forName(modelClass).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						throw new IllegalStateException("Missing chest model", e);
					}
				}
			}
			
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			
			String unlocalizedChestName = unlocalizedName + "_chest";
			CustomArmor armorChest = new CustomArmor(modId, material, 4, EntityEquipmentSlot.CHEST, 
					unlocalizedChestName, textureName, chestModel, hudTextureName);
			armorChest.setUnlocalizedName(unlocalizedChestName);
			GameRegistry.registerItem(armorChest, unlocalizedChestName);

			return armorChest;
		}
		
		public CustomArmor buildBoots(boolean isClient) {
			
			if(isClient) {
				
				if(bootsModel == null) {
					try {
						bootsModel = (ModelBiped) Class.forName(modelClass).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						throw new IllegalStateException("Missing boots model", e);
					}
				}
			}
			
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			
			
			String unlocalizedBootsName = unlocalizedName + "_boots";
			CustomArmor armorBoots = new CustomArmor(modId, material, 4, EntityEquipmentSlot.LEGS, 
					unlocalizedBootsName, textureName, bootsModel, hudTextureName);
			armorBoots.setUnlocalizedName(unlocalizedBootsName);
			GameRegistry.registerItem(armorBoots, unlocalizedBootsName);
			
			return armorBoots;
		}
	}

	//private Builder builder;
	
	private String iconName;
	private String textureName;
	private ModelBiped model;
	private String hudTextureName;
	private String modId;
	private Map<ItemAttachment<CustomArmor>, CompatibleAttachment<CustomArmor>> compatibleAttachments = new HashMap<>();
	
	private CustomArmor(String modId, ArmorMaterial material, int renderIndex, EntityEquipmentSlot armorType, String iconName, String textureName,
			ModelBiped model, String hudTextureName) {
		super(material, renderIndex, armorType);
		this.modId = modId;
		this.iconName = iconName;
		this.textureName = textureName;
		this.model = model;
		this.hudTextureName = hudTextureName;
		/*setCreativeTab(ProjectXureosWarfareMod.faattachmentsTab);*/
	}
	
	public String getHudTexture() {
		return modId + ":" + "textures/hud/" + hudTextureName + ".png";
	}
	

//	TODO: @Override
//	@SideOnly(Side.CLIENT)
//	public void registerIcons(IIconRegister par1IconRegister) {
//		//String itemName = getUnlocalizedName().substring(getUnlocalizedName().lastIndexOf(".") + 1);
//		this.itemIcon = par1IconRegister.registerIcon(modId + ":" + iconName);
//	}

//	@Override
//	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
//		return modId + ":textures/models/" + textureName + ".png";
//	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return modId + ":textures/models/" + textureName + ".png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot,
			ModelBiped _default) {
		
		ModelBiped armorModel = null;
		
		if (itemStack != null) {
			
			if (itemStack.getItem() instanceof CustomArmor) {
				/*
				int type = ((ItemArmor) itemStack.getItem()).armorType;
				if (type == 1 || type == 3) {
					armorModel = ProjectXureosWarfareMod.proxy.getArmorModel(0);
				} else {
					armorModel = ProjectXureosWarfareMod.proxy.getArmorModel(1);
				}*/
				armorModel = model;
			}
			
			if (armorModel != null) {
				throw new UnsupportedOperationException("Fixme below");
//				armorModel.bipedHead.showModel = armorSlot == 0;
//				armorModel.bipedHeadwear.showModel = armorSlot == 0;
//				armorModel.bipedBody.showModel = armorSlot == 1
//						|| armorSlot == 2;
//				armorModel.bipedRightArm.showModel = armorSlot == 1;
//				armorModel.bipedLeftArm.showModel = armorSlot == 1;
//				
//				armorModel.bipedRightLeg.showModel = false;
//				armorModel.bipedLeftLeg.showModel = false;
//				
//				armorModel.bipedRightLeg.showModel = armorSlot == 2
//						|| armorSlot == 3;
//				armorModel.bipedLeftLeg.showModel = armorSlot == 2
//						|| armorSlot == 3;
//				
//				armorModel.isSneak = entityLiving.isSneaking();
//				armorModel.isRiding = entityLiving.isRiding();
//				armorModel.isChild = entityLiving.isChild();
//				armorModel.heldItemRight = entityLiving.getEquipmentInSlot(0) != null ? 1 : 0;
//				
//				if (entityLiving instanceof EntityPlayer) {
//					boolean isAimedWeapon = Weapon.isAimed(entityLiving.getEquipmentInSlot(0));
//					armorModel.aimedBow = ((EntityPlayer) entityLiving).getItemInUseDuration() > 0 
//							|| isAimedWeapon;
//				}
//				return armorModel;
			}
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public void changeAttachment(AttachmentCategory attachmentCategory, ItemStack itemStack, EntityPlayer player) {
		ensureItemStack(itemStack);
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		int activeAttachmentIdForThisCategory = activeAttachmentsIds[attachmentCategory.ordinal()];
		ItemAttachment<CustomArmor> item = null;
		if(activeAttachmentIdForThisCategory > 0) {
			item = (ItemAttachment<CustomArmor>) Item.getItemById(activeAttachmentIdForThisCategory);
			if(item != null && item.getRemove() != null) {
				item.getRemove().apply(item, this, player);
			}
		}
		
		ItemAttachment<CustomArmor> nextAttachment = nextCompatibleAttachment(attachmentCategory, item, player);

		if(nextAttachment != null && nextAttachment.getApply() != null) {
			nextAttachment.getApply().apply(nextAttachment, this, player);
		}
		
		activeAttachmentsIds[attachmentCategory.ordinal()] = Item.getIdFromItem(nextAttachment);;
		
		itemStack.getTagCompound().setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
	}
	
	private ItemAttachment<CustomArmor> nextCompatibleAttachment(AttachmentCategory category, Item currentAttachment, EntityPlayer player) {
		
		//EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		ItemAttachment<CustomArmor> nextAttachment = null;
		boolean foundCurrent = false;
		for (int i = 0; i < 36; i++) {
			ItemStack itemStack = player.inventory.getStackInSlot(i);
			if(itemStack != null) {
				if(itemStack.getItem() instanceof ItemAttachment) {
					@SuppressWarnings("unchecked")
					ItemAttachment<CustomArmor> compatibleAttachment = (ItemAttachment<CustomArmor>) itemStack.getItem();
					//System.out.println("Found compatible attachment " + compatibleAttachment);
					if(compatibleAttachment.getCategory() == category) {
						if(foundCurrent || currentAttachment == null) {
							nextAttachment = compatibleAttachment;
							break;
						} else if(currentAttachment == compatibleAttachment) {
							foundCurrent = true;
						}
						//System.out.println("Compatible attachment category match for " + compatibleAttachment);
					}
				}
				//System.out.println("Item in slot " + i + ": " + itemStack);
			}
			
		}
		
		return nextAttachment;
	}
	
	private void ensureItemStack(ItemStack itemStack) {
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
	}
	
	public ItemAttachment<CustomArmor> getActiveAttachment (ItemStack itemStack, AttachmentCategory category) {
		ensureItemStack(itemStack);
		
		ItemAttachment<CustomArmor> itemAttachment = null;
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<CustomArmor> compatibleAttachment = compatibleAttachments.get(item);
				if(compatibleAttachment != null && category == compatibleAttachment.getAttachment().getCategory()) {
					itemAttachment = compatibleAttachment.getAttachment();
					break;
				}
			}
			
		}
		return itemAttachment;
	}
	
	public List<CompatibleAttachment<CustomArmor>> getActiveAttachments (ItemStack itemStack) {
		ensureItemStack(itemStack);
		
		List<CompatibleAttachment<CustomArmor>> activeAttachments = new ArrayList<>();
		
		int[] activeAttachmentsIds = ensureActiveAttachments(itemStack);
		
		for(int activeIndex: activeAttachmentsIds) {
			if(activeIndex == 0) continue;
			Item item = Item.getItemById(activeIndex);
			if(item instanceof ItemAttachment) {
				CompatibleAttachment<CustomArmor> compatibleAttachment = compatibleAttachments.get(item);
				if(compatibleAttachment != null) {
					activeAttachments.add(compatibleAttachment);
				}
				
			}
			
		}
		return activeAttachments;
	}

	private int[] ensureActiveAttachments(ItemStack itemStack) {
		int activeAttachmentsIds[] = itemStack.getTagCompound().getIntArray(ACTIVE_ATTACHMENT_TAG);
		
		if(activeAttachmentsIds == null || activeAttachmentsIds.length != AttachmentCategory.values.length) {
			activeAttachmentsIds = new int[AttachmentCategory.values.length];
			itemStack.getTagCompound().setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
			for(CompatibleAttachment<CustomArmor> attachment: compatibleAttachments.values()) {
				if(attachment.isDefault()) {
					activeAttachmentsIds[attachment.getAttachment().getCategory().ordinal()] = Item.getIdFromItem(attachment.getAttachment());
				}
			}
		}
		return activeAttachmentsIds;
	}
	
	public static boolean isActiveAttachment(ItemStack itemStack, ItemAttachment<CustomArmor> attachment) {
		CustomArmor armor = (CustomArmor) itemStack.getItem();
		int[] activeAttachmentsIds = armor.ensureActiveAttachments(itemStack);
		return Arrays.stream(activeAttachmentsIds).anyMatch((attachmentId) -> attachment == Item.getItemById(attachmentId));
	}

}