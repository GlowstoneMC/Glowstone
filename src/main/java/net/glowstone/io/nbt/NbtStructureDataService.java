package net.glowstone.io.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.glowstone.GlowWorld;
import net.glowstone.ServerProvider;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.generator.structures.GlowStructure;
import net.glowstone.i18n.LocalizedStrings;
import net.glowstone.io.StructureDataService;
import net.glowstone.io.structure.StructureStorage;
import net.glowstone.io.structure.StructureStore;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NbtInputStream;
import net.glowstone.util.nbt.NbtOutputStream;
import org.bukkit.Server;

public class NbtStructureDataService implements StructureDataService {

    private final GlowWorld world;
    private final File structureDir;
    private final Server server;

    /**
     * Creates the instance for the given world's structures.
     *
     * @param world the world
     * @param structureDir the world's structure-data folder, which is created if it doesn't exist
     */
    public NbtStructureDataService(GlowWorld world, File structureDir) {
        this.world = world;
        this.structureDir = structureDir;
        server = ServerProvider.getServer();

        if (!structureDir.isDirectory() && !structureDir.mkdirs()) {
            LocalizedStrings.Console.Warn.Io.MKDIR_FAILED.log(structureDir);
        }
    }

    @Override
    public Map<Integer, GlowStructure> readStructuresData() {
        Map<Integer, GlowStructure> structures = new HashMap<>();
        for (StructureStore<?> store : StructureStorage.getStructureStores()) {
            File structureFile = new File(structureDir, store.getId() + ".dat");
            if (structureFile.exists()) {
                try (NbtInputStream in = new NbtInputStream(new FileInputStream(structureFile))) {
                    CompoundTag data = in.readCompound();
                    if (data.isCompound("data")) { // NON-NLS
                        data = data.getCompound("data"); // NON-NLS
                        if (data.isCompound("Features")) { // NON-NLS
                            CompoundTag features = data.getCompound("Features"); // NON-NLS
                            features.getValue().keySet().stream().filter(features::isCompound)
                                .forEach(key -> {
                                    GlowStructure structure = StructureStorage
                                        .loadStructure(world, features.getCompound(key));
                                    structures.put(GlowChunk.Key
                                        .of(structure.getChunkX(), structure.getChunkZ())
                                        .hashCode(), structure);
                                });
                        }
                    } else {
                        LocalizedStrings.Console.Error.Structure.NO_DATA.log(structureFile);
                    }
                } catch (IOException e) {
                    LocalizedStrings.Console.Error.Structure.IO_READ.log(e, structureFile);
                }
            }
        }
        return structures;
    }

    @Override
    public void writeStructuresData(Map<Integer, GlowStructure> structures) {
        for (GlowStructure structure : structures.values()) {
            if (structure.isDirty()) {
                CompoundTag data = new CompoundTag();
                CompoundTag features = new CompoundTag();
                CompoundTag feature = new CompoundTag();
                StructureStore<GlowStructure> store = StructureStorage
                    .saveStructure(structure, feature);
                File structureFile = new File(structureDir, store.getId() + ".dat");
                if (structureFile.exists()) {
                    try (NbtInputStream in = new NbtInputStream(
                        new FileInputStream(structureFile))) {
                        data = new CompoundTag();
                        data = in.readCompound();
                        if (data.isCompound("data")) { // NON-NLS
                            data = data.getCompound("data"); // NON-NLS
                            if (data.isCompound("Features")) { // NON-NLS
                                features = data.getCompound("Features"); // NON-NLS
                            }
                        }
                    } catch (IOException e) {
                        LocalizedStrings.Console.Error.Structure.IO_READ.log(e, structureFile);
                    }
                }
                String key = "[" + structure.getChunkX() + "," + structure.getChunkZ() + "]";
                features.putCompound(key, feature);
                data.putCompound("Features", features); // NON-NLS
                CompoundTag root = new CompoundTag();
                root.putCompound("data", data); // NON-NLS
                try (NbtOutputStream nbtOut = new NbtOutputStream(
                    new FileOutputStream(structureFile))) {
                    nbtOut.writeTag(root);
                } catch (IOException e) {
                    LocalizedStrings.Console.Error.Structure.IO_WRITE.log(e, structureFile);
                }
                structure.setDirty(false);
            }
        }
    }
}
