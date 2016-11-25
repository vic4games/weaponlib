package com.vicmatskiv.weaponlib;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {

	public static KeyBinding reloadKey;
	public static KeyBinding attachmentKey;
	public static KeyBinding upArrowKey;
	public static KeyBinding downArrowKey;
	public static KeyBinding leftArrowKey;
	public static KeyBinding rightArrowKey;
	public static KeyBinding laserSwitchKey;

	public static void init() {
		
		reloadKey = new KeyBinding("key.reload", Keyboard.KEY_R,
				"key.categories.weaponlib");
		
		laserSwitchKey = new KeyBinding("key.laser", Keyboard.KEY_L,
				"key.categories.weaponlib");
		
		attachmentKey = new KeyBinding("key.attachment", Keyboard.KEY_F,
				"key.categories.weaponlib");
		
		upArrowKey = new KeyBinding("key.scope", Keyboard.KEY_UP,
				"key.categories.weaponlib");
		
		downArrowKey = new KeyBinding("key.recoil_fitter", Keyboard.KEY_DOWN,
				"key.categories.weaponlib");
		
		leftArrowKey = new KeyBinding("key.silencer", Keyboard.KEY_LEFT,
				"key.categories.weaponlib");
		
		rightArrowKey = new KeyBinding("key.texture_change", Keyboard.KEY_RIGHT,
				"key.categories.weaponlib");
		
		ClientRegistry.registerKeyBinding(reloadKey);
		ClientRegistry.registerKeyBinding(attachmentKey);
		ClientRegistry.registerKeyBinding(upArrowKey);
		ClientRegistry.registerKeyBinding(downArrowKey);
		ClientRegistry.registerKeyBinding(leftArrowKey);
		ClientRegistry.registerKeyBinding(rightArrowKey);
		ClientRegistry.registerKeyBinding(laserSwitchKey);
	}
}
