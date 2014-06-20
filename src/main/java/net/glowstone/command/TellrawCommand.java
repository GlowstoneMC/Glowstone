package net.glowstone.command;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;

public class TellrawCommand extends BukkitCommand {

    public TellrawCommand() {
        super("tellraw");
        this.description = "Send a private JSON message to the given player";
        this.usageMessage = "/tellraw <player> <raw-json-message>";
        this.setAliases(Arrays.<String>asList());
        this.setPermission("glowstone.command.tellraw");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        Player player = Bukkit.getPlayerExact(args[0]);

        if (player == null || (sender instanceof Player && !((Player) sender).canSee(player))) {
            sender.sendMessage("There's no player by that name online.");
        } else {
            StringBuilder message = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                if (i > 1) message.append(" ");
                message.append(args[i]);
            }

            GlowPlayer glowPlayer = (GlowPlayer) player;

            Object obj = JSONValue.parse(message.toString());
            if (!(obj instanceof JSONObject)) {
                sender.sendMessage(ChatColor.RED + "Failed to parse JSON");
            } else {
                glowPlayer.getSession().send(new ChatMessage((JSONObject) obj));
            }
        }

        return true;
    }
}
