package com.vicmatskiv.weaponlib.compatibility;

import java.util.HashMap;
import java.util.Map;

import com.vicmatskiv.weaponlib.ClassInfo;

public class CompatibleClassInfoProvider {

    private static Map<String, ClassInfo> classInfoMap = new HashMap<>();

    static {
        classInfoMap.put("net/minecraft/client/renderer/EntityRenderer",
                new ClassInfo("net/minecraft/client/renderer/EntityRenderer", "buq")
                .addMethodInfo("hurtCameraEffect", "(F)V", "d")
                .addMethodInfo("setupCameraTransform", "(FI)V", "a")
                .addMethodInfo("setupViewBobbing", "applyBobbing", "(F)V", "e")
                );
                
        classInfoMap.put("net/minecraft/client/model/ModelBiped", 
                new ClassInfo("net/minecraft/client/model/ModelBiped", "bpx")
                .addMethodInfo2("render", "(Lnet/minecraft/entity/Entity;FFFFFF)V", "a", "(Lvg;FFFFFF)V")
                .addMethodInfo2("postRenderArm", "(FLnet/minecraft/util/EnumHandSide;)V", "a", "(FLvo;)V")
                );
        
        classInfoMap.put("net/minecraft/client/model/ModelPlayer", 
                new ClassInfo("net/minecraft/client/model/ModelPlayer", "bqj")
                .addMethodInfo2("render", "(Lnet/minecraft/entity/Entity;FFFFFF)V", "a", "(Lvg;FFFFFF)V")
                );
        
        classInfoMap.put("net/minecraft/client/renderer/entity/RenderLivingBase", 
                new ClassInfo("net/minecraft/client/renderer/entity/RenderLivingBase", "caa")
                .addMethodInfo2("renderModel", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V", "a", "(Lvp;FFFFFF)V")
                .addMethodInfo2("getMainModel", "()Lnet/minecraft/client/model/ModelBase;", "b", "()Lbqf;")
                );
        
        classInfoMap.put("net/minecraft/client/model/ModelBase", 
                new ClassInfo("net/minecraft/client/model/ModelBase", "bqf")
                .addMethodInfo2("render", "(Lnet/minecraft/entity/Entity;FFFFFF)V", "a", "(Lvg;FFFFFF)V")
                );
        
        classInfoMap.put("net/minecraft/client/renderer/entity/layers/LayerArmorBase", 
                new ClassInfo("net/minecraft/client/renderer/entity/layers/LayerArmorBase", "cbp")
                .addMethodInfo2("renderArmorLayer", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V", "a", "(Lvp;FFFFFFFLvl;)V")
                );
        
        classInfoMap.put("net/minecraft/client/renderer/entity/layers/LayerHeldItem", 
                new ClassInfo("net/minecraft/client/renderer/entity/layers/LayerHeldItem", "ccc")
                .addMethodInfo2("renderHeldItem", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Lnet/minecraft/util/EnumHandSide;)V", "a", "(Lvp;Laip;Lbwc$b;Lvo;)V")
                .addMethodInfo2("translateToHand", "(Lnet/minecraft/util/EnumHandSide;)V", "a", "(Lvo;)V")
                );
        
        classInfoMap.put("net/minecraft/client/entity/EntityPlayerSP", 
                new ClassInfo("net/minecraft/client/entity/EntityPlayerSP", "bnn")
                .addMethodInfo2("isSneaking", "()Z", "aM", "()Z")
                .addMethodInfo2("updateEntityActionState", "()V", "cr", "()V")
                );
    }

    private static CompatibleClassInfoProvider instance = new CompatibleClassInfoProvider();

    public static CompatibleClassInfoProvider getInstance() {
        return instance;
    }

    public ClassInfo getClassInfo(String mcpClassName) {
        return classInfoMap.get(mcpClassName.replace('.', '/'));
    }

}