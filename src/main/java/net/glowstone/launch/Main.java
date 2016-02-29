package net.glowstone.launch;

import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> argList = new ArrayList<>();
        argList.addAll(Arrays.asList(args));
        argList.add("--tweakClass");
        argList.add("net.glowstone.launch.GlowTweak");

        Launch.main(argList.toArray(new String[argList.size()]));
    }
}
