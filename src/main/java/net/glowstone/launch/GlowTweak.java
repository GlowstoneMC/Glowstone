package net.glowstone.launch;

import net.glowstone.GlowServer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.util.List;

public class GlowTweak implements ITweaker {

    @Override
    public void acceptOptions(List<String> list, File file, File file1, String s) {

    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader loader) {
        MixinBootstrap.init();
        MixinEnvironment.setCompatibilityLevel(MixinEnvironment.CompatibilityLevel.JAVA_8);

        MixinEnvironment.getDefaultEnvironment()
                .addConfiguration("mixins/mixins.glowstone.json")
                .setSide(MixinEnvironment.Side.SERVER);
    }

    @Override
    public String getLaunchTarget() {
        return "net.glowstone.GlowServer";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
