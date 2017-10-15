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
    }

    private static CompatibleClassInfoProvider instance = new CompatibleClassInfoProvider();

    public static CompatibleClassInfoProvider getInstance() {
        return instance;
    }

    public ClassInfo getClassInfo(String mcpClassName) {
        return classInfoMap.get(mcpClassName.replace('.', '/'));
    }

}
