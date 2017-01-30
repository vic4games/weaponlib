package com.vicmatskiv.weaponlib;


import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class SpawnEntityRenderer extends Render {

    
    @Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
    	WeaponSpawnEntity weaponSpawnEntity = (WeaponSpawnEntity) entity;
    	Weapon weapon = weaponSpawnEntity.getWeapon();
		if(weapon == null) {
    		return;
    	}
    	ModelBase model = weapon.getAmmoModel();
    	if(model != null) {
    		String ammoModelTextureName = weapon.getAmmoModelTextureName();
			ResourceLocation textureLocation = ammoModelTextureName != null ? new ResourceLocation(ammoModelTextureName) : null;
        	if(model != null) {
        		GL11.glPushMatrix();
        		if(textureLocation != null) {
        			bindTexture(textureLocation);
        		}
        		GL11.glTranslated(x, y, z);
        		model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        		GL11.glPopMatrix();
        	}
    	}
    	
	}

    @Override
    protected ResourceLocation getEntityTexture(Entity entity){
    	WeaponSpawnEntity weaponSpawnEntity = (WeaponSpawnEntity) entity;
        return new ResourceLocation(weaponSpawnEntity.getWeapon().getAmmoModelTextureName());
    }

  
}