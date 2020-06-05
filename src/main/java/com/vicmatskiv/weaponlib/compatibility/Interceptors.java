package com.vicmatskiv.weaponlib.compatibility;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.PlayerRenderer;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.SpreadableExposure;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;
import com.vicmatskiv.weaponlib.animation.ScreenShakingAnimationManager;
import com.vicmatskiv.weaponlib.inventory.CustomPlayerInventory;
import com.vicmatskiv.weaponlib.mission.Missions;
import com.vicmatskiv.weaponlib.mission.ObtainItemAction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumHandSide;

public class Interceptors {
    
    public static boolean is3dRenderableItem(Item item) {
        return compatibility.is3dRenderable(item);
    }

    public static void setupCameraTransformAfterHurtCameraEffect(float partialTicks) {
        
        PlayerWeaponInstance weaponInstance = getPlayerWeaponInstance();
        EntityPlayer player = compatibility.getClientPlayer();
        if(weaponInstance != null ) {
            ClientModContext context = (ClientModContext) weaponInstance.getWeapon().getModContext();
            MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager = weaponInstance.getWeapon().getRenderer().getStateManager(player);
//            if(stateManager != null) {
//                RenderableState lastState = stateManager.getLastState();
//                if(lastState != RenderableState.NORMAL && lastState != RenderableState.ZOOMING) {
//                    System.out.println("Last state " + lastState);
//                }
//            }
            ScreenShakingAnimationManager yawPitchAnimationManager = context.getPlayerRawPitchAnimationManager();
            yawPitchAnimationManager.update(player, weaponInstance, stateManager != null ? stateManager.getLastState() : null);
//            if(weaponInstance.isAimed() && !isProning(player)) {
//                yawPitchAnimationManager.update(player, stateManager != null ? stateManager.getLastState() : null);
////                GL11.glRotatef(5f * partialTicks, 1.0F, 0.0F, 1.0F);
//            } else {
//                yawPitchAnimationManager.reset(player, stateManager != null ? stateManager.getLastState() : null);
//            }
        }
    }
    
    private static PlayerWeaponInstance getPlayerWeaponInstance() {
        EntityPlayer player = compatibility.getClientPlayer();
        ItemStack itemStack = compatibility.getHeldItemMainHand(player);
        PlayerWeaponInstance weaponInstance = null;
        if(itemStack != null) {
            Item item = itemStack.getItem();
            if(item != null && item instanceof Weapon) {
                Weapon weapon = (Weapon) item;
                ClientModContext context = (ClientModContext) weapon.getModContext();
                weaponInstance = context.getMainHeldWeapon();
            }
        }
        return weaponInstance;
    }
    
    public static boolean setupViewBobbing(float partialTicks) {
        
        if(!(compatibility.getRenderViewEntity() instanceof EntityPlayer)) {
            return true;
        }
        
        EntityPlayer entityplayer = (EntityPlayer)compatibility.getRenderViewEntity();

        {
            float f =entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
            float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
            float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
            GL11.glTranslatef(CompatibleMathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(CompatibleMathHelper.cos(f1 * (float)Math.PI) * f2), 0.0F);
            GL11.glRotatef(CompatibleMathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(CompatibleMathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f3, 1.0F, 0.0F, 0.0F);
        }
        
        
        {
            SpreadableExposure spreadableExposure = CompatibleExposureCapability.getExposure(entityplayer, SpreadableExposure.class);

            if(spreadableExposure != null) {
                float totalDose = spreadableExposure.getTotalDose();
                
                float f1 = totalDose; // * partialTicks;
                if(f1 > 1f) {
                    f1 = 1f;
                }
                float speed = 0.4f;//

                float f2 = 5f / (f1 * f1 + 5f) - f1 * 0.01F;
                f2 = f2 * f2;
                GL11.glRotatef(((float)spreadableExposure.getTickCount() + partialTicks) * speed, 0.0F, 1.0F, 1.0F);
                GL11.glScalef(1.0F / f2, 1.0F, 1.0F);
                GL11.glRotatef(-((float)spreadableExposure.getTickCount() + partialTicks) * speed, 0.0F, 1.0F, 1.0F);
                spreadableExposure.incrementTickCount();
            }
        }
        
        return false;
    }
    
    public static boolean hurtCameraEffect(float partialTicks) {
        
        if(!(compatibility.getRenderViewEntity() instanceof EntityPlayer)) {
            return true;
        }
        
        boolean allowDefaultEffect = false;

        EntityPlayer entitylivingbase = (EntityPlayer)compatibility.getRenderViewEntity();
        float f = (float) entitylivingbase.hurtTime - partialTicks;

        if (entitylivingbase.getHealth() <= 0.0F) {
            float f1 = (float) entitylivingbase.deathTime + partialTicks;
            GL11.glRotatef(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
        }

        if (f < 0.0F) {
            return allowDefaultEffect;
        }

        f = f / (float) entitylivingbase.maxHurtTime;
        f = CompatibleMathHelper.sin(f * f * f * f * (float) Math.PI);
        float f2 = entitylivingbase.attackedAtYaw;
        GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
        SpreadableExposure spreadableExposure = CompatibleExposureCapability.getExposure(entitylivingbase, SpreadableExposure.class);

        if(spreadableExposure != null) {
            GL11.glRotatef(-f * 4.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-f * 1.0F, 0.0F, 0.0F, 1.0F);
        } else {
            GL11.glRotatef(-f * 14.0F, 0.0F, 0.0F, 1.0F);
        }
        
        GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);

        return allowDefaultEffect;
    }
        
    public static boolean nauseaCameraEffect(float partialTicks) {
        boolean allowDefaultEffect = false;

//        Minecraft mc = Minecraft.getMinecraft();
//
//        float f1 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * partialTicks;
//
//        int i = 1;
//
//        float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
//        f2 = f2 * f2;
//        GlStateManager.rotate(((float)rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);
//        GlStateManager.scale(1.0F / f2, 1.0F, 1.0F);
//        GlStateManager.rotate(-((float)rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);

        //rendererUpdateCount++;
        return allowDefaultEffect;
    }
    

    private static Map<Entity, PlayerRenderer> renderers = new HashMap<>();
    
    public static PlayerRenderer getPlayerRenderer(Entity entity) {
        return renderers.get(entity);
    }
    
    public static void render2(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        
        if(entityIn instanceof EntityPlayer && modelBase instanceof ModelPlayer) {
            
            ModelPlayer modelPlayer = (ModelPlayer) modelBase;
            EntityPlayer player = (EntityPlayer) entityIn;

            PlayerRenderer playerRenderer = renderers.computeIfAbsent(entityIn, 
                    e -> new PlayerRenderer((EntityPlayer) entityIn, ClientModContext.getContext()));
            
            playerRenderer.renderModel(modelPlayer, player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            
            CustomPlayerInventory capability = CompatibleCustomPlayerInventoryCapability.getInventory(player);
            if(capability != null) {
                ItemStack backpackStack = capability.getStackInSlot(0); // TODO: replace 0 with constant for backpack slot 
                if(backpackStack != null) {
                    GL11.glPushMatrix();
                    adjustBodyWearablePosition(player);
                    compatibility.renderItem(player, backpackStack);
                    GL11.glPopMatrix();
                }
                ItemStack vestStack = capability.getStackInSlot(1); // TODO: replace 0 with constant for backpack slot 
                if(vestStack != null) {
                    GL11.glPushMatrix();
                    adjustBodyWearablePosition(player);
                    compatibility.renderItem(player, vestStack);
                    GL11.glPopMatrix();
                }
            }
        } else {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
    
    private static void adjustBodyWearablePosition(EntityPlayer player) {
//        GL11.glScalef(0.8f, 0.8f, 0.8f);
//        GL11.glTranslatef(-0.02f, 0.69f, -0.35f);
//        GL11.glRotatef(180f, 0, 0, 1);
//        if(isProning(player)) {
//            GL11.glScalef(0.8f, 0.8f, 0.8f);
//            GL11.glTranslatef(-0.02f, -3f, -0.35f);
//            GL11.glRotatef(180f, 0, 0, 1);
//        } else {
//            GL11.glScalef(0.8f, 0.8f, 0.8f);
//            GL11.glTranslatef(-0.02f, 0.69f, -0.35f);
//            GL11.glRotatef(180f, 0, 0, 1);
//        }
        
    }

    public static void renderArmorLayer(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        
        if(entityIn instanceof EntityPlayer) { 
            PlayerRenderer playerRenderer = renderers.get(entityIn);
            EntityPlayer player = (EntityPlayer) entityIn;
            if(playerRenderer == null || !playerRenderer.renderArmor((ModelBiped) modelBase, player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)) {
                modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
        } else {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    public static void positionItemSide(RenderLivingBase<?> livingEntityRenderer, EntityLivingBase entity,
            ItemStack itemStack, TransformType transformType, EnumHandSide handSide) {
        if(entity instanceof EntityPlayer /* && isProning((EntityPlayer) entity)*/) { 
            PlayerRenderer playerRenderer = renderers.get(entity);
            EntityPlayer player = (EntityPlayer) entity;
            if(playerRenderer == null || !playerRenderer.positionItemSide(player, itemStack, CompatibleTransformType.fromItemRenderType(transformType), 
                    CompatibleEnumHandSide.fromEnumHandSide(handSide))) {
                ((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
            }
        } else {
            ((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
        }
    }
    
    public static boolean isProning(EntityPlayer player) {
        return (CompatibleExtraEntityFlags.getFlags(player) & CompatibleExtraEntityFlags.PRONING) != 0;
    }
    
    public static float adjustCameraPosition(EntityLivingBase player, float position) {
        return player instanceof EntityPlayer && isProning((EntityPlayer) player) 
                && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 ? position 
                + player.getEyeHeight() * 1.6f : position;
    }
}
