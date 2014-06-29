package net.glowstone.entity.meta;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Information about a player's name, UUID, and other properties.
 */
public final class PlayerProfile {

    private final String name;
    private final UUID uuid;
    private final List<PlayerProperty> properties;

    /**
     * Construct a new profile with only a name and UUID.
     * @param name The player's name.
     * @param uuid The player's UUID.
     */
    public PlayerProfile(String name, UUID uuid) {
        this(name, uuid, new ArrayList<PlayerProperty>(0));
    }

    /**
     * Construct a new profile with additional properties.
     * @param name The player's name.
     * @param uuid The player's UUID.
     * @param properties A list of extra properties.
     * @throws IllegalArgumentException if any arguments are null.
     */
    public PlayerProfile(String name, UUID uuid, List<PlayerProperty> properties) {
        Validate.notNull(name, "name must not be null");
        Validate.notNull(uuid, "uuid must not be null");
        Validate.notNull(properties, "properties must not be null");
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
    }

    /**
     * Get the profile's name.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the profile's unique identifier.
     * @return The UUID.
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Get a list of the profile's extra properties. May be empty.
     * @return The property list.
     */
    public List<PlayerProperty> getProperties() {
        return properties;
    }

    public String toString() {
        return "PlayerProfile{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerProfile that = (PlayerProfile) o;

        if (!name.equals(that.name)) return false;
        if (!properties.equals(that.properties)) return false;
        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + uuid.hashCode();
        result = 31 * result + properties.hashCode();
        return result;
    }
}
