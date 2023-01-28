package com.jimholden.conomy.client.gui.engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.jimholden.conomy.client.gui.BankingGUI;
import com.jimholden.conomy.client.gui.networking.GUINetworkBank;
import com.jimholden.conomy.client.gui.networking.GUINetworkLedger;

public class GuiRegistry {
	
	public static HashMap<Integer, GUINetworkHandler> guis = new HashMap<>();
	
	static {
		register(0, new GUINetworkBank());
		register(1, new GUINetworkLedger());
	}

	public static GUINetworkHandler getGUI(int aID) {
		return guis.get(aID);
	}
	
	public static void register(int id, GUINetworkHandler g) {
		guis.put(id, g);
	}

}
