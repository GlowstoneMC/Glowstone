package net.glowstone.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.util.TextMessage;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.FloatTag;
import net.glowstone.util.nbt.TagType;
import org.json.simple.JSONObject;

/**
 * Utilities for helping with the protocol test.
 */
public final class ProtocolTestUtils {

    private ProtocolTestUtils() {
    }

    @SuppressWarnings("unchecked")
    public static JSONObject getJson() {
        JSONObject obj = new JSONObject();
        obj.put("key", "value");
        return obj;
    }

    public static TextMessage getTextMessage() {
        return new TextMessage("text");
    }

    public static List<MetadataMap.Entry> getMetadataEntry() {
        List<MetadataMap.Entry> list = new ArrayList<>();
        list.add(new MetadataMap.Entry(MetadataIndex.HEALTH, 1f));
        return list;
    }

    public static CompoundTag getTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("int", 5);
        tag.putString("string", "text");
        tag.putList("list", TagType.FLOAT, Arrays.asList(1.f, 2.f, 3.f), FloatTag::new);
        tag.putCompound("compound", new CompoundTag());
        return tag;
    }
}
