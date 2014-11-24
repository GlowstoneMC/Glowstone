package net.glowstone.shiny.plugin;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.fromNullable(plugins.get(id));
    }

    @Override
    public Logger getLogger(PluginContainer plugin) {
        return LoggerFactory.getLogger("Plugin/" + plugin.getName());
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return plugins.values();
    }
}
