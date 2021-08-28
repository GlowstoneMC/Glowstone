package net.glowstone;

import lombok.Getter;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.entity.meta.profile.ProfileCache;
import net.glowstone.io.PlayerDataService.PlayerReader;
import net.glowstone.util.StatisticMap;
import net.glowstone.util.UuidUtils;
import org.bukkit.BanList.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a player which is not connected to the server.
 */
@SerializableAs("Player")
public final class GlowOfflinePlayer implements OfflinePlayer {

    private final GlowServer server;
    @Getter
    private final GlowPlayerProfile profile;
    private boolean hasPlayed;
    @Getter
    private long firstPlayed;
    @Getter
    private long lastPlayed;
    @Getter
    private long lastLogin;
    private String lastName;
    @Getter
    private Location bedSpawnLocation;
    /**
     * The player's statistics, and related data.
     */
    private final StatisticMap stats = new StatisticMap();

    /**
     * Create a new offline player for the given name. If possible, the player's data will be
     * loaded.
     *
     * @param server The server of the offline player. Must not be null.
     * @param profile The profile associated with the player. Must not be null.
     */
    public GlowOfflinePlayer(GlowServer server, GlowPlayerProfile profile) {
        checkNotNull(server, "server must not be null"); // NON-NLS
        checkNotNull(profile, "profile must not be null"); // NON-NLS
        this.server = server;
        this.profile = profile;
        loadData();
    }

    /**
     * Returns a Future for a GlowOfflinePlayer by UUID. If possible, the player's data (including
     * name) will be loaded based on the UUID.
     *
     * @param server The server of the offline player. Must not be null.
     * @param uuid The UUID of the player. Must not be null.
     * @return A {@link GlowOfflinePlayer} future.
     */
    public static CompletableFuture<GlowOfflinePlayer> getOfflinePlayer(GlowServer server,
            UUID uuid) {
        checkNotNull(server, "server must not be null"); // NON-NLS
        checkNotNull(uuid, "UUID must not be null"); // NON-NLS
        return ProfileCache.getProfile(uuid)
                .thenApplyAsync((profile) -> new GlowOfflinePlayer(server, profile));
    }

    /**
     * Required method for configuration serialization.
     *
     * @param val map to deserialize
     * @return deserialized player record
     * @see org.bukkit.configuration.serialization.ConfigurationSerializable
     */
    @SuppressWarnings("UnusedDeclaration")
    public static OfflinePlayer deserialize(Map<String, Object> val) {
        Server server = ServerProvider.getServer();
        if (val.get("name") != null) { // NON-NLS
            // use name
            return server.getOfflinePlayer(val.get("name").toString()); // NON-NLS
        } else {
            // use UUID
            return server.getOfflinePlayer(
                    UuidUtils.fromString(val.get("UUID").toString())); // NON-NLS
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

    private void loadData() {
        profile.completeCached();
        try (PlayerReader reader = server.getPlayerDataService().beginReadingData(getUniqueId())) {
            hasPlayed = reader.hasPlayedBefore();
            if (hasPlayed) {
                firstPlayed = reader.getFirstPlayed();
                lastPlayed = reader.getLastPlayed();
                lastLogin = reader.getLastLogin();
                bedSpawnLocation = reader.getBedSpawnLocation();

                String lastName = reader.getLastKnownName();
                if (lastName != null) {
                    this.lastName = lastName;
                }
            }
        }
    }

    @Override
    public String getName() {
        Player player = getPlayer();
        if (player != null) {
            return player.getName();
        }
        if (profile.getName() != null) {
            return profile.getName();
        }
        if (lastName != null) {
            return lastName;
        }
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return profile.getId();
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player properties

    @Override
    public Player getPlayer() {
        return server.getPlayer(getUniqueId());
    }

    @Override
    public boolean hasPlayedBefore() {
        return hasPlayed;
    }

    @Override
    public long getLastSeen() {
        return lastPlayed;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Ban, op, whitelist

    @Override
    public boolean isBanned() {
        return server.getBanList(Type.NAME).isBanned(getName());
    }

    @Override
    public boolean isWhitelisted() {
        return server.getWhitelist().containsProfile(profile);
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(this);
        } else {
            server.getWhitelist().remove(profile);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Statistics

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return stats.get(statistic);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return stats.get(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return stats.get(statistic, entityType);
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
        stats.set(statistic, newValue);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material,
                             int newValue) throws IllegalArgumentException {
        stats.set(statistic, material, newValue);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        stats.set(statistic, entityType, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) {
        stats.add(statistic, amount);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) {
        incrementStatistic(statistic, material, 1);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        stats.add(statistic, material, amount);
    }

    @Override
    public void incrementStatistic(Statistic statistic,
                                   EntityType entityType) throws IllegalArgumentException {
        incrementStatistic(statistic, entityType, 1);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType,
                                   int amount) throws IllegalArgumentException {
        stats.add(statistic, entityType, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        stats.add(statistic, -1);
    }

    @Override
    public void decrementStatistic(Statistic statistic,
                                   int amount) throws IllegalArgumentException {
        stats.add(statistic, -amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic,
                                   Material material) throws IllegalArgumentException {
        stats.add(statistic, material, -1);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material,
                                   int amount) throws IllegalArgumentException {
        stats.add(statistic, material, -amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic,
                                   EntityType entityType) throws IllegalArgumentException {
        stats.add(statistic, entityType, -1);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        stats.add(statistic, entityType, -amount);
    }

    public StatisticMap getStatisticMap() {
        return stats;
    }

    @Override
    public boolean isOp() {
        return server.getOpsList().containsUuid(getUniqueId());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Serialization

    @Override
    public void setOp(boolean value) {
        if (value) {
            server.getOpsList().add(this);
        } else {
            server.getOpsList().remove(profile);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("UUID", UuidUtils.toString(getUniqueId())); // NON-NLS
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GlowOfflinePlayer that = (GlowOfflinePlayer) o;

        return profile.equals(that.profile);
    }

    public int hashCode() {
        return getUniqueId() != null ? getUniqueId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GlowOfflinePlayer{" + "name='" + getName() + '\'' + ", uuid="
                + UuidUtils.toString(getUniqueId()) + '}';
    }
}
