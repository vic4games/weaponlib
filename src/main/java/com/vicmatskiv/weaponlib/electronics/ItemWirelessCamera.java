package com.vicmatskiv.weaponlib.electronics;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWirelessCamera extends CompatibleItem {
    
    private ModContext modContext;
    
    public ItemWirelessCamera() {
        this(null);
    }

    public ItemWirelessCamera(ModContext modContext) {
        this.modContext = modContext;
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    protected ItemStack onCompatibleItemRightClick(ItemStack itemStack, World world, EntityPlayer player,
            boolean mainHand) {
        
        if (!player.capabilities.isCreativeMode)
        {
            --itemStack.stackSize;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote)
        {
            world.spawnEntityInWorld(new EntityWirelessCamera(modContext, world, player));
        }

        return itemStack;        
    }
}