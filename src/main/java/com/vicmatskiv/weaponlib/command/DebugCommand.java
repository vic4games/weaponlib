package com.vicmatskiv.weaponlib.command;

import static com.vicmatskiv.weaponlib.compatibility.CompatibilityProvider.compatibility;

import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.animation.DebugPositioner;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClientEventHandler;
import com.vicmatskiv.weaponlib.compatibility.CompatibleCommand;
import com.vicmatskiv.weaponlib.vehicle.VehiclePart;

import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;

public class DebugCommand extends CompatibleCommand {

    private static final String SHOW_OPTION_CODE = "code";
    private static final String COMMAND_DEBUG = "wdb";
    private static final String DEBUG_ARG_ON = "on";
    private static final String DEBUG_ARG_OFF = "off";
    private static final String DEBUG_ARG_PAUSE = "pause";
    private static final String DEBUG_ARG_PART = "part";
    private static final String DEBUG_ARG_VPART = "vpart";
    private static final String DEBUG_ARG_SCALE = "scale";
    private static final String DEBUG_ARG_SHOW = "show";
    private static final String DEBUG_ARG_WATCH = "watch";
    private static final String DEBUG_ARG_STEP = "step";
    private static final String DEBUG_ARG_AUTOROTATE = "ar";
    
    private static final String DEBUG_FREECAM = "freecam";
    private static final String DEBUG_MUZZLE_POS = "muzzle";

    private String modId;

    public DebugCommand(String modId) {
        this.modId = modId;
    }

    @Override
    public String getCompatibleName() {
        return COMMAND_DEBUG;
    }
    
    public String getDebugPrefix() {
    	return TextFormatting.RED + "" + TextFormatting.RED + "(" + TextFormatting.DARK_GRAY + "MW" + TextFormatting.RED + ") ";
    }
    
    public String getDefaultPrefix() {
    	return TextFormatting.BOLD + "" + TextFormatting.GOLD + "(" + TextFormatting.DARK_GRAY + "MW" + TextFormatting.GOLD + ") ";
    }

    @Override
    public String getCompatibleUsage(ICommandSender sender) {
        return getDebugPrefix() + "/" + COMMAND_DEBUG + " <options>";
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
    
    private String getSubCommandVPartUsage() {
        return String.format("/%s %s main|lhand|rhand|swheel", COMMAND_DEBUG, DEBUG_ARG_VPART);
    }

    private String getSubCommandShowUsage() {
        return String.format("/%s %s code", COMMAND_DEBUG, DEBUG_ARG_SHOW);
    }

    private String getSubCommandScaleUsage() {
        return String.format("/%s %s <scale>", COMMAND_DEBUG, DEBUG_ARG_SCALE);
    }

    private String getSubCommandStepUsage() {
        return String.format("/%s %s <step>", COMMAND_DEBUG, DEBUG_ARG_STEP);
    }

    private String getSubCommandWatchUsage() {
        return String.format("/%s %s [entity-id]", COMMAND_DEBUG, DEBUG_ARG_WATCH);
    }
    
    private String getSubCommandAutorotateUsage() {
        return String.format("/%s %s <rpm> [x|y|z]", COMMAND_DEBUG, DEBUG_ARG_AUTOROTATE);
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
                processWeaponPartSubCommand(args);
                break;
            case DEBUG_ARG_VPART:
                processVehiclePartSubCommand(args);
                break;
            case DEBUG_ARG_SHOW:
                processShowSubCommand(args);
                break;
            case DEBUG_ARG_SCALE:
                processScaleSubCommand(args);
                break;
            case DEBUG_ARG_STEP:
                processStepSubCommand(args);
                break;
            case DEBUG_ARG_WATCH:
                processWatchSubCommand(args);
                break;
            case DEBUG_ARG_AUTOROTATE:
                processAutorotateSubCommand(args);
                break;
            case DEBUG_FREECAM:
            	processFreecamAndMuzzleSubCommands(args);
            	break;
            case DEBUG_MUZZLE_POS:
            	processFreecamAndMuzzleSubCommands(args);
            	break;
             
            default:
                compatibility.addChatMessage(compatibility.clientPlayer(), getCompatibleUsage(sender));
            }
        } else {
            compatibility.addChatMessage(compatibility.clientPlayer(), getCompatibleUsage(sender));
        }
    }
    
    private void processFreecamAndMuzzleSubCommands(String[] args) {
    	switch(args[0].toLowerCase()) {
    	case DEBUG_FREECAM:
    		if(CompatibleClientEventHandler.freecamEnabled) {
    			CompatibleClientEventHandler.freecamEnabled = false;
    			compatibility.addChatMessage(compatibility.clientPlayer(), getDebugPrefix() + "Freecam disabled");
     	       
    		} else {
    			CompatibleClientEventHandler.freecamEnabled = true;
    			compatibility.addChatMessage(compatibility.clientPlayer(), getDebugPrefix() + "Freecam enabled");
     	       
    		}
    		 
    		break;
    	case DEBUG_MUZZLE_POS:
    		 
    		if(CompatibleClientEventHandler.muzzlePositioner) {
    			compatibility.addChatMessage(compatibility.clientPlayer(), getDebugPrefix() + "Exiting muzzle debug...");
    			CompatibleClientEventHandler.muzzlePositioner = false;
      	      
    		} else {
    			compatibility.addChatMessage(compatibility.clientPlayer(), getDebugPrefix() + "Entering muzzle debug... a point will display.");
      	      	CompatibleClientEventHandler.muzzlePositioner = true;
    		}
    		
    		 break;
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
            compatibility.addChatMessage(compatibility.clientPlayer(), getDebugPrefix() + "Debug mode " + args[0].toLowerCase());
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
    
    private void processAutorotateSubCommand(String[] args) {
        if(args.length < 2) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandAutorotateUsage());
            return;
        }

        if(DebugPositioner.getDebugPart() == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }

        try {
            float rpm = Float.parseFloat(args[1]);
            if(rpm < 0) {
                compatibility.addChatMessage(compatibility.clientPlayer(), "RPM must be greater than 0");
                return;
            }
            float xrpm = 0f;
            float yrpm = 0f;
            float zrpm = 0f;
            if(args.length >= 3) {
                switch(args[2].trim().toLowerCase()) {
                case "y":
                    yrpm = rpm;
                    break;
                case "z":
                    zrpm = rpm;
                    break;
                case "x": default:
                    xrpm = rpm;
                    break;  
                }
            } else {
                xrpm = rpm;
            }
            DebugPositioner.setAutorotate(xrpm, yrpm, zrpm);
            compatibility.addChatMessage(compatibility.clientPlayer(), "Set autorotate to " 
                    + xrpm +", " + yrpm + ", " + zrpm);
        } catch(NumberFormatException e) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandAutorotateUsage());
        }
    }

    private void processStepSubCommand(String[] args) {
        if(args.length != 2) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandStepUsage());
            return;
        }

        if(DebugPositioner.getDebugPart() == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }

        try {
            float step = Float.parseFloat(args[1]);
            DebugPositioner.setStep(step);
            compatibility.addChatMessage(compatibility.clientPlayer(), "Set step to " + step);
        } catch(NumberFormatException e) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandStepUsage());
        }
    }

    private void processShowSubCommand(String[] args) {
        if(args.length != 2) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandShowUsage());
            return;
        }
        
        
        if(CompatibleClientEventHandler.muzzlePositioner) {
        	compatibility.addChatMessage(compatibility.clientPlayer(), getDebugPrefix() + "Muzzle Position: " + CompatibleClientEventHandler.debugmuzzlePosition);
            return;
        }
        if(DebugPositioner.getDebugPart() == null) {
            compatibility.addChatMessage(compatibility.clientPlayer(), "Debug part not selected");
            return;
        }

        switch(args[1].toLowerCase()) {
        case SHOW_OPTION_CODE:
            DebugPositioner.showCode();
            compatibility.addChatMessage(compatibility.clientPlayer(), "Code is copied to the console");
            break;
        default:
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandShowUsage());
        }
    }

    private void processWeaponPartSubCommand(String[] args) {
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
            case "inventory":
                DebugPositioner.setDebugPart(Part.INVENTORY);
                break;
            default:
                String partName = args[1];

                Item item = compatibility.findItemByName(modId, partName);
                Part part = null;
                if(item instanceof Part) {
                    part = (Part) item;
                } else if(item instanceof ItemAttachment) {
                    part = ((ItemAttachment<?>)item).getRenderablePart();
                }
                if(part != null) {
                    DebugPositioner.setDebugPart(part);
                }
                break;
            }

            compatibility.addChatMessage(compatibility.clientPlayer(), "Debugging part " + args[1]);
        } catch(NumberFormatException e) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandPartUsage());
        }
    }
    
    private void processVehiclePartSubCommand(String[] args) {
        if(args.length != 2) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandVPartUsage());
            return;
        }

        try {
            switch(args[1].toLowerCase()) {
            case "main":
                DebugPositioner.setDebugPart(VehiclePart.MAIN);
                break;
            case "lhand":
                DebugPositioner.setDebugPart(VehiclePart.LEFT_HAND);
                break;
            case "rhand":
                DebugPositioner.setDebugPart(VehiclePart.RIGHT_HAND);
                break;
            case "swheel":
                DebugPositioner.setDebugPart(VehiclePart.STEERING_WHEEL);
                break;
            case "flarm":
                DebugPositioner.setDebugPart(VehiclePart.FRONT_LEFT_CONTROL_ARM);
                break;
            case "frarm":
                DebugPositioner.setDebugPart(VehiclePart.FRONT_RIGHT_CONTROL_ARM);
                break;
            case "flwheel":
                DebugPositioner.setDebugPart(VehiclePart.FRONT_LEFT_WHEEL);
                break;
            case "frwheel":
                DebugPositioner.setDebugPart(VehiclePart.FRONT_RIGHT_WHEEL);
                break;
            case "rlwheel":
                DebugPositioner.setDebugPart(VehiclePart.REAR_LEFT_WHEEL);
                break;
            case "rrwheel":
                DebugPositioner.setDebugPart(VehiclePart.REAR_RIGHT_WHEEL);
                break;
            default:
                compatibility.addChatMessage(compatibility.clientPlayer(), "Don't know anything about part " + args[1]);
                return;
            }

            compatibility.addChatMessage(compatibility.clientPlayer(), "Debugging part " + args[1]);
        } catch(NumberFormatException e) {
            compatibility.addChatMessage(compatibility.clientPlayer(), getSubCommandVPartUsage());
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
