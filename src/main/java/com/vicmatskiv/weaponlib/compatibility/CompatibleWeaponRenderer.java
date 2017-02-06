package com.vicmatskiv.weaponlib.compatibility;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.AttachmentContainer;
import com.vicmatskiv.weaponlib.ClientModContext;
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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public abstract class CompatibleWeaponRenderer implements IItemRenderer {
	
	private static ModelBase surface; {
		try {
			surface = (ModelBase) Class.forName("com.vicmatskiv.mw.models.OpticalZoomSurface").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
	
	protected abstract ClientModContext getClientModContext();
	
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
		

		if(type == ItemRenderType.EQUIPPED_FIRST_PERSON && Weapon.isZoomed(null, Minecraft.getMinecraft().thePlayer.getHeldItem())) {
			//framebufferRender(positioner, renderContext, 400, 300);
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
					//GL11.glScalef(1, 1, 0.04f);
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
				
				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				
				if(surface != null) {
					GL11.glScalef(0.79f, 0.79f, 0.79f);
					GL11.glTranslatef(0.13f, -1.6f, 1.5f);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, getClientModContext().getFramebuffer().framebufferTexture);
					Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glDepthMask(true);
		            GL11.glDisable(GL11.GL_LIGHTING);
		            GL11.glDisable(GL11.GL_ALPHA_TEST);
		            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					surface.render(entity, f, f1, f2, f3, f4, f5);
				}
				GL11.glPopAttrib();
				GL11.glPopMatrix();
				
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
	
	public void framebufferRender(Positioner<Part, RenderContext> positioner, RenderContext renderContext, int width, int height)
    {
        if (OpenGlHelper.isFramebufferEnabled()) { 
        	
        	GL11.glEnable(GL11.GL_STENCIL_TEST);
        	
            GL11.glPushMatrix();
    		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    		
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, getClientModContext().getFramebuffer().framebufferTexture);
			
//			GL11.glMatrixMode(GL11.GL_PROJECTION);
//			GL11.glLoadIdentity();
//			GL11.glOrtho(0D, (double)width, (double)height, 0.0D, -1000D, 3000.0D);
//			GL11.glMatrixMode(GL11.GL_MODELVIEW);
//			GL11.glLoadIdentity();
			
			
		    {
		    	Minecraft mc = Minecraft.getMinecraft();
		        ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		        //GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		        GL11.glMatrixMode(GL11.GL_PROJECTION);
		        GL11.glLoadIdentity();
		        GL11.glScalef(0.25f, 0.25f, 0.25f);
		        GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		        GL11.glMatrixMode(GL11.GL_MODELVIEW);
		        GL11.glLoadIdentity();
		        
		        GL11.glTranslatef((scaledresolution.getScaledWidth() - getClientModContext().getFramebuffer().framebufferWidth) / 2, 
		        		(scaledresolution.getScaledHeight() - getClientModContext().getFramebuffer().framebufferHeight) / 2, 
		        		1000.0F);
		        
		        //positioner.position(Part.WEAPON, renderContext);
		    }


			float f2 = (float)getClientModContext().getFramebuffer().framebufferWidth / (float)getClientModContext().getFramebuffer().framebufferTextureWidth;
			float f3 = (float)getClientModContext().getFramebuffer().framebufferHeight / (float)getClientModContext().getFramebuffer().framebufferTextureHeight;
//
			//GL11.glViewport(0, 100, width + 100, height);
//			
			//GL11.glTranslatef(100F, 60.0F, 1001F); //-1000.0F);
			//GL11.glScalef(0.5f, 0.5f, 0.5f);
			
			
            {
				GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
				//GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT );
				//GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glColorMask(false, false, false, false);
				//GL11.glDepthMask(false);
				GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
				GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);   // draw 1s on test fail (always)

	    		GL11.glStencilMask(0xFF);
	    		//GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
	    		//GL11.glClearStencil(0);
	    		
				float items = 30;
	    		float RADIUS = 150;
				GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				float x = 200; 
				float y = 150;
				GL11.glVertex2f(x, y);
				for(int i = 0; i <= items; i++)
				{
					float t = (float) (2 * Math.PI * (float) i / (float) items);
					GL11.glVertex2d(x + Math.sin(t) * RADIUS, y + Math.cos(t) * RADIUS);
				}
				GL11.glEnd();
				
				//GL11.glEnable(GL11.GL_DEPTH_TEST);
		        GL11.glColorMask(true, true, true, true);
		        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 255); // We Draw Only Where The Stencil Is 1
		                                                            // (I.E. Where The Floor Was Drawn)
		        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
				
	        }
            //RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
//            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//			GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);

			Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(-1);
            tessellator.addVertexWithUV(0.0D, (double)height, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV((double)width, (double)height, 0.0D, (double)f2, 0.0D);
            tessellator.addVertexWithUV((double)width, 0.0D, 0.0D, (double)f2, (double)f3);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)f3);
            tessellator.draw();
            

			
//            GL11.glMatrixMode(GL11.GL_PROJECTION);
//			GL11.glLoadIdentity();
//		    Project.gluPerspective(80, (float)Minecraft.getMinecraft().displayWidth / (float)Minecraft.getMinecraft().displayHeight, 0.05F, 100 * 2.0F);
////		    GL11.glViewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
//		      
////			GL11.glOrtho(0D, (double)800, 0d, (double)480, 0D, 1000.0D);
//			GL11.glMatrixMode(GL11.GL_MODELVIEW);
//			GL11.glLoadIdentity();
			
            GL11.glPopAttrib();
            
            GL11.glPopMatrix();
            
            
//            GL11.glEnable(GL11.GL_DEPTH_TEST);
//            GL11.glDepthMask(true);
            
            GL11.glDisable(GL11.GL_STENCIL_TEST);
        	
        }
        
    }
}
