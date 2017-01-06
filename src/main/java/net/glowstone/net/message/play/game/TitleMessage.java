package net.glowstone.net.message.play.game;

import com.destroystokyo.paper.Title;
import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.TextMessage;
import net.md_5.bungee.api.chat.BaseComponent;

@Data
public final class TitleMessage implements Message {

    private final Action action;
    private final TextMessage text;
    private final int fadeIn, stay, fadeOut;

    // TITLE, SUBTITLE
    public TitleMessage(Action action, TextMessage text) {
        this.action = action;
        this.text = text;
        fadeIn = 0;
        stay = 0;
        fadeOut = 0;
    }

    // TIMES
    public TitleMessage(Action action, int fadeIn, int stay, int fadeOut) {
        this.action = action;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        text = null;
    }

    // CLEAR, RESET
    public TitleMessage(Action action) {
        this.action = action;
        text = null;
        fadeIn = 0;
        stay = 0;
        fadeOut = 0;
    }

    public static TitleMessage[] fromTitle(Title title) {
        TextMessage titleMessage = title.getTitle() != null ? asTextMessage(BaseComponent.toPlainText(title.getTitle())) : asTextMessage(null);
        TextMessage subTitleMessage = title.getSubtitle() != null ? asTextMessage(BaseComponent.toPlainText(title.getSubtitle())) : asTextMessage(null);

        return new TitleMessage[]{
                new TitleMessage(Action.TITLE, titleMessage),
                new TitleMessage(Action.SUBTITLE, subTitleMessage),
                new TitleMessage(Action.TIMES, title.getFadeIn(), title.getStay(), title.getFadeOut())
        };
    }

    private static TextMessage asTextMessage(String rawString) {
        if (rawString == null) rawString = "";
        return new TextMessage(rawString);
    }

    public enum Action {
        TITLE,
        SUBTITLE,
        ACTION,
        TIMES,
        CLEAR,
        RESET;

        public static Action getAction(int id) {
            Action[] values = values();
            return id < 0 || id >= values.length ? null : values[id];
        }
    }
}
