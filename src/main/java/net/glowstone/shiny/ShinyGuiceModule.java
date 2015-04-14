package net.glowstone.shiny;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.glowstone.shiny.plugin.ShinyPluginManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginManager;

public class ShinyGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Shiny.class).toInstance(Shiny.instance);
        bind(Game.class).to(ShinyGame.class).in(Scopes.SINGLETON);
        bind(PluginManager.class).to(ShinyPluginManager.class).in(Scopes.SINGLETON);
    }
}
