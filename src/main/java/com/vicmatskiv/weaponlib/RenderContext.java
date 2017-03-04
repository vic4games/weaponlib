package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class RenderContext {

	private EntityPlayer player;
	private ItemStack itemStack;
	private float limbSwing;
	private float flimbSwingAmount;
	private float ageInTicks;
	private float netHeadYaw;
	private float headPitch;
	private float scale;
	private float transitionProgress;
	private CompatibleTransformType compatibleTransformType;
	private RenderableState fromState;
	private RenderableState toState;
	private ClientModContext clientModContext;
	private PlayerItemInstance<?> playerItemInstance;

	public RenderContext(ClientModContext clientModContext, EntityPlayer player, ItemStack itemStack) {
		this.clientModContext = clientModContext;
		this.player = player;
		this.itemStack = itemStack;
	}

	public ClientModContext getClientModContext() {
		return clientModContext;
	}

	public float getLimbSwing() {
		return limbSwing;
	}

	public void setLimbSwing(float limbSwing) {
		this.limbSwing = limbSwing;
	}

	public float getFlimbSwingAmount() {
		return flimbSwingAmount;
	}

	public void setFlimbSwingAmount(float flimbSwingAmount) {
		this.flimbSwingAmount = flimbSwingAmount;
	}

	public float getAgeInTicks() {
		return ageInTicks;
	}

	public void setAgeInTicks(float ageInTicks) {
		this.ageInTicks = ageInTicks;
	}

	public float getNetHeadYaw() {
		return netHeadYaw;
	}

	public void setNetHeadYaw(float netHeadYaw) {
		this.netHeadYaw = netHeadYaw;
	}

	public float getHeadPitch() {
		return headPitch;
	}

	public void setHeadPitch(float headPitch) {
		this.headPitch = headPitch;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	public void setWeapon(ItemStack weapon) {
		this.itemStack = weapon;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public ItemStack getWeapon() {
		return itemStack;
	}

	public CompatibleTransformType getCompatibleTransformType() {
		return compatibleTransformType;
	}

	public void setCompatibleTransformType(CompatibleTransformType compatibleTransformType) {
		this.compatibleTransformType = compatibleTransformType;
	}

	public RenderableState getFromState() {
		return fromState;
	}

	public void setFromState(RenderableState fromState) {
		this.fromState = fromState;
	}

	public RenderableState getToState() {
		return toState;
	}

	public void setToState(RenderableState toState) {
		this.toState = toState;
	}

	public float getTransitionProgress() {
		return transitionProgress;
	}

	public void setTransitionProgress(float transitionProgress) {
		this.transitionProgress = transitionProgress;
	}

	public PlayerItemInstance<?> getPlayerItemInstance() {
		return playerItemInstance;
	}

	public void setPlayerItemInstance(PlayerItemInstance<?> playerItemInstance) {
		this.playerItemInstance = playerItemInstance;
	}
	
	public PlayerWeaponInstance getWeaponInstance() {
		if(playerItemInstance instanceof PlayerWeaponInstance) {
			return (PlayerWeaponInstance) playerItemInstance;
		}
		PlayerWeaponInstance itemInstance = (PlayerWeaponInstance) clientModContext.getPlayerItemInstanceRegistry()
				.getItemInstance(player, itemStack);
		if(itemInstance instanceof PlayerWeaponInstance) {
			return (PlayerWeaponInstance) itemInstance;
		}
		return null;
	}
}