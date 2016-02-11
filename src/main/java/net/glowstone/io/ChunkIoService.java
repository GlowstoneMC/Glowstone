package net.glowstone.io;

import net.glowstone.GlowChunk;

import java.io.IOException;

/**
 * Provider of chunk I/O services. Implemented by classes to provide a way of
 * saving and loading chunks to external storage.
 */
public interface ChunkIoService {

    /**
     * Reads a single chunk. The provided chunk must not yet be initialized.
     *
     * @param chunk The GlowChunk to read into.
     * @throws IOException if an I/O error occurs.
     */
    boolean read(GlowChunk chunk) throws IOException;

    /**
     * Writes a single chunk.
     *
     * @param chunk The {@link GlowChunk} to write from.
     * @throws IOException if an I/O error occurs.
     */
    void write(GlowChunk chunk) throws IOException;

    /**
     * Unload the service, performing any cleanup necessary.
     *
     * @throws IOException if an I/O error occurs.
     */
    void unload() throws IOException;

}
