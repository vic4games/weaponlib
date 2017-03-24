package com.vicmatskiv.weaponlib.compatibility;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.CustomRenderer;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.ModelSource;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.StaticModelSourceRenderer.Builder;
import com.vicmatskiv.weaponlib.Tuple;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public abstract class CompatibleStaticModelSourceRenderer implements IItemRenderer {
	
	protected Builder builder;

	protected CompatibleStaticModelSourceRenderer(Builder builder)
	{
		this.builder = builder;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data)
	{
		GL11.glPushMatrix();
		
		GL11.glScaled(-1F, -1F, 1F);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		switch (type)
		{
		case ENTITY:
			builder.getEntityPositioning().accept(itemStack);
			break;
		case INVENTORY:
			builder.getInventoryPositioning().accept(itemStack);
			break;
		case EQUIPPED:
			builder.getThirdPersonPositioning().accept(player, itemStack);
			break;
		case EQUIPPED_FIRST_PERSON:
			builder.getFirstPersonPositioning().accept(player, itemStack);
			break;
		default:
		}
		
		renderModelSource(itemStack, type, null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		
		GL11.glPopMatrix();
	}
	
	private void renderModelSource(
			ItemStack itemStack, ItemRenderType type, Entity entity, 
			float f, float f1, float f2, float f3, float f4, float f5) {
		
		if(!(itemStack.getItem() instanceof ModelSource)) {
			throw new IllegalArgumentException();
		}
		
		GL11.glPushMatrix();

		ModelSource modelSource = (ModelSource)itemStack.getItem();
        for(Tuple<ModelBase, String> texturedModel: modelSource.getTexturedModels()) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId() 
					+ ":textures/models/" + texturedModel.getV()));
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			ModelBase model = texturedModel.getU();
			switch (type)
			{
			case ENTITY:
				builder.getEntityModelPositioning().accept(model, itemStack);
				break;
			case INVENTORY:
				builder.getInventoryModelPositioning().accept(model, itemStack);
				break;
			case EQUIPPED:
				builder.getThirdPersonModelPositioning().accept(model, itemStack);
				break;
			case EQUIPPED_FIRST_PERSON:
				builder.getFirstPersonModelPositioning().accept(model, itemStack);
				break;
			default:
			}
			
			model.render(entity, f, f1, f2, f3, f4, f5);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		
        @SuppressWarnings("unchecked")
        CustomRenderer<RenderableState> postRenderer = (CustomRenderer<RenderableState>) modelSource.getPostRenderer();

		if(postRenderer != null) {
	        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
	        RenderContext<RenderableState> renderContext = new RenderContext<>(getModContext(), player, itemStack);
	        renderContext.setAgeInTicks(-0.4f);
	        renderContext.setScale(0.08f);
	        renderContext.setCompatibleTransformType(CompatibleTransformType.fromItemRenderType(type));

	        renderContext.setPlayerItemInstance(getModContext().getPlayerItemInstanceRegistry()
	                .getItemInstance(player, itemStack));

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);

            postRenderer.render(renderContext);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
		GL11.glPopMatrix();
	}

    protected abstract ModContext getModContext();

}
