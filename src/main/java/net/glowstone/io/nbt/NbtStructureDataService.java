package net.glowstone.io.nbt;

import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.generator.structures.GlowStructure;
import net.glowstone.io.StructureDataService;
import net.glowstone.io.structure.StructureStorage;
import net.glowstone.io.structure.StructureStore;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class NbtStructureDataService implements StructureDataService {

    private final GlowWorld world;
    private final File structureDir;
    private final GlowServer server;

    public NbtStructureDataService(GlowWorld world, File structureDir) {
        this.world = world;
        this.structureDir = structureDir;
        server = (GlowServer) Bukkit.getServer();

        if (!structureDir.isDirectory() && !structureDir.mkdirs()) {
            server.getLogger().warning("Failed to create directory: " + structureDir);
        }
    }

    @Override
    public Map<Integer, GlowStructure> readStructuresData() throws IOException {
        final Map<Integer, GlowStructure> structures = new HashMap<>();
        for (StructureStore<?> store : StructureStorage.getStructureStores()) {
            File structureFile = new File(structureDir, store.getId() + ".dat");
            if (structureFile.exists()) {
                try (NBTInputStream in = new NBTInputStream(new FileInputStream(structureFile))) {
                    CompoundTag data = new CompoundTag();
                    data = in.readCompound();
                    if (data.isCompound("data")) {
                        data = data.getCompound("data");
                        if (data.isCompound("Features")) {
                            CompoundTag features = data.getCompound("Features");
                            features.getValue().keySet().stream().filter(features::isCompound).forEach(key -> {
                                GlowStructure structure = StructureStorage.loadStructure(world, features.getCompound(key));
                                structures.put(new GlowChunk.Key(structure.getChunkX(), structure.getChunkZ()).hashCode(), structure);
                            });
                        }
                    } else {
                        server.getLogger().log(Level.SEVERE, "No data tag in " + structureFile);
                    }
                } catch (IOException e) {
                    server.getLogger().log(Level.SEVERE, "Failed to read structure data from " + structureFile, e);
                }
            }
        }
        return structures;
    }

    @Override
    public void writeStructuresData(Map<Integer, GlowStructure> structures) throws IOException {
        for (GlowStructure structure : structures.values()) {
            if (structure.isDirty()) {
                CompoundTag root = new CompoundTag();
                CompoundTag data = new CompoundTag();
                CompoundTag features = new CompoundTag();
                CompoundTag feature = new CompoundTag();
                StructureStore<GlowStructure> store = StructureStorage.saveStructure(structure, feature);
                File structureFile = new File(structureDir, store.getId() + ".dat");
                if (structureFile.exists()) {
                    try (NBTInputStream in = new NBTInputStream(new FileInputStream(structureFile))) {
                        data = new CompoundTag();
                        data = in.readCompound();
                        if (data.isCompound("data")) {
                            data = data.getCompound("data");
                            if (data.isCompound("Features")) {
                                features = data.getCompound("Features");
                            }
                        }
                    } catch (IOException e) {
                        server.getLogger().log(Level.SEVERE, "Failed to read structure data from " + structureFile, e);
                    }
                }
                final String key = "[" + structure.getChunkX() + "," + structure.getChunkZ() + "]";
                features.putCompound(key, feature);
                data.putCompound("Features", features);
                root.putCompound("data", data);
                try (NBTOutputStream nbtOut = new NBTOutputStream(new FileOutputStream(structureFile))) {
                    nbtOut.writeTag(root);
                } catch (IOException e) {
                    server.getLogger().log(Level.SEVERE, "Failed to write structure data to " + structureFile, e);
                }
                structure.setDirty(false);
            }
        }
    }
}
