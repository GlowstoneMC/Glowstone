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
    private static final Map<UUID, PlayerProfile> profileCache = new HashMap<>();

    /**
     * Look up the PlayerProfile for a given UUID.
     *
     * @param uuid The UUID to look up.
     * @return A PlayerProfile future, contains a null name if the lookup failed.
     */
    public static CompletableFuture<PlayerProfile> getProfile(UUID uuid) {
        if (profileCache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(profileCache.get(uuid));
        }
        CompletableFuture<PlayerProfile> profileFuture = CompletableFuture
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
}
