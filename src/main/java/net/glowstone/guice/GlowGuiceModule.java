package net.glowstone.guice;

import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;
import net.glowstone.GlowServer;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.EventManager;

@AllArgsConstructor
public class GlowGuiceModule extends AbstractModule {

    private final GlowServer server;
    private final Logger logger;

    @Override
    protected void configure() {
        //bind(SpongeVanilla.class).toInstance(this.instance);
        //bind(Logger.class).toInstance(this.logger);

        //bind(PluginContainer.class).annotatedWith(Names.named(SpongeImpl.ECOSYSTEM_NAME)).toInstance(this.instance);
        //bind(PluginContainer.class).annotatedWith(Names.named(SpongeImpl.API_NAME)).to(SpongeApiContainer.class).in(Scopes.SINGLETON);
        //bind(PluginContainer.class).annotatedWith(Names.named("Minecraft")).to(MinecraftPluginContainer.class).in(Scopes.SINGLETON);

        bind(Game.class).toInstance((Game) (Object) server);
        bind(org.bukkit.Server.class).toInstance(server);
        bind(GlowServer.class).toInstance(server);
        //bind(MinecraftVersion.class).toInstance(SpongeImpl.MINECRAFT_VERSION);
        //bind(Platform.class).to(VanillaPlatform.class).in(Scopes.SINGLETON);
        //bind(PluginManager.class).toInstance((PluginManager) server.getPluginManager());
        bind(EventManager.class).toInstance(server.getPluginManager().getGlowEventManager());
        //bind(GameRegistry.class).to(SpongeGameRegistry.class).in(Scopes.SINGLETON);
        //bind(ServiceManager.class).to(SimpleServiceManager.class).in(Scopes.SINGLETON);
        //bind(TeleportHelper.class).to(SpongeTeleportHelper.class).in(Scopes.SINGLETON);
        //bind(ChannelRegistrar.class).to(VanillaChannelRegistrar.class).in(Scopes.SINGLETON);

        //ConfigDirAnnotation sharedRoot = new ConfigDirAnnotation(true);
        //bind(Path.class).annotatedWith(sharedRoot).toInstance(SpongeImpl.getConfigDir());
        //bind(File.class).annotatedWith(sharedRoot).toInstance(SpongeImpl.getConfigDir().toFile());
    }

}
