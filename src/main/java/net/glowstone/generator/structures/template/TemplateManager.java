package net.glowstone.generator.structures.template;

import net.glowstone.GlowServer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class TemplateManager {

    private static final String[] TEMPLATE_INDEXES = new String[]{"igloo/igloo_bottom", "igloo/igloo_middle", "igloo/igloo_top"};
    private static final String TEMPLATES_LCOATION = "assets/structures/";

    private final ArrayList<Template> templates = new ArrayList<>();

    public void initialize() {
        loadTemplates();
        GlowServer.logger.info("Loaded " + templates.size() + " structure templates.");
    }

    public Template getTemplate(String name) {
        for (Template template : templates) {
            if (template.getName().equals(name))
                return template;
        }
        return null;
    }

    public void loadTemplate(String name) {
        if (getTemplate(name) != null) {
            GlowServer.logger.severe("Cannot load template '" + name + "' because it is already registered.");
            return;
        }

        InputStream stream = getClass().getClassLoader().getResourceAsStream(TEMPLATES_LCOATION + name + ".json");

        if (stream == null) {
            GlowServer.logger.severe("Could not find structure template from name '" + name + "'.");
            return;
        }

        try {
            InputStreamReader templateStream = new InputStreamReader(stream);
            JSONParser jsonParser = new JSONParser();
            JSONObject json;
            json = (JSONObject) jsonParser.parse(templateStream);

            long version = (long) json.get("Int_version");
            JSONArray sizeArray = (JSONArray) json.get("List_Int_size");
            Vector size = new Vector((long) sizeArray.get(0), (long) sizeArray.get(1), (long) sizeArray.get(2));
            ArrayList<TemplateBlock> blocks = new ArrayList<>();
            ArrayList<TemplateEntity> entities = new ArrayList<>();

            JSONArray blocksArray = (JSONArray) json.get("List_Compound_blocks");
            for (Object o : blocksArray) {
                JSONObject blockEntry = (JSONObject) o;
                JSONArray posArray = (JSONArray) blockEntry.get("List_Int_pos");
                Vector pos = new Vector((long) posArray.get(0), (long) posArray.get(1), (long) posArray.get(2));
                long state = (long) blockEntry.get("Int_state");
                TemplateBlock block;

                if (blockEntry.get("Compound_nbt") != null) {
                    block = new TemplateTileEntity(pos, (int) state, jsonToNBT((JSONObject) blockEntry.get("Compound_nbt")));
                } else {
                    block = new TemplateBlock(pos, (int) state);
                }

                blocks.add(block);
            }

            JSONArray entitiesArray = (JSONArray) json.get("List_Compound_entities");
            if (entitiesArray != null) {
                for (Object o : entitiesArray) {
                    JSONObject entityEntry = (JSONObject) o;
                    JSONArray posArray = (JSONArray) entityEntry.get("List_Double_pos");
                    Vector pos = new Vector((double) posArray.get(0), (double) posArray.get(1), (double) posArray.get(2));
                    JSONArray blockPosArray = (JSONArray) entityEntry.get("List_Int_blockPos");
                    Vector blockPos = new Vector((long) blockPosArray.get(0), (long) blockPosArray.get(1), (long) blockPosArray.get(2));
                    CompoundTag compound = jsonToNBT((JSONObject) entityEntry.get("Compound_nbt"));
                    TemplateEntity entity = new TemplateEntity(pos, blockPos, compound);
                    entities.add(entity);
                }
            }

            Template template = new Template(name, (int) version, size, blocks, entities);
            templates.add(template);
        } catch (Exception e) {
            GlowServer.logger.severe("Could not load structure template '" + name + "'.");
            e.printStackTrace();
        }
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
                    e.printStackTrace();
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
            } else if (val instanceof Double) {
                Double doubleVal = (Double) val;

                if (float.class == tagType.getValueClass()) {
                    val = doubleVal.floatValue();
                }
            }

            compound.putValue(nbtKey, tagType, val);
        }
        return compound;
    }

    public void loadTemplates() {
        templates.clear();
        for (String templateIndex : TEMPLATE_INDEXES) {
            loadTemplate(templateIndex);
        }
    }

}
