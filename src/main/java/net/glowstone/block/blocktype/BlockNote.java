package net.glowstone.block.blocktype;

import static org.bukkit.Instrument.BANJO;
import static org.bukkit.Instrument.BELL;
import static org.bukkit.Instrument.BIT;
import static org.bukkit.Instrument.CHIME;
import static org.bukkit.Instrument.COW_BELL;
import static org.bukkit.Instrument.DIDGERIDOO;
import static org.bukkit.Instrument.FLUTE;
import static org.bukkit.Instrument.GUITAR;
import static org.bukkit.Instrument.IRON_XYLOPHONE;
import static org.bukkit.Instrument.PIANO;
import static org.bukkit.Instrument.PLING;
import static org.bukkit.Instrument.SNARE_DRUM;
import static org.bukkit.Instrument.XYLOPHONE;

import com.destroystokyo.paper.MaterialTags;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Tag;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockNote extends BlockType {

    public static final int OCTAVES_COUNT = 2;
    public static final int NOTES_COUNT = OCTAVES_COUNT * Note.Tone.TONES_COUNT;

    private static Instrument instrumentOf(Material type) {
        switch (type) {
            case GOLD_BLOCK:
                return BELL;
            case CLAY:
                return FLUTE;
            case PACKED_ICE:
                return CHIME;
            case BONE_BLOCK:
                return XYLOPHONE;
            case IRON_BLOCK:
                return IRON_XYLOPHONE;
            case SOUL_SAND:
                return COW_BELL;
            case PUMPKIN:
                return DIDGERIDOO;
            case EMERALD_BLOCK:
                return BIT;
            case HAY_BLOCK:
                return BANJO;
            case GLOWSTONE:
                return PLING;
            default:
                // TODO: use SAND_LIKES tag with HashObservableSets
                if (Tag.SAND.isTagged(type) || MaterialTags.CONCRETE_POWDER.isTagged(type) ||
                    type == Material.GRAVEL) {
                    return SNARE_DRUM;
                }
                if (Tag.WOOL.isTagged(type)) {
                    return GUITAR;
                }
                // TODO: wait for stone, glass, and wood tags
                return PIANO;
        }
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        NoteBlock noteBlockData = getNoteBlockData(block);
        Note note = noteBlockData.getNote();
        noteBlockData.setNote(note.getId() == NOTES_COUNT ? new Note(0) : note.sharped());
        block.setBlockData(noteBlockData);
        return false;
    }

    public NoteBlock getNoteBlockData(GlowBlock block) {
        return (NoteBlock) block.getBlockData();
    }

    public boolean playBlock(GlowBlock block) {
        NoteBlock noteBlockData = getNoteBlockData(block);
        return play(noteBlockData.getInstrument(), noteBlockData.getNote(), block);
    }

    public void updateInstrument(GlowBlock block) {
        NoteBlock noteBlockData = getNoteBlockData(block);
        noteBlockData.setInstrument(instrumentOf(block.getRelative(BlockFace.DOWN).getType()));
    }

    @Override
    public void leftClickBlock(GlowPlayer player, GlowBlock block, ItemStack holding) {
        playBlock(block);
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
                           GlowBlockState oldState) {
        super.afterPlace(player, block, holding, oldState);
        updateInstrument(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
                                   Material oldType,
                                   byte oldData, Material newType, byte newData) {
        updateInstrument(block);
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock block) {
        super.updatePhysicsAfterEvent(block);
        if (block.isBlockIndirectlyPowered()) {
            playBlock(block);
        }
    }

    public boolean play(Instrument instrument, Note note, GlowBlock block) {
        if (block.getType() != Material.NOTE_BLOCK) {
            return false;
        }

        NotePlayEvent event =
            EventFactory.getInstance().callEvent(new NotePlayEvent(block, instrument, note));
        if (event.isCancelled()) {
            return false;
        }

        Location location = block.getLocation();

        GlowChunk.Key key = GlowChunk.Key.of(block.getX() >> 4, block.getZ() >> 4);
        block.getWorld().getRawPlayers().parallelStream().filter(player -> player.canSeeChunk(key))
            .forEach(player -> player.playNote(location, instrument, note));

        return true;
    }
}
