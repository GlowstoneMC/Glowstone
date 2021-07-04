package net.glowstone.entity.meta.profile;

import net.glowstone.GlowServer;
import net.glowstone.util.UuidUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Methods for accessing Mojang servers to look up player profiles and UUIDs.
 */
class PlayerDataFetcher {

    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String PROFILE_URL_SUFFIX = "?unsigned=false";

    private static final String UUID_URL = "https://api.mojang.com/profiles/minecraft";

    /**
     * Look up the GlowPlayerProfile for a given UUID.
     *
     * @param uuid The UUID to look up.
     * @return The resulting GlowPlayerProfile, contains a null name on failure.
     */
    public static GlowPlayerProfile getProfile(UUID uuid) {
        InputStream is;
        try {
            URL url = new URL(PROFILE_URL + UuidUtils.toFlatString(uuid) + PROFILE_URL_SUFFIX);
            URLConnection conn = url.openConnection();
            //potentially blocking
            is = conn.getInputStream();
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Failed to look up profile");
            return new GlowPlayerProfile(null, uuid, true);
        }

        JSONObject json;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            if (br.ready()) {
                json = (JSONObject) new JSONParser().parse(br);
            } else {
                return new GlowPlayerProfile(null, uuid, true);
            }
        } catch (ParseException e) {
            GlowServer.logger.log(Level.WARNING, "Failed to parse profile response", e);
            return new GlowPlayerProfile(null, uuid, true);
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Failed to look up profile", e);
            return new GlowPlayerProfile(null, uuid, true);
        }
        return GlowPlayerProfile.fromJson(json);
    }

    /**
     * Look up the UUID for a given username.
     *
     * @param playerName The name to look up.
     * @return The UUID, or null on failure.
     */
    public static UUID getUuid(String playerName) {
        HttpsURLConnection conn;
        try {
            URL url = new URL(UUID_URL);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Failed to look up UUID", e);
            return null;
        }

        List<String> playerList = new ArrayList<>();
        playerList.add(playerName);

        JSONArray json;

        try {
            try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeBytes(JSONValue.toJSONString(playerList));
            }

            json = (JSONArray) JSONValue.parse(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            GlowServer.logger.warning("Couldn't get UUID due to IO error: " + e);
            return null;
        }

        if (!json.isEmpty()) {
            String uuid = (String) ((JSONObject) json.get(0)).get("id");
            return UuidUtils.fromFlatString(uuid);
        }
        return null;
    }

}
