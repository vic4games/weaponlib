package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.animation.PlayerRawPitchAnimationManager;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExposureCapability;
import com.vicmatskiv.weaponlib.compatibility.CompatibleExtraEntityFlags;
import com.vicmatskiv.weaponlib.compatibility.CompatibleMathHelper;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumHandSide;

public class Interceptors {
    
    public static boolean is3dRenderableItem(Item item) {
        return compatibility.is3dRenderable(item);
    }

    public static void setupCameraTransformAfterHurtCameraEffect(float partialTicks) {

//        FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4).put(new float[] {0.7f, 0.7f, 0.7f, 1.0f });
//        lightAmbient.flip();
//
//        FloatBuffer spotDirection = BufferUtils.createFloatBuffer(4).put(new float[] { -1.0f, 0.0f, 0.0f, 1.0f});
//        spotDirection.flip();
//
//        GL11.glLightf(GL11.GL_LIGHT7, GL11.GL_SPOT_CUTOFF, 45.0f);
//        GL11.glLight(GL11.GL_LIGHT7, GL11.GL_SPOT_DIRECTION, spotDirection);
//        GL11.glLight(GL11.GL_LIGHT7, GL11.GL_AMBIENT, lightAmbient);
//        GL11.glEnable(GL11.GL_LIGHT7);
//        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);

//        EntityPlayer player = compatibility.getClientPlayer();
//        ItemStack itemStack = compatibility.getHeldItemMainHand(player);
//        if(itemStack != null) {
//            Item item = itemStack.getItem();
//            if(item != null && item instanceof Weapon) {
//                Weapon weapon = (Weapon) item;
//                ClientModContext context = (ClientModContext) weapon.getModContext();
//                PlayerWeaponInstance weaponInstance = context.getMainHeldWeapon();
//                
//            }
//        }
        
        PlayerWeaponInstance weaponInstance = getPlayerWeaponInstance();
        EntityPlayer player = compatibility.getClientPlayer();
        if(weaponInstance != null ) {
            ClientModContext context = (ClientModContext) weaponInstance.getWeapon().getModContext();
            PlayerRawPitchAnimationManager yawPitchAnimationManager = context.getPlayerRawPitchAnimationManager();
            if(weaponInstance.isAimed()) {
                yawPitchAnimationManager.update(player);
            } else {
                yawPitchAnimationManager.reset(player);
            }
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
    
    public static void render2(ModelBase base, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        
        if(base instanceof ModelPlayer) {
            
            ModelPlayer modelPlayer = (ModelPlayer) base;

            PlayerRenderer playerRenderer = renderers.computeIfAbsent(entityIn, e -> new PlayerRenderer(ClientModContext.getContext()));
            
            EntityPlayer player = (EntityPlayer) entityIn;
            playerRenderer.renderModel(modelPlayer, player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        } else {
            base.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
    
    public static void renderArmorLayer(ModelBase base, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        
        if(entityIn instanceof EntityPlayer) { 
            PlayerRenderer playerRenderer = renderers.get(entityIn);
            EntityPlayer player = (EntityPlayer) entityIn;
            if(playerRenderer == null || !playerRenderer.renderArmor((ModelBiped) base, player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)) {
                base.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
        } else {
            base.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    public static void positionItemSide(RenderLivingBase<?> livingEntityRenderer, EntityLivingBase entity,
            ItemStack itemStack, TransformType transformType, EnumHandSide handSide) {
        if(entity instanceof EntityPlayer && isProning((EntityPlayer) entity)) { 
            PlayerRenderer playerRenderer = renderers.get(entity);
            EntityPlayer player = (EntityPlayer) entity;
            if(playerRenderer == null || !playerRenderer.positionItemSide(player, itemStack, transformType, handSide)) {
                ((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
            }
        } else {
            ((ModelBiped)livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
        }
    }
    
    public static boolean isProning(EntityPlayer player) {
        return (CompatibleExtraEntityFlags.getFlags(player) & CompatibleExtraEntityFlags.PRONING) != 0;
    }
}
