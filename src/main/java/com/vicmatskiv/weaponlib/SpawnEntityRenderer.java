package com.vicmatskiv.weaponlib;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityRenderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class SpawnEntityRenderer extends CompatibleEntityRenderer {

	@Override
	public void doCompatibleRender(Entity entity, double x, double y, double z, float yaw, float tick) {
	    if(/*entity.distanceWalkedModified < 1.5f */entity.ticksExisted < 2) {
	        return;
	    }
    		WeaponSpawnEntity weaponSpawnEntity = (WeaponSpawnEntity) entity;
    		
    		//System.out.println("Rendering entity with aim tan: " + weaponSpawnEntity.getAimTan());
    		Weapon weapon = weaponSpawnEntity.getWeapon();
    		if(weapon == null) {
        		return;
        	}
        	ModelBase model = weapon.getAmmoModel();
        //	System.out.println("Tick: " + entity.ticksExisted);
        	if(model != null) {
        		String ammoModelTextureName = weapon.getAmmoModelTextureName();
    			ResourceLocation textureLocation = ammoModelTextureName != null ? new ResourceLocation(ammoModelTextureName) : null;
            	if(model != null) {
            		GL11.glPushMatrix();
            		if(textureLocation != null) {
            			bindTexture(textureLocation);
            		}
            		
            		double xxofset = 0.4 - entity.distanceWalkedModified * weaponSpawnEntity.getAimTan();
            		double angle = Math.atan(weaponSpawnEntity.getAimTan());
            		// 360 -> 2p, x -> angle, x = 360 * angle / 2pi = 180 * angle /pi
            		double zOffset = xxofset * Math.cos(entity.rotationYaw / 180.0F * (float) Math.PI);
            		double xOffset = xxofset * Math.sin(entity.rotationYaw / 180.0F * (float) Math.PI);
            		//System.out.println("Xoffset: " + xOffset + ", zoffset: " + zOffset);
            		GL11.glTranslated(x + xOffset, y, z + zOffset);
//            		GL11.glRotatef(90, 1f, 0f, 0f);
//            		GL11.glRotatef(90, 0f, 0f, 1f);
//            		GL11.glRotatef(45, 0f, 1f, 0f);
            		GL11.glRotatef(0f, 1f, 0f, 0f);
            		GL11.glRotatef(entity.rotationYaw - 90 + (float)(angle * 180 / Math.PI), 0f, 1f, 0f);
            		GL11.glRotatef(90f + entity.rotationPitch, 0f, 0f, 1f);
            		GL11.glScalef(2f, 2f, 2f);
            		model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            		GL11.glPopMatrix();
            	}
        	}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		WeaponSpawnEntity weaponSpawnEntity = (WeaponSpawnEntity) entity;
        return new ResourceLocation(weaponSpawnEntity.getWeapon().getAmmoModelTextureName());
	}


}