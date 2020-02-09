package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;

public class GlowNoteBlockData extends AbstractBlockData implements NoteBlock, GlowPowered {

    public GlowNoteBlockData(Material material) {
        super(material, StateGenerator.INSTRUMENT, StateGenerator.NOTE, StateGenerator.POWERED);
    }

    public EnumStateValue<Instrument> getInstrumentStateValue(){
        return (EnumStateValue<Instrument>) this.<Instrument>getStateValue("instrument").get();
    }

    public IntegerStateValue.Ranged getNoteStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("note").get();
    }

    @Override
    public @NotNull Instrument getInstrument() {
        return this.getInstrumentStateValue().getValue();
    }

    @Override
    public void setInstrument(@NotNull Instrument instrument) {
        this.getInstrumentStateValue().setValue(instrument);
    }

    @Override
    public @NotNull Note getNote() {
        return new Note(this.getNoteStateValue().getValue());
    }

    @Override
    public void setNote(@NotNull Note note) {
        getNoteStateValue().setValue((int)note.getId());
    }

    @Override
    public @NotNull BlockData clone() {
        GlowNoteBlockData note = new GlowNoteBlockData(this.getMaterial());
        note.setInstrument(this.getInstrument());
        note.setNote(this.getNote());
        note.setPowered(this.isPowered());
        return note;
    }
}
