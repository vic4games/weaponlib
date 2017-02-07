package com.vicmatskiv.weaponlib;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Viewfinder {
	
	private ViewfinderModel model = new ViewfinderModel();
	private Supplier<Integer> textureProvider;
	private BiConsumer<EntityPlayer, ItemStack> positioning;

	public Viewfinder(Supplier<Integer> textureProvider, BiConsumer<EntityPlayer, ItemStack> positioning) {
		this.textureProvider = textureProvider;
		this.positioning = positioning;
	}

	public void render(EntityPlayer player, ItemStack parentStack, float brightness, 
			float f, float f1, float f2, float f3, float f4, float f5) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

		positioning.accept(player, parentStack);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureProvider.get());
		
		Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glColor4f(brightness, brightness, brightness, 1f);
		model.render(player, f, f1, f2, f3, f4, f5);
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
