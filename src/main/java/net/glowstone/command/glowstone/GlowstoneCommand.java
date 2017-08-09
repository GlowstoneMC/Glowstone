package net.glowstone.command.glowstone;

import com.google.common.base.Preconditions;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.ReflectionProcessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.stream.Collectors;

public class GlowstoneCommand extends BukkitCommand {

    private static final List<String> SUBCOMMANDS = Arrays.asList("about", "eval", "help", "property", "vm", "world");

    public GlowstoneCommand() {
        super("glowstone", GlowServer.lang.getString("command.glowstone.description"), "/glowstone help", Arrays.asList("gs"));
        setPermission("glowstone.debug");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("about"))) {
            // some info about this Glowstone server
            sender.sendMessage(GlowServer.lang.getString(sender, "command.glowstone.about.title"));
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.brand",
                ChatColor.AQUA + Bukkit.getName() + ChatColor.RESET));
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.name",
                ChatColor.AQUA + Bukkit.getServerName() + ChatColor.RESET));
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.version.glowstone",
                ChatColor.AQUA + Bukkit.getVersion() + ChatColor.RESET));
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.version.api",
                ChatColor.AQUA + Bukkit.getBukkitVersion() + ChatColor.RESET));
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.players",
                ChatColor.AQUA + "" + Bukkit.getOnlinePlayers().size() + ChatColor.RESET));
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.worlds",
                ChatColor.AQUA + "" + Bukkit.getWorlds().size() + ChatColor.RESET));
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.plugins",
                ChatColor.AQUA + "" + Bukkit.getPluginManager().getPlugins().length + ChatColor.RESET));

            // thread count
            int threadCount = 0;
            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            for (Thread t : threads) {
                if (t.getThreadGroup() == Thread.currentThread().getThreadGroup()) {
                    threadCount++;
                }
            }
            sender.sendMessage(" - " + ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.about.threads", ChatColor.AQUA + "" + threadCount + ChatColor.RESET));
            return false;
        }
        if (args[0].equalsIgnoreCase("help")) {
            // some help
            return false;
        }
        if (args[0].equalsIgnoreCase("property")) {
            if (args.length == 1) {
                // list all
                System.getProperties().forEach((key, value) -> sender.sendMessage(GlowServer.lang.getString(sender, "command.glowstone.property.result", ChatColor.AQUA + "" + key + ChatColor.RESET, ChatColor.GOLD + "" + value + ChatColor.RESET)));
            } else {
                // get a property
                String key = args[1].toLowerCase();
                String value = System.getProperty(key);
                if (value == null) {
                    sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.glowstone.property.unknown", key));
                } else {
                    sender.sendMessage(GlowServer.lang.getString(sender, "command.glowstone.property.result", ChatColor.AQUA + key + ChatColor.RESET, ChatColor.GOLD + value + ChatColor.RESET));
                }
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("vm")) {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMxBean.getInputArguments();
            if (arguments.size() == 0) {
                sender.sendMessage(GlowServer.lang.getString(sender, "command.glowstone.vm.noargs"));
            } else {
                sender.sendMessage(GlowServer.lang.getString(sender, "command.glowstone.vm.args", arguments.size()));
                for (String argument : arguments) {
                    sender.sendMessage(" - '" + ChatColor.AQUA + argument + ChatColor.RESET + "'.");
                }
            }

            return false;
        }
        if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds")) {
            if (args.length == 1) {
                // list worlds
                sender.sendMessage(GlowServer.lang.getString(sender, "command.glowstone.world.list", CommandUtils.prettyPrint(getWorldNames().toArray(new String[0]))));
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.glowstone.world.players"));
                return false;
            }
            GlowPlayer player = (GlowPlayer) sender;
            String worldName = args[1];
            GlowWorld world = player.getServer().getWorld(worldName);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.glowstone.world.missing", worldName));
                return false;
            }
            player.teleport(world.getSpawnLocation());
            player.sendMessage(GlowServer.lang.getString(sender, "command.glowstone.world.teleported", world.getName()));
            return true;
        }
        if (args[0].equalsIgnoreCase("eval")) {
            if (args.length == 1) {
                // no args, send usage
                sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.glowstone.eval.usage", label));
                return false;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i] + (i == args.length - 1 ? "" : " "));
            }
            ReflectionProcessor processor = new ReflectionProcessor(builder.toString(), sender instanceof Entity ? sender : Bukkit.getServer());
            Object result = processor.process();
            sender.sendMessage(ChatColor.GOLD + GlowServer.lang.getString(sender, "command.glowstone.eval.return", (result == null ? ChatColor.RED + GlowServer.lang.getString(sender, "command.glowstone.eval.missing") : ChatColor.AQUA + result.toString())));
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Preconditions.checkNotNull(sender, "Sender cannot be null");
        Preconditions.checkNotNull(args, "Arguments cannot be null");
        Preconditions.checkNotNull(alias, "Alias cannot be null");
        if (args.length == 0) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList(SUBCOMMANDS.size()));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("property")) {
            return (List) StringUtil.copyPartialMatches(args[1], System.getProperties().stringPropertyNames(), new ArrayList(System.getProperties().stringPropertyNames().size()));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds")) && sender instanceof Player) {
            Collection<String> worlds = getWorldNames();
            return (List) StringUtil.copyPartialMatches(args[1], worlds, new ArrayList(worlds.size()));
        }
        return Collections.emptyList();
    }

    private Collection<String> getWorldNames() {
        return Bukkit.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }
}
