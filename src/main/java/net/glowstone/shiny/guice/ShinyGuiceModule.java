package net.glowstone.shiny.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.glowstone.shiny.Shiny;
import net.glowstone.shiny.ShinyGame;
import net.glowstone.shiny.ShinyGameRegistry;
import net.glowstone.shiny.event.ShinyEventManager;
import net.glowstone.shiny.plugin.ShinyPluginManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.event.EventManager;

import java.io.File;

public class ShinyGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Shiny.class).toInstance(Shiny.instance);
        bind(Game.class).to(ShinyGame.class).in(Scopes.SINGLETON);
        bind(PluginManager.class).to(ShinyPluginManager.class).in(Scopes.SINGLETON);
        bind(EventManager.class).to(ShinyEventManager.class).in(Scopes.SINGLETON);
        bind(GameRegistry.class).to(ShinyGameRegistry.class).in(Scopes.SINGLETON);
        bind(File.class).annotatedWith(new ConfigDirAnnotation(true)).toInstance(Shiny.instance.getConfigDirectory());
    }
}
