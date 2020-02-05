package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.StructureBlock;
import org.jetbrains.annotations.NotNull;

public class GlowStructureBlockData extends AbstractBlockData implements StructureBlock {
    public GlowStructureBlockData(Material material) {
        super(material);
    }

    public EnumStateValue<StructureBlock.Mode> getModeStateValue(){
        return (EnumStateValue<Mode>) this.<Mode>getStateValue("mode").get();
    }

    @Override
    public @NotNull Mode getMode() {
        return this.getModeStateValue().getValue();
    }

    @Override
    public void setMode(@NotNull Mode mode) {
        this.getModeStateValue().setValue(mode);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowStructureBlockData structure = new GlowStructureBlockData(this.getMaterial());
        structure.setMode(this.getMode());
        return structure;
    }
}
