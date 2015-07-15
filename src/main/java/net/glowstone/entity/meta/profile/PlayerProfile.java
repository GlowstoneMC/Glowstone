package net.glowstone.entity.meta.profile;

import lombok.Data;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.UuidUtils;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.logging.Level;

/**
 * Information about a player's name, UUID, and other properties.
 */
@Data
public final class PlayerProfile {

    private final String name;
    private final UUID uniqueId;
    private final List<PlayerProperty> properties;

    public static final int MAX_USERNAME_LENGTH = 16;

    /**
     * Construct a new profile with only a name and UUID.
     * @param name The player's name.
     * @param uuid The player's UUID.
     */
    public PlayerProfile(String name, UUID uuid) {
        this(name, uuid, Collections.<PlayerProperty>emptyList());
    }

    /**
     * Construct a new profile with additional properties.
     * @param name The player's name.
     * @param uuid The player's UUID.
     * @param properties A list of extra properties.
     * @throws IllegalArgumentException if any arguments are null.
     */
    public PlayerProfile(String name, UUID uuid, List<PlayerProperty> properties) {
        Validate.notNull(uuid, "uuid must not be null");
        Validate.notNull(properties, "properties must not be null");

        if (null == name) {
            PlayerProfile profile = ProfileCache.getProfile(uuid);
            name = profile != null ? profile.getName() : null;
        }

        this.name = name;
        this.uniqueId = uuid;
        this.properties = properties;
    }

    /**
     * Get the profile for a username.
     * @param name The username to lookup.
     * @return The profile.
     */
    public static PlayerProfile getProfile(String name) {
        if (name == null || name.length() > MAX_USERNAME_LENGTH || name.isEmpty()) {
            return null;
        }

        Player player = ((GlowServer) Bukkit.getServer()).getPlayer(name);
        if (player != null) {
            return ((GlowPlayer) player).getProfile();
        }

        UUID uuid = ProfileCache.getUUID(name);
        if (uuid != null) {
            return ProfileCache.getProfile(uuid);
        }
        GlowServer.logger.warning("Unable to get UUID for username: " + name);
        return null;
    }

    public CompoundTag toNBT() {
        CompoundTag profileTag = new CompoundTag();
        profileTag.putString("Id", uniqueId.toString());
        profileTag.putString("Name", name);

        CompoundTag propertiesTag = new CompoundTag();
        for (PlayerProperty property : properties) {
            CompoundTag propertyValueTag = new CompoundTag();
            propertyValueTag.putString("Signature", property.getSignature());
            propertyValueTag.putString("Value", property.getValue());

            propertiesTag.putCompoundList(property.getName(), Arrays.asList(propertyValueTag));
        }
        if (!propertiesTag.isEmpty()) { // Only add properties if not empty
            profileTag.putCompound("Properties", propertiesTag);
        }
        return profileTag;
    }

    public static PlayerProfile fromNBT(CompoundTag tag) {
        // NBT: {Id: "", Name: "", Properties: {textures: [{Signature: "", Value: {}}]}}
        String uuidStr = tag.getString("Id");
        String name = tag.getString("Name");

        List<PlayerProperty> properties = new ArrayList<>();
        if (tag.containsKey("Properties")) {
            for (Map.Entry<String, Tag> property : tag.getCompound("Properties").getValue().entrySet()) {
                @SuppressWarnings("unchecked")
                CompoundTag propertyValueTag = ((List<CompoundTag>) property.getValue().getValue()).get(0);
                properties.add(new PlayerProperty(property.getKey(), propertyValueTag.getString("Value"), propertyValueTag.getString("Signature")));
            }
        }
        return new PlayerProfile(name, UUID.fromString(uuidStr), properties);
    }

    public static PlayerProfile fromJson(JSONObject json) {
        final String name = (String) json.get("name");
        final String id = (String) json.get("id");
        final JSONArray propsArray = (JSONArray) json.get("properties");

        // Parse UUID
        final UUID uuid;
        try {
            uuid = UuidUtils.fromFlatString(id);
        } catch (IllegalArgumentException ex) {
            GlowServer.logger.log(Level.SEVERE, "Returned authentication UUID invalid: " + id);
            return null;
        }

        // Parse properties
        final List<PlayerProperty> properties = new ArrayList<>(propsArray.size());
        for (Object obj : propsArray) {
            JSONObject propJson = (JSONObject) obj;
            String propName = (String) propJson.get("name");
            String value = (String) propJson.get("value");
            String signature = (String) propJson.get("signature");
            properties.add(new PlayerProperty(propName, value, signature));
        }

        return new PlayerProfile(name, uuid, properties);
    }

}
