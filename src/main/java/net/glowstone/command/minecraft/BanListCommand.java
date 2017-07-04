package net.glowstone.command.minecraft;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import java.util.*;

public class BanListCommand extends VanillaCommand {

    private static final List<String> BAN_TYPES;

    private static final int BAN_TYPES_SIZE;

    static {
        BAN_TYPES = Arrays.asList("ips", "players");
        BAN_TYPES_SIZE = BAN_TYPES.size();
    }

    public BanListCommand() {
        super("banlist", "Displays the server's blacklist.", "/banlist [ips|players]", Collections.emptyList());
        setPermission("minecraft.command.ban.list");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;

        BanList.Type banType;

        if (args.length > 0) {
            final String parameter = args[0];
            switch (parameter) {
                case "ips" :
                    banType = BanList.Type.IP;
                    break;
                case "players" :
                    banType = BanList.Type.NAME;
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Invalid parameter '" + parameter + "'. Usage : " + usageMessage);
                    return false;
            }
        } else {
             banType = BanList.Type.NAME;
        }

        final Set<BanEntry> banEntries = Bukkit.getBanList(banType).getBanEntries();

        if (banEntries.isEmpty()) {
            sender.sendMessage("There are no banned players");
        } else {
            int index = 0, size = banEntries.size();
            final StringBuilder banList = new StringBuilder(200);

            for (final BanEntry banEntry : banEntries) {
                banList.append(banEntry.getTarget());
                if (index != size - 1) {
                    if (index == size - 2) {
                        banList.append(" and ");
                    } else {
                        banList.append(", ");
                    }
                }
                ++index;
            }

            sender.sendMessage("There are " + size + " banned players : ");
            sender.sendMessage(banList.toString());
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], BAN_TYPES, new ArrayList(BAN_TYPES_SIZE));
        }
        return super.tabComplete(sender, alias, args);
    }
}
