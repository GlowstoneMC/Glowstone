package net.glowstone.mixin.plugin;

import net.glowstone.interfaces.IGlowPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(JavaPlugin.class)
@Implements(value = @Interface(iface = IGlowPlugin.class, prefix = "gp$"))
public abstract class MixinPluginBase extends PluginBase {

    private Logger spongelogger = LoggerFactory.getLogger(getClass());

    @Shadow(remap = false)
    public abstract PluginDescriptionFile getDescription();

    public Logger gp$getLogger() {
        return spongelogger;
    }

    public Optional<Object> gp$getInstance() {
        return Optional.of(this);
    }

    public String gp$getId() {
        return getName();
    }

    public String gp$getVersion() {
        return getDescription().getVersion();
    }

    public IGlowPlugin.Type gp$getType() {
        return IGlowPlugin.Type.BUKKIT;
    }

    public Plugin gp$getHandle() {
        return this;
    }

}
