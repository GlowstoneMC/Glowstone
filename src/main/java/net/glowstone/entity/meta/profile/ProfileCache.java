package net.glowstone.entity.meta.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Cached methods for accessing Mojang servers to find UUIDs and player profiles.
 */
public class ProfileCache {

    private static final Map<String, UUID> uuidCache = new HashMap<>();
    private static final Map<UUID, GlowPlayerProfile> profileCache = new HashMap<>();

    /**
     * Look up the GlowPlayerProfile for a given UUID.
     *
     * @param uuid The UUID to look up.
     * @return A GlowPlayerProfile future, contains a null name if the lookup failed.
     */
    public static CompletableFuture<GlowPlayerProfile> getProfile(UUID uuid) {
        if (profileCache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(profileCache.get(uuid));
        }
        CompletableFuture<GlowPlayerProfile> profileFuture = CompletableFuture
                .supplyAsync(() -> PlayerDataFetcher.getProfile(uuid));
        profileFuture.thenAccept(profile -> profileCache.put(uuid, profile));
        return profileFuture;
    }

    /**
     * Look up the UUID for a given username.
     *
     * @param playerName The name to look up.
     * @return A UUID future, UUID may be null on failure.
     */
    public static CompletableFuture<UUID> getUuid(String playerName) {
        if (uuidCache.containsKey(playerName)) {
            return CompletableFuture.completedFuture(uuidCache.get(playerName));
        }
        CompletableFuture<UUID> uuidFuture = CompletableFuture
                .supplyAsync(() -> PlayerDataFetcher.getUuid(playerName));
        uuidFuture.thenAccept(uid -> uuidCache.put(playerName, uid));
        return uuidFuture;
    }

    /**
     * Look up the UUID for a given username, but only in the cache and not on the Mojang server.
     *
     * @param playerName The name to look up.
     * @return A UUID, or null if it's not found in the cache.
     */
    public static UUID getUuidCached(String playerName) {
        return uuidCache.get(playerName);
    }
}
