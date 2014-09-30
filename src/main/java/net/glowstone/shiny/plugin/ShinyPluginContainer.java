package net.glowstone.shiny.plugin;

import org.spongepowered.api.plugin.PluginContainer;

/**
 * Implementation of {@link PluginContainer}.
 */
public final class ShinyPluginContainer implements PluginContainer {

    private final String id;
    private final String name;
    private final String version;
    private final Object instance;

    public ShinyPluginContainer(String id, String name, String version, Object instance) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.instance = instance;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
