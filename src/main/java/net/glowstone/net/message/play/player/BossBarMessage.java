package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.util.TextMessage;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class BossBarMessage implements Message {

    private final UUID uuid;
    private final Action action;
    private final TextMessage title;
    private final float health;
    private final Color color;
    private final Division division;
    private final byte flags;


    //For action 1, remove
    public BossBarMessage(UUID uuid, Action action) {
        this(uuid, action, null, 0f, null, null,(byte)0);
    }

    //For action 2, update health
    public BossBarMessage(UUID uuid, Action action, float health) {
        this(uuid, action, null, health, null, null, (byte)0);
    }

    //For action 3, update title
    public BossBarMessage(UUID uuid, Action action, TextMessage title) {
        this(uuid, action, title, 0f, null, null, (byte) 0);
    }

    //For action 4, update style
    public BossBarMessage(UUID uuid, Action action, Color color, Division division) {
        this(uuid, action, null, 0f, color, division, (byte) 0);
    }

    //For action 5, update flags
    public BossBarMessage(UUID uuid, Action action, byte flags) {
        this(uuid, action, null, 0f, null, null, flags);
    }

    public enum Action {
        ADD,
        REMOVE,
        UPDATE_HEALTH,
        UPDATE_TITLE,
        UPDATE_STYLE,
        UPDATE_FLAGS;

        private static Action[] values = null;

        //Since values() is expensive, let's cache it.
        public static Action fromInt(int i) {
            if (Action.values == null) {
                Action.values = Action.values();
            }
            return Action.values[i];
        }
    }

    public enum Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE;

        private static Color[] values = null;

        //Since values() is expensive, let's cache it.
        public static Color fromInt(int i) {
            if (Color.values == null) {
                Color.values = Color.values();
            }
            return Color.values[i];
        }
    }

    public enum Division {
        NO_DIVISION,
        SIX_NOTCHES,
        TEN_NOTCHES,
        TWELVE_NOTCHES,
        TWENTY_NOTCHES;

        private static Division[] values = null;

        //Since values() is expensive, let's cache it.
        public static Division fromInt(int i) {
            if (Division.values == null) {
                Division.values = Division.values();
            }
            return Division.values[i];
        }
    }
}
