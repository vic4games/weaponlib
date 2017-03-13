package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class CompatibleItem extends Item {
        
    public static final CompatibleItem GUNPOWDER = new CompatibleItem(Items.GUNPOWDER) {};
    
    private Item item;
    public CompatibleItem() {}
    
    private CompatibleItem(Item item) {
        this.item = item;
    }
    
    public Item getItem() {
        return item;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		return new ActionResult<>(EnumActionResult.SUCCESS, onCompatibleItemRightClick(itemStack, world, player, hand == EnumHand.MAIN_HAND));
	};

	protected ItemStack onCompatibleItemRightClick(ItemStack itemStack, World world, EntityPlayer player, boolean mainHand) {
		return itemStack;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return onItemUseFirst(stack, player, world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}
	
	protected boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world) {
		return false;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return true;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return EnumActionResult.SUCCESS;
	}
}
