package com.vicmatskiv.weaponlib.command;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.compatibility.CompatibleCommand;

import net.minecraft.command.ICommandSender;

public class DebugCommand extends CompatibleCommand {

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
    public String getName() {
        return COMMAND_DEBUG;
    }

    @Override
    public String getUsage(ICommandSender sender) {
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
    public void execCommand(ICommandSender sender, String[] args) {
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
                compatibility.addChatMessage(compatibility.clientPlayer(), getUsage(sender));
            }
        } else {
            compatibility.addChatMessage(compatibility.clientPlayer(), getUsage(sender));
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
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug mode " + args[0].toLowerCase());
        } else {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandDebugUsage());
        }
    }

    private void processPauseSubCommand(String[] args) {
        if(args.length != 3) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandPauseUsage());
            return;
        }

        try {
            int transitionNumber = Integer.parseInt(args[1]);
            long pauseDuration = Long.parseLong(args[2]);
            DebugPositioner.configureTransitionPause(transitionNumber, pauseDuration);
            compatibility.addChatMessage(compatibility.clientPlayer(), "Set transition "
                    + transitionNumber + " pause to " + pauseDuration + "ms");
        } catch(NumberFormatException e) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandPauseUsage());
        }
    }

    private void processWatchSubCommand(String[] args) {
        if(args.length < 1) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandWatchUsage());
            return;
        }

        DebugPositioner.watch();
    }

    private void processScaleSubCommand(String[] args) {
        if(args.length != 2) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandScaleUsage());
            return;
        }

        if(DebugPositioner.getDebugPart() == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }

        try {
            float scale = Float.parseFloat(args[1]);
            DebugPositioner.setScale(scale);
            compatibility.addChatMessage(compatibility.clientPlayer(), "Set scale to " + scale);
        } catch(NumberFormatException e) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandScaleUsage());
        }
    }

    private void processShowSubCommand(String[] args) {
        if(args.length != 2) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandPauseUsage());
            return;
        }

        if(DebugPositioner.getDebugPart() == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }

        switch(args[1].toLowerCase()) {
        case SHOW_OPTION_CODE:
            DebugPositioner.showCode();
            break;
        default:
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandShowUsage());
        }
    }

    private void processPartSubCommand(String[] args) {
        if(args.length != 2) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandPartUsage());
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

            compatibility.addChatMessage(compatibility.clientPlayer(), "Debugging part "
                    + args[1]);
        } catch(NumberFormatException e) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandPartUsage());
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
