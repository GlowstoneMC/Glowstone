package net.glowstone.interfaces;

import org.bukkit.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

public interface IGlowPlugin extends PluginContainer {

    public enum Type {
        BUKKIT,
        SPONGE
    }

    Type getType();

    Plugin getHandle();
}
