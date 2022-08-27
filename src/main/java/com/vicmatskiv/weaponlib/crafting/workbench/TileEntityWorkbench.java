package com.vicmatskiv.weaponlib.crafting.workbench;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.management.modelmbean.ModelMBeanNotificationBroadcaster;

import org.apache.commons.codec.binary.Hex;

import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTileEntity;
import com.vicmatskiv.weaponlib.crafting.CraftingGroup;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;
import com.vicmatskiv.weaponlib.crafting.items.CraftingItem;
import com.vicmatskiv.weaponlib.tile.CustomTileEntity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import scala.actors.threadpool.Arrays;

public class TileEntityWorkbench extends TileEntityStation implements ITickable {

	public int craftingTimer = -1;
	public int craftingDuration = -1;
	public IModernCrafting craftingTarget;

	// For the client.
	public String craftingTargetName;

	public int[] dismantleStatus = new int[] { -1, -1, -1, -1 };
	public int[] dismantleDuration = new int[] { -1, -1, -1, -1 };

	public int ticker;

	public boolean pushInventoryRefresh = false;

	/*
	 * Contents: 9 for crafting output 4 for dismantling slots 10 dismantling
	 * inventory 27 for main inventory + ------------------------- 50 slots total
	 * 
	 */
	public ItemStackHandler mainInventory = new ItemStackHandler(50);

	public TileEntityWorkbench() {
	}

	@Override
	public void onLoad() {
		super.onLoad();

	}

	public double getProgress() {
		if (craftingTimer == -1 || craftingDuration == -1)
			return 0.0;
		return craftingTimer / (double) craftingDuration;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return super.getUpdateTag();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return super.getUpdatePacket();
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("mainInventory", mainInventory.serializeNBT());
		if (craftingTimer != -1) {
			compound.setInteger("craftingTargetID", this.craftingTarget.getCraftingGroup().getID());
			compound.setString("craftingTargetName", this.craftingTarget.getItem().getUnlocalizedName());
			compound.setInteger("craftingTimer", craftingTimer);
			compound.setInteger("craftingDuration", craftingDuration);

		}
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("mainInventory"))
			this.mainInventory.deserializeNBT((NBTTagCompound) compound.getTag("mainInventory"));
		if (compound.hasKey("craftingTimer") && compound.hasKey("craftingDuration")) {
			this.craftingTarget = CraftingRegistry.getModernCrafting(
					CraftingGroup.getValue(compound.getInteger("craftingTargetID")),
					compound.getString("craftingTargetName"));
			this.craftingTimer = compound.getInteger("craftingTimer");
			this.craftingDuration = compound.getInteger("craftingDuration");
		}

	}

	public void setTimer(int time, int duration) {
		this.craftingTimer = time;
		this.craftingDuration = duration;
	}

	public void setDismantling(int[] instant, int[] lengths) {
		this.dismantleStatus = instant;
		this.dismantleDuration = lengths;
	}

	public void addStackToInventoryRange(ItemStack stack, int start, int end) {

		for (int i = start; i <= end; ++i) {
			if (ItemStack.areItemsEqual(mainInventory.getStackInSlot(i), stack)) {
				ItemStack inInventory = mainInventory.getStackInSlot(i);
				if (inInventory.getCount() + stack.getCount() <= inInventory.getMaxStackSize()) {
					inInventory.grow(stack.getCount());
					stack.shrink(stack.getCount());
				} else if (inInventory.getCount() >= inInventory.getMaxStackSize()) {
					continue;
				} else if (inInventory.getCount() + inInventory.getCount() >= inInventory.getMaxStackSize()) {
					int difference = inInventory.getMaxStackSize() - inInventory.getCount();
					inInventory.grow(difference);
					stack.shrink(difference);
					continue;
				}
			}
		}

		if (stack.getCount() > 0) {
			for (int i = start; i <= end; ++i) {
				if (mainInventory.getStackInSlot(i).isEmpty()) {
					mainInventory.setStackInSlot(i, stack);
					break;
				}
			}
		}
	}

	@Override
	public void update() {

		
		if(this.world.isRemote) {
			//System.out.println(mainInventory.serializeNBT());
		}
		
		if (this.craftingTimer != -1) {
			this.craftingTimer++;
		}

		for (int i = 0; i < dismantleStatus.length; ++i) {
			if (dismantleStatus[i] == -1 || dismantleDuration[i] == -1)
				continue;
			dismantleStatus[i]++;

			if (mainInventory.getStackInSlot(i + 9).isEmpty()) {
				dismantleStatus[i] = -1;
				dismantleDuration[i] = -1;
			}

			if (dismantleStatus[i] > dismantleDuration[i]) {
				dismantleStatus[i] = -1;
				dismantleDuration[i] = -1;

				if (!this.world.isRemote) {
					ItemStack stackToDismantle = mainInventory.getStackInSlot(i + 9);
					if (stackToDismantle.getItem() instanceof IModernCrafting) {
						ItemStack[] modernRecipe = ((IModernCrafting) stackToDismantle.getItem()).getModernRecipe();
						stackToDismantle.shrink(1);

						for (ItemStack stack : modernRecipe) {
							if (stack.getItem() instanceof CraftingItem) {
								stack.setCount((int) Math.round(
										stack.getCount() * ((CraftingItem) stack.getItem()).getRecoveryPercentage()));
							}
							addStackToInventoryRange(stack, 12, 22);
						}
					}
				}

			}
		}

		if (getProgress() >= 1) {
			craftingTimer = -1;
			craftingDuration = -1;


			if (!this.world.isRemote) {
				addStackToInventoryRange(new ItemStack(this.craftingTarget.getItem()), 0, 9);
			}

		}

	}

}
