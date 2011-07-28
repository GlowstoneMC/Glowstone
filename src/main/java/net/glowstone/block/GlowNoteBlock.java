package net.glowstone.block;

import java.util.Map;

import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;

import net.glowstone.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;

/**
 * Represents a noteblock in the world.
 */
public class GlowNoteBlock extends GlowBlockState implements NoteBlock {
    
    private Note note = new Note((byte) 0);

    public GlowNoteBlock(GlowBlock block) {
        super(block);
        if (block.getType() != Material.NOTE_BLOCK) {
            throw new IllegalArgumentException("GlowNoteBlock: expected NOTE_BLOCK, got " + block.getType());
        }
    }
    
    public Note getNote() {
        return note;
    }
    
    public byte getRawNote() {
        return note.getId();
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public void setRawNote(byte note) {
        this.note = new Note(note);
    }

    public boolean play() {
        return play(instrumentOf(getBlock().getRelative(BlockFace.DOWN).getType()), note);
    }

    public boolean play(byte instrument, byte note) {
        if (getBlock().getType() != Material.NOTE_BLOCK) {
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
    
    public static Instrument instrumentOf(Material material) {
        // TODO: check more blocks.
        switch (material) {
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
    
    // Internal mechanisms
    
    @Override
    public void destroy() {
        setRawNote((byte) 0);
    }
    
    @Override
    public void load(CompoundTag compound) {
        super.load(compound, "Music");
        setRawNote(((ByteTag) compound.getValue().get("note")).getValue());
    }
    
    @Override
    public CompoundTag save() {
        Map<String, Tag> map = super.save("Music");
        map.put("note", new ByteTag("note", getRawNote()));
        return new CompoundTag("", map);
    }
    
}
