package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vicmatskiv.weaponlib.compatibility.CompatibleItem;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class ItemVest extends CompatibleItem implements ISpecialArmor, ModelSource {
        
    
	public static class Builder {
                
        private String name;
        private CreativeTabs tab;
        private ModelBase model;
        private String textureName;
        
        private int durability;
        private int damageReduceAmount;
        private double percentDamageBlocked;
        
        private Consumer<ItemStack> entityPositioning;
        private Consumer<ItemStack> inventoryPositioning;
        private BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
        private BiConsumer<EntityPlayer, ItemStack> customEquippedPositioning;
        private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
        private BiConsumer<ModelBase, ItemStack> firstPersonModelPositioning;
        private BiConsumer<ModelBase, ItemStack> thirdPersonModelPositioning;
        private BiConsumer<ModelBase, ItemStack> inventoryModelPositioning;
        private BiConsumer<ModelBase, ItemStack> entityModelPositioning;
        private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioning;
        private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioning;
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withDamageReduceAmount(int damageReduceAmount) {
            this.damageReduceAmount = damageReduceAmount;
            return this;
        }
        
        public Builder withPercentDamageBlocked(double ratio) {
        	this.percentDamageBlocked = ratio;
        	return this;
        }
        
        public Builder withDurability(int durability) {
            this.durability = durability;
            return this;
        }
        
        public Builder withTab(CreativeTabs tab) {
            this.tab = tab;
            return this;
        }
        
        public Builder withModel(ModelBase model) {
            this.model = model;
            return this;
        }
        
//        public Builder withGuiTextureName(String guiTextureName) {
//            this.guiTextureName = guiTextureName;
//            return this;
//        }
        
//        public Builder withGuiTextureWidth(int guiTextureWidth) {
//            this.guiTextureWidth = guiTextureWidth;
//            return this;
//        }
        
        public Builder withModelTextureName(String textureName) {
            this.textureName = textureName;
            return this;
        }
        
        public Builder withEntityPositioning(Consumer<ItemStack> entityPositioning) {
            this.entityPositioning = entityPositioning;
            return this;
        }

        public Builder withInventoryPositioning(Consumer<ItemStack> inventoryPositioning) {
            this.inventoryPositioning = inventoryPositioning;
            return this;
        }

        public  Builder withThirdPersonPositioning(BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning) {
            this.thirdPersonPositioning = thirdPersonPositioning;
            return this;
        }
        
        public  Builder withCustomEquippedPositioning(BiConsumer<EntityPlayer, ItemStack> customEquippedPositioning) {
            this.customEquippedPositioning = customEquippedPositioning;
            return this;
        }

        public Builder withFirstPersonPositioning(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning) {
            this.firstPersonPositioning = firstPersonPositioning;
            return this;
        }

        public Builder withFirstPersonModelPositioning(BiConsumer<ModelBase, ItemStack> firstPersonModelPositioning) {
            this.firstPersonModelPositioning = firstPersonModelPositioning;
            return this;
        }

        public Builder withEntityModelPositioning(BiConsumer<ModelBase, ItemStack> entityModelPositioning) {
            this.entityModelPositioning = entityModelPositioning;
            return this;
        }

        public Builder withInventoryModelPositioning(BiConsumer<ModelBase, ItemStack> inventoryModelPositioning) {
            this.inventoryModelPositioning = inventoryModelPositioning;
            return this;
        }

        public Builder withThirdPersonModelPositioning(BiConsumer<ModelBase, ItemStack> thirdPersonModelPositioning) {
            this.thirdPersonModelPositioning = thirdPersonModelPositioning;
            return this;
        }

        public Builder withFirstPersonHandPositioning(
                Consumer<RenderContext<RenderableState>> leftHand,
                Consumer<RenderContext<RenderableState>> rightHand)
        {
            this.firstPersonLeftHandPositioning = leftHand;
            this.firstPersonRightHandPositioning = rightHand;
            return this;
        }

        private static class RendererRegistrationHelper {
            private static Object registerRenderer(Builder builder, ModContext modContext) {
                return new StaticModelSourceRenderer.Builder()
                .withHiddenInventory(builder.tab == null)
                .withEntityPositioning(builder.entityPositioning)
                .withFirstPersonPositioning(builder.firstPersonPositioning)
                .withThirdPersonPositioning(builder.thirdPersonPositioning)
                .withCustomEquippedPositioning(builder.customEquippedPositioning)
                .withInventoryPositioning(builder.inventoryPositioning)
                .withEntityModelPositioning(builder.entityModelPositioning)
                .withFirstPersonModelPositioning(builder.firstPersonModelPositioning)
                .withThirdPersonModelPositioning(builder.thirdPersonModelPositioning)
                .withInventoryModelPositioning(builder.inventoryModelPositioning)
                .withFirstPersonHandPositioning(builder.firstPersonLeftHandPositioning, builder.firstPersonRightHandPositioning)
                .withModContext(modContext)
                .withModId(modContext.getModId())
                .build();
            }
        }
       
        public ItemVest build(ModContext modContext) {
            if(name == null) {
                throw new IllegalStateException("ItemStorage name not set");
            }
            
//            if(size <= 0) {
//                throw new IllegalStateException("ItemStorage size must be greater than 0");
//            }
            
//            if(guiTextureName == null) {
//                throw new IllegalStateException("ItemStorage gui texture not set");
//            }
//            
//            if(!guiTextureName.startsWith("textures/gui/")) {
//                guiTextureName = "textures/gui/" + guiTextureName;
//            }
//            ResourceLocation guiTextureLocation = new ResourceLocation(modContext.getModId(), 
//                    addFileExtension(guiTextureName, ".png"));
            
            ItemVest item = new ItemVest(modContext, percentDamageBlocked, durability);
            
            item.setUnlocalizedName(modContext.getModId() + "_" + name);
            
            if(model != null) {
                item.texturedModels.add(new Tuple<>(model, addFileExtension(textureName, ".png")));
            }
            
            if(tab != null) {
                item.setCreativeTab(tab);
                
                
            }
            
            item.customEquippedPositioning = customEquippedPositioning;
            
            modContext.registerRenderableItem(name, item, compatibility.isClientSide() ? RendererRegistrationHelper.registerRenderer(this, modContext) : null);
            
            return item;
        }
    }
    
    
    private List<Tuple<ModelBase, String>> texturedModels = new ArrayList<>();
    private int size;
    private final int damageReduceAmount;
    
    private int durability;
    private double percentDamageBlocked;
    public BiConsumer<EntityPlayer, ItemStack> customEquippedPositioning;

    
    public BiConsumer<EntityPlayer, ItemStack> getCustomEquippedPositioning() {
    	return customEquippedPositioning;
    }

    
    
    public ItemVest(ModContext context, double percentDamageBlocked, int durability) {
        this.percentDamageBlocked = percentDamageBlocked;
        this.damageReduceAmount = 1;
        this.durability = durability;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1; // return any value greater than zero
    }
    
    @Override
    public List<Tuple<ModelBase, String>> getTexturedModels() {
        return texturedModels;
    }

    @Override
    public CustomRenderer<?> getPostRenderer() {
        return null;
    }
    
    public int getSize() {
        return size;
    }
    
//    public ResourceLocation getGuiTextureLocation() {
//        return guiTextureLocation;
//    }
    
    private static String addFileExtension(String s, String ext) {
        return s != null && !s.endsWith(ext) ? s + ext : s;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack vestStack, DamageSource source, double damage,
            int slot) {
    	System.out.println("% blocked = " + (this.percentDamageBlocked*100));
        return new ArmorProperties(0, this.percentDamageBlocked, durability);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {

        return (int) (this.percentDamageBlocked*10);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      
    	double absorb = damage * percentDamageBlocked;
        int itemDamage = (int)(absorb / 25.0 < 1 ? 1 : absorb / 25.0);
        stack.damageItem(itemDamage, entity);
    }
}