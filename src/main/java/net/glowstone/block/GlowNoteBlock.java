package net.glowstone.block;

import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;

import net.glowstone.GlowChunk;
import net.glowstone.entity.GlowPlayer;

/**
 * Represents a noteblock in the world.
 */
public class GlowNoteBlock extends GlowBlockState implements NoteBlock {
    
    private NoteWrapper wrapper = new NoteWrapper(new Note((byte) 0));

    public GlowNoteBlock(GlowBlock block) {
        super(block);
        if (block.getTypeId() != BlockID.NOTE_BLOCK) {
            throw new IllegalArgumentException("GlowNoteBlock: expected NOTE_BLOCK, got " + block.getType());
        }
    }
    
    public Note getNote() {
        return wrapper.note;
    }
    
    public byte getRawNote() {
        return wrapper.note.getId();
    }

    public void setNote(Note note) {
        this.wrapper.note = note;
    }

    public void setRawNote(byte note) {
        this.wrapper.note = new Note(note);
    }

    public boolean play() {
        return play(instrumentOf(getBlock().getRelative(BlockFace.DOWN).getTypeId()), wrapper.note);
    }

    public boolean play(byte instrument, byte note) {
        if (getBlock().getTypeId() != BlockID.NOTE_BLOCK) {
            return false;
        }
        
        Location location = getBlock().getLocation();
        
        for (GlowPlayer player : getWorld().getRawPlayers()) {
            if (player.canSee(new GlowChunk.Key(getX() >> 4, getZ() >> 4)))
                player.playNote(location, instrument, note);
        }
        
        return true;
    }

    public boolean play(Instrument instrument, Note note) {
        return play(instrument.getType(), note.getId());
    }
    
    public static Instrument instrumentOf(int id) {
        // TODO: check more blocks.
        switch (id) {
            case BlockID.WOOD:
            case BlockID.NOTE_BLOCK:
            case BlockID.WORKBENCH:
            case BlockID.LOG:
                return Instrument.BASS_GUITAR;
            case BlockID.SAND:
            case BlockID.GRAVEL:
            case BlockID.SOUL_SAND:
                return Instrument.SNARE_DRUM;
            case BlockID.GLASS:
                return Instrument.STICKS;
            case BlockID.STONE:
            case BlockID.OBSIDIAN:
            case BlockID.NETHERRACK:
            case BlockID.BRICK:
                return Instrument.BASS_DRUM;
            case BlockID.DIRT:
            case BlockID.AIR:
            default:
                return Instrument.PIANO;
        }
    }
    
    // Internal mechanisms
    
    private class NoteWrapper {
        public Note note;
        public NoteWrapper(Note note) {
            this.note = note;
        }
    }
    
    @Override
    public GlowNoteBlock shallowClone() {
        GlowNoteBlock result = new GlowNoteBlock(getBlock());
        result.wrapper = wrapper;
        return result;
    }
    
    @Override
    public void destroy() {
        setRawNote((byte) 0);
    }
    
}
