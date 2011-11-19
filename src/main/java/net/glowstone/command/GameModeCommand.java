package net.glowstone.command;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class GameModeCommand extends GlowCommand {

    public GameModeCommand(GlowServer server) {
        super(server, "gamemode", "Change the game mode for players", "[player] <mode>");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 1, 2)) {
            return false;
        }
        String name;
        String target;
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this without a player name");
                return false;
            }
            name = sender.getName();
            target = args[0];

        } else {
            name = args[0];
            target = args[1];
        }
        GameMode targetMode = null;
        try {
        targetMode = GameMode.valueOf(target.toUpperCase());
        } catch (IllegalArgumentException e) {
            try {
                targetMode = GameMode.getByValue(Integer.parseInt(target));
            } catch (NumberFormatException ex) {}
        }
        if (targetMode == null) {
            sender.sendMessage(ChatColor.RED + "Unknown game mode: " + target);
            return false;
        }
        Player targetPlayer = server.getPlayerExact(name);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Unknown player given: " + name);
            return false;
        }
        targetPlayer.setGameMode(targetMode);
        tellOps(sender, name + "'s game mode set to " + targetMode.name());
        return true;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
    
}
