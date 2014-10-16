package net.glowstone.util;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Simple container for chat message structures until more advanced chat
 * formatting is implemented.
 */
public final class TextMessage {

    private final JSONObject object;

    /**
     * Construct a new chat message from a simple text string. Handles style
     * and colors in the original string, converting them to the new format.
     * @param text The text of the message.
     */
    public TextMessage(String text) {
        object = convert(text);
    }

    /**
     * Construct a chat message from a JSON structure. No validation occurs.
     * @param object The JSON structure of the message.
     */
    public TextMessage(JSONObject object) {
        Validate.notNull(object, "object must not be null");
        this.object = object;
    }

    /**
     * Encode this chat message to its textual JSON representation.
     * @return The encoded representation.
     */
    public String encode() {
        return object.toJSONString();
    }

    /**
     * Attempt to convert the message to its plaintext representation.
     * @return The plain text, or the empty string on failure.
     */
    public String asPlaintext() {
        if (object.containsKey("text")) {
            Object obj = object.get("text");
            if (obj instanceof String) {
                return (String) obj;
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "Message" + encode();
    }

    /**
     * Decode a chat message from its textual JSON representation if possible.
     * @param json The encoded representation.
     * @return The decoded TextMessage.
     */
    public static TextMessage decode(String json) {
        JSONParser parser = new JSONParser();
        try {
            Object o = parser.parse(json);
            if (o instanceof JSONObject) {
                return new TextMessage((JSONObject) o);
            } else {
                return new TextMessage(o.toString());
            }
        } catch (ParseException e) {
            return new TextMessage("parse error");
        }
    }

    /**
     * Convert from an old-style to a new-style chat message.
     * @param text The The text of the message.
     * @return The converted JSON structure.
     */
    @SuppressWarnings("unchecked")
    private static JSONObject convert(String text) {
        // state
        final List<JSONObject> items = new LinkedList<>();
        final Set<ChatColor> formatting = EnumSet.noneOf(ChatColor.class);
        final StringBuilder current = new StringBuilder();
        ChatColor color = null;

        // work way through text, converting colors
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch != ChatColor.COLOR_CHAR) {
                // no special handling
                current.append(ch);
                continue;
            }

            if (i == text.length() - 1) {
                // ignore color character at end
                continue;
            }

            // handle colors
            append(items, current, color, formatting);
            ChatColor code = ChatColor.getByChar(text.charAt(++i));
            if (code == ChatColor.RESET) {
                color = null;
                formatting.clear();
            } else if (code.isFormat()) {
                formatting.add(code);
            } else {
                color = code;
                formatting.clear();
            }
        }
        append(items, current, color, formatting);

        // convert list of items into structure
        if (items.isEmpty()) {
            // no items, return a blank message
            JSONObject object = new JSONObject();
            object.put("text", "");
            return object;
        } else if (items.size() == 1) {
            // only one item, return it as-is
            return items.get(0);
        } else {
            JSONObject object = items.get(0);
            if (object.size() == 1) {
                // only contains "text", no formatting, can reuse
                object.put("extra", items.subList(1, items.size()));
            } else {
                // must put everything in the "extra" list
                object = new JSONObject();
                object.put("text", "");
                object.put("extra", items);
            }
            return object;
        }
    }

    @SuppressWarnings("unchecked")
    private static void append(List<JSONObject> items, StringBuilder current, ChatColor color, Set<ChatColor> formatting) {
        if (current.length() == 0) {
            return;
        }

        JSONObject object = new JSONObject();
        object.put("text", current.toString());
        if (color != null) {
            object.put("color", color.name().toLowerCase());
        }
        for (ChatColor format : formatting) {
            if (format == ChatColor.MAGIC) {
                object.put("obfuscated", true);
            } else {
                object.put(format.name().toLowerCase(), true);
            }
        }
        current.setLength(0);
        items.add(object);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextMessage that = (TextMessage) o;

        return object.equals(that.object);
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }
}
