package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.util.TextMessage;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Documented at http://wiki.vg/Protocol#Player_List_Item
 */
@Data
public final class UserListItemMessage implements Message {

    private final List<Action> actions;
    private final List<Entry> entries;

    /**
     * Creates an instance.
     *
     * @param actions  List of actions in this message
     * @param entries the players to add, update or remove
     */
    public UserListItemMessage(List<Action> actions, List<Entry> entries) {
        this.actions = actions;
        this.entries = entries;

        for (Entry entry : entries) {
            if (!entry.actions.equals(actions)) {
                throw new IllegalArgumentException("Entries do not match");
            }
        }
    }

    public UserListItemMessage(List<Action> actions, Entry entry) {
        this(actions, Arrays.asList(entry));
    }

    // add

    public static Entry add(GlowPlayerProfile profile) {
        return add(profile, 0, 0, null);
    }

    /**
     * Adds a player to this message.
     *
     * @param profile     the player to add
     * @param gameMode    the player's game mode's value (see {@link org.bukkit.GameMode})
     * @param ping        the player's ping time in milliseconds (TODO: is this up, down, or round-trip?)
     * @param displayName the name to display for the player
     * @return The resultant entry
     */
    public static Entry add(GlowPlayerProfile profile, int gameMode, int ping,
                            TextMessage displayName) {
        // TODO: measure ping
        return new Entry(profile.getId(), profile, gameMode, ping, displayName,
            Lists.newArrayList(Action.ADD_PLAYER));
    }

    public static UserListItemMessage addOne(GlowPlayerProfile profile) {
        return new UserListItemMessage(Lists.newArrayList(Action.ADD_PLAYER), add(profile));
    }

    // gamemode

    public static Entry gameMode(UUID uuid, int gameMode) {
        return new Entry(uuid, null, gameMode, 0, null, Lists.newArrayList(Action.UPDATE_GAMEMODE));
    }

    public static UserListItemMessage gameModeOne(UUID uuid, int gameMode) {
        return new UserListItemMessage(Lists.newArrayList(Action.UPDATE_GAMEMODE), gameMode(uuid, gameMode));
    }

    // latency

    public static Entry latency(UUID uuid, int ping) {
        return new Entry(uuid, null, 0, ping, null, Lists.newArrayList(Action.UPDATE_LATENCY));
    }

    public static UserListItemMessage latencyOne(UUID uuid, int ping) {
        return new UserListItemMessage(Lists.newArrayList(Action.UPDATE_LATENCY), latency(uuid, ping));
    }

    // display name

    public static Entry displayName(UUID uuid, TextMessage displayName) {
        return new Entry(uuid, null, 0, 0, displayName, Lists.newArrayList(Action.UPDATE_DISPLAY_NAME));
    }

    public static UserListItemMessage displayNameOne(UUID uuid, TextMessage displayName) {
        return new UserListItemMessage(Lists.newArrayList(Action.UPDATE_DISPLAY_NAME), displayName(uuid, displayName));
    }

    // remove

    public static Entry remove(UUID uuid) {
        return new Entry(uuid, null, 0, 0, null, Lists.newArrayList(Action.REMOVE_PLAYER));
    }

    public static UserListItemMessage removeOne(UUID uuid) {
        return new UserListItemMessage(Lists.newArrayList(Action.REMOVE_PLAYER), remove(uuid));
    }

    // inner classes

    /**
     * The actions that can be performed in a user list update
     * The bitFieldIndex field is the index of the action fla
     */
    public enum Action {
        ADD_PLAYER(0),
        UPDATE_GAMEMODE(2),
        REMOVE_PLAYER(3),
        UPDATE_LATENCY(4),
        UPDATE_DISPLAY_NAME(5);
        private int bitFieldIndex;
        private Action(int bitIndex) {
            this.bitFieldIndex = bitIndex;
        }
        public int getBitFieldIndex() {
            return bitFieldIndex;
        }
    }

    @Data
    @RequiredArgsConstructor
    public static final class Entry {

        public final UUID uuid;
        public final GlowPlayerProfile profile;
        public final int gameMode;
        public final int ping;
        public final TextMessage displayName;
        private final List<Action> actions;
        public final long timestamp;
        public final byte[] publicKey;
        public final byte[] signature;

        public Entry(UUID uuid, GlowPlayerProfile profile, int gameMode, int ping, TextMessage displayName, List<Action> actions) {
            this(uuid, profile, gameMode, ping, displayName, actions, 0, null, null);
        }
    }
}
