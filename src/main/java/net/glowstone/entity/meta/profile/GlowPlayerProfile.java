package net.glowstone.entity.meta.profile;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.util.UuidUtils;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Information about a player's name, UUID, and other properties.
 */
public class GlowPlayerProfile implements PlayerProfile {

    public static final int MAX_USERNAME_LENGTH = 16;
    private final Map<String, ProfileProperty> properties;
    @Getter
    @Nullable
    private String name;
    @Nullable
    private volatile CompletableFuture<UUID> uniqueId;

    /**
     * Construct a new profile with only a name and UUID.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name        The player's name.
     * @param uuid        The player's UUID; may be null.
     * @param asyncLookup If true and {@code uuid} is null, the UUID is looked up asynchronously.
     */
    public GlowPlayerProfile(String name, UUID uuid, boolean asyncLookup) {
        this(name, maybeLookUpNull(name, uuid, asyncLookup), Collections.emptySet());
    }

    /**
     * Construct a new profile with additional properties.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name        The player's name.
     * @param uuid        The player's UUID; may be null.
     * @param properties  A list of extra properties.
     * @param asyncLookup If true and {@code uuid} is null, the UUID is looked up asynchronously
     *                    even if it's not in cache.
     * @throws IllegalArgumentException if properties are null.
     */
    public GlowPlayerProfile(String name, UUID uuid, Collection<ProfileProperty> properties,
                             boolean asyncLookup) {
        this(name, maybeLookUpNull(name, uuid, asyncLookup), properties);
    }

    /**
     * Construct a new profile with additional properties.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name       The player's name.
     * @param uuid       Lookup of the player's UUID.
     * @param properties A list of extra properties.
     * @throws IllegalArgumentException if uuid or properties are null.
     */
    private GlowPlayerProfile(String name, CompletableFuture<UUID> uuid,
                              Collection<ProfileProperty> properties) {
        checkNotNull(properties, "properties must not be null");
        this.name = name;
        this.uniqueId = uuid;
        this.properties = Maps.newHashMap();
        properties.forEach((property) -> this.properties.put(property.getName(), property));
    }

    private static CompletableFuture<UUID> maybeLookUpNull(String name, UUID maybeUuid,
                                                           boolean asyncLookup) {
        if (maybeUuid == null) {
            if (asyncLookup) {
                return ProfileCache.getUuid(name);
            }
            UUID maybeCachedUuid = ProfileCache.getUuidCached(name);
            return maybeCachedUuid == null ? null : completedFuture(maybeCachedUuid);
        } else {
            return completedFuture(maybeUuid);
        }
    }

    /**
     * Get the profile for a username.
     *
     * @param name The username to lookup.
     * @return A GlowPlayerProfile future. May be null if the name could not be resolved.
     */
    public static CompletableFuture<GlowPlayerProfile> getProfile(String name) {
        if (name == null || name.length() > MAX_USERNAME_LENGTH || name.isEmpty()) {
            return completedFuture(null);
        }

        GlowServer server = (GlowServer) ServerProvider.getServer();
        if (server.getOnlineMode() || server.getProxySupport()) {
            return ProfileCache.getUuid(name).thenComposeAsync((uuid) -> {
                if (uuid == null) {
                    return completedFuture(null);
                } else {
                    return ProfileCache.getProfile(uuid);
                }
            });
        }
        return completedFuture(null);
    }

    /**
     * Get the profile from a NBT tag (e.g. skulls). Missing information is fetched asynchronously.
     *
     * @param tag The NBT tag containing profile information.
     * @return A GlowPlayerProfile future. May contain a null name if the lookup failed.
     */
    public static CompletableFuture<GlowPlayerProfile> fromNbt(CompoundTag tag) {
        // NBT: {Id: "", Name: "", Properties: {textures: [{Signature: "", Value: {}}]}}
        UUID uuid = UuidUtils.fromString(tag.getString("Id"));

        Collection<ProfileProperty> properties = Sets.newHashSet();
        if (tag.containsKey("Properties")) {
            CompoundTag texture = tag.getCompound("Properties").getCompoundList("textures").get(0);
            if (texture.containsKey("Signature")) {
                properties.add(new ProfileProperty("textures", texture.getString("Value"),
                    texture.getString("Signature")));
            } else {
                properties.add(new ProfileProperty("textures", texture.getString("Value")));
            }
        }

        if (tag.containsKey("Name")) {
            return completedFuture(
                new GlowPlayerProfile(tag.getString("Name"), uuid, properties, true));
        } else {
            return ProfileCache.getProfile(uuid).thenApplyAsync(
                (profile) -> new GlowPlayerProfile(profile.getName(), uuid, properties, true));
        }
    }

    /**
     * Reads a GlowPlayerProfile from a JSON object.
     *
     * @param json a player profile in JSON form
     * @return {@code json} as a GlowPlayerProfile
     */
    public static GlowPlayerProfile fromJson(JSONObject json) {
        String name = (String) json.get("name");
        String id = (String) json.get("id");
        JSONArray propsArray = (JSONArray) json.get("properties");

        // Parse UUID
        UUID uuid;
        try {
            uuid = UuidUtils.fromFlatString(id);
        } catch (IllegalArgumentException ex) {
            GlowServer.logger.log(Level.SEVERE, "Returned authentication UUID invalid: " + id);
            return null;
        }

        // Parse properties
        Collection<ProfileProperty> properties = new HashSet<>(propsArray.size());
        for (Object obj : propsArray) {
            JSONObject propJson = (JSONObject) obj;
            String propName = (String) propJson.get("name");
            String value = (String) propJson.get("value");
            String signature = (String) propJson.get("signature");
            properties.add(new ProfileProperty(propName, value, signature));
        }

        return new GlowPlayerProfile(name, uuid, properties, true);
    }

    /**
     * Converts this player profile to an NBT compound tag.
     *
     * @return an NBT compound tag that's a copy of this player profile
     */
    public CompoundTag toNbt() {
        CompoundTag profileTag = new CompoundTag();
        UUID uuid = getId();
        if (uuid != null) {
            profileTag.putString("Id", uuid.toString());
        }
        if (name != null) {
            profileTag.putString("Name", name);
        }

        CompoundTag propertiesTag = new CompoundTag();
        for (ProfileProperty property : properties.values()) {
            CompoundTag propertyValueTag = new CompoundTag();
            if (property.isSigned()) {
                propertyValueTag.putString("Signature", property.getSignature());
            }
            propertyValueTag.putString("Value", property.getValue());

            propertiesTag.putCompoundList(property.getName(),
                Collections.singletonList(propertyValueTag));
        }
        if (!propertiesTag.isEmpty()) { // Only add properties if not empty
            profileTag.putCompound("Properties", propertiesTag);
        }
        return profileTag;
    }

    private void checkOwnerCriteria(String name, UUID id) {
        if (id == null && (name == null || name.isEmpty())) {
            throw new IllegalArgumentException("Either name or uuid must be present in profile");
        }
    }

    @Override
    public String setName(@Nullable String name) {
        checkOwnerCriteria(name, getId());
        String oldname = this.name;
        this.name = name;
        return oldname;
    }

    @Override
    public UUID getId() {
        return uniqueId == null ? null : uniqueId.getNow(null);
    }

    @Override
    public UUID setId(@Nullable UUID uuid) {
        checkOwnerCriteria(name, uuid);
        UUID oldUuid;
        if (uniqueId == null) {
            synchronized (this) {
                if (uniqueId == null) {
                    uniqueId = CompletableFuture.completedFuture(uuid);
                    return null;
                }
                oldUuid = uniqueId.getNow(null);
            }
        } else {
            oldUuid = uniqueId.getNow(null);
        }
        if (!uniqueId.complete(uuid)) {
            uniqueId.obtrudeValue(uuid);
        }
        return oldUuid;
    }

    @Override
    public @org.jetbrains.annotations.Nullable UUID getUniqueId() {
        return null;
    }

    @Override
    public @NotNull PlayerTextures getTextures() {
        return null;
    }

    @Override
    public void setTextures(@org.jetbrains.annotations.Nullable PlayerTextures textures) {

    }

    /**
     * Waits for the lookup of, then returns, the player's UUID.
     *
     * @return the player UUID, or null if it's unknown and couldn't be looked up
     */
    public UUID getIdBlocking() {
        complete();
        return getId();
    }

    @Override
    public Set<ProfileProperty> getProperties() {
        return Sets.newHashSet(this.properties.values());
    }

    @Override
    public void setProperties(Collection<ProfileProperty> properties) {
        clearProperties();
        if (properties != null) {
            properties.forEach(property -> this.properties.put(property.getName(), property));
        }
    }

    @Override
    public boolean hasProperty(String property) {
        return properties.containsKey(property);
    }

    @Override
    public void setProperty(ProfileProperty property) {
        checkNotNull(property);
        this.properties.put(property.getName(), property);
    }

    @Override
    public boolean removeProperty(String name) {
        checkNotNull(name);
        return this.properties.remove(name) != null;
    }

    @Override
    public boolean removeProperty(ProfileProperty property) {
        checkNotNull(property);
        return removeProperty(property.getName());
    }

    @Override
    public boolean removeProperties(Collection<ProfileProperty> properties) {
        checkNotNull(properties);
        boolean foundAll = true;
        for (ProfileProperty property : properties) {
            if (!removeProperty(property)) {
                foundAll = false;
            }
        }
        return foundAll;
    }

    @Override
    public void clearProperties() {
        this.properties.clear();
    }

    /**
     * {@inheritDoc}
     *
     * <p>A player profile that's currently incomplete may become complete later, because UUIDs are
     * looked up asynchronously when needed.
     */
    @Override
    public boolean isComplete() {
        return name != null && getId() != null && properties.containsKey("textures");
    }

    @Override
    public @NotNull CompletableFuture<PlayerProfile> update() {
        return null;
    }

    @Override
    public org.bukkit.profile.@NotNull PlayerProfile clone() {
        return null;
    }

    @Override
    public boolean completeFromCache() {
        return completeCached();
    }

    @Override
    public boolean completeFromCache(boolean onlineMode) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean completeFromCache(boolean lookupUuid, boolean onlineMode) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Looks up the UUID if it's missing and hasn't already been attempted, and waits for it.
     *
     * @return true if the profile {@link #isComplete()} when done; false otherwise
     */
    public boolean complete() {
        completeAsync();
        uniqueId.join();
        return isComplete();
    }

    @Override
    public boolean complete(boolean textures) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean complete(boolean textures, boolean onlineMode) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Looks up the UUID asynchronously if it's missing and hasn't already been attempted. Returns
     * immediately.
     */
    public void completeAsync() {
        if (uniqueId == null) {
            synchronized (this) {
                if (uniqueId == null) {
                    uniqueId = ProfileCache.getUuid(name);
                }
            }
        }
    }

    /**
     * Looks up the UUID in cache, if it's missing and hasn't already been attempted.
     *
     * @return true if the profile {@link #isComplete()} when done; false otherwise
     */
    public boolean completeCached() {
        if (uniqueId == null) {
            synchronized (this) {
                if (uniqueId == null) {
                    UUID maybeCachedUuid = ProfileCache.getUuidCached(name);
                    if (maybeCachedUuid != null) {
                        uniqueId = completedFuture(maybeCachedUuid);
                    } else {
                        return false;
                    }
                }
            }
        }
        return isComplete();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return null;
    }
}
