package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vicmatskiv.weaponlib.compatibility.CompatibleCustomArmor;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityEquipmentSlot;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.model.ModelBaseRendererWrapper;
import com.vicmatskiv.weaponlib.model.WrappableModel;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class CustomArmor extends CompatibleCustomArmor implements ExposureProtection {

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
		private ModelBiped helmetModel;
		private String modelClassName;
		private String hudTextureName;
		//private Function<Integer, ModelBiped> modelFactory;
		private Map<ItemAttachment<CustomArmor>, CompatibleAttachment<CustomArmor>> compatibleAttachments = new HashMap<>();
        private CreativeTabs creativeTab;
        private boolean nightVision;
        private float exposureReductionFactor;
        private String breathingSound;

		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}

		public Builder withCreativeTab(CreativeTabs creativeTab) {
	        this.creativeTab = creativeTab;
	        return this;
	    }

		public Builder withTextureName(String textureName) {
			this.textureName = textureName.toLowerCase();
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
			this.modelClassName = modelClass;
			return this;
		}

		public Builder withHudTextureName(String hudTextureName) {
			this.hudTextureName = hudTextureName.toLowerCase();
			return this;
		}

		public Builder withNightVision(boolean nightVision) {
            this.nightVision = nightVision;
            return this;
        }
		
		public Builder withExposureReductionFactor(float exposureReductionFactor) {
            this.exposureReductionFactor = exposureReductionFactor;
            return this;
        }

		public Builder withBreathingSound(String sound) {
		    this.breathingSound = sound.toLowerCase();
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
					chestModel = (ModelBiped) Class.forName(modelClassName).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new IllegalStateException("Missing chest model", e);
				}

				try {
					bootsModel = (ModelBiped) Class.forName(modelClassName).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new IllegalStateException("Missing boots model", e);
				}
			}

			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}

			String unlocalizedHelmetName = unlocalizedName + "_helmet";
			CustomArmor armorHelmet = new CustomArmor(modId, material, 4, CompatibleEntityEquipmentSlot.HEAD,
					unlocalizedHelmetName, textureName, chestModel, hudTextureName);
			if(creativeTab != null) {
			    armorHelmet.setCreativeTab(creativeTab);
			}

			armorHelmet.setUnlocalizedName(unlocalizedHelmetName);
			compatibility.registerItem(armorHelmet, unlocalizedHelmetName.toLowerCase());

			String unlocalizedChestName = unlocalizedName + "_chest";
			CustomArmor armorChest = new CustomArmor(modId, material, 4, CompatibleEntityEquipmentSlot.CHEST,
					unlocalizedChestName, textureName, chestModel, hudTextureName);
			if(creativeTab != null) {
			    armorChest.setCreativeTab(creativeTab);
            }
			armorChest.setUnlocalizedName(unlocalizedChestName);
			compatibility.registerItem(armorChest, unlocalizedChestName.toLowerCase());

			String unlocalizedBootsName = unlocalizedName + "_boots";
			CustomArmor armorBoots = new CustomArmor(modId, material, 4, CompatibleEntityEquipmentSlot.FEET,
					unlocalizedBootsName, textureName, bootsModel, hudTextureName);
			if(armorBoots != null) {
			    armorBoots.setCreativeTab(creativeTab);
            }
			armorBoots.setUnlocalizedName(unlocalizedBootsName);
			compatibility.registerItem(armorBoots, unlocalizedBootsName.toLowerCase());
		}


		public CustomArmor buildHelmet(ModContext context) {
		    
			if(context.isClient()) {
			    //ModelBiped helmetModel = null;
				if(helmetModel == null) {
					try {
					    Class<?> modelClass = Class.forName(modelClassName);
						
						if(ModelBiped.class.isAssignableFrom(modelClass)) {
						    helmetModel = (ModelBiped) modelClass.newInstance();
						} else if(ModelBase.class.isAssignableFrom(modelClass)) {
						    helmetModel = new ModelBiped() {
	                            {
	                                this.bipedHead = new ModelBaseRendererWrapper((WrappableModel) modelClass.newInstance());
	                                this.bipedHeadwear.isHidden = true;
	                            }
	                        };
	                    }
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						throw new IllegalStateException("Missing helmet model", e);
					}
				}
			}

			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}

			String unlocalizedHelmetName = unlocalizedName + "_helmet";
			CustomArmor armorHelmet = new CustomArmor(modId, material, 4, CompatibleEntityEquipmentSlot.HEAD,
					unlocalizedHelmetName, textureName, helmetModel, hudTextureName);
			if(nightVision) {
			    armorHelmet.hasNightVision = true;
			}
			armorHelmet.exposureReductionFactor = this.exposureReductionFactor;
			armorHelmet.setUnlocalizedName(unlocalizedHelmetName);
			armorHelmet.breathingSound = context.registerSound(breathingSound);
			compatibility.registerItem(armorHelmet, unlocalizedHelmetName.toLowerCase());
			if(creativeTab != null) {
			    armorHelmet.setCreativeTab(creativeTab);
			}

			return armorHelmet;
		}

		public CustomArmor buildChest(boolean isClient) {

			if(isClient) {
				if(chestModel == null) {
					try {
						chestModel = (ModelBiped) Class.forName(modelClassName).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						throw new IllegalStateException("Missing chest model", e);
					}
				}
			}

			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}

			String unlocalizedChestName = unlocalizedName + "_chest";
			CustomArmor armorChest = new CustomArmor(modId, material, 4, CompatibleEntityEquipmentSlot.CHEST,
					unlocalizedChestName, textureName, chestModel, hudTextureName);
			if(creativeTab != null) {
			    armorChest.setCreativeTab(creativeTab);
            }
			armorChest.setUnlocalizedName(unlocalizedChestName);
			compatibility.registerItem(armorChest, unlocalizedChestName.toLowerCase());

			return armorChest;
		}

		public CustomArmor buildBoots(boolean isClient) {

			if(isClient) {

				if(bootsModel == null) {
					try {
						Class<?> modelClass = Class.forName(modelClassName);
						
						if(ModelBiped.class.isAssignableFrom(modelClass)) {
						    bootsModel = (ModelBiped) modelClass.newInstance();
                        } else if(ModelBase.class.isAssignableFrom(modelClass)) {
//                            bootsModel = new ModelBiped() {
//                                {
//                                    this.bipedHead = new ModelBaseRendererWrapper((WrappableModel) modelClass.newInstance());
//                                    this.bipedHeadwear.isHidden = true;
//                                }
//                            };
                        }
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						throw new IllegalStateException("Missing boots model", e);
					}
				}
			}

			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}


			String unlocalizedBootsName = unlocalizedName + "_boots";
			CustomArmor armorBoots = new CustomArmor(modId, material, 4, CompatibleEntityEquipmentSlot.FEET,
					unlocalizedBootsName, textureName, bootsModel, hudTextureName);
			if(creativeTab != null) {
			    armorBoots.setCreativeTab(creativeTab);
            }
			armorBoots.setUnlocalizedName(unlocalizedBootsName);
			compatibility.registerItem(armorBoots, unlocalizedBootsName.toLowerCase());

			return armorBoots;
		}
	}

	private Map<ItemAttachment<CustomArmor>, CompatibleAttachment<CustomArmor>> compatibleAttachments = new HashMap<>();

	//private CompatibleEntityEquipmentSlot slot;
	private boolean hasNightVision;
	private float exposureReductionFactor;
	private CompatibleSound breathingSound;

    private CompatibleEntityEquipmentSlot compatibleEquipmentType;

	private CustomArmor(String modId, ArmorMaterial material, int renderIndex,
	        CompatibleEntityEquipmentSlot armorType, String iconName, String textureName,
	        ModelBiped model, String hudTextureName) {
		super(modId, material, renderIndex, armorType, iconName.toLowerCase(), textureName, model, hudTextureName);
		this.compatibleEquipmentType = armorType;
	}

	public String getHudTexture() {
		return modId + ":" + "textures/hud/" + hudTextureName + ".png";
	}

	@SuppressWarnings("unchecked")
	public void changeAttachment(AttachmentCategory attachmentCategory, ItemStack itemStack, EntityPlayer player) {
		compatibility.ensureTagCompound(itemStack);

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

		compatibility.getTagCompound(itemStack).setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
	}

	private ItemAttachment<CustomArmor> nextCompatibleAttachment(AttachmentCategory category, Item currentAttachment, EntityPlayer player) {

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

	public ItemAttachment<CustomArmor> getActiveAttachment (ItemStack itemStack, AttachmentCategory category) {
		compatibility.ensureTagCompound(itemStack);

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
		compatibility.ensureTagCompound(itemStack);

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
		int activeAttachmentsIds[] = compatibility.getTagCompound(itemStack).getIntArray(ACTIVE_ATTACHMENT_TAG);

		if(activeAttachmentsIds == null || activeAttachmentsIds.length != AttachmentCategory.values.length) {
			activeAttachmentsIds = new int[AttachmentCategory.values.length];
			compatibility.getTagCompound(itemStack).setIntArray(ACTIVE_ATTACHMENT_TAG, activeAttachmentsIds);
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
    
    public boolean hasNightVision() {
        return hasNightVision;
    }

    @Override
    public Function<Float, Float> getAbsorbFunction(EntitySpreadable spreadable) {
        return dose -> dose * (1f - exposureReductionFactor);
    }
    
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
//        if(!worldIn.isRemote && entityIn != null) {
//            compatibility.ensureTagCompound(stack);
//            NBTTagCompound nbt = stack.getTagCompound();
//            long lastBreathTimestamp = nbt.getLong("LastBreathTimestamp");
//            
//            long breathingPeriodMillis;
//            if(entityIn.isSprinting()) {
//                long startRunningTimestamp = nbt.getLong("StartRunningTimestamp");
//                if(startRunningTimestamp == 0) {
//                    nbt.setLong("StartRunningTimestamp", System.currentTimeMillis());
//                }
//                long runningDuration = System.currentTimeMillis() - startRunningTimestamp;
//                float runningProgress = MiscUtils.clamp(((float)runningDuration) / 5000, 0.0f, 1.0f);
//                breathingPeriodMillis = 2000L - (long)(runningProgress * 1500);
//                System.out.println("Breathing period: " + breathingPeriodMillis);
//            } else {
//                nbt.setLong("StartRunningTimestamp", 0L);
//                breathingPeriodMillis = 2000;
//            }
//            
//            if(lastBreathTimestamp + breathingPeriodMillis < System.currentTimeMillis() 
//                    && entityIn instanceof EntityLivingBase) {
//                //compatibility.playSound((EntityLivingBase) entityIn, breathingSound, 1.0f, 1.0f);
//                compatibility.playSoundAtEntity((EntityLivingBase) entityIn, breathingSound, 1.0f, 1.0f);
//                System.out.println("Breathe!");
//                nbt.setLong("LastBreathTimestamp", System.currentTimeMillis());
//            }
//        }
    }

    public CompatibleEntityEquipmentSlot getCompatibleEquipmentSlot() {
        return compatibleEquipmentType;
    }

   

}