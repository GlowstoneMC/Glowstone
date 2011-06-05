package net.glowstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
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
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;

import net.glowstone.io.NbtChunkIoService;
import net.glowstone.net.MinecraftPipelineFactory;
import net.glowstone.net.Session;
import net.glowstone.net.SessionRegistry;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.util.PlayerListFile;
import net.glowstone.world.*;

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
	public static final Logger logger = Logger.getLogger(GlowServer.class.getName());
            
    /**
     * The configuration the server uses.
     */
    private static final Properties properties = new Properties();

	/**
	 * Creates a new server on TCP port 25565 and starts listening for
	 * connections.
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		try {
            File props = new File("server.properties");
            if (props.exists()) {
                properties.load(new FileInputStream(props));
            } else {
                properties.setProperty("server-port", "25565");
                properties.save(new FileOutputStream(props), "Glowstone server properties");
            }
            int port = Integer.valueOf(properties.getProperty("server-port", "25565"));
            
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
     * The list of OPs on the server.
     */
    private final PlayerListFile opsList = new PlayerListFile("ops.txt");
    
    /**
     * The plugin manager of this server.
     */
    private final SimplePluginManager pluginManager = new SimplePluginManager(this);
    
    /**
     * The services manager of this server.
     */
    private final SimpleServicesManager servicesManager = new SimpleServicesManager();
    
    /**
     * The command map of this server.
     */
    private final SimpleCommandMap commandMap = new SimpleCommandMap(this);

	/**
	 * The task scheduler used by this server.
	 */
	private final GlowScheduler scheduler = new GlowScheduler(this);

	/**
	 * The world this server is managing.
	 */
	private final ArrayList<GlowWorld> worlds = new ArrayList<GlowWorld>();

	/**
	 * Creates a new server.
	 */
	public GlowServer() {
		logger.info("Starting Glowstone...");
		init();
	}

	/**
	 * Initializes the channel and pipeline factories.
	 */
	private void init() {
        Bukkit.setServer(this);
        
        worlds.add(new GlowWorld(new NbtChunkIoService(), new ForestWorldGenerator()));
        
		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new MinecraftPipelineFactory(this);
		bootstrap.setPipelineFactory(pipelineFactory);
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
        reload();
        
		logger.info("Ready for connections.");
	}

    /**
     * Reloads the server, refreshing settings and plugin information
     */
    public void reload() {
        try {
            properties.load(new FileInputStream(new File("server.properties")));
            opsList.load();
            
            File folder = new File(properties.getProperty("plugin-folder", "plugins"));
            folder.mkdirs();
            
            // clear and reregister our commands
            commandMap.clearCommands();
            commandMap.register("glowstone", new net.glowstone.command.OpCommand(this));
            commandMap.register("glowstone", new net.glowstone.command.DeopCommand(this));
            commandMap.register("glowstone", new net.glowstone.command.ListCommand(this));
            
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
            
            // register plugin commands
            for (Plugin plugin : plugins) {
                List<Command> commands = PluginCommandYamlParser.parse(plugin);
                commandMap.registerAll(plugin.getDescription().getName(), commands);
            }
            
            // enable plugins
            for (Plugin plugin : plugins) {
                try {
                    pluginManager.enablePlugin(plugin);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error enabling {0}: {1}", new Object[]{plugin.getDescription().getName(), ex.getMessage()});
                    ex.printStackTrace();
                }
            }
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
        return "git-Glowstone-unknown";
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
        return Integer.valueOf(properties.getProperty("max-players", "0"));
    }

    /**
     * Gets the port the server listens on.
     * @return The port number the server is listening on.
     */
    public int getPort() {
        return Integer.valueOf(properties.getProperty("server-port", "25565"));
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
        return properties.getProperty("update-folder", "update");
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
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @return Newly created or loaded World
     */
    public GlowWorld createWorld(String name, Environment environment) {
        return createWorld(name, environment, new Random().nextLong());
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
        if (getWorld(name) != null) return getWorld(name);
        throw new UnsupportedOperationException("Not supported yet.");
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
            String[] args = commandLine.split(" +");
            String commandName = args[0];
            
            String[] newargs = new String[args.length - 1];
            for (int i = 1; i < args.length; ++i) {
                newargs[i - 1] = args[i];
            }
            
            Command command = commandMap.getCommand(commandName);
            if (command == null)
                return false;
            return command.execute(sender, commandName, newargs);
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
     * @param config ServerConfig to populate
     */
    public void configureDbConfig(com.avaje.ebean.config.ServerConfig config) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Adds a recipe to the crafting manager.
     * @param recipe The recipe to add.
     * @return True to indicate that the recipe was added.
     */
    public boolean addRecipe(Recipe recipe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
