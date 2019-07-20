package net.glowstone.command.minecraft;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class TellrawCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TellrawCommand() {
        super("tellraw");
        setPermission("minecraft.command.tellraw"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length < 2) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        Player player = Bukkit.getPlayerExact(args[0]);

        if (player == null || sender instanceof Player && !((Player) sender).canSee(player)) {
            commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER).send(sender, args[0]);
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
                commandMessages.getGeneric(GenericMessage.INVALID_JSON)
                        .sendInColor(ChatColor.RED, sender, e.getMessage());
                return false;
            }
            if (obj instanceof JSONArray || obj instanceof JSONObject) {
                BaseComponent[] components = ComponentSerializer.parse(json);
                player.sendMessage(components);
                return true;
            } else {
                commandMessages.getGeneric(GenericMessage.INVALID_JSON)
                        .sendInColor(ChatColor.RED, sender, json);
                return false;
            }
        }
    }
}
