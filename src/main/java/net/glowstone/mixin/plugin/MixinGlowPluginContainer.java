package net.glowstone.mixin.plugin;

import com.avaje.ebean.EbeanServer;
import com.google.inject.Injector;
import net.glowstone.GlowServer;
import net.glowstone.plugin.GlowPluginContainer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.PluginLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

@Mixin(value = GlowPluginContainer.class, remap = false)
public abstract class MixinGlowPluginContainer implements Plugin {

    @Shadow
    private Class<?> pluginClass;
    @Shadow
    private Injector injector;
    @Shadow
    private String version;

    private PluginDescriptionFile pluginDescriptionFile;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstruct(CallbackInfo callbackInfo) {
        pluginDescriptionFile = new PluginDescriptionFile(getName(), version, pluginClass.getCanonicalName());

        try {
            Field f = PluginDescriptionFile.class.getDeclaredField("order");
            f.setAccessible(true);
            f.set(pluginDescriptionFile, PluginLoadOrder.STARTUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Plugin getHandle() {
        return this;
    }

    @Override
    public File getDataFolder() {
        return new File("plugins/" + getName());
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return pluginDescriptionFile;
    }

    @Override
    public FileConfiguration getConfig() {
        return null;
    }

    @Override
    public InputStream getResource(String s) {
        return null;
    }

    @Override
    public void saveConfig() { }

    @Override
    public void saveDefaultConfig() { }

    @Override
    public void saveResource(String s, boolean b) {

    }

    @Override
    public void reloadConfig() { }

    @Override
    public PluginLoader getPluginLoader() {
        return null;
    }

    @Override
    public Server getServer() {
        return injector.getInstance(GlowServer.class);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onDisable() { }

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() { }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean b) { }

    @Override
    public EbeanServer getDatabase() {
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(getName());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
