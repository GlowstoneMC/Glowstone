package net.glowstone.generator.structures.template;

import net.glowstone.GlowServer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TemplateTileEntity extends TemplateBlock {

    private JSONObject jsonNBT;

    public TemplateTileEntity(Vector position, int raw, JSONObject jsonNBT) {
        super(position, raw);
        this.jsonNBT = jsonNBT;
    }

    private CompoundTag jsonToNBT(JSONObject object) {
        CompoundTag compound = new CompoundTag();
        for (Object o : object.keySet()) {
            String key = (String) o;
            String nbtKey = null;
            Object val = object.get(key);
            TagType tagType = null;

            for (TagType t : TagType.values()) {
                if (key.startsWith(t.getName().replace("_", ""))) {
                    nbtKey = key.replace(t.getName().replace("_", "") + "_", "");
                    tagType = t;
                    break;
                }
            }

            if (tagType == TagType.LIST) {
                JSONArray jarray = (JSONArray) object.get(key);
                TagType arrayType = null;

                for (TagType t : TagType.values()) {
                    if (nbtKey.startsWith(t.getName().replace("_", ""))) {
                        nbtKey = nbtKey.replace(t.getName().replace("_", "") + "_", "");
                        arrayType = t;
                    }
                }

                if (arrayType == null) {
                    GlowServer.logger.severe("Cannot find array type for key '" + nbtKey + "'.");
                    continue;
                }

                if (arrayType == TagType.COMPOUND) {
                    List<CompoundTag> compoundTags = new ArrayList<>();
                    for (Object ao : jarray) {
                        compoundTags.add(jsonToNBT((JSONObject) ao));
                    }
                    compound.putCompoundList(nbtKey, compoundTags);
                    continue;
                }
                if (arrayType == TagType.END) {
                    continue;
                }
                try {
                    compound.putList(nbtKey, arrayType, jarray);
                } catch (Exception e) {
                    GlowServer.logger.severe("Cannot save array '" + nbtKey + "' (" + arrayType + ").");
                    continue;
                }
                continue;
            }

            if (tagType == TagType.COMPOUND) {
                compound.putCompound(nbtKey, jsonToNBT((JSONObject) val));
                continue;
            }

            if (tagType == TagType.END) {
                continue;
            }

            if (val instanceof Long) {
                Long longVal = (Long) val;

                if (byte.class == tagType.getValueClass()) {
                    val = longVal.byteValue();
                }
                if (int.class == tagType.getValueClass()) {
                    val = longVal.intValue();
                }
                if (short.class == tagType.getValueClass()) {
                    val = longVal.shortValue();
                }
            }

            compound.putValue(nbtKey, tagType, val);
        }
        return compound;
    }

    public CompoundTag getNBT() {
        return jsonToNBT(jsonNBT);
    }

    public JSONObject getJsonNBT() {
        return jsonNBT;
    }
}
