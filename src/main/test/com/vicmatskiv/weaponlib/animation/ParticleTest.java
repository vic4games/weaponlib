//package com.vicmatskiv.weaponlib.animation;
//
//import java.awt.Color;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.Collections;
//import java.util.function.BiConsumer;
//
//import javax.imageio.ImageIO;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.lwjgl.BufferUtils;
//import org.lwjgl.LWJGLException;
//import org.lwjgl.input.Keyboard;
//import org.lwjgl.input.Mouse;
//import org.lwjgl.opengl.Display;
//import org.lwjgl.opengl.DisplayMode;
//import org.lwjgl.opengl.GL11;
//
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemStack;
//
//public class ParticleTest {
//
//	BiConsumer<EntityPlayer, ItemStack> s0Position = (p, i) -> {
//		GL11.glTranslatef(0, 0, 0);
//	};
//
//	BiConsumer<EntityPlayer, ItemStack> s1Position = (p, i) -> {
//		GL11.glRotatef(-25f, 0f, 0f, 1f);
//		GL11.glTranslatef(300, 180, 0);
//	};
//
//	BiConsumer<EntityPlayer, ItemStack> s2Position = (p, i) -> {
//		// GL11.glRotatef(-25f, 0f, 0f, 1f);
//		GL11.glTranslatef(100, 100, 0);
//	};
//
//	BiConsumer<EntityPlayer, ItemStack> s3Position = (p, i) -> {
//		// GL11.glRotatef(-25f, 0f, 0f, 1f);
//		GL11.glTranslatef(0, 0, 0);
//	};
//
//	BiConsumer<EntityPlayer, ItemStack> aPosition = (p, i) -> {
//		// GL11.glRotatef(-25f, 0f, 0f, 1f);
//		GL11.glTranslatef(0, 0, 0);
//	};
//
//	BiConsumer<EntityPlayer, ItemStack> bPosition = (p, i) -> {
//		// GL11.glRotatef(-25f, 0f, 0f, 1f);
//		GL11.glTranslatef(100, 100, 0);
//	};
//
//	@Test
//	public void test() throws LWJGLException, IOException {
//		Display.setDisplayMode(new DisplayMode(800, 600));
//		Display.create();
//		GL11.glMatrixMode(GL11.GL_PROJECTION);
//		GL11.glLoadIdentity();
//		//GL11.glOrtho(0, 800, 0, 600, 1, -1);
//		GL11.glOrtho(0, 800, 0, 600, -1, 1);
//		GL11.glMatrixMode(GL11.GL_MODELVIEW);
//
//
//		TransitionProvider<String> positioningManager = (state) -> {
//			if (state.equals("a"))
//				return Collections.singletonList(new Transition(aPosition, 50, 0));
//			else if (state.equals("b"))
//				return Collections.singletonList(new Transition(bPosition, 0, 0));
//			else
//				return null;
//		};
//
//		GL11.glPushMatrix();
//		GL11.glMatrixMode(GL11.GL_MODELVIEW);
//		RenderStateManager<String> stateManager = new RenderStateManager<>("a", positioningManager);
//		BiConsumer<EntityPlayer, ItemStack> position = stateManager.getPosition();
//		Assert.assertNotNull(position);
//		GL11.glPopMatrix();
//
//		for(int i = 0; i < 1000; i++) {
//			stateManager.setState("b", true, false);
//			stateManager.setState("a", true, false);
//		}
//
//		int px = 0;
//
//		while (!Display.isCloseRequested()) {
//			// if(System.currentTimeMillis() % 20 != 0) continue;
//			if(!pollInput(stateManager)) break;
//
//			// Clear the screen and depth buffer
//
//
//			GL11.glPushMatrix();
//			//GL11.glMatrixMode(GL11.GL_MODELVIEW);
//
////			BiConsumer<EntityPlayer, ItemStack> position2 = stateManager.getPosition();
////			position2.accept(null, null);
//
//			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//			GL11.glEnable(GL11.GL_BLEND);
//			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//
//			GL11.glEnable(GL11.GL_TEXTURE_2D);
//
//			//GL11.glTranslatef(px++, px++, 0);
//
//			GL11.glColor4f(1f, 1f, 1f, 1f - (px / 700f));
//
//			GL11.glTranslatef(225f/2, 225f/2, 0);
//			GL11.glRotatef(90, 0, 0, 1f);
//			GL11.glTranslatef(-225f / 2, -225f / 2, 0);
//			//GL11.glTranslatef(450f, 450f, 0);
//
//			drawImage();
//
////			// set the color of the quad (R,G,B,A)
////			GL11.glColor3f(0.5f, 0.5f, 1.0f);
////
////			// draw quad
////			GL11.glBegin(GL11.GL_QUADS);
////			GL11.glVertex2f(100, 100);
////			GL11.glVertex2f(100 + 200, 100);
////			GL11.glVertex2f(100 + 200, 100 + 200);
////			GL11.glVertex2f(100, 100 + 200);
////			GL11.glEnd();
//
//			GL11.glPopMatrix();
//
//			Display.update();
//		}
//
//		Display.destroy();
//	}
//
//	boolean previousMouseButton = false;
//
//	private void drawImage() throws IOException {
//		BufferedImage image = bindTexture();
//
//        GL11.glBegin(GL11.GL_QUADS);
//
//        GL11.glTexCoord2f(0, 1);
//        GL11.glVertex2f(0, 0);
//
//        GL11.glTexCoord2f(1, 1);
//        GL11.glVertex2f(image.getWidth(), 0);
//
//        GL11.glTexCoord2f(1,0);
//        GL11.glVertex2f(image.getWidth(), image.getHeight());
//
//        GL11.glTexCoord2f(0, 0);
//        GL11.glVertex2f(0, image.getHeight());
//
//        GL11.glEnd();
//
//	}
//
//	private BufferedImage bindTexture() throws IOException {
//		BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/sample.png"));
//
//	    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
//	    int[] pixels = new int[image.getWidth() * image.getHeight()];
//        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
//
//	    int textureID = GL11.glGenTextures();
//
//	    for(int y = 0; y < image.getHeight(); y++){
//            for(int x = 0; x < image.getWidth(); x++){
//                int pixel = pixels[y * image.getWidth() + x];
//                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
//                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
//                buffer.put((byte) (pixel & 0xFF));               // Blue component
//                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
//            }
//         }
//
//	    buffer.flip();
//
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
//        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0,
//        		GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
//
//
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
//		return image;
//	}
//
//	public boolean pollInput(RenderStateManager<String> stateManager) {
//
//		if (Mouse.isButtonDown(0)) {
//			int x = Mouse.getX();
//			int y = Mouse.getY();
//
//			//System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
//
//			if(!previousMouseButton) {
//				previousMouseButton = true;
//
////				stateManager.setState("s2", true);
////				stateManager.setState("s3", true);
//			}
//		} else {
//			previousMouseButton = false;
//		}
//
//		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
//			System.out.println("SPACE KEY IS DOWN");
//		}
//
//		while (Keyboard.next()) {
//			if (Keyboard.getEventKeyState()) {
//				if (Keyboard.getEventKey() == Keyboard.KEY_A) {
//					System.out.println("A Key Pressed");
//
//				}
//				if (Keyboard.getEventKey() == Keyboard.KEY_S) {
//					System.out.println("S Key Pressed");
//				}
//				if (Keyboard.getEventKey() == Keyboard.KEY_D) {
//					System.out.println("D Key Pressed");
//				}
//			} else {
//				if (Keyboard.getEventKey() == Keyboard.KEY_A) {
//					System.out.println("A Key Released");
//				}
//				if (Keyboard.getEventKey() == Keyboard.KEY_S) {
//					System.out.println("S Key Released");
//				}
//				if (Keyboard.getEventKey() == Keyboard.KEY_D) {
//					System.out.println("D Key Released");
//				}
//				if (Keyboard.getEventKey() == Keyboard.KEY_X) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//}
