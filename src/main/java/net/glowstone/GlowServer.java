package net.glowstone;

import net.glowstone.command.*;
import net.glowstone.inventory.CraftingManager;
import net.glowstone.io.StorageQueue;
import net.glowstone.io.mcregion.McRegionWorldStorageProvider;
import net.glowstone.map.GlowMapView;
import net.glowstone.net.MinecraftPipelineFactory;
import net.glowstone.net.Session;
import net.glowstone.net.SessionRegistry;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.util.PlayerListFile;
import net.glowstone.util.ServerConfig;
import net.glowstone.util.bans.BanManager;
import net.glowstone.util.bans.FlatFileBanManager;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.permissions.DefaultPermissions;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * The protocol version supported by the server.
     */
    public static final int PROTOCOL_VERSION = 4;

    /**
     * The storage queue for handling I/O operations.
     */
    public static final StorageQueue storeQueue = new StorageQueue();

    /**
     * Creates a new server on TCP port 25565 and starts listening for
     * connections.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        try {
            storeQueue.start();

            ConfigurationSerialization.registerClass(GlowOfflinePlayer.class);

            GlowServer server = new GlowServer();
            server.start();
            server.bind();
            logger.info("Ready for connections.");
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error during server startup.", t);
            System.exit(1);
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
    private final ConsoleManager consoleManager = new ConsoleManager(this, "jline");
    
    /**
     * The services manager of this server.
     */
    private final SimpleServicesManager servicesManager = new SimpleServicesManager();

    /**
     * The command map of this server.
     */
    private final GlowCommandMap commandMap = new GlowCommandMap(this);

    /**
     * The plugin manager of this server.
     */
    private final SimplePluginManager pluginManager = new SimplePluginManager(this, commandMap);
    
    /**
     * The crafting manager for this server.
     */
    private final CraftingManager craftingManager = new CraftingManager();

    /**
     * The configuration for the server.
     */
    private final ServerConfig config = new ServerConfig();
    
    /**
     * The list of OPs on the server.
     */
    private final PlayerListFile opsList = new PlayerListFile(new File(ServerConfig.CONFIG_DIR, "ops.txt"));
    
    /**
     * The list of players whitelisted on the server.
     */
    private final PlayerListFile whitelist = new PlayerListFile(new File(ServerConfig.CONFIG_DIR, "whitelist.txt"));

    /**
     * The server's ban manager.
     */
    private BanManager banManager = new FlatFileBanManager(this);

    /**
     * The world this server is managing.
     */
    private final ArrayList<GlowWorld> worlds = new ArrayList<GlowWorld>();

    /**
     * The task scheduler used by this server.
     */
    private final GlowScheduler scheduler = new GlowScheduler(this);

    /**
     * The server's default game mode
     */
    private GameMode defaultGameMode = GameMode.CREATIVE;

    /**
     * The setting for verbose deprecation warnings.
     */
    private Warning.WarningState warnState = Warning.WarningState.DEFAULT;

    /**
     * Whether the server is shutting down
     */
    private boolean isShuttingDown = false;

    /**
     * A cache of existing OfflinePlayers
     */
    private final Map<String, OfflinePlayer> offlineCache = new ConcurrentHashMap<String, OfflinePlayer>();

    /**
     * Creates a new server.
     */
    public GlowServer() {
        Bukkit.setServer(this);

        ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
        bootstrap.setFactory(factory);

        ChannelPipelineFactory pipelineFactory = new MinecraftPipelineFactory(this);
        bootstrap.setPipelineFactory(pipelineFactory);

        config.load();
        warnState = Warning.WarningState.value(config.getString(ServerConfig.Key.WARNING_STATE));
        try {
            defaultGameMode = GameMode.valueOf(GameMode.class, config.getString(ServerConfig.Key.GAMEMODE));
        } catch (IllegalArgumentException e) {
            defaultGameMode = GameMode.SURVIVAL;
            config.set(ServerConfig.Key.GAMEMODE, defaultGameMode.name());
        }
        // config.getString("server.terminal-mode", "jline")
    }

    /**
     * Starts this server.
     */
    public void start() {
        consoleManager.setupConsole();
        
        // Load player lists
        opsList.load();
        whitelist.load();
        banManager.load();

        // Start loading plugins
        loadPlugins();

        // Begin registering permissions
        DefaultPermissions.registerCorePermissions();

        // Register these first so they're usable while the worlds are loading
        GlowCommandMap.initGlowPermissions(this);
        commandMap.register(new MeCommand(this));
        commandMap.register(new ColorCommand(this));
        commandMap.register(new KickCommand(this));
        commandMap.register(new ListCommand(this));
        commandMap.register(new TimeCommand(this));
        commandMap.register(new WhitelistCommand(this));
        commandMap.register(new BanCommand(this));
        commandMap.register(new GameModeCommand(this));
        commandMap.register(new OpCommand(this));
        commandMap.register(new DeopCommand(this));
        commandMap.register(new StopCommand(this));
        commandMap.register(new SaveCommand(this));
        commandMap.register(new SayCommand(this));
        commandMap.removeAllOfType(ReloadCommand.class);
        commandMap.register(new ReloadCommand(this));
        commandMap.register(new HelpCommand(this, commandMap.getKnownCommands(false)));

        enablePlugins(PluginLoadOrder.STARTUP);

        // Create worlds
        String world = config.getString(ServerConfig.Key.LEVEL_NAME);
        createWorld(WorldCreator.name(world).environment(Environment.NORMAL));
        if (getAllowNether()) {
            createWorld(WorldCreator.name(world + "_nether").environment(Environment.NETHER));
        }
        if (getAllowEnd()) {
            createWorld(WorldCreator.name(world + "_the_end").environment(Environment.THE_END));
        }

        // Finish loading plugins
        enablePlugins(PluginLoadOrder.POSTWORLD);
        commandMap.registerServerAliases();
        consoleManager.refreshCommands();
    }

    /**
     * Binds this server to the address specified in the configuration.
     */
    public void bind() {
        String ip = getIp();
        int port = getPort();

        SocketAddress address;
        if (ip.length() == 0) {
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(ip, port);
        }

        logger.log(Level.INFO, "Binding to address: {0}...", address);
        group.add(bootstrap.bind(address));
    }
    
    /**
     * Stops this server.
     */
    public void shutdown() {
        // This is so we don't run this twice (/stop and actual shutdown)
        if (isShuttingDown) return;
        isShuttingDown = true;
        logger.info("The server is shutting down...");
        
        // Stop scheduler and disable plugins
        scheduler.stop();
        pluginManager.clearPlugins();

        // Kick (and save) all players
        for (Player player : getOnlinePlayers()) {
            player.kickPlayer("Server shutting down.");
        }
        
        // Save worlds
        for (World world : getWorlds()) {
            unloadWorld(world, true);
        }
        storeQueue.end();
        
        // Gracefully stop Netty
        group.close();
        bootstrap.getFactory().releaseExternalResources();
        
        // And finally kill the console
        consoleManager.stop();
    }
    
    /**
     * Loads all plugins, calling onLoad, &c.
     */
    private void loadPlugins() {
        // clear the map
        commandMap.removeAllOfType(PluginCommand.class);

        File folder = new File(config.getString(ServerConfig.Key.PLUGIN_FOLDER));
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
     * Reloads the server, refreshing settings and plugin information
     */
    public void reload() {
        try {
            // Reload relevant configuration
            config.load();
            opsList.load();
            whitelist.load();
            
            // Reset crafting
            craftingManager.resetRecipes();
            
            // Load plugins
            loadPlugins();
            DefaultPermissions.registerCorePermissions();
            GlowCommandMap.initGlowPermissions(this);
            commandMap.registerAllPermissions();
            enablePlugins(PluginLoadOrder.STARTUP);
            enablePlugins(PluginLoadOrder.POSTWORLD);
            commandMap.registerServerAliases();
            consoleManager.refreshCommands();
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
     * Returns the list of OPs on this server.
     */
    public PlayerListFile getWhitelist() {
        return whitelist;
    }

    /**
     * Returns the folder where configuration files are stored
     */
    public File getConfigDir() {
        return ServerConfig.CONFIG_DIR;
    }

    public Set<OfflinePlayer> getOperators() {
        Set<OfflinePlayer> offlinePlayers = new HashSet<OfflinePlayer>();
        for (String name : opsList.getContents()) {
            offlinePlayers.add(getOfflinePlayer(name));
        }
        return offlinePlayers;
    }

    /**
     * Returns the currently used ban manager for the server
     */
    public BanManager getBanManager() {
        return banManager;
    }

    public void setBanManager(BanManager manager) {
        this.banManager = manager;
        manager.load();
        logger.log(Level.INFO, "Using {0} for ban management", manager.getClass().getName());
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
        return new ArrayList<World>(worlds);
    }
    
    /**
     * Gets a list of available commands from the command map.
     * @return A list of all commands at the time.
     */
    protected String[] getAllCommands() {
        HashSet<String> knownCommands = new HashSet<String>(commandMap.getKnownCommandNames());
        return knownCommands.toArray(new String[knownCommands.size()]);
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

    public String getBukkitVersion() {
        return getClass().getPackage().getSpecificationVersion();
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
        return result.toArray(new Player[result.size()]);
    }

    /**
     * Gets every player that has ever played on this server.
     *
     * @return Array containing all players
     */
    public OfflinePlayer[] getOfflinePlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ConsoleCommandSender getConsoleSender() {
        return consoleManager.getSender();
    }

    /**
     * Broadcast a message to all players.
     *
     * @param message the message
     * @return the number of players
     */
    public int broadcastMessage(String message) {
        return broadcast(message, BROADCAST_CHANNEL_USERS);
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

    public Player getPlayerExact(String name) {
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
        ConfigurationSection worlds = config.getSection("worlds");
        if (worlds != null && worlds.contains(name + ".generator")) {
            String[] args = worlds.getString(name + ".generator").split(":", 2);
            if (getPluginManager().getPlugin(args[0]) == null) {
                logger.log(Level.WARNING, "Plugin {0} specified for world {1} does not exist, using default.", new Object[]{args[0], name});
            } else {
                return getPluginManager().getPlugin(args[0]).getDefaultWorldGenerator(name, args.length == 2 ? args[1] : "");
            }
        }
        
        if (environment == Environment.NETHER) {
            return new net.glowstone.generator.UndergroundGenerator();
        } else if (environment == Environment.THE_END) {
            return new net.glowstone.generator.CakeTownGenerator();
        } else {
            return new net.glowstone.generator.SurfaceGenerator();
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
    @Deprecated
    public GlowWorld createWorld(String name, Environment environment) {
        return createWorld(WorldCreator.name(name).environment(environment));
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
    @Deprecated
    public GlowWorld createWorld(String name, Environment environment, long seed) {
        return createWorld(WorldCreator.name(name).environment(environment).seed(seed));
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
    @Deprecated
    public GlowWorld createWorld(String name, Environment environment, ChunkGenerator generator) {
        return createWorld(WorldCreator.name(name).environment(environment).generator(generator));
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
    @Deprecated
    public GlowWorld createWorld(String name, Environment environment, long seed, ChunkGenerator generator) {
        return createWorld(WorldCreator.name(name).environment(environment).seed(seed).generator(generator));
    }
    
    /**
     * Creates or loads a world with the given name using the specified options.
     * <p>
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(creator.name()).
     *
     * @param creator Options to use when creating the world
     * @return Newly created or loaded world
     */
    public GlowWorld createWorld(WorldCreator creator) {
        GlowWorld world = getWorld(creator.name());
        if (world != null) {
            return world;
        }

        if (creator.generator() == null) {
            creator.generator(getGenerator(creator.name(), creator.environment()));
        }

        world = new GlowWorld(this, creator.name(), creator.environment(), creator.seed(), new McRegionWorldStorageProvider(new File(getWorldContainer(), creator.name())), creator.generator());
        worlds.add(world);
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
        GlowWorld world = getWorld(name);
        if (world == null) return false;
        return unloadWorld(world, save);
    }

    /**
     * Unloads the given world.
     *
     * @param world The world to unload
     * @param save Whether to save the chunks before unloading.
     * @return Whether the action was Successful
     */
    public boolean unloadWorld(World world, boolean save) {
        if (!(world instanceof GlowWorld)) {
            return false;
        }
        if (save) {
            world.setAutoSave(false);
            ((GlowWorld) world).save(false);
        }
        if (worlds.contains((GlowWorld) world)) {
            worlds.remove((GlowWorld) world);
            ((GlowWorld) world).unload();
            EventFactory.onWorldUnload((GlowWorld)world);
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
     * @param commandLine command + arguments. Example: "test abc 123"
     * @return targetFound returns false if no target is found.
     * @throws CommandException Thrown when the executor for the given command fails with an unhandled exception
     */
    public boolean dispatchCommand(CommandSender sender, String commandLine) {
        try {
            if (commandMap.dispatch(sender, commandLine, false)) {
                return true;
            }

            if (getFuzzyCommandMatching()) {
                if (commandMap.dispatch(sender, commandLine, true)) {
                    return true;
                }
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
     * Populates a given {@link com.avaje.ebean.config.ServerConfig} with values attributes to this server
     *
     * @param dbConfig ServerConfig to populate
     */
    public void configureDbConfig(com.avaje.ebean.config.ServerConfig dbConfig) {
        com.avaje.ebean.config.DataSourceConfig ds = new com.avaje.ebean.config.DataSourceConfig();
        ConfigurationSection section = config.getSection("database");
        ds.setDriver(section.getString("driver", "org.sqlite.JDBC"));
        ds.setUrl(section.getString("url", "jdbc:sqlite:{DIR}{NAME}.db"));
        ds.setUsername(section.getString("username", "glow"));
        ds.setPassword(section.getString("password", "stone"));
        ds.setIsolationLevel(com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation.getLevel(section.getString("isolation", "SERIALIZABLE")));

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
        Map<String, String[]> aliases = new HashMap<String, String[]>();
        ConfigurationSection section = config.getSection("aliases");
        if (section == null) return aliases;
        List<String> cmdAliases = new ArrayList<String>();
        for (String key : section.getKeys(false)) {
            cmdAliases.clear();
            cmdAliases.addAll(section.getStringList(key));
            aliases.put(key, cmdAliases.toArray(new String[cmdAliases.size()]));
        }
        return aliases;
    }

    public void reloadCommandAliases() {
        commandMap.removeAllOfType(MultipleCommandAlias.class);
        commandMap.registerServerAliases();
    }

    public void setWhitelist(boolean enabled) {
        config.set(ServerConfig.Key.WHITELIST, enabled);
    }

    public Set<OfflinePlayer> getWhitelistedPlayers() {
        Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
        for (String name : whitelist.getContents()) {
            players.add(getOfflinePlayer(name));
        }
        return players;
     }

    public void reloadWhitelist() {
        whitelist.load();
    }

    public boolean getAllowFlight() {
        return config.getBoolean(ServerConfig.Key.ALLOW_FLIGHT);
    }

    public int broadcast(String message, String permission) {
        int count = 0;
        for (Permissible permissible : getPluginManager().getPermissionSubscriptions(permission)) {
            if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                ((CommandSender) permissible).sendMessage(message);
                ++count;
            }
        }
        return count;
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer player = getPlayerExact(name);
        if (player == null) {
            player = offlineCache.get(name);
            if (player == null) {
                player = new GlowOfflinePlayer(this, name);
                offlineCache.put(name, player);
                // Call creation event here?
            }
        } else {
            offlineCache.remove(name);
        }
        return player;
    }

    public Set<String> getIPBans() {
        return banManager.getIpBans();
    }

    public void banIP(String address) {
       banManager.setIpBanned(address, true);
    }

    public void unbanIP(String address) {
        banManager.setIpBanned(address, false);
    }

    public Set<OfflinePlayer> getBannedPlayers() {
        Set<OfflinePlayer> bannedPlayers = new HashSet<OfflinePlayer>();
        for (String name : banManager.getBans()) {
            bannedPlayers.add(getOfflinePlayer(name));
        }
        return bannedPlayers;
    }

    public GameMode getDefaultGameMode() {
        return defaultGameMode;
    }

    public void setDefaultGameMode(GameMode mode) {
        defaultGameMode = mode;
    }

    public GlowMapView getMap(short id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GlowMapView createMap(World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public StorageQueue getStorageQueue() {
        return storeQueue;
    }

    public boolean getFuzzyCommandMatching() {
        return config.getBoolean(ServerConfig.Key.FUZZY_COMMANDS);
    }

    public String getLogFile() {
        return "logs/log-%D.txt";
        //return config.getString(ServerConfig.Key.LOG_FILE);
    }

    public Warning.WarningState getWarningState() {
        return warnState;
    }

    // NEW STUFF

    @Override
    public List<Recipe> getRecipesFor(ItemStack result) {
        return null;
    }

    @Override
    public Iterator<Recipe> recipeIterator() {
        return null;
    }

    @Override
    public void clearRecipes() {

    }

    @Override
    public void resetRecipes() {

    }

    @Override
    public Messenger getMessenger() {
        return null;
    }

    @Override
    public HelpMap getHelpMap() {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size) {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size, String title) {
        return null;
    }

    @Override
    public boolean isPrimaryThread() {
        return false;
    }

    @Override
    public ItemFactory getItemFactory() {
        return null;
    }

    @Override
    public ScoreboardManager getScoreboardManager() {
        return null;
    }

    @Override
    public CachedServerIcon getServerIcon() {
        return null;
    }

    @Override
    public CachedServerIcon loadServerIcon(File file) throws IllegalArgumentException, Exception {
        return null;
    }

    @Override
    public CachedServerIcon loadServerIcon(BufferedImage image) throws IllegalArgumentException, Exception {
        return null;
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {

    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Configuration

    public String getIp() {
        return config.getString(ServerConfig.Key.SERVER_IP);
    }

    public int getPort() {
        return config.getInt(ServerConfig.Key.SERVER_PORT);
    }

    public String getServerName() {
        return config.getString(ServerConfig.Key.SERVER_NAME);
    }

    public String getServerId() {
        return Integer.toHexString(getServerName().hashCode());
    }

    public int getMaxPlayers() {
        return config.getInt(ServerConfig.Key.MAX_PLAYERS);
    }

    public String getUpdateFolder() {
        return config.getString(ServerConfig.Key.UPDATE_FOLDER);
    }

    public File getUpdateFolderFile() {
        return new File(getUpdateFolder());
    }

    public int getSpawnRadius() {
        return config.getInt(ServerConfig.Key.SPAWN_RADIUS);
    }

    public void setSpawnRadius(int value) {
        config.set(ServerConfig.Key.SPAWN_RADIUS, value);
    }

    public boolean getOnlineMode() {
        return config.getBoolean(ServerConfig.Key.ONLINE_MODE);
    }

    public boolean getAllowNether() {
        return config.getBoolean(ServerConfig.Key.ALLOW_NETHER);
    }

    public boolean getAllowEnd() {
        return config.getBoolean(ServerConfig.Key.ALLOW_END);
    }

    public boolean hasWhitelist() {
        return config.getBoolean(ServerConfig.Key.WHITELIST);
    }

    public int getViewDistance() {
        return config.getInt(ServerConfig.Key.VIEW_DISTANCE);
    }

    public String getMotd() {
        return config.getString(ServerConfig.Key.MOTD);
    }

    public File getWorldContainer() {
        return new File(config.getString(ServerConfig.Key.WORLD_FOLDER));
    }

    public String getWorldType() {
        return config.getString(ServerConfig.Key.LEVEL_TYPE);
    }

    public boolean getGenerateStructures() {
        return config.getBoolean(ServerConfig.Key.GENERATE_STRUCTURES);
    }

    public long getConnectionThrottle() {
        return config.getInt(ServerConfig.Key.CONNECTION_THROTTLE);
    }

    public int getTicksPerAnimalSpawns() {
        return config.getInt(ServerConfig.Key.ANIMAL_TICKS);
    }

    public int getTicksPerMonsterSpawns() {
        return config.getInt(ServerConfig.Key.MONSTER_TICKS);
    }

    public boolean isHardcore() {
        return config.getBoolean(ServerConfig.Key.HARDCORE);
    }

    public boolean useExactLoginLocation() {
        return config.getBoolean(ServerConfig.Key.EXACT_LOGIN_LOCATION);
    }

    public int getMonsterSpawnLimit() {
        return config.getInt(ServerConfig.Key.MONSTER_LIMIT);
    }

    public int getAnimalSpawnLimit() {
        return config.getInt(ServerConfig.Key.ANIMAL_LIMIT);
    }

    public int getWaterAnimalSpawnLimit() {
        return config.getInt(ServerConfig.Key.WATER_ANIMAL_LIMIT);
    }

    public int getAmbientSpawnLimit() {
        return config.getInt(ServerConfig.Key.AMBIENT_LIMIT);
    }

    public String getShutdownMessage() {
        return config.getString(ServerConfig.Key.SHUTDOWN_MESSAGE);
    }
}
