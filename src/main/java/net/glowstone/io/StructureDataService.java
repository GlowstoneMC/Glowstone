package net.glowstone.io;

import net.glowstone.generator.structures.GlowStructure;

import java.io.IOException;
import java.util.Map;

/**
 * Provider of I/O for structures data.
 */
public interface StructureDataService {

    /**
     * Reads the structures data from storage.
     * @return A map containing structures indexed by their chunk hash.
     * @throws IOException if an I/O error occurs.
     */
    Map<Integer, GlowStructure> readStructuresData() throws IOException;

    /**
     * Write the structures data to storage.
     * @throws IOException if an I/O error occurs.
     */
    void writeStructuresData(Map<Integer, GlowStructure> structures) throws IOException;
}
