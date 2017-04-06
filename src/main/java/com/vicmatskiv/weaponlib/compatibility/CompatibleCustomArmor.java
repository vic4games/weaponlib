package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CompatibleCustomArmor extends ItemArmor {
	
	//private static final String ACTIVE_ATTACHMENT_TAG = "ActiveAttachments";
	
	//private String iconName;
	protected String textureName;
	protected ModelBiped model;
	protected String hudTextureName;
	protected String modId;
	
	protected CompatibleCustomArmor(String modId, ArmorMaterial material, int renderIndex, CompatibleEntityEquipmentSlot armorType, String iconName, String textureName,
			ModelBiped model, String hudTextureName) {
		super(material, renderIndex, armorType.getSlot());
		this.modId = modId;
		//this.iconName = iconName;
		this.textureName = textureName;
		this.model = model;
		this.hudTextureName = hudTextureName;
		/*setCreativeTab(ProjectXureosWarfareMod.faattachmentsTab);*/
	}
	
	public String getHudTexture() {
		return modId + ":" + "textures/hud/" + hudTextureName + ".png";
	}
	
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
			
			if (itemStack.getItem() instanceof CompatibleCustomArmor) {
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

}