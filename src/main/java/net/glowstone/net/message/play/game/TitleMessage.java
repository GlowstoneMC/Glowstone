package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.title.Title;
import org.bukkit.title.TitleOptions;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public final class TitleMessage implements Message {

    private final Action action;
    private final String text;
    private final int fadeIn, stay, fadeOut;

    // TITLE, SUBTITLE
    public TitleMessage(Action action, String text) {
        this.action = action;
        this.text = text;
        this.fadeIn = 0;
        this.stay = 0;
        this.fadeOut = 0;
    }

    // TIMES
    public TitleMessage(Action action, int fadeIn, int stay, int fadeOut) {
        this.action = action;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.text = null;
    }

    // CLEAR, RESET
    public TitleMessage(Action action) {
        this.action = action;
        this.text = null;
        this.fadeIn = 0;
        this.stay = 0;
        this.fadeOut = 0;
    }

    public static TitleMessage[] fromTitle(Title title) {
        return new TitleMessage[] {
                new TitleMessage(Action.TITLE, toJson(title.getHeading())),
                new TitleMessage(Action.SUBTITLE, toJson(title.getSubtitle()))
        };
    }

    public static TitleMessage fromOptions(TitleOptions options) {
        return new TitleMessage(Action.TIMES, options.getFadeInTime(), options.getVisibleTime(), options.getFadeOutTime());
    }

    private static String toJson(String message) {
        if (message == null) {
            return "";
        }

        // TODO: Replace this with proper chat formatting components at some point
        Object parsed = JSONValue.parse(message);
        if (parsed == null) {
            Map<String, Object> converted = null;

            List<Object> extra = new ArrayList<>();
            String[] parts = message.split(ChatColor.COLOR_CHAR + "");
            for (String part : parts) {
                if (part.length() == 0) {
                    continue;
                }

                Map<String, Object> mcReady = toJsonPart(part);

                if (converted == null) {
                    converted = mcReady;
                } else {
                    extra.add(mcReady);
                }
            }

            if (converted == null) {
                return ""; // Fail-safe
            }

            if (extra.size() > 0) {
                converted.put("extra", extra);
            }

            return JSONValue.toJSONString(converted);
        } else {
            return message; // Already valid JSON, return it
        }
    }

    private static Map<String, Object> toJsonPart(String decolored) {
        String prepended = ChatColor.COLOR_CHAR + decolored;
        Map<String, Object> component = new HashMap<>();

        if (!ChatColor.stripColor(prepended).equals(prepended)) {
            // Has a color code to start
            String rawText = decolored.substring(1);
            ChatColor last = ChatColor.getByChar(decolored.charAt(0));

            // Formatting codes are still accepted, just not healthy in terms of specification
            component.put("color", getColor(last.name().toLowerCase()));
            component.put("text", rawText);
        } else {
            // Does not have a prepended color code
            component.put("text", decolored);
            component.put("color", "white");
        }

        return component;
    }

    private static String getColor(String colorName) {
        switch (colorName) {
            case "magic":
                return "obfuscated";
            default:
                return colorName;
        }
    }

    public enum Action {
        TITLE,
        SUBTITLE,
        TIMES,
        CLEAR,
        RESET;

        public static Action getAction(int id) {
            Action[] values = values();
            return id < 0 || id >= values.length ? null : values[id];
        }
    }

}
