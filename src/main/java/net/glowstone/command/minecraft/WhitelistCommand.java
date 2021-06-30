package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class WhitelistCommand extends GlowVanillaCommand {

    private static final List<String> SUBCOMMANDS = Arrays
        .asList("on", "off", "list", "add", "remove", "reload");

    /**
     * Creates the instance for this command.
     */
    public WhitelistCommand() {
        super("whitelist");
        setPermission("minecraft.command.whitelist"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length == 0) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        String subcommand = args[0];
        GlowServer server = (GlowServer) ServerProvider.getServer();
        if (subcommand.equals("on")) {
            sender.getServer().setWhitelist(true);
            sender.sendMessage("Turned on the whitelist");
            return true;
        }
        if (subcommand.equals("off")) {
            sender.getServer().setWhitelist(false);
            sender.sendMessage("Turned off the whitelist");
            return true;
        }
        if (subcommand.equals("list")) {
            Set<OfflinePlayer> whitelistedPlayers = sender.getServer().getWhitelistedPlayers();
            String[] names = new String[whitelistedPlayers.size()];
            int i = 0;
            for (OfflinePlayer p : whitelistedPlayers) {
                names[i++] = p.getName();
            }
            sender.sendMessage("There are " + names.length + " whitelisted players:");
            sender.sendMessage(commandMessages.joinList(names));
            return true;
        }
        if (subcommand.equals("add")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /whitelist add <player>");
                return false;
            }
            String name = args[1];
            server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
                if (ex != null) {
                    sender.sendMessage(ChatColor.RED + "Failed to add " + name
                        + " to the whitelist: " + ex.getMessage());
                    ex.printStackTrace();
                    return;
                }
                player.setWhitelisted(true);
                sender.sendMessage("Added " + player.getName() + " to the whitelist");
            });
            return true;
        }
        if (subcommand.equals("remove")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /whitelist remove <player>");
                return false;
            }
            String name = args[1];
            server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
                if (ex != null) {
                    sender.sendMessage(ChatColor.RED + "Failed to remove " + name
                        + " from the whitelist: " + ex.getMessage());
                    ex.printStackTrace();
                    return;
                }
                player.setWhitelisted(false);
                sender.sendMessage("Removed " + player.getName() + " from the whitelist");
            });
            return true;
        }
        if (subcommand.equals("reload")) {
            sender.getServer().reloadWhitelist();
            sender.sendMessage("Reloaded the whitelist");
            return true;
        }
        sendUsageMessage(sender, commandMessages);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil
                .copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList<>(SUBCOMMANDS.size()));
        }
        if (args.length > 1) {
            String subcommand = args[0];
            if (subcommand.equals("add")) {
                return super.tabComplete(sender, alias, args);
            }
            if (subcommand.equals("remove")) {
                Set<OfflinePlayer> whitelistedPlayers = sender.getServer().getWhitelistedPlayers();
                List<String> names = whitelistedPlayers.stream().map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
                return StringUtil
                    .copyPartialMatches(args[1], names, new ArrayList<>(names.size()));
            }
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
