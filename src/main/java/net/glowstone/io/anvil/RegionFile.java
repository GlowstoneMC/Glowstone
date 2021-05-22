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
 * It has been slightly modified by Mojang AB (constants instead of magic
 * numbers, a chunk timestamp header, and auto-formatted according to our
 * formatter template).
 *
 */

/*
 * Later changes made by the Glowstone project.
 */

package net.glowstone.io.anvil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.util.config.ServerConfig.Key;

/**
 * Interfaces with region files on the disk
 *
 * <p><strong>Region File Format</strong>
 *
 * <p>Concept: The minimum unit of storage on hard drives is 4KB. 90% of Minecraft chunks are
 * smaller than 4KB. 99% are smaller than 8KB. Write a simple container to store chunks in single
 * files in runs of 4KB sectors.
 *
 * <p>Each region file represents a 32x32 group of chunks. The conversion from chunk number to
 * region number is floor(coord / 32): a chunk at (30, -3) would be in region (0, -1), and one at
 * (70, -30) would be at (3, -1). Region files are named "r.x.z.data", where x and z are the region
 * coordinates.
 *
 * <p>A region file begins with a 4KB header that describes where chunks are stored in the file. A
 * 4-byte big-endian integer represents sector offsets and sector counts. The chunk offset for a
 * chunk (x, z) begins at byte 4*(x+z*32) in the file. The bottom byte of the chunk offset indicates
 * the number of sectors the chunk takes up, and the top 3 bytes represent the sector number of the
 * chunk. Given a chunk offset o, the chunk data begins at byte 4096*(o/256) and takes up at most
 * 4096*(o%256) bytes. A chunk cannot exceed 1MB in size. If a chunk offset is 0, the corresponding
 * chunk is not stored in the region file.
 *
 * <p>Chunk data begins with a 4-byte big-endian integer representing the chunk data length in
 * bytes, not counting the length field. The length must be smaller than 4096 times the number of
 * sectors. The next byte is a version field, to allow backwards-compatible updates to how chunks
 * are encoded.
 *
 * <p>A version of 1 represents a gzipped NBT file. The gzipped data is the chunk length - 1.
 *
 * <p>A version of 2 represents a deflated (zlib compressed) NBT file. The deflated data is the
 * chunk length - 1.
 */
public class RegionFile {

    private static final boolean COMPRESSION_ENABLED =
        ((GlowServer) ServerProvider.getServer()).getConfig()
            .getBoolean(Key.REGION_COMPRESSION);

    private static final byte VERSION_GZIP = 1;
    private static final byte VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    private static final int CHUNK_HEADER_SIZE = 5;

    private static final byte[] emptySector = new byte[SECTOR_BYTES];
    private final int[] offsets;
    private final int[] chunkTimestamps;
    private final AtomicInteger sizeDelta = new AtomicInteger();
    private final RandomAccessFile file;
    private final BitSet sectorsUsed;
    /**
     * Returns the modification timestamp of the region file when it was first opened by this
     * instance, or zero if this instance created the file. The timestamp is in milliseconds since
     * the Unix epoch (see {@link File#lastModified}).
     *
     * @return the modification timestamp
     */
    @Getter
    private long lastModified;

    /**
     * Opens a region file for reading and writing, creating it if it doesn't exist.
     *
     * @param path the file path; must be in an existing folder
     * @throws IOException if the file cannot be opened
     */
    public RegionFile(File path) throws IOException {
        offsets = new int[SECTOR_INTS];
        chunkTimestamps = new int[SECTOR_INTS];

        sizeDelta.set(0);

        if (path.exists()) {
            lastModified = path.lastModified();
        }

        file = new RandomAccessFile(path, "rw");

        int initialLength = (int) file.length();

        // if the file size is under 8KB, grow it (4K chunk offset table, 4K timestamp table)
        if (lastModified == 0 || initialLength < 4096) {
            // fast path for new or region files under 4K
            file.write(emptySector);
            file.write(emptySector);
            sizeDelta.set(2 * SECTOR_BYTES);
        } else {
            // seek to the end to prepare for grows
            file.seek(initialLength);
            if (initialLength < 2 * SECTOR_BYTES) {
                // if the file size is under 8KB, grow it
                sizeDelta.set(2 * SECTOR_BYTES - initialLength);
                GlowServer.logger.warning(
                    "Region \"" + path + "\" under 8K: " + initialLength + " increasing by " + (
                        2 * SECTOR_BYTES - initialLength));

                for (long i = 0; i < sizeDelta.get(); ++i) {
                    file.write(0);
                }
            } else if ((initialLength & (SECTOR_BYTES - 1)) != 0) {
                // if the file size is not a multiple of 4KB, grow it
                sizeDelta.set(initialLength & (SECTOR_BYTES - 1));
                GlowServer.logger.warning(
                    "Region \"" + path + "\" not aligned: " + initialLength + " increasing by "
                        + (
                        SECTOR_BYTES - (initialLength & (SECTOR_BYTES - 1))));

                for (long i = 0; i < sizeDelta.get(); ++i) {
                    file.write(0);
                }
            }
        }

        // set up the available sector map
        int totalSectors = (int) Math.ceil(file.length() / (double) SECTOR_BYTES);
        sectorsUsed = new BitSet(totalSectors);

        // reserve the first two sectors
        sectorsUsed.set(0, 2);

        // read offset table and timestamp tables
        file.seek(0);

        ByteBuffer header = ByteBuffer.allocate(2 * SECTOR_BYTES);
        while (header.hasRemaining()) {
            if (file.getChannel().read(header) == -1) {
                throw new EOFException();
            }
        }
        header.flip();

        // populate the offset table
        IntBuffer headerAsInts = header.asIntBuffer();
        for (int i = 0; i < SECTOR_INTS; ++i) {
            int offset = headerAsInts.get();
            offsets[i] = offset;

            int startSector = offset >> 8;
            int numSectors = offset & 255;

            if (offset != 0 && startSector >= 0 && startSector + numSectors <= totalSectors) {
                sectorsUsed.set(startSector, startSector + numSectors + 1);
            } else if (offset != 0) {
                GlowServer.logger.warning(
                    "Region \"" + path + "\": offsets[" + i + "] = " + offset + " -> "
                        + startSector
                        + "," + numSectors + " does not fit");
            }
        }

        for (int i = 0; i < SECTOR_INTS; ++i) {
            chunkTimestamps[i] = headerAsInts.get();
        }
    }

    /**
     * Returns how much the region file has grown since this function was last called.
     *
     * @return the growth in bytes
     */
    public int getSizeDelta() {
        return sizeDelta.getAndSet(0);
    }

    /**
     * Gets an (uncompressed) stream representing the chunk data. Returns null if the chunk is not
     * found or an error occurs.
     *
     * @param x the chunk X coordinate relative to the region
     * @param z the chunk Z coordinate relative to the region
     * @return an input stream with the chunk data, or null if the chunk is missing
     * @throws IOException if the file cannot be read, or the chunk is invalid
     */
    public DataInputStream getChunkDataInputStream(int x, int z) throws IOException {
        checkBounds(x, z);

        int offset = getOffset(x, z);
        if (offset == 0) {
            // does not exist
            return null;
        }

        int totalSectors = sectorsUsed.length();
        int sectorNumber = offset >> 8;
        int numSectors = offset & 0xFF;
        if (sectorNumber + numSectors > totalSectors) {
            throw new IOException(
                "Invalid sector: " + sectorNumber + "+" + numSectors + " > " + totalSectors);
        }

        file.seek(sectorNumber * SECTOR_BYTES);
        int length = file.readInt();
        if (length > SECTOR_BYTES * numSectors) {
            throw new IOException("Invalid length: " + length + " > " + SECTOR_BYTES * numSectors);
        } else if (length <= 0) {
            throw new IOException("Invalid length: " + length + " <= 0 ");
        }

        byte version = file.readByte();
        if (version == VERSION_GZIP) {
            byte[] data = new byte[length - 1];
            file.read(data);
            try {
                return new DataInputStream(new BufferedInputStream(
                    new GZIPInputStream(new ByteArrayInputStream(data), 2048)));
            } catch (ZipException e) {
                if (e.getMessage().equals("Not in GZIP format")) {
                    GlowServer.logger.info("Incorrect region version, switching to zlib...");
                    file.seek((sectorNumber * SECTOR_BYTES) + Integer.BYTES);
                    file.write(VERSION_DEFLATE);
                    return getZlibInputStream(data);
                }
            }
        } else if (version == VERSION_DEFLATE) {
            byte[] data = new byte[length - 1];
            file.read(data);
            return getZlibInputStream(data);
        }

        throw new IOException("Unknown version: " + version);
    }

    private DataInputStream getZlibInputStream(byte[] data) {
        return new DataInputStream(new BufferedInputStream(new InflaterInputStream(
            new ByteArrayInputStream(data), new Inflater(), 2048)));
    }

    /**
     * Creates a {@link DataOutputStream} to write a chunk to a byte.
     *
     * @param x the chunk X coordinate within the region
     * @param z the chunk Z coordinate within the region
     * @return a {@link DataOutputStream}, backed by memory, that can prepare the chunk for writing
     * to disk.
     */
    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        checkBounds(x, z);
        Deflater deflater = new Deflater(
            COMPRESSION_ENABLED ? Deflater.BEST_SPEED : Deflater.NO_COMPRESSION);
        deflater.setStrategy(Deflater.HUFFMAN_ONLY);
        DeflaterOutputStream dos = new DeflaterOutputStream(new ChunkBuffer(x, z), deflater, 2048) {
            @Override
            public void close() throws IOException {
                super.close();
                def.end();
            }
        };
        return new DataOutputStream(new BufferedOutputStream(dos));
    }

    /* write a chunk at (x,z) with length bytes of data to disk */
    protected void write(int x, int z, byte[] data, int length) throws IOException {
        int offset = getOffset(x, z);
        int sectorNumber = offset >> 8;
        int sectorsAllocated = offset & 0xFF;
        int sectorsNeeded = (length + CHUNK_HEADER_SIZE) / SECTOR_BYTES + 1;

        // maximum chunk size is 1MB
        if (sectorsNeeded >= 256) {
            return;
        }

        if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
            /* we can simply overwrite the old sectors */
            writeSector(sectorNumber, data, length);
        } else {
            /* mark the sectors previously used for this chunk as free */
            if (sectorNumber != 0) {
                sectorsUsed.clear(sectorNumber, sectorNumber + sectorsAllocated + 1);
            }

            /* scan for a free space large enough to store this chunk */
            sectorNumber = findNewSectorStart(sectorsNeeded);
            if (sectorNumber == -1) {
                /*
                 * no free space large enough found -- we need to grow the
                 * file
                 */
                file.seek(file.length());
                for (int i = 0; i < sectorsNeeded; ++i) {
                    file.write(emptySector);
                }
                sizeDelta.addAndGet(SECTOR_BYTES * sectorsNeeded);
                sectorNumber = sectorsUsed.length();
            }

            sectorsUsed.set(sectorNumber, sectorNumber + sectorsNeeded + 1);
            writeSector(sectorNumber, data, length);
            setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
            setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000L));
        }
    }

    private int findNewSectorStart(int sectorsNeeded) {
        int start = -1;
        int runLength = 0;
        for (int i = sectorsUsed.nextClearBit(0); i < sectorsUsed.length(); i++) {
            if (sectorsUsed.get(i)) {
                // must reset
                start = -1;
                runLength = 0;
            } else {
                if (start == -1) {
                    start = i;
                }
                runLength++;
                if (runLength >= sectorsNeeded) {
                    return start;
                }
            }
        }
        // reached the end, append to the end of the region instead
        return -1;
    }

    /* write a chunk data to the region file at specified sector number */
    private void writeSector(int sectorNumber, byte[] data, int length) throws IOException {
        file.seek(sectorNumber * SECTOR_BYTES);
        file.writeInt(length + 1); // chunk length
        file.writeByte(VERSION_DEFLATE); // chunk version number
        file.write(data, 0, length); // chunk data
    }

    /* is this an invalid chunk coordinate? */
    private void checkBounds(int x, int z) {
        if (x < 0 || x >= 32 || z < 0 || z >= 32) {
            throw new IllegalArgumentException("Chunk out of bounds: (" + x + ", " + z + ")");
        }
    }

    private int getOffset(int x, int z) {
        return offsets[x + (z << 5)];
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    private void setOffset(int x, int z, int offset) throws IOException {
        offsets[x + (z << 5)] = offset;
        file.seek((x + (z << 5)) << 2);
        file.writeInt(offset);
    }

    private void setTimestamp(int x, int z, int value) throws IOException {
        chunkTimestamps[x + (z << 5)] = value;
        file.seek(SECTOR_BYTES + ((x + (z << 5)) << 2));
        file.writeInt(value);
    }

    public void close() throws IOException {
        file.getChannel().force(true);
        file.close();
    }

    /*
     * lets chunk writing be multithreaded by not locking the whole file as a
     * chunk is serializing -- only writes when serialization is over
     */
    class ChunkBuffer extends ByteArrayOutputStream {

        private final int x;
        private final int z;

        public ChunkBuffer(int x, int z) {
            super(SECTOR_BYTES); // initialize to 4KB
            this.x = x;
            this.z = z;
        }

        @Override
        public void close() throws IOException {
            RegionFile.this.write(x, z, buf, count);
        }
    }
}
