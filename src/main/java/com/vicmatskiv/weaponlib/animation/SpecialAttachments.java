package com.vicmatskiv.weaponlib.animation;


import com.vicmatskiv.weaponlib.AttachmentBuilder;
import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.compatibility.CompatibleFmlPreInitializationEvent;
import com.vicmatskiv.weaponlib.config.ConfigurationManager;
import com.vicmatskiv.weaponlib.model.Bullet556;

public class SpecialAttachments {
	public static ItemAttachment<Weapon> MagicMag;
	
	public static void init(Object mod, ConfigurationManager configurationManager, CompatibleFmlPreInitializationEvent event, ModContext context) {
		
		MagicMag = new AttachmentBuilder<Weapon>()
	            .withCategory(AttachmentCategory.MAGICMAG)
	            
	            // This model serves as a placeholder
	            .withModel(new Bullet556(), "tan.png")
	            
	            
	            .withName("magazine_extra")
	            .withRenderablePart()
	            .withModId("mw")
	            .withTextureName("Dummy.png").build(context);
		
		
		
	}

}
