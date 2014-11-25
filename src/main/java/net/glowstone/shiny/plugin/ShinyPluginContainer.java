package net.glowstone.shiny.plugin;

import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

/**
 * Implementation of {@link PluginContainer}.
 */
final class ShinyPluginContainer implements PluginContainer {

    private final Plugin annotation;
    Object instance;

    public ShinyPluginContainer(Plugin annotation) {
        this.annotation = annotation;
    }

    @Override
    public String getId() {
        return annotation.id();
    }

    @Override
    public String getName() {
        return annotation.name();
    }

    @Override
    public String getVersion() {
        return annotation.version();
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
