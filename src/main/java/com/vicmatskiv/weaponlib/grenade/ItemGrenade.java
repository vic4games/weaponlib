package com.vicmatskiv.weaponlib.grenade;
import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vicmatskiv.weaponlib.AttachmentContainer;
import com.vicmatskiv.weaponlib.CompatibleAttachment;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerItemInstanceFactory;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.Updatable;
import com.vicmatskiv.weaponlib.compatibility.CompatibleItem;
import com.vicmatskiv.weaponlib.crafting.CraftingComplexity;
import com.vicmatskiv.weaponlib.crafting.OptionsMetadata;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemGrenade extends CompatibleItem implements
PlayerItemInstanceFactory<PlayerGrenadeInstance, GrenadeState>, AttachmentContainer, Updatable {

    public static final int DEFAULT_FUSE_TIMEOUT = 3000;
    public static final float DEFAULT_EXPLOSION_STRENTH = 2f;
    public static final int EXPLODE_ON_IMPACT = -1;

    public static class Builder {

        protected String name;
        protected String modId;
        protected ModelBase model;
        protected String textureName;
        protected Consumer<ItemStack> entityPositioning;
        protected Consumer<ItemStack> inventoryPositioning;
        protected BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
        protected BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
        protected BiConsumer<ModelBase, ItemStack> firstPersonModelPositioning;
        protected BiConsumer<ModelBase, ItemStack> thirdPersonModelPositioning;
        protected BiConsumer<ModelBase, ItemStack> inventoryModelPositioning;
        protected BiConsumer<ModelBase, ItemStack> entityModelPositioning;

        protected Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioning;
        protected Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioning;

        protected Map<ItemAttachment<ItemGrenade>, CompatibleAttachment<ItemGrenade>> compatibleAttachments = new HashMap<>();



        private int maxStackSize = 1;

        private int explosionTimeout = DEFAULT_FUSE_TIMEOUT;
        private float explosionStrength = DEFAULT_EXPLOSION_STRENTH;

        protected CreativeTabs tab;

        private CraftingComplexity craftingComplexity;
        private Object[] craftingMaterials;
        private int craftingCount = 1;

        private GrenadeRenderer renderer;
        List<String> textureNames = new ArrayList<>();


        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCreativeTab(CreativeTabs tab) {
            this.tab = tab;
            return this;
        }

        public Builder withModId(String modId) {
            this.modId = modId;
            return this;
        }

        public Builder withModel(ModelBase model) {
            this.model = model;
            return this;
        }

        public Builder withExplosionStrength(float explosionStrength) {
            this.explosionStrength = explosionStrength;
            return this;
        }

        public Builder withExplosionTimeout(int explosionTimeout) {
            this.explosionTimeout = explosionTimeout;
            return this;
        }

        public Builder withExplosionOnImpact() {
            this.explosionTimeout = EXPLODE_ON_IMPACT;
            return this;
        }

        public Builder withTextureNames(String... textureNames) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            for (String textureName : textureNames) {
                this.textureNames.add(textureName.toLowerCase() + ".png");
            }
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<ItemGrenade> attachment, BiConsumer<EntityPlayer, ItemStack> positioning) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, null, true));
            return this;
        }

        public Builder withMaxStackSize(int maxStackSize) {
            this.maxStackSize = maxStackSize;
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

        public Builder withRenderer(GrenadeRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public Builder withCrafting(CraftingComplexity craftingComplexity, Object...craftingMaterials) {
            return withCrafting(1, craftingComplexity, craftingMaterials);
        }

        public Builder withCrafting(int craftingCount, CraftingComplexity craftingComplexity, Object...craftingMaterials) {
            if(craftingComplexity == null) {
                throw new IllegalArgumentException("Crafting complexity not set");
            }
            if(craftingMaterials.length < 2) {
                throw new IllegalArgumentException("2 or more materials required for crafting");
            }
            if(craftingCount == 0) {
                throw new IllegalArgumentException("Invalid item count");
            }
            this.craftingComplexity = craftingComplexity;
            this.craftingMaterials = craftingMaterials;
            this.craftingCount = craftingCount;
            return this;
        }

        public ItemGrenade build(ModContext modContext) {

            ItemGrenade grenade = new ItemGrenade(this, modContext);
            grenade.setUnlocalizedName(modId + "_" + name);
            grenade.setCreativeTab(tab);
            grenade.maxStackSize = maxStackSize;

            modContext.registerGrenadeWeapon(name, grenade, renderer);

            if(craftingComplexity != null) {
                OptionsMetadata optionsMetadata = new OptionsMetadata.OptionMetadataBuilder()
                    .withSlotCount(9)
                    .build(craftingComplexity, Arrays.copyOf(craftingMaterials, craftingMaterials.length));

                List<Object> shape = modContext.getRecipeGenerator().createShapedRecipe(name, optionsMetadata);

                ItemStack itemStack = new ItemStack(grenade);
                compatibility.setStackSize(itemStack, craftingCount);
                if(optionsMetadata.hasOres()) {
                    compatibility.addShapedOreRecipe(itemStack, shape.toArray());
                } else {
                    compatibility.addShapedRecipe(itemStack, shape.toArray());
                }
            }

            return grenade;
        }


        static String addFileExtension(String s, String ext) {
            return s != null && !s.endsWith(ext) ? s + ext : s;
        }

        protected static String stripFileExtension(String str, String extension) {
            return str.endsWith(extension) ? str.substring(0, str.length() - extension.length()) : str;
        }
    }

    Builder builder;
    private ModContext modContext;

    public ItemGrenade(Builder builder, ModContext modContext) {
        this.builder = builder;
        this.modContext = modContext;
        this.maxStackSize = 16;
    }


    public GrenadeRenderer getRenderer() {
        return builder.renderer;
    }

    public String getTextureName() {
        return builder.textureNames.get(0);
    }

    public boolean hasSafetyPin() {
        return builder.explosionTimeout > 0;
    }

    public List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(EntityPlayer player,
            ItemStack itemStack) {
        return new ArrayList<>(builder.compatibleAttachments.values());
    }

    Map<ItemAttachment<ItemGrenade>, CompatibleAttachment<ItemGrenade>> getCompatibleAttachments() {
        return builder.compatibleAttachments;
    }

    public String getName() {
        return builder.name;
    }

    @Override
    public PlayerGrenadeInstance createItemInstance(EntityPlayer player, ItemStack itemStack, int slot) {
        PlayerGrenadeInstance instance = new PlayerGrenadeInstance(slot, player, itemStack);
        instance.setState(GrenadeState.READY);
        return instance;
    }

    public void attack(EntityPlayer player, boolean b) {
        modContext.getGrenadeAttackAspect().onAttackButtonClick(player);
    }

    @Override
    public void update(EntityPlayer player) {
        modContext.getGrenadeAttackAspect().onUpdate(player);
    }

    public float getExplosionStrength() {
        return builder.explosionStrength;
    }

    public int getExplosionTimeout() {
        return builder.explosionTimeout;
    }

    public long getThrowTimeout() {
        return 200;
    }

    public long getTotalTakeSafetyPinOffDuration() {
        return 100;
    }

    public long getReequipTimeout() {
        return 800;
    }

    public double getTotalThrowingDuration() {
        return 500;
    }

}