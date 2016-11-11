package com.vicmatskiv.weaponlib.animation;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Test;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class StateManagerTest {

	BiConsumer<EntityPlayer, ItemStack> s0Position = (p, i) -> {
		GL11.glTranslatef(0, 0, 0);
	};

	BiConsumer<EntityPlayer, ItemStack> s1Position = (p, i) -> {
		GL11.glRotatef(-25f, 0f, 0f, 1f);
		GL11.glTranslatef(300, 180, 0);
	};

	BiConsumer<EntityPlayer, ItemStack> s2Position = (p, i) -> {
		// GL11.glRotatef(-25f, 0f, 0f, 1f);
		GL11.glTranslatef(100, 100, 0);
	};

	BiConsumer<EntityPlayer, ItemStack> s3Position = (p, i) -> {
		// GL11.glRotatef(-25f, 0f, 0f, 1f);
		GL11.glTranslatef(0, 0, 0);
	};
	
	BiConsumer<EntityPlayer, ItemStack> aPosition = (p, i) -> {
		// GL11.glRotatef(-25f, 0f, 0f, 1f);
		GL11.glTranslatef(0, 0, 0);
	};
	
	BiConsumer<EntityPlayer, ItemStack> bPosition = (p, i) -> {
		// GL11.glRotatef(-25f, 0f, 0f, 1f);
		GL11.glTranslatef(100, 100, 0);
	};

	@Test
	public void test() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.create();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

//		TransitionProvider<String> positioningManager = (state) -> {
//			if (state.equals("s0"))
//				return Collections.singletonList(new Transition(s0Position, 300, 0));
//			else if (state.equals("s2"))
//				return Arrays.asList(new Transition(s1Position, 310, 300), new Transition(s2Position, 320, 0));
//			else if (state.equals("s3"))
//				return Collections.singletonList(new Transition(s3Position, 330, 0));
//			else
//				return null;
//		};
		
		
		TransitionProvider<String> positioningManager = (state) -> {
			if (state.equals("a"))
				return Collections.singletonList(new Transition(aPosition, 50, 0));
			else if (state.equals("b"))
				return Collections.singletonList(new Transition(bPosition, 0, 0));
			else
				return null;
		};

		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		RenderStateManager<String> stateManager = new RenderStateManager<>("a", positioningManager);
		BiConsumer<EntityPlayer, ItemStack> position = stateManager.getPosition();
		Assert.assertNotNull(position);
		GL11.glPopMatrix();

		for(int i = 0; i < 1000; i++) {
			stateManager.setState("b", true, false);
			stateManager.setState("a", true, false);
		}
		

		while (!Display.isCloseRequested()) {
			// if(System.currentTimeMillis() % 20 != 0) continue;
			pollInput(stateManager);
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			BiConsumer<EntityPlayer, ItemStack> position2 = stateManager.getPosition();
			position2.accept(null, null);

			// Clear the screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			// set the color of the quad (R,G,B,A)
			GL11.glColor3f(0.5f, 0.5f, 1.0f);

			// draw quad
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(100, 100);
			GL11.glVertex2f(100 + 200, 100);
			GL11.glVertex2f(100 + 200, 100 + 200);
			GL11.glVertex2f(100, 100 + 200);
			GL11.glEnd();

			GL11.glPopMatrix();

			Display.update();
		}

		Display.destroy();
	}
	
	boolean previousMouseButton = false;

	public void pollInput(RenderStateManager<String> stateManager) {

		if (Mouse.isButtonDown(0)) {
			int x = Mouse.getX();
			int y = Mouse.getY();

			//System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
			
			if(!previousMouseButton) {
				previousMouseButton = true;
				
//				stateManager.setState("s2", true);
//				stateManager.setState("s3", true);
			}
		} else {
			previousMouseButton = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			System.out.println("SPACE KEY IS DOWN");
		}

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_A) {
					System.out.println("A Key Pressed");
					
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_S) {
					System.out.println("S Key Pressed");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_D) {
					System.out.println("D Key Pressed");
				}
			} else {
				if (Keyboard.getEventKey() == Keyboard.KEY_A) {
					System.out.println("A Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_S) {
					System.out.println("S Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_D) {
					System.out.println("D Key Released");
				}
			}
		}
	}
}
