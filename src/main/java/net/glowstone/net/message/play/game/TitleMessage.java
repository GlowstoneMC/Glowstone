package net.glowstone.net.message.play.game;

import com.destroystokyo.paper.Title;
import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.TextMessage;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Documented at http://wiki.vg/Protocol#Title
 */
@Data
public final class TitleMessage implements Message {

    private final Action action;
    private final TextMessage text;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    /**
     * Creates an instance with a message.
     *
     * @param action should be {@link Action#TITLE}, {@link Action#SUBTITLE} or {@link
     *               Action#ACTION}
     * @param text   the new text
     */
    public TitleMessage(Action action, TextMessage text) {
        this.action = action;
        this.text = text;
        fadeIn = 0;
        stay = 0;
        fadeOut = 0;
    }

    /**
     * Creates an instance with times.
     *
     * @param action  should be {@link Action#TIMES}
     * @param fadeIn  the fade-in duration in ticks
     * @param stay    the delay between the end of fade-in and the start of fade-out, in ticks
     * @param fadeOut the fade-out duration in ticks
     */
    public TitleMessage(Action action, int fadeIn, int stay, int fadeOut) {
        this.action = action;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        text = null;
    }

    /**
     * Creates an instance with no message or times.
     *
     * @param action should be {@link Action#CLEAR} or {@link Action#RESET}
     */
    public TitleMessage(Action action) {
        this.action = action;
        text = null;
        fadeIn = 0;
        stay = 0;
        fadeOut = 0;
    }

    /**
     * Converts a {@link Title} to 3 instances of {@link TitleMessage}.
     *
     * @param title the title to convert
     * @return 3 messages: the first sets the title, the second sets the subtitle, and the third
     *     sets the fade/stay/fade durations
     */
    public static TitleMessage[] fromTitle(Title title) {
        TextMessage titleMessage = asTextMessage(BaseComponent.toLegacyText(title.getTitle()));
        TextMessage subTitleMessage = title.getSubtitle() != null ? asTextMessage(
            BaseComponent.toLegacyText(title.getSubtitle())) : asTextMessage(null);

        return new TitleMessage[] {
            new TitleMessage(Action.TITLE, titleMessage),
            new TitleMessage(Action.SUBTITLE, subTitleMessage),
            new TitleMessage(Action.TIMES, title.getFadeIn(), title.getStay(), title
                .getFadeOut())
        };
    }

    private static TextMessage asTextMessage(String rawString) {
        if (rawString == null) {
            rawString = "";
        }
        return new TextMessage(rawString);
    }

    public enum Action {
        TITLE,
        SUBTITLE,
        /**
         * Set text above the action bar.
         */
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
