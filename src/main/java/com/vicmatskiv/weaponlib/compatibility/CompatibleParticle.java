package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CompatibleParticle extends Particle {
    
    public static class CompatibleParticleBreaking extends ParticleBreaking {
        protected CompatibleParticleBreaking(World worldIn, double posXIn, double posYIn, double posZIn, Item itemIn) {
            super(worldIn, posXIn, posYIn, posZIn, itemIn);
        }
    }
    
    public static CompatibleParticleBreaking createParticleBreaking(World worldIn, double posXIn, double posYIn, double posZIn, Item itemIn) {
        return new CompatibleParticleBreaking(worldIn, posXIn, posYIn, posZIn, itemIn);
    }

	public CompatibleParticle(World par1World, double positionX, double positionY, double positionZ, 
			double motionX, double motionY, double motionZ)
	{
		super(par1World, positionX, positionY, positionZ, motionX, motionY, motionZ);
	}

	protected boolean isCollided() {
		return this.isCollided;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		renderParticle(CompatibleTessellator.getInstance(), partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}
	
	public abstract void renderParticle(CompatibleTessellator tessellator, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ);
	
}
