package com.vicmatskiv.weaponlib;

import com.vicmatskiv.weaponlib.compatibility.CompatibleChannel;
import com.vicmatskiv.weaponlib.compatibility.CompatibleSound;
import com.vicmatskiv.weaponlib.crafting.RecipeGenerator;
import com.vicmatskiv.weaponlib.grenade.GrenadeAttackAspect;
import com.vicmatskiv.weaponlib.grenade.GrenadeRenderer;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;
import com.vicmatskiv.weaponlib.melee.ItemMelee;
import com.vicmatskiv.weaponlib.melee.MeleeAttachmentAspect;
import com.vicmatskiv.weaponlib.melee.MeleeAttackAspect;
import com.vicmatskiv.weaponlib.melee.MeleeRenderer;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface ModContext {

	public void init(Object mod, String modId, CompatibleChannel channel);

	public void registerWeapon(String name, Weapon weapon, WeaponRenderer renderer);

	public CompatibleChannel getChannel();

	public void runSyncTick(Runnable runnable);

	public void registerRenderableItem(String name, Item weapon, Object renderer);

	//TODO: append mod id in 1.7.10
	public CompatibleSound registerSound(String sound);

	public void runInMainThread(Runnable runnable);

	public PlayerItemInstanceRegistry getPlayerItemInstanceRegistry();

	public WeaponReloadAspect getWeaponReloadAspect();

	public WeaponFireAspect getWeaponFireAspect();

	public WeaponAttachmentAspect getAttachmentAspect();

	public MagazineReloadAspect getMagazineReloadAspect();

	public PlayerWeaponInstance getMainHeldWeapon();

	public StatusMessageCenter getStatusMessageCenter();

	public RecipeGenerator getRecipeGenerator();

	public CompatibleSound getZoomSound();

	public void setChangeZoomSound(String sound);

	public CompatibleSound getChangeFireModeSound();

	public void setChangeFireModeSound(String sound);

	public CompatibleSound getNoAmmoSound();

	public void setNoAmmoSound(String sound);

	public CompatibleSound getExplosionSound();

	public void setExplosionSound(String sound);

    public void registerMeleeWeapon(String name, ItemMelee itemMelee, MeleeRenderer renderer);

    public void registerGrenadeWeapon(String name, ItemGrenade itemGrenade, GrenadeRenderer renderer);

    public MeleeAttackAspect getMeleeAttackAspect();

    public MeleeAttachmentAspect getMeleeAttachmentAspect();

    public AttachmentContainer getGrenadeAttachmentAspect();

    public ResourceLocation getNamedResource(String name);

    public float getAspectRatio();

    public GrenadeAttackAspect getGrenadeAttackAspect();

    public String getModId();

    public EffectManager getEffectManager();
}
