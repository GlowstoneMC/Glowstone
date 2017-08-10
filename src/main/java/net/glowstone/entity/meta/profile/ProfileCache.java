package net.glowstone.entity.meta.profile;

import net.glowstone.GlowServer;
import net.glowstone.util.lang.I;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/**
 * Cached methods for accessing Mojang servers to find UUIDs and player profiles.
 */
public class ProfileCache {
    private static Map<String, UUID> uuidCache = new HashMap<>();
    private static Map<UUID, PlayerProfile> profileCache = new HashMap<>();

    /**
     * Look up the PlayerProfile for a given UUID.
     *
     * @param uuid The UUID to look up.
     * @return The resulting PlayerProfile, or null on failure.
     */
    public static PlayerProfile getProfile(UUID uuid) {
        if (profileCache.containsKey(uuid)) {
            return profileCache.get(uuid);
        }
        profileCache.put(uuid, PlayerDataFetcher.getProfile(uuid));
        return profileCache.get(uuid);
    }

    /**
     * Look up the UUID for a given username.
     *
     * @param playerName The name to look up.
     * @return The UUID, or null on failure.
     */
    public static UUID getUUID(String playerName) {
        if (uuidCache.containsKey(playerName)) {
            return uuidCache.get(playerName);
        }
        UUID uuid = null;
        CompletableFuture<UUID> uuidFuture = CompletableFuture.supplyAsync(() -> PlayerDataFetcher.getUUID(playerName));
        uuidFuture.thenAcceptAsync(uid -> uuidCache.put(playerName, uid));
        try {
            uuid = uuidFuture.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            GlowServer.logger.log(Level.SEVERE, I.tr("error.entity.profile.cache.interrupted"), e);
        } catch (TimeoutException e) {
            GlowServer.logger.log(Level.SEVERE, I.tr("error.entity.profile.cache.timeout"), e);
        }
        return uuid;
    }
}
