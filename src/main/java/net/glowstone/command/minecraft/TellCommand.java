package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TellCommand extends VanillaCommand {

    public TellCommand() {
        super("tell", I.tr("command.minecraft.tell.description"), I.tr("command.minecraft.tell.usage"), Arrays.asList("msg", "w"));
        setPermission("minecraft.command.tell");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.tell.usage")));
            return false;
        }
        String name = args[0];
        Player[] players;
        if (name.charAt(0) == '@' && CommandUtils.isPhysical(sender)) {
            Location location = CommandUtils.getLocation(sender);
            CommandTarget target = new CommandTarget(sender, name);
            target.getArguments().put("type", new CommandTarget.SelectorValue("player")); // only players
            Entity[] matched = target.getMatched(location);
            if (matched.length == 0) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.selector", name));
                return false;
            }
            players = new Player[matched.length];
            for (int i = 0; i < matched.length; i++) {
                players[i] = (Player) matched[i];
            }
        } else {
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.player.missing", name));
                return false;
            }
            players = new Player[]{player};
        }
        String senderName = CommandUtils.getName(sender);
        String message = StringUtils.join(args, ' ', 1, args.length);
        for (Player player : players) {
            if (sender.equals(player)) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.tell.self"));
                continue;
            }
            player.sendMessage(ChatColor.GRAY + I.tr(sender, "command.minecraft.tell.whisper.1", senderName, message));
            sender.sendMessage(ChatColor.GRAY + I.tr(sender, "command.minecraft.tell.whisper.2", senderName, player.getName(), message));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length > 1) {
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
