package net.glowstone.plugin;

import com.google.inject.Injector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.glowstone.guice.GlowPluginGuiceModule;
import net.glowstone.interfaces.IGlowPlugin;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@AllArgsConstructor
public class GlowPluginContainer implements IGlowPlugin {

    private final Class<?> pluginClass;
    private final Injector injector;
    private final Optional<Object> instance;
    @Getter private final String name, id, version;
    @Getter private final Logger logger;

    public GlowPluginContainer(Class<?> pluginClass, Injector parent) {
        this.pluginClass = pluginClass;
        this.logger = LoggerFactory.getLogger(pluginClass);

        this.injector = parent.createChildInjector(new GlowPluginGuiceModule(this, pluginClass));
        this.instance = Optional.of(this.injector.getInstance(pluginClass));

        org.spongepowered.api.plugin.Plugin info = pluginClass.getAnnotation(org.spongepowered.api.plugin.Plugin.class);
        this.id = info.id();
        this.name = info.name();
        this.version = info.version();

    }

    @Override
    public Optional<Object> getInstance() {
        return instance;
    }

    @Override
    public Plugin getHandle() {
        throw new UnsupportedOperationException("NIY");
    }

    public Type getType() {
        return Type.SPONGE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IGlowPlugin) {
            return getId().equals(((IGlowPlugin) obj).getId());
        }
        return false;
    }
}
