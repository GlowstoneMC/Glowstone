package net.glowstone.entity.meta.profile;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import lombok.Data;
import net.glowstone.GlowServer;
import net.glowstone.util.UuidUtils;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Information about a player's name, UUID, and other properties.
 */
@Data
public class PlayerProfile {

    public static final int MAX_USERNAME_LENGTH = 16;
    private final String name;
    private final UUID uniqueId;
    private final List<PlayerProperty> properties;

    /**
     * Construct a new profile with only a name and UUID.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name The player's name.
     * @param uuid The player's UUID.
     * @throws IllegalArgumentException if uuid is null.
     */
    public PlayerProfile(String name, UUID uuid) {
        this(name, uuid, Collections.emptyList());
    }

    /**
     * Construct a new profile with additional properties.
     *
     * <p>This does not try to resolve the name if it's null.
     *
     * @param name The player's name.
     * @param uuid The player's UUID.
     * @param properties A list of extra properties.
     * @throws IllegalArgumentException if uuid or properties are null.
     */
    public PlayerProfile(String name, UUID uuid, List<PlayerProperty> properties) {
        checkNotNull(uuid, "uuid must not be null");
        checkNotNull(properties, "properties must not be null");

        this.name = name;
        this.uniqueId = uuid;
        this.properties = properties;
    }

    /**
     * Get the profile for a username.
     *
     * @param name The username to lookup.
     * @return A PlayerProfile future. May be null if the name could not be resolved.
     */
    public static CompletableFuture<PlayerProfile> getProfile(String name) {
        if (name == null || name.length() > MAX_USERNAME_LENGTH || name.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        if (Bukkit.getServer().getOnlineMode()
                || ((GlowServer) Bukkit.getServer()).getProxySupport()) {
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
     * @return A PlayerProfile future. May contain a null name if the lookup failed.
     */
    public static CompletableFuture<PlayerProfile> fromNbt(CompoundTag tag) {
        // NBT: {Id: "", Name: "", Properties: {textures: [{Signature: "", Value: {}}]}}
        UUID uuid = UUID.fromString(tag.getString("Id"));

        List<PlayerProperty> properties = new ArrayList<>();
        if (tag.containsKey("Properties")) {
            CompoundTag texture = tag.getCompound("Properties").getCompoundList("textures").get(0);
            if (texture.containsKey("Signature")) {
                properties.add(new PlayerProperty("textures", texture.getString("Value"),
                    texture.getString("Signature")));
            } else {
                properties.add(new PlayerProperty("textures", texture.getString("Value")));
            }
        }

        if (tag.containsKey("Name")) {
            return CompletableFuture.completedFuture(
                    new PlayerProfile(tag.getString("Name"), uuid, properties));
        } else {
            return ProfileCache.getProfile(uuid).thenApplyAsync(
                (profile) -> new PlayerProfile(profile.getName(), uuid, properties));
        }
    }

    /**
     * Reads a PlayerProfile from a JSON object.
     *
     * @param json a player profile in JSON form
     * @return {@code json} as a PlayerProfile
     */
    public static PlayerProfile fromJson(JSONObject json) {
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
        List<PlayerProperty> properties = new ArrayList<>(propsArray.size());
        for (Object obj : propsArray) {
            JSONObject propJson = (JSONObject) obj;
            String propName = (String) propJson.get("name");
            String value = (String) propJson.get("value");
            String signature = (String) propJson.get("signature");
            properties.add(new PlayerProperty(propName, value, signature));
        }

        return new PlayerProfile(name, uuid, properties);
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
        for (PlayerProperty property : properties) {
            CompoundTag propertyValueTag = new CompoundTag();
            if (property.isSigned()) {
                propertyValueTag.putString("Signature", property.getSignature());
            }
            propertyValueTag.putString("Value", property.getValue());

            propertiesTag.putCompoundList(property.getName(), Arrays.asList(propertyValueTag));
        }
        if (!propertiesTag.isEmpty()) { // Only add properties if not empty
            profileTag.putCompound("Properties", propertiesTag);
        }
        return profileTag;
    }

}
