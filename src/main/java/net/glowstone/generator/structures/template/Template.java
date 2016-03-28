package net.glowstone.generator.structures.template;

import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Template {

    private String name;
    private int version;
    private Vector size;
    private ArrayList<TemplateBlock> blocks;
    private ArrayList<TemplateEntity> entities;

    public Template(String name, int version, Vector size, ArrayList<TemplateBlock> blocks, ArrayList<TemplateEntity> entities) {
        this.name = name;
        this.version = version;
        this.size = size;
        this.blocks = blocks;
        this.entities = entities;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public Vector getSize() {
        return size;
    }

    public ArrayList<TemplateBlock> getBlocks() {
        return blocks;
    }

    public ArrayList<TemplateEntity> getEntities() {
        return entities;
    }
}
