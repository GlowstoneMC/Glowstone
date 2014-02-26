package net.glowstone;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.glowstone.command.ColorCommand;
import net.glowstone.inventory.CraftingManager;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.map.GlowMapView;
import net.glowstone.net.GlowNetworkServer;
import net.glowstone.net.SessionRegistry;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.scheduler.WorldScheduler;
import net.glowstone.util.*;
import net.glowstone.util.bans.GlowBanList;
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
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.permissions.DefaultPermissions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
     * The game version supported by the server.
     */
    public static final String GAME_VERSION = "1.7.2";

    /**
     * The protocol version supported by the server.
     */
    public static final int PROTOCOL_VERSION = 4;

    /**
     * Creates a new server on TCP port 25565 and starts listening for
     * connections.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        try {
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
     * A list of all the active {@link net.glowstone.net.GlowSession}s.
     */
    private final SessionRegistry sessions = new SessionRegistry();
    
    /**
     * The console manager of this server.
     */
    private final ConsoleManager consoleManager = new ConsoleManager(this);
    
    /**
     * The services manager of this server.
     */
    private final SimpleServicesManager servicesManager = new SimpleServicesManager();

    /**
     * The command map of this server.
     */
    private final SimpleCommandMap commandMap = new SimpleCommandMap(this);

    /**
     * The plugin manager of this server.
     */
    private final PluginManager pluginManager = new SimplePluginManager(this, commandMap);

    /**
     * The plugin channel messenger for the server.
     */
    private final Messenger messenger = new StandardMessenger();

    /**
     * The help map for the server.
     */
    private final GlowHelpMap helpMap = new GlowHelpMap(this);

    /**
     * The scoreboard manager for the server.
     */
    private final ScoreboardManager scoreboardManager = null;
    
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
     * The BanList for player names.
     */
    private GlowBanList nameBans = new GlowBanList(this, BanList.Type.NAME);

    /**
     * The BanList for IP addresses.
     */
    private GlowBanList ipBans = new GlowBanList(this, BanList.Type.IP);

    /**
     * The world this server is managing.
     */
    private final WorldScheduler worlds = new WorldScheduler();

    /**
     * The task scheduler used by this server.
     */
    private final GlowScheduler scheduler = new GlowScheduler(this, worlds);

    /**
     * The Bukkit UnsafeValues implementation.
     */
    private final UnsafeValues unsafeAccess = new GlowUnsafeValues();

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
     * Whether the whitelist is in effect.
     */
    private boolean whitelistEnabled;

    /**
     * The size of the area to keep protected around the spawn point.
     */
    private int spawnRadius;

    /**
     * The ticks until a player who has not played the game has been kicked, or 0.
     */
    private int idleTimeout;

    /**
     * A cache of existing OfflinePlayers
     */
    private final Map<String, OfflinePlayer> offlineCache = new ConcurrentHashMap<String, OfflinePlayer>();

    /**
     * A RSA key pair used for encryption and authentication
     */
    private final KeyPair keyPair = SecurityUtils.generateKeyPair();

    /**
     * The network server used for network communication
     */
    private final GlowNetworkServer networkServer = new GlowNetworkServer(this);

    /**
     * The default icon, usually blank, used for the server list.
     */
    private GlowServerIcon defaultIcon;

    /**
     * Creates a new server.
     */
    public GlowServer() {
        Bukkit.setServer(this);
        loadConfig();
    }

    /**
     * Starts this server.
     */
    public void start() {
        // Determine console mode and start reading input
        consoleManager.startConsole(config.getBoolean(ServerConfig.Key.USE_JLINE));
        consoleManager.startFile(config.getString(ServerConfig.Key.LOG_FILE));
        
        // Load player lists
        opsList.load();
        whitelist.load();
        nameBans.load();
        ipBans.load();

        // Start loading plugins
        loadPlugins();
        enablePlugins(PluginLoadOrder.STARTUP);

        // Create worlds
        String name = config.getString(ServerConfig.Key.LEVEL_NAME);
        String seedString = config.getString(ServerConfig.Key.LEVEL_SEED);
        long seed = seedString.isEmpty() ? System.currentTimeMillis() : seedString.hashCode();
        boolean structs = getGenerateStructures();
        WorldType type = WorldType.getByName(getWorldType());
        if (type == null) {
            type = WorldType.NORMAL;
        }

        createWorld(WorldCreator.name(name).environment(Environment.NORMAL).seed(seed).type(type).generateStructures(structs));
        if (getAllowNether()) {
            createWorld(WorldCreator.name(name + "_nether").environment(Environment.NETHER).seed(seed).type(type).generateStructures(structs));
        }
        if (getAllowEnd()) {
            createWorld(WorldCreator.name(name + "_the_end").environment(Environment.THE_END).seed(seed).type(type).generateStructures(structs));
        }

        // Finish loading plugins
        enablePlugins(PluginLoadOrder.POSTWORLD);
        commandMap.registerServerAliases();
        scheduler.start();
    }

    /**
     * Binds this server to the address specified in the configuration.
     */
    private void bind() {
        String ip = getIp();
        int port = getPort();

        SocketAddress address;
        if (ip.length() == 0) {
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(ip, port);
        }

        logger.log(Level.INFO, "Binding to address: {0}...", address);
        ChannelFuture future = networkServer.bind(address);
        Channel channel = future.awaitUninterruptibly().channel();
        if (!channel.isActive()) {
            throw new RuntimeException("Failed to bind to address. Maybe it is already in use?");
        }
    }
    
    /**
     * Stops this server.
     */
    public void shutdown() {
        // Just in case this gets called twice
        if (isShuttingDown) return;
        isShuttingDown = true;
        logger.info("The server is shutting down...");
        
        // Disable plugins
        pluginManager.clearPlugins();

        // Kick all players (this saves their data too)
        for (Player player : getOnlinePlayers()) {
            player.kickPlayer("Server shutting down.");
        }

        // Stop the network server - starts the shutdown process
        // It may take a second or two for Netty to totally clean up
        networkServer.shutdown();
        
        // Save worlds
        for (World world : getWorlds()) {
            logger.info("Saving world: " + world.getName());
            unloadWorld(world, true);
        }

        // Stop scheduler and console
        scheduler.stop();
        consoleManager.stop();

        // Wait for a while and terminate any rogue threads
        new ShutdownMonitorThread().start();
    }

    /**
     * Load the server configuration.
     */
    private void loadConfig() {
        config.load();

        // modifiable values
        spawnRadius = config.getInt(ServerConfig.Key.SPAWN_RADIUS);
        whitelistEnabled = config.getBoolean(ServerConfig.Key.WHITELIST);
        idleTimeout = config.getInt(ServerConfig.Key.PLAYER_IDLE_TIMEOUT);
        craftingManager.initialize();

        // special handling
        warnState = Warning.WarningState.value(config.getString(ServerConfig.Key.WARNING_STATE));
        try {
            defaultGameMode = GameMode.valueOf(GameMode.class, config.getString(ServerConfig.Key.GAMEMODE));
        } catch (IllegalArgumentException | NullPointerException e) {
            defaultGameMode = GameMode.SURVIVAL;
        }

        // server icon
        defaultIcon = new GlowServerIcon();
        try {
            File file = new File(ServerConfig.CONFIG_DIR, "server-icon.png");
            if (file.isFile()) {
                defaultIcon = new GlowServerIcon(file);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load server-icon.png", e);
        }
    }
    
    /**
     * Loads all plugins, calling onLoad, &c.
     */
    private void loadPlugins() {
        // clear the map
        commandMap.clearCommands();
        commandMap.setFallbackCommands();
        commandMap.register("glowstone", new ColorCommand("colors"));

        File folder = new File(config.getString(ServerConfig.Key.PLUGIN_FOLDER));
        if (!folder.isDirectory() && !folder.mkdirs()) {
            logger.log(Level.SEVERE, "Could not create plugins directory: " + folder);
        }

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
    private void enablePlugins(PluginLoadOrder type) {
        if (type == PluginLoadOrder.STARTUP) {
            helpMap.clear();
            helpMap.initializeGeneralTopics();
        }

        // load all the plugins
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

        if (type == PluginLoadOrder.POSTWORLD) {
            commandMap.registerServerAliases();
            // permissions.yml should be loaded here
            DefaultPermissions.registerCorePermissions();
            helpMap.initializeCommands();
        }
    }

    /**
     * Reloads the server, refreshing settings and plugin information
     */
    public void reload() {
        try {
            // Reload relevant configuration
            loadConfig();
            opsList.load();
            whitelist.load();
            nameBans.load();
            ipBans.load();
            
            // Reset crafting
            craftingManager.resetRecipes();
            
            // Load plugins
            loadPlugins();
            enablePlugins(PluginLoadOrder.STARTUP);
            enablePlugins(PluginLoadOrder.POSTWORLD);
            commandMap.registerServerAliases();
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, "Uncaught error while reloading: {0}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access to internals

    /**
     * Gets the command map.
     * @return The {@link SimpleCommandMap}.
     */
    public SimpleCommandMap getCommandMap() {
        return commandMap;
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

    /**
     * Return the crafting manager.
     * @return The server's crafting manager.
     */
    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    /**
     * The key pair generated at server start up
     * @return The key pair generated at server start up
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Static server properties

    public String getName() {
        return "Glowstone";
    }

    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    public String getBukkitVersion() {
        return getClass().getPackage().getSpecificationVersion();
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isPrimaryThread() {
        return scheduler.isPrimaryThread();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access to Bukkit API

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public GlowScheduler getScheduler() {
        return scheduler;
    }

    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public HelpMap getHelpMap() {
        return helpMap;
    }

    public ItemFactory getItemFactory() {
        return GlowItemFactory.instance();
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Deprecated
    public UnsafeValues getUnsafe() {
        return unsafeAccess;
    }

    public BanList getBanList(BanList.Type type) {
        switch (type) {
            case NAME:
                return nameBans;
            case IP:
                return ipBans;
            default:
                throw new IllegalArgumentException("Unknown BanList type " + type);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Commands and console

    public ConsoleCommandSender getConsoleSender() {
        return consoleManager.getSender();
    }

    public PluginCommand getPluginCommand(String name) {
        Command command = commandMap.getCommand(name);
        if (command instanceof PluginCommand) {
            return (PluginCommand) command;
        } else {
            return null;
        }
    }

    public Map<String, String[]> getCommandAliases() {
        Map<String, String[]> aliases = new HashMap<String, String[]>();
        ConfigurationSection section = config.getAliases();
        if (section == null) return aliases;
        for (String key : section.getKeys(false)) {
            List<String> list = section.getStringList(key);
            aliases.put(key, list.toArray(new String[list.size()]));
        }
        return aliases;
    }

    public void reloadCommandAliases() {
        //commandMap.removeAllOfType(MultipleCommandAlias.class);
        commandMap.registerServerAliases();
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) throws CommandException {
        if (commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        String firstword = commandLine;
        if (firstword.indexOf(' ') >= 0) {
            firstword = firstword.substring(0, firstword.indexOf(' '));
        }

        sender.sendMessage(ChatColor.GRAY + "Unknown command \"" + firstword + "\", try \"help\"");
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player management

    public Set<OfflinePlayer> getOperators() {
        Set<OfflinePlayer> offlinePlayers = new HashSet<OfflinePlayer>();
        for (String name : opsList.getContents()) {
            offlinePlayers.add(getOfflinePlayer(name));
        }
        return offlinePlayers;
    }

    public Player[] getOnlinePlayers() {
        ArrayList<Player> result = new ArrayList<Player>();
        for (World world : getWorlds()) {
            for (Player player : world.getPlayers())
                result.add(player);
        }
        return result.toArray(new Player[result.size()]);
    }

    public OfflinePlayer[] getOfflinePlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Player getPlayer(String name) {
        name = name.toLowerCase();
        Player bestPlayer = null;
        int bestDelta = -1;
        for (Player player : getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(name)) {
                int delta = player.getName().length() - name.length();
                if (bestPlayer == null || delta < bestDelta) {
                    bestPlayer = player;
                }
            }
        }
        return bestPlayer;
    }

    public Player getPlayerExact(String name) {
        for (Player player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }

    public List<Player> matchPlayer(String name) {
        name = name.toLowerCase();

        ArrayList<Player> result = new ArrayList<Player>();
        for (Player player : getOnlinePlayers()) {
            String lower = player.getName().toLowerCase();
            if (lower.equals(name)) {
                result.clear();
                result.add(player);
                break;
            } else if (lower.contains(name)) {
                result.add(player);
            }
        }
        return result;
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

    public void savePlayers() {
        for (Player player : getOnlinePlayers())
            player.saveData();
    }

    public int broadcastMessage(String message) {
        return broadcast(message, BROADCAST_CHANNEL_USERS);
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

    public Set<String> getIPBans() {
        Set<String> result = new HashSet<>();
        for (BanEntry entry : ipBans.getBanEntries()) {
            result.add(entry.getTarget());
        }
        return result;
    }

    public void banIP(String address) {
        ipBans.addBan(address, null, null, null);
    }

    public void unbanIP(String address) {
        ipBans.pardon(address);
    }

    public Set<OfflinePlayer> getBannedPlayers() {
        Set<OfflinePlayer> bannedPlayers = new HashSet<>();
        for (BanEntry entry : nameBans.getBanEntries()) {
            bannedPlayers.add(getOfflinePlayer(entry.getTarget()));
        }
        return bannedPlayers;
    }

    ////////////////////////////////////////////////////////////////////////////
    // World management

    public GlowWorld getWorld(String name) {
        return worlds.getWorld(name);
    }

    public GlowWorld getWorld(UUID uid) {
        for (GlowWorld world : worlds.getWorlds()) {
            if (uid.equals(world.getUID()))
                return world;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<World> getWorlds() {
        // Shenanigans needed to cast List<GlowWorld> to List<World>
        return (List) worlds.getWorlds();
    }

    /**
     * Gets the default ChunkGenerator for the given environment and type.
     * @return The ChunkGenerator.
     */
    private ChunkGenerator getGenerator(String name, Environment environment, WorldType type) {
        // find generator based on configuration
        ConfigurationSection worlds = config.getWorlds();
        if (worlds != null) {
            String genName = worlds.getString(name + ".generator", null);
            ChunkGenerator generator = WorldCreator.getGeneratorForName(name, genName, getConsoleSender());
            if (generator != null) {
                return generator;
            }
        }

        // find generator based on environment and world type
        if (environment == Environment.NETHER) {
            return new net.glowstone.generator.UndergroundGenerator();
        } else if (environment == Environment.THE_END) {
            return new net.glowstone.generator.CakeTownGenerator();
        } else {
            return new net.glowstone.generator.SurfaceGenerator();
        }
    }

    public GlowWorld createWorld(WorldCreator creator) {
        GlowWorld world = getWorld(creator.name());
        if (world != null) {
            return world;
        }

        if (creator.generator() == null) {
            creator.generator(getGenerator(creator.name(), creator.environment(), creator.type()));
        }

        world = new GlowWorld(this, creator);
        return worlds.addWorld(world);
    }

    public boolean unloadWorld(String name, boolean save) {
        GlowWorld world = getWorld(name);
        return world != null && unloadWorld(world, save);
    }

    public boolean unloadWorld(World bWorld, boolean save) {
        if (!(bWorld instanceof GlowWorld)) {
            return false;
        }
        GlowWorld world = (GlowWorld) bWorld;
        if (save) {
            world.setAutoSave(false);
            world.save(false);
        }
        if (worlds.removeWorld(world)) {
            world.unload();
            EventFactory.onWorldUnload(world);
            return true;
        }
        return false;
    }

    public GlowMapView getMap(short id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GlowMapView createMap(World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inventory and crafting

    @Override
    public List<Recipe> getRecipesFor(ItemStack result) {
        return craftingManager.getRecipesFor(result);
    }

    @Override
    public Iterator<Recipe> recipeIterator() {
        return craftingManager.iterator();
    }

    public boolean addRecipe(Recipe recipe) {
        return craftingManager.addRecipe(recipe);
    }

    @Override
    public void clearRecipes() {
        craftingManager.clearRecipes();
    }

    @Override
    public void resetRecipes() {
        craftingManager.resetRecipes();
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return new GlowInventory(owner, type);
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size) {
        return new GlowInventory(owner, InventoryType.CHEST, size);
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size, String title) {
        return new GlowInventory(owner, InventoryType.CHEST, size, title);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Server icons

    @Override
    public GlowServerIcon getServerIcon() {
        return defaultIcon;
    }

    @Override
    public CachedServerIcon loadServerIcon(File file) throws Exception {
        return new GlowServerIcon(file);
    }

    @Override
    public CachedServerIcon loadServerIcon(BufferedImage image) throws Exception {
        return new GlowServerIcon(image);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Plugin messages

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(getMessenger(), source, channel, message);
        for (Player player : getOnlinePlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        HashSet<String> result = new HashSet<String>();
        for (Player player : getOnlinePlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Configuration with special handling

    public GameMode getDefaultGameMode() {
        return defaultGameMode;
    }

    public void setDefaultGameMode(GameMode mode) {
        defaultGameMode = mode;
    }

    public int getSpawnRadius() {
        return spawnRadius;
    }

    public void setSpawnRadius(int value) {
        spawnRadius = value;
    }

    public boolean hasWhitelist() {
        return whitelistEnabled;
    }

    public void setWhitelist(boolean enabled) {
        whitelistEnabled = enabled;
    }

    public Warning.WarningState getWarningState() {
        return warnState;
    }

    public void setIdleTimeout(int timeout) {
        idleTimeout = timeout;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void configureDbConfig(com.avaje.ebean.config.ServerConfig dbConfig) {
        com.avaje.ebean.config.DataSourceConfig ds = new com.avaje.ebean.config.DataSourceConfig();
        ds.setDriver(config.getString(ServerConfig.Key.DB_DRIVER));
        ds.setUrl(config.getString(ServerConfig.Key.DB_URL));
        ds.setUsername(config.getString(ServerConfig.Key.DB_USERNAME));
        ds.setPassword(config.getString(ServerConfig.Key.DB_PASSWORD));
        ds.setIsolationLevel(com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation.getLevel(config.getString(ServerConfig.Key.DB_ISOLATION)));

        if (ds.getDriver().contains("sqlite")) {
            dbConfig.setDatabasePlatform(new com.avaje.ebean.config.dbplatform.SQLitePlatform());
            dbConfig.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        dbConfig.setDataSourceConfig(ds);
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

    public boolean getOnlineMode() {
        return config.getBoolean(ServerConfig.Key.ONLINE_MODE);
    }

    public boolean getAllowNether() {
        return config.getBoolean(ServerConfig.Key.ALLOW_NETHER);
    }

    public boolean getAllowEnd() {
        return config.getBoolean(ServerConfig.Key.ALLOW_END);
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

    public boolean getAllowFlight() {
        return config.getBoolean(ServerConfig.Key.ALLOW_FLIGHT);
    }
}
