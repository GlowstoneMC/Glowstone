package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;
import net.glowstone.util.TextMessage;
import org.bukkit.title.Title;
import org.bukkit.title.TitleOptions;

@Data
public final class TitleMessage implements Message {

    private final Action action;
    private final TextMessage text;
    private final int fadeIn, stay, fadeOut;

    // TITLE, SUBTITLE
    public TitleMessage(Action action, TextMessage text) {
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
        return new TitleMessage[]{
                new TitleMessage(Action.TITLE, asTextMessage(title.getHeading())),
                new TitleMessage(Action.SUBTITLE, asTextMessage(title.getSubtitle()))
        };
    }

    public static TitleMessage fromOptions(TitleOptions options) {
        return new TitleMessage(Action.TIMES, options.getFadeInTime(), options.getVisibleTime(), options.getFadeOutTime());
    }

    private static TextMessage asTextMessage(String rawString) {
        if (rawString == null) rawString = "";
        return new TextMessage(rawString);
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
