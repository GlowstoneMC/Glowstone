package net.glowstone.block.entity.state;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.NoteblockEntity;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.chunk.GlowChunk.Key;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;
import org.bukkit.event.block.NotePlayEvent;

public class GlowNoteBlock extends GlowBlockState implements NoteBlock {

    @Getter
    private Note note;

    /**
     * Creates the instance for a note block.
     *
     * @param block the note block
     */
    public GlowNoteBlock(GlowBlock block) {
        super(block);
        if (block.getType() != Material.NOTE_BLOCK) {
            throw new IllegalArgumentException(
                "GlowNoteBlock: expected NOTE_BLOCK, got " + block.getType());
        }

        note = getBlockEntity().getNote();
    }

    private static Instrument instrumentOf(Material mat) {
        switch (mat) {
            case WOOD:
            case ACACIA_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case DARK_OAK_STAIRS:
            case WOOD_STAIRS:
            case BOOKSHELF:
            case CHEST:
            case FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
            case TRAP_DOOR:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case DAYLIGHT_DETECTOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case TRAPPED_CHEST:
            case NOTE_BLOCK:
            case WORKBENCH:
            case LOG:
                return Instrument.BASS_GUITAR;
            case SAND:
            case GRAVEL:
            case GLOWSTONE:
            case SOUL_SAND:
                return Instrument.SNARE_DRUM;
            case GLASS:
            case BEACON:
            case SEA_LANTERN:
                return Instrument.STICKS;
            case STONE:
            case DIAMOND_ORE:
            case COBBLESTONE:
            case DROPPER:
            case REDSTONE_ORE:
            case STONE_SLAB2:
            case COAL_ORE:
            case LAPIS_ORE:
            case IRON_ORE:
            case DISPENSER:
            case GOLD_ORE:
            case BEDROCK:
            case COBBLESTONE_STAIRS:
            case MOSSY_COBBLESTONE:
            case SMOOTH_BRICK:
            case COBBLE_WALL:
            case QUARTZ_ORE:
            case QUARTZ_BLOCK:
            case QUARTZ_STAIRS:
            case SMOOTH_STAIRS:
            case QUARTZ:
            case BRICK_STAIRS:
            case SANDSTONE:
            case SANDSTONE_STAIRS:
            case EMERALD_ORE:
            case NETHER_BRICK:
            case NETHER_BRICK_STAIRS:
            case ENDER_STONE:
            case RED_SANDSTONE:
            case RED_SANDSTONE_STAIRS:
            case OBSIDIAN:
            case ENDER_PORTAL_FRAME:
            case FURNACE:
            case ENDER_CHEST:
            case END_BRICKS:
            case PURPUR_BLOCK:
            case PRISMARINE:
            case PURPUR_PILLAR:
            case PURPUR_STAIRS:
            case PURPUR_SLAB:
            case PURPUR_DOUBLE_SLAB:
            case HARD_CLAY:
            case STAINED_CLAY:
            case CLAY_BRICK:
            case NETHERRACK:
            case COAL_BLOCK:
            case BRICK:
                return Instrument.BASS_DRUM;
            case CLAY:
                return Instrument.FLUTE;
            case GOLD_BLOCK:
                return Instrument.BELL;
            case WOOL:
                return Instrument.GUITAR;
            case PACKED_ICE:
                return Instrument.CHIME;
            case BONE_BLOCK:
                return Instrument.XYLOPHONE;
            case DIRT:
            case AIR:
            default:
                return Instrument.PIANO;
        }
    }

    private NoteblockEntity getBlockEntity() {
        return (NoteblockEntity) getBlock().getBlockEntity();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implementation

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            getBlockEntity().setNote(note);
        }
        return result;
    }

    @Override
    public void setNote(Note note) {
        checkNotNull(note);
        this.note = note;
    }

    @Override
    public byte getRawNote() {
        return note.getId();
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
        return play(Instrument.getByType(instrument), new Note(note));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    @Override
    public boolean play(Instrument instrument, Note note) {
        if (getBlock().getType() != Material.NOTE_BLOCK) {
            return false;
        }
        NotePlayEvent event = EventFactory
            .callEvent(new NotePlayEvent(getBlock(), instrument, note));
        if (event.isCancelled()) {
            return false;
        }

        Location location = getBlock().getLocation();

        Key key = GlowChunk.Key.of(getX() >> 4, getZ() >> 4);
        getWorld().getRawPlayers().stream().filter(player -> player.canSeeChunk(key))
            .forEach(player -> player.playNote(location, instrument, note));

        return true;
    }

}
