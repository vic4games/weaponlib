package com.vicmatskiv.weaponlib.compatibility;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import com.google.common.collect.Maps;
import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponRenderer;
import com.vicmatskiv.weaponlib.WeaponRenderer.Builder;
import com.vicmatskiv.weaponlib.animation.ClientValueRepo;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.animation.MatrixHelper;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateDescriptor;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.compatibility.CompatibleWeaponRenderer.StateDescriptor;
import com.vicmatskiv.weaponlib.debug.DebugRenderer;
import com.vicmatskiv.weaponlib.numerical.LissajousCurve;
import com.vicmatskiv.weaponlib.render.Bloom;
import com.vicmatskiv.weaponlib.render.Dloom;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
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

    
    public static void applyRotationAtPoint(float xOffset, float yOffset, float zOffset, float xRotation, float yRotation, float zRotation) {
    	GL11.glTranslatef(-xOffset, -yOffset, -zOffset);
        
        
        GL11.glRotatef(xRotation, 1f, 0f, 0f);
        GL11.glRotatef(yRotation, 0f, 1f, 0f);
        GL11.glRotatef(zRotation, 0f, 0f, 1f);

        GL11.glTranslatef(xOffset, yOffset, zOffset);
    }
    public static Shader gunLightingShader = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/gunlight"));
    public static Shader flash = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/flash"));
	
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
            if(player instanceof EntityPlayer && !Interceptors.isProning((EntityPlayer) player)){
                StateDescriptor thirdPersonStateDescriptor = getThirdPersonStateDescriptor(player, itemStack);

                renderContext.setPlayerItemInstance(thirdPersonStateDescriptor.instance);

                MultipartPositioning<Part, RenderContext<RenderableState>> thirdPersonMultipartPositioning = thirdPersonStateDescriptor.stateManager.nextPositioning();

                renderContext.setTransitionProgress(thirdPersonMultipartPositioning.getProgress());

                renderContext.setFromState(thirdPersonMultipartPositioning.getFromState(RenderableState.class));

                renderContext.setToState(thirdPersonMultipartPositioning.getToState(RenderableState.class));

                positioner = thirdPersonMultipartPositioning.getPositioner();
                
                positioner.position(Part.MAIN_ITEM, renderContext);
                
                if(DebugPositioner.isDebugModeEnabled()) {
                    DebugPositioner.position(Part.MAIN_ITEM, renderContext);
                }
            } else {
                builder.getThirdPersonPositioning().accept(renderContext);
            }
            break;
        case FIRST_PERSON_RIGHT_HAND: case FIRST_PERSON_LEFT_HAND:

        	/*
        	 * 
        	 */
        	
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

            
            RecoilParam parameters = renderContext.getWeaponInstance().getWeapon().getRecoilParameters();
           
            
            boolean scopeFlag = true;
            boolean isPistol = parameters.getRecoilGroup() == 1;
            
            boolean isShotgun = parameters.getRecoilGroup() == 2;
            boolean isAssault = parameters.getRecoilGroup() == 0;
            float min = (isAssault && renderContext.getWeaponInstance().isAimed()) ? 0.2f : 1f;
            if(renderContext.getWeaponInstance().getScope() != null &&
            		renderContext.getWeaponInstance().getScope().isOptical() && renderContext.getWeaponInstance().isAimed()) {
            	min *= 0.5;
            	scopeFlag = true;
            	//System.out.println("yo");
            }
            float maxAngle = (float) (2*Math.PI);
            float time = (float) (35f - (ClientValueRepo.gunPow/400));
            if(min != 1.0) time = 35f;
            float tick = (float) ((float) maxAngle*((Minecraft.getMinecraft().player.ticksExisted%time)/time))-(maxAngle/2);
            
            double amp = 0.07+(ClientValueRepo.gunPow/700);
            double a = 1;
            double b = 2;
            double c = Math.PI;
            
            
            EntityPlayer p = Minecraft.getMinecraft().player;
            
            
            float xRotation = (float) ((float) amp*Math.sin(a*tick+c));
            float yRotation = (float) ((float) amp*Math.sin(b*tick));
            float zRotation = (float) 0;
            
            RenderableState sus = stateDescriptor.getStateManager().getLastState();
            
            float shoting = (float) ClientValueRepo.gunPow;
            if(scopeFlag) shoting *= 0.2f;
           
            float recoilStop = (float) ClientValueRepo.recoilStop/1.5f;
            

            float zRot = (float) ((float) -ClientValueRepo.gunPow/25f+((float) 0))*min;
            
            
            float pistol = 25;
            float pR = isPistol ? (float) ClientValueRepo.randomRot.y : 0f;
            
            float muzzleRiser = (float) shoting/60f;
            if(shoting > recoilStop) {
            	muzzleRiser = recoilStop/60f;
            }
            
            if(isPistol || isShotgun) muzzleRiser *= pistol;
            muzzleRiser *= (min);
            muzzleRiser *= parameters.getMuzzleClimbMultiplier(); 
            
            float wavyBoi = 0f;
            if(!isPistol) {
            	wavyBoi = (float) Math.pow(Math.sin(ClientValueRepo.recovery*0.048+shoting*0.015), 3)*2; 
            } else {
            	wavyBoi = (float) Math.pow(-Math.sin((ClientValueRepo.recovery-ClientValueRepo.gunPow)*0.2), 1)*2;
                
            }
            wavyBoi *= min;
            
            // System.out.println(wavyBoi);
            //System.out.println(System.currentTimeMillis());
           
           //float muzzleDown = ClientValueRepo.gunPow > 30 ? (float) (ClientValueRepo.gunPow-30f)/5f : 0f;
        //    System.out.println(shoting);
           
            float aimMultiplier = renderContext.getWeaponInstance().isAimed() ? 0.1f : 1.0f;
            
            float strafe = (float) ClientValueRepo.strafe * aimMultiplier * 0.7f;
           
            
            float forwardMov = (float) ClientValueRepo.forward * aimMultiplier * 0.7f;
            float rise = (float) (ClientValueRepo.rise/1f);
            
            forwardMov = Math.max(0, forwardMov);
            
            
            // gun sway
            applyRotationAtPoint(0f, 0f, 3f, (float) (xRotation)-(wavyBoi)+forwardMov+(rise/1f), yRotation+strafe, zRotation+zRot);
            
            // Gun inertia
            //applyRotationAtPoint(0.0f, 0.0f, 0.0f, wavyBoi, 0, 0);
            
            float fight = (float) Math.pow(Math.sin(shoting*0.015), 3);
            fight *= min;
           // +-+
            
          //  System.out.println(Minecraft.getMinecraft().player.motionY);
           // float prevWiggle = (float) (2*Math.PI*((Minecraft.getMinecraft().player.ticksExisted%20)/20.0))*Minecraft.getMinecraft().getRenderPartialTicks();
            float prevTickWiggle = (float) (2*Math.PI*(((Minecraft.getMinecraft().player.ticksExisted-1)%20)/20.0));
            
           
           // System.out.println(Minecraft.getMinecraft().player.ticksExisted);
            float tickWiggle = (float) (2*Math.PI*(((ClientValueRepo.ticker.getLerpedFloat())%36)/36.0));
            

            /*
            if(ClientValueRepo.prevTickTick != Minecraft.getMinecraft().player.ticksExisted) {
            	//ClientValueRepo.prevTickTick = Minecraft.getMinecraft().player.ticksExisted;
            	ClientValueRepo.walkYWiggle = tickWiggle;
            }
            */
         //   tickWiggle = MatrixHelper.solveLerp((float) ClientValueRepo.walkYWiggle, tickWiggle, Minecraft.getMinecraft().getRenderPartialTicks());
           	
           
            
            
            float xWiggle = (float) ((float) Math.sin(tickWiggle)*ClientValueRepo.walkingGun.getLerpedPosition());
            //xWiggle = MatrixHelper.solveLerp((float) ClientValueRepo.walkXWiggle, xWiggle, Minecraft.getMinecraft().getRenderPartialTicks());
          
           // ClientValueRepo.walkXWiggle = xWiggle;
            
            
            float yWiggle = (float) ((float) Math.cos(tickWiggle)*ClientValueRepo.walkingGun.getLerpedPosition())*0.02f;
            
            
            float sway = (float) ((float) ((float) Math.sin(tickWiggle*2))*ClientValueRepo.forward)*0.2f;
           
            
            // xWiggle = (float) ClientValueRepo.walkingGun.getLerpedPosition();
           // xWiggle = 0f;
           // forwardMov = 0f;
            
            // Gun inertia
            applyRotationAtPoint(0.0f, 0.0f, 0.0f, (float) ClientValueRepo.yInertia+fight+(isPistol ? -muzzleRiser : 0f)+forwardMov+(rise/1f)+(yWiggle*3), (float) -ClientValueRepo.xInertia-fight+pR+strafe-(forwardMov*3)+(sway*10), (float) ClientValueRepo.xInertia+fight+xWiggle+(forwardMov*10));
            
            
            if(!isPistol) applyRotationAtPoint(0.0f, 0.0f, -1.0f, -muzzleRiser, 0.0f, 0.0f);
            
           
            float limitedShoting = Math.min(shoting, (float) ClientValueRepo.recoilStop/1.5f);
            
            GlStateManager.translate(0.0*parameters.getTranslationMultipliers().x + (-strafe/10)+(sway/3f), (isPistol ? -0.01*limitedShoting : 0f)*parameters.getTranslationMultipliers().y+(rise/35f)+yWiggle+(forwardMov/10f), 0.01*limitedShoting*min*parameters.getTranslationMultipliers().z);
            
            
            if(DebugPositioner.isDebugModeEnabled()) {
                DebugPositioner.position(Part.MAIN_ITEM, renderContext);
            }

            if(player != null && player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof Weapon) {
                // Draw hands only if weapon is held in the main hand
            	gunLightingShader.use();
            	GL20.glUniform1i(GL20.glGetUniformLocation(gunLightingShader.getShaderId(), "lightmap"), 1);
            	GL20.glUniform1f(GL20.glGetUniformLocation(gunLightingShader.getShaderId(), "lightIntensity"), (ClientValueRepo.flash > 0) ? 5.0f : 0.0f);
            	
                renderLeftArm(player, renderContext, positioner);
                renderRightArm(player, renderContext, positioner);
                gunLightingShader.release();
            }

            break;
        default:
        }

        if(transformType != TransformType.GUI || inventoryTextureInitializationPhaseOn) {
        	//gunLightingShader = ShaderManager.loadShader(new ResourceLocation("mw" + ":" + "shaders/gunlight"));
        	
        	/*
        	gunLightingShader.use();
        	//System.out.println(ClientValueRepo.flash);
        	GL20.glUniform1i(GL20.glGetUniformLocation(gunLightingShader.getShaderId(), "lightmap"), 1);
        	GL20.glUniform1f(GL20.glGetUniformLocation(gunLightingShader.getShaderId(), "lightIntensity"), (ClientValueRepo.flash > 0) ? 5.0f : 0.0f);
        	*/
        	
        	
        	//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        	//GlStateManager.color(20.0f, 20.0f, 20.0f);
          
        	//Bloom.data.bindFramebuffer(true);
        	//GlStateManager.enableBlend();
        	//GL14.glBlendEquation(GL14.GL_FUNC_ADD);
        //	Bloom.data.bindFramebuffer(false);
        //	Dloom.bloomData.bindFramebuffer(true);
        //	renderItem(itemStack, renderContext, positioner);
        	//GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        	//Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        	
        	
        	
        	renderItem(itemStack, renderContext, positioner);
        	
        	if(renderContext.getWeaponInstance().isAimed() && ((ClientModContext) renderContext.getModContext()).getSafeGlobals().renderingPhase.get() != RenderingPhase.RENDER_PERSPECTIVE) {
        		Dloom.blitDepth();
        		CompatibleClientEventHandler.postBlur();
        	}
        	
        	//   gunLightingShader.release();
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

    public static void renderRightArm(ModelBiped modelplayer, AbstractClientPlayer clientPlayer) {
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
            ((ModelPlayer)modelplayer).bipedRightArmwear.rotateAngleX = -0.3F;
            ((ModelPlayer)modelplayer).bipedRightArmwear.render(0.0625F);
        }
       
        GlStateManager.disableBlend();
    }
    
    public static void renderLeftArm(ModelBiped modelplayer, AbstractClientPlayer clientPlayer) {
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

    public static void renderVehicleRightArm(ModelBiped modelplayer, AbstractClientPlayer clientPlayer) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        setModelVisibilities(modelplayer, clientPlayer);

        GlStateManager.enableBlend();
        modelplayer.bipedRightArm.render(0.0625F);
        if(modelplayer instanceof ModelPlayer) {
            ((ModelPlayer)modelplayer).bipedRightArmwear.rotateAngleX = 0.0F;
            ((ModelPlayer)modelplayer).bipedRightArmwear.rotateAngleX = -0.3F;
            ((ModelPlayer)modelplayer).bipedRightArmwear.render(0.0625F);
        }
       
        GlStateManager.disableBlend();
    }
    
    public static void renderLeftVehicleArm(ModelBiped modelplayer, AbstractClientPlayer clientPlayer) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        setModelVisibilities(modelplayer, clientPlayer);
        
        GlStateManager.enableBlend();
        modelplayer.bipedLeftArm.render(0.0625F);
        if(modelplayer instanceof ModelPlayer) {
            ((ModelPlayer)modelplayer).bipedLeftArmwear.rotateAngleX = 0.0F;
            ((ModelPlayer)modelplayer).bipedLeftArmwear.render(0.0625F);
        }
        
        GlStateManager.disableBlend();
    }
    
    public static void setModelVisibilities(ModelBiped modelplayer, AbstractClientPlayer clientPlayer)
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

    public static ModelBiped getArmorModelHook(net.minecraft.entity.EntityLivingBase entity, net.minecraft.item.ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
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
