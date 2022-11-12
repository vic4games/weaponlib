package com.vicmatskiv.weaponlib.command;

import com.vicmatskiv.weaponlib.CommonModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class WeaponDebugCommand extends TidyCompatibleCommand {

	private static final String NAME = "name";

	public WeaponDebugCommand() {
		super("wdc", "Weapon Debug Command");

		addMainOption(NAME, "Gets the name of the weapon");
	}

	@Override
	protected void executeTidyCommand(ICommandSender sender, String mainArgument, String secondArgument,
			String[] args) {
		if (mainArgument.equals(NAME)) {

			if (sender instanceof EntityPlayer && CommonModContext.getContext() != null) {
				PlayerWeaponInstance pwi = CommonModContext.getContext().getMainHeldWeapon();
				if(pwi == null) return;
 				sendFormattedMessage(sender, "The weapon name is: " + getSecondaryColor() + pwi.getWeapon().getName());

			}

		}
	}

}
