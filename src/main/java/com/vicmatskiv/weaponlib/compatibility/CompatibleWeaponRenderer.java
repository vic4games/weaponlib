package com.vicmatskiv.weaponlib.compatibility;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;
import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponRenderer;
import com.vicmatskiv.weaponlib.WeaponRenderer.Builder;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateDescriptor;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer.StateDescriptor;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CompatibleWeaponRenderer extends ModelSourceRenderer implements IBakedModel {

    private static final int INVENTORY_TEXTURE_WIDTH = 256;
    private static final int INVENTORY_TEXTURE_HEIGHT = 256;

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.<String, ResourceLocation>newHashMap();

    protected static class StateDescriptor implements MultipartRenderStateDescriptor<RenderableState, Part, RenderContext<RenderableState>>{
		protected MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager;
        protected float rate;
        protected float amplitude = 0.04f;
        private PlayerWeaponInstance instance;
		public StateDescriptor(PlayerWeaponInstance instance, MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> stateManager,
                float rate, float amplitude) {
            this.instance = instance;
            this.stateManager = stateManager;
            this.rate = rate;
            this.amplitude = amplitude;
        }
        @Override
        public MultipartRenderStateManager<RenderableState, Part, RenderContext<RenderableState>> getStateManager() {
            return stateManager;
        }
    }

    protected EntityLivingBase player;

    protected TextureManager textureManager;

    private Pair<? extends IBakedModel, Matrix4f> pair;
    protected ModelBiped playerBiped = new ModelBiped();

    protected ItemStack itemStack;

    protected ModelResourceLocation resourceLocation;

    private class WeaponItemOverrideList extends ItemOverrideList {

        public WeaponItemOverrideList(List<ItemOverride> overridesIn) {
            super(overridesIn);
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world,
                EntityLivingBase entity) {
            CompatibleWeaponRenderer.this.itemStack = stack;
            CompatibleWeaponRenderer.this.player = entity;
            return super.handleItemState(originalModel, stack, world, entity);
        }
    }

    private ItemOverrideList itemOverrideList = new WeaponItemOverrideList(Collections.emptyList());

    TransformType transformType;

    private Builder builder;

    protected CompatibleWeaponRenderer (WeaponRenderer.Builder builder) {
        this.builder = builder;

        this.textureManager = Minecraft.getMinecraft().getTextureManager();
        this.pair = Pair.of((IBakedModel) this, null);
        this.playerBiped = new ModelBiped();
        this.playerBiped.textureWidth = 64;
        this.playerBiped.textureHeight = 64;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if(transformType == TransformType.GROUND
                || transformType == TransformType.GUI
                || transformType == TransformType.FIRST_PERSON_RIGHT_HAND
                || transformType == TransformType.THIRD_PERSON_RIGHT_HAND
                || transformType == TransformType.FIRST_PERSON_LEFT_HAND
                || transformType == TransformType.THIRD_PERSON_LEFT_HAND
                ) {

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldrenderer = tessellator.getBuffer();
            tessellator.draw();
            GlStateManager.pushMatrix();

            if (player != null) {
                if (transformType == TransformType.THIRD_PERSON_RIGHT_HAND) {
                    if (player.isSneaking()) GlStateManager.translate(0.0F, -0.2F, 0.0F);
                } else if (transformType == TransformType.FIRST_PERSON_LEFT_HAND || transformType == TransformType.FIRST_PERSON_RIGHT_HAND) {
                    //
                }
            }

            if (onGround()) {
                GlStateManager.scale(-3f, -3f, -3f);
            }

            int currentTextureId = Framebuffers.getCurrentTexture();
            renderItem();
            if(currentTextureId != 0) {
                GlStateManager.bindTexture(currentTextureId);
            }
            GlStateManager.popMatrix();
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        }

        // Reset the dynamic values.
        this.player = null;
        this.itemStack = null;
        this.transformType = null;

        return Collections.emptyList();
    }

    protected boolean onGround() {
        return transformType == null;
    }

    @Override
    public final boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public final boolean isGui3d() {
        return true;
    }

    @Override
    public final boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    public void setOwner(EntityLivingBase player) {
        this.player = player;
    }

    protected abstract ClientModContext getClientModContext();

//    protected abstract StateDescriptor getStateDescriptor(EntityLivingBase player, ItemStack itemStack);
    
    protected abstract StateDescriptor getFirstPersonStateDescriptor(EntityLivingBase player, ItemStack itemStack);

    protected abstract StateDescriptor getThirdPersonStateDescriptor(EntityLivingBase player, ItemStack itemStack);

    @SideOnly(Side.CLIENT)
    public void renderItem()
    {
        GL11.glPushMatrix();

        //Framebuffer originalFramebuffer = Minecraft.getMinecraft().getFramebuffer();
        Framebuffer framebuffer = null;
        Integer inventoryTexture = null;

        boolean inventoryTextureInitializationPhaseOn = false;

        Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution scaledresolution = new ScaledResolution(mc);

        int originalFramebufferId = -1;

        if(transformType == TransformType.GUI) {

            Object textureMapKey = this; //weaponItemStack != null ? weaponItemStack : this;
            inventoryTexture = getClientModContext().getInventoryTextureMap().get(textureMapKey);

            if(inventoryTexture == null) {
                originalFramebufferId = Framebuffers.getCurrentFramebuffer();

                Framebuffers.unbindFramebuffer();

                inventoryTextureInitializationPhaseOn = true;
                framebuffer = new Framebuffer(INVENTORY_TEXTURE_WIDTH, INVENTORY_TEXTURE_HEIGHT, true);

                framebuffer.bindFramebuffer(true);

                inventoryTexture = framebuffer.framebufferTexture;

                getClientModContext().getInventoryTextureMap().put(textureMapKey, inventoryTexture);

                setupInventoryRendering(INVENTORY_TEXTURE_WIDTH, INVENTORY_TEXTURE_HEIGHT);

            }
        }

		RenderContext<RenderableState> renderContext = new RenderContext<>(getClientModContext(), player, itemStack);

        renderContext.setAgeInTicks(-0.4f);
        renderContext.setScale(0.08f);
        renderContext.setCompatibleTransformType(CompatibleTransformType.fromItemRenderType(transformType));

        Positioner<Part, RenderContext<RenderableState>> positioner = null;
        switch (transformType)
        {
        case GROUND:
            GL11.glScaled(-1F, -1F, 1F);
            GL11.glScaled(0.45F, 0.45F, 0.45F);
            GL11.glTranslatef(-1.1f, -0.9f, 0f);
            GL11.glRotatef(0F, 1f, 0f, 0f);
            GL11.glRotatef(0F, 0f, 1f, 0f);
            GL11.glRotatef(0F, 0f, 0f, 1f);
            builder.getEntityPositioning().accept(itemStack);
            break;
        case GUI:
            GL11.glScaled(-1F, -1F, 1F);

            //RenderHelper.enableStandardItemLighting();
            GL11.glScalef(140f, 140f, 140f);

            if(DebugPositioner.isDebugModeEnabled()) {
                DebugPositioner.position(Part.INVENTORY, null);
            }

            GL11.glRotatef(-20.000000f, 1f, 0f, 0f);
            GL11.glRotatef(60.000000f, 0f, 1f, 0f);
            GL11.glRotatef(15.000000f, 0f, 0f, 1f);
            GL11.glTranslatef(-1.9f, -1.1f, 0f);

            builder.getInventoryPositioning().accept(itemStack);
            break;
        case THIRD_PERSON_RIGHT_HAND: case THIRD_PERSON_LEFT_HAND:
            GL11.glScaled(-1F, -1F, 1F);
            GL11.glScaled(0.4F, 0.4F, 0.4F);
            GL11.glTranslatef(-1.25f, -2.1f, 0.6f);
            GL11.glRotatef(110F, 1f, 0f, 0f);
            GL11.glRotatef(135F, 0f, 1f, 0f);
            GL11.glRotatef(-180F, 0f, 0f, 1f);

            builder.getThirdPersonPositioning().accept(renderContext);
            break;
        case FIRST_PERSON_RIGHT_HAND: case FIRST_PERSON_LEFT_HAND:

            fixVersionSpecificFirstPersonPositioning(transformType);

            GL11.glScaled(-1F, -1F, 1F);

            StateDescriptor stateDescriptor = getFirstPersonStateDescriptor(player, itemStack);
            renderContext.setPlayerItemInstance(stateDescriptor.instance);
            MultipartPositioning<Part, RenderContext<RenderableState>> multipartPositioning = stateDescriptor.stateManager.nextPositioning();

            renderContext.setTransitionProgress(multipartPositioning.getProgress());

            renderContext.setFromState(multipartPositioning.getFromState(RenderableState.class));

            renderContext.setToState(multipartPositioning.getToState(RenderableState.class));

            positioner = multipartPositioning.getPositioner();

            positioner.randomize(stateDescriptor.rate, stateDescriptor.amplitude);

            positioner.position(Part.MAIN_ITEM, renderContext);

            if(DebugPositioner.isDebugModeEnabled()) {
                DebugPositioner.position(Part.MAIN_ITEM, renderContext);
            }

            if(player != null && player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof Weapon) {
                // Draw hands only if weapon is held in the main hand
                renderLeftArm(player, renderContext, positioner);
                renderRightArm(player, renderContext, positioner);
            }

            break;
        default:
        }

        if(transformType != TransformType.GUI || inventoryTextureInitializationPhaseOn) {
            renderItem(itemStack, renderContext, positioner);
        }

        if(transformType == TransformType.GUI  && inventoryTextureInitializationPhaseOn) {
            framebuffer.unbindFramebuffer();
            framebuffer.framebufferTexture = -1;
            framebuffer.deleteFramebuffer();

            restoreInventoryRendering(scaledresolution);
        }

        GL11.glPopMatrix();

        if(originalFramebufferId >= 0) {
            Framebuffers.bindFramebuffer(originalFramebufferId, true, mc.getFramebuffer().framebufferWidth,
                    mc.getFramebuffer().framebufferHeight);
        }

        if(transformType == TransformType.GUI) {
            renderCachedInventoryTexture(inventoryTexture);
        }
    }

    static void fixVersionSpecificFirstPersonPositioning(TransformType transformType) {
        int i = transformType == TransformType.FIRST_PERSON_RIGHT_HAND ? 1 : -1;

        GL11.glTranslatef(0.5f, 0.5f, 0.5f); // untranslate 1.9.4

        i = -i;
        GL11.glTranslatef((float)i * 0.56F, 0.52F + /*p_187459_2_ * */ +0.6F, 0.72F); // untranslate 1.9.4

        if(transformType == TransformType.FIRST_PERSON_LEFT_HAND) {
            // mirror everything if left hand
            GL11.glScalef(-1f, 1f, 1f);
        }

        i = 1; // Draw everything as if for the right hand, assuming mirroring is already in place
        GL11.glTranslatef((float)i * 0.56F, -0.52F + /*p_187459_2_ * */ -0.6F, -0.72F); // re-translate 1.9.4

        GL11.glTranslatef(0f, 0.6f, 0f); // -0.6 y-offset is set somewhere upstream in 1.9.4, so adjusting it

        GL11.glRotatef(45f, 0f, 1f, 0f); // rotate as per 1.8.9 transformFirstPersonItem

        GL11.glScalef(0.4F, 0.4F, 0.4F); // scale as per 1.8.9 transformFirstPersonItem
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
    }

    private void setupInventoryRendering(double projectionWidth, double projectionHeight) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, projectionWidth, projectionHeight, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    private void restoreInventoryRendering(final ScaledResolution scaledresolution) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

//        GlStateManager.loadIdentity();
//        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    private void renderCachedInventoryTexture(Integer inventoryTexture) {

        //RenderHelper.enableGUIStandardItemLighting();

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

        // Undo inventory translations

        GL11.glTranslatef(0.0F, 1.0F, 0.5F);
        GL11.glScalef(0.004F, 0.004F, 0.004F);
        GL11.glScalef(1.0F, -1.0F, 1F);
        GlStateManager.translate(-8.0F, -8.0F, 0.0F);

        GlStateManager.bindTexture(inventoryTexture);
        //Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(CALIBRATION_TEXTURE));

        drawTexturedQuadFit(0, 0, 256, 256, 0);

//        GlStateManager.enableLighting();
        GL11.glPopAttrib();

        GL11.glPopMatrix();
    }

    private static void drawTexturedQuadFit(double x, double y, double width, double height, double zLevel){
        CompatibleTessellator tessellator = CompatibleTessellator.getInstance();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
        tessellator.draw();
    }

    static <T> void renderRightArm(EntityLivingBase player, RenderContext<T> renderContext,
            Positioner<Part, RenderContext<T>> positioner) {
        Render<AbstractClientPlayer> entityRenderObject = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((AbstractClientPlayer)player);
        RenderPlayer render = (RenderPlayer) entityRenderObject;
        Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());

        GL11.glPushMatrix();
        GL11.glScaled(1F, 1F, 1F);
        GL11.glTranslatef(-0.25f, 0f, 0.2f);
        GL11.glRotatef(5F, 1f, 0f, 0f);
        GL11.glRotatef(25F, 0f, 1f, 0f);
        GL11.glRotatef(0F, 0f, 0f, 1f);
        positioner.position(Part.RIGHT_HAND, renderContext);
        if(DebugPositioner.isDebugModeEnabled()) {
            DebugPositioner.position(Part.RIGHT_HAND, renderContext);
        }

        renderContext.capturePartPosition(Part.RIGHT_HAND);

        renderRightArm(render.getMainModel(),(AbstractClientPlayer) player);
        
        ItemStack itemstack = getItemStackFromSlot(player, EntityEquipmentSlot.CHEST);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            //ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
            render.bindTexture(getArmorResource(player, itemstack, EntityEquipmentSlot.CHEST, null));
            
            ModelBiped armorModel = getArmorModelHook(player, itemstack, EntityEquipmentSlot.CHEST, null);
            if(armorModel != null) {
                renderRightArm(armorModel,(AbstractClientPlayer) player);
            }
        }

        GL11.glPopMatrix();
    }

    static <T> void renderLeftArm(EntityLivingBase player, RenderContext<T> renderContext,
            Positioner<Part, RenderContext<T>> positioner) {
        Render<AbstractClientPlayer> entityRenderObject = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((AbstractClientPlayer)player);
        RenderPlayer render = (RenderPlayer) entityRenderObject;
        Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());

        GL11.glPushMatrix();
        GL11.glTranslatef(0f, -1f, 0f);
        GL11.glRotatef(-10F, 1f, 0f, 0f);
        GL11.glRotatef(0F, 0f, 1f, 0f);
        GL11.glRotatef(10F, 0f, 0f, 1f);
        positioner.position(Part.LEFT_HAND, renderContext);
        if(DebugPositioner.isDebugModeEnabled()) {
            DebugPositioner.position(Part.LEFT_HAND, renderContext);
        }

        renderContext.capturePartPosition(Part.LEFT_HAND);

        renderLeftArm(render.getMainModel(),(AbstractClientPlayer) player);

        ItemStack itemstack = getItemStackFromSlot(player, EntityEquipmentSlot.CHEST);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            //ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
            render.bindTexture(getArmorResource(player, itemstack, EntityEquipmentSlot.CHEST, null));
            
            ModelBiped armorModel = getArmorModelHook(player, itemstack, EntityEquipmentSlot.CHEST, null);
            if(armorModel != null) {
                renderLeftArm(armorModel,(AbstractClientPlayer) player);
            }
        }
        GL11.glPopMatrix();
    }

    protected abstract void renderItem(ItemStack weaponItemStack, RenderContext<RenderableState> renderContext,
            Positioner<Part, RenderContext<RenderableState>> positioner);

    private static void renderRightArm(ModelBiped modelplayer, AbstractClientPlayer clientPlayer) {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        //ModelPlayer modelplayer = renderPlayer.getMainModel();
        // Can ignore private method setModelVisibilities since it was already called earlier for left hand
        setModelVisibilities(modelplayer, clientPlayer);

        GlStateManager.enableBlend();
        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedRightArm.rotateAngleX = -0.3F;
        modelplayer.bipedRightArm.rotateAngleY = 0.0F;
        modelplayer.bipedRightArm.render(0.0625F);
        if(modelplayer instanceof ModelPlayer) {
            ((ModelPlayer)modelplayer).bipedRightArmwear.rotateAngleX = 0.0F;
            ((ModelPlayer)modelplayer).bipedRightArmwear.render(0.0625F);
        }
       
        GlStateManager.disableBlend();
    }
    
    private static void renderLeftArm(ModelBiped modelplayer, AbstractClientPlayer clientPlayer) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        setModelVisibilities(modelplayer, clientPlayer);
        
        GlStateManager.enableBlend();
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0F;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedLeftArm.rotateAngleX = 0.0F;
        modelplayer.bipedLeftArm.render(0.0625F);
        if(modelplayer instanceof ModelPlayer) {
            ((ModelPlayer)modelplayer).bipedLeftArmwear.rotateAngleX = 0.0F;
            ((ModelPlayer)modelplayer).bipedLeftArmwear.render(0.0625F);
        }
        
        GlStateManager.disableBlend();
    }

    private static void setModelVisibilities(ModelBiped modelplayer, AbstractClientPlayer clientPlayer)
    {
        //ModelPlayer modelplayer = renderPlayer.getMainModel();

        if (clientPlayer.isSpectator())
        {
            //modelplayer.setInvisible(false);
            modelplayer.setVisible(true);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        }
        else
        {
            ItemStack itemstack = clientPlayer.getHeldItemMainhand();
            ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
            modelplayer.setVisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);

            if(modelplayer instanceof ModelPlayer) {
                ((ModelPlayer)modelplayer).bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
                ((ModelPlayer)modelplayer).bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
                ((ModelPlayer)modelplayer).bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
                ((ModelPlayer)modelplayer).bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
                ((ModelPlayer)modelplayer).bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            }
            
            modelplayer.isSneak = clientPlayer.isSneaking();
            ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

            if (itemstack != null)
            {
                modelbiped$armpose = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                    }
                    else if (enumaction == EnumAction.BOW)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (itemstack1 != null)
            {
                modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction1 = itemstack1.getItemUseAction();

                    if (enumaction1 == EnumAction.BLOCK)
                    {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                    }
                }
            }

            if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT) {
                modelplayer.rightArmPose = modelbiped$armpose;
                modelplayer.leftArmPose = modelbiped$armpose1;
            } else {
                modelplayer.rightArmPose = modelbiped$armpose1;
                modelplayer.leftArmPose = modelbiped$armpose;
            }
        }
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {

        return itemOverrideList;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        this.transformType = cameraTransformType;
        return pair;
    }

    protected static ModelBiped getArmorModelHook(net.minecraft.entity.EntityLivingBase entity, net.minecraft.item.ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }
    
    protected void setModelVisible(ModelBiped model) {
        model.setVisible(true);
    }
    
    @SuppressWarnings("incomplete-switch")
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn) {
        this.setModelVisible(p_188359_1_);

        switch (slotIn)
        {
            case HEAD:
                p_188359_1_.bipedHead.showModel = true;
                p_188359_1_.bipedHeadwear.showModel = true;
                break;
            case CHEST:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightArm.showModel = true;
                p_188359_1_.bipedLeftArm.showModel = true;
                break;
            case LEGS:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
                break;
            case FEET:
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
        }
    }

    @Nullable
    public static ItemStack getItemStackFromSlot(EntityLivingBase living, EntityEquipmentSlot slotIn) {
        return living.getItemStackFromSlot(slotIn);
    }

//    public static ModelBiped getModelFromSlot(EntityEquipmentSlot slotIn) {
//        return (ModelBiped)(isLegSlot(slotIn) ? this.modelLeggings : this.modelArmor);
//    }

    private static boolean isLegSlot(EntityEquipmentSlot slotIn) {
        return slotIn == EntityEquipmentSlot.LEGS;
    }
    
    public static ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EntityEquipmentSlot slot, String type)
    {
        ItemArmor item = (ItemArmor)stack.getItem();
        String texture = item.getArmorMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1)
        {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (isLegSlot(slot) ? 2 : 1), type == null ? "" : String.format("_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = (ResourceLocation)ARMOR_TEXTURE_RES_MAP.get(s1);

        if (resourcelocation == null)
        {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
        }

        return resourcelocation;
    }
}
