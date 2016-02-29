package net.glowstone.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

@AllArgsConstructor
public class GlowPluginGuiceModule extends AbstractModule {

    private final PluginContainer container;
    private final Class<?> pluginClass;

    @Override
    protected void configure() {
        //ConfigDir privateConfigDir = new ConfigDirAnnotation(false);
        //DefaultConfig sharedConfigFile = new ConfigFileAnnotation(true);
        //DefaultConfig privateConfigFile = new ConfigFileAnnotation(false);

        bind(this.pluginClass).in(Scopes.SINGLETON);
        bind(PluginContainer.class).toInstance(this.container);
        bind(Logger.class).toInstance(this.container.getLogger());

        // Plugin-private config directory (shared dir is in the global guice module)
        //bind(Path.class).annotatedWith(privateConfigDir).toProvider(PrivateConfigDirProvider.class);
        //bind(File.class).annotatedWith(privateConfigDir).toProvider(FilePrivateConfigDirProvider.class);
        //bind(Path.class).annotatedWith(sharedConfigFile).toProvider(SharedConfigFileProvider.class); // Shared-directory config file
        //bind(File.class).annotatedWith(sharedConfigFile).toProvider(FileSharedConfigFileProvider.class);
        //bind(Path.class).annotatedWith(privateConfigFile).toProvider(PrivateConfigFileProvider.class); // Plugin-private directory config file
        //bind(File.class).annotatedWith(privateConfigFile).toProvider(FilePrivateConfigFileProvider.class);

        //bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        //}).annotatedWith(sharedConfigFile)
        //        .toProvider(SharedHoconConfigProvider.class); // Loader for shared-directory config file
        //bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        //}).annotatedWith(privateConfigFile)
        ///        .toProvider(PrivateHoconConfigProvider.class); // Loader for plugin-private directory config file

        // SpongeExecutorServices
        //bind(SpongeExecutorService.class).annotatedWith(SynchronousExecutor.class).toProvider(SynchronousExecutorProvider.class);
        //bind(SpongeExecutorService.class).annotatedWith(AsynchronousExecutor.class).toProvider(AsynchronousExecutorProvider.class);
    }
}
