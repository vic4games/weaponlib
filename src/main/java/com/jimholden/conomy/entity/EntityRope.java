package com.jimholden.conomy.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import com.google.common.base.Optional;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.entity.data.Vec3dSerializer;
import com.jimholden.conomy.entity.data.VecListSerializer;
import com.jimholden.conomy.entity.rope.NonLivingLookHelper;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemRope;
import com.jimholden.conomy.util.VectorUtil;
import com.jimholden.conomy.util.packets.PlayerMotionPacket;
import com.jimholden.conomy.util.packets.UpdatePlayerRopePacket;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
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

public class EntityRope extends Entity
{
	
	
	
	// publicly accessible lists for rendering
	public ArrayList<Vec3d> vecList = new ArrayList<>();
	public ArrayList<BlockPos> blockList = new ArrayList<>();
	
	// data parameters
	protected static final DataParameter<Optional<UUID>> ROPER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityRope.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	public static final DataParameter<VecListSerializer> TEMPLATE = EntityDataManager.createKey(EntityRope.class, VecListSerializer.SERIALIZER);
	private static final DataParameter<ItemStack> ROPE_STACK = EntityDataManager.<ItemStack>createKey(EntityRope.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Float> CURRENT_LENGTH = EntityDataManager.createKey(EntityRope.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> FROZEN_LENGTH = EntityDataManager.createKey(EntityRope.class, DataSerializers.FLOAT);
	private static final DataParameter<Vec3dSerializer> MOTION_VECTOR = EntityDataManager.createKey(EntityRope.class, Vec3dSerializer.SERIALIZER);
	
	private NonLivingLookHelper lookHelper;
	public VecListSerializer vecSer = new VecListSerializer(new ArrayList<Vec3d>(), new ArrayList<BlockPos>());
	public Vec3dSerializer vec3dSer = new Vec3dSerializer(Vec3d.ZERO);
	
	public BlockPos fencePost;
	
	
	public EntityRope(World worldIn, BlockPos fence, EntityPlayer player) {
		super(worldIn);
		
		this.lookHelper = new NonLivingLookHelper(this);
		setPosition(fence.getX()+0.5, fence.getY(), fence.getZ()+0.5);
		//setSize(0.5F, 0.5F);
		this.fencePost = fence;
		
	}
	
	public EntityRope(World worldIn) {
		super(worldIn);
	}
	
	@Nullable
	public EntityPlayer getRoper() {
		
		try {
			if(getRoperId() != null) {
				return this.world.getPlayerEntityByUUID(getRoperId());
			} else {
				return null;
			}
		} catch(Exception e) {
			
			return null;
		}
		
	}
	
	
	/*
	 * GETTERS AND SETTERS FOR THE DATA MANAGER
	 */
	
	@Nullable
    public UUID getRoperId()
    {
		return (UUID)((Optional)this.dataManager.get(ROPER_UNIQUE_ID)).orNull();
    }
	
	public void setRoperId(@Nullable UUID uuid)
    {
		this.dataManager.set(ROPER_UNIQUE_ID, Optional.fromNullable(uuid));  
    }
	
    public ArrayList<Vec3d> getVecList()
    {
		return this.dataManager.get(TEMPLATE).vecList;
    }
	
	public void setVecList(ArrayList<Vec3d> vecL)
    {
		vecSer.setVecList(vecL);
		this.dataManager.set(TEMPLATE, vecSer);
    }
	
	public void setBlockPosList(ArrayList<BlockPos> vecL)
    {
		vecSer.setBlockPosList(vecL);
		this.dataManager.set(TEMPLATE, vecSer);
        
    }
	
	public ArrayList<BlockPos> getBlockPosList()
    {
		return this.dataManager.get(TEMPLATE).blockPosList;  
    }
	
	public ItemStack getRopeStack() {
		return this.dataManager.get(ROPE_STACK);
	}
	
	public void setRopeStack(ItemStack ropeStack) {
		this.dataManager.set(ROPE_STACK, ropeStack);
	}
	
	public float currentRopeLength() {
		return this.dataManager.get(CURRENT_LENGTH);
	}
	
	public void setCurrentRopeLength(float length) {
		this.dataManager.set(CURRENT_LENGTH, length);
	}
	
	public float currentFreezeLength() {
		return this.dataManager.get(FROZEN_LENGTH);
	}
	
	public void setCurrentFreezeLength(float length) {
		this.dataManager.set(FROZEN_LENGTH, length);
	}
	
	public void setMotionData(double x, double y, double z) {
		Vec3d v = new Vec3d(x, y, z);
		vec3dSer.setVec(v);
		this.dataManager.set(MOTION_VECTOR, vec3dSer);
	}
	
	public Vec3d getMotionData() {
		return this.dataManager.get(MOTION_VECTOR).getVec();
	}
	
	
	public NonLivingLookHelper getLookHelper() {
		if(this.lookHelper == null) {
			this.lookHelper = new NonLivingLookHelper(this);
		}
		return this.lookHelper;
	}
	
	public boolean isRopeStackEmpty() {
		if(getRopeStack().isEmpty()) return true;
		else return false;
	}
	
	public int getRopeType() {
		if(getRopeStack().isEmpty()) return 0;
		return ((ItemRope)getRopeStack().getItem()).ropeType;
		
	}
	
	@Override
	public boolean canBeCollidedWith() {
		// TODO Auto-generated method stub
		return true;
	}
	

	
	@Override
	public boolean hasNoGravity() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	
	
	
	

	
	
	//public EntityPlayer player;
	
	public static EntityRope createKnot(World worldIn, BlockPos fence, EntityPlayer player)
    {
    	EntityRope entityleashknot = new EntityRope(worldIn, fence, player);
        worldIn.spawnEntity(entityleashknot);
        return entityleashknot;
    }
	
	public boolean shouldRenderRope() {
		if(getRoper() == null || isRopeStackEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public Vec3d getRopeStart(float yaw) {
    	return (new Vec3d(0.3, 0.1, -0.02)).rotateYaw(yaw).add(this.getPositionVector());
    	
    }
	
	public void mountRope(EntityPlayer player) {
		setRoperId(player.getUniqueID());
		setCurrentFreezeLength(0.0F);
	}
	
	public void dismountRope() {
		EntityPlayer roper = getRoper();
		if(roper == null) return;
		ArrayList<Vec3d> vdl3 = getVecList();
		if(vdl3.isEmpty()) {
			vdl3.add(new Vec3d(this.posX, roper.getPositionVector().y, this.posZ));
		} else {
			Vec3d pVec = vdl3.get(vdl3.size()-1);
			vdl3.add(new Vec3d(pVec.x, roper.posY, pVec.z));
		}
		
		//Vec3d prevVelocity = getMotionData();
		setVecList(vdl3);
		blockList.add(roper.getPosition());
		
		setCurrentFreezeLength((float) getRopeLength(roper.getPositionVector()));
		//System.out.println("bro " + currentRopeLength());
		setRoperId(null);
		EntityRemountRope e = new EntityRemountRope(this.world, this);
        this.world.spawnEntity(e);
        //System.out.println(prevVelocity);
       // Main.NETWORK.sendTo(new PlayerMotionPacket(prevVelocity.x, prevVelocity.y, prevVelocity.z), (EntityPlayerMP) roper);
        
       
	}
	
	
	
	public boolean isHangingRope() {
		if(!isRopeStackEmpty() && getRoper() == null) {
			return true;
		} else return false;
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		World world = player.world;
		if(world.isRemote) return false;
		if(!player.isSneaking()) {
			
			ItemStack stack = player.getHeldItem(hand);
			if(!(stack.getItem() instanceof ItemRope)) return false;
			//System.out.println(stack);
			//System.out.println("hello");
			
			ItemStack newStack = stack.copy();
			newStack.setCount(1);
			player.swingArm(hand);
			//System.out.println(newStack);
			setRopeStack(newStack);
			stack.shrink(1);
			mountRope(player);
			return true;
		} else {
			if(hand == EnumHand.OFF_HAND) return false;
			EntityItem item = new EntityItem(world, posX, posY, posZ, new ItemStack(ModItems.CLIMBING_ANCHOR));
	        world.spawnEntity(item);
	        setDead();
			return true;
		}
		
		
		
		
	}
	
	public void shortenRope(float change) {
		float changedLen = currentRopeLength() + change;
		if(changedLen > 0.0F) {
			setCurrentRopeLength(changedLen);
		}
		
	}
	
	public void lengthenRope(float change) {
		float changedLen = currentRopeLength() + change;
		ItemStack ropeStack = getRopeStack();
		if(changedLen < ((ItemRope) ropeStack.getItem()).getRopeMaxLength(ropeStack)) {
			setCurrentRopeLength(changedLen);
		}
		
	}
	
	
	/*
	 * ROPE LOGIC ---- BEGINS VOLUME 1
	 * very epic rope stuff for ropey things
	 */
	
	
	/**
	 * Checks if the ray intercept hits the block's edge.
	 */
	public boolean isOnEdge(BlockPos pos, Vec3d vector) {
		//System.out.println(pos);
		double d = new Vec3d(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5).squareDistanceTo(vector);
		//System.out.println(d);
		if(d > 0.350) {
			return true;
		}
		return false;
	}
	
	/**
	 * Retrieves the current rope length
	 * @return
	 */
	public double getRopeLength(Vec3d endPoint) {
		double length = 0;
		
		
		if(!this.vecList.isEmpty()) {
			length += this.getPositionVector().distanceTo(vecList.get(0));
			if(vecList.size() > 1) {
				for(int x = 0; x < vecList.size()-1; ++x) {
					length += vecList.get(x).distanceTo(vecList.get(x+1));
				}
			}
			length += vecList.get(vecList.size()-1).distanceTo(endPoint);
			return length;
		} else {
			length = this.getPositionVector().distanceTo(endPoint);
			return length;
		}
	}
	
	/**
	 * Performs an update on the length update. If the player
	 * is past the maximum length of the rope, they will be
	 * pulled back.
	 */
	public void lengthLogicUpdate(EntityPlayer roper) {
		double maxLength = currentRopeLength();
		double ropeElas = ((ItemRope) this.getRopeStack().getItem()).getElasticity();
		
		double elas = 0.5;
		double stretch = 0.01;
		double ropeLen = getRopeLength(roper.getPositionVector());
		//System.out.println(getRopeLength(roper.getPositionVector()));
		//System.out.println("yo");
		//if(ropeLen > maxLength) {
		if(1+1==2) {
			//System.out.println("yo2");
			Vec3d usedVec;
			
			Vec3d motVec;
			Vec3d diffVec;
			if(vecList.isEmpty()) {
				diffVec = getPositionVector().subtract(roper.getPositionVector());
				
				
			} else {
				diffVec = vecList.get(vecList.size()-1).subtract(roper.getPositionVector());
			}
			double pull = ropeLen-maxLength;
			if(pull < 0) pull = 0;
			motVec = diffVec.normalize().scale(pull);
			//motVec = diffVec.normalize().scale(pull);
			
			//System.out.println(motVec);
			
			
			//System.out.println(motVec.lengthVector());
			double reducer = ((ropeElas/50.0F));
			//System.out.println("hello");
			//System.out.println(motVec.x*reducer*20);
		
			/*
			 * if( amITethered )
{
    if (testPosition - tetherPoint ).Length() > tetherLength )
    {
        // we're past the end of our rope
        // pull the avatar back in.
        testPosition = (testPosition - tetherPoint).Normalized() * tetherLength;
    }
}
			 */
			
			//System.out.println(roper.motionX + " | " + roper.motionY + " | " + roper.motionZ);
			
				
	
			 roper.motionX += motVec.x*reducer*2;
			roper.motionY += motVec.y*reducer;
			roper.motionZ += motVec.z*reducer*2;
			 
			Vec3d motVec2 = new Vec3d(roper.motionX, roper.motionY, roper.motionZ);
			//Vec3d tV = motVec2.normalize().crossProduct(diffVec.normalize()).crossProduct(diffVec.normalize()).scale(0.01);
			
			//Vec3d tV = diffVec.crossProduct(motVec2);
			//System.out.println(tV);
			
			Vec3d tV = motVec2.crossProduct(motVec).crossProduct(motVec).normalize().scale(0.02);
			
			roper.motionX -= tV.x;
			roper.motionY -= tV.y;
			roper.motionZ -= tV.z; 
			
			setMotionData(roper.motionX, roper.motionY, roper.motionZ);
			getDataManager().setDirty(MOTION_VECTOR);
			
		
			
			//double shavings = roper.motionY
			
			//System.out.println(roper.motionX + " | " + roper.motionY + " | " + roper.motionZ);
			//roper.motionX *= stretch;
			//roper.motionY *= stretch;
			//roper.motionZ *= stretch;
		}
	}
	
	public void setFreezeLength(float len) {
		
	}
	
	
	
	/**
	 * Unhinges vertices below the player
	 */
	public void unhingeVertBelow(EntityPlayer roper) {
		
		
		if(roper == null) return;
		
		if(!vecList.isEmpty() && !blockList.isEmpty()) {
			for(int p = 0; p < vecList.size(); ++p) {
				RayTraceResult ray = world.rayTraceBlocks(vecList.get(p), roper.getPositionVector(), false, true, false);
				//System.out.println(ray);
				
				if(ray == null) {
					vecList.remove(p);
					if(blockList.size()-1 >= p) {
						blockList.remove(p);
					}
					
				}
				/*
				if(roper.posY > vecList.get(p).y) {
					vecList.remove(p);
					blockList.remove(p);
				}
				*/
			}
		}
		
	}
	
	public void ropeContactUpdate(EntityPlayer roper) {
		
		RayTraceResult ray;
		World world = roper.world;
		if(getVecList().isEmpty()) {
			//System.out.println(this.getPositionVector() + " | " + roper.getPositionVector());
			
			ray = world.rayTraceBlocks(this.getPositionVector().addVector(0.0, 0.3, 0.0), roper.getPositionVector(), false, true, false);
		} else {
			Vec3d chosen = this.vecList.get(vecList.size()-1).add(new Vec3d(0.5, 0.5, 0.5));
			
			ray = world.rayTraceBlocks(chosen, roper.getPositionVector(), false, true, false);
		}
		//System.out.println("hi " + ray);
		//System.out.println(ray);
		
		if(ray != null) {

			if(!blockList.contains(ray.getBlockPos()) && isOnEdge(ray.getBlockPos(), ray.hitVec)) {
				//System.out.println("hello");
				vecList.add(ray.hitVec);
				blockList.add(ray.getBlockPos());
				
				//setVecList(vecList);
			}
		}
	}
	
	

	/*
	 * ROPE LOGIC END
	 */

	@Override
	public void onUpdate() {
		
		//System.out.println(this.getPositionVector());
		
		//System.out.println(world.isRemote + " | " + isRopeStackEmpty());
		if(isRopeStackEmpty()) return;
		//synchrotest
		if(!world.isRemote) {
			//System.out.println(this.vecList);
			setVecList(this.vecList);
			setBlockPosList(this.blockList);
			
			//optimize this so it's not constantly updating!
			getDataManager().setDirty(TEMPLATE);
			
			
			//System.out.println("Fuck: " + this.dataManager.get(TEMPLATE).vecList);
			//System.out.println("Server V3DL: " + vecList);
		}
		if(world.isRemote) {
			
			vecList = getVecList();
			blockList = getBlockPosList();
			
			//System.out.println(getVecList());
			//System.out.println("Fuck: " + this.dataManager.get(TEMPLATE).vecList);
			//System.out.println("Fuck: " + this.dataManager.get(TEMPLATE).vecList);
			//System.out.println("Client V3DL: " + vecList);
		}
		
		EntityPlayer roper = getRoper();
	
		
		if(this.lookHelper != null) {
			this.lookHelper.onUpdateLook();
		} else {
			lookHelper = new NonLivingLookHelper(this);
		}
		//System.out.println("yo");	
		
		if(!this.world.isRemote) {
			if(roper == null) return;
			//System.out.println("yo");
			
			
			// rope should unhinge vertices below player
			unhingeVertBelow(roper);
			
			// should hook a new vert
			ropeContactUpdate(roper);	
			
			
		} else {
			// rope should pull back on the player
			if(roper == null) return;
						lengthLogicUpdate(roper);
		}
			
		super.onUpdate();
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(ROPER_UNIQUE_ID, Optional.absent());
		this.dataManager.register(TEMPLATE, new VecListSerializer(new ArrayList<Vec3d>(), new ArrayList<BlockPos>()));
		this.dataManager.register(ROPE_STACK, ItemStack.EMPTY);
		this.dataManager.register(CURRENT_LENGTH, 15.0F);
		this.dataManager.register(FROZEN_LENGTH, 0.0F);
		this.dataManager.register(MOTION_VECTOR, vec3dSer);
		setSize(0.3F, 0.3F);
		//System.out.println(this.getPositionVector());
		
		//setEntityBoundingBox(new AxisAlignedBB(posX-0.5, posY-0.5, posZ-0.5, posX+0.5, posY+0.5, posZ+0.5));
	}

	/*
	 * NBT STUFF
	 */
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		//System.out.println("reed calleds");
		
		if(compound.hasKey("frozenLen")) {
			setCurrentFreezeLength(compound.getFloat("frozenLen"));
		}
		
		if (compound.hasKey("RoperUUID", 8))
        {
            String uuidS = compound.getString("RoperUUID");
            if(uuidS != null && !uuidS.isEmpty()) {
            	setRoperId(UUID.fromString(uuidS));
            }
        }
		
		//System.out.println("hi" + compound.getKeySet());
		if(compound.hasKey("vectorList")) {
			
			ArrayList<Vec3d> vec3dL = new ArrayList<>();
			 NBTTagList tagList = compound.getTagList("vectorList", Constants.NBT.TAG_COMPOUND);
			 for(int i = 0; i < tagList.tagCount(); i++)
			 {
			  NBTTagCompound tag = tagList.getCompoundTagAt(i);
			  double x = tag.getDouble("x");
			  double y = tag.getDouble("y");
			  double z = tag.getDouble("z");
			  vec3dL.add(new Vec3d(x, y, z));
			 }
			 vecList = vec3dL;
		}
		if(compound.hasKey("blockPosList")) {
			ArrayList<BlockPos> bpL = new ArrayList<>();
			 NBTTagList tagList = compound.getTagList("blockPosList", Constants.NBT.TAG_COMPOUND);
			 for(int i = 0; i < tagList.tagCount(); i++)
			 {
			  NBTTagCompound tag = tagList.getCompoundTagAt(i);
			  long bpLong = tag.getLong("long");
			  bpL.add(BlockPos.fromLong(bpLong));
			 }
			 blockList = bpL;
		}
		
		if(compound.hasKey("ropeStack")) {
			NBTTagCompound stackCompound = compound.getCompoundTag("ropeStack");
			setRopeStack(new ItemStack(stackCompound));
		}
		
//		/compound.getString("roper").
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
		compound.setFloat("frozenLen", currentFreezeLength());
		
		if(!vecList.isEmpty()) {
			
			NBTTagList tList = new NBTTagList();
			for(Vec3d vecTW : vecList) {
				NBTTagCompound comp = new NBTTagCompound();
				comp.setDouble("x", vecTW.x);
				comp.setDouble("y", vecTW.y);
				comp.setDouble("z", vecTW.z);
				tList.appendTag(comp);
			}
			//System.out.println("saving list");
			compound.setTag("vectorList", tList);
			
		}
		if(!blockList.isEmpty()) {
			
			NBTTagList tList = new NBTTagList();
			for(BlockPos bpTW : blockList) {
				NBTTagCompound comp = new NBTTagCompound();
				comp.setDouble("long", bpTW.toLong());
				tList.appendTag(comp);
			}
			
			compound.setTag("blockPosList", tList);
			
		}
		
		if (this.getRoperId() == null)
        {
            compound.setString("RoperUUID", "");
        }
        else
        {
            compound.setString("RoperUUID", this.getRoperId().toString());
        }
		
		NBTTagCompound stackCompound = new NBTTagCompound();
		getRopeStack().writeToNBT(stackCompound);
		compound.setTag("ropeStack", stackCompound);
	}
	


	
}