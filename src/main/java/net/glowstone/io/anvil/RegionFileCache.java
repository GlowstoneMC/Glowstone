package net.glowstone.io.anvil;

/*
 ** 2011 January 5
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 **/

/*
 * 2011 February 16
 *
 * This source code is based on the work of Scaevolus (see notice above).
 * It has been slightly modified by Mojang AB to limit the maximum cache
 * size (relevant to extremely big worlds on Linux systems with limited
 * number of file handles). The region files are postfixed with ".mcr"
 * (Minecraft region file) instead of ".data" to differentiate from the
 * original McRegion files.
 *
 */

/*
 * Some changes have been made as part of the Glowstone project.
 */

import net.glowstone.GlowServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple cache and wrapper for efficiently accessing multiple RegionFiles simultaneously.
 */
public class RegionFileCache {

    private static final int MAX_CACHE_SIZE = 256;

    private final Map<File, Reference<RegionFile>> cache = new HashMap<>();

    private final String extension;

    public RegionFileCache(String extension) {
        this.extension = extension;
    }

    public RegionFile getRegionFile(File basePath, int chunkX, int chunkZ) throws IOException {
        File regionDir = new File(basePath, "region");
        File file = new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + extension);

        Reference<RegionFile> ref = cache.get(file);

        if (ref != null && ref.get() != null) {
            return ref.get();
        }

        if (!regionDir.isDirectory() && !regionDir.mkdirs()) {
            GlowServer.logger.warning("Failed to create directory: " + regionDir);
        }

        if (cache.size() >= MAX_CACHE_SIZE) {
            clear();
        }

        RegionFile reg = new RegionFile(file);
        cache.put(file, new SoftReference<>(reg));
        return reg;
    }

    public void clear() throws IOException {
        for (Reference<RegionFile> ref : cache.values()) {
            RegionFile value = ref.get();
            if (value != null) {
                value.close();
            }
        }
        cache.clear();
    }

    public int getSizeDelta(File basePath, int chunkX, int chunkZ) throws IOException {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.getSizeDelta();
    }

    public DataInputStream getChunkDataInputStream(File basePath, int chunkX, int chunkZ) throws IOException {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
    }

    public DataOutputStream getChunkDataOutputStream(File basePath, int chunkX, int chunkZ) throws IOException {
        RegionFile r = getRegionFile(basePath, chunkX, chunkZ);
        return r.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
    }

}
