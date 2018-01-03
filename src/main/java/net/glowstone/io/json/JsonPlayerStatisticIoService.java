package net.glowstone.io.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.io.PlayerStatisticIoService;
import net.glowstone.util.StatisticMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonPlayerStatisticIoService implements PlayerStatisticIoService {

    private GlowServer server;
    private File statsDir;

    public JsonPlayerStatisticIoService(GlowServer server, File statsDir) {
        this.server = server;
        this.statsDir = statsDir;
    }

    /**
     * Gets the statistics file for the given UUID.
     *
     * @param uuid the UUID of the player
     * @return the statistics file of the given UUID
     */
    private File getPlayerFile(UUID uuid) {
        if (!statsDir.isDirectory() && !statsDir.mkdirs()) {
            server.getLogger().warning("Failed to create directory: " + statsDir);
        }
        return new File(statsDir, uuid + ".json");
    }

    /**
     * Reads the stats of a player from its statistics file and writes the values to the
     * StatisticMap.
     *
     * @param player the player to read the statistics from
     */
    @Override
    public void readStatistics(GlowPlayer player) {
        File statsFile = getPlayerFile(player.getUniqueId());
        player.getStatisticMap().getValues().clear();
        if (statsFile.exists()) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(new FileReader(statsFile));
                for (Object obj : json.entrySet()) {
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) obj;
                    Long longValue = null;
                    if (entry.getValue() instanceof Long) {
                        longValue = (Long) entry.getValue();
                    } else if (entry.getValue() instanceof JSONObject) {
                        JSONObject object = (JSONObject) entry.getValue();
                        if (object.containsKey("value")) {
                            longValue = (Long) object.get("value");
                        }
                    } else {
                        GlowServer.logger.warning(String.format(
                                "Unknown statistic type for '%s': %s (%s)",
                                entry.getKey(), entry.getValue(),
                                entry.getValue().getClass().getSimpleName()));
                    }
                    if (longValue != null) {
                        player.getStatisticMap().getValues()
                                .put(entry.getKey(), longValue.intValue());
                    }
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the statistics of the player into its statistics file.
     *
     * @param player the player to write the statistics file from
     */
    @Override
    public void writeStatistics(GlowPlayer player) {
        File file = getPlayerFile(player.getUniqueId());
        StatisticMap map = player.getStatisticMap();
        JSONObject json = new JSONObject(map.getValues());
        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(json.toJSONString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
