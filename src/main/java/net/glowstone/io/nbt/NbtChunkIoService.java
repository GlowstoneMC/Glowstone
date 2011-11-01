package net.glowstone.io.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.io.ChunkIoService;
import net.glowstone.util.nbt.ByteArrayTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.Tag;

/**
 * An implementation of the {@link net.glowstone.io.ChunkIoService} which reads and writes NBT
 * maps.
 * @author Graham Edgecombe
 */
public final class NbtChunkIoService implements ChunkIoService {
    private final File dir;

    public NbtChunkIoService() {
        this(new File("world"));
    }

    public NbtChunkIoService(File dir) {
        this.dir = dir;
    }


    public boolean read(GlowChunk chunk, int x, int z) {
        int fileX = formatInt(x);
        int fileZ = formatInt(z);

        File chunkFile = new File(dir, Integer.toString(fileX & 63, 36) + File.separatorChar + Integer.toString(fileZ & 63, 36)
                + File.separatorChar + "c." + Integer.toString(fileX, 36) + "." + Integer.toString(fileZ, 36) + ".dat");

        Map<String, Tag> levelTags;
        try {
            NBTInputStream nbt = new NBTInputStream(new FileInputStream(chunkFile));
            CompoundTag tag = (CompoundTag) nbt.readTag();
            levelTags = ((CompoundTag) tag.getValue().get("Level")).getValue();
        } catch (IOException e) {
            return false;
        }

        byte[] tileData = ((ByteArrayTag) levelTags.get("Blocks")).getValue();
        chunk.initializeTypes(tileData);

        byte[] skyLightData = ((ByteArrayTag) levelTags.get("SkyLight")).getValue();
        byte[] blockLightData = ((ByteArrayTag) levelTags.get("BlockLight")).getValue();
        byte[] metaData = ((ByteArrayTag) levelTags.get("Data")).getValue();

        for (int cx = 0; cx < GlowChunk.WIDTH; cx++) {
            for (int cz = 0; cz < GlowChunk.HEIGHT; cz++) {
                for (int cy = 0; cy < GlowChunk.DEPTH; cy++) {
                    boolean mostSignificantNibble = ((cx * GlowChunk.HEIGHT + cz) * GlowChunk.DEPTH + cy) % 2 == 1;
                    int offset = ((cx * GlowChunk.HEIGHT + cz) * GlowChunk.DEPTH + cy) / 2;

                    int skyLight, blockLight, meta;
                    if (mostSignificantNibble) {
                        skyLight = (skyLightData[offset] & 0xF0) >> 4;
                        blockLight = (blockLightData[offset] & 0xF0) >> 4;
                        meta = (metaData[offset] & 0xF0) >> 4;
                    } else {
                        skyLight = skyLightData[offset] & 0x0F;
                        blockLight = blockLightData[offset] & 0x0F;
                        meta = metaData[offset] & 0x0F;
                    }

                    chunk.setSkyLight(cx, cz, cy, skyLight);
                    chunk.setBlockLight(cx, cz, cy, blockLight);
                    chunk.setMetaData(cx, cz, cy, meta);
                }
            }
        }

        return true;
    }

    public void write(int x, int z, GlowChunk chunk) throws IOException {

    }

    public void unload() throws IOException {
    }

    private int formatInt(int i) {
        if (i >= 0)
            return i;
        String bin = Integer.toBinaryString(i);
        StringBuilder ret = new StringBuilder();
        byte[] bytes = bin.getBytes();
        for (int ii = 1; i < bytes.length; ii++) {
            if (bytes[ii] == 1)
                break;
            ret.append((ii == 0) ? 1 : ii);
        }
        return Integer.parseInt(ret.toString());
    }

}
