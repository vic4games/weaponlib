package com.vicmatskiv.weaponlib.compatibility;

import java.io.IOException;
import java.util.Set;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;

public abstract class CompatibleResourcePack implements IResourcePack {

	@Override
	public final Set<?> getResourceDomains() {
		return getCompatibleResourceDomains();
	}

	protected abstract Set<String> getCompatibleResourceDomains();
	
	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
		return null;
	}
}
