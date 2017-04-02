package com.vicmatskiv.weaponlib.compatibility;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public abstract class CompatibleCommand extends CommandBase {
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        execCommand(sender, args);
        
    }
    
    protected abstract void execCommand(ICommandSender sender, String[] args);
}
