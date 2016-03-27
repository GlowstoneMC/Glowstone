package net.glowstone.generator.structures.template;

import net.glowstone.GlowServer;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

            JSONArray blocksArray = (JSONArray) json.get("List_Compound_blocks");
            for (Object o : blocksArray) {
                JSONObject blockEntry = (JSONObject) o;
                JSONArray posArray = (JSONArray) blockEntry.get("List_Int_pos");
                Vector pos = new Vector((long) posArray.get(0), (long) posArray.get(1), (long) posArray.get(2));
                long state = (long) blockEntry.get("Int_state");
                TemplateBlock block;

                if (blockEntry.get("Compound_nbt") != null) {
                    block = new TemplateTileEntity(pos, (int) state, (JSONObject) blockEntry.get("Compound_nbt"));
                } else {
                    block = new TemplateBlock(pos, (int) state);
                }

                blocks.add(block);
            }

            Template template = new Template(name, (int) version, size, blocks);
            templates.add(template);
        } catch (Exception e) {
            GlowServer.logger.severe("Could not load structure template '" + name + "'.");
            e.printStackTrace();
        }
    }

    public void loadTemplates() {
        templates.clear();
        for (String templateIndex : TEMPLATE_INDEXES) {
            loadTemplate(templateIndex);
        }
    }

}
