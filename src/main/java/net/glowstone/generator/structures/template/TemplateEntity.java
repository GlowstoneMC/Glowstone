package net.glowstone.generator.structures.template;

import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class TemplateEntity {

    private Vector pos;
    private Vector blockPos;
    private CompoundTag nbt;

    public TemplateEntity(Vector pos, Vector blockPos, CompoundTag nbt) {
        this.pos = pos;
        this.blockPos = blockPos;
        this.nbt = nbt;
    }

    public Vector getPos() {
        return pos;
    }

    public Vector getBlockPos() {
        return blockPos;
    }

    public CompoundTag getNBT() {
        return nbt;
    }

    public EntityType getType() {
        return EntityType.fromName(nbt.getString("id"));
    }
}
