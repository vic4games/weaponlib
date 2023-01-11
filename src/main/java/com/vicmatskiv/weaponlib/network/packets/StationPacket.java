package com.vicmatskiv.weaponlib.network.packets;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.print.attribute.HashAttributeSet;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.lang3.RandomUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vicmatskiv.weaponlib.ClientEventHandler;
import com.vicmatskiv.weaponlib.ItemBullet;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessage;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageHandler;
import com.vicmatskiv.weaponlib.config.BalancePackManager;
import com.vicmatskiv.weaponlib.config.BalancePackManager.BalancePack;
import com.vicmatskiv.weaponlib.crafting.CraftingEntry;
import com.vicmatskiv.weaponlib.crafting.CraftingGroup;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.ammopress.TileEntityAmmoPress;
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;
import com.vicmatskiv.weaponlib.crafting.workbench.TileEntityWorkbench;
import com.vicmatskiv.weaponlib.jim.util.RandomUtil;
import com.vicmatskiv.weaponlib.network.CompressionUtil;
import com.vicmatskiv.weaponlib.network.NetworkUtil;
import com.vicmatskiv.weaponlib.render.shells.ShellParticleSimulator.Shell;
import com.vicmatskiv.weaponlib.vehicle.EntityVehicle;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleClientPacket;
import com.vicmatskiv.weaponlib.vehicle.network.VehicleDataContainer;
import com.vicmatskiv.weaponlib.vehicle.network.VehiclePacketLatencyTracker;

import akka.japi.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.oredict.OreDictionary;
import scala.actors.threadpool.Arrays;

public class StationPacket implements CompatibleMessage {

	
	
	public static final int CRAFT = 1;
	public static final int DISMANTLE = 2;
	public static final int MOVE_OUTPUT = 3;
	public static final int UPDATE = 4;
	public static final int POP_FROM_QUEUE = 5;
	
	public int opcode;
	public BlockPos teLocation;
	
	
	public int craftingTimer;
	public int craftingDuration;
	
	public CraftingGroup craftingGroup;
	public String craftingName = "";
	
	public int playerID;
	public int slotToMove;

	public int quantity = -1;
	
	public StationPacket() {}
	
	
	public StationPacket(int type, BlockPos location, String nameToCraft, CraftingGroup group, int quantity) {
		this.opcode = type;
		this.teLocation = location;
		
		this.craftingName = nameToCraft;
		this.craftingGroup = group;
		
		this.quantity = quantity;
	}
	
	public StationPacket(int type, BlockPos location, int craftingTimer, int craftingDuration, CraftingGroup group, String nameToCraft) {
		
		//System.out.printf("Initializing SP with Opcode %d | Crafting Group=%s | Name to Craft: %s\n", type, group.toString(), nameToCraft);
		
		this.opcode = type;
		this.teLocation = location;
		
		this.craftingTimer = craftingTimer;
		this.craftingDuration = craftingDuration;
		
		this.craftingGroup = group;
		this.craftingName = nameToCraft;
	}
	
	public StationPacket(int type, BlockPos location, int playerID, int slotToMove)  {
		this.opcode = type;
		this.teLocation = location;
		this.playerID = playerID;
		this.slotToMove = slotToMove;
	}
	

	public void fromBytes(ByteBuf buf) {
		this.opcode = buf.readInt();
		this.teLocation = BlockPos.fromLong(buf.readLong());
		if(this.opcode == CRAFT) {
			this.quantity = buf.readInt();
			if(quantity == -1) {
				this.craftingTimer = buf.readInt();
				this.craftingDuration = buf.readInt();	
			}
			this.craftingGroup = CraftingGroup.getValue(buf.readInt());
			this.craftingName = ByteBufUtils.readUTF8String(buf);
			
		} else if(this.opcode == MOVE_OUTPUT || this.opcode == POP_FROM_QUEUE) {
			this.playerID = buf.readInt();
			this.slotToMove = buf.readInt();
		} else if(this.opcode == DISMANTLE) {
			this.craftingDuration = buf.readInt();
		}
		
		
	}
	
	

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.opcode);
		buf.writeLong(this.teLocation.toLong());
		if(this.opcode == CRAFT) {
			buf.writeInt(this.quantity);
			if(this.quantity == -1) {
				buf.writeInt(this.craftingTimer);
				buf.writeInt(this.craftingDuration);
			}
			buf.writeInt(this.craftingGroup.getID());
			ByteBufUtils.writeUTF8String(buf, this.craftingName);
			 
		} else if(this.opcode == MOVE_OUTPUT || this.opcode == POP_FROM_QUEUE) {
			buf.writeInt(this.playerID);
			buf.writeInt(this.slotToMove);
		} else if(this.opcode == DISMANTLE) {
			buf.writeInt(this.craftingDuration);
		}
		
	}

	public static class WorkbenchPacketHandler implements CompatibleMessageHandler<StationPacket, CompatibleMessage> {
		
		private ModContext modContext;
		
		
		public WorkbenchPacketHandler(ModContext context) {
			this.modContext = context;
		}
		

		@Override
		public <T extends CompatibleMessage> T onCompatibleMessage(StationPacket m, CompatibleMessageContext ctx) {
			if(ctx.isServerSide()) {
	            ctx.runInMainThread(() -> {
					

	            	World world = ctx.getPlayer().world;
	            	
	            	TileEntity tileEntity = world.getTileEntity(m.teLocation);
	            	if(tileEntity instanceof TileEntityStation) {
	            		TileEntityStation station = (TileEntityStation) tileEntity;
	            		
	            		if(m.opcode == CRAFT) {
	            			
	            			
	            			
	            			
	            			if(tileEntity instanceof TileEntityAmmoPress) {
	            				// Since it's based on a queue, you can add whatever you'd like and it
	            				// will merely refuse to craft it until you have the resources avaliable.
	            				
	            				
	            				TileEntityAmmoPress press = (TileEntityAmmoPress) station;
		            			Item item = CraftingRegistry.getModernCrafting(m.craftingGroup, m.craftingName).getItem();
		            			ItemStack newStack = new ItemStack(item, m.quantity);
		            			
		            			
		            		
		            			if(press.hasStack()) {
		            				ItemStack topQueue = press.getCraftingQueue().getLast();
		            				if(ItemStack.areItemsEqualIgnoreDurability(topQueue, newStack)) {
		            					
		            					topQueue.grow(m.quantity);
		            					
		            					
		            				} else {
		            					press.addStack(newStack);
		            				}
		            			} else {
		            				press.addStack(newStack);
		            			}
		            			
		            			
		            			modContext.getChannel().getChannel().sendToAllAround(new StationClientPacket(station.getWorld(), m.teLocation), new TargetPoint(0, m.teLocation.getX(), m.teLocation.getY(), m.teLocation.getZ(), 20));
			            		
		            			
		            			return;
	            			}
	            			
	            			
	            		
	            			
	            			
	            			//System.out.println(m.craftingGroup + " | " + m.craftingName);
	            			CraftingEntry[] modernRecipe = CraftingRegistry.getModernCrafting(m.craftingGroup, m.craftingName).getModernRecipe();
		            		if(modernRecipe == null) return;
		            		
		            	
		            		// Add all items to an item list to verify that they exist.
		            		HashMap<Item, ItemStack> itemList = new HashMap<>(27, 0.7f);
		            		for(int i = 23; i < station.mainInventory.getSlots(); ++i) {
		            			itemList.put(station.mainInventory.getStackInSlot(i).getItem(), station.mainInventory.getStackInSlot(i));
		            		}
		            		
		            		
		            		ArrayList<Pair<Item, Integer>> toConsume = new ArrayList<>();
		            		
		            		// Verify
		            		for(CraftingEntry stack : modernRecipe) {
		            			
		            			if(!stack.isOreDictionary()) {
		            				// Does it even have that item? / Does it have enough of that item?
			            			if(!itemList.containsKey(stack.getItem()) || stack.getCount() > itemList.get(stack.getItem()).getCount()) {
			            				return;
			            			}
			            			
			            			toConsume.add(new Pair<Item, Integer>(stack.getItem(), stack.getCount()));
		            			} else {		            				
		            				// Stack is an OreDictionary term
		            				boolean hasAny = false;
		            				NonNullList<ItemStack> list = OreDictionary.getOres(stack.getOreDictionaryEntry());
		            				for(ItemStack toTest : list) {
		            					if(itemList.containsKey(toTest.getItem()) && stack.getCount() <= itemList.get(toTest.getItem()).getCount()) {
		            						hasAny = true;
		            						
		            						toConsume.add(new Pair<Item, Integer>(toTest.getItem(), stack.getCount()));
		            						
		            				
		            						break;
		            					}
		            				}
		            				
		            				if(!hasAny) return;
		            			}
		            		}
		            		
		         
		            		
		            		/*
		            		// Consume materials
		            		for(CraftingEntry stack : modernRecipe) {
		            			if(!stack.isOreDictionary()) {
		            				itemList.get(stack.getItem()).shrink(stack.getCount());
		            			} else {
		            				
		            				List<ItemStack> list = OreDictionary.getOres(stack.getOreDictionaryEntry());
		            				for(ItemStack test : list) {
		            					
		            				}
		            				itemList.get(stack.getItem()).shrink(stack.getCount());
		            			}
		            			
		            		}*/
		            		
		            		for(Pair<Item, Integer> i : toConsume) {
		            			itemList.get(i.first()).shrink(i.second());
		            		}
		            		
		            		
		            		
		            		if(station instanceof TileEntityWorkbench) {
		            			TileEntityWorkbench workbench = (TileEntityWorkbench) station;
		            			workbench.craftingTimer = m.craftingTimer;
		            			workbench.craftingDuration = m.craftingDuration;
			            		workbench.craftingTarget = CraftingRegistry.getModernCrafting(m.craftingGroup, m.craftingName);

			            		
		            		}
		            		
		            		station.sendUpdate();
		            		//station.markDirty();
		            	
		            		modContext.getChannel().getChannel().sendToAllAround(new StationClientPacket(station.getWorld(), m.teLocation), new TargetPoint(0, m.teLocation.getX(), m.teLocation.getY(), m.teLocation.getZ(), 20));
		            		
	            		} else if(m.opcode == DISMANTLE) {
	            			
	            			for(int i = 9; i < 13; ++i) {
	            				if(!station.mainInventory.getStackInSlot(i).isEmpty()) {
	            					
	            					ItemStack stack = station.mainInventory.getStackInSlot(i);
	            					if(stack.getItem() instanceof IModernCrafting && ((IModernCrafting) stack.getItem()).getModernRecipe() != null && (station.dismantleStatus[i - 9] == -1 || station.dismantleStatus[i - 9] > station.dismantleDuration[i - 9])) {
	            						

	            						station.dismantleStatus[i - 9] = 0;
	            						station.dismantleDuration[i - 9] = ((TileEntityStation) tileEntity).getDismantlingTime(((IModernCrafting) stack.getItem()));
	            						
	            						
	            					}
	            					
	            					
	            				}
	            			}
	            			
	            			modContext.getChannel().getChannel().sendToAllAround(new StationClientPacket(station.getWorld(), m.teLocation), new TargetPoint(0, m.teLocation.getX(), m.teLocation.getY(), m.teLocation.getZ(), 25));
		            		
	            			
	            			
	            			
	            		} else if(m.opcode == MOVE_OUTPUT) {
	            			((EntityPlayer) world.getEntityByID(m.playerID)).addItemStackToInventory(station.mainInventory.getStackInSlot(m.slotToMove));
	            		} else if(m.opcode == POP_FROM_QUEUE) {
	            			if(!(tileEntity instanceof TileEntityAmmoPress)) return;
	            			
	            			TileEntityAmmoPress teAmmoPress = (TileEntityAmmoPress) tileEntity;
	            			
	            			if(teAmmoPress.hasStack() && teAmmoPress.getCraftingQueue().size() > m.slotToMove) {
	            				teAmmoPress.getCraftingQueue().remove(m.slotToMove);
	            			}
	            			
	            			modContext.getChannel().getChannel().sendToAllAround(new StationClientPacket(station.getWorld(), m.teLocation), new TargetPoint(0, m.teLocation.getX(), m.teLocation.getY(), m.teLocation.getZ(), 25));
		            		
	            		}
	            		
	            		
	            		
	            		
	            		
	            	}
	       
	            	
		            
		            	
				});
			}
			
			return null;
		}

	}

	
}
