package net.glowstone.io;

import java.io.File;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import net.glowstone.io.region.RegionFile;
import net.glowstone.io.region.RegionFileCache;
import net.glowstone.GlowChunk;
import net.glowstone.util.nbt.ByteArrayTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.Tag;

/**
 * An implementation of the {@link ChunkIoService} which reads and writes
 * McRegion maps.
 * <p />
 * Information on the McRegion file format can be found on the
 * <a href="http://mojang.com/2011/02/16/minecraft-save-file-format-in-beta-1-3">Mojang</a>
 * blog.
 * @author Zhuowei Zhang
 * @author Graham Edgecombe
 */
public final class McRegionChunkIoService implements ChunkIoService {

	/**
	 * The size of a region - a 32x32 group of chunks.
	 */
	private static final int REGION_SIZE = 32;

	/**
	 * The root directory of the map.
	 */
	private File dir;

	/**
	 * The region file cache.
	 */
	private RegionFileCache cache = new RegionFileCache();

	// TODO: consider the session.lock file

	public McRegionChunkIoService() {
		this(new File("world"));
	}

	public McRegionChunkIoService(File dir) {
		this.dir = dir;
	}

	@Override
	public GlowChunk read(int x, int z) throws IOException {
		RegionFile region = cache.getRegionFile(dir, x, z);
		int regionX = x & (REGION_SIZE - 1);
		int regionZ = z & (REGION_SIZE - 1);
		if (!region.hasChunk(regionX, regionZ)){
			return null;
		}

		DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);
		GlowChunk chunk = new GlowChunk(x, z);

		NBTInputStream nbt = new NBTInputStream(in, false);
		CompoundTag tag = (CompoundTag) nbt.readTag();
		Map<String, Tag> levelTags = ((CompoundTag) tag.getValue().get("Level")).getValue();

		byte[] tileData = ((ByteArrayTag) levelTags.get("Blocks")).getValue();
		chunk.setTypes(tileData);

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

		return chunk;
	}

	@Override
	public void write(int x, int z, GlowChunk chunk) throws IOException {
		// TODO
	}

}
