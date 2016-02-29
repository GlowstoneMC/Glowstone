package net.glowstone.plugin;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.event.EventRegister;
import net.glowstone.event.SpongeEventManager;
import net.glowstone.interfaces.IGlowPlugin;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.lang3.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handles all plugin management from the Server
 */
public final class GlowPluginManager implements PluginManager {

    private static final String SPONGE_PLUGIN_DESCRIPTOR = "Lorg/spongepowered/api/plugin/Plugin;";
    private static final String FORGEF_PLUGIN_DESCRIPTOR = "Lcpw/mods/fml/common/Mod;";
    private static final String FORGEN_PLUGIN_DESCRIPTOR = "Lnet/minecraftforge/fml/common/Mod;";

    private final GlowServer server;
    private final Map<Pattern, PluginLoader> fileAssociations = new HashMap<>();
    private final Map<String, IGlowPlugin> plugins = new HashMap<>();
    private final SimpleCommandMap commandMap;
    private final Map<String, Permission> permissions = new HashMap<>();
    private final Map<Boolean, Set<Permission>> defaultPerms = new LinkedHashMap<>();
    private final Map<String, Map<Permissible, Boolean>> permSubs = new HashMap<>();
    private final Map<Boolean, Map<Permissible, Boolean>> defSubs = new HashMap<>();
    private boolean useTimings = false;
    private Collection<URL> ignoreURLs = null;

    private final EventRegister eventRegister;

    @Getter
    private final SpongeEventManager glowEventManager;

    public GlowPluginManager(GlowServer instance, SimpleCommandMap commandMap) {
        server = instance;
        this.commandMap = commandMap;
        this.eventRegister = new EventRegister(this, instance);
        this.glowEventManager = eventRegister.getEventManager();

        defaultPerms.put(true, new HashSet<>());
        defaultPerms.put(false, new HashSet<>());
    }

    /**
     * Registers the specified plugin loader
     *
     * @param loader Class name of the PluginLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a
     *     valid PluginLoader
     */
    public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException {
        PluginLoader instance;

        if (PluginLoader.class.isAssignableFrom(loader)) {
            Constructor<? extends PluginLoader> constructor;

            try {
                constructor = loader.getConstructor(Server.class);
                instance = constructor.newInstance(server);
            } catch (NoSuchMethodException ex) {
                String className = loader.getName();

                throw new IllegalArgumentException(String.format("Class %s does not have a public %s(Server) constructor", className, className), ex);
            } catch (Exception ex) {
                throw new IllegalArgumentException(String.format("Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName()), ex);
            }
        } else {
            throw new IllegalArgumentException(String.format("Class %s does not implement interface PluginLoader", loader.getName()));
        }

        Pattern[] patterns = instance.getPluginFileFilters();

        synchronized (this) {
            for (Pattern pattern : patterns) {
                fileAssociations.put(pattern, instance);
            }
        }
    }

    public void setIgnoreURLs(Collection<URL> urls) {
        this.ignoreURLs = urls;
    }

    /**
     * Loads the plugins contained within the specified directory
     *
     * @param directory Directory to check for plugins
     * @return A list of all plugins loaded
     */
    public Plugin[] loadPlugins(File directory) {
        Validate.notNull(directory, "Directory cannot be null");
        Validate.isTrue(directory.isDirectory(), "Directory must be a directory");

        return this.loadPlugins(directory.listFiles(pathname -> {
            return pathname.getName().endsWith(".jar");
        }), directory.getPath());
    }

    /*
    private void loadSpongePlugins() {
        //TODO dependencies
        for(SpongePrePlugin candidate : spongePrePlugins) {
            Launch.classLoader.addURL(candidate.getUrl());

            for (String pluginClassName : candidate.getPluginClasses()) {
                try {
                    Class<?> pluginClazz = Class.forName(pluginClassName);
                    GlowPluginContainer container = GlowPluginContainer.wrapSponge(pluginClazz, server.getInjector());
                    plugins.put(container.getId(), container);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void loadBukkitPlugins() {
        //TODO dependencies
        for(BukkitPrePlugin candidate : bukkitPrePlugins) {
            //Launch.classLoader.addURL(candidate.getUrl());

            try {
                Plugin result = candidate.getPluginLoader().loadPlugin(candidate.getFile());
                GlowPluginContainer container = GlowPluginContainer.wrapBukkit(result);
                plugins.put(container.getId(), container);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    public Plugin[] loadPlugins(File[] files, String sourceFolder) {
        List<Plugin> result = new ArrayList<>();

        Map<String, File> plugins = new HashMap<>();
        Set<String> loadedPlugins = new HashSet<>();
        Map<String, Collection<String>> dependencies = new HashMap<>();
        Map<String, Collection<String>> softDependencies = new HashMap<>();

        List<PrePlugin> prePlugins = new ArrayList<>();

        // This is where it figures out all possible plugins
        for (File file : files) {
            PrePlugin prePlugin = scanFile(file);
            if (prePlugin != null) {
                prePlugins.add(prePlugin);
            }
        }

        for (PrePlugin preplugin : prePlugins) {
            if (preplugin.isSponge()) {
                try {
                    Plugin handle = preplugin.load();
                    if (handle != null) {
                        result.add(handle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (PrePlugin preplugin : prePlugins) {
            if (preplugin.isBukkit()) {
                try {
                    Plugin handle = preplugin.load();
                    if (handle != null) {
                        result.add(handle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toArray(new Plugin[result.size()]);
    }

    /**
     * Loads the plugin in the specified file
     * <p>
     * File must be valid according to the current enabled Plugin interfaces
     *
     * @param file File containing the plugin to load
     * @return The Plugin loaded, or null if it was invalid
     * @throws InvalidPluginException Thrown when the specified file is not a
     *     valid plugin
     * @throws UnknownDependencyException If a required dependency could not
     *     be found
     */
    public synchronized Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        Validate.notNull(file, "File cannot be null");
        PrePlugin pre = scanFile(file);
        if (pre == null) return null;

        return pre.load();
    }

    /**
     * Checks if the given plugin is loaded and returns it when applicable
     * <p>
     * Please note that the name of the plugin is case-sensitive
     *
     * @param name Name of the plugin to check
     * @return Plugin if it exists, otherwise null
     */
    public synchronized Plugin getPlugin(String name) {
        IGlowPlugin raw = plugins.get(name.replace(' ', '_').toLowerCase());
        if (raw == null) return null;
        return raw.getHandle(); // Spigot
    }

    public synchronized IGlowPlugin getRawPlugin(String name) {
        return plugins.get(name.replace(' ', '_').toLowerCase());
    }

    public synchronized Plugin[] getPlugins() {
        Collection<IGlowPlugin> raws = this.plugins.values();
        Plugin[] plugins = new Plugin[raws.size()];
        int i = 0;
        for (IGlowPlugin raw : raws) {
            try {
                plugins[i] = raw.getHandle();
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return plugins;
    }

    /**
     * Checks if the given plugin is enabled or not
     * <p>
     * Please note that the name of the plugin is case-sensitive.
     *
     * @param name Name of the plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    public boolean isPluginEnabled(String name) {
        Plugin plugin = getPlugin(name);

        return isPluginEnabled(plugin);
    }

    /**
     * Checks if the given plugin is enabled or not
     *
     * @param plugin Plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    public boolean isPluginEnabled(Plugin plugin) {
        if ((plugin != null) && (plugins.containsValue(plugin))) {
            return plugin.isEnabled();
        } else {
            return false;
        }
    }

    public void enablePlugin(final Plugin plugin) {
        if (!plugin.isEnabled()) {
            List<Command> pluginCommands = PluginCommandYamlParser.parse(plugin);

            if (!pluginCommands.isEmpty()) {
                commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
            }

            try {
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            HandlerList.bakeAll();
        }
    }

    public void disablePlugins() {
        Plugin[] plugins = getPlugins();
        for (int i = plugins.length - 1; i >= 0; i--) {
            disablePlugin(plugins[i]);
        }
    }

    public void disablePlugin(final Plugin plugin) {
        if (plugin.isEnabled()) {

            PluginLoader loader = plugin.getPluginLoader();
            if (loader == null) return;

            try {
                loader.disablePlugin(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                server.getScheduler().cancelTasks(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                server.getServicesManager().unregisterAll(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                HandlerList.unregisterAll(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            try {
                server.getMessenger().unregisterIncomingPluginChannel(plugin);
                server.getMessenger().unregisterOutgoingPluginChannel(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    public void clearPlugins() {
        synchronized (this) {
            disablePlugins();
            plugins.clear();
            HandlerList.unregisterAll();
            fileAssociations.clear();
            permissions.clear();
            defaultPerms.get(true).clear();
            defaultPerms.get(false).clear();
        }
    }

    /**
     * Calls an event with the given details.
     * <p>
     * This method only synchronizes when the event is not asynchronous.
     *
     * @param event Event details
     */
    public void callEvent(Event event) {
        if (event.isAsynchronous()) {
            if (Thread.holdsLock(this)) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");
            }
            if (server.isPrimaryThread()) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from primary server thread.");
            }
            fireEvent(event);
        } else {
            synchronized (this) {
                fireEvent(event);
            }
        }
    }

    private void fireEvent(Event event) {
        eventRegister.callEvent(event);
    }

    public void registerEvents(Listener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }

        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : plugin.getPluginLoader().createRegisteredListeners(listener, plugin).entrySet()) {
            getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }

    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin) {
        registerEvent(event, listener, priority, executor, plugin, false);
    }

    /**
     * Registers the given event to the specified listener using a directly
     * passed EventExecutor
     *
     * @param event Event class to register
     * @param listener PlayerListener to register
     * @param priority Priority of this event
     * @param executor EventExecutor to register
     * @param plugin Plugin to register
     * @param ignoreCancelled Do not call executor if event was already
     *     cancelled
     */
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled) {
        Validate.notNull(listener, "Listener cannot be null");
        Validate.notNull(priority, "Priority cannot be null");
        Validate.notNull(executor, "Executor cannot be null");
        Validate.notNull(plugin, "Plugin cannot be null");

        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }

        executor = new co.aikar.timings.TimedEventExecutor(executor, plugin, null, event); // Spigot
        if (false) { // Spigot - RL handles useTimings check now
            getEventListeners(event).register(new TimedRegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        } else {
            getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        }
    }

    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
            }
        }
    }

    public Permission getPermission(String name) {
        return permissions.get(name.toLowerCase());
    }

    public void addPermission(Permission perm) {
        String name = perm.getName().toLowerCase();

        if (permissions.containsKey(name)) {
            throw new IllegalArgumentException("The permission " + name + " is already defined!");
        }

        permissions.put(name, perm);
        calculatePermissionDefault(perm);
    }

    public Set<Permission> getDefaultPermissions(boolean op) {
        return ImmutableSet.copyOf(defaultPerms.get(op));
    }

    public void removePermission(Permission perm) {
        removePermission(perm.getName());
    }

    public void removePermission(String name) {
        permissions.remove(name.toLowerCase());
    }

    public void recalculatePermissionDefaults(Permission perm) {
        if (perm != null && permissions.containsKey(perm.getName().toLowerCase())) {
            defaultPerms.get(true).remove(perm);
            defaultPerms.get(false).remove(perm);

            calculatePermissionDefault(perm);
        }
    }

    private void calculatePermissionDefault(Permission perm) {
        if ((perm.getDefault() == PermissionDefault.OP) || (perm.getDefault() == PermissionDefault.TRUE)) {
            defaultPerms.get(true).add(perm);
            dirtyPermissibles(true);
        }
        if ((perm.getDefault() == PermissionDefault.NOT_OP) || (perm.getDefault() == PermissionDefault.TRUE)) {
            defaultPerms.get(false).add(perm);
            dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op) {
        Set<Permissible> permissibles = getDefaultPermSubscriptions(op);

        for (Permissible p : permissibles) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(String permission, Permissible permissible) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map == null) {
            map = new WeakHashMap<>();
            permSubs.put(name, map);
        }

        map.put(permissible, true);
    }

    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map != null) {
            map.remove(permissible);

            if (map.isEmpty()) {
                permSubs.remove(name);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(String permission) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(map.keySet());
        }
    }

    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map == null) {
            map = new WeakHashMap<>();
            defSubs.put(op, map);
        }

        map.put(permissible, true);
    }

    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map != null) {
            map.remove(permissible);

            if (map.isEmpty()) {
                defSubs.remove(op);
            }
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(map.keySet());
        }
    }

    public Set<Permission> getPermissions() {
        return new HashSet<Permission>(permissions.values());
    }

    public boolean useTimings() {
        return co.aikar.timings.Timings.isTimingsEnabled(); // Spigot
    }

    /**
     * Sets whether or not per event timing code should be used
     *
     * @param use True if per event timing code should be used
     */
    public void useTimings(boolean use) {
        co.aikar.timings.Timings.setTimingsEnabled(use); // Spigot
    }


    private interface PrePlugin {

        Plugin load();

        default boolean isBukkit() {
            return false;
        }

        default boolean isSponge() {
            return false;
        }

    }

    @AllArgsConstructor
    private class BukkitPrePlugin implements PrePlugin {

        private File file;
        private PluginLoader pluginLoader;

        @Override
        public Plugin load() {
            try {
                Plugin result = pluginLoader.loadPlugin(file);
                IGlowPlugin container = (IGlowPlugin) result;
                plugins.put(container.getId(), container);
                try {
                    result.onLoad();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean isBukkit() {
            return true;
        }
    }

    @AllArgsConstructor
    private class SpongePrePlugin implements PrePlugin {

        private URL url;
        private Set<String> pluginClasses;

        @Override
        public Plugin load() {
            Launch.classLoader.addURL(url);
            Plugin toReturn = null;

            for (String pluginClassName : pluginClasses) {
                try {
                    Class<?> pluginClazz = Class.forName(pluginClassName, true, Launch.classLoader);
                    IGlowPlugin container = new GlowPluginContainer(pluginClazz, server.getInjector());
                    try {
                        if (toReturn == null) toReturn = container.getHandle();
                    } catch (UnsupportedOperationException e) {
                        e.printStackTrace();
                    }
                    glowEventManager.registerListeners(container, container.getInstance().get());
                    plugins.put(container.getId(), container);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return toReturn;
        }

        @Override
        public boolean isSponge() {
            return true;
        }
    }

    private PrePlugin scanFile(File file) {

        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }

        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entryIn = entries.nextElement();
                String name = entryIn.getName();

                if (name.equals("plugin.yml")) {

                    Set<Pattern> filters = fileAssociations.keySet();
                    PluginLoader loader = null;
                    for (Pattern filter : filters) {
                        Matcher match = filter.matcher(file.getName());
                        if (match.find()) {
                            loader = fileAssociations.get(filter);
                        }
                    }

                    if (loader == null) continue;

                    try {
                        PluginDescriptionFile description = loader.getPluginDescription(file);
                        String pluginName = description.getName();
                        if (pluginName.equalsIgnoreCase("bukkit") || pluginName.equalsIgnoreCase("minecraft") || pluginName.equalsIgnoreCase("mojang")) {
                            server.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + ": Restricted Name");
                            return null;
                        } else if (description.getRawName().indexOf(' ') != -1) {
                            server.getLogger().warning(String.format(
                                    "Plugin `%s' uses the space-character (0x20) in its name `%s' - this is discouraged",
                                    description.getFullName(),
                                    description.getRawName()
                            ));
                        }
                    } catch (InvalidDescriptionException ex) {
                        server.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "'", ex);
                        return null;
                    }

                    return new BukkitPrePlugin(file, loader);
                }

                if (name.equals("Canary.inf")) {
                    return null; // no plugin found
                }

                if (!entryIn.isDirectory() && name.endsWith(".class")) {
                    // Analyze class file
                    ClassReader reader = new ClassReader(zip.getInputStream(entryIn));
                    ClassNode classNode = new ClassNode();
                    reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

                    Set<String> pluginClasses = new HashSet<>();
                    if (classNode.visibleAnnotations != null) {
                        for (AnnotationNode node : classNode.visibleAnnotations) {
                            String desc = node.desc;
                            if (desc.equals(SPONGE_PLUGIN_DESCRIPTOR)) {
                                pluginClasses.add(classNode.name.replace('/', '.'));
                            } else if (FORGEF_PLUGIN_DESCRIPTOR.equals(desc)) {
                                //return; // no plugin found
                            } else if (FORGEN_PLUGIN_DESCRIPTOR.equals(desc)) {
                                //return; // no plugin found
                            }
                        }
                    }

                    if (pluginClasses.size() > 0) {
                        return new SpongePrePlugin(url, pluginClasses);
                    }
                }
            }
        } catch (IOException ex) {
            GlowServer.logger.log(Level.WARNING, "PluginTypeDetector: Error reading " + file, ex);
        }
        return null;
    }
}
