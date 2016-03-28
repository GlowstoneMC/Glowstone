package net.glowstone.generator.structures.template;

import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.util.Vector;

public class TemplateTileEntity extends TemplateBlock {

    private CompoundTag compound;

    public TemplateTileEntity(Vector position, int raw, CompoundTag compound) {
        super(position, raw);
        this.compound = compound;
    }

    public CompoundTag getNBT() {
        return compound;
    }
}
