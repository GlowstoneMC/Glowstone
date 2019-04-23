package net.glowstone.command.glowstone;

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
import net.glowstone.command.GlowCommandWithSubcommands;
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

public class GlowstoneCommand extends GlowCommandWithSubcommands {

    private static final Subcommand WORLD = new Subcommand(
            ROOT_COMMAND_NAME, "world", "worlds") {
        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            ResourceBundle bundle = commandMessages.getResourceBundle();
            if (args.length == 1) {
                // list worlds
                new LocalizedStringImpl("glowstone.worlds", bundle)
                        .send(sender, commandMessages.joinList(getWorldNames()));
                return true;
            }
            if (!(sender instanceof Player)) {
                new LocalizedStringImpl("glowstone.world.not-player", bundle)
                        .sendInColor(ChatColor.RED, sender);
                return false;
            }
            GlowPlayer player = (GlowPlayer) sender;
            String worldName = args[1];
            GlowWorld world = player.getServer().getWorld(worldName);
            if (world == null) {
                new LocalizedStringImpl("glowstone.world.invalid", bundle)
                        .sendInColor(ChatColor.RED, sender, worldName);
                return false;
            }
            player.teleport(world.getSpawnLocation());
            new LocalizedStringImpl("glowstone.world.done", bundle)
                    .send(player, world.getName());
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
                throws IllegalArgumentException {
            if (sender instanceof Player) {
                Collection<String> worlds = getWorldNames();
                return StringUtil.copyPartialMatches(args[1], worlds,
                        new ArrayList<>(worlds.size()));
            }
            return Collections.emptyList();
        }
    };
    private static final Subcommand VM = new Subcommand(ROOT_COMMAND_NAME, "vm") {
        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMxBean.getInputArguments();
            ResourceBundle bundle = commandMessages.getResourceBundle();
            if (arguments.size() == 0) {
                new LocalizedStringImpl("glowstone.vm.empty", bundle).send(sender);
            } else {
                new LocalizedStringImpl("glowstone.vm", bundle).send(sender, arguments.size());
                for (String argument : arguments) {
                    sender.sendMessage(
                            " - '" + ChatColor.AQUA + argument + ChatColor.RESET + "'.");
                }
            }
            return false;
        }
    };
    private static final Subcommand PROPERTY = new Subcommand(ROOT_COMMAND_NAME, "property") {
        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            ResourceBundle bundle = commandMessages.getResourceBundle();
            if (args.length == 1) {
                LocalizedStringImpl propertyTemplate = new LocalizedStringImpl(
                        "glowstone.property", bundle);
                // list all
                System.getProperties().forEach(
                    (key, value) -> propertyTemplate.send(sender, key, value));
            } else {
                // get a property
                String key = args[1].toLowerCase();
                String value = System.getProperty(key);
                if (value == null) {
                    new LocalizedStringImpl("glowstone.property.invalid", bundle)
                            .sendInColor(ChatColor.RED, sender, key);
                } else {
                    new LocalizedStringImpl("glowstone.property", bundle)
                            .send(sender, key, value);
                }
            }
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
                throws IllegalArgumentException {
            Set<String> propertyNames = System.getProperties().stringPropertyNames();
            return StringUtil.copyPartialMatches(args[1],
                    propertyNames, new ArrayList<>(propertyNames.size()));
        }
    };
    private static final Subcommand EVAL = new Subcommand(ROOT_COMMAND_NAME, "eval") {
        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            if (args.length == 1) {
                sendHelp(sender, label, commandMessages.getResourceBundle());
                return false;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i] + (i == args.length - 1 ? "" : " "));
            }
            ReflectionProcessor processor = new ReflectionProcessor(builder.toString(),
                    sender instanceof Entity ? sender : ServerProvider.getServer());
            Object result = processor.process();
            if (result == null) {
                new LocalizedStringImpl("glowstone.eval.null",
                        commandMessages.getResourceBundle())
                        .send(sender);
            } else {
                new LocalizedStringImpl("glowstone.eval", commandMessages.getResourceBundle())
                        .send(sender, result);
            }
            return true;
        }
    };
    private static final Subcommand CHUNK = new Subcommand(ROOT_COMMAND_NAME, "chunk") {
        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            if (!CommandUtils.isPhysical(sender)) {
                commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL)
                        .sendInColor(ChatColor.RED, sender);
                return false;
            }
            Chunk chunk = CommandUtils.getLocation(sender).getChunk();
            new LocalizedStringImpl("glowstone.chunk", commandMessages.getResourceBundle())
                    .send(sender, chunk.getX(), chunk.getZ());
            return true;
        }
    };
    private static final Subcommand ABOUT = new Subcommand(ROOT_COMMAND_NAME, "about") {
        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            ResourceBundle bundle = commandMessages.getResourceBundle();
            // some info about this Glowstone server
            new LocalizedStringImpl("glowstone.about", bundle).send(sender);
            LocalizedStringImpl tpl
                    = new LocalizedStringImpl("glowstone.about._template", bundle);
            sendBullet(sender, tpl, bundle, "glowstone.about.brand", Bukkit.getName());
            sendBullet(sender, tpl, bundle, "glowstone.about.name", Bukkit.getServerName());
            sendBullet(sender, tpl, bundle, "glowstone.about.version", Bukkit.getVersion());
            sendBullet(sender, tpl, bundle, "glowstone.about.api-version",
                    Bukkit.getBukkitVersion());
            sendBullet(sender, tpl, bundle, "glowstone.about.players",
                    Bukkit.getOnlinePlayers().size());
            sendBullet(sender, tpl, bundle, "glowstone.about.worlds",
                    Bukkit.getWorlds().size());
            sendBullet(sender, tpl, bundle, "glowstone.about.plugins",
                    Bukkit.getPluginManager().getPlugins().length);

            // thread count for group, non-recursive
            ThreadGroup myGroup = Thread.currentThread().getThreadGroup();
            long threadCount = Thread.getAllStackTraces().keySet().stream()
                    .filter(thread -> thread.getThreadGroup() == myGroup)
                    .count();
            sendBullet(sender, tpl, bundle, "glowstone.about.threads", threadCount);
            return false;
        }
    };
    public static final String ROOT_COMMAND_NAME = "glowstone";

    /**
     * Creates the instance for this command.
     */
    public GlowstoneCommand() {
        super(ROOT_COMMAND_NAME, Arrays.asList("gs"), Arrays.asList(
                ABOUT, CHUNK, EVAL, PROPERTY, VM, WORLD)); // NON-NLS
        setPermission("glowstone.debug"); // NON-NLS
    }

    private static void sendBullet(CommandSender sender,
            LocalizedStringImpl template, ResourceBundle resourceBundle, @NonNls String key,
            Object value) {
        template.send(sender, new LocalizedStringImpl(key, resourceBundle), value);
    }

    private static Collection<String> getWorldNames() {
        return ServerProvider.getServer().getWorlds().stream().map(World::getName)
                .collect(Collectors.toList());
    }
}
