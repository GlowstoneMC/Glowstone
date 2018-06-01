package net.glowstone.net;

import com.destroystokyo.paper.profile.ProfileProperty;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.util.UuidUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Container for proxy (e.g. BungeeCord) player data spoofing.
 */
@AllArgsConstructor
public final class ProxyData {

    /**
     * The Lilypad security key sent by the proxy, or null if not present.
     */
    @Getter
    private String securityKey;

    /** The spoofed hostname to use instead of the actual one. */
    @Getter
    private String hostname;

    /**
     * The spoofed address to use instead of the actual one.
     *
     * @return The spoofed address.
     */
    @Getter
    private InetSocketAddress address;

    /** The player name for the spoofed profile, or null if not specified. */
    private String name;

    /** The player UUID for the spoofed profile. */
    private UUID uuid;

    /** The player properties for the spoofed profile. */
    private List<ProfileProperty> properties;

    /**
     * Create a proxy data structure for a session from the given source text.
     *
     * @param session The session to create the data for.
     * @param sourceText Contents of the hostname field of the handshake.
     * @throws Exception if an error occurs parsing the source text.
     */
    public ProxyData(GlowSession session, String sourceText) throws Exception {
        // Attempt to parse the sourceText as JSON (LilyPad) first
        try {
            // This throws a ParseException if parsing failed (ie: not LilyPad)
            JSONObject payload = (JSONObject) new JSONParser().parse(sourceText);

            // LilyPad-only values
            securityKey = (String) payload.get("s"); // Not used by us anywhere
            name = (String) payload.get("n");

            // Spoof hostname, address, and UUID
            // LilyPad also spoofs the port, unlike Bungee
            hostname = (String) payload.get("h");
            uuid = UuidUtils.fromFlatString((String) payload.get("u"));
            address = new InetSocketAddress(
                    (String) payload.get("rIp"), ((Long) payload.get("rP")).intValue());

            // Extract properties, if available
            if (payload.containsKey("p")) {
                JSONArray props = (JSONArray) payload.get("p");

                properties = new ArrayList<>(props.size());
                for (Object obj : props) {
                    JSONObject prop = (JSONObject) obj;
                    String propName = (String) prop.get("n");
                    String value = (String) prop.get("v");
                    String signature = (String) prop.get("s");
                    properties.add(new ProfileProperty(propName, value, signature));
                }
            } else {
                properties = new ArrayList<>(0);
            }

            return; // We've processed the data, don't re-parse it as Bungee data
        } catch (ParseException ignored) {
            // Swallow JSON parse exception and process sourceText as Bungee data
        }

        // Likely Bungee data at this point. If not, then a friendly exception will be thrown.

        String[] parts = sourceText.split("\0");
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException(
                    "parts length was " + parts.length + ", should be 3 or 4");
        }

        // Set values that aren't supported or present to null
        name = null;
        securityKey = null;

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
                properties.add(new ProfileProperty(propName, value, signature));
            }
        } else {
            properties = new ArrayList<>(0);
        }
    }

    /**
     * Get a spoofed profile to use with the given name.
     *
     * @param name The player name.
     * @return The spoofed profile.
     */
    public GlowPlayerProfile getProfile(String name) {
        return new GlowPlayerProfile(name, uuid, properties, true);
    }

    /**
     * Get a spoofed profile to use. Returns null if the proxy did not send a username as part of
     * the payload.
     *
     * @return The spoofed profile.
     */
    public GlowPlayerProfile getProfile() {
        if (name == null) {
            return null;
        }
        return new GlowPlayerProfile(name, uuid, properties, true);
    }
}
