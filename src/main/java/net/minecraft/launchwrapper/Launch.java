package net.minecraft.launchwrapper;

import java.util.HashMap;

public class Launch {

    public static HashMap<String, Object> blackboard = new HashMap<>();

    public static void main(String args[]) {
        System.out.println("blackboard="+blackboard);

        // FML passes us in args:
        // --tweakClass net.minecraftforge.fml.common.launcher.FMLServerTweaker
        for (int i = 0; i < args.length; ++i) {
            System.out.println("arg "+args[i]);
        }

        // just call it directly

        ITweaker tweaker = new net.minecraftforge.fml.common.launcher.FMLServerTweaker();
        System.out.println("getLaunchTarget = "+tweaker.getLaunchTarget());
        // launch target will be 'net.minecraft.server.MinecraftServer'
        // again, lets just call it directly, keep it simple

        net.minecraft.server.MinecraftServer.main(args);
    }
}
