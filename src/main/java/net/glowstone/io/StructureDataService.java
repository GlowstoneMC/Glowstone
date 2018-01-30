package net.glowstone.io;

import java.util.Map;
import net.glowstone.generator.structures.GlowStructure;

/**
 * Provider of I/O for structures data.
 */
public interface StructureDataService {

    /**
     * Reads the structures data from storage.
     *
     * @return A map containing structures indexed by their chunk hash.
     */
    Map<Integer, GlowStructure> readStructuresData();

    /**
     * Write the structures data to storage.
     *
     * @param structures The structures to write to storage.
     */
    void writeStructuresData(Map<Integer, GlowStructure> structures);
}
