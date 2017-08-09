package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;
import net.glowstone.command.CommandUtils;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class BanListCommand extends VanillaCommand {

    private static final List<String> BAN_TYPES = Arrays.asList("ips", "players");

    public BanListCommand() {
        super("banlist", GlowServer.lang.getString("command.minecraft.banlist.description"), "/banlist " + GlowServer.lang.getString("command.minecraft.banlist.args.target"), Collections.emptyList());
        setPermission("minecraft.command.ban.list");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;

        BanList.Type banType;

        if (args.length > 0) {
            if ("ips".equalsIgnoreCase(args[0])) {
                banType = BanList.Type.IP;
            } else if ("players".equalsIgnoreCase(args[0])) {
                banType = BanList.Type.NAME;
            } else {
                sender.sendMessage(ChatColor.RED + GlowServer.lang.getString(sender, "command.minecraft.banlist.invalid", args[0]) + " " + GlowServer.lang.getString(sender, "command.generic.usage", "/banlist " + GlowServer.lang.getString(sender, "command.minecraft.banlist.args.target")));
                return false;
            }
        } else {
             banType = BanList.Type.NAME;
        }

        final Set<BanEntry> banEntries = Bukkit.getBanList(banType).getBanEntries();

        if (banEntries.isEmpty()) {
            sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.banlist.empty"));
        } else {
            final List<String> targets = banEntries.stream().map(BanEntry::getTarget).collect(Collectors.toList());
            sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.banlist.count", banEntries.size()));
            sender.sendMessage(CommandUtils.prettyPrint(targets.toArray(new String[targets.size()])));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], BAN_TYPES, new ArrayList(BAN_TYPES.size()));
        } else {
            return args.length == 0 ? super.tabComplete(sender, alias, args) : Collections.emptyList();
        }
    }
}
