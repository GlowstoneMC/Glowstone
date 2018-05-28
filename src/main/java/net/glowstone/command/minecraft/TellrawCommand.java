package net.glowstone.command.minecraft;

import java.util.Collections;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class TellrawCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TellrawCommand() {
        super("tellraw", "Send a private JSON message to the given player",
            "/tellraw <player> <raw-json-message>", Collections.emptyList());
        setPermission("minecraft.command.tellraw");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        Player player = Bukkit.getPlayerExact(args[0]);

        if (player == null || sender instanceof Player && !((Player) sender).canSee(player)) {
            sender.sendMessage("There's no player by that name online.");
            return false;
        } else {
            StringBuilder message = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                if (i > 1) {
                    message.append(" ");
                }
                message.append(args[i]);
            }

            Object obj = null;
            String json = message.toString();
            try {
                obj = JSONValue.parseWithException(json);
            } catch (ParseException e) {
                sender.sendMessage(ChatColor.RED + "Failed to parse JSON: " + e.getMessage());
                return false;
            }
            if (obj instanceof JSONArray || obj instanceof JSONObject) {
                BaseComponent[] components = ComponentSerializer.parse(json);
                player.sendMessage(components);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to parse JSON");
                return false;
            }
        }
    }
}
