package com.vicmatskiv.weaponlib.animation;

import java.util.List;

public interface TransitionProvider<State> {

	public List<Transition> getPositioning(State state);
}
