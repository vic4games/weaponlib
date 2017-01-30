package com.vicmatskiv.weaponlib.compatibility;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public abstract class CompatibleParticle extends EntityFX {

	public CompatibleParticle(World par1World, double positionX, double positionY, double positionZ, 
			double motionX, double motionY, double motionZ)
	{
		super(par1World, positionX, positionY, positionZ, motionX, motionY, motionZ);
	}
	
	protected void setExpired() {
		setDead();
	}
	
	protected boolean isCollided() {
		return this.onGround;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void renderParticle(Tessellator tessellator, float p_70539_2_, float p_70539_3_, float p_70539_4_,
			float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		renderParticle(CompatibleTessellator.getInstance(), p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
	}
	
	public abstract void renderParticle(CompatibleTessellator tessellator, float p_70539_2_, float p_70539_3_, float p_70539_4_,
			float p_70539_5_, float p_70539_6_, float p_70539_7_);

}
