package com.vicmatskiv.weaponlib;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindings {

	public static KeyBinding reloadKey;
	public static KeyBinding attachmentKey;
	public static KeyBinding upArrowKey;
	public static KeyBinding downArrowKey;
	public static KeyBinding leftArrowKey;
	public static KeyBinding rightArrowKey;
	public static KeyBinding laserSwitchKey;
	
	public static KeyBinding addKey;
	
	public static KeyBinding subtractKey;

	public static void init() {
		
		reloadKey = new KeyBinding("key.reload", Keyboard.KEY_R,
				"key.categories.weaponlib");
		
		laserSwitchKey = new KeyBinding("key.laser", Keyboard.KEY_L,
				"key.categories.weaponlib");
		
		attachmentKey = new KeyBinding("key.attachment", Keyboard.KEY_M,
				"key.categories.weaponlib");
		
		upArrowKey = new KeyBinding("key.scope", Keyboard.KEY_UP,
				"key.categories.weaponlib");
		
		downArrowKey = new KeyBinding("key.recoil_fitter", Keyboard.KEY_DOWN,
				"key.categories.weaponlib");
		
		leftArrowKey = new KeyBinding("key.silencer", Keyboard.KEY_LEFT,
				"key.categories.weaponlib");
		
		rightArrowKey = new KeyBinding("key.texture_change", Keyboard.KEY_RIGHT,
				"key.categories.weaponlib");
		
		addKey = new KeyBinding("key.add", Keyboard.KEY_ADD,
				"key.categories.weaponlib");
		
		subtractKey = new KeyBinding("key.add", Keyboard.KEY_SUBTRACT,
				"key.categories.weaponlib");
		
		compatibility.registerKeyBinding(reloadKey);
		compatibility.registerKeyBinding(attachmentKey);
		compatibility.registerKeyBinding(upArrowKey);
		compatibility.registerKeyBinding(downArrowKey);
		compatibility.registerKeyBinding(leftArrowKey);
		compatibility.registerKeyBinding(rightArrowKey);
		compatibility.registerKeyBinding(laserSwitchKey);
		compatibility.registerKeyBinding(addKey);
		compatibility.registerKeyBinding(subtractKey);
	}
}
