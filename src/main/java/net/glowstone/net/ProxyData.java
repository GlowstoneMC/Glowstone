package net.glowstone.net;

import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.meta.profile.PlayerProperty;
import net.glowstone.util.UuidUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Container for proxy (e.g. BungeeCord) player data spoofing.
 */
public final class ProxyData {

    private final String hostname;
    private final InetSocketAddress address;
    private final UUID uuid;
    private final List<PlayerProperty> properties;

    /**
     * Create a proxy data structure for a session from the given source text.
     * @param session The session to create the data for.
     * @param sourceText Contents of the hostname field of the handshake.
     * @throws Exception if an error occurs parsing the source text.
     */
    public ProxyData(GlowSession session, String sourceText) throws Exception {
        String[] parts = sourceText.split("\0");
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("parts length was " + parts.length + ", should be 3 or 4");
        }

        // Spoof hostname, address, and UUID
        hostname = parts[0];
        address = new InetSocketAddress(parts[1], session.getAddress().getPort());
        uuid = UuidUtils.fromFlatString(parts[2]);

        if (parts.length == 4) {
            // Spoof properties
            JSONArray jsonProperties = (JSONArray) new JSONParser().parse(parts[3]);

            properties = new ArrayList<>(jsonProperties.size());
            for (Object obj : jsonProperties) {
                JSONObject propJson = (JSONObject) obj;
                String propName = (String) propJson.get("name");
                String value = (String) propJson.get("value");
                String signature = (String) propJson.get("signature");
                properties.add(new PlayerProperty(propName, value, signature));
            }
        } else {
            properties = new ArrayList<>(0);
        }
    }

    /**
     * Get the spoofed hostname to use instead of the actual one.
     * @return The spoofed hostname.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Get the spoofed address to use instead of the actual one.
     * @return The spoofed address.
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Get a spoofed profile to use with the given name.
     * @param name The player name.
     * @return The spoofed profile.
     */
    public PlayerProfile getProfile(String name) {
        return new PlayerProfile(name, uuid, properties);
    }
}
