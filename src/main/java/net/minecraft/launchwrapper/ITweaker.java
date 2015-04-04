package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;

public interface ITweaker {

    public void injectIntoClassLoader(LaunchClassLoader classLoader);

    public String getLaunchTarget();

    public String[] getLaunchArguments();

    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile);
}
