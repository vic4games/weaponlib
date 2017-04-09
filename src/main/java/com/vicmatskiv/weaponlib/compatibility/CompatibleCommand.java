package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class CompatibleCommand extends CommandBase {

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        execCommand(sender, args);
<<<<<<< HEAD
=======

>>>>>>> 152023007a3d5249eeb06ad133ca373d5ae9a05e
    }

    protected abstract void execCommand(ICommandSender sender, String[] args);

    @Override
<<<<<<< HEAD
    public String getName() {
        return getCompatibleName();
    }

    public abstract String getCompatibleName();

    @Override
    public String getUsage(ICommandSender sender) {
        return getCompatibleUsage(sender);
    }

    public abstract String getCompatibleUsage(ICommandSender sender);
=======
    public String getCommandName() {
        return getCompatibleName();
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getCompatibleUsage(sender);
    }

    protected abstract String getCompatibleName();

    protected abstract String getCompatibleUsage(ICommandSender sender);
>>>>>>> 152023007a3d5249eeb06ad133ca373d5ae9a05e
}
