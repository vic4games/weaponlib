package com.vicmatskiv.weaponlib;

import java.util.function.Function;

public interface ExposureProtection {

    public Function<Float, Float> getAbsorbFunction(Spreadable spreadable);
}
