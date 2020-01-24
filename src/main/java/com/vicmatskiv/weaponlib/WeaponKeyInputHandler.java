package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.function.Function;

import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExtraEntityFlags;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMessageContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponKeyInputHandler;
import com.vicmatskiv.weaponlib.electronics.PlayerTabletInstance;
import com.vicmatskiv.weaponlib.inventory.GuiHandler;
import com.vicmatskiv.weaponlib.inventory.OpenCustomPlayerInventoryGuiMessage;
import com.vicmatskiv.weaponlib.melee.MeleeState;
import com.vicmatskiv.weaponlib.melee.PlayerMeleeInstance;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class WeaponKeyInputHandler extends CompatibleWeaponKeyInputHandler {

    @SuppressWarnings("unused")
    private CompatibleChannel channel;
    private Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier;
    private ModContext modContext;

    public WeaponKeyInputHandler(
            ModContext modContext,
            Function<CompatibleMessageContext, EntityPlayer> entityPlayerSupplier,
            WeaponAttachmentAspect attachmentAspect,
            CompatibleChannel channel) {
        this.modContext = modContext;
        this.entityPlayerSupplier = entityPlayerSupplier;
        this.channel = channel;
    }

    @Override
    public void onCompatibleKeyInput() {

        EntityPlayer player = entityPlayerSupplier.apply(null);
        ItemStack itemStack = compatibility.getHeldItemMainHand(player);

        if(DebugPositioner.isDebugModeEnabled() && KeyBindings.upArrowKey.isPressed()) {
            DebugPositioner.incrementXRotation(5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.downArrowKey.isPressed()) {
            DebugPositioner.incrementXRotation(-5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.leftArrowKey.isPressed()) {
            DebugPositioner.incrementYRotation(5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.rightArrowKey.isPressed()) {
            DebugPositioner.incrementYRotation(-5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.jDebugKey.isPressed()) {
            DebugPositioner.incrementZRotation(5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.kDebugKey.isPressed()) {
            DebugPositioner.incrementZRotation(-5);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.minusDebugKey.isPressed()) {
            DebugPositioner.incrementXPosition(-1f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.equalsDebugKey.isPressed()) {
            DebugPositioner.incrementXPosition(1f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.lBracketDebugKey.isPressed()) {
            DebugPositioner.incrementYPosition(-1f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.rBracketDebugKey.isPressed()) {
            DebugPositioner.incrementYPosition(1f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.semicolonDebugKey.isPressed()) {
            DebugPositioner.incrementZPosition(-1f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.apostropheDebugKey.isPressed()) {
            DebugPositioner.incrementZPosition(1f);
        } else if(DebugPositioner.isDebugModeEnabled() && KeyBindings.deleteDebugKey.isPressed()) {
            DebugPositioner.reset();
        }

        else if(KeyBindings.reloadKey.isPressed()) {
            if(itemStack != null) {
                Item item = itemStack.getItem();
                if(item instanceof Reloadable) {
                    ((Reloadable) item).reloadMainHeldItemForPlayer(player);
                }
            }
        }
        
        else if(KeyBindings.unloadKey.isPressed()) {
            if(itemStack != null) {
                Item item = itemStack.getItem();
                if(item instanceof Reloadable) {
                    ((Reloadable) item).unloadMainHeldItemForPlayer(player);
                }
            }
        }
        
        else if(KeyBindings.inspectKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && instance.getState() == WeaponState.MODIFYING) {
                instance.setAltModificationModeEnabled(!instance.isAltMofificationModeEnabled());
            } else if(itemStack != null) {
                Item item = itemStack.getItem();
                if(item instanceof Inspectable) {
                    ((Inspectable) item).inspectMainHeldItemForPlayer(player);
                }
            }
        }

        else if(KeyBindings.laserSwitchKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && (instance.getState() == WeaponState.READY || instance.getState() == WeaponState.MODIFYING)) {
                instance.setLaserOn(!instance.isLaserOn());
            }
        }

        else if(KeyBindings.nightVisionSwitchKey.isPressed()) {
            ItemStack helmetStack = compatibility.getHelmet();
            if(helmetStack != null && helmetStack.getItem() instanceof CustomArmor 
                    && ((CustomArmor)helmetStack.getItem()).hasNightVision()){
                modContext.getChannel().getChannel().sendToServer(new ArmorControlMessage(true));
                NBTTagCompound tagCompound = compatibility.getTagCompound(helmetStack);
                boolean nightVisionOn = tagCompound != null && tagCompound.getBoolean(ArmorControlHandler.TAG_NIGHT_VISION);
                compatibility.playSound(compatibility.clientPlayer(), 
                        nightVisionOn ? modContext.getNightVisionOffSound() : modContext.getNightVisionOnSound(), 1.0f, 1.0f);
            } else {
                PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
                if(instance != null && (instance.getState() == WeaponState.READY || instance.getState() == WeaponState.MODIFYING || instance.getState() == WeaponState.EJECT_REQUIRED)) {
                    instance.setNightVisionOn(!instance.isNightVisionOn());
                }
            }
        }

        else if(KeyBindings.attachmentKey.isPressed()) {
            if(itemStack != null && itemStack.getItem() instanceof Modifiable /* && itemStack.getItem() instanceof Weapon*/) {
                ((Modifiable) itemStack.getItem()).toggleClientAttachmentSelectionMode(player);
            }
        }
        
        else if(KeyBindings.customInventoryKey.isPressed()) {
            modContext.getChannel().getChannel().sendToServer(new OpenCustomPlayerInventoryGuiMessage(GuiHandler.CUSTOM_PLAYER_INVENTORY_GUI_ID));
        }

        else if(KeyBindings.upArrowKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && instance.getState() == WeaponState.MODIFYING) {
                AttachmentCategory category = instance.isAltMofificationModeEnabled() 
                        ? AttachmentCategory.RAILING: AttachmentCategory.SCOPE;
                modContext.getAttachmentAspect().changeAttachment(category, instance);
            }
        }

        else if(KeyBindings.rightArrowKey.isPressed()) {
            PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player);
            if(instance instanceof PlayerWeaponInstance && instance.getState() == WeaponState.MODIFYING) {
                AttachmentCategory category = ((PlayerWeaponInstance) instance).isAltMofificationModeEnabled() 
                        ? AttachmentCategory.STOCK: AttachmentCategory.SKIN;
                modContext.getAttachmentAspect().changeAttachment(category, (PlayerWeaponInstance) instance);
            } else if(instance instanceof PlayerMeleeInstance && instance.getState() == MeleeState.MODIFYING) {
                modContext.getMeleeAttachmentAspect().changeAttachment(AttachmentCategory.SKIN, (PlayerMeleeInstance) instance);
            } else if(instance instanceof PlayerTabletInstance) {
                PlayerTabletInstance playerTabletInstance = (PlayerTabletInstance) instance;
                playerTabletInstance.nextActiveWatchIndex();
            }
        }

        else if(KeyBindings.downArrowKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && instance.getState() == WeaponState.MODIFYING) {
                AttachmentCategory category = instance.isAltMofificationModeEnabled() 
                        ? AttachmentCategory.GUARD: AttachmentCategory.GRIP;
                modContext.getAttachmentAspect().changeAttachment(category, instance);
            }
        }
        
        else if(KeyBindings.laserAttachmentKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && instance.getState() == WeaponState.MODIFYING) {
                AttachmentCategory category = instance.isAltMofificationModeEnabled() 
                        ? AttachmentCategory.BACKGRIP: AttachmentCategory.LASER;
                modContext.getAttachmentAspect().changeAttachment(category, instance);
            }
        }

        else if(KeyBindings.leftArrowKey.isPressed()) {
            PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player);
            if(instance instanceof PlayerWeaponInstance && instance.getState() == WeaponState.MODIFYING) {
                AttachmentCategory category = ((PlayerWeaponInstance) instance).isAltMofificationModeEnabled() 
                        ? AttachmentCategory.RECEIVER: AttachmentCategory.SILENCER;
                modContext.getAttachmentAspect().changeAttachment(category, (PlayerWeaponInstance) instance);
            } else if(instance instanceof PlayerTabletInstance) {
                PlayerTabletInstance playerTabletInstance = (PlayerTabletInstance) instance;
                playerTabletInstance.previousActiveWatchIndex();
            }
        }
        
        else if(KeyBindings.periodKey.isPressed()) {
            PlayerItemInstance<?> instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player);
            if(instance instanceof PlayerWeaponInstance && instance.getState() == WeaponState.MODIFYING 
                    && ((PlayerWeaponInstance) instance).isAltMofificationModeEnabled()) {
                AttachmentCategory category = AttachmentCategory.FRONTSIGHT;
                modContext.getAttachmentAspect().changeAttachment(category, (PlayerWeaponInstance) instance);
            }
        }

        else if(KeyBindings.fireModeKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && instance.getState() == WeaponState.READY) {
                instance.getWeapon().changeFireMode(instance);
            }
        }

        else if(KeyBindings.addKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && (instance.getState() == WeaponState.READY || instance.getState() == WeaponState.EJECT_REQUIRED)) {
                instance.getWeapon().incrementZoom(instance);
            }
        }

        else if(KeyBindings.subtractKey.isPressed()) {
            PlayerWeaponInstance instance = modContext.getPlayerItemInstanceRegistry().getMainHandItemInstance(player, PlayerWeaponInstance.class);
            if(instance != null && (instance.getState() == WeaponState.READY || instance.getState() == WeaponState.EJECT_REQUIRED)) {
                instance.getWeapon().decrementZoom(instance);
            }
        }
        
        else if(KeyBindings.proningSwitchKey.isPressed()) {
            modContext.getChannel().getChannel().sendToServer(new EntityControlMessage(player, 
                    CompatibleExtraEntityFlags.PRONING | CompatibleExtraEntityFlags.FLIP, 0));
        }
    }
}
