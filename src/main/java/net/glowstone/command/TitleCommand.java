package net.glowstone.command;

import com.destroystokyo.paper.Title;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Collections;

public class TitleCommand extends BukkitCommand {

    public TitleCommand() {
        super("title", "Sends a title to the specified player(s)", "/title <player> <title|subtitle|times|clear|reset> ...", Collections.emptyList());
        setPermission("glowstone.command.title");
    }

    /**
     * Converts a valid JSON chat component to a basic colored string. This does not parse
     * components like hover or click events. This returns null on parse failure.
     *
     * @param json the json chat component
     * @return the colored string, or null
     */
    // TODO: Replace with proper chat components when possible
    public static String convertJson(JSONObject json) {
        if (json == null || !json.containsKey("text") && !(json.get("text") instanceof String))
            return null; // We can't even parse this

        ChatColor color = ChatColor.WHITE;
        if (json.containsKey("color")) {
            if (!(json.get("color") instanceof String)) return null;
            color = toColor((String) json.get("color"));
        }
        if (color == null) return null; // Invalid color

        String text = color + (String) json.get("text");

        // Check for "extra"
        if (json.containsKey("extra")) {
            Object extraObj = json.get("extra");

            // Check to make sure it's a valid component
            if (!(extraObj instanceof JSONArray)) return null;

            // Check all components in 'extra'
            JSONArray extra = (JSONArray) extraObj;
            if (extra.isEmpty()) return null;
            for (Object o : extra) {
                if (!(o instanceof JSONObject)) return null;

                JSONObject e = (JSONObject) o;

                // Attempt to parse the component
                String temp = convertJson(e);
                if (temp == null) return null; // Could not parse 'extra'

                // It's valid, append it
                text += temp;
            }
        }

        return text;
    }

    private static ChatColor toColor(String name) {
        if (name.equals("obfuscated"))
            return ChatColor.MAGIC;

        // Loop to avoid exceptions, we'll just return null if it can't be parsed
        for (ChatColor color : ChatColor.values()) {
            if (color == ChatColor.MAGIC) continue; // This isn't a valid value for color anyways

            if (color.name().equals(name.toUpperCase()))
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
                if (i > 2) message.append(" ");
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

            player.sendTitle(new Title(component));

            sender.sendMessage("Updated " + player.getName() + "'s title");
        } else if (args[1].equalsIgnoreCase("subtitle")) {
            sender.sendMessage("This feature is not yet supported");
        } else if (args[1].equalsIgnoreCase("times")) {
            sender.sendMessage("This feature is not yet supported");
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        return true;
    }

    private int tryParseInt(CommandSender sender, String number, int lastParse) {
        if (lastParse == -1) {
            return -1;
        }

        int n = -1;
        try {
            n = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + number + "' is not a number");
        }
        return n;
    }

    private boolean validJson(String raw) {

        Object object = JSONValue.parse(raw);

        if (object == null) {
            // Could not parse JSON: Check to see if it's at least a single word

            // Rule set:
            // 1. Cannot contain a space (or else the client fails)
            // 2. Must not look like JSON (first character check is sufficient)
            return !raw.contains(" ") && raw.charAt(0) != '{' && raw.charAt(0) != '[';
        }

        if (!(object instanceof JSONObject))
            return false; // Not a valid JSON object

        JSONObject json = (JSONObject) object;

        // Run through all of the keys to see if they are valid keys,
        // and have valid values
        for (Object key : json.keySet()) {
            Object value = json.get(key);

            if (!(key instanceof String))
                return false; // The key is not a string, meaning that it is not valid

            String keyString = (String) key;

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
}
