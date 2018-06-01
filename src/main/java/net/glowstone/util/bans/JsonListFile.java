package net.glowstone.util.bans;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Tools for storing lists of entries in JSON files.
 */
public abstract class JsonListFile {

    /**
     * The list as we currently know it.
     */
    protected final List<BaseEntry> entries = new ArrayList<>();

    /**
     * The file the list is associated with.
     */
    private final File file;

    /**
     * Initialize the list from the given file.
     *
     * @param file The file to use for this list.
     */
    public JsonListFile(File file) {
        this.file = file;
    }

    /**
     * Reloads from the file.
     */
    public void load() {
        entries.clear();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(reader);

                for (Object object : jsonArray) {
                    JSONObject jsonObj = (JSONObject) object;
                    Map<String, String> map = new HashMap<>(jsonObj.size());
                    for (Object jsonEntry : jsonObj.entrySet()) {
                        Entry<?, ?> entry = (Map.Entry<?, ?>) jsonEntry;
                        map.put(entry.getKey().toString(), entry.getValue().toString());
                    }

                    entries.add(readEntry(map));
                }
            } catch (Exception ex) {
                GlowServer.logger.log(Level.SEVERE, "Error reading from: " + file, ex);
            }
        } else {
            //importLegacy();
            save();
        }
    }

    /**
     * Saves to the file.
     */
    @SuppressWarnings("unchecked")
    protected void save() {
        JSONArray array = new JSONArray();
        for (BaseEntry entry : entries) {
            JSONObject obj = new JSONObject();
            for (Entry<String, String> mapEntry : entry.write().entrySet()) {
                obj.put(mapEntry.getKey(), mapEntry.getValue());
            }
            array.add(obj);
        }

        try (Writer writer = new FileWriter(file)) {
            array.writeJSONString(writer);
        } catch (Exception ex) {
            GlowServer.logger.log(Level.SEVERE, "Error writing to: " + file, ex);
        }
    }

    /**
     * Deserialize an entry from JSON format.
     *
     * @param object The JSON object to read from.
     * @return The finished Entry.
     */
    protected abstract BaseEntry readEntry(Map<String, String> object);

    /*
     * Import data from a legacy format if possible.
     */
    //protected abstract void importLegacy();

    /**
     * Base interface for entries in JSON list files.
     */
    protected interface BaseEntry {

        /**
         * Serialize this entry to JSON format.
         *
         * @return The resulting JSON object.
         */
        Map<String, String> write();
    }

}
