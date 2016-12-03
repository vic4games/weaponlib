package com.vicmatskiv.weaponlib;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpawnEntityRenderer extends Render<WeaponSpawnEntity>
{
    //private ResourceLocation textureLocation;
//    private ModelBase model;

//    public SpawnEntityRenderer(/*ModelBase model, String textureName*/) {
////    	this.model = model;
////    	this.textureLocation = new ResourceLocation(textureName);
//    }
    
    protected SpawnEntityRenderer(RenderManager renderManager) {
		super(renderManager);
		// TODO this.model = model;
		// TODO this.textureLocation = new ResourceLocation(textureName);
	}

	@Override
	public void doRender(WeaponSpawnEntity weaponSpawnEntity, double x, double y, double z, float yaw, float tick) {
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
        		model.render(weaponSpawnEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        		GL11.glPopMatrix();
        	}
    	}
	}

	@Override
	protected ResourceLocation getEntityTexture(WeaponSpawnEntity entity) {
		WeaponSpawnEntity weaponSpawnEntity = (WeaponSpawnEntity) entity;
        return new ResourceLocation(weaponSpawnEntity.getWeapon().getAmmoModelTextureName());
	}

  
}