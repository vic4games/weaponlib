package com.vicmatskiv.weaponlib.animation;

import org.lwjgl.util.vector.Matrix4f;

public interface PartPositionProvider {

    public Matrix4f getPartPosition(Object part);
}
