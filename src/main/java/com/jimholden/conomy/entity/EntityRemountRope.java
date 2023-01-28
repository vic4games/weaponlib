package com.jimholden.conomy.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import com.google.common.base.Optional;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.entity.data.VecListSerializer;
import com.jimholden.conomy.entity.rope.NonLivingLookHelper;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemRope;
import com.jimholden.conomy.util.VectorUtil;
import com.jimholden.conomy.util.packets.UpdatePlayerRopePacket;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public class EntityRemountRope extends Entity
{
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	// data parameters
	private static final DataParameter<BlockPos> ANCHOR_POS = EntityDataManager.createKey(EntityRemountRope.class, DataSerializers.BLOCK_POS);
	
	private EntityRope anchorage;
	
	public EntityRemountRope(World worldIn) {
		super(worldIn);
	}
	
	public EntityRemountRope(World worldIn, EntityRope rope) {
		super(worldIn);
		//System.out.println("Shit");
		setAnchorPoint(rope.getPosition());
		//System.out.println("hello");
	//	Vec3d last = rope.getVecList().get(rope.getVecList().size()-1);
		//System.out.println("Fuck " + last);
		//setPosition(last.x, last.y, last.z);
		
		
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	} 
	
	public void setAnchorPoint(BlockPos pos) {
		this.dataManager.set(ANCHOR_POS, pos);
	}
	
	public BlockPos getAnchorage() {
		return this.dataManager.get(ANCHOR_POS);
	}
	

	@Override
	protected void entityInit() {
		this.dataManager.register(ANCHOR_POS, new BlockPos(0, 0, 0));
		setSize(0.3F, 0.3F);
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			System.out.println("hello");
			anchorage.mountRope(player);
			setDead();
		}
		return super.processInitialInteract(player, hand);
	}

	
	public void lengthLogicUpdate(EntityRope roper) {
		double maxLength = roper.currentFreezeLength();
		double elas = 0.0005;
		double stretch = 0.01;
		
		
		double ropeLength = roper.getRopeLength(this.getPositionVector());
		//System.out.println("hi " + ropeLength + " | " + maxLength);
		if(ropeLength > maxLength) {
			//System.out.println("hif1");
			Vec3d motVec;
			
			Vec3d diffVec;
			ArrayList<Vec3d> vecList = roper.vecList;
			if(vecList.size() < 2) {
				diffVec = roper.getPositionVector().subtract(this.getPositionVector());
				
			} else {
				diffVec = vecList.get(vecList.size()-2).subtract(this.getPositionVector());
			}
			double pull = ropeLength-maxLength;
			
			
			
			motVec = diffVec.normalize().scale(pull);
			
			
			//System.out.println(motVec);
			this.motionX += motVec.x*elas;
			this.motionY += motVec.y*elas;
			this.motionZ += motVec.z*elas;
		}
	}

	

	
	@Override
	public void onUpdate() {
		
		if(anchorage == null) {
			for(Entity ent : world.loadedEntityList) {
				boolean flag = VectorUtil.areBlockPosEqual(ent.getPosition(), getAnchorage());
				if(ent instanceof EntityRope && flag) {
					
					if(!((EntityRope) ent).getVecList().isEmpty()) {
						anchorage = (EntityRope) ent;
						
						Vec3d last = anchorage.getVecList().get(anchorage.getVecList().size()-1);
						//System.out.println("Fuck");
						setPosition(last.x, last.y, last.z);
					}
					
				}
			}
		}
		if(anchorage != null && anchorage.getVecList() != null && !anchorage.getVecList().isEmpty() && world.isRemote) {
			/*
			Vec3d last = anchorage.getVecList().get(anchorage.getVecList().size()-1);
			//setPosition(this.anchorage.posX, this.anchorage.posY, this.anchorage.posZ);
			this.motionY -= 0.08D;
			lengthLogicUpdate(anchorage);
			
			
			//System.out.println(this.motionX + " | " + this.motionY + " | " + this.motionZ);
			this.motionX *= 0.9900000095367432D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9900000095367432D;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            System.out.println(this.motionX + " | " + this.motionY + " | " + this.motionZ);
            ArrayList<Vec3d> vdl = anchorage.getVecList();
            vdl.set(anchorage.getVecList().size()-1, VectorUtil.ptI(this));
           anchorage.setVecList(vdl);
           */
            
		}
		
		super.onUpdate();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setLong("anchorPoint", getAnchorage().toLong());
	}
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		setAnchorPoint(BlockPos.fromLong(compound.getLong("anchorPoint")));
	}
	
	
	
}