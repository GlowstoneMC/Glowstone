package net.glowstone.io.structure;

import static net.glowstone.GlowInternationalizedStrings.logError;

import java.util.HashMap;
import java.util.Map;
import net.glowstone.generator.structures.GlowStructurePiece;
import net.glowstone.util.nbt.CompoundTag;

/**
 * The class responsible for mapping structure piece types to their storage methods and reading and
 * writing structure piece data using those storage methods.
 */
public final class StructurePieceStorage {

    /**
     * A table which maps structure pieces ids to compound readers.
     *
     * <p>This is generally used to map stored structure pieces to actual structure pieces.
     */
    private static final Map<String, StructurePieceStore<?>> idTable = new HashMap<>();
    /**
     * A table which maps structure pieces to stores.
     *
     * <p>This is generally used to map structure pieces being stored.
     */
    private static final Map<Class<? extends GlowStructurePiece>, StructurePieceStore<?>> classTable
            = new HashMap<>();

    /*
     * Populates the maps with stores.
     */
    static {
        bind(new DesertTempleStore());
        bind(new JungleTempleStore());
        bind(new WitchHutStore());
    }

    private StructurePieceStorage() {
    }

    /**
     * Binds a store by adding entries for it to the tables.
     *
     * @param store The store object.
     * @param <T> The type of structure piece.
     */
    private static <T extends GlowStructurePiece> void bind(StructurePieceStore<T> store) {
        idTable.put(store.getId(), store);
        classTable.put(store.getType(), store);
    }

    /**
     * Load a structure piece from the given data tag.
     *
     * @param compound The tag to load from.
     * @return The newly constructed structure piece.
     * @throws IllegalArgumentException if there is an error in the data.
     */
    public static GlowStructurePiece loadStructurePiece(CompoundTag compound) {
        // look up the store by the tag's id
        if (!compound.isString("id")) {
            throw new IllegalArgumentException("StructurePiece has no type");
        }
        StructurePieceStore<?> store = idTable.get(compound.getString("id"));
        if (store == null) {
            logError("console.error.structure.unknown-piece-type",
                    compound.getString("id"));
            return null;
        }

        return createStructurePiece(store, compound);
    }

    /**
     * Save a structure piece data to the given compound tag.
     *
     * @param structurePiece The structure piece to save.
     * @param compound The target tag.
     */
    public static void saveStructurePiece(GlowStructurePiece structurePiece, CompoundTag compound) {
        // look up the store for the structure piece
        StructurePieceStore<?> store = classTable.get(structurePiece.getClass());
        if (store == null) {
            throw new IllegalArgumentException(
                    "Unknown structure piece type to save: \"" + structurePiece.getClass() + "\"");
        }

        compound.putString("id", store.getId());

        getBaseStore(store).save(structurePiece, compound);
    }

    /**
     * Helper method to call StructurePieceStore methods for type safety.
     */
    private static <T extends GlowStructurePiece> T createStructurePiece(
            StructurePieceStore<T> store, CompoundTag compound) {
        T structurePiece = store.createStructurePiece();
        store.load(structurePiece, compound);
        return structurePiece;
    }

    /**
     * Unsafe-cast an unknown StructurePieceStore to the base type.
     */
    @SuppressWarnings("unchecked")
    private static StructurePieceStore<GlowStructurePiece> getBaseStore(
            StructurePieceStore<?> store) {
        return (StructurePieceStore<GlowStructurePiece>) store;
    }
}
