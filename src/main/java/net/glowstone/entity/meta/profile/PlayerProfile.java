package net.glowstone.entity.meta.profile;

import lombok.Data;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.UuidUtils;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;

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
     * @param name The player's name.
     * @param uuid The player's UUID.
     */
    public PlayerProfile(String name, UUID uuid) {
        this(name, uuid, Collections.emptyList());
    }

    /**
     * Construct a new profile with additional properties.
     *
     * @param name       The player's name.
     * @param uuid       The player's UUID.
     * @param properties A list of extra properties.
     * @throws IllegalArgumentException if any arguments are null.
     */
    public PlayerProfile(String name, UUID uuid, List<PlayerProperty> properties) {
        checkNotNull(uuid, "uuid must not be null");
        checkNotNull(properties, "properties must not be null");

        if (null == name) {
            PlayerProfile profile = ProfileCache.getProfile(uuid);
            name = profile != null ? profile.getName() : null;
        }

        this.name = name;
        uniqueId = uuid;
        this.properties = properties;
    }

    /**
     * Get the profile for a username.
     *
     * @param name The username to lookup.
     * @return The profile.
     */
    public static PlayerProfile getProfile(String name) {
        if (name == null || name.length() > MAX_USERNAME_LENGTH || name.isEmpty()) {
            return null;
        }

        Player player = Bukkit.getServer().getPlayer(name);
        if (player != null) {
            return ((GlowPlayer) player).getProfile();
        }

        UUID uuid = ProfileCache.getUUID(name);
        if (uuid != null) {
            return ProfileCache.getProfile(uuid);
        }
        GlowServer.logger.warning(GlowServer.lang.getString("warning.entity.profile.uuid", name));
        return null;
    }

    public static PlayerProfile fromNBT(CompoundTag tag) {
        // NBT: {Id: "", Name: "", Properties: {textures: [{Signature: "", Value: {}}]}}
        String uuidStr = tag.getString("Id");
        String name;
        if (tag.containsKey("Name")) {
            name = tag.getString("Name");
        } else {
            name = ProfileCache.getProfile(UUID.fromString(uuidStr)).getName();
        }

        List<PlayerProperty> properties = new ArrayList<>();
        if (tag.containsKey("Properties")) {
            CompoundTag texture = tag.getCompound("Properties").getCompoundList("textures").get(0);
            if (texture.containsKey("Signature")) {
                properties.add(new PlayerProperty("textures", texture.getString("Value"), texture.getString("Signature")));
            } else {
                properties.add(new PlayerProperty("textures", texture.getString("Value")));
            }
        }
        return new PlayerProfile(name, UUID.fromString(uuidStr), properties);
    }

    public static PlayerProfile fromJson(JSONObject json) {
        String name = (String) json.get("name");
        String id = (String) json.get("id");
        JSONArray propsArray = (JSONArray) json.get("properties");

        // Parse UUID
        UUID uuid;
        try {
            uuid = UuidUtils.fromFlatString(id);
        } catch (IllegalArgumentException ex) {
            GlowServer.logger.log(Level.SEVERE, GlowServer.lang.getString("error.entity.profile.invalid", id));
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

    public CompoundTag toNBT() {
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
