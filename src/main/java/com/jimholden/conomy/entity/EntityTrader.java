package com.jimholden.conomy.entity;

import java.util.ArrayList;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.economy.data.Trade;
import com.jimholden.conomy.util.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityTrader extends EntityCreature {
	
	public ArrayList<Trade> tradeData = new ArrayList<>();

	public EntityTrader(World worldIn) {
		super(worldIn);
		this.setSize(1.4F, 2.0F);
		
		
		if(!worldIn.isRemote) {
		//	tradeData.add(new Trade(40, 38, 300, new ItemStack(Items.ACACIA_BOAT)));
		}
	}
	
	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		// TODO Auto-generated method stub
		player.openGui(Main.instance, Reference.GUI_TRADER, world, getEntityId(), 0, 0);
		return super.processInteract(player, hand);
	}
	
	
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		// TODO Auto-generated method stub
		return super.applyPlayerInteraction(player, vec, hand);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		
		NBTTagList list = new NBTTagList();
		for(Trade t : this.tradeData) {
			list.appendTag(t.writeNBT());
		}
		compound.setTag("tradeData", list);
		System.out.println("saved trades " + compound);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		
		NBTTagList list = compound.getTagList("tradeData", NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); ++i) {
			this.tradeData.add(Trade.readNBT((NBTTagCompound) list.get(i)));
		}
		
		System.out.println("loaded trades " + compound);
	}
	
	@Override
	protected boolean canDespawn() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}
	
	@Override
	public void performHurtAnimation() {
		// TODO Auto-generated method stub
		//super.performHurtAnimation();
	}
	
	@Override
	public void setDead() {
		// TODO Auto-generated method stub
		//super.setDead();
	}

	public void killTrader() {
		super.setDead();
	}
	
	@Override
	public boolean attackable() {
		System.out.println("hi");
		return false;
	}
	
	
	
}
