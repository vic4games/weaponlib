package com.vicmatskiv.weaponlib.electronics;
import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.List;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.compatibility.CompatibleRayTraceResult;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleItem;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWirelessCamera extends CompatibleItem {
    
    private ModContext modContext;
    private ModelBase model;
    private String textureName;

    public ItemWirelessCamera(ModContext modContext, ModelBase model, String textureName) {
        this.modContext = modContext;
        this.maxStackSize = 16;
        this.model = model;
        this.textureName = textureName;
    }

    @Override
    protected ItemStack onCompatibleItemRightClick(ItemStack itemStack, World world, EntityPlayer player,
            boolean mainHand) {
        
        if (!player.capabilities.isCreativeMode)
        {
            --itemStack.stackSize;
        }

        //compatibility.playSound(player, new CompatibleSound("random.bow"), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote)
        {
            world.spawnEntityInWorld(new EntityWirelessCamera(modContext, world, player, this));
        }

        return itemStack;        
    }

    public ModelBase getModel() {
        return model;
    }

    public String getTextureName() {
        return textureName;
    }
}