package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Jukebox;
import org.jetbrains.annotations.NotNull;

public class GlowJukeBoxBlockData extends AbstractBlockData implements Jukebox {

    public GlowJukeBoxBlockData(Material material) {
        super(material);
    }

    public BooleanStateValue getRecordStateValue(){
        return (BooleanStateValue) this.getStateValue("has_record");
    }

    @Override
    public boolean hasRecord() {
        return this.getRecordStateValue().getValue();
    }

    @Override
    public @NotNull BlockData clone() {
        GlowJukeBoxBlockData juke = new GlowJukeBoxBlockData(this.getMaterial());
        juke.getRecordStateValue().setValue(this.hasRecord());
        return juke;
    }
}
