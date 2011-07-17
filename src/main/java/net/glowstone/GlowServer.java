package net.glowstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.util.config.Configuration;

import net.glowstone.io.McRegionChunkIoService;
import net.glowstone.net.MinecraftPipelineFactory;
import net.glowstone.net.Session;
import net.glowstone.net.SessionRegistry;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.util.PlayerListFile;
import net.glowstone.inventory.CraftingManager;
import org.bukkit.permissions.Permission;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * The core class of the Glowstone server.
 * @author Graham Edgecombe
 */
public final class GlowServer implements Server {

    /**
     * The logger for this class.
     */
    public static final Logger logger = Logger.getLogger("Minecraft");

    /**
     * The directory configurations are stored in
     */
    private static final File configDir = new File("config");
            
    /**
     * The configuration the server uses.
     */
    private static final Configuration config = new Configuration(new File(configDir, "glowstone.yml"));

    /**
     * Creates a new server on TCP port 25565 and starts listening for
     * connections.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        try {
            if (!configDir.exists() || !configDir.isDirectory())
                configDir.mkdirs();
            config.load();
            
            int port = config.getInt("server.port", 25565);
            
            GlowServer server = new GlowServer();
            server.bind(new InetSocketAddress(port));
            server.start();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error during server startup.", t);
        }
    }

    /**
     * The {@link ServerBootstrap} used to initialize Netty.
     */
    private final ServerBootstrap bootstrap = new ServerBootstrap();

    /**
     * A group containing all of the channels.
     */
    private final ChannelGroup group = new DefaultChannelGroup();

    /**
     * The network executor service - Netty dispatches events to this thread
     * pool.
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * A list of all the active {@link Session}s.
     */
    private final SessionRegistry sessions = new SessionRegistry();
    
    /**
     * The console manager of this server.
     */
    private final ConsoleManager consoleManager = new ConsoleManager(this, true);
    
    /**
     * The services manager of this server.
     */
    private final SimpleServicesManager servicesManager = new SimpleServicesManager();
    
    /**
     * The command map of this server.
     */
    private final SimpleCommandMap commandMap = new SimpleCommandMap(this);
    
    /**
     * The command map for commands built-in to Glowstone.
     */
    private final SimpleCommandMap builtinCommandMap = new SimpleCommandMap(this);
    
    /**
     * The plugin manager of this server.
     */
    private final SimplePluginManager pluginManager = new SimplePluginManager(this, commandMap);
    
    /**
     * The crafting manager for this server.
     */
    private final CraftingManager craftingManager = new CraftingManager();
    
    /**
     * The list of OPs on the server.
     */
    private final PlayerListFile opsList = new PlayerListFile(new File(configDir, "ops.txt"));

    /**
     * The world this server is managing.
     */
    private final ArrayList<GlowWorld> worlds = new ArrayList<GlowWorld>();

    /**
     * The task scheduler used by this server.
     */
    private final GlowScheduler scheduler = new GlowScheduler(this);

    /**
     * Creates a new server.
     */
    public GlowServer() {
        logger.log(Level.INFO, "This server is running {0} version {1}", new Object[]{getName(), getVersion()});
        init();
    }

    /**
     * Initializes the channel and pipeline factories.
     */
    private void init() {
        Bukkit.setServer(this);
        
        ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
        bootstrap.setFactory(factory);

        ChannelPipelineFactory pipelineFactory = new MinecraftPipelineFactory(this);
        bootstrap.setPipelineFactory(pipelineFactory);
        
        if (config.getKeys().size() <= 1) {
            System.out.println("Generating default configuration config/glowstone.yml...");

            // Server config
            config.setProperty("server.port", 25565);
            config.setProperty("server.world-name", "world");
            config.setProperty("server.max-players", 0);
            config.setProperty("server.spawn-radius", 16);
            config.setProperty("server.online-mode", true);
            config.setProperty("server.log-file", "logs/log-%D.txt");

            // Server folders config
            config.setProperty("server.folders.plugins", "plugins");
            config.setProperty("server.folders.update", "update");

            // Database config
            config.setProperty("database.driver", "org.sqlite.JDBC");
            config.setProperty("database.url", "jdbc:sqlite:{DIR}{NAME}.db");
            config.setProperty("database.username", "glowstone");
            config.setProperty("database.password", "nether");
            config.setProperty("database.isolation", "SERIALIZABLE");

            // Autodetect any movable configuration
            File bukkitYml = new File("bukkit.yml");
            if (bukkitYml.exists()) {
                Configuration bukkit = new Configuration(bukkitYml);
                bukkit.load();
                String moved = "", separator = "";

                if (bukkit.getNode("database") != null) {
                    config.setProperty("database", bukkit.getNode("database").getAll());
                    moved += separator + "database settings";
                    separator = ", ";
                }

                if (bukkit.getProperty("settings.spawn-radius") != null) {
                    config.setProperty("server.spawn-radius", bukkit.getInt("settings.spawn-radius", 16));
                    moved += separator + "spawn radius";
                    separator = ", ";
                }

                if (bukkit.getString("settings.update-folder") != null) {
                    config.setProperty("server.folders.update", bukkit.getString("settings.update-folder"));
                    moved += separator + "update folder";
                    separator = ", ";
                }

                if (bukkit.getNode("worlds") != null) {
                    config.setProperty("worlds", bukkit.getNode("worlds").getAll());
                    moved += separator + "world generators";
                    separator = ", ";
                }

                // TODO: move aliases when those are implemented

                if (moved.length() > 0) {
                    System.out.println("Copied " + moved + " from bukkit.yml");
                }
            }

            File serverProps = new File("server.properties");
            if (serverProps.exists()) {
                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(serverProps));
                    String moved = "", separator = "";

                    if (properties.containsKey("level-name")) {
                        String world = properties.getProperty("level-name", "world");
                        config.setProperty("server.world-name", world);
                        moved += separator + "world name";
                        separator = ", ";
                    }

                    if (properties.containsKey("online-mode")) {
                        String value = properties.getProperty("online-mode", "true");
                        boolean bool = value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
                        config.setProperty("server.online-mode", bool);
                        moved += separator + "online mode";
                        separator = ", ";
                    }

                    if (properties.containsKey("server-port")) {
                        String value = properties.getProperty("server-port", "25565");
                        try {
                            int port = Integer.parseInt(value);
                            config.setProperty("server.port", port);
                            moved += separator + "port";
                            separator = ", ";
                        }
                        catch (NumberFormatException ex) {}
                    }

                    if (properties.containsKey("max-players")) {
                        String value = properties.getProperty("max-players", "20");
                        try {
                            int players = Integer.parseInt(value);
                            config.setProperty("server.max-players", players);
                            moved += separator + "max players";
                            separator = ", ";
                        }
                        catch (NumberFormatException ex) {}
                    }

                    // TODO: move nether, view distance, monsters, etc when implemented

                    if (moved.length() > 0) {
                        System.out.println("Copied " + moved + " from server.properties");
                    }
                }
                catch (IOException ex) {}
            }

            config.save();
        }
    }

    /**
     * Binds this server to the specified address.
     * @param address The addresss.
     */
    public void bind(SocketAddress address) {
        logger.log(Level.INFO, "Binding to address: {0}...", address);
        group.add(bootstrap.bind(address));
    }

    /**
     * Starts this server.
     */
    public void start() {
        try {
            config.load();
        } catch (Exception ex) {
            logger.warning("Failed to load glowstone.yml, using defaults");
        }
        
        opsList.load();

        loadPlugins();
        enablePlugins(PluginLoadOrder.STARTUP);
        createWorld(config.getString("server.world-name", "world"), Environment.NORMAL);
        enablePlugins(PluginLoadOrder.POSTWORLD);
        registerCommands();

        logger.info("Ready for connections.");
    }
    
    /**
     * Stops this server.
     */
    public void stop() {
        logger.info("The server is shutting down...");
        
        // Stop scheduler and disable plugins
        scheduler.stop();
        pluginManager.clearPlugins();
        
        // Save worlds
        for (World world : getWorlds()) {
            world.save();
        }
        
        // Kick (and save) all players
        for (Player player : getOnlinePlayers()) {
            player.kickPlayer("Server shutting down.");
        }
        
        // Gracefully stop Netty
        group.close();
        bootstrap.getFactory().releaseExternalResources();
    }
    
    /**
     * Loads all plugins, calling onLoad, &c.
     */
    private void loadPlugins() {
        // clear the map
        commandMap.clearCommands();
            
        File folder = new File(config.getString("server.folders.plugins", "plugins"));
        folder.mkdirs();
            
        // clear plugins and prepare to load
        pluginManager.clearPlugins();
        pluginManager.registerInterface(JavaPluginLoader.class);
        Plugin[] plugins = pluginManager.loadPlugins(folder);

        // call onLoad methods
        for (Plugin plugin : plugins) {
            try {
                plugin.onLoad();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error loading {0}: {1}", new Object[]{plugin.getDescription().getName(), ex.getMessage()});
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Enable all plugins of the given load order type.
     * @param type The type of plugin to enable.
     */
    public void enablePlugins(PluginLoadOrder type) {
        Plugin[] plugins = pluginManager.getPlugins();
        for (Plugin plugin : plugins) {
            if (!plugin.isEnabled() && plugin.getDescription().getLoad() == type) {
                List<Command> pluginCommands = PluginCommandYamlParser.parse(plugin);

                if (!pluginCommands.isEmpty()) {
                    commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
                }
                
                List<Permission> perms = plugin.getDescription().getPermissions();
                for (Permission perm : perms) {
                    try {
                        pluginManager.addPermission(perm);
                    } catch (IllegalArgumentException ex) {
                        getLogger().log(Level.WARNING, "Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered", ex);
                    }
                }

                try {
                    pluginManager.enablePlugin(plugin);
                } catch (Throwable ex) {
                    logger.log(Level.SEVERE, "Error loading {0}", plugin.getDescription().getFullName());
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Registers built-in Glowstone commands and refreshes the autocomplete index.
     */
    private void registerCommands() {
        builtinCommandMap.register("#", new net.glowstone.command.OpCommand(this));
        builtinCommandMap.register("#", new net.glowstone.command.DeopCommand(this));
        builtinCommandMap.register("#", new net.glowstone.command.ListCommand(this));
        builtinCommandMap.register("#", new net.glowstone.command.ColorCommand(this));
        builtinCommandMap.register("#", new net.glowstone.command.StopCommand(this));
        
        consoleManager.refreshCommands();
    }

    /**
     * Reloads the server, refreshing settings and plugin information
     */
    public void reload() {
        try {
            config.load();
            opsList.load();
            
            // reset crafting
            craftingManager.resetRecipes();
            
            // load plugins
            loadPlugins();
            enablePlugins(PluginLoadOrder.STARTUP);
            enablePlugins(PluginLoadOrder.POSTWORLD);
            registerCommands();
            
            // TODO: register aliases
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, "Uncaught error while reloading: {0}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Gets the channel group.
     * @return The {@link ChannelGroup}.
     */
    public ChannelGroup getChannelGroup() {
        return group;
    }

    /**
     * Gets the session registry.
     * @return The {@link SessionRegistry}.
     */
    public SessionRegistry getSessionRegistry() {
        return sessions;
    }
    
    /**
     * Returns the list of OPs on this server.
     */
    public PlayerListFile getOpsList() {
        return opsList;
    }

    /**
     * Gets the world by the given name.
     * @param name The name of the world to look up.
     * @return The {@link GlowWorld} this server manages.
     */
    public GlowWorld getWorld(String name) {
        for (GlowWorld world : worlds) {
            if (world.getName().equalsIgnoreCase(name))
                return world;
        }
        return null;
    }

    /**
     * Gets the world from the given Unique ID
     *
     * @param uid Unique ID of the world to retrieve.
     * @return World with the given Unique ID, or null if none exists.
     */
    public GlowWorld getWorld(UUID uid) {
        for (GlowWorld world : worlds) {
            if (uid.equals(world.getUID()))
                return world;
        }
        return null;
    }
    
    /**
     * Gets the list of worlds currently loaded.
     * @return An ArrayList containing all loaded worlds.
     */
    public List<World> getWorlds() {
        ArrayList<World> result = new ArrayList<World>();
        for (GlowWorld world : worlds)
            result.add(world);
        return result;
    }
    
    /**
     * Use reflection to get a list of available commands from the command map.
     * @return A list of all commands at the time.
     */
    protected String[] getAllCommands() {
        // There's probably a better way of doing this.
        try {
            Class clazz = commandMap.getClass();
            Field knownCommandsField = clazz.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            HashSet<String> knownCommands = new HashSet<String>(((Map<String, Command>) knownCommandsField.get(commandMap)).keySet());
            knownCommands.addAll(((Map<String, Command>) knownCommandsField.get(builtinCommandMap)).keySet());
            return knownCommands.toArray(new String[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * Gets the name of this server implementation
     *
     * @return "Glowstone"
     */
    public String getName() {
        return "Glowstone";
    }

    /**
     * Gets the version string of this server implementation.
     *
     * @return version of this server implementation
     */
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }
    
    /**
     * Gets a list of all currently logged in players
     *
     * @return An array of Players that are currently online
     */
    public Player[] getOnlinePlayers() {
        ArrayList<Player> result = new ArrayList<Player>();
        for (World world : getWorlds()) {
            for (Player player : world.getPlayers())
                result.add(player);
        }
        return result.toArray(new Player[] {});
    }
    
    /**
     * Get the maximum amount of players which can login to this server
     *
     * @return The amount of players this server allows
     */
    public int getMaxPlayers() {
        return config.getInt("server.max-players", 0);
    }

    /**
     * Gets the port the server listens on.
     * @return The port number the server is listening on.
     */
    public int getPort() {
        return config.getInt("server.port", 25565);
    }

    /**
     * Get the IP that this server is bound to or empty string if not specified
     *
     * @return The IP string that this server is bound to, otherwise empty string
     */
    public String getIp() {
        return "";
    }
    
    /**
     * Get the name of this server
     *
     * @return The name of this server
     */
    public String getServerName() {
        return "Glowstone Server";
    }
    
    /**
     * Get an ID of this server. The ID is a simple generally alphanumeric
     * ID that can be used for uniquely identifying this server.
     *
     * @return The ID of this server
     */
    public String getServerId() {
        return Integer.toHexString(getServerName().hashCode());
    }

    /**
     * Broadcast a message to all players.
     *
     * @param message the message
     * @return the number of players
     */
    public int broadcastMessage(String message) {
        for (Player player : getOnlinePlayers()) {
            player.sendMessage(message);
        }
        return getOnlinePlayers().length;
    }
    
    /**
     * Gets the name of the update folder. The update folder is used to safely update
     * plugins at the right moment on a plugin load.
     *
     * @return The name of the update folder
     */
    public String getUpdateFolder() {
        return config.getString("server.folders.update", "update");
    }
    
    /**
     * Gets a player object by the given username
     *
     * This method may not return objects for offline players
     *
     * @param name Name to look up
     * @return Player if it was found, otherwise null
     */
    public Player getPlayer(String name) {
        for (Player player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }

    /**
     * Attempts to match any players with the given name, and returns a list
     * of all possibly matches
     *
     * This list is not sorted in any particular order. If an exact match is found,
     * the returned list will only contain a single result.
     *
     * @param name Name to match
     * @return List of all possible players
     */
    public List<Player> matchPlayer(String name) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player player : getOnlinePlayers()) {
            if (player.getName().startsWith(name)) {
                result.add(player);
            }
        }
        return result;
    }

    /**
     * Gets the PluginManager for interfacing with plugins
     *
     * @return PluginManager for this GlowServer instance
     */
    public SimplePluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Gets the Scheduler for managing scheduled events
     *
     * @return Scheduler for this GlowServer instance
     */
    public GlowScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Gets a services manager
     *
     * @return Services manager
     */
    public SimpleServicesManager getServicesManager() {
        return servicesManager;
    }
    
    /**
     * Gets the default ChunkGenerator for the given environment.
     * @return The ChunkGenerator.
     */
    private ChunkGenerator getGenerator(String name, Environment environment) {
        if (config.getString("worlds." + name + ".generator") != null) {
            String[] args = config.getString("worlds." + name + ".generator").split(":", 2);
            if (getPluginManager().getPlugin(args[0]) == null) {
                logger.log(Level.WARNING, "Plugin {0} specified for world {1} does not exist, using default.", new Object[]{args[0], name});
            } else {
                return getPluginManager().getPlugin(args[0]).getDefaultWorldGenerator(name, args.length == 2 ? args[1] : "");
            }
        }
        
        if (environment == Environment.NETHER) {
            return new net.glowstone.generator.FlatNetherGenerator();
        } else if (environment == Environment.SKYLANDS) {
            // TODO: add skylands generator
            return new net.glowstone.generator.FlatgrassGenerator();
        } else {
            return new net.glowstone.generator.FlatForestGenerator();
        }
    }

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @return Newly created or loaded World
     */
    public GlowWorld createWorld(String name, Environment environment) {
        return createWorld(name, environment, new Random().nextLong(), getGenerator(name, environment));
    }

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @param seed Seed value to create the world with
     * @return Newly created or loaded World
     */
    public GlowWorld createWorld(String name, Environment environment, long seed) {
        return createWorld(name, environment, seed, getGenerator(name, environment));
    }

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @param generator ChunkGenerator to use in the construction of the new world
     * @return Newly created or loaded World
     */
    public GlowWorld createWorld(String name, Environment environment, ChunkGenerator generator) {
        return createWorld(name, environment, new Random().nextLong(), generator);
    }

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @param seed Seed value to create the world with
     * @param generator ChunkGenerator to use in the construction of the new world
     * @return Newly created or loaded World
     */
    public GlowWorld createWorld(String name, Environment environment, long seed, ChunkGenerator generator) {
        if (getWorld(name) != null) return getWorld(name);
        GlowWorld world = new GlowWorld(this, name, environment, seed, new McRegionChunkIoService(new File(name)), generator);
        if (world != null) worlds.add(world);
        return world;
    }

    /**
     * Unloads a world with the given name.
     *
     * @param name Name of the world to unload
     * @param save Whether to save the chunks before unloading.
     * @return Whether the action was Successful
     */
    public boolean unloadWorld(String name, boolean save) {
        if (getWorld(name) == null) return false;
        return unloadWorld(getWorld(name), save);
    }

    /**
     * Unloads the given world.
     *
     * @param world The world to unload
     * @param save Whether to save the chunks before unloading.
     * @return Whether the action was Successful
     */
    public boolean unloadWorld(World world, boolean save) {
        if (save) {
            world.save();
        }
        if (!(world instanceof GlowWorld)) {
            return false;
        }
        if (worlds.contains((GlowWorld) world)) {
            worlds.remove((GlowWorld) world);
            return true;
        }
        return false;
    }
    
    /**
     * Returns the primary logger associated with this server instance
     *
     * @return Logger associated with this server
     */
    public Logger getLogger() {
        return logger;
    }
    
    /**
     * Gets a {@link PluginCommand} with the given name or alias
     *
     * @param name Name of the command to retrieve
     * @return PluginCommand if found, otherwise null
     */
    public PluginCommand getPluginCommand(String name) {
        Command command = commandMap.getCommand(name);
        if (command instanceof PluginCommand) {
            return (PluginCommand) command;
        } else {
            return null;
        }
    }

    /**
     * Writes loaded players to disk
     */
    public void savePlayers() {
        for (Player player : getOnlinePlayers())
            player.saveData();
    }

    /**
     * Dispatches a command on the server, and executes it if found.
     *
     * @param cmdLine command + arguments. Example: "test abc 123"
     * @return targetFound returns false if no target is found.
     * @throws CommandException Thrown when the executor for the given command fails with an unhandled exception
     */
    public boolean dispatchCommand(CommandSender sender, String commandLine) {
        try {
            if (commandMap.dispatch(sender, commandLine)) {
                return true;
            }
            
            if (builtinCommandMap.dispatch(sender, commandLine)) {
                return true;
            }
            
            return false;
        }
        catch (CommandException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new CommandException("Unhandled exception executing command", ex);
        }
    }

    /**
     * Populates a given {@link ServerConfig} with values attributes to this server
     *
     * @param dbConfig ServerConfig to populate
     */
    public void configureDbConfig(com.avaje.ebean.config.ServerConfig dbConfig) {
        com.avaje.ebean.config.DataSourceConfig ds = new com.avaje.ebean.config.DataSourceConfig();
        ds.setDriver(config.getString("database.driver", "org.sqlite.JDBC"));
        ds.setUrl(config.getString("database.url", "jdbc:sqlite:{DIR}{NAME}.db"));
        ds.setUsername(config.getString("database.username", "glow"));
        ds.setPassword(config.getString("database.password", "stone"));
        ds.setIsolationLevel(com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation.getLevel(config.getString("database.isolation", "SERIALIZABLE")));

        if (ds.getDriver().contains("sqlite")) {
            dbConfig.setDatabasePlatform(new com.avaje.ebean.config.dbplatform.SQLitePlatform());
            dbConfig.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        dbConfig.setDataSourceConfig(ds);
    }
    
    /**
     * Return the crafting manager.
     * @return The server's crafting manager.
     */
    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    /**
     * Adds a recipe to the crafting manager.
     * @param recipe The recipe to add.
     * @return True to indicate that the recipe was added.
     */
    public boolean addRecipe(Recipe recipe) {
        return craftingManager.addRecipe(recipe);
    }

    public Map<String, String[]> getCommandAliases() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getSpawnRadius() {
        return config.getInt("server.spawn-radius", 16);
    }

    public void setSpawnRadius(int value) {
        config.setProperty("server.spawn-radius", value);
    }

    public boolean getOnlineMode() {
        return config.getBoolean("server.online-mode", true);
    }
    
    public String getLogFile() {
        return config.getString("server.log-file", "logs/log-%D.txt");
    }
     
}
