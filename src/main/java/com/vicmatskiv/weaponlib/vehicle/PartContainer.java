package com.vicmatskiv.weaponlib.vehicle;

import java.util.List;

public interface PartContainer<Part> {

    List<Part> getChildParts();

}
