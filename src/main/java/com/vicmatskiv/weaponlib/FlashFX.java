package com.vicmatskiv.weaponlib;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FlashFX extends EntityFX {
	
	private static final float FLASH_ALPHA_FACTOR = 0.8f;

	private static final double FLASH_SCALE_FACTOR = 1.1;
	
	private static final String DEFAULT_PARTICLES_TEXTURE = "textures/particle/particles.png";
	private static final String FLASH_TEXTURE = "weaponlib:/com/vicmatskiv/weaponlib/resources/flashes.png";
		
	private int imageIndex;
	
    private static final int imagesPerRow = 4;
    
	public FlashFX(World par1World, double positionX, double positionY, double positionZ, 
			float scale, float alpha,
			float motionX, float motionY, float motionZ)
	{
		super(par1World, positionX, positionY, positionZ, 0.0D, 0.0D, 0.0D);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		
		if (motionX == 0.0F) {
			motionX = 0.01F;
		}
		
		if (motionZ == 0.0F) {
			motionZ = 0.01F;
		}
		
		if (motionY == 0.0F) {
			motionY = 0.01F;
		}
		
		this.particleTextureIndexX = 0; 
		this.particleTextureIndexY = 0;
		this.particleRed = 1.0F;
		this.particleGreen = 1.0F;
		this.particleBlue = 1.0F;
		this.particleAlpha = alpha;
		this.particleScale *= 1.4F;
		this.particleScale *= scale;
		this.particleMaxAge = 3;
		
		this.imageIndex = this.rand.nextInt() % imagesPerRow;
	}
	
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        
        this.motionX *= 0.9999999785423279D;
        this.motionY *= 0.9999999785423279D;
        this.motionZ *= 0.9999999785423279D;

        this.particleAlpha *= FLASH_ALPHA_FACTOR;
        
        this.particleScale *= FLASH_SCALE_FACTOR;
        
        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
	}
    
    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float par3, float par4, float par5, float par6, float par7)
    {
    	VertexFormat currentFormat = worldRendererIn.getVertexFormat();
    	Tessellator tessellator = Tessellator.getInstance();
    	tessellator.draw();
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(FLASH_TEXTURE));
		
		worldRendererIn.begin(GL11.GL_QUADS, currentFormat);
    	
    	//tesselator.setBrightness(200);
        int i = this.getBrightnessForRender(partialTicks); // or simply set it to 200?
        int j = i >> 16 & 65535;
        int k = i & 65535;
    	
        float f10 = 0.1F * this.particleScale;

        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        
        //tesselator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        
        /*
         *  (cU, cV)   (bU, bV)
         * 
         *  (dU, dV)   (aU, aV)
         * 
         */
        float uWidth = 1f / imagesPerRow;
        
		float aU = (imageIndex + 1) * uWidth; // imageIndex = 0, imagesPerRow = 2, aU = 0.5; imageIndex = 1, aU = 1
			// imagesPerRow = 4; imageIndex = 1; aU = 2/4 = 0.5
        float aV = 1f;
        
        float bU = (imageIndex + 1) * uWidth;
        float bV = 0f;
        
        float cU = imageIndex * uWidth; // imageIndex = 0, imagesPerRow = 2, cU = 0; imageIndex = 1, cU = 0.5
        float cV = 0f;
        
        float dU = imageIndex * uWidth;
        float dV = 1f;
        
        worldRendererIn.pos((double)(f11 - par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 - par5 * f10 - par7 * f10)).tex(aU, aV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double)(f11 - par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 - par5 * f10 + par7 * f10)).tex(bU, bV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double)(f11 + par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 + par5 * f10 + par7 * f10)).tex(cU, cV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        worldRendererIn.pos((double)(f11 + par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 + par5 * f10 - par7 * f10)).tex(dU, dV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    	
        tessellator.draw();
    	
    	Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(DEFAULT_PARTICLES_TEXTURE));
    	worldRendererIn.begin(GL11.GL_QUADS, currentFormat);
    }
}