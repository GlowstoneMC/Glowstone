/*
 * 2011 January 5
 *
 * The author disclaims copyright to this source code. In place of
 * a legal notice, here is a blessing:
 *
 *    May you do good and not evil.
 *    May you find forgiveness for yourself and forgive others.
 *    May you share freely, never taking more than you give.
 */

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
 * Later changes made by the Glowstone project.
 */

package net.glowstone.io.anvil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import net.glowstone.GlowServer;
import net.glowstone.util.config.ServerConfig.Key;

/**
 * A simple cache and wrapper for efficiently accessing multiple RegionFiles simultaneously.
 */
public class RegionFileCache {

    private static final int MAX_CACHE_SIZE =
            ((GlowServer) GlowServerProvider.getServer()).getConfig().getInt(Key.REGION_CACHE_SIZE);

    private static final RemovalListener<File, RegionFile> removalListener = removal -> {
        try {
            removal.getValue().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    private LoadingCache<File, RegionFile> regions = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(MAX_CACHE_SIZE)
            .removalListener(removalListener)
            .build(new CacheLoader<File, RegionFile>() {
                @Override
                public RegionFile load(File file) throws Exception {
                    return new RegionFile(file);
                }
            });

    private final String extension;
    private final File regionDir;

    public RegionFileCache(File basePath, String extension) {
        this.extension = extension;
        regionDir = new File(basePath, "region");
    }

    /**
     * Returns the region file where a chunk is stored, opening it if necessary. Both the region
     * file and the directory containing it will be created if they don't exist.
     *
     * @param chunkX the absolute chunk X coordinate
     * @param chunkZ the absolute chunk Z coordinate
     * @return the region file
     */
    public RegionFile getRegionFile(int chunkX, int chunkZ) {
        if (!regionDir.isDirectory() && !regionDir.mkdirs()) {
            GlowServer.logger.warning("Failed to create directory: " + regionDir);
        }
        return regions.getUnchecked(
                new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + extension));
    }

    public void clear() throws RejectedExecutionException {
        regions.invalidateAll();
    }

}
