package net.minecraft.launchwrapper;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.ModClassLoader;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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
        System.out.println("getLaunchTarget = " + tweaker.getLaunchTarget());
        // launch target will be 'net.minecraft.server.MinecraftServer'
        // indirectly load it through the required class loader

        URL urls[] = ((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs(); // pass same URLs from original loader
        LaunchClassLoader launchClassLoader = new LaunchClassLoader(urls, null); //ClassLoader.getSystemClassLoader());
        ModClassLoader modClassLoader = new ModClassLoader(launchClassLoader);

        try {
            // TODO: why is Loader instantiated with sun.misc.Launcher.AppClassLoader instead of LaunchClassLoader?
            Class<?> serverClass = modClassLoader.loadClass("net.minecraft.server.MinecraftServer");
            Method main = serverClass.getMethod("main", new Class[]{ String[].class });

            main.invoke(null, (Object) args);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        //net.minecraft.server.MinecraftServer.main(args);
    }
}
