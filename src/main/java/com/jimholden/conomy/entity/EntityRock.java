package com.jimholden.conomy.entity;


import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRock extends EntityThrowable {

	public EntityRock(World world, EntityLivingBase thrower) {
		super(world, thrower);
		// TODO Auto-generated constructor stub
	}
	
	public EntityRock(World worldIn) {
		super(worldIn);
		
		// TODO Auto-generated constructor stub
	}

	/**
     * Handler for {@link World#setEntityState}
     */
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
    	
        if (id == 3)
        {
            for (int i = 0; i < 8; ++i)
            {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, Block.getStateId(Blocks.STONE.getDefaultState()));
            }
        }
    }

    @Override
    public void onUpdate() {
    	//this.rotationPitch += 36;
    //	System.out.println(this.getPosition());
    	super.onUpdate();
    }
    
    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
    	
        if (result.entityHit != null)
        {
            int i = 3;

            if (result.entityHit instanceof EntityBlaze)
            {
                i = 3;
            }

            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)i);
        }

        if(!this.world.isRemote) {
        	//ZombieTool.causeInvestigation(this.world, this.posX, this.posY, this.posZ, 15);
        }
        
        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte)3);
            this.setDead();
        }
    }

}
