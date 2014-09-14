package net.glowstone.shiny.plugin;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.util.Collection;
import java.util.HashMap;

/**
 * Implementation of {@link PluginManager}.
 */
public class ShinyPluginManager implements PluginManager {

    private final HashMap<String, PluginContainer> plugins = new HashMap<>();

    @Override
    public PluginContainer getPlugin(String id) {
        return plugins.get(id);
    }

    @Override
    public Logger getLogger(PluginContainer plugin) {
        return null;
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return plugins.values();
    }
}
