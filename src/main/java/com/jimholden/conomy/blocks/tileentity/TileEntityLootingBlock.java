package com.jimholden.conomy.blocks.tileentity;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeFactory;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.LootingBlockBase;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.clans.EnumRank;
import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;
import com.jimholden.conomy.looting.keycards.CLIDManager;
import com.jimholden.conomy.main.ModEventHandler.NodeChooser;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;

import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityLootingBlock extends TileEntity{


	public ItemStackHandler lootboxInventory;

	// RANDOM NUMBER GENERATOR
	public static final Random rand = new Random();
	
	
	private String customName;

	public float lootingTime = 50.0F;

	public ArrayList<Integer> forcedAnimList = new ArrayList();
	
	public Instant openTime;

	public Duration dur;
	
	public boolean isSingle;
	
	// looting ID
	public int clid;
	
	
	public IBlockState getState() {
		return this.world.getBlockState(getPos());
	}
	
	
	// https://forums.minecraftforge.net/topic/60853-1121tileentity-how-do-i-sync-data-between-server-and-client/
	public void sendUpdates() {
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, getState(), getState(), 3);
		world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
		markDirty();
	}
	
	@Override
	public void onLoad() {
		this.lootingTime = ((LootingBlockBase) this.getBlockType()).getLootingTime();
		this.dur = ((LootingBlockBase) this.getBlockType()).getCooldownDuration();
		this.isSingle = ((LootingBlockBase) this.getBlockType()).hasOnlySingleSlot();
		this.clid = ((LootingBlockBase) this.getBlockType()).getCLID();
		
		if(this.isSingle) {
			this.lootboxInventory = new ItemStackHandler(1);
		} else {
			this.lootboxInventory = new ItemStackHandler(14);
		}
		
		super.onLoad();
	}
	
	/*
	 * GETTERS
	 */
	
	public boolean hasOnlySingleSlot() {
		return this.isSingle;
	}
	
	
	public static class ItemChooser extends WeightedRandom.Item
	{
		public final ItemStack stack;
		public ItemChooser(ItemStack stack, int itemWeightIn) {
			super(itemWeightIn);
			this.stack = stack;
		}
		
	}
	
	public boolean isOnCooldown() {
		// if this has never been opened before, obviously
		// it's not on cooldown
		if(this.openTime == null) return false;
		
		// is the time equal to when it's done cooling down,
		// or is it after?
		Instant now = Instant.now();
		if(now.equals(openTime) || now.isAfter(openTime)) {
			return false;
		}
		
		// if neither of the above, then it's on cooldown
		return true;
	}
	
	
	public void setCooldown() {
		
		// Takes the current time, adds the duration
		// of the cooldown, and saves it.
		this.openTime = Instant.now().plus(this.dur);
		sendUpdates();

	}
	

	
	

	
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.lootcrate";
	}

	public boolean hasCustomName() {
		return this.customName != null && !this.customName.isEmpty();
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
		
	}
	
	
	

	
	
	public ItemStack getRandomStack() {
		
		ItemStackHandler lootTable = CLIDManager.getHandlerFromCLID(this.clid);
		
		int ran = rand.nextInt(lootTable.getSlots());
		return lootTable.getStackInSlot(ran);
		/*
		System.out.println("Handler: ");
		for(int x = 0; x < handler.getSlots(); x++) {
			System.out.println(handler.getStackInSlot(x));
		}
		
		System.out.println("Inv: ");
		for(int x = 0; x < inventory.size(); x++) {
			System.out.println(inventory.get(x));
		}
		System.out.print("RandomThingChhosen: " + handler.getSlots());
		
		Map<ItemStack, Integer> avaliableSlots = new ConcurrentHashMap<>();
		//ArrayList<Map.Entry<ItemStack, Integer>> avaliableSlots = new ArrayList();
		for(int x = 0; x < handler.getSlots(); x++) {
			int power = getSlotPower(x);
			if(!handler.getStackInSlot(x).isEmpty())
			{
				avaliableSlots.put(handler.getStackInSlot(x), power);
				//avaliableSlots.add((handler.getStackInSlot(x), power));
			}
		}
		for(Map.Entry<ItemStack, Integer> stack : avaliableSlots.entrySet())
		{
			addItemToChoice(stack.getKey(), stack.getValue());
		}
		
		
		Random rand = new Random();
		ItemChooser winner = (ItemChooser) WeightedRandom.getRandomItem(rand, selList);
		this.selList.clear();
		ItemStack chosenStack = winner.stack;
		
		
		
		/*
		Random rn = new Random();
		int minimum = 0;
		int maxValue = avaliableSlots.size()-1;
		System.out.println("here's the handler: " + handler.getStackInSlot(0));
		int randIndex = minimum + rn.nextInt(maxValue - minimum + 1);
		
		
		return chosenStack.copy(); */
		
	}
	

	
	public void addLootedItems() {
		for(int x = 0; x < lootboxInventory.getSlots(); x++) {
			
			//System.out.println("adding to " + x);
			ItemStack randomStack = getRandomStack();
			if(randomStack.isEmpty()) {
				
				forcedAnimList.add(x);
			} else {
				lootboxInventory.setStackInSlot(x, randomStack);
			}
			
			/*
			int chance = rand.nextInt(100 - 0) + 0;
			if(chance < 10) {
				forcedAnimList.add(count);
			} else {
				handlerEmpty.setStackInSlot(x, getRandomStack());
			}*/
			
		}
	}


	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = new NBTTagCompound();
		if(this.openTime == null) this.openTime = Instant.now();
		compound.setString("ot", this.openTime.toString());
		return compound;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		this.openTime = Instant.parse(tag.getString("ot"));
		System.out.println("OPENTIME UPDATE");
		
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		//System.out.println("Update packer");
		NBTTagCompound compound = new NBTTagCompound();
		if(this.openTime == null) this.openTime = Instant.now();
		compound.setString("ot", this.openTime.toString());
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), compound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		//System.out.println("received le packer");
		this.openTime = Instant.parse(pkt.getNbtCompound().getString("ot"));
		System.out.println("OPENTIME UPDATE");
	}
	

	@Override
	public void updateContainingBlockInfo() {
		// TODO Auto-generated method stub
		super.updateContainingBlockInfo();
	}
	

	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}

	public void openInventory(EntityPlayer player) {
		
	}

	public void closeInventory(EntityPlayer player) {

	}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		return true;
	}
	


	

	
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		
		this.lootboxInventory.deserializeNBT(compound.getCompoundTag("Inventory"));
		
		//if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			this.openTime = Instant.parse(compound.getString("OpenTime"));
	//	}
		
		

		if(compound.hasKey("CustomName", 8)) this.setCustomName(compound.getString("CustomName"));
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		// TODO Auto-generated method stub
		return super.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		super.deserializeNBT(nbt);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		if(this.openTime == null) {
			this.openTime = Instant.now();
		}
		
		
		
		
		System.out.println("writing opentime " + this.openTime.toString());
		compound.setTag("Inventory", this.lootboxInventory.serializeNBT());
		compound.setString("OpenTime", this.openTime.toString());
		
		
		System.out.println("retreiving " + compound.getString("OpenTime"));

		if(this.hasCustomName()) compound.setString("CustomName", this.customName);
		return compound;
	}

	
	

	
	
	
	

}
