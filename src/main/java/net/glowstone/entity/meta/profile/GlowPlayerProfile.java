package net.glowstone.entity.meta.profile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.util.UuidUtils;
import net.glowstone.util.nbt.CompoundTag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Information about a player's name, UUID, and other properties.
 */
public class GlowPlayerProfile implements PlayerProfile {

    public static final int MAX_USERNAME_LENGTH = 16;
    @Getter
    private final String name;
    private final CompletableFuture<UUID> uniqueId;
    private final Map<String, ProfileProperty> properties;

    private static CompletableFuture<UUID> lookUpIfNull(String name, UUID maybeUuid) {
        return maybeUuid == null ? ProfileCache.getUuid(name) : CompletableFuture.completedFuture(
                maybeUuid);
    }

    /**
     * Construct a new profile with only a name and UUID.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name The player's name.
     * @param uuid The player's UUID; may be null.
     */
    public GlowPlayerProfile(String name, UUID uuid) {
        this(name, lookUpIfNull(name, uuid), Collections.emptySet());
    }

    /**
     * Construct a new profile with additional properties.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name The player's name.
     * @param uuid The player's UUID; may be null.
     * @param properties A list of extra properties.
     * @throws IllegalArgumentException if properties are null.
     */
    public GlowPlayerProfile(String name, UUID uuid, Collection<ProfileProperty> properties) {
        this(name, lookUpIfNull(name, uuid), properties);
    }

    /**
     * Construct a new profile with additional properties.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name The player's name.
     * @param uuid Lookup of the player's UUID.
     * @param properties A list of extra properties.
     * @throws IllegalArgumentException if uuid or properties are null.
     */
    private GlowPlayerProfile(String name, CompletableFuture<UUID> uuid,
            Collection<ProfileProperty> properties) {
        checkNotNull(properties, "properties must not be null");
        checkNotNull(uuid, "uuid must not be null");
        this.name = name;
        this.uniqueId = uuid;
        this.properties = Maps.newHashMap();
        properties.forEach((property) -> this.properties.put(property.getName(), property));
    }

    /**
     * Get the profile for a username.
     *
     * @param name The username to lookup.
     * @return A GlowPlayerProfile future. May be null if the name could not be resolved.
     */
    public static CompletableFuture<GlowPlayerProfile> getProfile(String name) {
        if (name == null || name.length() > MAX_USERNAME_LENGTH || name.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        GlowServer server = (GlowServer) ServerProvider.getServer();
        if (server.getOnlineMode() || server.getProxySupport()) {
            return ProfileCache.getUuid(name).thenComposeAsync((uuid) -> {
                if (uuid == null) {
                    return CompletableFuture.completedFuture(null);
                } else {
                    return ProfileCache.getProfile(uuid);
                }
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Get the profile from a NBT tag (e.g. skulls). Missing information is fetched asynchronously.
     *
     * @param tag The NBT tag containing profile information.
     * @return A GlowPlayerProfile future. May contain a null name if the lookup failed.
     */
    public static CompletableFuture<GlowPlayerProfile> fromNbt(CompoundTag tag) {
        // NBT: {Id: "", Name: "", Properties: {textures: [{Signature: "", Value: {}}]}}
        UUID uuid = UUID.fromString(tag.getString("Id"));

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
            return CompletableFuture.completedFuture(
                    new GlowPlayerProfile(tag.getString("Name"), uuid, properties));
        } else {
            return ProfileCache.getProfile(uuid).thenApplyAsync(
                (profile) -> new GlowPlayerProfile(profile.getName(), uuid, properties));
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

        return new GlowPlayerProfile(name, uuid, properties);
    }

    /**
     * Converts this player profile to an NBT compound tag.
     *
     * @return an NBT compound tag that's a copy of this player profile
     */
    public CompoundTag toNbt() {
        CompoundTag profileTag = new CompoundTag();
        profileTag.putString("Id", uniqueId.toString());
        profileTag.putString("Name", name);

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

    @Override
    public UUID getId() {
        return uniqueId.getNow(null);
    }

    /**
     * Waits for the lookup of, then returns, the player's UUID.
     *
     * @return the player UUID, or null if it's unknown and couldn't be looked up
     */
    public UUID getIdBlocking() {
        return uniqueId.join();
    }

    @Override
    public Set<ProfileProperty> getProperties() {
        return Sets.newHashSet(this.properties.values());
    }

    @Override
    public void setProperty(ProfileProperty property) {
        checkNotNull(property);
        this.properties.put(property.getName(), property);
    }

    @Override
    public void setProperties(Collection<ProfileProperty> properties) {
        clearProperties();
        if (properties != null) {
            properties.forEach(property -> this.properties.put(property.getName(), property));
        }
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
        return name != null && uniqueId.isDone()
                && uniqueId.join() != null && properties.containsKey("textures");
    }
}
