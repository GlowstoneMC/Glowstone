package net.glowstone.io;

import net.glowstone.generator.structures.GlowStructure;

import java.util.Map;

/**
 * Provider of I/O for structures data.
 */
public interface StructureDataService {

    /**
     * Reads the structures data from storage.
     *
     * @return A map containing structures indexed by their chunk hash.
     */
    Map<Long, GlowStructure> readStructuresData();

    /**
     * Write the structures data to storage.
     *
     * @param structures The structures to write to storage.
     */
    void writeStructuresData(Map<Long, GlowStructure> structures);
}
