package com.vicmatskiv.weaponlib.tile;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockContainer;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockPos;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockRenderType;
import com.vicmatskiv.weaponlib.compatibility.CompatibleBlockState;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEnumFacing;
import com.vicmatskiv.weaponlib.compatibility.CompatibleEnumHand;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMaterial;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CustomTileEntityBlock extends CompatibleBlockContainer {

    private Class<? extends TileEntity> tileEntityClass;
    
    protected CustomTileEntityBlock(CompatibleMaterial material, Class<? extends TileEntity> tileEntityClass) {
        super(material);
        this.tileEntityClass = tileEntityClass;
    }
    
    @Override
    public boolean isNormalCube(CompatibleBlockState state, CompatibleBlockPos pos) {
        return false;
    }
    
    @Override
    public CompatibleBlockRenderType getRenderType(CompatibleBlockState state) {
        return CompatibleBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(CompatibleBlockState state) {
        return false;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        try {
            return tileEntityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create tile entity from class " + tileEntityClass, e);
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, CompatibleBlockPos pos, CompatibleBlockState state,
            EntityPlayer player, CompatibleEnumHand hand, CompatibleEnumFacing facing, float hitX, float hitY,
            float hitZ) {
        
        CustomTileEntity<?> entity = (CustomTileEntity<?>)compatibility.getTileEntity(world, pos);
        
        if(entity != null) {
            entity.onEntityBlockActivated(world, pos, player);
        }
        
        compatibility.markBlockForUpdate(world, pos);
        
        return true;
    }
    
    @Override
    public void onBlockPlacedBy(World world, CompatibleBlockPos pos, CompatibleBlockState state,
            EntityLivingBase player, ItemStack stack) {
        CustomTileEntity<?> entity = (CustomTileEntity<?>)compatibility.getTileEntity(world, pos);
        if(entity != null) {
            int side = CompatibleMathHelper.floor_double(player.rotationYaw/90f + 0.5) & 3;
            entity.setSide(side);
        }
    }
}
