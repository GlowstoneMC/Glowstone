package net.glowstone.command.minecraft;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.TitleMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TitleCommand extends VanillaCommand {

    public TitleCommand() {
        super("title", "Sends a title to the specified player(s)", "/title <player> <title|subtitle|times|clear|reset> ...", Collections.emptyList());
        setPermission("minecraft.command.title");
    }

    /**
     * Converts a valid JSON chat component to a basic colored string. This does not parse
     * components like hover or click events. This returns null on parse failure.
     *
     * @param json the json chat component
     * @return the colored string, or null
     */
    public String convertJson(Map<String, Object> json) {
        if (json == null || !json.containsKey("text") && !(json.get("text") instanceof String))
            return null; // We can't even parse this

        ChatColor color = ChatColor.WHITE;
        List<ChatColor> style = new ArrayList<>();

        for (Object key : json.keySet()) {
            if (!(key instanceof String))
                continue;

            String keyString = (String) key;

            if (keyString.equalsIgnoreCase("color")) {
                if (!(json.get("color") instanceof String)) return null;
                color = toColor((String) json.get(keyString));
            } else if (!keyString.equalsIgnoreCase("text")) {
                if (toColor(keyString) == null) return null;
                style.add(toColor(keyString));
            }
        }

        style.add(color);

        String text = (String) json.get("text");

        for (ChatColor c : style) {
            text = c + text;
        }

        return text;
    }

    private static ChatColor toColor(String name) {
        if (name.equals("obfuscated"))
            return ChatColor.MAGIC;

        if (name.equals("underlined"))
            return ChatColor.UNDERLINE;

        // Loop to avoid exceptions, we'll just return null if it can't be parsed
        for (ChatColor color : ChatColor.values()) {
            if (color == ChatColor.MAGIC) continue; // This isn't a valid value for color anyways

            if (color.name().equalsIgnoreCase(name.toUpperCase()))
                return color;
        }
        return null;
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
            return false;
        }

        String action = args[1];

        if (action.equalsIgnoreCase("clear")) {
            ((GlowPlayer) player).clearTitle();
            sender.sendMessage("Cleared " + player.getName() + "'s title");
        } else if (action.equalsIgnoreCase("reset")) {
            player.resetTitle();
            sender.sendMessage("Reset " + player.getName() + "'s title");
        } else if (action.equalsIgnoreCase("title")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /title <player> " + action + " <raw json>");
                return false;
            }

            StringBuilder message = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                message.append(args[i]);
            }

            String raw = message.toString().trim();
            if (!validJson(raw)) {
                sender.sendMessage(ChatColor.RED + "Invalid JSON: Could not parse, invalid format?");
                return false;
            }

            String component = raw;
            Map<String, Object> parsed = getJson(raw);
            if (parsed != null) {
                component = convertJson(parsed);
            }

            ((GlowPlayer) player).updateTitle(TitleMessage.Action.TITLE, component);
            ((GlowPlayer) player).sendTitle();

            sender.sendMessage("Updated " + player.getName() + "'s title");
        } else if (action.equalsIgnoreCase("subtitle")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /title <player> " + action + " <raw json>");
                return false;
            }

            StringBuilder message = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                message.append(args[i]);
            }

            String raw = message.toString().trim();
            if (!validJson(raw)) {
                sender.sendMessage(ChatColor.RED + "Invalid JSON: Could not parse, invalid format?");
                return false;
            }

            String component = raw;
            Object parsed = JSONValue.parse(raw);
            if (parsed instanceof JSONObject) {
                component = convertJson((JSONObject) parsed);
            }

            ((GlowPlayer) player).updateTitle(TitleMessage.Action.SUBTITLE, component);

            sender.sendMessage("Updated " + player.getName() + "'s subtitle");
        } else if (action.equalsIgnoreCase("times")) {
            if (args.length != 5) {
                sender.sendMessage(ChatColor.RED + "Usage: /title <player> " + action + " <fade in> <stay time> <fade out>");
                return false;
            }

            if (!tryParseInt(args[2])) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number");
                return false;
            }
            if (!tryParseInt(args[3])) {
                sender.sendMessage(ChatColor.RED + "'" + args[3] + "' is not a number");
                return false;
            }
            if (!tryParseInt(args[4])) {
                sender.sendMessage(ChatColor.RED + "'" + args[4] + "' is not a number");
                return false;
            }

            ((GlowPlayer) player).updateTitle(TitleMessage.Action.TIMES, toInt(args[2]), toInt(args[3]), toInt(args[4]));

            sender.sendMessage("Updated " + player.getName() + "'s times");
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        return true;
    }

    private boolean tryParseInt(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private int toInt(String number) {
        return Integer.parseInt(number.trim());
    }

    private boolean validJson(String raw) {
        Map<String, Object> object = getJson(raw);

        if (object == null) {
            // Could not parse JSON: Check to see if it's at least a single word

            // Rule set:
            // 1. Cannot contain a space (or else the client fails)
            // 2. Must not look like JSON (first character check is sufficient)
            return !raw.contains(" ") && raw.charAt(0) != '{' && raw.charAt(0) != '[';
        }

        Map<String, Object> json = object;

        // Run through all of the keys to see if they are valid keys,
        // and have valid values
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            Object value = entry.getValue();

            if (entry.getKey() == null)
                return false; // The key is empty, meaning that it is not valid

            String keyString = entry.getKey();

            switch (keyString) {
                case "text":
                    if (!(value instanceof String))
                        return false; // The value is not a valid type
                    break;
                case "color":
                    if (!(value instanceof String))
                        return false; // The value is not a valid type
                    break;
                case "bold":
                    if (!(value instanceof Boolean))
                        return false; // The value is not a valid type
                    break;
                case "italic":
                    if (!(value instanceof Boolean))
                        return false; // The value is not a valid type
                    break;
                case "underlined":
                    if (!(value instanceof Boolean))
                        return false; // The value is not a valid type
                    break;
                case "strikethrough":
                    if (!(value instanceof Boolean))
                        return false; // The value is not a valid type
                    break;
                case "obfuscated":
                    if (!(value instanceof Boolean))
                        return false; // The value is not a valid type
                    break;
                default:
                    // The key is not in the list of valid keys,
                    // meaning that it is not a valid key
                    return false;
            }
        }

        // If we made it this far then it has a pretty good chance at being valid
        return true;
    }

    private Map<String, Object> getJson(String raw) {
        Gson gson = new Gson();
        try {
            Map<String, Object> map = gson.fromJson(raw, new TypeToken<Map<String, Object>>() {
            }.getType());

            return map;
        } catch (JsonSyntaxException e) {
            // Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }
}
