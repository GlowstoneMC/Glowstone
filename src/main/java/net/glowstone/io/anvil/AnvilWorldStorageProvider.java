package net.glowstone.io.anvil;

import lombok.Getter;
import net.glowstone.GlowWorld;
import net.glowstone.io.FunctionIoService;
import net.glowstone.io.PlayerDataService;
import net.glowstone.io.ScoreboardIoService;
import net.glowstone.io.StructureDataService;
import net.glowstone.io.WorldStorageProvider;
import net.glowstone.io.data.WorldFunctionIoService;
import net.glowstone.io.json.JsonPlayerStatisticIoService;
import net.glowstone.io.nbt.NbtPlayerDataService;
import net.glowstone.io.nbt.NbtScoreboardIoService;
import net.glowstone.io.nbt.NbtStructureDataService;
import net.glowstone.io.nbt.NbtWorldMetadataService;

import java.io.File;

/**
 * A {@link WorldStorageProvider} for the Anvil map format.
 */
public class AnvilWorldStorageProvider implements WorldStorageProvider {

    @Getter
    private final File folder;
    private final File dataDir;
    private GlowWorld world;
    @Getter(lazy = true)
    private final PlayerDataService playerDataService
        = new NbtPlayerDataService(world.getServer(), new File(folder, "playerdata"));
    @Getter(lazy = true)
    private final ScoreboardIoService scoreboardIoService
        = new NbtScoreboardIoService(world.getServer(), new File(folder, "data"));
    @Getter(lazy = true)
    private final JsonPlayerStatisticIoService playerStatisticIoService
        = new JsonPlayerStatisticIoService(world.getServer(), new File(folder, "stats"));
    @Getter(lazy = true)
    private final FunctionIoService functionIoService = new WorldFunctionIoService(world, dataDir);
    @Getter
    private AnvilChunkIoService chunkIoService;
    @Getter
    private NbtWorldMetadataService metadataService;
    @Getter
    private StructureDataService structureDataService;

    /**
     * Create an instance for the given root folder.
     *
     * @param folder the root folder
     */
    public AnvilWorldStorageProvider(File folder) {
        this.folder = folder;
        this.dataDir = new File(folder, "data");
        this.dataDir.mkdirs();
    }

    @Override
    public void setWorld(GlowWorld world) {
        if (this.world != null) {
            throw new IllegalArgumentException("World is already set");
        }
        this.world = world;
        chunkIoService = new AnvilChunkIoService(folder);
        metadataService = new NbtWorldMetadataService(world, folder);
        dataDir.mkdirs();
        structureDataService = new NbtStructureDataService(world, dataDir);
    }
}
