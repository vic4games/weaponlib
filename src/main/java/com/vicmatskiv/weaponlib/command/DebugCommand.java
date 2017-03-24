package com.vicmatskiv.weaponlib.command;

import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class DebugCommand extends CommandBase {

    private static final String SHOW_OPTION_CODE = "code";
    private static final String COMMAND_DEBUG = "wdb";
    private static final String DEBUG_ARG_ON = "on";
    private static final String DEBUG_ARG_OFF = "off";
    private static final String DEBUG_ARG_PAUSE = "pause";
    private static final String DEBUG_ARG_PART = "part";
    private static final String DEBUG_ARG_SCALE = "scale";
    private static final String DEBUG_ARG_SHOW = "show";
    private static final String DEBUG_ARG_WATCH = "watch";

    @Override
    public String getCommandName() {
        return COMMAND_DEBUG;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + COMMAND_DEBUG + "<options>";
    }
    
    private String getSubCommandDebugUsage() {
        return "/" + COMMAND_DEBUG + " <" + DEBUG_ARG_ON + "|" + DEBUG_ARG_OFF + ">";
    }
    
    private String getSubCommandPauseUsage() {
        return String.format("/%s %s <transition-number> <pause-duration>", COMMAND_DEBUG, DEBUG_ARG_PAUSE);
    }
    
    private String getSubCommandPartUsage() {
        return String.format("/%s %s main|lhand|rhand", COMMAND_DEBUG, DEBUG_ARG_PART);
    }
    
    private String getSubCommandShowUsage() {
        return String.format("/%s %s code", COMMAND_DEBUG, DEBUG_ARG_SHOW);
    }
    
    private String getSubCommandScaleUsage() {
        return String.format("/%s %s <scale>", COMMAND_DEBUG, DEBUG_ARG_SCALE);
    }
    
    private String getSubCommandWatchUsage() {
        return String.format("/%s %s [entity-id]", COMMAND_DEBUG, DEBUG_ARG_WATCH);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            switch(args[0].toLowerCase()) {
            case DEBUG_ARG_ON:
                processDebugModeSubCommand(args);
                break;
            case DEBUG_ARG_OFF:
                processDebugModeSubCommand(args);
                break;
            case DEBUG_ARG_PAUSE:
                processPauseSubCommand(args);
                break;
            case DEBUG_ARG_PART:
                processPartSubCommand(args);
                break;
            case DEBUG_ARG_SHOW:
                processShowSubCommand(args);
                break;
            case DEBUG_ARG_SCALE:
                processScaleSubCommand(args);
                break;
            case DEBUG_ARG_WATCH:
                processWatchSubCommand(args);
                break;
            default:
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
        }
    }
    
    private void processDebugModeSubCommand(String[] args) {
        Boolean debugMode = null;
        switch(args[0].toLowerCase()) {
        case DEBUG_ARG_ON:
            debugMode = true;
            break;
        case DEBUG_ARG_OFF:
            debugMode = false;
            break;
        }
        if(debugMode != null) {
            DebugPositioner.setDebugMode(debugMode);
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Debug mode " + args[0].toLowerCase()));
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandDebugUsage()));
        }
    }
    
    private void processPauseSubCommand(String[] args) {
        if(args.length != 3) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandPauseUsage()));
            return;
        }
        
        try {
            int transitionNumber = Integer.parseInt(args[1]);
            long pauseDuration = Long.parseLong(args[2]);
            DebugPositioner.configureTransitionPause(transitionNumber, pauseDuration);
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Set transition "
                    + transitionNumber + " pause to " + pauseDuration + "ms"));
        } catch(NumberFormatException e) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandPauseUsage()));
        }
    }
    
    private void processWatchSubCommand(String[] args) {
        if(args.length < 1) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandWatchUsage()));
            return;
        }
        
//        try {
//            float scale = Float.parseFloat(args[1]);
//            DebugPositioner.setScale(scale);
//            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Set scale to " + scale));
//        } catch(NumberFormatException e) {
//            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandScaleUsage()));
//        }
        
        DebugPositioner.watch();
    }
    
    private void processScaleSubCommand(String[] args) {
        if(args.length != 2) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandScaleUsage()));
            return;
        }
        
        if(DebugPositioner.getDebugPart() == null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }
        
        try {
            float scale = Float.parseFloat(args[1]);
            DebugPositioner.setScale(scale);
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Set scale to " + scale));
        } catch(NumberFormatException e) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandScaleUsage()));
        }
    }
    
    private void processShowSubCommand(String[] args) {
        if(args.length != 2) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandPauseUsage()));
            return;
        }
        
        if(DebugPositioner.getDebugPart() == null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Debug part not selected"));
            return;
        }

        switch(args[1].toLowerCase()) {
        case SHOW_OPTION_CODE:
            DebugPositioner.showCode();
            break;
        default:
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandShowUsage()));
        }
    }
    
    private void processPartSubCommand(String[] args) {
        if(args.length != 2) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandPartUsage()));
            return;
        }
        
        try {
            switch(args[1].toLowerCase()) {
            case "main":
                DebugPositioner.setDebugPart(Part.MAIN_ITEM);
                break;
            case "lhand":
                DebugPositioner.setDebugPart(Part.LEFT_HAND);
                break;
            case "rhand":
                DebugPositioner.setDebugPart(Part.RIGHT_HAND);
                break;
            }
            
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Debugging part "
                    + args[1]));
        } catch(NumberFormatException e) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getSubCommandPartUsage()));
        }
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
