package com.vicmatskiv.weaponlib.config.novel;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.debug.SysOutController;
import com.vicmatskiv.weaponlib.jim.util.VMWHooksHandler;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.common.Loader;
import scala.Array;

public class ModernConfigManager {
	
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static HashMap<Field, Property> elementMappings = new HashMap<>();
	
	private static List<Field> MODERN_CONFIG_FIELDS = new ArrayList<Field>();
	
	public static final String CATEGORY_RENDERING = "rendering";
	public static final String CATEGORY_RENDERING_SCREENSHADERS = "rendering.screenshaders";
	public static final String CATEGORY_GAMEPLAY = "gameplay";
	
	
	private static ArrayList<String> categories = new ArrayList<>();
	
	
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Setting this to false disables all shaders, enabling allows to select which shaders are used.")
	public static boolean enableAllShaders = true;

	@ConfigSync(category = CATEGORY_RENDERING, comment = "Setting this to false disables all world shaders, enabling allows to select which world shaders are used.")
	public static boolean enableWorldShaders = true;
	
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Setting this to false disables all screen shaders, enabling allows to select which screen shaders are used.")
	public static boolean enableScreenShaders = true;
	
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Enables gun shaders, so skinning & lighting")
	public static boolean enableGunShaders = true;
	
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Enables scope effects")
	public static boolean enableScopeEffects = true;
	
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Enables flash shaders")
	public static boolean enableFlashShaders = true;
	
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Enables reticle shaders")
	public static boolean enableReticleShaders = true;
	
	
	@ConfigSync(category = CATEGORY_RENDERING_SCREENSHADERS, comment = "Enables film grain effect")
	public static boolean filmGrain = true;
	
	@RangeDouble(min = 0.0, max = 1.0)
	@ConfigSync(category = CATEGORY_RENDERING_SCREENSHADERS, comment = "Configures the intensity of the film grain effect")
	public static double filmGrainIntensity = 0.025;
	
	@ConfigSync(category = CATEGORY_RENDERING_SCREENSHADERS, comment = "Enables glow around bright objects (bloom)")
	public static boolean bloomEffect = true;
	
	@RequiresMcRestart
	@RangeInt(min = 2, max = 8)
	@ConfigSync(category = CATEGORY_RENDERING_SCREENSHADERS, comment = "Lower numbers = better performance")
	public static int bloomLayers = 3;
	
	@ConfigSync(category = CATEGORY_RENDERING_SCREENSHADERS, comment = "Enable on-screen rain/snow VFX")
	public static boolean onScreenRainAndSnow = true;
	
	@RequiresMcRestart
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Enables the HDR framebuffer, requires restart. \nThe HDR is the cause of a lot of shader incompat, \nbut Bloom will look weird without it")
	public static boolean enableHDRFramebuffer = true;
	
	@ConfigSync(category = CATEGORY_RENDERING, comment = "Enables the fancy VMW snow/rain")
	public static boolean enableFancyRainAndSnow = true;
	
	
	@ConfigSync(category = CATEGORY_GAMEPLAY, comment = "Enables the ammo counter")
	public static boolean enableAmmoCounter = true;
	
	@ConfigSync(category = CATEGORY_GAMEPLAY, comment = "Enable open door key display when hovering doors")
	public static boolean enableOpenDoorDisplay = true;
	
	@ConfigSync(category = CATEGORY_GAMEPLAY, comment = "If true, hold to aim. If false, toggle to aim.")
	public static boolean holdToAim = true;
	
	@ConfigSync(category = CATEGORY_GAMEPLAY, comment = "Enables the black background on the ammo counter.")
	public static boolean enableAmmoCounterBackground = true;
	
	//@ConfigSync(category = CATEGORY_RENDERING, comment = "Turns on the custom render for third person, may improve compat.")
	//public static boolean enableThirdPersonAnimations = true;


	
	
	
	
	
	
	
	
	private static Configuration config = null;
	

	
	private static boolean isLoaded = false;
	
	static {
		
		
		
		// Register all categories
		
	}
	
	public static void updateField(Field f, Object value) {
		
		// Try to set field
		try {
			// Instance is 'null' because all of these
			// fields are static.
			f.set(null, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.error("Could not set field for field name: {}, please report this to developers.", f.getName());
			e.printStackTrace();
		}
		
		
		if(f.getType() == int.class) {
			elementMappings.get(f).set((int) value);
		} else if(f.getType() == int[].class) {
			elementMappings.get(f).set((int[]) value);
		} else if(f.getType() == boolean.class) {
		 	elementMappings.get(f).set((boolean) value);
		} else if(f.getType() == boolean[].class) {
			elementMappings.get(f).set((boolean[]) value);
		} else if(f.getType() == String.class) {
			elementMappings.get(f).set((String) value);
		} else if(f.getType() == String[].class) {
			elementMappings.get(f).set((String[]) value);
		} else if(f.getType() == double.class) {
			elementMappings.get(f).set((double) value);
		} else if(f.getType() == double[].class) {
			elementMappings.get(f).set((double[]) value);
		}else if(f.getType() == long.class) {
			elementMappings.get(f).set((long) value);
		}
 		
		
	}
	
	
	
	private static void registerProperty(Field f, ConfigSync annotation) {
		
		Property property = null;
		
	
		
		try {
			if(f.getType() == int.class) {
				
				boolean isRanged = f.isAnnotationPresent(RangeInt.class);
				if(!isRanged) {
					property = config.get(annotation.category(), f.getName(), f.getInt(null), annotation.comment());
					f.set(null, property.getInt());
				} else {
					RangeInt rangedAnnotation = f.getAnnotation(RangeInt.class);
					property = config.get(annotation.category(), f.getName(), f.getInt(null), annotation.comment(), rangedAnnotation.min(), rangedAnnotation.max());
					f.set(null, property.getInt());
				}
				
				
			} else if(f.getType() == boolean.class) {
				property = config.get(annotation.category(), f.getName(), f.getBoolean(null), annotation.comment());
				f.set(null, property.getBoolean());
			} else if(f.getType() == String.class) {
				property = config.get(annotation.category(), f.getName(), (String) f.get(null), annotation.comment());
				f.set(null, property.getString());
			} else if(f.getType() == double.class) {
				
				boolean isRanged = f.isAnnotationPresent(RangeDouble.class);
				if(!isRanged) {
					property = config.get(annotation.category(), f.getName(), f.getDouble(null), annotation.comment());
					f.set(null, property.getDouble());
				} else {
					RangeDouble range = f.getAnnotation(RangeDouble.class);
					property = config.get(annotation.category(), f.getName(), f.getDouble(null), annotation.comment(), range.min(), range.max());
					f.set(null, property.getDouble());
				}
				
				
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(f.isAnnotationPresent(RequiresMcRestart.class)) {
			property.setRequiresMcRestart(true);
		}
		
		
		// Put property into the map
		elementMappings.put(f, property);
	}
	
	public static void saveConfig() {
		config.save();
	}
	
	
	public static void init() {
		
	
		
		// Return if already loaded
		if(isLoaded) {
			return;
		} else isLoaded = true;
		
		
		config = new Configuration(new File(Loader.instance().getConfigDir(), "mw.cfg"));
		
		// Initialize this class' fields
		for(Field f : ModernConfigManager.class.getFields()) {
			ConfigSync annotation = f.getAnnotation(ConfigSync.class);
			if(annotation == null) continue;
			MODERN_CONFIG_FIELDS.add(f);
			
			registerProperty(f, annotation);
			
			
			
			if(!VMWHooksHandler.isOnServer()) {
				// Submits field to be organized within the tree
				VMWModConfigGUI.submitField(annotation, f);
			}
			

		}
		
		saveConfig();
		
	}
	


	

}
