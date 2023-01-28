package com.jimholden.conomy.client.gui.particles;

import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBlood extends Particle
{
    private final float lavaParticleScale;
    private TextureAtlasSprite texR;

    public ParticleBlood(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        //texR = new TextureAtlasSprite(BLOODPARTICLE.toString());
      //  texR.initSprite(16, 16, 0, 0, false);
        //this.motionX *= 0.800000011920929D;
       // this.motionY *= 0.800000011920929D;
        //this.motionZ *= 0.800000011920929D;
        this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
        this.lavaParticleScale = this.particleScale;
        this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        //this.setParticleTextureIndex(49);
    }
    
    

    public ParticleBlood(World worldIn, double posX, double posY, double posZ, double d, double e, double f, boolean isBlack) {
    	 super(worldIn, posX, posY, posZ, d, e, f);
         //texR = new TextureAtlasSprite(BLOODPARTICLE.toString());
       //  texR.initSprite(16, 16, 0, 0, false);
    	 
    	// System.out.println("yo" + d);
         this.motionX *= 0.0800000011920929D + d;
         this.motionY *= 0.0800000011920929D + e;
         this.motionZ *= 0.0800000011920929D + f;
         
        // System.out.println(this.motionX + " | " + this.motionY + " | " + this.motionZ);
         //this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
         if(isBlack) {
        	 this.particleRed = 0.0F;
         } else {
        	 this.particleRed = 1.0F;
         }
        
         this.particleGreen = 0.0F;
         this.particleBlue = 0.0F;
         this.particleScale *= this.rand.nextFloat() * 1.0F + 0.2F;
         this.lavaParticleScale = this.particleScale;
         //this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
         //this.setParticleTextureIndex(49);
	}


    
    public static final ResourceLocation BLOODPARTICLE = new ResourceLocation(Reference.MOD_ID + ":textures/gui/blood.png");

    /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        //float f = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
       // this.particleScale = this.lavaParticleScale * (1.0F - f * f);
    	Minecraft.getMinecraft().renderEngine.bindTexture(BLOODPARTICLE);
    	
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }

        float f = (float)this.particleAge / (float)this.particleMaxAge;



        this.motionY -= 0.04D;
        this.move(this.motionX, this.motionY, this.motionZ);
        
        
        //this.motionX *= 0.9990000128746033D;
       // this.motionY *= 0.9990000128746033D;
      //  this.motionZ *= 0.9990000128746033D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
        
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory
        {
            public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
            {
                return new ParticleBlood(worldIn, xCoordIn, yCoordIn, zCoordIn);
            }
        }
}