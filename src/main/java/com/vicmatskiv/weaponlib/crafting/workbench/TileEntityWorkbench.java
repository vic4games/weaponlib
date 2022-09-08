package com.vicmatskiv.weaponlib.crafting.workbench;

import com.vicmatskiv.weaponlib.crafting.CraftingEntry;
import com.vicmatskiv.weaponlib.crafting.CraftingGroup;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.base.TileEntityStation;
import com.vicmatskiv.weaponlib.crafting.items.CraftingItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityWorkbench extends TileEntityStation {

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
						CraftingEntry[] modernRecipe = ((IModernCrafting) stackToDismantle.getItem()).getModernRecipe();
						stackToDismantle.shrink(1);

						for (CraftingEntry stack : modernRecipe) {
							ItemStack itemStack = new ItemStack(stack.getItem());
							if (stack.getItem() instanceof CraftingItem) {
								itemStack.setCount((int) Math.round(
										stack.getCount() * ((CraftingItem) stack.getItem()).getRecoveryPercentage()));
							}
							addStackToInventoryRange(itemStack, 31, 40);
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
