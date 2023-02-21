package com.vicmatskiv.weaponlib.config.novel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.config.ModernConfigurationManager;
import com.vicmatskiv.weaponlib.config.novel.HierarchialTree.Branch;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import scala.actors.threadpool.Arrays;

public class VMWModConfigGUI extends GuiConfig {
	
	private static HierarchialTree<IConfigElement> configTree = new HierarchialTree<IConfigElement>();
	private static List<IConfigElement> cachedList = new ArrayList<>();
	private static boolean shouldUpdateCache = true;
	
	public VMWModConfigGUI(GuiScreen parentScreen) {
		super(parentScreen,
				getElements(),
				"mw",
				false,
				false,
				"Finally... a config!");
		
	}
	
	
	public static ConfigGuiType getGUITypeFromFieldType(Field f) {
		if(f.getType() == int.class) {
			return ConfigGuiType.INTEGER;
		} else if(f.getType() == boolean.class) {
			return ConfigGuiType.BOOLEAN;
		} else if(f.getType() == double.class) {
			return ConfigGuiType.DOUBLE;
		} else if(f.getType() == String.class) {
			return ConfigGuiType.STRING;
		} 
		return null;
	}
	
	protected static void submitField(ConfigSync annotation, Field f) {
		
		
		String propertyTag = "config." + annotation.category() + "." + f.getName() + ".property";
		
		try {
			
			SynchronizedConfigElement element = null;
			
			if(f.getType() == int.class) {
			
				if(f.isAnnotationPresent(RangeInt.class)) {
					// Is ranged
					RangeInt range = f.getAnnotation(RangeInt.class);
					element = new SynchronizedConfigElement(f, f.getName(), propertyTag + ".name", f.get(null), getGUITypeFromFieldType(f), propertyTag, range.min(), range.max());
					
				}
				
			} else if(f.getType() == double.class) {
				
				if(f.isAnnotationPresent(RangeDouble.class)) {
					// Is ranged
					RangeDouble range = f.getAnnotation(RangeDouble.class);
					element = new SynchronizedConfigElement(f, f.getName(), propertyTag + ".name", f.get(null), getGUITypeFromFieldType(f), propertyTag, range.min(), range.max());
				}
			}
			
			// If no element yet, make an element w/ the standard method
			if(element == null) {
				element = new SynchronizedConfigElement(f, f.getName(), propertyTag + ".name", f.get(null), getGUITypeFromFieldType(f), propertyTag);
			}
			
			if(f.isAnnotationPresent(RequiresMcRestart.class)) {
				element.setRequiresMcRestart(true);
			}
			
			// Insert element into tree
			configTree.addNode(annotation.category(), element);
		
			
		} catch(IllegalArgumentException e) {
			
		} catch(IllegalAccessException e) {
			
		}
		
		
	}
	


	
	private static void recursiveWalk(Branch<IConfigElement> branch, ModernConfigCategory element) {
		//System.out.println(branch.getKey());
		
		String[] splitted = branch.getPathway().split("\\.");
		String name = splitted[splitted.length-1];
		
		if(!element.getCategoryName().equals(name)) {
			ModernConfigCategory newElement = new ModernConfigCategory(name, "config.category." + name, new ArrayList<>());
			element.getChildElements().add(newElement);
			element = newElement;
		}
		
		
		Iterator<IConfigElement> nodeIterator = branch.getNodeIterator();
		while(nodeIterator.hasNext()) {
			element.getChildElements().add(nodeIterator.next());
		}
		
		for(Entry<String, Branch<IConfigElement>> entry : branch.getSubBranches().entrySet()) {
			// Process all sub-branches
			recursiveWalk(entry.getValue(), element);
		}
	}
	
	
	public static List<IConfigElement> getElements() {

		// FOR DEBUG
		ModernConfigManager.init();
		
		//shouldUpdateCache = true;
		if(shouldUpdateCache) {
			// Set up caching
			cachedList.clear();
			shouldUpdateCache = false;
			
			Iterator<Branch<IConfigElement>> itr = configTree.getRootIterator();
			while(itr.hasNext()) {
				
				// Get current root
				Branch<IConfigElement> root = itr.next();
				
			
				// Add the root category
				ModernConfigCategory category = new ModernConfigCategory(root.getKey(), "config.category." + root.getKey(), new ArrayList<>());
				cachedList.add(category);
				
				
				// Begin recursive walk
				recursiveWalk(root, category);
			}
			
		}
		
		
		

		return cachedList;
	}
	
	@Override
	public void onGuiClosed() {
		ModernConfigManager.saveConfig();
		super.onGuiClosed();
	}
	
	@Override
	public void initGui() {
		// TODO Auto-generated method stub
		super.initGui();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
	
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	
	@Override
	protected void actionPerformed(GuiButton button) {
		// TODO Auto-generated method stub
		super.actionPerformed(button);
	}

}
