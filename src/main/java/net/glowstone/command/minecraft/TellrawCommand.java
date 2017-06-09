package net.glowstone.command.minecraft;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Collections;

public class TellrawCommand extends VanillaCommand {

    public TellrawCommand() {
        super("tellraw", "Send a private JSON message to the given player", "/tellraw <player> <raw-json-message>", Collections.emptyList());
        setPermission("minecraft.command.tellraw");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        Player player = Bukkit.getPlayerExact(args[0]);

        if (player == null || sender instanceof Player && !((Player) sender).canSee(player)) {
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
