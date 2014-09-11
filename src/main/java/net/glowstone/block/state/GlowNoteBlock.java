package net.glowstone.block.state;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TENote;
import net.glowstone.entity.GlowPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;

public class GlowNoteBlock extends GlowBlockState implements NoteBlock {

    private Note note;

    public GlowNoteBlock(GlowBlock block) {
        super(block);
        if (block.getType() != Material.NOTE_BLOCK) {
            throw new IllegalArgumentException("GlowNoteBlock: expected NOTE_BLOCK, got " + block.getType());
        }

        note = getTileEntity().getNote();
    }

    private TENote getTileEntity() {
        return (TENote) getBlock().getTileEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (!result) {
            getTileEntity().setNote(note);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implementation

    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public byte getRawNote() {
        return note.getId();
    }

    @Override
    public void setNote(Note note) {
        Validate.notNull(note);
        this.note = note;
    }

    @Override
    public void setRawNote(byte note) {
        this.note = new Note(note);
    }

    @Override
    public boolean play() {
        return play(instrumentOf(getBlock().getRelative(BlockFace.DOWN).getType()), getNote());
    }

    @Override
    public boolean play(byte instrument, byte note) {
        if (getBlock().getType() != Material.NOTE_BLOCK) {
            return false;
        }

        Location location = getBlock().getLocation();

        GlowChunk.Key key = new GlowChunk.Key(getX() >> 4, getZ() >> 4);
        for (GlowPlayer player : getWorld().getRawPlayers()) {
            if (player.canSee(key)) {
                player.playNote(location, instrument, note);
            }
        }

        return true;
    }

    @Override
    public boolean play(Instrument instrument, Note note) {
        return play(instrument.getType(), note.getId());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    private static Instrument instrumentOf(Material mat) {
        // todo: check more blocks.
        switch (mat) {
            case WOOD:
            case NOTE_BLOCK:
            case WORKBENCH:
            case LOG:
                return Instrument.BASS_GUITAR;
            case SAND:
            case GRAVEL:
            case SOUL_SAND:
                return Instrument.SNARE_DRUM;
            case GLASS:
                return Instrument.STICKS;
            case STONE:
            case OBSIDIAN:
            case NETHERRACK:
            case BRICK:
                return Instrument.BASS_DRUM;
            case DIRT:
            case AIR:
            default:
                return Instrument.PIANO;
        }
    }

}
