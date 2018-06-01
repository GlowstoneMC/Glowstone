package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.glowstone.command.CommandUtils;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

public class BanListCommand extends VanillaCommand {

    private static final List<String> BAN_TYPES = Arrays.asList("ips", "players");

    /**
     * Creates the instance for this command.
     */
    public BanListCommand() {
        super("banlist", "Displays the server's blacklist.", "/banlist [ips|players]",
            Collections.emptyList());
        setPermission("minecraft.command.ban.list");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        BanList.Type banType;

        if (args.length > 0) {
            if ("ips".equalsIgnoreCase(args[0])) {
                banType = BanList.Type.IP;
            } else if ("players".equalsIgnoreCase(args[0])) {
                banType = BanList.Type.NAME;
            } else {
                sender.sendMessage(
                    ChatColor.RED + "Invalid parameter '" + args[0] + "'. Usage: " + usageMessage);
                return false;
            }
        } else {
            banType = BanList.Type.NAME;
        }

        final Set<BanEntry> banEntries = Bukkit.getBanList(banType).getBanEntries();

        if (banEntries.isEmpty()) {
            sender.sendMessage("There are no banned players");
        } else {
            final List<String> targets = banEntries.stream().map(BanEntry::getTarget)
                .collect(Collectors.toList());
            sender.sendMessage("There are " + banEntries.size() + " banned players: ");
            sender
                .sendMessage(CommandUtils.prettyPrint(targets.toArray(new String[targets.size()])));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil
                .copyPartialMatches(args[0], BAN_TYPES, new ArrayList(BAN_TYPES.size()));
        } else {
            return args.length == 0 ? super.tabComplete(sender, alias, args)
                : Collections.emptyList();
        }
    }
}
