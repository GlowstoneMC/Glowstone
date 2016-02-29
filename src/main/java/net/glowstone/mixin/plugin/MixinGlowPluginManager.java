package net.glowstone.mixin.plugin;

import net.glowstone.interfaces.IGlowPlugin;
import net.glowstone.plugin.GlowPluginManager;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Mixin(value = GlowPluginManager.class, remap = false)
public class MixinGlowPluginManager implements PluginManager {

    @Shadow
    private Map<String, IGlowPlugin> plugins;

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        PluginContainer toReturn = null;
        for (PluginContainer container : plugins.values()) {
            if (container.getInstance() == instance) {
                toReturn = container;
                break;
            }
        }
        return Optional.ofNullable(toReturn);
    }

    @Override
    public Optional<PluginContainer> getPlugin(String name) {
        return Optional.ofNullable(plugins.get(name));
    }

    @Override
    public Logger getLogger(PluginContainer pluginContainer) {
        return pluginContainer.getLogger();
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return (Collection) plugins.values();
    }

    @Override
    public boolean isLoaded(String name) {
        return plugins.containsKey(name);
    }

}
