package net.glowstone.io.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Data;
import net.glowstone.GlowWorld;
import net.glowstone.generator.structures.GlowStructure;
import net.glowstone.generator.structures.GlowStructurePiece;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.util.Vector;

/**
 * The base for structure store classes.
 *
 * @param <T> The type of structure being stored.
 */
@Data
public abstract class StructureStore<T extends GlowStructure> {

    private final Class<T> type;
    private final String id;

    /**
     * Create a structure of this store's type in the given world. The load method will be called
     * separately.
     *
     * @param world The target world.
     * @param chunkX The structure chunk X.
     * @param chunkZ The structure chunk Z.
     * @return The structure.
     */
    public abstract T createStructure(GlowWorld world, int chunkX, int chunkZ);

    /**
     * Create a new structure of this store's type in the given world. The load method will be
     * called separately.
     *
     * @param world The target world.
     * @param random The seeded random.
     * @param chunkX The structure chunk X.
     * @param chunkZ The structure chunk Z.
     * @return The new structure.
     */
    public abstract T createNewStructure(GlowWorld world, Random random, int chunkX, int chunkZ);

    /**
     * Load structure data of the appropriate type from the given compound tag.
     *
     * @param structure The target structure.
     * @param compound The structure's tag.
     */
    public void load(T structure, CompoundTag compound) {
        compound.readIntArray(bb -> {
            if (bb.length == 6) {
                StructureBoundingBox boundingBox = new StructureBoundingBox(
                        new Vector(bb[0], bb[1], bb[2]), new Vector(bb[3], bb[4], bb[5]));
                structure.setBoundingBox(boundingBox);
            }
        }, "BB");
        compound.iterateCompoundList(
            tag -> structure.addPiece(StructurePieceStorage.loadStructurePiece(tag)),
                "Children");
    }

    /**
     * Save information about this structure to the given tag.
     *
     * @param structure The structure to save.
     * @param compound The target tag.
     */
    public void save(T structure, CompoundTag compound) {
        StructureBoundingBox boundingBox = structure.getBoundingBox();
        int[] bb = new int[6];
        bb[0] = boundingBox.getMin().getBlockX();
        bb[1] = boundingBox.getMin().getBlockY();
        bb[2] = boundingBox.getMin().getBlockZ();
        bb[3] = boundingBox.getMax().getBlockX();
        bb[4] = boundingBox.getMax().getBlockY();
        bb[5] = boundingBox.getMax().getBlockZ();
        compound.putIntArray("BB", bb);
        List<CompoundTag> children = new ArrayList<>();
        for (GlowStructurePiece piece : structure.getPieces()) {
            CompoundTag tag = new CompoundTag();
            StructurePieceStorage.saveStructurePiece(piece, tag);
            children.add(tag);
        }
        compound.putCompoundList("Children", children);
    }
}
