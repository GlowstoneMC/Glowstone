package net.glowstone.command.glowstone;

import com.google.common.base.Preconditions;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import net.glowstone.GlowWorld;
import net.glowstone.ServerProvider;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.minecraft.GlowVanillaCommand;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.LocalizedStringImpl;
import net.glowstone.util.ReflectionProcessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NonNls;

public class GlowstoneCommand extends GlowVanillaCommand {

    private static final @NonNls List<String> SUBCOMMANDS
            = Arrays.asList("about", "chunk", "eval", "help", "property", "vm", "world");

    /**
     * Creates the instance for this command.
     */
    public GlowstoneCommand() {
        super("glowstone", Arrays.asList("gs"));
        setPermission("glowstone.debug"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("about")) {
            ResourceBundle b = commandMessages.getResourceBundle();
            // some info about this Glowstone server
            new LocalizedStringImpl("glowstone.about", b).send(sender);
            LocalizedStringImpl t
                    = new LocalizedStringImpl("glowstone.about._template", b);
            sendBullet(sender, t, b, "glowstone.about.brand", Bukkit.getName());
            sendBullet(sender, t, b, "glowstone.about.name", Bukkit.getServerName());
            sendBullet(sender, t, b, "glowstone.about.version", Bukkit.getVersion());
            sendBullet(sender, t, b, "glowstone.about.api-version", Bukkit.getBukkitVersion());
            sendBullet(sender, t, b, "glowstone.about.players",
                    Bukkit.getOnlinePlayers().size());
            sendBullet(sender, t, b, "glowstone.about.worlds", Bukkit.getWorlds().size());
            sendBullet(sender, t, b, "glowstone.about.plugins",
                    Bukkit.getPluginManager().getPlugins().length);

            // thread count
            int threadCount = 0;
            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            for (Thread thread : threads) {
                if (thread.getThreadGroup() == Thread.currentThread().getThreadGroup()) {
                    threadCount++;
                }
            }
            sender.sendMessage(" - " + ChatColor.GOLD + "Threads: " + ChatColor.AQUA + threadCount
                    + ChatColor.RESET + ".");
            return false;
        }
        if ("help".equalsIgnoreCase(args[0])) {
            // some help
            sender.sendMessage(ChatColor.GOLD + "Glowstone command help:");
            sender.sendMessage(helpForSubCommand(label, "about", "Information about this server"));
            sender.sendMessage(helpForSubCommand(label, "eval <eval>", "Evaluate a reflection "
                    + "string"));
            sender.sendMessage(helpForSubCommand(label, "help", "Shows the help screen"));
            sender.sendMessage(helpForSubCommand(label, "property [name]", "Lists or gets system "
                    + "properties"));
            sender.sendMessage(helpForSubCommand(label, "chunk", "Gets the coordinates of the "
                    + "current chunk"));
            sender.sendMessage(helpForSubCommand(label, "vm", "Lists JVM options"));
            sender.sendMessage(helpForSubCommand(label, "world [teleportTo]", "Lists or teleports"
                    + " to worlds"));
            return false;
        }
        if ("property".equalsIgnoreCase(args[0])) {
            if (args.length == 1) {
                // list all
                System.getProperties().forEach((key, value) -> sender.sendMessage(
                        "Property '" + ChatColor.AQUA + key + ChatColor.RESET + "' = \""
                                + ChatColor.GOLD + value + ChatColor.RESET + "\"."));
            } else {
                // get a property
                String key = args[1].toLowerCase();
                String value = System.getProperty(key);
                if (value == null) {
                    sender.sendMessage(ChatColor.RED + "Unknown system property '" + key + "'.");
                } else {
                    sender.sendMessage(
                            "Property '" + ChatColor.AQUA + key + ChatColor.RESET + "' = \""
                                    + ChatColor.GOLD + value + ChatColor.RESET + "\".");
                }
            }
            return false;
        }
        if ("vm".equalsIgnoreCase(args[0])) {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMxBean.getInputArguments();
            if (arguments.size() == 0) {
                sender.sendMessage("There are no VM arguments.");
            } else {
                sender.sendMessage("Glowstone VM arguments (" + arguments.size() + "):");
                for (String argument : arguments) {
                    sender.sendMessage(" - '" + ChatColor.AQUA + argument + ChatColor.RESET + "'.");
                }
            }

            return false;
        }
        if ("world".equalsIgnoreCase(args[0]) || "worlds".equalsIgnoreCase(args[0])) {
            if (args.length == 1) {
                // list worlds
                sender.sendMessage(
                        "Worlds: " + CommandUtils
                                .prettyPrint(getWorldNames().toArray(new String[0])));
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can switch worlds.");
                return false;
            }
            GlowPlayer player = (GlowPlayer) sender;
            String worldName = args[1];
            GlowWorld world = player.getServer().getWorld(worldName);
            if (world == null) {
                sender.sendMessage(
                        ChatColor.RED + "World '" + worldName
                                + "' is not loaded, or does not exist");
                return false;
            }
            player.teleport(world.getSpawnLocation());
            player.sendMessage("Teleported to world '" + world.getName() + "'.");
            return true;
        }
        if ("chunk".equalsIgnoreCase(args[0])) {
            if (!CommandUtils.isPhysical(sender)) {
                sender.sendMessage(
                        ChatColor.RED + "This command may only be used by physical objects");
                return false;
            }
            Chunk chunk = CommandUtils.getLocation(sender).getChunk();
            sender
                    .sendMessage(
                            "Chunk coordinates: [x=" + chunk.getX() + ", z=" + chunk.getZ() + "]");
            return true;
        }
        if ("eval".equalsIgnoreCase(args[0])) {
            if (args.length == 1) {
                // no args, send usage
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " eval <eval>");
                return false;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i] + (i == args.length - 1 ? "" : " "));
            }
            ReflectionProcessor processor = new ReflectionProcessor(builder.toString(),
                    sender instanceof Entity ? sender : ServerProvider.getServer());
            Object result = processor.process();
            sender.sendMessage(
                    ChatColor.GOLD + "Eval returned: " + (result == null ? ChatColor.RED
                            + "<no value>"
                            : ChatColor.AQUA + result.toString()));
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <"
                + SUBCOMMANDS.stream().collect(Collectors.joining("|")) + ">");
        return false;
    }

    private static void sendBullet(CommandSender sender,
            LocalizedStringImpl template, ResourceBundle resourceBundle, @NonNls String key,
            Object value) {
        template.send(sender, new LocalizedStringImpl(key, resourceBundle), value);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        Preconditions.checkNotNull(sender, "Sender cannot be null");
        Preconditions.checkNotNull(args, "Arguments cannot be null");
        Preconditions.checkNotNull(alias, "Alias cannot be null");
        if (args.length == 0) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return StringUtil
                    .copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList<>(SUBCOMMANDS.size()));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("property")) {
            return StringUtil
                    .copyPartialMatches(args[1], System.getProperties().stringPropertyNames(),
                            new ArrayList<>(System.getProperties().stringPropertyNames().size()));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("world") || args[0]
                .equalsIgnoreCase("worlds")) && sender instanceof Player) {
            Collection<String> worlds = getWorldNames();
            return StringUtil
                    .copyPartialMatches(args[1], worlds, new ArrayList<>(worlds.size()));
        }
        return Collections.emptyList();
    }

    private String helpForSubCommand(String label, String subcommand, String description) {
        return "- " + ChatColor.GOLD + "/" + label + " "
                + ChatColor.AQUA + subcommand
                + ChatColor.GRAY + ": " + description;
    }

    private Collection<String> getWorldNames() {
        return ServerProvider.getServer().getWorlds().stream().map(World::getName)
                .collect(Collectors.toList());
    }
}
