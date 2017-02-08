package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleTransformType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class RenderContext {

	private EntityPlayer player;
	private ItemStack weapon;
	private float limbSwing;
	private float flimbSwingAmount;
	private float ageInTicks;
	private float netHeadYaw;
	private float headPitch;
	private float scale;
	private float transitionProgress;
	private CompatibleTransformType compatibleTransformType;
	private RenderableState fromState;
	private ClientModContext clientModContext;

	public RenderContext(ClientModContext clientModContext, EntityPlayer player, ItemStack weapon) {
		this.clientModContext = clientModContext;
		this.player = player;
		this.weapon = weapon;
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
		this.weapon = weapon;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public ItemStack getWeapon() {
		return weapon;
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

	public float getTransitionProgress() {
		return transitionProgress;
	}

	public void setTransitionProgress(float transitionProgress) {
		this.transitionProgress = transitionProgress;
	}
}