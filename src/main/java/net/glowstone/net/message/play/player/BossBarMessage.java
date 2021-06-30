package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

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
        this(uuid, action, null, 0f, null, null, (byte) 0);
    }

    //For action 2, update health
    public BossBarMessage(UUID uuid, Action action, float health) {
        this(uuid, action, null, health, null, null, (byte) 0);
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

        private static Action[] values;

        /**
         * Since values() is expensive, we cache it.
         *
         * @param i the ordinal to look up
         * @return the Action with ordinal {@code i}
         * @throws ArrayIndexOutOfBoundsException if {@code values()[i]} doesn't exist
         */
        public static Action fromInt(int i) {
            if (values == null) {
                values = Action.values();
            }
            return values[i];
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

        private static Color[] values;

        /**
         * Since values() is expensive, we cache it.
         *
         * @param i the ordinal to look up
         * @return the Color with ordinal {@code i}
         * @throws ArrayIndexOutOfBoundsException if {@code values()[i]} doesn't exist
         */
        public static Color fromInt(int i) {
            if (values == null) {
                values = Color.values();
            }
            return values[i];
        }

        /**
         * Converts a {@link BarColor} to an instance of this enum.
         *
         * @param barColor the bar color to convert
         * @return the bar color as a Color
         */
        public static Color fromBarColor(BarColor barColor) {
            if (values == null) {
                values = Color.values();
            }
            return values[barColor.ordinal()];
        }
    }

    public enum Division {
        NO_DIVISION,
        SIX_NOTCHES,
        TEN_NOTCHES,
        TWELVE_NOTCHES,
        TWENTY_NOTCHES;

        private static Division[] values;

        /**
         * Since values() is expensive, we cache it.
         *
         * @param i the ordinal to look up
         * @return the Action with ordinal {@code i}
         * @throws ArrayIndexOutOfBoundsException if {@code values()[i]} doesn't exist
         */
        public static Division fromInt(int i) {
            if (values == null) {
                values = Division.values();
            }
            return values[i];
        }

        /**
         * Converts a {@link BarStyle} to an instance of this enum.
         *
         * @param barStyle the bar style to convert
         * @return the bar style as a Division
         */
        public static Division fromBarStyle(BarStyle barStyle) {
            if (values == null) {
                values = Division.values();
            }
            return values[barStyle.ordinal()];
        }
    }
}
