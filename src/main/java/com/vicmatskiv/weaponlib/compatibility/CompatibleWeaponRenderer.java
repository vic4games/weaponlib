package com.vicmatskiv.weaponlib.compatibility;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.AttachmentContainer;
import com.vicmatskiv.weaponlib.CompatibleAttachment;
import com.vicmatskiv.weaponlib.CustomRenderer;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ModelWithAttachments;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.Tuple;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponRenderer.Builder;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning;
import com.vicmatskiv.weaponlib.animation.MultipartPositioning.Positioner;
import com.vicmatskiv.weaponlib.animation.MultipartRenderStateManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public abstract class CompatibleWeaponRenderer implements IItemRenderer {
	
	protected static class RenderContext {
		private EntityPlayer player;
		private ItemStack weapon;

		public RenderContext(EntityPlayer player, ItemStack weapon) {
			this.player = player;
			this.weapon = weapon;
		}

		public EntityPlayer getPlayer() {
			return player;
		}

		public ItemStack getWeapon() {
			return weapon;
		}
	}
	
	protected static class StateDescriptor {
		protected MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager;
		protected float rate;
		protected float amplitude = 0.04f;
		public StateDescriptor(MultipartRenderStateManager<RenderableState, Part, RenderContext> stateManager,
				float rate, float amplitude) {
			this.stateManager = stateManager;
			this.rate = rate;
			this.amplitude = amplitude;
		}
		
	}
	
	private Builder builder;
	
	protected CompatibleWeaponRenderer(Builder builder){
		this.builder = builder;
	}
	
	protected abstract StateDescriptor getStateDescriptor(EntityPlayer player, ItemStack itemStack);
	
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
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		
		GL11.glScaled(-1F, -1F, 1F);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		RenderContext renderContext = new RenderContext(player, item);
		Positioner<Part, RenderContext> positioner = null;
		switch (type)
		{
		case ENTITY:
			builder.getEntityPositioning().accept(item);
			break;
		case INVENTORY:
			builder.getInventoryPositioning().accept(item);
			break;
		case EQUIPPED:
			
			builder.getThirdPersonPositioning().accept(player, item);
			
			break;
		case EQUIPPED_FIRST_PERSON:
			
			StateDescriptor stateDescriptor = getStateDescriptor(player, item);
			MultipartPositioning<Part, RenderContext> multipartPositioning = stateDescriptor.stateManager.nextPositioning();
			
			positioner = multipartPositioning.getPositioner();
						
			positioner.randomize(stateDescriptor.rate, stateDescriptor.amplitude);
			
			positioner.position(Part.WEAPON, renderContext);
			
			renderLeftArm(player, renderContext, positioner);
			
			renderRightArm(player, renderContext, positioner);
	        
			break;
		default:
		}
		
		if(builder.getTextureName() != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId() 
					+ ":textures/models/" + builder.getTextureName()));
		} else {
			Weapon weapon = ((Weapon) item.getItem());
			String textureName = weapon.getActiveTextureName(item);
			if(textureName != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId() 
						+ ":textures/models/" + textureName));
			}
		}
		
		builder.getModel().render(null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		if(builder.getModel() instanceof ModelWithAttachments) {
			List<CompatibleAttachment<? extends AttachmentContainer>> attachments = ((Weapon) item.getItem()).getActiveAttachments(item);
			renderAttachments(positioner, renderContext, item, type, attachments , null,  0.0F, 0.0f, -0.4f, 0.0f, 0.0f, 0.08f);
		}
		
		GL11.glPopMatrix();
	   
	}
	
	private void renderAttachments(Positioner<Part, RenderContext> positioner, RenderContext renderContext,
			ItemStack itemStack, ItemRenderType type, List<CompatibleAttachment<? extends AttachmentContainer>> attachments, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		
		for(CompatibleAttachment<?> compatibleAttachment: attachments) {
			if(compatibleAttachment != null) {
				GL11.glPushMatrix();
				
				ItemAttachment<?> itemAttachment = compatibleAttachment.getAttachment();
				
				if(positioner != null) {
					if(itemAttachment instanceof Part) {
						positioner.position((Part) itemAttachment, renderContext);
					} else if(itemAttachment.getRenderablePart() != null) {
						positioner.position(itemAttachment.getRenderablePart(), renderContext);
					}
				}
				

				for(Tuple<ModelBase, String> texturedModel: compatibleAttachment.getAttachment().getTexturedModels()) {
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(builder.getModId() 
							+ ":textures/models/" + texturedModel.getV()));
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					if(compatibleAttachment.getPositioning() != null) {
						compatibleAttachment.getPositioning().accept(texturedModel.getU());
					}
					texturedModel.getU().render(entity, f, f1, f2, f3, f4, f5);

					CustomRenderer postRenderer = compatibleAttachment.getAttachment().getPostRenderer();
					if(postRenderer != null) {
						postRenderer.render(CompatibleTransformType.fromItemRenderType(type), itemStack);
					}
					GL11.glPopAttrib();
					GL11.glPopMatrix();
				}
				
				GL11.glPopMatrix();
			}
		}
		
	}

	private void renderRightArm(EntityPlayer player, RenderContext renderContext,
			Positioner<Part, RenderContext> positioner) {
		RenderPlayer render = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
		Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
		GL11.glPushMatrix();
		GL11.glScaled(1F, 1F, 1F);
		
		GL11.glScaled(1F, 1F, 1F);
		GL11.glTranslatef(-0.25f, 0f, 0.2f);
		
		GL11.glRotatef(5F, 1f, 0f, 0f);
		GL11.glRotatef(25F, 0f, 1f, 0f);
		GL11.glRotatef(0F, 0f, 0f, 1f);
		
		positioner.position(Part.RIGHT_HAND, renderContext);
		GL11.glColor3f(1F, 1F, 1F);
		render.modelBipedMain.onGround = 0.0F;
		render.modelBipedMain.setRotationAngles(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		render.modelBipedMain.bipedRightArm.render(0.0625F);
		GL11.glPopMatrix();
	}

	private void renderLeftArm(EntityPlayer player, RenderContext renderContext,
			Positioner<Part, RenderContext> positioner) {
		RenderPlayer render = (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
		Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
		
		GL11.glPushMatrix();
		
		GL11.glScaled(1F, 1F, 1F);
		
		GL11.glTranslatef(0f, -1f, 0f);
		
		GL11.glRotatef(-10F, 1f, 0f, 0f);
		GL11.glRotatef(0F, 0f, 1f, 0f);
		GL11.glRotatef(10F, 0f, 0f, 1f);
		
		positioner.position(Part.LEFT_HAND, renderContext);
		
		GL11.glColor3f(1F, 1F, 1F);
		render.modelBipedMain.onGround = 0.0F;
		render.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		render.modelBipedMain.bipedLeftArm.render(0.0625F);
		
		GL11.glPopMatrix();
	}
}
