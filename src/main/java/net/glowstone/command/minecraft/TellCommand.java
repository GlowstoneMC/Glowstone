package net.glowstone.command.minecraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TellCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TellCommand() {
        super("tell", "Send a private message.", "/tell <player> <private message ...>",
            Arrays.asList("msg", "w"));
        setPermission("minecraft.command.tell");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String name = args[0];
        Player[] players;
        if (name.charAt(0) == '@' && CommandUtils.isPhysical(sender)) {
            Location location = CommandUtils.getLocation(sender);
            CommandTarget target = new CommandTarget(sender, name);
            target.getArguments()
                .put("type", new CommandTarget.SelectorValue("player")); // only players
            Entity[] matched = target.getMatched(location);
            if (matched.length == 0) {
                sender.sendMessage(ChatColor.RED + "Selector '" + name + "' found nothing");
                return false;
            }
            players = new Player[matched.length];
            for (int i = 0; i < matched.length; i++) {
                players[i] = (Player) matched[i];
            }
        } else {
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + name + "' cannot be found");
                return false;
            }
            players = new Player[]{player};
        }
        String senderName = CommandUtils.getName(sender);
        String message = StringUtils.join(args, ' ', 1, args.length);
        for (Player player : players) {
            if (sender.equals(player)) {
                sender.sendMessage(ChatColor.RED + "You can't send a private message to yourself!");
                continue;
            }
            player.sendMessage(ChatColor.GRAY + senderName + " whispers " + message);
            sender.sendMessage(
                ChatColor.GRAY + "[" + senderName + "->" + player.getName() + "] " + message);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length > 1) {
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
