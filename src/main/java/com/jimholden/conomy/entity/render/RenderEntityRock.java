package com.jimholden.conomy.entity.render;

import com.jimholden.conomy.entity.models.DeerModel;
import com.jimholden.conomy.entity.models.HogModel;
import com.jimholden.conomy.entity.models.RockModel;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.ItemRock;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderEntityRock<T extends Entity> extends Render<EntityThrowable> {
	
	private static final ResourceLocation ROCK_TEX = new ResourceLocation(Reference.MOD_ID + ":textures/entity/rock.png");
	private static final ItemStack rock = new ItemStack(ModItems.ROCK);
	private static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	
	public RenderEntityRock(RenderManager renderManager, RenderItem itemRenderer) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void doRender(EntityThrowable entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }
        //System.out.println("IS??:" + itemRender);
        this.itemRender.renderItem(rock, ItemCameraTransforms.TransformType.GROUND);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	

	@Override
	protected ResourceLocation getEntityTexture(EntityThrowable entity) {
		return ROCK_TEX;
	}

}
