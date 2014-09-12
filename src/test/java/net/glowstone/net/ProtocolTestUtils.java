package net.glowstone.net;

import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.util.TextMessage;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProtocolTestUtils {

    public static JSONObject getJson() {
        JSONObject obj = new JSONObject();
        obj.put("key", "value");
        return obj;
    }

    public static TextMessage getTextMessage() {
        return new TextMessage("text");
    }

    public static List<MetadataMap.Entry> getMetadataEntry() {
        List<MetadataMap.Entry> arraylist = new ArrayList<>();
        arraylist.add(new MetadataMap.Entry(MetadataIndex.AGE, 1));
        return arraylist;
    }
}
