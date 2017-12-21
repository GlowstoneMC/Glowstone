package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

public class DeopCommand extends VanillaCommand {

    public DeopCommand() {
        super("deop", "Removes server operator status from a player.", "/deop <player>",
            Collections.emptyList());
        setPermission("minecraft.command.deop");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String name = args[0];
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player.isOp()) {
            player.setOp(false);
            sender.sendMessage("Deopped " + player.getName());
        } else {
            sender.sendMessage("Could not deop " + player.getName());
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            List<String> operators = new ArrayList<>();
            Bukkit.getOperators().stream().map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .forEach(player -> operators.add(player));
            return StringUtil.copyPartialMatches(args[0], operators, new ArrayList<>(operators.size()));
        } else if (args.length > 1) {
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
