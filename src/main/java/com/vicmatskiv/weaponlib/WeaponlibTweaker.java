package com.vicmatskiv.weaponlib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class WeaponlibTweaker implements ITweaker {

    public List<String> args;
    public File gameDir;
    public File assetsDir;
    public String version;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
        this.args = args;
        this.gameDir = gameDir;
        this.assetsDir = assetsDir;
        this.version = version;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        System.out.println("Injecting into classloader");
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
    @Override
    public String[] getLaunchArguments() {
        ArrayList argumentList = (ArrayList) Launch.blackboard.get("ArgumentList");
        if (argumentList.isEmpty()) {
            List argsList = new ArrayList();
            if (this.gameDir != null) {
                argumentList.add("--gameDir");
                argumentList.add(this.gameDir.getPath());
            }
            if (this.assetsDir != null) {
                argumentList.add("--assetsDir");
                argumentList.add(this.assetsDir.getPath());
            }
            argumentList.add("--version");
            argumentList.add(this.version);
            argumentList.addAll(this.args);
        }
        return new String[0];
    }

}
