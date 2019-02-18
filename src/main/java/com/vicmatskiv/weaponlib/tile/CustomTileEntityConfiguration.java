package com.vicmatskiv.weaponlib.tile;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMaterial;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class CustomTileEntityConfiguration<T extends CustomTileEntityConfiguration<T>> {
    
    private CompatibleMaterial material;
    private String name;
    private String textureName;
    private CreativeTabs creativeTab;
    private float hardness = 6f;
    private float resistance = 600000f;
    private String modelClassName;
    private AtomicInteger counter = new AtomicInteger(10000);
    private Supplier<Integer> entityIdSupplier = () -> counter.incrementAndGet();
    private Consumer<TileEntity> positioning = tileEntity -> {};
    
    @SuppressWarnings("unchecked")
    private T safeCast(CustomTileEntityConfiguration<T> input) {
        return (T) input;
    }

    public T withMaterial(CompatibleMaterial material) {
        this.material = material;
        return safeCast(this);
    }
    
    public T withName(String name) {
        this.name = name;
        return safeCast(this);
    }
    
    public T withTextureName(String textureName) {
        this.textureName = textureName;
        return safeCast(this);
    }
    
    public T withCreativeTab(CreativeTabs creativeTab) {
        this.creativeTab = creativeTab;
        return safeCast(this);
    }
    
    public T withHardness(float hardness) {
        this.hardness = hardness;
        return safeCast(this);
    }
    
    public T withResistance(float resistance) {
        this.resistance = resistance;
        return safeCast(this);
    }
    
    public T withModelClassName(String modelClassName) {
        this.modelClassName = modelClassName;
        return safeCast(this);
    }
    
    public T withPositioning(Consumer<TileEntity> positioning) {
        this.positioning = positioning;
        return safeCast(this);
    }
    
    protected Class<? extends TileEntity> getBaseClass() {
        return CustomTileEntity.class;
    }
    
    @SuppressWarnings("unchecked")
    protected Class<CustomTileEntity<T>> createTileEntityClass() {
        int modEntityId = entityIdSupplier.get();
        return (Class<CustomTileEntity<T>>) CustomTileEntityClassFactory.getInstance().generateEntitySubclass(
                getBaseClass(), modEntityId, this);
    }
    
    public void build(ModContext modContext) {
        
        Class<? extends TileEntity> tileEntityClass = createTileEntityClass();
        
        CustomTileEntityBlock tileEntityBlock = new CustomTileEntityBlock(material, tileEntityClass);
        tileEntityBlock.setBlockName(modContext.getModId() + "_" + name);
        tileEntityBlock.setHardness(hardness);
        tileEntityBlock.setResistance(resistance);
        tileEntityBlock.setCreativeTab(creativeTab);
        ResourceLocation textureResource = new ResourceLocation(modContext.getModId(), textureName);
        tileEntityBlock.setBlockTextureName(textureResource.toString());
        compatibility.registerTileEntity(tileEntityClass, "tile" + name);
                    
        compatibility.registerBlock(modContext.getModId(), tileEntityBlock, name);
        
        if(compatibility.isClientSide()) {
            RendererRegistration.registerRenderableEntity(modContext, tileEntityClass, modelClassName, 
                    textureResource, positioning);
        }            
    }
    
    private static class RendererRegistration {
        /*
         * This method is wrapped into a static class to facilitate conditional client-side only loading
         */
        private static <T extends CustomTileEntityConfiguration<T>> void registerRenderableEntity(ModContext context, Class<? extends TileEntity> tileEntityClass, String modelClassName, 
                ResourceLocation textureResource, Consumer<TileEntity> positioning) {
            try {
                ModelBase model = (ModelBase) Class.forName(modelClassName).newInstance();
                compatibility.bindTileEntitySpecialRenderer(tileEntityClass, 
                        new CustomTileEntityRenderer(model, textureResource, positioning));
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
