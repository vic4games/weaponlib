package com.jimholden.conomy.main;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.jimholden.conomy.util.Reference;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class ConomyModContainer extends DummyModContainer {
	
	public ConomyModContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = Reference.MOD_ID;
        meta.name = Reference.NAME;
        meta.description = "conomy mod";
        meta.version = "1.12.2";
        meta.authorList = Arrays.asList("JimHolden");
    }
    
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
