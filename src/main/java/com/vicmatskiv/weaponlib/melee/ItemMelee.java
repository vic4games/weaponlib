package com.vicmatskiv.weaponlib.melee;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.AttachmentContainer;
import com.vicmatskiv.weaponlib.CompatibleAttachment;
import com.vicmatskiv.weaponlib.ImpactHandler;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ItemAttachment.ApplyHandler2;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.Modifiable;
import com.vicmatskiv.weaponlib.PlayerItemInstanceFactory;
import com.vicmatskiv.weaponlib.Updatable;
import com.vicmatskiv.weaponlib.WeaponSpawnEntity;
import com.vicmatskiv.weaponlib.compatibility.CompatibleItem;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.crafting.CraftingComplexity;
import com.vicmatskiv.weaponlib.crafting.OptionsMetadata;

import net.minecraft.client.model.ModelBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMelee extends CompatibleItem implements 
PlayerItemInstanceFactory<PlayerMeleeInstance, MeleeState>, AttachmentContainer, Modifiable, Updatable {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(ItemMelee.class);

    public static class Builder {

        String name;
        List<String> textureNames = new ArrayList<>();

        private String attackSound;
        private String reloadSound;
        private String unloadSound;

        private CreativeTabs creativeTab;
        private MeleeRenderer renderer;

        private String modId;

        Map<ItemAttachment<ItemMelee>, CompatibleAttachment<ItemMelee>> compatibleAttachments = new HashMap<>();

        private Class<? extends WeaponSpawnEntity> spawnEntityClass;
        ImpactHandler blockImpactHandler;

        private Function<ItemStack, List<String>> informationProvider;

        private CraftingComplexity craftingComplexity;

        private Object[] craftingMaterials;

        public Builder withModId(String modId) {
            this.modId = modId;
            return this;
        }

        public Builder withInformationProvider(Function<ItemStack, List<String>> informationProvider) {
            this.informationProvider = informationProvider;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTextureNames(String... textureNames) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            for (String textureName : textureNames) {
                this.textureNames.add(textureName + ".png");
            }
            return this;
        }

        public Builder withAttackSound(String attackSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.attackSound = attackSound; //modId + ":" + shootSound;
            return this;
        }

        public Builder withReloadSound(String reloadSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.reloadSound = reloadSound; //modId + ":" + reloadSound;
            return this;
        }

        public Builder withUnloadSound(String unloadSound) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }
            this.unloadSound = unloadSound;
            return this;
        }

        public Builder withCreativeTab(CreativeTabs creativeTab) {
            this.creativeTab = creativeTab;
            return this;
        }

        public Builder withRenderer(MeleeRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<ItemMelee> attachment, BiConsumer<EntityPlayer, ItemStack> positioning) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, null, false));
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<ItemMelee> attachment, boolean isDefault,
                BiConsumer<EntityPlayer, ItemStack> positioning, Consumer<ModelBase> modelPositioning) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioning, modelPositioning, isDefault));
            return this;
        }

        public Builder withCompatibleAttachment(ItemAttachment<ItemMelee> attachment, boolean isDefault,
                Consumer<ModelBase> positioner) {
            compatibleAttachments.put(attachment, new CompatibleAttachment<>(attachment, positioner, isDefault));
            return this;
        }

        public Builder withCrafting(CraftingComplexity craftingComplexity, Object...craftingMaterials) {
            if(craftingComplexity == null) {
                throw new IllegalArgumentException("Crafting complexity not set");
            }
            if(craftingMaterials.length < 2) {
                throw new IllegalArgumentException("2 or more materials required for crafting");
            }
            this.craftingComplexity = craftingComplexity;
            this.craftingMaterials = craftingMaterials;
            return this;
        }

        public ItemMelee build(ModContext modContext) {
            if (modId == null) {
                throw new IllegalStateException("ModId is not set");
            }

            if (name == null) {
                throw new IllegalStateException("Item name not provided");
            }

            if (attackSound == null) {
                attackSound = name;
            }

            if (reloadSound == null) {
                reloadSound = "reload";
            }

            if (unloadSound == null) {
                unloadSound = "unload";
            }

            if (spawnEntityClass == null) {
                spawnEntityClass = WeaponSpawnEntity.class;
            }


            ItemMelee itemMelee = new ItemMelee(this, modContext);

            itemMelee.attackSound = modContext.registerSound(this.attackSound);
            itemMelee.reloadSound = modContext.registerSound(this.reloadSound);
            itemMelee.unloadSound = modContext.registerSound(this.unloadSound);

            itemMelee.setCreativeTab(creativeTab);
            itemMelee.setUnlocalizedName(name);

            modContext.registerMeleeWeapon(name, itemMelee, renderer);

            if(craftingComplexity != null) {
                OptionsMetadata optionsMetadata = new OptionsMetadata.OptionMetadataBuilder()
                        .withSlotCount(9)
                        .build(craftingComplexity, Arrays.copyOf(craftingMaterials, craftingMaterials.length));

                List<Object> shape = modContext.getRecipeGenerator().createShapedRecipe(itemMelee.getName(), optionsMetadata);

                compatibility.addShapedRecipe(new ItemStack(itemMelee), shape.toArray());

            }
            return itemMelee;
        }
    }

    static final long MAX_RELOAD_TIMEOUT_TICKS = 60;
    static final long MAX_UNLOAD_TIMEOUT_TICKS = 60;

    Builder builder;

    private ModContext modContext;

    private CompatibleSound attackSound;
    private CompatibleSound silencedShootSound;
    private CompatibleSound reloadSound;
    private CompatibleSound unloadSound;
    private CompatibleSound ejectSpentRoundSound;
    private AttachmentContainer meleeAttachmentAspect;

    public static enum State { READY, SHOOTING, RELOAD_REQUESTED, RELOAD_CONFIRMED, UNLOAD_STARTED, UNLOAD_REQUESTED_FROM_SERVER, UNLOAD_CONFIRMED, PAUSED, MODIFYING, EJECT_SPENT_ROUND};

    ItemMelee(Builder builder, ModContext modContext) {
        this.builder = builder;
        this.modContext = modContext;
        setMaxStackSize(1);
    }

    public String getName() {
        return builder.name;
    }

    public CompatibleSound getShootSound() {
        return attackSound;
    }

    public CompatibleSound getSilencedShootSound() {
        return silencedShootSound;
    }

    public CompatibleSound getReloadSound() {
        return reloadSound;
    }

    public CompatibleSound getUnloadSound() {
        return unloadSound;
    }

    public CompatibleSound getEjectSpentRoundSound() {
        return ejectSpentRoundSound;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack itemStack) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean active) {
    }

   
    Map<ItemAttachment<ItemMelee>, CompatibleAttachment<ItemMelee>> getCompatibleAttachments() {
        return builder.compatibleAttachments;
    }


    public static boolean isActiveAttachment(PlayerMeleeInstance weaponInstance, ItemAttachment<ItemMelee> attachment) {
        return weaponInstance != null ? 
                MeleeAttachmentAspect.isActiveAttachment(attachment, weaponInstance) : false;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 0;
    }

    @Override
    public List<CompatibleAttachment<? extends AttachmentContainer>> getActiveAttachments(EntityPlayer player, ItemStack itemStack) {
        return meleeAttachmentAspect.getActiveAttachments(player, itemStack);
    }

    public MeleeRenderer getRenderer() {
        return builder.renderer;
    }

    List<ItemAttachment<ItemMelee>> getCompatibleAttachments(Class<? extends ItemAttachment<ItemMelee>> target) {
        return builder.compatibleAttachments.entrySet().stream()
                .filter(e -> target.isInstance(e.getKey()))
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer,
            @SuppressWarnings("rawtypes") List list, boolean p_77624_4_) {
        if(list != null && builder.informationProvider != null) {
            list.addAll(builder.informationProvider.apply(itemStack));
        }
    }

    @Override
    public void update(EntityPlayer player) {
//        modContext.getWeaponReloadAspect().updateMainHeldItem(player);
//        modContext.getWeaponFireAspect().onUpdate(player);
//        modContext.getAttachmentAspect().updateMainHeldItem(player);
    }

//    public void tryFire(EntityPlayer player) {
//        modContext.getWeaponFireAspect().onFireButtonClick(player);
//    }
//
//    public void tryStopFire(EntityPlayer player) {
//        modContext.getWeaponFireAspect().onFireButtonRelease(player);
//    }

    @Override
    public PlayerMeleeInstance createItemInstance(EntityPlayer player, ItemStack itemStack, int slot){
        PlayerMeleeInstance instance = new PlayerMeleeInstance(slot, player, itemStack);
        //state.setAmmo(Tags.getAmmo(itemStack)); // TODO: get ammo properly
        instance.setState(MeleeState.READY);
        
        for(CompatibleAttachment<ItemMelee> compatibleAttachment: ((ItemMelee) itemStack.getItem()).getCompatibleAttachments().values()) {
            ItemAttachment<ItemMelee> attachment = compatibleAttachment.getAttachment();
            if(compatibleAttachment.isDefault() && attachment.getApply2() != null) {
                attachment.getApply3().apply(attachment, instance);
            }
        }
        return instance;
    }

    @Override
    public void toggleClientAttachmentSelectionMode(EntityPlayer player) {
        modContext.getAttachmentAspect().toggleClientAttachmentSelectionMode(player);
    }

//    @Override
//    public boolean onDroppedByPlayer(ItemStack itemStack, EntityPlayer player) {
//        // Server side only method
////        PlayerWeaponInstance instance = (PlayerWeaponInstance) Tags.getInstance(itemStack);
////        return instance == null || instance.getState() == WeaponState.READY;
//    }

    public String getTextureName() {
        return builder.textureNames.get(0);
    }

    public ApplyHandler2<ItemMelee> getEquivalentHandler(AttachmentCategory attachmentCategory) {
        // TODO Auto-generated method stub
        return null;
    }

}
