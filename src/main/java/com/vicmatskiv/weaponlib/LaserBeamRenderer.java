package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTessellator;
import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;

import net.minecraft.item.Item;

public class LaserBeamRenderer implements CustomRenderer {
	
	private float xOffset = 0.5f;
	private float yOffset = -1.4f;
	private float zOffset = -2f;
	
	public LaserBeamRenderer() {
	}

	@Override
	public void render(RenderContext renderContext) {
		
		Item item = renderContext.getWeapon().getItem();
		if(!(item instanceof Weapon)) {
			throw new IllegalStateException("Item is not weapon");
		}

		CompatibleTransformType type = renderContext.getCompatibleTransformType();
		if(Tags.isLaserOn(renderContext.getWeapon()) && (
				type == CompatibleTransformType.THIRD_PERSON_LEFT_HAND 
				|| type == CompatibleTransformType.THIRD_PERSON_RIGHT_HAND 
				|| type == CompatibleTransformType.FIRST_PERSON_LEFT_HAND
				|| type == CompatibleTransformType.FIRST_PERSON_RIGHT_HAND
				|| type == CompatibleTransformType.GROUND)) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1f, 0f, 0f, 0.5f); 
			GL11.glLineWidth(1.5F);
			GL11.glDepthMask(false);

			GL11.glRotatef(0f, 0f, 1f, 0f);

			compatibility.disableLightMap();
			
			CompatibleTessellator tessellator = CompatibleTessellator.getInstance();
			tessellator.startDrawingLines();

			long time = System.currentTimeMillis();
			Random random = new Random(time - time % 300);
			float start = zOffset; //forwardOffset;
			float length = 100;

			float end = 0;
			for(int i = 0; i < 100 && start < length && end < length; i++) {
				tessellator.addVertex(xOffset, yOffset, start);
				
		        int ii = 15728880; //this.getBrightnessForRender(partialTicks); // or simply set it to 200?
		        int j = ii >> 16 & 65535;
		        int k = ii & 65535;
		        tessellator.setLightMap(j, k);
				end = start - ( 1 + random.nextFloat() * 2);
				if(end > length) end = length;
				tessellator.addVertex(xOffset, yOffset, end);
				tessellator.endVertex();
				start = end + random.nextFloat() * 0.5f;
			}

			tessellator.draw();
			
			compatibility.enableLightMap();
			
			GL11.glDepthMask(true);// do we need this?
			
			GL11.glPopAttrib();

			GL11.glPopMatrix();
		}
	}
}
